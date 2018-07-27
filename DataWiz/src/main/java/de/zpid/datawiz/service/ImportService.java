package de.zpid.datawiz.service;

import com.google.gson.Gson;
import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import de.zpid.datawiz.dao.RecordDAO;
import de.zpid.datawiz.dto.FileDTO;
import de.zpid.datawiz.dto.RecordCompareDTO;
import de.zpid.datawiz.dto.RecordDTO;
import de.zpid.datawiz.dto.UserDTO;
import de.zpid.datawiz.enumeration.DataWizErrorCodes;
import de.zpid.datawiz.enumeration.VariableStatus;
import de.zpid.datawiz.exceptions.DataWizSystemException;
import de.zpid.datawiz.form.StudyForm;
import de.zpid.datawiz.util.DateUtil;
import de.zpid.datawiz.util.FileUtil;
import de.zpid.datawiz.util.ObjectCloner;
import de.zpid.datawiz.util.RegexUtil;
import de.zpid.spss.SPSSIO;
import de.zpid.spss.dto.SPSSFileDTO;
import de.zpid.spss.dto.SPSSValueLabelDTO;
import de.zpid.spss.dto.SPSSVarDTO;
import de.zpid.spss.util.*;
import org.apache.any23.encoding.TikaEncodingDetector;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Service class for the Import controller to separate the web logic from the business logic.
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
public class ImportService {

    private static Logger log = LogManager.getLogger(ImportService.class);
    private ClassPathXmlApplicationContext applicationContext;
    private SPSSIO spss;
    private FileUtil fileUtil;
    private MessageSource messageSource;
    private RecordDAO recordDAO;

    @Autowired
    public ImportService(ClassPathXmlApplicationContext applicationContext, SPSSIO spss, FileUtil fileUtil, MessageSource messageSource, RecordDAO recordDAO) {
        super();
        this.applicationContext = applicationContext;
        this.spss = spss;
        this.fileUtil = fileUtil;
        this.messageSource = messageSource;
        this.recordDAO = recordDAO;
    }

    /**
     * This function is the first step in importing SPSS and CSV files. The uploaded files are validated in this step and checked for (syntax) errors.
     * The content check takes place manually in the next step.
     *
     * @param pid      Project identifier as long
     * @param studyId  Study identifier as long
     * @param recordId Record identifier as long
     * @param sForm    {@link StudyForm} contains the file, and the upload information
     * @param user     {@link UserDTO} contains user information
     * @throws DataWizSystemException Import Exceptions
     */
    public void importFile(final long pid, final long studyId, final long recordId, final StudyForm sForm, final UserDTO user)
            throws DataWizSystemException {
        boolean error;
        Set<String> warnings = new HashSet<>();
        List<String> errors = new ArrayList<>();
        String fileType = null;
        String[] file = null;
        if (sForm.getSelectedFileType() != null && sForm.getSelectedFileType().equals("SPSS") && sForm.getSpssFile() != null
                && sForm.getSpssFile().getOriginalFilename() != null) {
            file = sForm.getSpssFile().getOriginalFilename().split("\\.");
        } else if (sForm.getSelectedFileType() != null && sForm.getSelectedFileType().equals("CSV") && sForm.getCsvFile() != null
                && sForm.getCsvFile().getOriginalFilename() != null) {
            file = sForm.getCsvFile().getOriginalFilename().split("\\.");
        }
        if (file != null && file.length > 1)
            fileType = file[file.length - 1].trim().toLowerCase();
        if (sForm.getSelectedFileType() != null && sForm.getSelectedFileType().equals("SPSS") && sForm.getSpssFile() != null && sForm.getSpssFile().getSize() > 0
                && fileType != null && fileType.equals("sav")) {
            error = validateSPSSFile(pid, studyId, recordId, sForm, user, errors);
        } else if (sForm.getSelectedFileType() != null && sForm.getSelectedFileType().equals("CSV") && sForm.getCsvFile() != null
                && sForm.getCsvFile().getSize() > 0 && fileType != null && (fileType.equals("csv") || fileType.equals("txt") || fileType.equals("dat"))) {
            error = validateCSVFile(pid, studyId, recordId, sForm, user, warnings, errors);
        } else if ((sForm.getSelectedFileType() != null && sForm.getSelectedFileType().equals("SPSS")
                && (sForm.getSpssFile() == null || sForm.getSpssFile().getSize() <= 0))
                || (sForm.getSelectedFileType() != null && sForm.getSelectedFileType().equals("CSV")
                && (sForm.getCsvFile() == null || sForm.getCsvFile().getSize() <= 0))) {
            throw new DataWizSystemException(
                    messageSource.getMessage("logging.selected.file.empty", new Object[]{sForm.getSelectedFileType()}, LocaleContextHolder.getLocale()),
                    DataWizErrorCodes.IMPORT_FILE_IS_EMPTY);
        } else {
            throw new DataWizSystemException(
                    messageSource.getMessage("logging.selected.filetype.missmatch", new Object[]{sForm.getSelectedFileType()}, LocaleContextHolder.getLocale()),
                    DataWizErrorCodes.IMPORT_TYPE_NOT_SUPPORTED);
        }
        sForm.setParsingError(error);
        sForm.setErrors(errors);
        if (!error)
            sForm.setWarnings(warnings);
    }

