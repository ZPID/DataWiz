package de.zpid.datawiz.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.itextpdf.io.source.ByteArrayOutputStream;
import de.zpid.datawiz.dao.*;
import de.zpid.datawiz.dto.*;
import de.zpid.datawiz.enumeration.DWFieldTypes;
import de.zpid.datawiz.enumeration.DataWizErrorCodes;
import de.zpid.datawiz.enumeration.MinioResult;
import de.zpid.datawiz.enumeration.Roles;
import de.zpid.datawiz.exceptions.DataWizSystemException;
import de.zpid.datawiz.form.ExportProjectForm;
import de.zpid.datawiz.form.ProjectForm;
import de.zpid.datawiz.form.StudyForm;
import de.zpid.datawiz.spss.SPSSValueLabelDTO;
import de.zpid.datawiz.util.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.*;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


/**
 * Service class for the Export controller to separate the web logic from the business logic.
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
 * TODO This class should be checked because it may contain code redundancies or is not implemented optimally due to lack of time.
 **/
@Service
public class ExportService {

    private static final Logger log = LogManager.getLogger(ExportService.class);

    private final String FILES_PROJECT_FOLDER = "supplementary_files/";

    private final MessageSource messageSource;
    private final ClassPathXmlApplicationContext applicationContext;
    private final FileUtil fileUtil;
    private final MinioUtil minioUtil;
    private final ProjectDAO projectDAO;
    private final DmpDAO dmpDAO;
    private final FormTypesDAO formTypeDAO;
    private final FileDAO fileDAO;
    private final DDIUtil ddi;
    private final StudyDAO studyDAO;
    private final ContributorDAO contributorDAO;
    private final RecordDAO recordDAO;
    private final UserDAO userDAO;
    private final RecordService recordService;
    private final StudyService studyService;
    private final ConsistencyCheckUtil ccUtil;
    private final Environment env;
    private final StringUtil stringUtil;
    private final CSVUtil csvUtil;
    private final ITextUtil itextUtil;
    private final SpssIoService spssService;

    @Autowired
    public ExportService(MessageSource messageSource, ClassPathXmlApplicationContext applicationContext, StringUtil stringUtil,
                         StudyService studyService, ContributorDAO contributorDAO, RecordDAO recordDAO, FileUtil fileUtil,
                         StudyDAO studyDAO, Environment env, RecordService recordService, MinioUtil minioUtil, ProjectDAO projectDAO, DmpDAO dmpDAO,
                         FormTypesDAO formTypeDAO, FileDAO fileDAO, DDIUtil ddi, UserDAO userDAO, ConsistencyCheckUtil ccUtil, CSVUtil csvUtil, ITextUtil itextUtil, SpssIoService spssService) {
        this.messageSource = messageSource;
        this.applicationContext = applicationContext;
        this.stringUtil = stringUtil;
        this.studyService = studyService;
        this.contributorDAO = contributorDAO;
        this.recordDAO = recordDAO;
        this.fileUtil = fileUtil;
        this.studyDAO = studyDAO;
        this.env = env;
        this.recordService = recordService;
        this.minioUtil = minioUtil;
        this.projectDAO = projectDAO;
        this.dmpDAO = dmpDAO;
        this.formTypeDAO = formTypeDAO;
        this.fileDAO = fileDAO;
        this.ddi = ddi;
        this.userDAO = userDAO;
        this.ccUtil = ccUtil;
        this.csvUtil = csvUtil;
        this.itextUtil = itextUtil;
        this.spssService = spssService;
    }

