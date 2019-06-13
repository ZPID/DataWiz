package de.zpid.datawiz.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import de.zpid.datawiz.dao.*;
import de.zpid.datawiz.dto.FileDTO;
import de.zpid.datawiz.dto.RecordCompareDTO;
import de.zpid.datawiz.dto.RecordDTO;
import de.zpid.datawiz.dto.UserDTO;
import de.zpid.datawiz.enumeration.*;
import de.zpid.datawiz.exceptions.DataWizSystemException;
import de.zpid.datawiz.form.StudyForm;
import de.zpid.datawiz.spss.*;
import de.zpid.datawiz.util.MinioUtil;
import de.zpid.datawiz.util.RegexUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


/**
 * Service Class for RecordController
 * <p>
 * This file is part of the DataWiz distribution (https://github.com/ZPID/DataWiz).
 * Copyright (c) 2018 <a href="https://leibniz-psychology.org/">Leibniz Institute for Psychology Information (ZPID)</a>.
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3.
 * <p>
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <a href="http://www.gnu.org/licenses/">http://www.gnu.org/licenses/</a>.
 *
 * @author Ronny Boelter
 * @version 1.0
 * FIXME: StudyForm is stored in Session. This should be revised, because this causes errors on using multiple browser tabs!!!
 */
@Service
public class RecordService {

    private static Logger log = LogManager.getLogger(RecordService.class);

    private ImportService importService;
    private ProjectDAO projectDAO;
    private StudyDAO studyDAO;
    private StudyListTypesDAO studyListTypesDAO;
    private StudyConstructDAO studyConstructDAO;
    private StudyInstrumentDAO studyInstrumentDAO;
    private RecordDAO recordDAO;
    private ClassPathXmlApplicationContext applicationContext;
    private PlatformTransactionManager txManager;
    private MinioUtil minioUtil;
    private FileDAO fileDAO;
    private MessageSource messageSource;

    @Autowired
    public RecordService(ImportService importService, ProjectDAO projectDAO, StudyDAO studyDAO, StudyListTypesDAO studyListTypesDAO, StudyConstructDAO studyConstructDAO,
                         StudyInstrumentDAO studyInstrumentDAO, RecordDAO recordDAO, ClassPathXmlApplicationContext applicationContext,
                         PlatformTransactionManager txManager, MinioUtil minioUtil, FileDAO fileDAO, MessageSource messageSource) {
        this.importService = importService;
        this.projectDAO = projectDAO;
        this.studyDAO = studyDAO;
        this.studyListTypesDAO = studyListTypesDAO;
        this.studyConstructDAO = studyConstructDAO;
        this.studyInstrumentDAO = studyInstrumentDAO;
        this.recordDAO = recordDAO;
        this.applicationContext = applicationContext;
        this.txManager = txManager;
        this.minioUtil = minioUtil;
        this.fileDAO = fileDAO;
        this.messageSource = messageSource;
    }

    final static private int VAR_LABEL_MAX_LENGTH = 250;
    final static private int VAR_NAME_MAX_LENGTH = 50;

    /**
     * Loads Project, Study and Record data from the DBS and Minio
     *
     * @param pid           Project identifier as long
     * @param studyId       Study identifier as long
     * @param recordId      Record identifier as long
     * @param versionId     Version identifier as long
     * @param subPage       Subpage identifier as {@link String}
     * @param parsingErrors {@link Set} of parsing errors.
     * @return {@link StudyForm} with Record and Study Data
     * @throws DataWizSystemException One of the following Error Codes: <br>
     *                                MISSING_PID_ERROR <br>
     *                                MISSING_STUDYID_ERROR <br>
     *                                PROJECT_NOT_AVAILABLE <br>
     *                                STUDY_NOT_AVAILABLE <br>
     *                                RECORD_NOT_AVAILABLE
     */
    public StudyForm setStudyform(final long pid, final long studyId, final long recordId, final long versionId,
                                  final String subPage, final List<String> parsingErrors) throws DataWizSystemException {
        if (pid <= 0)
            throw new DataWizSystemException(messageSource.getMessage("logging.pid.not.present", null, Locale.ENGLISH), DataWizErrorCodes.MISSING_PID_ERROR);
        if (studyId <= 0)
            throw new DataWizSystemException(messageSource.getMessage("logging.studyid.not.present", null, Locale.ENGLISH), DataWizErrorCodes.MISSING_STUDYID_ERROR);
        StudyForm sForm = (StudyForm) applicationContext.getBean("StudyForm");
        sForm.setProject(projectDAO.findById(pid));
        if (sForm.getProject() == null) {
            throw new DataWizSystemException(messageSource.getMessage("logging.project.not.found", new Object[]{pid}, Locale.ENGLISH),
                    DataWizErrorCodes.PROJECT_NOT_AVAILABLE);
        }
        sForm.setStudy(studyDAO.findById(studyId, pid, true, false));
        if (sForm.getStudy() == null)
            throw new DataWizSystemException(messageSource.getMessage("logging.study.not.found", new Object[]{studyId}, Locale.ENGLISH),
                    DataWizErrorCodes.STUDY_NOT_AVAILABLE);
        if (recordId > 0) {
            RecordDTO rec = recordDAO.findRecordWithID(recordId, versionId);
            if (rec != null) {
                if (subPage == null || subPage.equals("")) {
                    sForm.setRecords(recordDAO.findRecordVersionList(recordId));
                    rec.setVariables(recordDAO.findVariablesByVersionID(rec.getVersionId()));
                    transformCodebook(parsingErrors, rec, false);
                } else if (subPage.equals("codebook")) {
                    sForm.getStudy().setConstructs(studyConstructDAO.findAllByStudy(studyId));
                    sForm.getStudy().setMeasOcc(studyListTypesDAO.findAllByStudyAndType(studyId, DWFieldTypes.MEASOCCNAME));
                    sForm.getStudy().setInstruments(studyInstrumentDAO.findAllByStudy(studyId, true));
                    rec.setVariables(recordDAO.findVariablesByVersionID(rec.getVersionId()));
                    transformCodebook(parsingErrors, rec, false);
                } else {
                    rec.setVariables(recordDAO.findVariablesByVersionID(rec.getVersionId()));
                    transformDataMatrix(parsingErrors, rec, false, pid);
                }
            } else {
                throw new DataWizSystemException(messageSource.getMessage("logging.record.not.found", new Object[]{recordId}, Locale.ENGLISH),
                        DataWizErrorCodes.RECORD_NOT_AVAILABLE);
            }
            sForm.setRecord(rec);
        }
        return sForm;
    }