    /**
     * This function generates the import report from the previous importFile function. This report is used to check the data manually by the data provider.
     *
     * @param recordId Record identifier as long
     * @param sForm    {@link StudyForm} contains the file, and the upload information
     * @throws Exception DBS and IO Exceptions
     */
    public void loadImportReport(final long recordId, final StudyForm sForm) throws Exception {
        RecordDTO lastVersion = recordDAO.findRecordWithID(recordId, 0);
        List<SPSSVarDTO> vars = recordDAO.findVariablesByVersionID(lastVersion.getVersionId());
        for (SPSSVarDTO newVar : vars) {
            newVar.setAttributes(recordDAO.findVariableAttributes(newVar.getId(), false));
            newVar.setValues(recordDAO.findVariableValues(newVar.getId(), false));
            sortVariableAttributes(newVar);
        }
        sForm.getRecord().getVariables().parallelStream().forEach(var -> {
            if (sortVariableAttributes(var)) {
                if (sForm.getWarnings() == null) {
                    sForm.setWarnings(new HashSet<>());
                }
                sForm.getWarnings().add(messageSource.getMessage("dataset.import.report.error.dw.attr", new Object[]{var.getName()}, LocaleContextHolder.getLocale()));
            }
        });
        lastVersion.setVariables(vars);
        sForm.setPreviousRecordVersion(lastVersion);
        compareVarVersion(sForm);
    }