    /**
     * Builds the export form for a project. This form contains all projects, studies and records to which the passed user has access.
     *
     * @param pid  Project identifier as long
     * @param user {@link UserDTO} contains the user information
     * @return {@link ExportProjectForm} contains the generated export information
     * @throws DataWizSystemException possible exceptions are: PROJECT_NOT_AVAILABLE, DATABASE_ERROR, USER_ACCESS_PROJECT_PERMITTED
     */
    public ExportProjectForm getExportForm(final long pid, final UserDTO user) throws DataWizSystemException {
        log.trace("Entering getExportForm for project [pid: {}] and user [mail: {}]", () -> pid, user::getEmail);
        ExportProjectForm exportForm = (ExportProjectForm) applicationContext.getBean("ExportProjectForm");
        if (user.hasRole(Roles.ADMIN) || user.hasRole(Roles.PROJECT_ADMIN, pid, false) || user.hasRole(Roles.PROJECT_WRITER, pid, false)
                || user.hasRole(Roles.PROJECT_READER, pid, false)) {
            try {
                ProjectDTO project = projectDAO.findById(pid);
                if (project != null && project.getId() > 0) {
                    exportForm.setProjectTitle(project.getTitle());
                    exportForm.setProjectId(project.getId());
                    exportForm.setStudies(new ArrayList<>());
                    exportForm.setMaterial(fileDAO.findProjectMaterialFiles(project));
                    List<StudyDTO> studies = studyDAO.findAllStudiesByProjectId(project);
                    if (studies != null) {
                        for (StudyDTO study : studies) {
                            ExportStudyDTO studExp = (ExportStudyDTO) applicationContext.getBean("ExportStudyDTO");
                            studExp.setStudyId(study.getId());
                            studExp.setStudyTitle(study.getTitle());
                            studExp.setRecords(new ArrayList<>());
                            studExp.setMaterial(fileDAO.findStudyMaterialFiles(study.getProjectId(), study.getId()));
                            List<RecordDTO> records = recordDAO.findRecordsWithStudyID(study.getId());
                            if (records != null) {
                                for (RecordDTO record : records) {
                                    ExportRecordDTO recExp = (ExportRecordDTO) applicationContext.getBean("ExportRecordDTO");
                                    recExp.setRecordId(record.getId());
                                    recExp.setVersionId(record.getVersionId());
                                    recExp.setRecordTitle(record.getRecordName());
                                    studExp.getRecords().add(recExp);
                                }
                            }
                            ccUtil.checkStudyConsistency(studExp, studyDAO.findById(study.getId(), project.getId(), false, false));
                            exportForm.getStudies().add(studExp);
                        }
                    }
                    ccUtil.checkDMPConsistency(exportForm, dmpDAO.findByID(project));
                } else {
                    throw new DataWizSystemException(messageSource.getMessage("logging.project.not.found", new Object[]{pid}, Locale.ENGLISH),
                            DataWizErrorCodes.PROJECT_NOT_AVAILABLE);
                }
            } catch (Exception e) {
                if (e instanceof DataWizSystemException) {
                    log.warn(messageSource.getMessage("logging.project.not.found", new Object[]{pid}, Locale.ENGLISH));
                    throw e;
                } else {
                    log.warn("DBS Exception during getExportForm", () -> e);
                    throw new DataWizSystemException(messageSource.getMessage("logging.database.error", new Object[]{e.getMessage()}, Locale.ENGLISH),
                            DataWizErrorCodes.DATABASE_ERROR);
                }
            }
        } else {
            log.warn(messageSource.getMessage("logging.user.permitted", new Object[]{user.getEmail(), "getExportForm for project", pid}, Locale.ENGLISH));
            throw new DataWizSystemException(
                    messageSource.getMessage("logging.user.permitted", new Object[]{user.getEmail(), "getExportForm for project", pid}, Locale.ENGLISH),
                    DataWizErrorCodes.USER_ACCESS_PROJECT_PERMITTED);
        }
        log.trace("Leaving getExportForm for project [pid: {}] and user [mail: {}]", () -> pid, user::getEmail);
        return exportForm;
    }