    /**
     * Transform the codebook for view and export
     *
     * @param parsingErrors {@link Set} of parsing errors.
     * @param rec           {@link RecordDTO} Contains the record information
     * @param isSPSS        Used for Export True, if Export Type is SPSS, otherwise false
     */
    void transformCodebook(final List<String> parsingErrors, final RecordDTO rec, final boolean isSPSS) {
        rec.setAttributes(recordDAO.findRecordAttributes(rec.getVersionId(), true));
        if (rec.getVariables() != null && rec.getVariables().size() > 0) {
            for (SPSSVarDTO var : rec.getVariables()) {
                var.setAttributes(recordDAO.findVariableAttributes(var.getId(), false));
                importService.sortVariableAttributes(var);
                var.setValues(recordDAO.findVariableValues(var.getId(), false));
                // SET DATE VALUES TO VIEW DATE (d.M.yyyy)
                if (!isSPSS) {
                    if (RecordDTO.simplifyVarTypes(var.getType()).equals(SPSSVarTypes.SPSS_FMT_DATE)) {
                        var.getValues().parallelStream().forEach(value -> {
                            String viewDate;
                            viewDate = parseDateToViewTime(value.getValue(), parsingErrors, var, "value-label", false);
                            if (viewDate != null)
                                value.setValue(viewDate);
                        });
                        String missing;
                        missing = parseDateToViewTime(var.getMissingVal1(), parsingErrors, var, "missingVal1", true);
                        if (missing != null)
                            var.setMissingVal1(missing);
                        missing = parseDateToViewTime(var.getMissingVal2(), parsingErrors, var, "missingVal2", true);
                        if (missing != null)
                            var.setMissingVal2(missing);
                        missing = parseDateToViewTime(var.getMissingVal3(), parsingErrors, var, "missingVal3", true);
                        if (missing != null)
                            var.setMissingVal3(missing);
                    }
                }
            }
        }
    }

    /**
     * Transforms the data matrix, which is stored as a JSON string in the Minio system, to a List<List<Object>>, which is used for the view and export.
     *
     * @param parsingErrors {@link Set} of parsing errors.
     * @param rec           {@link RecordDTO} Contains the record information
     * @param isSPSS        Used for Export True, if Export Type is SPSS, otherwise false
     * @param pid           Project identifier as long
     * @throws DataWizSystemException Minio and DBS Exceptions
     */
    void transformDataMatrix(final List<String> parsingErrors, final RecordDTO rec, final boolean isSPSS, final long pid) throws DataWizSystemException {
        String dataMatrix = recordDAO.findMatrixByVersionId(rec.getVersionId());
        if (dataMatrix == null || dataMatrix.isEmpty())
            dataMatrix = loadDataMatrix(rec, pid);
        rec.setDataMatrixJson(dataMatrix);
        if (rec.getDataMatrixJson() != null && !rec.getDataMatrixJson().isEmpty())
            rec.setDataMatrix(new Gson().fromJson(rec.getDataMatrixJson(), new TypeToken<List<List<Object>>>() {
            }.getType()));
        if (!isSPSS && rec.getVariables() != null && rec.getVariables().size() > 0 && rec.getDataMatrix() != null && !rec.getDataMatrix().isEmpty()) {
            int varPosition = 0;
            for (SPSSVarDTO var : rec.getVariables()) {
                int sd = varPosition++;
                rec.getDataMatrix().parallelStream().forEach(row -> {
                    if (RecordDTO.simplifyVarTypes(var.getType()).equals(SPSSVarTypes.SPSS_FMT_DATE) && row.get(sd) != null && !String.valueOf(row.get(sd)).isEmpty()) {
                        String viewDate;
                        viewDate = parseDateToViewTime(String.valueOf(row.get(sd)), parsingErrors, var, "matrix-row-" + sd, false);
                        if (viewDate != null && !viewDate.isEmpty()) {
                            row.set(sd, viewDate);
                        }
                    } else if (RecordDTO.simplifyVarTypes(var.getType()).equals(SPSSVarTypes.SPSS_FMT_F) && row.get(sd) != null) {
                        if (!String.valueOf(row.get(sd)).isEmpty()) {
                            BigDecimal dec = new BigDecimal(String.valueOf(row.get(sd)));
                            //dec = dec.stripTrailingZeros();
                            if (var.getDecimals() <= 0) {
                                row.set(sd, Math.round(dec.doubleValue()));
                            } else {
                                row.set(sd, dec);
                            }
                        } else {
                            row.set(sd, null);
                        }
                    }
                });
            }
        }
    }


    /**
     * Parses a String to the DBS date Format. It is quite a bit complicated, because SPSS uses a lot of time-formats!
     *
     * @param valueString   {@link String} Value that has to be parsed
     * @param parsingErrors {@link Set} of parsing errors.
     * @param var           {@link SPSSVarDTO} Contains the variable information
     * @param missing       True if missing, false if value-label
     * @param type          {@link String} type identifier, only for log purposes
     * @return The parsed value as {@link String}
     */
    private String parseDateToViewTime(String valueString, final List<String> parsingErrors, final SPSSVarDTO var, final String type,
                                       boolean missing) {
        String viewDate = null;
        SPSSVarTypes varType = var.getType();
        if (valueString != null && !valueString.isEmpty() && valueString.length()>4) {
            try {
                LocalDate date;
                if (varType.equals(SPSSVarTypes.SPSS_FMT_DATE_TIME)) {
                    String[] dt = valueString.trim().split(" ");
                    date = LocalDate.parse(dt[0].trim(), DateTimeFormatter.ofPattern("M/d/yyyy"));
                    viewDate = ((date.getDayOfMonth() <= 9 ? "0" + date.getDayOfMonth() : date.getDayOfMonth()) + "."
                            + (date.getMonthValue() <= 9 ? "0" + date.getMonthValue() : date.getMonthValue()) + "." + date.getYear() + " " + dt[1].trim()).trim();
                } else if (varType.equals(SPSSVarTypes.SPSS_FMT_DATE) || varType.equals(SPSSVarTypes.SPSS_FMT_ADATE) || varType.equals(SPSSVarTypes.SPSS_FMT_JDATE)
                        || varType.equals(SPSSVarTypes.SPSS_FMT_EDATE) || varType.equals(SPSSVarTypes.SPSS_FMT_SDATE)) {
                    date = LocalDate.parse(valueString.trim(), DateTimeFormatter.ofPattern("M/d/yyyy"));
                    viewDate = ((date.getDayOfMonth() <= 9 ? "0" + date.getDayOfMonth() : date.getDayOfMonth()) + "."
                            + (date.getMonthValue() <= 9 ? "0" + date.getMonthValue() : date.getMonthValue()) + "." + date.getYear()).trim();
                }
            } catch (Exception e) {
                log.warn("DBS Warning in parseDateToViewTime: corrupt {} date found in database: value [{}] of variable [id:{}] doesn't match the correct dateformat",
                        () -> type, () -> valueString, var::getId);
                parsingErrors.add(messageSource.getMessage("record.dbs.date.corrupt",
                        new Object[]{valueString, messageSource.getMessage(missing ? "dataset.import.report.codebook.missings" : "dataset.import.report.codebook.values",
                                null, LocaleContextHolder.getLocale()), var.getName()},
                        LocaleContextHolder.getLocale()));
            }
        }
        return viewDate;
    }