    /**
     * Validating a CSV file is not trivial, so this function is a bit more complicated and should be checked and revised if necessary.
     * It does what it's supposed to, but it's a little confusing.
     * <p>
     * The following procedures are applied: <br>
     * - Reading the file encoding<br>
     * - Validation of the CSV file for syntactic correctness (number of columns)<br>
     * - Checking the separator (is passed) and the string quote character (automatic recognition)<br>
     * - Automatic recognition of number, text and date formats.<br>
     * - Transfer of various date formats into the standard format.<br>
     * - Read the header line (if available) and validate the variable names<br>
     *
     * @param pid      Project identifier as long
     * @param studyId  Study identifier as long
     * @param recordId Record identifier as long
     * @param sForm    {@link StudyForm} contains the file, and the upload information
     * @param user     {@link UserDTO} contains user information
     * @param warnings {@link List} of {@link String} with import warning messages
     * @param errors   {@link List} of {@link String} with import error messages
     * @return true if file is valid, otherwise false
     */
    private Boolean validateCSVFile(final long pid, final long studyId, final long recordId, StudyForm sForm, final UserDTO user,
                                    Set<String> warnings, List<String> errors) {
        FileDTO file = null;
        boolean init = true, error = false;
        try {
            file = fileUtil.buildFileDTO(pid, studyId, recordId, 0, user.getId(), sForm.getCsvFile());
        } catch (Exception e) {
            error = true;
            log.error("Error creating FileDTO with input: [pid: {}; studyid: {}; recordid: {}; userid: {}; uploadedFile: {}]", () -> pid, () -> studyId,
                    () -> recordId, () -> 0, user::getId, () -> (sForm.getCsvFile() == null) ? "null" : sForm.getCsvFile().getOriginalFilename(), () -> e);
            errors.add(messageSource.getMessage("error.upload.creating.file", null, LocaleContextHolder.getLocale()));
        }
        if (!error) {
            Charset ch = null;
            try {
                ch = Charset.forName(new TikaEncodingDetector().guessEncoding(sForm.getCsvFile().getInputStream()));
                if (ch.name().equals("IBM500")) {
                    ch = Charset.forName("Latin1");
                } else if (!ch.name().equals("UTF-8") && !ch.name().equals("Latin1") && !ch.name().equals("ISO-8859-1")) {
                    warnings.add(messageSource.getMessage("warning.charset.detection.standard", new Object[]{ch.name()}, LocaleContextHolder.getLocale()));
                }
            } catch (Exception e1) {
                log.warn("Detecting Charset for CSV File not successful - set Charset to UTF-8");
                warnings.add(messageSource.getMessage("warning.charset.detection", null, LocaleContextHolder.getLocale()));
            }
            try {
                CSVParser parser = new CSVParserBuilder().withSeparator((sForm.getCsvSeperator() == 't' ? '\t' : sForm.getCsvSeperator()))
                        .withQuoteChar(sForm.getCsvQuoteChar() == 'q' ? '\"' : '\'').build();
                CSVReader reader = new CSVReaderBuilder(
                        new BufferedReader(new InputStreamReader(sForm.getCsvFile().getInputStream(), ch != null ? ch : Charset.forName("UTF-8")))).withCSVParser(parser)
                        .build();
                String[] nextLine;
                int numOfCol = 0, lineNumber = 1;
                List<List<Object>> matrix = new ArrayList<>();
                List<SPSSVarDTO> vars = new ArrayList<>();
                List<String> dateFormatList = new ArrayList<>();
                List<SPSSVarTypes> types = new ArrayList<>();
                List<String[]> importMatrix = new ArrayList<>();
                int varCount = 1;
                if (sForm.isHeaderRow() && (nextLine = reader.readNext()) != null) {
                    numOfCol = nextLine.length;
                    Set<String> store = new HashSet<>();
                    int i = 0;
                    for (String s : nextLine) {
                        s = s.trim();
                        SPSSVarDTO var = new SPSSVarDTO();
                        var.setPosition(i + 1);
                        if (s.startsWith("\uFEFF")) {
                            s = s.substring(1);
                            nextLine[i] = s;
                        }
                        if (s.isEmpty()) {
                            s = "VAR_" + varCount++;
                            warnings.add(messageSource.getMessage("warning.var.name.empty", new Object[]{(i + 1), s}, LocaleContextHolder.getLocale()));
                        }
                        if (!store.add(s.toUpperCase())) {
                            int varNum = 1;
                            String changedName = s + "_" + varNum;
                            while (!store.add(changedName.toUpperCase())) {
                                changedName = s + "_" + varNum++;
                            }
                            warnings.add(messageSource.getMessage("warning.var.name.doublette", new Object[]{s, (i + 1), changedName}, LocaleContextHolder.getLocale()));
                            s = changedName;
                        }
                        var.setName(s);
                        var.setMissingFormat(SPSSMissing.SPSS_NO_MISSVAL);
                        var.setAligment(SPSSAligment.SPSS_ALIGN_LEFT);
                        var.setMeasureLevel(SPSSMeasLevel.SPSS_MLVL_UNK);
                        var.setRole(SPSSRoleCodes.SPSS_ROLE_INPUT);
                        vars.add(var);
                        i++;
                    }
                    importMatrix.add(nextLine);
                    lineNumber++;
                }
                while ((nextLine = reader.readNext()) != null) {
                    if (init) {
                        if (!sForm.isHeaderRow())
                            numOfCol = nextLine.length;
                        for (int i = 0; i < numOfCol; i++) {
                            types.add(SPSSVarTypes.SPSS_FMT_A);
                            dateFormatList.add("");
                        }
                        init = false;
                    }
                    if (nextLine.length != numOfCol) {
                        log.debug("CSV File corrupt - Number of Cols incorrect in [line: {}; current colums: {}, 1st row colums: {}", lineNumber, nextLine.length,
                                numOfCol);
                        errors.add(
                                messageSource.getMessage("error.upload.csv.corrupt", new Object[]{lineNumber, numOfCol, nextLine.length}, LocaleContextHolder.getLocale()));
                        error = true;
                        break;
                    }
                    importMatrix.add(nextLine);
                    List<Object> vector = new ArrayList<>();
                    int column = 0;
                    for (String s : nextLine) {
                        s = s.trim();
                        if (s.startsWith("\uFEFF")) {
                            s = s.substring(1);
                        }
                        String dateFormat;
                        if ((sForm.isHeaderRow() && lineNumber > 2) || (!sForm.isHeaderRow() && lineNumber > 1)) {
                            // Check Values of the following data matrix values
                            if (types.get(column).equals(SPSSVarTypes.SPSS_FMT_F) || types.get(column).equals(SPSSVarTypes.SPSS_UNKNOWN)) {
                                // Check number and unknown columns
                                BigDecimal val = null;
                                if (!s.isEmpty() && (val = parseDouble(s, false)) == null) {
                                    // current type is numeric, but a col was found which could not parsed to number -> set type to
                                    // alphanumeric
                                    types.set(column, SPSSVarTypes.SPSS_FMT_A);
                                } else if (types.get(column).equals(SPSSVarTypes.SPSS_UNKNOWN) && val != null) {
                                    // if type is still UNKNOWN (empty string) but now a number was found, set col to number
                                    types.set(column, SPSSVarTypes.SPSS_FMT_F);
                                }
                            } // Check date colums - if existing pattern doesn't match anymore,
                            else if (types.get(column).equals(SPSSVarTypes.SPSS_FMT_DATE) || types.get(column).equals(SPSSVarTypes.SPSS_FMT_TIME)
                                    || types.get(column).equals(SPSSVarTypes.SPSS_FMT_DATE_TIME)) {
                                dateFormat = DateUtil.determineDateFormat(s);
                                if (!s.isEmpty() && (dateFormat == null || !dateFormat.equals(dateFormatList.get(column)))) {
                                    types.set(column, SPSSVarTypes.SPSS_FMT_A);
                                }
                            }
                        } else {
                            // Check values in first line of the data matrix
                            identifyRowType(dateFormatList, types, column, s);
                        }
                        vector.add(s);
                        column++;
                    }
                    matrix.add(vector);
                    lineNumber++;
                }
                if (!sForm.isHeaderRow()) {
                    for (int i = 1; i <= numOfCol; i++) {
                        SPSSVarDTO var = new SPSSVarDTO();
                        var.setName("var" + i);
                        var.setPosition(i);
                        var.setMissingFormat(SPSSMissing.SPSS_NO_MISSVAL);
                        var.setAligment(SPSSAligment.SPSS_ALIGN_LEFT);
                        var.setMeasureLevel(SPSSMeasLevel.SPSS_MLVL_UNK);
                        var.setRole(SPSSRoleCodes.SPSS_ROLE_INPUT);
                        vars.add(var);
                    }
                }
                RecordDTO record = new RecordDTO();
                if (!error) {
                    file.setFilePath(null);
                    parseResult(numOfCol, matrix, vars, types, dateFormatList, record);
                    record.setNumberOfVariables(vars.size());
                    record.setNumberOfCases(matrix.size());
                    record.setEstimatedNofCases(matrix.size());
                    record.setId(recordId);
                    record.setChangedBy(user.getEmail());
                    record.setChangeLog(sForm.getNewChangeLog());
                    record.setFileEncoding("UTF-8");
                    record.setFileCodePage(65001);
                    record.setDateInfo(0);
                    record.setDateNumOfElements(0);
                    record.setVariables(vars);
                    record.setDataMatrix(matrix);
                    record.setDataMatrixJson(new Gson().toJson(record.getDataMatrix()));
                    record.setFileName(sForm.getCsvFile().getOriginalFilename());
                    sForm.setRecord(record);
                    sForm.setImportMatrix(importMatrix);
                    sForm.setFile(file);
                }
            } catch (IOException e) {
                error = true;
                log.warn("Error reading CSV File with input: [uploadedFile name: {}; size: {}; contentType: {}]",
                        () -> (sForm.getCsvFile() == null) ? "null" : sForm.getCsvFile().getOriginalFilename(),
                        () -> (sForm.getCsvFile() == null) ? "null" : sForm.getCsvFile().getSize(),
                        () -> (sForm.getCsvFile() == null) ? "null" : sForm.getCsvFile().getContentType(), () -> e);
                errors.add(messageSource.getMessage("error.upload.internal.error", null, LocaleContextHolder.getLocale()));
            }
        }
        return error;
    }