    /**
     * Creates the export List with the items selected by the user.
     *
     * @param exportForm {@link ExportProjectForm} contains the selected export information
     * @param pid        Project identifier as long
     * @param user       {@link UserDTO} contains the user information
     * @return {@link List} of {@link Entry} containing a {@link String} with the path where the file will be included in
     * the zip file and the filename, and the file content as byte[]
     * @throws Exception DataWizSystemException and IOExceptions
     */
    public List<Entry<String, byte[]>> createExportFileList(final ExportProjectForm exportForm, final long pid, final UserDTO user) throws Exception {
        log.trace("Entering createExportFileList for project [pid: {}] and user [mail: {}]", () -> pid, user::getEmail);
        List<Entry<String, byte[]>> files = new ArrayList<>();
        ProjectDTO projectDB = projectDAO.findById(pid);
        if (projectDB != null) {
            ProjectForm pForm = (ProjectForm) applicationContext.getBean("ProjectForm");
            createProjectExport(exportForm, files, projectDB, pForm);
            // Create Studies
            if (exportForm.getStudies() != null) {
                int studyCount = 1;
                for (ExportStudyDTO studyEx : exportForm.getStudies()) {
                    StudyForm sForm = (StudyForm) applicationContext.getBean("StudyForm");
                    List<FileDTO> sFiles = null;
                    StudyDTO study;
                    StudyDTO studyDB = studyDAO.findById(studyEx.getStudyId(), exportForm.getProjectId(), false, false);
                    List<UserDTO> sUploader = new ArrayList<>();
                    if (studyDB != null) {
                        String studyFolder = "studies/(" + studyCount++ + ") " + stringUtil.formatFilename(studyDB.getTitle()) + "/";
                        studyService.setStudyDTOExport(studyDB);
                        if (studyEx.isExportMetaData() || studyEx.isExportStudyMaterial()) {
                            if (studyEx.isExportMetaData()) {
                                study = studyDB;
                            } else {
                                study = (StudyDTO) applicationContext.getBean("StudyDTO");
                                study.setId(studyDB.getId());
                                study.setTitle(studyDB.getTitle());
                            }
                            if (studyEx.isExportStudyMaterial()) {
                                sFiles = fileDAO.findStudyMaterialFiles(study.getProjectId(), study.getId());
                                if (sFiles != null && sFiles.size() > 0) {
                                    setAdditionalFilestoExportList(files, sFiles, studyFolder + FILES_PROJECT_FOLDER);
                                    for (FileDTO file : sFiles) {
                                        sUploader.add(userDAO.findById(file.getUserId()));
                                    }
                                }
                            }
                            sForm.setProject(projectDB);
                            sForm.setStudy(study);
                            sForm.setCollectionModes(formTypeDAO.findAllByType(true, DWFieldTypes.COLLECTIONMODE));
                            sForm.setSourFormat(formTypeDAO.findAllByType(true, DWFieldTypes.DATAFORMAT));
                            Document sdoc = ddi.createStudyDocument(sForm, sFiles, sUploader, user);
                            if (sdoc != null) {
                                String STUDY_FILE_NAME = "study_long_term.xml";
                                files.add(new SimpleEntry<>(studyFolder + STUDY_FILE_NAME, createByteArrayFromXML(sdoc)));
                            } else {
                                throw new DataWizSystemException(
                                        messageSource.getMessage("logging.xml.create.error", new Object[]{"createProjectDocument", exportForm.toString()}, Locale.ENGLISH),
                                        DataWizErrorCodes.STUDY_NOT_AVAILABLE);
                            }
                        }
                        createRecordExport(exportForm, pid, user, files, pForm, studyEx, sForm, studyDB, studyFolder);
                    } else {
                        throw new DataWizSystemException(messageSource.getMessage("logging.study.not.found", new Object[]{studyEx.getStudyId()}, Locale.ENGLISH),
                                DataWizErrorCodes.STUDY_NOT_AVAILABLE);
                    }
                }
            }
        } else {
            throw new DataWizSystemException(messageSource.getMessage("logging.project.not.found", new Object[]{exportForm.getProjectId()}, Locale.ENGLISH),
                    DataWizErrorCodes.PROJECT_NOT_AVAILABLE);
        }
        log.trace("Leaving createExportFileList for project [pid: {}] and user [mail: {}]", () -> pid, user::getEmail);
        return files;
    }