    /**
     * Parses a String to the DBS date Format. It is quite a bit complicated, because SPSS uses a lot of time-formats!
     *
     * @param valueString   {@link String} Value that has to be parsed
     * @param parsingErrors {@link Set} of parsing errors.
     * @param var           {@link SPSSVarDTO} Contains the variable information
     * @param missing       True if missing, false if value-label
     * @return The parsed value as {@link String}
     */
    private String parseDateToDBTime(final String valueString, final Set<String> parsingErrors, final SPSSVarDTO var, boolean missing) {
        String viewDate = null;
        SPSSVarTypes varType = var.getType();
        try {
            LocalDate date;
            if (varType.equals(SPSSVarTypes.SPSS_FMT_DATE_TIME)) {
                String[] dt = valueString.trim().split(" ");
                date = LocalDate.parse(dt[0].trim(), DateTimeFormatter.ofPattern("d.M.yyyy"));
                viewDate = ((date.getMonthValue() <= 9 ? "0" + date.getMonthValue() : date.getMonthValue()) + "/"
                        + (date.getDayOfMonth() <= 9 ? "0" + date.getDayOfMonth() : date.getDayOfMonth()) + "/" + date.getYear() + " " + dt[1].trim()).trim();
            } else if (varType.equals(SPSSVarTypes.SPSS_FMT_DATE) || varType.equals(SPSSVarTypes.SPSS_FMT_ADATE) || varType.equals(SPSSVarTypes.SPSS_FMT_JDATE)
                    || varType.equals(SPSSVarTypes.SPSS_FMT_EDATE) || varType.equals(SPSSVarTypes.SPSS_FMT_SDATE)) {
                date = LocalDate.parse(valueString.trim(), DateTimeFormatter.ofPattern("d.M.yyyy"));
                viewDate = ((date.getMonthValue() <= 9 ? "0" + date.getMonthValue() : date.getMonthValue()) + "/"
                        + (date.getDayOfMonth() <= 9 ? "0" + date.getDayOfMonth() : date.getDayOfMonth()) + "/" + date.getYear()).trim();
            }
        } catch (Exception e) {
            log.debug("corrupt {} date found in form: value [{}] of variable [id:{}] doesn't match the correct dateformat", () -> missing ? "missing" : "value-label", () -> valueString,
                    var::getId);
            parsingErrors.add(messageSource.getMessage("record.dbs.date.corrupt",
                    new Object[]{valueString, messageSource.getMessage(missing ? "dataset.import.report.codebook.missings" : "dataset.import.report.codebook.values",
                            null, LocaleContextHolder.getLocale()), var.getName()},
                    LocaleContextHolder.getLocale()));
        }
        return viewDate;
    }


    /**
     * Saves the record metadata into the DBS
     *
     * @param studyId  Study identifier as long
     * @param recordId Record identifier as long
     * @param sForm    {@link StudyForm} Contains the values which have to be stored
     * @param user     {@link UserDTO} Mail address used for the createdBy field
     */
    public void insertOrUpdateRecordMetadata(final long studyId, final long recordId, final StudyForm sForm, final UserDTO user) {
        if (recordId <= 0 || sForm.getRecord().getId() <= 0) {
            sForm.getRecord().setCreatedBy(user.getEmail());
            sForm.getRecord().setStudyId(studyId);
            recordDAO.insertRecordMetaData(sForm.getRecord());
        } else {
            recordDAO.updateRecordMetaData(sForm.getRecord());
        }
    }


    /**
     * Returns a value-label of a passed variable
     *
     * @param varId Variable identifier as long
     * @return {@link SPSSValueLabelDTO} value label of the passed variable
     */
    public List<SPSSValueLabelDTO> getVariableValues(final long varId) {
        return recordDAO.findVariableValues(varId, true);
    }

    /**
     * Sets the Value-Label of a Variable, or sets it global if the ID of the passed Variable (varVal) == -1
     *
     * @param varVal {@link SPSSVarDTO} contains the new Value-Label
     * @param sForm  {@link StudyForm} Contains the Variables where the labels have to be stored
     */
    public void setVariableValues(final SPSSVarDTO varVal, final StudyForm sForm) {
        varVal.getValues().removeIf((SPSSValueLabelDTO val) -> ((val.getLabel() == null || val.getLabel().trim().isEmpty()) && (val.getValue() == null || val.getValue().trim().isEmpty())));
        if (varVal.getId() > 0) {
            for (SPSSVarDTO var : sForm.getRecord().getVariables()) {
                if (var.getId() == varVal.getId()) {
                    var.setValues(varVal.getValues());
                    break;
                }
            }
        } else if (varVal.getId() == -1) {
            sForm.getRecord().getVariables().parallelStream().forEach(var -> {
                List<SPSSValueLabelDTO> global = new ArrayList<>();
                boolean setGlobal = false;
                for (SPSSValueLabelDTO val : varVal.getValues()) {
                    SPSSValueLabelDTO newVal = new SPSSValueLabelDTO();
                    newVal.setLabel(val.getLabel());
                    newVal.setValue(val.getValue());
                    SPSSVarTypes type = SPSSVarTypes.fromInt((int) val.getId());
                    if (type.equals(SPSSVarTypes.SPSS_FMT_A) && RecordDTO.simplifyVarTypes(var.getType()).equals(SPSSVarTypes.SPSS_FMT_A)) {
                        setGlobal = true;
                        global.add(newVal);
                    } else if (type.equals(SPSSVarTypes.SPSS_FMT_F) && RecordDTO.simplifyVarTypes(var.getType()).equals(SPSSVarTypes.SPSS_FMT_F)) {
                        setGlobal = true;
                        global.add(newVal);
                    } else if (type.equals(SPSSVarTypes.SPSS_FMT_DATE) && var.getType().equals(SPSSVarTypes.SPSS_FMT_DATE)) {
                        setGlobal = true;
                        global.add(newVal);
                    }
                }
                if (setGlobal)
                    var.setValues(global);
            });
        }
    }