    /**
     * Compares a passed variable with the variables of the version that is saved in the DBS
     * Different states can be achieved: <br>
     * - Variable is equal (all values) <br>
     * - Name has changed - type is equal <br>
     * - Type has changed - name is equal <br>
     * - variable has been moved to another position <br>
     * - new variable if name and type do not match any variables from the previous version
     *
     * @param vars             {@link List} of {@link SPSSVarDTO} from the previous version
     * @param position         List position of the actual variable which has to be compared
     * @param curr             {@link SPSSVarDTO} variable which has to be compared
     * @param selectedFileType {@link String} FileType of the uploaded file - SPSS or CSV are currently supported
     * @return {@link RecordCompareDTO} Compare Results
     */
    private RecordCompareDTO compareVariable(final List<SPSSVarDTO> vars, final int position, final SPSSVarDTO curr, final String selectedFileType) {
        SPSSVarDTO savedVar;
        RecordCompareDTO comp = (RecordCompareDTO) applicationContext.getBean("RecordCompareDTO");
        if ((position) < vars.size()) {
            savedVar = vars.get(position);
        } else {
            savedVar = new SPSSVarDTO();
        }
        long savedId = savedVar.getId();
        savedVar.setId(0);
        long id = curr.getId();
        curr.setId(0);
        if (savedVar.getLabel() == null)
            savedVar.setLabel("");
        if (curr.getLabel() == null)
            curr.setLabel("");
        if (!curr.equals(savedVar)) {
            // var not equal -
            if (curr.getType().equals(savedVar.getType()) && curr.getName().equals(savedVar.getName())) {
                // Same TYPE AND NAME -> save new VAR with old documentation, constructs and new verion_id
                if (selectedFileType.equals("CSV") && curr.getDecimals() == savedVar.getDecimals() && curr.getWidth() == savedVar.getWidth()) {
                    comp.setVarStatus(VariableStatus.EQUAL_CSV);
                    comp.setEqualVarId(savedId);
                    comp.setKeepExpMeta(true);
                    comp.setBootstrapItemColor("success");
                } else {
                    comp.setVarStatus(VariableStatus.META_CHANGED);
                    comp.setKeepExpMeta(true);
                    comp.setEqualVarId(savedId);
                    comp.setBootstrapItemColor("success");
                }
            } else {
                boolean found;
                // name equal type changed
                if (curr.getName().equals(savedVar.getName())) {
                    comp.setVarStatus(VariableStatus.TYPE_CHANGED);
                    comp.setKeepExpMeta(true);
                    comp.setEqualVarId(savedId);
                    comp.setBootstrapItemColor("warning");
                    found = true;
                } else {
                    // check if var has moved!
                    found = findVarInList(vars, curr, comp, selectedFileType);
                }
                // maybe renamed - type is equal and name not found in list of variables
                if (!found && curr.getType().equals(savedVar.getType())
                    // && curr.getWidth() == savedVar.getWidth() && curr.getDecimals() == savedVar.getDecimals()
                ) {
                    comp.setVarStatus(VariableStatus.NAME_CHANGED);
                    comp.setKeepExpMeta(true);
                    comp.setEqualVarId(savedId);
                    comp.setBootstrapItemColor("danger");
                } else if (!found) {
                    comp.setVarStatus(VariableStatus.NEW_VAR);
                    comp.setBootstrapItemColor("warning");
                }
            }
        } else {
            comp.setVarStatus(VariableStatus.EQUAL);
            comp.setEqualVarId(savedId);
            comp.setKeepExpMeta(true);
            comp.setBootstrapItemColor("success");
        }
        savedVar.setId(savedId);
        curr.setId(id);
        return comp;
    }