    /**
     * Creates the record export (ddi file) for all records of a study
     *
     * @param exportForm  {@link ExportProjectForm} contains the selected export information
     * @param pid         Project identifier as long
     * @param user        {@link UserDTO} contains the user information
     * @param files       {@link List} of {@link Entry} contains the files which has to be exported - this function includes its results to this list
     * @param pForm       {@link ProjectForm} contains project information
     * @param studyEx     {@link ExportStudyDTO} contains export information for a study (which parts have to be exported)
     * @param sForm       {@link StudyForm} contains study information which do not fit into StudyDTO
     * @param studyDB     {@link StudyDTO} contains study information
     * @param studyFolder {@link String} file path + name, needed for zip container
     * @throws Exception DataWizSystemException and IOExceptions
     */
    private void createRecordExport(final ExportProjectForm exportForm, final long pid, final UserDTO user, final List<Entry<String, byte[]>> files,
                                    final ProjectForm pForm, final ExportStudyDTO studyEx, final StudyForm sForm, final StudyDTO studyDB, final String studyFolder)
            throws Exception {
        log.trace("Entering createRecordExport for project [pid: {}] and user [mail: {}]", () -> pid, user::getEmail);
        if (studyEx.getRecords() != null) {
            int recCount = 1;
            for (ExportRecordDTO recordEx : studyEx.getRecords()) {
                if (recordEx.isExportMetaData() || recordEx.isExportCodebook() || recordEx.isExportMatrix()) {
                    RecordDTO recordDB = recordDAO.findRecordWithID(recordEx.getRecordId(), recordEx.getVersionId());
                    if (recordDB != null) {
                        String recordFolder = studyFolder + "records/(" + recCount++ + ") " + stringUtil.formatFilename(recordDB.getRecordName()) + "/";
                        RecordDTO record = (RecordDTO) applicationContext.getBean("RecordDTO");
                        List<String> parsingErrors = new LinkedList<>();
                        recordService.transformDataMatrix(parsingErrors, recordDB, false, pid);
                        if (recordEx.isExportMetaData() && recordEx.isExportCodebook()) {
                            recordDB.setVariables(recordDAO.findVariablesByVersionID(recordDB.getVersionId()));
                            recordService.transformCodebook(parsingErrors, recordDB, false);
                            record = recordDB;
                        } else if (!recordEx.isExportMetaData() && recordEx.isExportCodebook()) {
                            record.setRecordName(recordDB.getRecordName());
                            record.setVersionId(recordDB.getVersionId());
                            record.setId(recordDB.getId());
                            record.setStudyId(recordDB.getStudyId());
                            record.setVariables(recordDAO.findVariablesByVersionID(recordDB.getVersionId()));
                            recordService.transformCodebook(parsingErrors, record, false);
                        } else if (recordEx.isExportMetaData() && !recordEx.isExportCodebook()) {
                            record = recordDB;
                        } else {
                            record = null;
                        }
                        if (!parsingErrors.isEmpty()) {
                            StringBuilder s = new StringBuilder();
                            parsingErrors.forEach(pe -> s.append(pe).append(" ; "));
                            throw new DataWizSystemException(
                                    messageSource.getMessage("logging.record.parsing.error", new Object[]{recordEx.getRecordId(), s.toString()}, Locale.ENGLISH),
                                    DataWizErrorCodes.RECORD_PARSING_ERROR);
                        }
                        FileDTO fileHash = null;
                        StringBuilder res = new StringBuilder();
                        if (recordEx.isExportMatrix()) {
                            byte[] content = null;
                            RecordDTO copy = (RecordDTO) ObjectCloner.deepCopy(recordDB);
                            copy.setVariables(recordDAO.findVariablesByVersionID(copy.getVersionId()));
                            if (copy.getDataMatrix() != null && !copy.getDataMatrix().isEmpty())
                                content = csvUtil.exportCSV(copy, res, true);
                            if (content != null && res.toString().trim().isEmpty()) {
                                fileHash = (FileDTO) applicationContext.getBean("FileDTO");
                                fileHash.setMd5checksum(fileUtil.getFileChecksum("MD5", new BufferedInputStream(new ByteArrayInputStream(content))));
                                fileHash.setSha1Checksum(fileUtil.getFileChecksum("SHA-1", new BufferedInputStream(new ByteArrayInputStream(content))));
                                fileHash.setSha256Checksum(fileUtil.getFileChecksum("SHA-256", new BufferedInputStream(new ByteArrayInputStream(content))));
                                String RECORD_MATRIX_NAME = "record_matrix.csv";
                                files.add(new SimpleEntry<>(recordFolder + RECORD_MATRIX_NAME, content));
                            } else if (!res.toString().trim().isEmpty()) {
                                throw new DataWizSystemException(
                                        messageSource.getMessage("logging.record.parsing.error",
                                                new Object[]{recordEx.getRecordId(), messageSource.getMessage(res.toString(), null, Locale.ENGLISH)}, Locale.ENGLISH),
                                        DataWizErrorCodes.RECORD_PARSING_ERROR);
                            }
                        }
                        if (record != null) {
                            sForm.setRecord(record);
                            sForm.setStudy(studyDB);
                            Document rdoc = ddi.createRecordDocument(sForm, fileHash, user);
                            if (rdoc != null) {
                                if (recordEx.isExportCodebook()) {
                                    String RECORD_CODEBOOK_NAME_CSV = "record_codebook.csv";
                                    if (record.getVariables() != null && !record.getVariables().isEmpty() && record.getDataMatrix() != null)
                                        files.add(new SimpleEntry<>(recordFolder + RECORD_CODEBOOK_NAME_CSV, csvUtil.exportCSV(record, res, false)));
                                    if (!res.toString().trim().isEmpty()) {
                                        throw new DataWizSystemException(
                                                messageSource.getMessage("logging.record.parsing.error",
                                                        new Object[]{recordEx.getRecordId(), messageSource.getMessage(res.toString(), null, Locale.ENGLISH)}, Locale.ENGLISH),
                                                DataWizErrorCodes.RECORD_PARSING_ERROR);
                                    }
                                    ProjectDTO project = pForm.getProject();
                                    if (project == null || project.getId() == 0)
                                        project = projectDAO.findById(pid);
                                    ContributorDTO primContri = pForm.getPrimaryContributor();
                                    if (project != null) {
                                        if (primContri == null || primContri.getId() == 0)
                                            primContri = contributorDAO.findPrimaryContributorByProject(project);
                                        recordDB.setVariables(recordDAO.findVariablesByVersionID(recordDB.getVersionId()));
                                        recordService.transformCodebook(parsingErrors, recordDB, false);
                                        String RECORD_CODEBOOK_NAME_PDF = "record_codebook.pdf";
                                        files.add(new SimpleEntry<>(recordFolder + RECORD_CODEBOOK_NAME_PDF,
                                                itextUtil.createRecordCodeBookPDFA(recordDB, studyDB, project, primContri, false, false)));
                                    }
                                }
                                String RECORD_FILE_NAME = "record_long_term.xml";
                                files.add(new SimpleEntry<>(recordFolder + RECORD_FILE_NAME, createByteArrayFromXML(rdoc)));
                            } else {
                                throw new DataWizSystemException(messageSource.getMessage("logging.xml.create.error",
                                        new Object[]{"createProjectDocument", exportForm.toString()}, LocaleContextHolder.getLocale()), DataWizErrorCodes.STUDY_NOT_AVAILABLE);
                            }
                        }
                    }
                }
            }
        }
        log.trace("Leaving createRecordExport for project [pid: {}] and user [mail: {}]", () -> pid, user::getEmail);
    }