    /**
     * Compares the records which is saved by a user with the last version of this record which is saved in the DBS.
     * This comparision is used to not save redundant variables into the DBS.
     * Therefore, a relation table is used and if the variables are equal, only the relation is changed.
     *
     * @param currentVersion {@link RecordDTO} contains the new record
     * @param changelog      Required changelog as {@link String}
     * @return {@link String} Messages on successful or failed saving
     * @throws DataWizSystemException DBS Exceptions DATABASE_ERROR
     */
    public String compareAndSaveRecord(final RecordDTO currentVersion, final String changelog, final long pid)
            throws DataWizSystemException {
        RecordDTO lastVersion;
        String msg = null;
        TransactionStatus status = txManager.getTransaction(new DefaultTransactionDefinition());
        try {
            String dataMatrix = recordDAO.findMatrixByVersionId(currentVersion.getVersionId());
            if (dataMatrix != null && !dataMatrix.isEmpty() && (currentVersion.getMinioName() == null || currentVersion.getMinioName().isEmpty())) {
                FileDTO matrix = new FileDTO();
                matrix.setFileName("tempName");
                matrix.setContent(dataMatrix.getBytes());
                matrix.setProjectId(pid);
                matrix.setStudyId(currentVersion.getStudyId());
                matrix.setRecordID(currentVersion.getId());
                matrix.setContentType("application/json; charset=UTF-8");
                MinioResult res = minioUtil.putFile(matrix, true);
                if (!res.equals(MinioResult.OK)) {
                    log.error("FATAL: No Connection to Minio Server - please check Settings or Server");
                    throw new DataWizSystemException("Minio returns an error MinioResult: " + res.name(), DataWizErrorCodes.MINIO_SAVE_ERROR);
                }
                currentVersion.setOriginalName("tempName");
                currentVersion.setMinioName(matrix.getFilePath());
            }
            lastVersion = recordDAO.findRecordWithID(currentVersion.getId(), 0);
            lastVersion.setVariables(recordDAO.findVariablesByVersionID(lastVersion.getVersionId()));
            lastVersion.setAttributes(recordDAO.findRecordAttributes(lastVersion.getVersionId(), false));
            if (lastVersion.getVariables() != null && lastVersion.getVariables().size() > 0) {
                for (SPSSVarDTO var : lastVersion.getVariables()) {
                    var.setAttributes(recordDAO.findVariableAttributes(var.getId(), false));
                    importService.sortVariableAttributes(var);
                    var.setValues(recordDAO.findVariableValues(var.getId(), false));
                }
            }
            if (currentVersion.getVariables() != null && lastVersion.getVariables() != null
                    && !currentVersion.getVariables().equals(lastVersion.getVariables())) {
                currentVersion.setChangeLog(changelog);
                recordDAO.insertCodeBookMetaData(currentVersion);
                recordDAO.insertAttributes(currentVersion.getAttributes(), currentVersion.getVersionId(), 0);
                int i = 0;
                for (SPSSVarDTO var : currentVersion.getVariables()) {
                    long varId = var.getId();
                    SPSSVarDTO dbvar = null;
                    if (lastVersion.getVariables() != null && lastVersion.getVariables().size() > i) {
                        dbvar = lastVersion.getVariables().get(i++);
                        dbvar.setId(0);
                        var.setId(0);
                    }
                    if (var.equals(dbvar)) {
                        recordDAO.insertVariableVersionRelation(varId, currentVersion.getVersionId(), var.getPosition(),
                                messageSource.getMessage("import.check.EQUAL", null, LocaleContextHolder.getLocale()));
                    } else {
                        varId = recordDAO.insertVariable(var);
                        recordDAO.insertVariableVersionRelation(varId, currentVersion.getVersionId(), var.getPosition(),
                                messageSource.getMessage("import.check.NEW_VAR", null, LocaleContextHolder.getLocale()));
                        if (var.getAttributes() != null) {
                            removeEmptyDWAttributes(var.getDw_attributes());
                            var.getAttributes().addAll(var.getDw_attributes());
                            recordDAO.insertAttributes(var.getAttributes(), currentVersion.getVersionId(), varId);
                        }
                        if (var.getValues() != null)
                            recordDAO.insertVarLabels(var.getValues(), varId);
                    }
                }
                txManager.commit(status);
                msg = "record.codebook.saved";
            } else if (currentVersion.getVariables() == null || lastVersion.getVariables() == null) {
                msg = "record.codebook.data.corrupt";
            } else if (currentVersion.getVariables().equals(lastVersion.getVariables())) {
                msg = "record.codebook.versions.equal";
            }
        } catch (Exception e) {
            txManager.rollback(status);
            throw new DataWizSystemException(messageSource.getMessage("logging.database.error", new Object[]{e.getMessage()}, Locale.ENGLISH),
                    DataWizErrorCodes.DATABASE_ERROR, e);
        }
        return msg;
    }


    /**
     * Deletes a record from the DBS and Minio. This function uses the TransactionManager to rollback the transaction if errors occur,
     * the behavior can be switched off with singleCommit=false. This is useful because it is used to delete a single record or all records of a study.
     *
     * @param pid          Project identifier as long
     * @param studyId      Study identifier as long
     * @param recordId     Record identifier as long
     * @param user         {@link UserDTO} contains the user data, used for permission check
     * @param singleCommit true if only one record has to be deleted, used for rollback/commit behaviour
     * @throws DataWizSystemException Permission, Minio or DBS Exceptions
     */
    public void deleteRecord(final long pid, final long studyId, final long recordId, final UserDTO user, final boolean singleCommit)
            throws DataWizSystemException {
        log.trace("Entering  deleteRecord for [recordId: {}; studyId {}; projectId {}] user[id: {}; email: {}]", () -> recordId, () -> studyId,
                () -> pid, user::getId, user::getEmail);
        TransactionStatus status = null;
        if (singleCommit)
            status = txManager.getTransaction(new DefaultTransactionDefinition());
        try {
            RecordDTO record = recordDAO.findRecordWithID(recordId, 0);
            if (record != null && (user.hasRole(Roles.ADMIN) || user.hasRole(Roles.PROJECT_ADMIN, pid, false)
                    || ((user.hasRole(Roles.PROJECT_WRITER, pid, false) || user.hasRole(Roles.DS_WRITER, studyId, true))
                    && record.getCreatedBy().trim().equals(user.getEmail().trim())))) {
                List<RecordDTO> versions = recordDAO.findRecordVersionList(recordId);
                if (versions != null) {
                    for (RecordDTO rec : versions) {
                        rec.setVariables(recordDAO.findVariablesByVersionID(rec.getVersionId()));
                    }
                }
                recordDAO.deleteRecord(recordId);
                if (versions != null) {
                    for (RecordDTO rec : versions) {
                        if (rec.getVariables() != null) {
                            for (SPSSVarDTO var : rec.getVariables()) {
                                recordDAO.deleteVariable(var.getId());
                            }
                        }
                    }
                }
                MinioResult res = minioUtil.cleanAndRemoveBucket(pid, studyId, recordId, false);
                if (res.equals(MinioResult.OK))
                    res = minioUtil.cleanAndRemoveBucket(pid, studyId, recordId, true);
                if (res.equals(MinioResult.OK)) {
                    if (singleCommit)
                        txManager.commit(status);
                    log.trace("Method deleteRecord completed");
                } else {
                    log.warn("Record [id: {}] not delteted: Transaction was rolled back!");
                    throw new DataWizSystemException(messageSource.getMessage("logging.minio.delete.error", new Object[]{res}, Locale.ENGLISH),
                            DataWizErrorCodes.MINIO_DELETE_ERROR);
                }
            } else {
                log.warn("User [email:{}; id: {}] tried to delete Record [projectId: {}; studyId: {}; recordId: {}]", user::getEmail, user::getId,
                        () -> pid, () -> studyId, () -> recordId);
                throw new DataWizSystemException(
                        messageSource.getMessage("logging.user.permitted", new Object[]{user.getEmail(), "record", recordId}, Locale.ENGLISH),
                        DataWizErrorCodes.USER_ACCESS_RECORD_PERMITTED);
            }
        } catch (Exception e) {
            if (singleCommit)
                txManager.rollback(status);
            if (e instanceof DataWizSystemException) {
                log.warn("DeleteRecord DataWizSystemException {}:", ((DataWizSystemException) e)::getErrorCode, () -> e);
                throw new DataWizSystemException(
                        messageSource.getMessage("logging.record.delete.error", new Object[]{user.getEmail(), "record", recordId}, Locale.ENGLISH),
                        DataWizErrorCodes.RECORD_DELETE_ERROR);
            } else {
                log.warn("DeleteRecord Database-Exception:", () -> e);
                throw new DataWizSystemException(messageSource.getMessage("logging.database.error", new Object[]{e.getMessage()}, Locale.ENGLISH),
                        DataWizErrorCodes.DATABASE_ERROR, e);
            }
        }
    }