    /**
     * Tries to find the passed variable in the list of variables of the previous version.
     * This function is called when a variable wasn't found on its previous position
     * <p>
     * Five states can be achieved:
     * <ul>
     * <li>not found (return false)</li>
     * <li>found (return true)</li>
     * <li>
     * <ul>
     * <li>var was moved (VariableStatus.MOVED)</li>
     * <li>var was moved and fileType is CSV (VariableStatus.MOVED_CSV)</li>
     * <li>var was moved and type was changed (VariableStatus.MOVED_AND_TYPE_CHANGED)</li>
     * <li>var was moved and type is equal but other meta information was changed (VariableStatus.MOVED_AND_META_CHANGED)</li>
     * </ul>
     * </li>
     * </ul>
     *
     * @param varList          {@link List} of {@link SPSSVarDTO} from the previous version
     * @param var              {@link SPSSVarDTO} variable which has to be found
     * @param comp             {@link RecordCompareDTO} Compare Results
     * @param selectedFileType {@link String} FileType of the uploaded file - SPSS or CSV are currently supported
     * @return true if found, otherwise false
     */
    private boolean findVarInList(final List<SPSSVarDTO> varList, final SPSSVarDTO var, final RecordCompareDTO comp, final String selectedFileType) {
        boolean found = false;
        for (SPSSVarDTO savedtmp : varList) {
            int lastPos = savedtmp.getPosition();
            int newPos = var.getPosition();
            long id = savedtmp.getId();
            savedtmp.setId(0);
            savedtmp.setPosition(0);
            var.setPosition(0);
            if (savedtmp.equals(var)) {
                comp.setVarStatus(VariableStatus.MOVED);
                comp.setMovedFrom(lastPos);
                comp.setMovedTo(newPos);
                comp.setKeepExpMeta(true);
                comp.setEqualVarId(id);
                comp.setBootstrapItemColor("info");
                found = true;
            } else if (selectedFileType.equals("CSV") && var.getType().equals(savedtmp.getType()) && var.getName().equals(savedtmp.getName())
                    && var.getDecimals() == savedtmp.getDecimals() && var.getWidth() == savedtmp.getWidth()) {
                comp.setVarStatus(VariableStatus.MOVED_CSV);
                comp.setMovedFrom(lastPos);
                comp.setMovedTo(newPos);
                comp.setKeepExpMeta(true);
                comp.setEqualVarId(id);
                comp.setBootstrapItemColor("info");
                found = true;
            } else if (var.getName().equals(savedtmp.getName())) {
                if (!var.getType().equals(savedtmp.getType())) {
                    comp.setVarStatus(VariableStatus.MOVED_AND_TYPE_CHANGED);
                    comp.setMovedFrom(lastPos);
                    comp.setMovedTo(newPos);
                    comp.setKeepExpMeta(true);
                    comp.setEqualVarId(id);
                    comp.setBootstrapItemColor("warning");
                } else {
                    comp.setVarStatus(VariableStatus.MOVED_AND_META_CHANGED);
                    comp.setMovedFrom(lastPos);
                    comp.setMovedTo(newPos);
                    comp.setKeepExpMeta(true);
                    comp.setEqualVarId(id);
                    comp.setBootstrapItemColor("info");
                }
                found = true;
            }
            savedtmp.setId(id);
            var.setPosition(newPos);
            savedtmp.setPosition(lastPos);
            if (found) {
                break;
            }
        }
        return found;
    }

    /**
     * Pareses the data matrix values to String, Number or to the default Date-Time, depending on the variable type.
     *
     * @param numOfCol       Amount of columns
     * @param matrix         ({@link List} of {@link List}) of {@link Objects} the data matrix
     * @param vars           {@link List} of {@link SPSSVarDTO} the variables
     * @param types          {@link List} of {@link SPSSVarTypes} List of variable types that were determined automatically in the calling function
     * @param dateFormatList {@link List} of {@link String} List of date types that were determined automatically in the calling function
     * @param record         {@link RecordDTO}
     */
    private void parseResult(final int numOfCol, final List<List<Object>> matrix, final List<SPSSVarDTO> vars, final List<SPSSVarTypes> types,
                             final List<String> dateFormatList, final RecordDTO record) {
        int caseSize = 0;
        for (int i = 0; i < numOfCol; i++) {
            DateTimeFormatter formatter = null;
            int width = 0, dec = 0;
            SPSSVarDTO var = vars.get(i);
            SPSSVarTypes type = types.get(i);
            String dateFormat = dateFormatList.get(i).trim();
            // SET UNKNOWN TYPES TO ALPHANUMERIC
            if (type.equals(SPSSVarTypes.SPSS_UNKNOWN)) {
                type = SPSSVarTypes.SPSS_FMT_A;
                types.set(i, type);
            }
            for (List<Object> row : matrix) {
                String value = String.valueOf(row.get(i)).trim();
                if (!dateFormat.isEmpty())
                    formatter = DateTimeFormatter.ofPattern(dateFormat);
                int curW = 0;
                switch (type) {
                    case SPSS_FMT_DATE:
                        if (formatter != null && !value.isEmpty())
                            row.set(i, LocalDate.parse(value, formatter).format(DateTimeFormatter.ofPattern("MM/dd/yyyy")));
                        else
                            row.set(i, "");
                        break;
                    case SPSS_FMT_TIME:
                        if (formatter != null && !value.isEmpty()) {
                            LocalTime time = LocalTime.parse(value, formatter);
                            curW = (time.getNano() != 0) ? 11 : (time.getSecond() != 0) ? 8 : 5;
                            width = (width < curW ? curW : width);
                            row.set(i, time.format(DateTimeFormatter.ofPattern("HH:mm:ss.SSS")));
                        } else
                            row.set(i, "");
                        break;
                    case SPSS_FMT_DATE_TIME:
                        if (formatter != null && !value.isEmpty()) {
                            LocalDateTime dateTime = LocalDateTime.parse(value, formatter);
                            curW = (dateTime.getNano() != 0) ? 23 : (dateTime.getSecond() != 0) ? 20 : 17;
                            width = (width < curW ? curW : width);
                            if (curW == 23)
                                row.set(i, dateTime.format(DateTimeFormatter.ofPattern("M/d/yyyy HH:mm:ss.SS")));
                            else if (curW == 20)
                                row.set(i, dateTime.format(DateTimeFormatter.ofPattern("M/d/yyyy HH:mm:ss")));
                            else
                                row.set(i, dateTime.format(DateTimeFormatter.ofPattern("M/d/yyyy HH:mm")));
                        } else
                            row.set(i, "");
                        break;
                    case SPSS_FMT_F:
                        BigDecimal d = parseDouble(value, true);
                        if (d != null) {
                            int decT = d.stripTrailingZeros().scale();
                            if (decT <= 0) {
                                row.set(i, d.longValue());
                                curW = String.valueOf(d.longValue()).length();
                            } else {
                                row.set(i, d.doubleValue());
                                curW = String.valueOf(d.doubleValue()).length();
                            }
                            if (decT > dec) {
                                dec = decT;
                            }
                        } else {
                            row.set(i, "");
                        }
                        if (curW > width) {
                            width = curW;
                        }
                        break;
                    case SPSS_FMT_A:
                        if (value.getBytes(Charset.forName("UTF-8")).length > width) {
                            width = value.getBytes(Charset.forName("UTF-8")).length;
                        }
                        break;
                    default:
                        break;
                }
            }
            if (width < 8)
                width = 8;
            if (type.equals(SPSSVarTypes.SPSS_FMT_A)) {
                caseSize += width;
                var.setVarType(width);
                var.setDecimals(0);
            } else if (type.equals(SPSSVarTypes.SPSS_FMT_DATE)) {
                caseSize += 8;
                width = 11;
            } else if (type.equals(SPSSVarTypes.SPSS_FMT_TIME) || type.equals(SPSSVarTypes.SPSS_FMT_DATE_TIME)) {
                caseSize += 8;
                var.setDecimals(dateFormat.contains(".S") ? 2 : 0);
            } else {
                caseSize += 8;
                var.setVarType(0);
                var.setDecimals(dec);
            }
            record.setCaseSize(caseSize);
            var.setType(type);
            var.setWidth(width);
            var.setColumns(width);
            vars.set(i, var);
        }
    }