    /**
     * Creates the project export (ddi file)
     *
     * @param exportForm {@link ExportProjectForm} contains the selected export information
     * @param files      {@link List} of {@link Entry} contains the files which has to be exported - this function includes its results to this list
     * @param projectDB  {@link ProjectDTO} contains project information
     * @param pForm      {@link ProjectForm} contains project information which do not fit into ProjectDTO
     * @throws DataWizSystemException DataWizSystemException
     */
    private void createProjectExport(final ExportProjectForm exportForm, final List<Entry<String, byte[]>> files, final ProjectDTO projectDB, final ProjectForm pForm) throws DataWizSystemException {
        log.trace("Entering createProjectExport for project [pid: {}]", projectDB::getId);
        if (exportForm.isExportMetaData() || exportForm.isExportDMP() || exportForm.isExportProjectMaterial()) {
            if (exportForm.isExportMetaData()) {
                pForm.setProject(projectDB);
                pForm.setContributors(contributorDAO.findByProject(projectDB, false, false));
            } else {
                ProjectDTO project = (ProjectDTO) applicationContext.getBean("ProjectDTO");
                project.setId(projectDB.getId());
                project.setTitle(projectDB.getTitle());
                project.setDescription(projectDB.getDescription());
                project.setFunding(projectDB.getFunding());
                pForm.setProject(project);
            }
            List<UserDTO> pUploader = new ArrayList<>();
            List<FileDTO> pFiles = null;
            if (exportForm.isExportProjectMaterial()) {
                pFiles = fileDAO.findProjectMaterialFiles(projectDB);
                if (pFiles != null && pFiles.size() > 0) {
                    setAdditionalFilestoExportList(files, pFiles, FILES_PROJECT_FOLDER);
                    pForm.setFiles(pFiles);
                    for (FileDTO file : pFiles) {
                        pUploader.add(userDAO.findById(file.getUserId()));
                    }
                }
            }
            pForm.setPrimaryContributor(contributorDAO.findPrimaryContributorByProject(projectDB));
            if (exportForm.isExportDMP()) {
                DmpDTO dmpDB = dmpDAO.findByID(projectDB);
                if (dmpDB != null && dmpDB.getId() > 0) {
                    dmpDB.setUsedDataTypes(formTypeDAO.findSelectedFormTypesByIdAndType(dmpDB.getId(), DWFieldTypes.DATATYPE, false));
                    dmpDB.setUsedCollectionModes(formTypeDAO.findSelectedFormTypesByIdAndType(dmpDB.getId(), DWFieldTypes.COLLECTIONMODE, false));
                    dmpDB.setSelectedMetaPurposes(formTypeDAO.findSelectedFormTypesByIdAndType(dmpDB.getId(), DWFieldTypes.METAPORPOSE, false));
                    pForm.setDataTypes(formTypeDAO.findAllByType(false, DWFieldTypes.DATATYPE));
                    pForm.setCollectionModes(formTypeDAO.findAllByType(false, DWFieldTypes.COLLECTIONMODE));
                    pForm.setMetaPurposes(formTypeDAO.findAllByType(false, DWFieldTypes.METAPORPOSE));
                }
                pForm.setDmp(dmpDB);
            }
            Document pdoc = ddi.createProjectDocument(pForm, pFiles, pUploader);
            String PROJECT_FILE_NAME = "project_long_term.xml";
            if (pdoc != null)
                files.add(new SimpleEntry<>(PROJECT_FILE_NAME, createByteArrayFromXML(pdoc)));
            else {
                throw new DataWizSystemException(
                        messageSource.getMessage("logging.xml.create.error", new Object[]{"createProjectDocument", exportForm.toString()}, Locale.ENGLISH),
                        DataWizErrorCodes.STUDY_NOT_AVAILABLE);
            }
        }
        log.trace("Leaving createProjectExport for project [pid: {}]", projectDB::getId);
    }