    /**
     * Validates the record codebook by calling validateAndPrepareCodebookForm with onlyValidation=true.
     *
     * @param sForm {@link StudyForm} contains the study and record data
     * @return String with parsing errors
     */
    public String validateCodeBook(final StudyForm sForm) {
        Set<String> parsingErrors = new HashSet<>();
        Set<String> parsingWarnings = new HashSet<>();
        try {
            validateAndPrepareCodebookForm(sForm.getRecord(), parsingErrors, parsingWarnings, null, true, false);
            sForm.setWarnings(parsingWarnings);
        } catch (DataWizSystemException e) {
            log.debug("Parsing Exception during saveCodebook - Code{}; Message: {}", e::getErrorCode, e::getMessage);
        }
        return setMessageString(parsingErrors.parallelStream().collect(Collectors.toList()));
    }


    /**
     * Validates a record and prepares it for storage in the database. If onlyValidation==true, no data will be stored in the DBS.
     * If errors/warnings occur during runtime of this function, they will be written into the {@link Set} parsingErrors/parsingWarnings.
     *
     * @param record          {@link RecordDTO} contains the record data
     * @param parsingErrors   {@link Set} of parsing errors.
     * @param parsingWarnings {@link Set} of parsing warnings.
     * @param changelog       {@link String} Content of the changelog field, which is required
     * @param onlyValidation  if true, the Values are only validated and not set
     * @param ignoreValErrors true, if SPSS Scheme validation is ignored
     * @throws DataWizSystemException Exceptions if errors occur during the runtime of this function, or functions on which this function depends.
     */
    public void validateAndPrepareCodebookForm(final RecordDTO record, final Set<String> parsingErrors, final Set<String> parsingWarnings,
                                               final String changelog, final boolean onlyValidation, final boolean ignoreValErrors) throws DataWizSystemException {
        boolean missingChangelog = false;
        if (!onlyValidation) {
            if (changelog == null || changelog.trim().isEmpty()) {
                parsingErrors.add(messageSource.getMessage("record.codebook.changelog.missing", null, LocaleContextHolder.getLocale()));
                missingChangelog = true;
            }
        }
        if (!missingChangelog) {
            Set<String> names = new HashSet<>();
            if (record != null && record.getVariables() != null)
                record.getVariables().parallelStream().forEach((var) -> {
                    if (var.getName() == null || var.getName().isEmpty()) {
                        parsingErrors.add(messageSource.getMessage("record.name.empty", new Object[]{var.getPosition()}, LocaleContextHolder.getLocale()));
                    } else if (var.getName().getBytes(Charset.forName("UTF-8")).length > VAR_NAME_MAX_LENGTH) {
                        parsingErrors.add(messageSource.getMessage("record.name.too.long", new Object[]{var.getPosition(), var.getName(), VAR_NAME_MAX_LENGTH},
                                LocaleContextHolder.getLocale()));
                    } else if (!names.add(var.getName().toUpperCase())) {
                        parsingErrors
                                .add(messageSource.getMessage("record.name.equal", new Object[]{var.getPosition(), var.getName()}, LocaleContextHolder.getLocale()));
                    } else if (!ignoreValErrors && !Pattern.compile(RegexUtil.VAR_NAME_REGEX).matcher(var.getName()).find()) {
                        parsingErrors
                                .add(messageSource.getMessage("record.name.invalid", new Object[]{var.getPosition(), var.getName()}, LocaleContextHolder.getLocale()));
                    }
                    if (var.getLabel() != null && !var.getLabel().isEmpty() && var.getLabel().getBytes(Charset.forName("UTF-8")).length > VAR_LABEL_MAX_LENGTH) {
                        parsingErrors.add(messageSource.getMessage("record.label.too.long", new Object[]{var.getPosition(), var.getName(), VAR_LABEL_MAX_LENGTH},
                                LocaleContextHolder.getLocale()));
                    }
                    if (var.getDecimals() > 16) {
                        parsingWarnings.add(
                                messageSource.getMessage("record.decimals.too.long", new Object[]{var.getPosition(), var.getName(), 16}, LocaleContextHolder.getLocale()));
                    }
                    for (SPSSValueLabelDTO val : var.getValues()) {
                        validateValueorMissingField(parsingErrors, onlyValidation, var, val, false, 0, parsingWarnings);
                    }
                    SPSSMissing missFormat = var.getMissingFormat();
                    if (!missFormat.equals(SPSSMissing.SPSS_UNKNOWN) && !missFormat.equals(SPSSMissing.SPSS_NO_MISSVAL)) {
                        validateValueorMissingField(parsingErrors, onlyValidation, var, null, true, 1, parsingWarnings);
                        if (missFormat.equals(SPSSMissing.SPSS_TWO_MISSVAL) || missFormat.equals(SPSSMissing.SPSS_MISS_RANGE)
                                || missFormat.equals(SPSSMissing.SPSS_THREE_MISSVAL) || missFormat.equals(SPSSMissing.SPSS_MISS_RANGEANDVAL)) {
                            validateValueorMissingField(parsingErrors, onlyValidation, var, null, true, 2, parsingWarnings);
                            if (missFormat.equals(SPSSMissing.SPSS_THREE_MISSVAL) || missFormat.equals(SPSSMissing.SPSS_MISS_RANGEANDVAL)) {
                                validateValueorMissingField(parsingErrors, onlyValidation, var, null, true, 3, parsingWarnings);
                            } else {
                                if (!onlyValidation) {
                                    var.setMissingVal3(null);
                                }
                            }
                        } else {
                            if (!onlyValidation) {
                                var.setMissingVal2(null);
                                var.setMissingVal3(null);
                            }
                        }
                    } else {
                        if (!onlyValidation) {
                            var.setMissingVal1(null);
                            var.setMissingVal2(null);
                            var.setMissingVal3(null);
                        }
                    }
                });
        }
        if (parsingErrors.size() > 0) {
            throw new DataWizSystemException(
                    "Error(s) during Record Validation in function (RecordService.validateAndPrepareCodebookForm) - List of errors returned to View",
                    DataWizErrorCodes.RECORD_VALIDATION_ERROR);
        }
    }