    /**
     * The validation of a SPSS file is not absolutely necessary, because it is assumed that the files are already validly written by SPSS.
     * This function reads only the file and writes the values into the RecordDTO and the FileDTO (for saving in the Minio system) object of StudyForm.
     * Therefore, the validation is limited to possible import errors.
     *
     * @param pid      Project identifier as long
     * @param studyId  Study identifier as long
     * @param recordId Record identifier as long
     * @param sForm    {@link StudyForm} The record is saved into this form after "validation"
     * @param user     {@link UserDTO} contains information about the user who uploaded this file.
     * @param errors   {@link List} of {@link String} The errors that can occur during the import are saved here.
     * @return false if valid, true if not (TODO should be changed )
     */
    private boolean validateSPSSFile(final long pid, final long studyId, final long recordId, final StudyForm sForm, final UserDTO user,
                                     final List<String> errors) {
        FileDTO file = null;
        boolean error = false;
        try {
            file = fileUtil.buildFileDTO(pid, studyId, recordId, 0, user.getId(), sForm.getSpssFile());
        } catch (Exception e) {
            error = true;
            log.error("Error creating FileDTO with input: [pid: {}; studyid: {}; recordid: {}; userid: {}; uploadedFile: {}]", () -> pid, () -> studyId,
                    () -> recordId, () -> 0, user::getId, () -> (sForm.getSpssFile() == null) ? "null" : sForm.getSpssFile().getOriginalFilename(), () -> e);
            errors.add(messageSource.getMessage("error.upload.creating.file", null, LocaleContextHolder.getLocale()));
        }
        // SAVE TMP FILE!!!
        String path = fileUtil.saveFile(file, true);
        // SPSS API TEST TODO
        //RecordDTO test = spssIoService.getJSONFromSPSS(path);
        // log.warn(test.getCaseSize());
        // SPSS API TEST TODO
        RecordDTO spssFile = null;
        if (path != null && !error) {
            SPSSFileDTO spssTMP;
            if ((spssTMP = spss.readWholeSPSSFile(path)) != null) {
                spssFile = new RecordDTO(spssTMP);
            } else {
                log.error("ERROR: Reading SPSS file wasn't successful");
                errors.add(messageSource.getMessage("error.upload.spss.file", null, LocaleContextHolder.getLocale()));
                error = true;
            }
            fileUtil.deleteFile(Paths.get(path));
            if (!error && file == null) {
                log.error("ERROR: FileDTO or RecordDTO empty: [FileDTO: {}; RecordDTO: {}]", "null",
                        "not null");
                error = true;
                errors.add(messageSource.getMessage("error.upload.internal.error", null, LocaleContextHolder.getLocale()));
            }
            if (!error) {
                file.setFilePath(null);
                spssFile.setId(recordId);
                spssFile.setChangedBy(user.getEmail());
                spssFile.setChangeLog(sForm.getNewChangeLog());
                spssFile.setFileName(sForm.getSpssFile().getOriginalFilename());
                sForm.setRecord(spssFile);
                sForm.setFile(file);
            }
        } else {
            log.warn("Error: Not FilePath was set - Temporary File was not saved to local Filesystem");
            errors.add(messageSource.getMessage("error.upload.internal.error", null, LocaleContextHolder.getLocale()));
            error = true;
        }
        return error;
    }