    /**
     * Creates record export (matrix and/or codebook)
     *
     * @param pid         Project identifier as long
     * @param exportType  {@link String} contains export format
     * @param attachments Used for PDF Exports - True if codebook and matrix have to be attached to the pdf doc
     * @param record      {@link RecordDTO} contains record information
     * @param res         {@link StringBuilder} used if errors occur
     * @return byte[] of export file
     * @throws Exception IOException or DataWizSystemException
     */
    public byte[] getRecordExportContentAsByteArray(final long pid, final String exportType, final boolean attachments, final RecordDTO record,
                                                    final StringBuilder res) throws Exception {
        log.trace("Entering getRecordExportContentAsByteArray for record [pid: {}] and exportType[{}]", record::getId, () -> exportType);
        byte[] content = null;
        if (record.getVariables() == null || record.getVariables().isEmpty())
            res.insert(0, "error.record.empty");
        else
            switch (exportType) {
                case "CSVMatrix":
                    content = csvUtil.exportCSV(record, res, true);
                    break;
                case "CSVCodebook":
                    content = csvUtil.exportCSV(record, res, false);
                    break;
                case "JSON":
                    content = exportJSON(record, res);
                    break;
                case "SPSS":
                    try {
                        AbstractMap.SimpleEntry<HttpStatus, byte[]> response = exportSPSSFile(record, res);
                        switch (response.getKey()) {
                            case OK:
                                content = response.getValue();
                                break;
                            case PARTIAL_CONTENT:
                                res.insert(0, "error.spss.api.partial.content");
                                break;
                            case SERVICE_UNAVAILABLE:
                                res.insert(0, "error.spss.api.service.unavailable");
                                log.error("SPSS WebService not available - Check if native lib is loaded!");
                                break;
                            case INTERNAL_SERVER_ERROR:
                                res.insert(0, "error.spss.api.internal.error");
                                log.error("SPSS WebService INTERNAL_SERVER_ERROR - Possible FileIO Exception - Check File Access for WebService");
                                break;
                            case UNPROCESSABLE_ENTITY:
                                res.insert(0, "error.spss.api.unprocessable.entity");
                                log.error("SPSS WebService UNPROCESSABLE_ENTITY - File Transfer Exception");
                                break;
                        }
                    } catch (Exception e) {
                        res.insert(0, "error.spss.api.not.found");
                        log.error("SPSS WebService not available - Check if Service is running! Exception: {}", () -> e);
                    }
                    break;
                case "PDF":
                    ProjectDTO project = projectDAO.findById(pid);
                    ContributorDTO primaryContri = null;
                    if (project != null)
                        primaryContri = contributorDAO.findPrimaryContributorByProject(project);
                    StudyDTO study = studyDAO.findById(record.getStudyId(), pid, true, false);
                    if (study != null)
                        study.setContributors(contributorDAO.findByStudy(study.getId()));
                    content = itextUtil.createRecordCodeBookPDFA(record, study, project, primaryContri, false, attachments);
                    break;
                case "CSVZIP":
                    List<Entry<String, byte[]>> files = new ArrayList<>();
                    files.add(new SimpleEntry<>(record.getRecordName() + "_Matrix.csv", csvUtil.exportCSV(record, res, true)));
                    files.add(new SimpleEntry<>(record.getRecordName() + "_Codebook.csv", csvUtil.exportCSV(record, res, false)));
                    content = exportZip(files, res);
                    break;
            }
        log.trace("Leaving getRecordExportContentAsByteArray for record [pid: {}] and exportType[{}]", record::getId, () -> exportType);
        return content;
    }