    /**
     * Validates a Value or Missing field. If errors/warnings occur during runtime of this function, they will be written into the {@link Set} parsingErrors/parsingWarnings.
     *
     * @param parsingErrors   {@link Set} of parsing errors.
     * @param onlyValidation  if true, the Values are only validated and not set
     * @param var             {@link SPSSVarDTO} Variable containing the missing values which has to be validated if switchToMissing==true
     * @param val             {@link SPSSValueLabelDTO} value-label which has to be validated if switchToMissing==false
     * @param switchToMissing true if missing has to be validated, false if value label
     * @param missingNum      position of the missing, 1-3 is possible
     * @param parsingWarnings {@link Set} of parsing warnings.
     */
    private void validateValueorMissingField(final Set<String> parsingErrors, final boolean onlyValidation, final SPSSVarDTO var, final SPSSValueLabelDTO val,
                                             final boolean switchToMissing, final int missingNum, final Set<String> parsingWarnings) {
        boolean date = (var.getType().equals(SPSSVarTypes.SPSS_FMT_DATE_TIME) || var.getType().equals(SPSSVarTypes.SPSS_FMT_DATE)
                || var.getType().equals(SPSSVarTypes.SPSS_FMT_ADATE) || var.getType().equals(SPSSVarTypes.SPSS_FMT_JDATE)
                || var.getType().equals(SPSSVarTypes.SPSS_FMT_EDATE) || var.getType().equals(SPSSVarTypes.SPSS_FMT_SDATE));
        String parsed;
        String value = null;
        String label = null;
        boolean varError = false;
        if (!switchToMissing && val != null) {
            value = val.getValue();
            label = val.getLabel();
        } else {
            switch (missingNum) {
                case 1:
                    value = var.getMissingVal1();
                    break;
                case 2:
                    value = var.getMissingVal2();
                    break;
                case 3:
                    value = var.getMissingVal3();
                    break;
            }
        }
        if (!switchToMissing && ((label != null && label.isEmpty()) || (value != null && value.isEmpty()))) {
            if (label != null && label.isEmpty()) {
                parsingWarnings
                        .add(messageSource.getMessage("record.value.label.empty", new Object[]{var.getName(), var.getPosition()}, LocaleContextHolder.getLocale()));
            } else
                parsingErrors
                        .add(messageSource.getMessage("record.value.label.empty", new Object[]{var.getName(), var.getPosition()}, LocaleContextHolder.getLocale()));
            varError = true;
        } else if (switchToMissing && (value == null || value.isEmpty())) {
            parsingErrors.add(messageSource.getMessage("record.value.missing.empty", new Object[]{var.getName(), missingNum, var.getMissingFormat()},
                    LocaleContextHolder.getLocale()));
            varError = true;
        }
        if (!varError && date && (parsed = parseDateToDBTime(value, parsingErrors, var, switchToMissing)) != null) {
            if (!onlyValidation && !switchToMissing & val != null)
                val.setValue(parsed);
            else if (!onlyValidation) {
                switch (missingNum) {
                    case 1:
                        var.setMissingVal1(parsed);
                        break;
                    case 2:
                        var.setMissingVal2(parsed);
                        break;
                    case 3:
                        var.setMissingVal3(parsed);
                        break;
                }
            }
        } else if (!varError && RecordDTO.simplifyVarTypes(var.getType()).equals(SPSSVarTypes.SPSS_FMT_F)) {
            String parsingErrorString = checkNumberFormat(var, value);
            if (parsingErrorString != null)
                addParsingError(parsingErrors, var, value, parsingErrorString, switchToMissing);
        } else if (!varError && !date && value != null) {
            switch (var.getType()) {
                case SPSS_FMT_MONTH:
                    try {
                        SPSSMonths.valueOf(value);
                    } catch (Exception e) {
                        addParsingError(parsingErrors, var, value, "record.parse.month.invalid", switchToMissing);
                    }
                    break;
                case SPSS_FMT_QYR:
                    if (!Pattern.compile(RegexUtil.QUATER_REGEX).matcher(value).find()) {
                        addParsingError(parsingErrors, var, value, "record.parse.quater.invalid", switchToMissing);
                    }
                    break;
                case SPSS_FMT_MOYR:
                    if (!Pattern.compile(RegexUtil.MOYR_REGEX).matcher(value).find()) {
                        addParsingError(parsingErrors, var, value, "record.parse.moyr.invalid", switchToMissing);
                    }
                    break;
                case SPSS_FMT_WKYR:
                    if (!Pattern.compile(RegexUtil.WKYR_REGEX).matcher(value).find()) {
                        addParsingError(parsingErrors, var, value, "record.parse.wkyr.invalid", switchToMissing);
                    }
                    break;
                case SPSS_FMT_DTIME:
                    if (!Pattern.compile(RegexUtil.DTIME_REGEX).matcher(value).find()) {
                        addParsingError(parsingErrors, var, value, "record.parse.dtime.invalid", switchToMissing);
                    }
                    break;
                case SPSS_FMT_WKDAY:
                    if (!Pattern.compile(RegexUtil.WKDAY_REGEX).matcher(value).find()) {
                        addParsingError(parsingErrors, var, value, "record.parse.dtime.invalid", switchToMissing);
                    }
                    break;
                default:
                    if (value.length() > var.getWidth()) {
                        addParsingError(parsingErrors, var, value, "record.val.invalid.length", switchToMissing);
                    }
                    break;
            }
        }
    }


    /**
     * Adds a parsing error string to a {@link Set} of {@link String}
     *
     * @param parsingErrors      {@link Set} of parsing errors
     * @param var                Variable as {@link SPSSVarDTO}
     * @param value              {@link String} Value that has occurred the error
     * @param parsingErrorString {@link String} Resource identifier
     * @param switchtoMissing    if true the value is a missing, if false it is a value-label
     */
    private void addParsingError(final Set<String> parsingErrors, final SPSSVarDTO var, final String value, final String parsingErrorString, final boolean switchtoMissing) {
        parsingErrors
                .add(
                        messageSource.getMessage(parsingErrorString,
                                new Object[]{value,
                                        messageSource.getMessage(switchtoMissing ? "dataset.import.report.codebook.missings" : "dataset.import.report.codebook.values", null,
                                                LocaleContextHolder.getLocale()),
                                        var.getName(), var.getDecimals(), var.getWidth(), var.getPosition()},
                                LocaleContextHolder.getLocale()));
    }


    /**
     * Checks if a String contains a number
     *
     * @param var          Variable as {@link SPSSVarDTO} for decimals an width indicator
     * @param numberString {@link String} which has to be checked
     * @return Error Messages es String. Null if the String contains a Number
     */
    private String checkNumberFormat(final SPSSVarDTO var, final String numberString) {
        String ret = null;
        try {
            String[] arr = numberString.split("\\.");
            if (var.getDecimals() == 0) {
                if (arr.length > 1)
                    ret = "record.value.invalid.dec";
                else
                    Integer.parseInt(numberString);
            } else {
                Double.parseDouble(numberString);
                if (arr.length > 1 && arr[1].length() > var.getDecimals()) {
                    ret = "record.value.invalid.dec";
                }
            }
            if (numberString.length() > var.getWidth()) {
                ret = "record.val.invalid.length";
            }
        } catch (Exception e) {
            log.debug("Error Parsing {} to Number. Exception: {}", () -> numberString, () -> e);
            ret = "record.parse.number.invalid";
        }
        return ret;
    }