    /**
     * Filters the DataWiz attributes from the SPSS attribute list and saves them into the dw_attributes list.
     * This function is also used for validation of the database variables. They should not contain DataWiz attributes at this point.
     * Therefore, the regex matcher and the boolean result was included.
     *
     * @param var {@link SPSSVarDTO} contains the data of a variable
     * @return True if matcher found a specific dw_attribute that is not allowed at this point!
     */
    boolean sortVariableAttributes(final SPSSVarDTO var) {
        boolean dw_construct = false, dw_measocc = false, dw_instrument = false, dw_itemtext = false, dw_filtervar = false;
        Iterator<SPSSValueLabelDTO> itt = var.getAttributes().iterator();
        List<SPSSValueLabelDTO> dw_attr = new ArrayList<>();
        boolean ret = false;
        while (itt.hasNext()) {
            SPSSValueLabelDTO att = itt.next();
            if (Pattern.compile(RegexUtil.VAR_ARRAY_ATTR).matcher(att.getLabel()).find()) {
                ret = true;
                itt.remove();
            } else if (att.getLabel().equals("dw_construct")) {
                dw_attr.add(att);
                dw_construct = true;
                itt.remove();
            } else if (att.getLabel().equals("dw_measocc")) {
                dw_attr.add(att);
                dw_measocc = true;
                itt.remove();
            } else if (att.getLabel().equals("dw_instrument")) {
                dw_attr.add(att);
                dw_instrument = true;
                itt.remove();
            } else if (att.getLabel().equals("dw_itemtext")) {
                dw_attr.add(att);
                dw_itemtext = true;
                itt.remove();
            } else if (att.getLabel().equals("dw_filtervar")) {
                if (!att.getValue().equals("0") && !att.getValue().equals("1")) {
                    att.setValue("");
                    ret = true;
                }
                dw_attr.add(att);
                dw_filtervar = true;
                itt.remove();
            }
        }
        if (!dw_construct) {
            dw_attr.add(new SPSSValueLabelDTO("dw_construct", ""));
        }
        if (!dw_measocc) {
            dw_attr.add(new SPSSValueLabelDTO("dw_measocc", ""));
        }
        if (!dw_instrument) {
            dw_attr.add(new SPSSValueLabelDTO("dw_instrument", ""));
        }
        if (!dw_itemtext) {
            dw_attr.add(new SPSSValueLabelDTO("dw_itemtext", ""));
        }
        if (!dw_filtervar) {
            dw_attr.add(new SPSSValueLabelDTO("dw_filtervar", ""));
        }
        var.setDw_attributes(dw_attr);
        return ret;
    }

    /**
     * This function tries to parse the values to numbers. It checks if the string contains untypical chars, or if the decimal separator is a comma instead of a
     * dot. This functions is also used for checking purposes, therefore an Exception is only thrown during parsing and not during checking procedure.
     *
     * @param s     with the value which has to be checked or parsed
     * @param parse if parsing (true) or check(false) procedure
     * @return Returns a BigDecimal on success, otherwise NULL
     */
    private BigDecimal parseDouble(final String s, boolean parse) {
        String val = s.trim();
        if (val.contains("/") || val.contains(":")) {
            return null;
        }
        BigDecimal ret = null;
        if (!val.isEmpty()) {
            try {
                if (StringUtils.countMatches(val, ",") == 1 && StringUtils.countMatches(val, ".") == 0)
                    val = val.replace(',', '.');
                ret = new BigDecimal(val);
            } catch (Exception e) {
                if (parse) {
                    log.error("ERROR: Parsing not successful - Number was checked before parsing, but something went wrong: Number: {} ", val, e);
                }
            }
        }
        return ret;
    }

    /**
     * This function is used to identify all row types of a CSV File. It differences between Numbers, String and Date formats.
     * It is called to check the first row of a record matrix. If the type changed during validation of the rest of the matrix, the type will be rearranged by another function.
     *
     * @param dateFormatList {@link List} of {@link String} which contains the date format of date rows.
     *                       This length of the list depends on the number of columns in the imported file and only entries are set for columns of type Date.
     * @param types          {@link List} of {@link SPSSVarTypes} The length of the list depends on the number of columns in the imported file
     *                       and contains the evaluated type for each column.
     * @param column         Count of the current column as int
     * @param s              {@link String} Value of the first entree from the current column
     */
    private void identifyRowType(final List<String> dateFormatList, final List<SPSSVarTypes> types, final int column, final String s) {
        String dateFormat;
        if (s.trim().isEmpty()) {
            // if string is emtpy set unkown
            types.set(column, SPSSVarTypes.SPSS_UNKNOWN);
        } else if (parseDouble(s, false) != null) {
            // if number was found set type to numeric
            types.set(column, SPSSVarTypes.SPSS_FMT_F);
        } else if ((dateFormat = DateUtil.determineDateFormat(s)) != null) {
            // if date was found set type to date
            dateFormatList.set(column, dateFormat);
            switch (dateFormat) {
                case "M/d/yyyy":
                case "M/d/yy":
                case "d.M.yyyy":
                case "d.M.yy":
                case "d-M-yyyy":
                case "d-M-yy":
                case "yyyy-M-d":
                    types.set(column, SPSSVarTypes.SPSS_FMT_DATE);
                    break;
                case "H:m":
                case "H:m:s":
                case "H:m:s.S":
                case "H:m:s.SS":
                case "H:m:s.SSS":
                    types.set(column, SPSSVarTypes.SPSS_FMT_TIME);
                    break;
                default:
                    types.set(column, SPSSVarTypes.SPSS_FMT_DATE_TIME);
                    break;
            }
        } else {
            types.set(column, SPSSVarTypes.SPSS_FMT_A);
        }
    }