    /**
     * Creates the zip file with all export content and returns it as byte[]
     *
     * @param files {@link List} of {@link Entry} contains the files which has to be exported
     * @param res   {@link StringBuilder} used if errors occur
     * @return export zip as byte[]
     */
    public byte[] exportZip(final List<Entry<String, byte[]>> files, final StringBuilder res) {
        log.trace("Entering exportZip for Num of File: [{}]", files::size);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ZipOutputStream zos = new ZipOutputStream(baos);
        String fileName = null;
        byte[] content = null;
        try {
            for (Entry<String, byte[]> file : files) {
                fileName = file.getKey();
                ZipEntry entry = new ZipEntry(file.getKey());
                entry.setSize(file.getValue().length);
                zos.putNextEntry(entry);
                zos.write(file.getValue());
                zos.closeEntry();
            }
            zos.close();
            content = baos.toByteArray();
            baos.close();
        } catch (Exception e) {
            log.warn("Error during exportZip: Filename: [{}] Exception: {}", fileName, e);
            res.insert(0, "export.error.exception.thrown");
        }
        log.trace("Leaving exportZip with result [{}]", res.toString().trim().isEmpty() ? "OK" : res.toString());
        return content;
    }

    /**
     * This function prepares the record DTO for the export and transfers it to the SPSS IO Module. If the return of the SPSS IO Module is valid, a byte[] which
     * contains the spss file is returned. If some errors occur diring this process, null is returned and an error code is written to ResponseEntity<String> resp.
     *
     * @param record Complete copy of the requested record version
     * @param res    Reference parameter to handle export errors in the calling function
     * @return the spss file as byte array
     */
    private SimpleEntry<HttpStatus, byte[]> exportSPSSFile(final RecordDTO record, final StringBuilder res) {
        SimpleEntry<HttpStatus, byte[]> ret = new SimpleEntry<>(HttpStatus.CONFLICT, null);
        if (record != null) {
            log.trace("Entering exportSPSSFile for RecordDTO: [id: {}; version:{}]", record::getId, record::getVersionId);
            record.setFileIdString(record.getRecordName() + "_Version_(" + record.getVersionId() + ")");
            record.setErrors(new LinkedList<>());
            if (record.getAttributes() == null) {
                record.setAttributes(new LinkedList<>());
            }
            record.getAttributes().add(new SPSSValueLabelDTO("@dw_construct", "@dw_construct"));
            record.getAttributes().add(new SPSSValueLabelDTO("@dw_measocc", "@dw_measocc"));
            record.getAttributes().add(new SPSSValueLabelDTO("@dw_instrument", "@dw_instrument"));
            record.getAttributes().add(new SPSSValueLabelDTO("@dw_itemtext", "@dw_itemtext"));
            record.getAttributes().add(new SPSSValueLabelDTO("@dw_filtervar", "@dw_filtervar"));
            record.getAttributes().add(new SPSSValueLabelDTO("CreatedBy", "DataWiz"));
            record.getAttributes().add(new SPSSValueLabelDTO("DataWizStudyId", String.valueOf(record.getStudyId())));
            record.getAttributes().add(new SPSSValueLabelDTO("DataWizRecordId", String.valueOf(record.getId())));
            record.getAttributes().add(new SPSSValueLabelDTO("DataWizVersionId", String.valueOf(record.getVersionId())));
            record.getAttributes().add(new SPSSValueLabelDTO("DataWizLastUpdateAt", String.valueOf(record.getChanged())));
            record.getAttributes().add(new SPSSValueLabelDTO("DataWizLastUpdateBy", record.getChangedBy()));
            // record.getAttributes().add(new SPSSValueLabelDTO("DataWizLastUpdateLog", record.getChangeLog()));
            // record.getAttributes().add(new SPSSValueLabelDTO("DataWizLRecordDescription", record.getDescription()));
            if (record.getVariables() != null && record.getDataMatrix() != null) {
                record.getVariables().parallelStream().forEach(var -> var.setDecimals(var.getDecimals() > 16 ? 16 : var.getDecimals()));
                ret = this.spssService.fromJson(record);
            } else {
                res.insert(0, "export.recorddto.empty");
            }
            log.trace("Leaving exportSPSSFile with result [{}]", res.toString().trim().isEmpty() ? "OK" : res.toString());
        } else {
            res.insert(0, "export.recorddto.empty");
        }
        return ret;
    }