    /**
     * Loads the data for the record export and returns it as {@link RecordDTO}. The export type has to be passed in the exportType Parameter.
     *
     * @param versionId  version identifier as long
     * @param recordId   record identifier as long
     * @param exportType Export type identifier as {@link String}
     * @param res        {@link StringBuilder} for error handling.
     * @param pid        Project identifier as long
     * @return The Record which has to be exported
     * @throws Exception DBS Exceptions
     */
    public RecordDTO loadRecordExportData(final long versionId, final long recordId, final String exportType, final StringBuilder res, final long pid) throws Exception {
        RecordDTO record;
        List<String> parsingErrors = new ArrayList<>();
        record = recordDAO.findRecordWithID(recordId, versionId);
        record.setVariables(recordDAO.findVariablesByVersionID(versionId));
        record.setErrors(null);
        record.setAttributes(recordDAO.findRecordAttributes(versionId, true));
        transformCodebook(parsingErrors, record, exportType.equals("SPSS"));
        //TODO: Can be removed
        String dataMatrix = recordDAO.findMatrixByVersionId(record.getVersionId());
        if (dataMatrix == null || dataMatrix.isEmpty())
            dataMatrix = loadDataMatrix(record, pid);
        record.setDataMatrixJson(dataMatrix);
        if (record.getDataMatrixJson() != null && !record.getDataMatrixJson().isEmpty()) {
            record.setDataMatrix(new Gson().fromJson(record.getDataMatrixJson(), new TypeToken<List<List<Object>>>() {
            }.getType()));
            record.setDataMatrixJson(null);
        }
        transformDataMatrix(parsingErrors, record, exportType.equals("SPSS"), pid);
        if (!parsingErrors.isEmpty()) {
            parsingErrors.forEach(s -> res.append(s).append("<br />"));
        }
        return record;
    }

    /**
     * Saves the record to the DB and the uploaded file to the Minio System. Transaction Manager is used to eventually rollback the transmission if an error
     * occurs.
     *
     * @param sForm {@link StudyForm} with the record
     * @throws Exception DataBase or Minio Exceptions
     */
    public void saveRecordToDBAndMinio(final StudyForm sForm) throws Exception {
        RecordDTO record = sForm.getRecord();
        FileDTO file = sForm.getFile();
        FileDTO matrix = new FileDTO();
        MinioResult res;
        if (record != null && file != null) {
            TransactionStatus status = txManager.getTransaction(new DefaultTransactionDefinition());
            try {
                matrix.setFileName(file.getFileName());
                matrix.setContent(record.getDataMatrixJson().getBytes());
                matrix.setProjectId(file.getProjectId());
                matrix.setStudyId(file.getStudyId());
                matrix.setRecordID(file.getRecordID());
                matrix.setContentType("application/json; charset=UTF-8");
                res = minioUtil.putFile(matrix, true);
                if (!res.equals(MinioResult.OK)) {
                    log.error("FATAL: No Connection to Minio Server - please check Settings or Server");
                    throw new DataWizSystemException("Minio returns an error MinioResult: " + res.name(), DataWizErrorCodes.MINIO_SAVE_ERROR);
                }
                record.setOriginalName(file.getFileName());
                record.setMinioName(matrix.getFilePath());
                recordDAO.insertCodeBookMetaData(record);
                removeUselessFileAttributes(record.getAttributes());
                recordDAO.insertAttributes(record.getAttributes(), record.getVersionId(), 0);
                if (record.getVersionId() > 0) {
                    // recordDAO.insertMatrix(record);
                    int position = 0;
                    for (SPSSVarDTO var : record.getVariables()) {
                        if (var.getColumns() == 0)
                            var.setColumns(var.getColumns() < 8 ? 8 : var.getColumns());
                        if (!sForm.getCompList().get(position).getVarStatus().equals(VariableStatus.EQUAL)
                                && !sForm.getCompList().get(position).getVarStatus().equals(VariableStatus.EQUAL_CSV)
                                && !sForm.getCompList().get(position).getVarStatus().equals(VariableStatus.MOVED)
                                && !sForm.getCompList().get(position).getVarStatus().equals(VariableStatus.MOVED_CSV)) {
                            long varId = recordDAO.insertVariable(var);
                            recordDAO.insertVariableVersionRelation(varId, record.getVersionId(), var.getPosition(), sForm.getCompList().get(position).getMessage());
                            if (var.getAttributes() != null) {
                                recordDAO.insertAttributes(var.getAttributes(), record.getVersionId(), varId);
                            }
                            if (var.getValues() != null)
                                recordDAO.insertVarLabels(var.getValues(), varId);
                        } else {
                            recordDAO.insertVariableVersionRelation(var.getId(), record.getVersionId(), var.getPosition(), sForm.getCompList().get(position).getMessage());
                        }
                        position++;
                    }
                    file.setVersion(record.getVersionId());
                    res = minioUtil.putFile(file, false);
                    if (res.equals(MinioResult.OK)) {
                        fileDAO.saveFile(file);
                    } else if (res.equals(MinioResult.CONNECTION_ERROR)) {
                        log.error("FATAL: No Connection to Minio Server - please check Settings or Server");
                        throw new DataWizSystemException("Minio returns an error MinioResult: " + res.name(), DataWizErrorCodes.MINIO_SAVE_ERROR);
                    }
                }
                txManager.commit(status);
            } catch (Exception e) {
                txManager.rollback(status);
                if (file.getFilePath() != null && !file.getFilePath().isEmpty())
                    minioUtil.deleteFile(file);
                if (matrix.getFilePath() != null && !matrix.getFilePath().isEmpty())
                    minioUtil.deleteFile(matrix);
                throw (e);
            }
        } else {
            throw new DataWizSystemException("saveRecordToDBAndMinio canceled: sForm.getRecord(), or sForm.getFile() is empty  ", DataWizErrorCodes.NO_DATA_ERROR);
        }
    }

    /**
     * This function removes all useless file attributes (not variable attributes). After that, the list only contains the user specific attributes.
     *
     * @param attr {@link Collection} of file {@link SPSSValueLabelDTO}
     */
    private void removeUselessFileAttributes(Collection<SPSSValueLabelDTO> attr) {
        if (attr == null) {
            attr = new ArrayList<>();
        }
        attr.removeIf((SPSSValueLabelDTO attribute) -> (!attribute.getValue().startsWith("@") || attribute.getValue().equals("@dw_construct")
                || attribute.getValue().equals("@dw_measocc") || attribute.getValue().equals("@dw_instrument") || attribute.getValue().equals("@dw_itemtext")
                || attribute.getValue().equals("@dw_filtervar")));

    }