    /**
     * Compares all new Variables with the variables from the Database and save the result into the StudyForm
     *
     * @param sForm {@link StudyForm} contains all new Variables and previous Variables
     * @throws Exception IOExceptions thrown by the deep copy function
     */
    private void compareVarVersion(StudyForm sForm) throws Exception {
        List<SPSSVarDTO> vars = new ArrayList<>();
        for (SPSSVarDTO tmp : sForm.getPreviousRecordVersion().getVariables()) {
            vars.add((SPSSVarDTO) ObjectCloner.deepCopy(tmp));
        }
        int listDiff = vars.size() - sForm.getRecord().getVariables().size();
        if (vars.size() == 0) {
            sForm.getWarnings().add(messageSource.getMessage("record.import.variable.amount.new", null, LocaleContextHolder.getLocale()));
        } else if (listDiff < 0) {
            sForm.getWarnings().add(messageSource.getMessage("record.import.variable.amount.added", null, LocaleContextHolder.getLocale()));
        } else if (listDiff > 0) {
            sForm.getWarnings().add(messageSource.getMessage("record.import.variable.amount.delete", null, LocaleContextHolder.getLocale()));
        }
        int position = 0;
        List<RecordCompareDTO> compList = new ArrayList<>();
        for (SPSSVarDTO curr : sForm.getRecord().getVariables()) {
            curr.setVarHandle(0.0);
            RecordCompareDTO comp = compareVariable(vars, position, curr, sForm.getSelectedFileType());
            comp.setMessage(messageSource.getMessage("import.check." + comp.getVarStatus().name(), new Object[]{comp.getMovedFrom(), comp.getMovedTo()},
                    LocaleContextHolder.getLocale()));
            compList.add(comp);
            position++;
        }
        position = 0;
        List<RecordCompareDTO> compList2 = new ArrayList<>();
        for (SPSSVarDTO curr : vars) {
            compList2.add(compareVariable(sForm.getRecord().getVariables(), position, curr, sForm.getSelectedFileType()));
            position++;
        }
        position = 0;
        List<SPSSVarDTO> delVars = new ArrayList<>();
        int delcount = 0;
        for (RecordCompareDTO comp : compList2) {
            if (comp.getVarStatus().equals(VariableStatus.NEW_VAR) && compList.size() > position
                    && !compList.get(position).getVarStatus().equals(VariableStatus.NEW_VAR)) {
                SPSSVarDTO del = vars.get(position - delcount++);
                delVars.add(del);
                vars.remove(del);
            }
            position++;
        }
        sForm.setDelVars(delVars);
        AtomicInteger varSize = new AtomicInteger(compList.size());
        compList.forEach(s -> {
            int index = varSize.get();
            varSize.set(s.getMovedTo() > index ? s.getMovedTo() : index);
        });
        List<SPSSVarDTO> viewVars = Stream.generate(SPSSVarDTO::new).limit(varSize.get()).collect(Collectors.toList());
        AtomicInteger atint = new AtomicInteger(0);
        vars.forEach(s -> {
            if (atint.get() < viewVars.size())
                viewVars.set(atint.get(), s);
            atint.incrementAndGet();
        });
        for (RecordCompareDTO rc : compList) {
            if (rc.getVarStatus().equals(VariableStatus.MOVED) || rc.getVarStatus().equals(VariableStatus.MOVED_AND_META_CHANGED)
                    || rc.getVarStatus().equals(VariableStatus.MOVED_AND_META_CHANGED_CSV) || rc.getVarStatus().equals(VariableStatus.MOVED_AND_TYPE_CHANGED)
                    || rc.getVarStatus().equals(VariableStatus.MOVED_CSV)) {
                SPSSVarDTO moved = sForm.getPreviousRecordVersion().getVariables().get((rc.getMovedFrom() - 1));
                if (compList.stream().noneMatch(obj -> rc.getMovedFrom() == obj.getMovedTo())) {
                    if (viewVars.size() > rc.getMovedFrom() - 1) {
                        viewVars.set((rc.getMovedFrom() - 1), new SPSSVarDTO());
                        compList.get((rc.getMovedFrom() - 1)).setKeepExpMeta(false);
                        compList.get((rc.getMovedFrom() - 1)).setVarStatus(VariableStatus.NEW_VAR);
                        compList.get((rc.getMovedFrom() - 1)).setBootstrapItemColor("warning");
                        compList.get((rc.getMovedFrom() - 1))
                                .setMessage(messageSource.getMessage("import.check." + VariableStatus.NEW_VAR.name(), null, LocaleContextHolder.getLocale()));
                    }
                }
                viewVars.set((rc.getMovedTo() - 1), moved);
            }
        }
        sForm.setCompList(compList);
        sForm.setViewVars(viewVars);
    }

}