    /**
     * This function transfers the record into a JSON String and returns it as byte array. If some errors occur diring this process, null is returned and an error
     * code is written to ResponseEntity resp.
     *
     * @param record Complete copy of the requested record version
     * @param res    Reference parameter to handle export errors in the calling function
     * @return the csv string as byte array
     */
    private byte[] exportJSON(final RecordDTO record, final StringBuilder res) {
        log.trace("Entering exportJSON for RecordDTO: [id: {}; version:{}]", record::getId, record::getVersionId);
        byte[] content = null;
        if (record.getVariables() != null && record.getDataMatrix() != null) {
            try {
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                String json = gson.toJson(record);
                if (json != null && json.length() > 0) {
                    content = json.getBytes(Charset.forName("UTF-8"));
                } else {
                    res.insert(0, "export.json.string.empty");
                }
            } catch (Exception e) {
                log.warn("Error during JSON export: RecordDTO: [id: {}; version:{}] Exception: ", record::getId, record::getVersionId, () -> e);
                res.insert(0, "export.error.exception.thrown");
            }
        } else {
            res.insert(0, "export.recorddto.empty");
        }
        log.trace("Leaving exportJSON with result [{}]", res.toString().trim().isEmpty() ? "OK" : res.toString());
        return content;
    }

    /**
     * Parses a DOM4J Document to an byte[]
     *
     * @param doc {@link Document}
     * @return Document as byte[]
     */
    private byte[] createByteArrayFromXML(final Document doc) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            XMLWriter writer = new XMLWriter(baos, OutputFormat.createPrettyPrint());
            writer.write(doc);
            return baos.toString().getBytes();
        } catch (IOException e) {
            log.error("Unexpected IO-Error during createByteArrayFromXML", () -> e);
            return null;
        }
    }

    /**
     * Saves the additional project and/or study files into the exportList
     *
     * @param exportList {@link List} of {@link Entry} contains the files which has to be exported
     * @param files      {@link List} of {@link FileDTO} contains the additional Files
     * @param folderName {@link String} file path, needed for zip container
     * @throws DataWizSystemException Minio Exceptions
     */
    private void setAdditionalFilestoExportList(final List<Entry<String, byte[]>> exportList, final List<FileDTO> files, final String folderName)
            throws DataWizSystemException {
        Set<String> filenames = new HashSet<>();
        int doublettCount = 0;
        for (FileDTO file : files) {
            if (!filenames.add(file.getFileName())) {
                if (file.getFileName().split("\\.").length > 1) {
                    file.setFileName(file.getFileName().replaceFirst("\\.", " (" + ++doublettCount + ")\\."));
                } else
                    file.setFileName(file.getFileName() + "(" + ++doublettCount + ")");
            }
            MinioResult result = minioUtil.getFile(file, false);
            if (result.equals(MinioResult.OK) && file.getContent().length > 0) {
                // only files < 20 MB
                if (file.getFileSize() < 20000000)
                    exportList.add(new SimpleEntry<>(folderName + file.getFileName(), file.getContent()));
            } else {
                throw new DataWizSystemException(
                        messageSource.getMessage("logging.minio.read.error",
                                new Object[]{result.name(), file.getFileName(), file.getProjectId(), file.getStudyId(), file.getRecordID(), file.getVersion(),
                                        file.getFilePath(), env.getRequiredProperty("organisation.admin.email")},
                                LocaleContextHolder.getLocale()),
                        DataWizErrorCodes.MINIO_READ_ERROR);
            }
        }
    }
}