    /**
     * Compares the recently saved variables and the variables from the previous version (DBS)
     * TODO: This function works, but direct accessing of list positions can trigger OutOfBounceExceptions. This should be revised.
     *
     * @param sForm {@link StudyForm}
     */
    public void sortVariablesAndSetMetaData(final StudyForm sForm) {
        List<RecordCompareDTO> compList = sForm.getCompList();
        List<SPSSVarDTO> newVars = sForm.getRecord().getVariables();
        List<SPSSVarDTO> prevVars = sForm.getPreviousRecordVersion().getVariables();
        boolean CSV = (sForm.getSelectedFileType() != null && sForm.getSelectedFileType().equals("CSV"));
        if (compList != null) {
            int position = 0;
            for (RecordCompareDTO comp : compList) {
                SPSSVarDTO newVar = null, prevVar = null;
                if (comp.getVarStatus().equals(VariableStatus.EQUAL) || comp.getVarStatus().equals(VariableStatus.EQUAL_CSV)) {
                    newVars.get(position).setId(prevVars.get(position).getId());
                } else if (comp.getVarStatus().equals(VariableStatus.MOVED) || comp.getVarStatus().equals(VariableStatus.MOVED_CSV)) {
                    newVars.get(position).setId(prevVars.get(comp.getMovedFrom() - 1).getId());
                } else if (comp.getVarStatus().equals(VariableStatus.MOVED_AND_META_CHANGED) || comp.getVarStatus().equals(VariableStatus.MOVED_AND_META_CHANGED_CSV)
                        || comp.getVarStatus().equals(VariableStatus.MOVED_AND_TYPE_CHANGED)) {
                    newVar = newVars.get(position);
                    prevVar = prevVars.get(comp.getMovedFrom() - 1);
                } else {
                    newVar = newVars.get(position);
                    prevVar = position < prevVars.size() ? prevVars.get(position) : null;
                }
                if (comp.isKeepExpMeta() && newVar != null && prevVar != null) {
                    if (CSV) {
                        newVar.setLabel(prevVar.getLabel());
                        newVar.setMissingFormat(prevVar.getMissingFormat());
                        newVar.setMissingVal1(prevVar.getMissingVal1());
                        newVar.setMissingVal2(prevVar.getMissingVal2());
                        newVar.setMissingVal3(prevVar.getMissingVal3());
                        newVar.setColumns(prevVar.getColumns());
                        newVar.setAligment(prevVar.getAligment());
                        newVar.setMeasureLevel(prevVar.getMeasureLevel());
                        newVar.setRole(prevVar.getRole());
                        newVar.setNumOfAttributes(prevVar.getNumOfAttributes());
                        newVar.setValues(prevVar.getValues());
                        newVar.setAttributes(prevVar.getAttributes());
                    }
                    if (prevVar.getDw_attributes() != null)
                        removeEmptyDWAttributes(prevVar.getDw_attributes());
                    newVar.getAttributes().addAll(prevVar.getDw_attributes());
                    if (newVar.getType().equals(SPSSVarTypes.SPSS_FMT_A) && !prevVar.getType().equals(SPSSVarTypes.SPSS_FMT_A)) {
                        newVar.setMeasureLevel(SPSSMeasLevel.SPSS_MLVL_UNK);
                    }
                } else if (!comp.isKeepExpMeta() && !CSV && newVar != null) {
                    if (newVar.getDw_attributes() != null)
                        removeEmptyDWAttributes(newVar.getDw_attributes());
                    if (newVar.getAttributes() == null)
                        newVar.setAttributes(new LinkedList<>());
                    newVar.getAttributes().addAll(newVar.getDw_attributes());
                }
                position++;
            }
        }
    }


    /**
     * Removes empty DW-attributes from the collection
     *
     * @param attributes {@link Collection} of {@link SPSSValueLabelDTO}
     */
    private void removeEmptyDWAttributes(final Collection<SPSSValueLabelDTO> attributes) {
        attributes.removeIf((SPSSValueLabelDTO att) -> (att.getValue() == null || att.getValue().trim().isEmpty()));
    }


    /**
     * Sets the missing types of a variable
     *
     * @param varVal {@link SPSSVarDTO} contains the new missing format
     * @param var    {@link SPSSVarDTO} Variables which has to be set
     */
    public void switchMissingType(final SPSSVarDTO varVal, final SPSSVarDTO var) {
        switch (varVal.getMissingFormat()) {
            case SPSS_NO_MISSVAL:
                var.setMissingVal1(null);
                var.setMissingVal2(null);
                var.setMissingVal3(null);
                break;
            case SPSS_ONE_MISSVAL:
                var.setMissingVal1(varVal.getMissingVal1());
                var.setMissingVal2(null);
                var.setMissingVal3(null);
                break;
            case SPSS_TWO_MISSVAL:
                var.setMissingVal1(varVal.getMissingVal1());
                var.setMissingVal2(varVal.getMissingVal2());
                var.setMissingVal3(null);
                break;
            case SPSS_THREE_MISSVAL:
                var.setMissingVal1(varVal.getMissingVal1());
                var.setMissingVal2(varVal.getMissingVal2());
                var.setMissingVal3(varVal.getMissingVal3());
                break;
            case SPSS_MISS_RANGE:
                var.setMissingVal1(varVal.getMissingVal1());
                var.setMissingVal2(varVal.getMissingVal2());
                var.setMissingVal3(null);
                break;
            case SPSS_MISS_RANGEANDVAL:
                var.setMissingVal1(varVal.getMissingVal1());
                var.setMissingVal2(varVal.getMissingVal2());
                var.setMissingVal3(varVal.getMissingVal3());
                break;
            default:
                log.warn("MissingFormat not known - " + varVal.getMissingFormat());
                break;
        }
    }

    /**
     * Returns a String which is build from a List of Strings
     *
     * @param messages {@link List} of Messages
     * @return All messages as one {@link String}
     */
    public String setMessageString(final List<String> messages) {
        String messageString = null;
        final AtomicInteger count = new AtomicInteger();
        if (messages != null && messages.size() > 0) {
            final int warningSize = messages.size();
            StringBuilder sb = new StringBuilder();
            messages.forEach(s -> {
                sb.append(s);
                if (count.incrementAndGet() < warningSize)
                    sb.append("<br />");
            });
            messageString = sb.toString();
        }
        return messageString;
    }

    /**
     * Returns a String which is build from a Set of Strings
     *
     * @param messages {@link Set} of Messages
     * @return All messages as one {@link String}
     */
    public String setMessageString(final Set<String> messages) {
        if (messages != null)
            return setMessageString(messages.parallelStream().collect(Collectors.toList()));
        else
            return null;
    }

    /**
     * Gets the Datamatrix from the Minio System and returns it.
     *
     * @param record {@link RecordDTO} Contains the record information
     * @param pid    {@link Long} Project identifier
     * @return the datamatrix as {@link String}
     * @throws DataWizSystemException If Minio errors occur
     */
    private String loadDataMatrix(final RecordDTO record, final long pid) throws DataWizSystemException {
        log.trace("Entering loadDataMatrix for Record [pid: {}; studtyid:{}; recordid: {}; path: {}]", () -> pid, record::getStudyId,
                record::getId, record::getMinioName);
        if (record.getMinioName() == null)
            return "";
        FileDTO file = new FileDTO();
        file.setFilePath(record.getMinioName());
        file.setFileName(record.getOriginalName());
        file.setProjectId(pid);
        file.setStudyId(record.getStudyId());
        file.setRecordID(record.getId());
        file.setContentType("application/json; charset=UTF-8");
        MinioResult res = minioUtil.getFile(file, true);
        if (res.equals(MinioResult.OK)) {
            log.trace("Leaving loadDataMatrix for Record [pid: {}; studyId:{}; recordId: {}; path: {}]", () -> pid, record::getStudyId,
                    record::getId, record::getMinioName);
            return new String(file.getContent());
        } else {
            log.error("FATAL: No Connection to Minio Server - please check Settings or Server");
            throw new DataWizSystemException("Minio returns an error MinioResult: " + res.name(), DataWizErrorCodes.MINIO_SAVE_ERROR);
        }
    }

}
