package de.zpid.datawiz.controller;

import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.gson.Gson;
import com.opencsv.CSVReader;

import de.zpid.datawiz.dto.FileDTO;
import de.zpid.datawiz.dto.RecordDTO;
import de.zpid.datawiz.dto.UserDTO;
import de.zpid.datawiz.enumeration.MinioResult;
import de.zpid.datawiz.enumeration.PageState;
import de.zpid.datawiz.form.StudyForm;
import de.zpid.datawiz.util.BreadCrumpUtil;
import de.zpid.datawiz.util.DateUtil;
import de.zpid.datawiz.util.UserUtil;
import de.zpid.spss.SPSSIO;
import de.zpid.spss.dto.SPSSVarTDO;
import de.zpid.spss.util.SPSSVarTypes;

@Controller
@RequestMapping(value = { "/record", "/project/{pid}/study/{studyId}/record" })
@SessionAttributes({ "StudyForm", "subnaviActive", "breadcrumpList" })
public class RecordController extends SuperController {

  private static Logger log = LogManager.getLogger(RecordController.class);

  @Autowired
  private SPSSIO spss;
  @Autowired
  private PlatformTransactionManager txManager;

  public RecordController() {
    super();
    log.info("Loading RecordController for mapping /project/{pid}/study/{sid}/record");
  }

  @RequestMapping(value = { "", "/{recordId}" }, method = RequestMethod.GET)
  public String showRecord(@PathVariable final Optional<Long> pid, @PathVariable final Optional<Long> studyId,
      @PathVariable final Optional<Long> recordId, final ModelMap model, final RedirectAttributes redirectAttributes) {
    final UserDTO user = UserUtil.getCurrentUser();
    String ret;
    if (recordId.isPresent()) {
      log.trace("Entering showRecord(edit) for [recordId: {}; studyId {}; projectId {}]", () -> recordId.get(),
          () -> studyId.get(), () -> pid.get());
      ret = checkStudyAccess(pid, studyId, redirectAttributes, false, user);
    } else {
      log.trace("Entering showRecord(create) ");
      ret = checkStudyAccess(pid, studyId, redirectAttributes, true, user);
    }
    if (ret != null)
      return ret;
    String accessState = "disabled";
    StudyForm sForm = createStudyForm();
    if (recordId.isPresent()) {
      try {
        sForm.setProject(projectDAO.findById(pid.get()));
        if (sForm.getProject() == null) {
          log.warn("No Project found for projectId {}", () -> pid.get());
          redirectAttributes.addFlashAttribute("errorMSG",
              messageSource.getMessage("project.not.available", null, LocaleContextHolder.getLocale()));
          return "redirect:/panel";
        }
        sForm.setStudy(studyDAO.findById(studyId.get(), pid.get(), true, false));
        if (sForm.getStudy() == null) {
          log.warn("No Study found for studyId {}", () -> studyId.get());
          redirectAttributes.addFlashAttribute("errorMSG",
              messageSource.getMessage("record.not.available", null, LocaleContextHolder.getLocale()));
          return "redirect:/project/" + pid.get() + "/studies";
        }
        List<RecordDTO> rList = new ArrayList<RecordDTO>();
        rList.add(recordDAO.findRecordWithID(recordId.get()));
        sForm.setRecords(rList);
      } catch (Exception e) {
        // TODO: handle exception
      }
    }
    model.put("breadcrumpList",
        BreadCrumpUtil.generateBC(PageState.RECORDS,
            new String[] { sForm.getProject().getTitle(), sForm.getStudy().getTitle(),
                (sForm.getRecords() != null && sForm.getRecords().get(0) != null
                    ? sForm.getRecords().get(0).getRecordName() : "TEST") },
            new long[] { pid.get(), studyId.get() }, messageSource));
    model.put("StudyForm", sForm);
    model.put("recordSubMenu", true);
    model.put("subnaviActive", PageState.RECORDMETA.name());
    log.trace("Method showRecord successfully completed");
    return "record";
  }

  @RequestMapping(value = { "/{recordId}" }, method = RequestMethod.POST, params = { "upload" })
  public String uploadFile(@PathVariable final Optional<Long> pid, @PathVariable final Optional<Long> studyId,
      @PathVariable final Optional<Long> recordId, final ModelMap model, final RedirectAttributes redirectAttributes,
      @ModelAttribute("StudyForm") StudyForm sForm) {
    final UserDTO user = UserUtil.getCurrentUser();
    log.trace("Entering  uploadFile for [recordId: {}; studyId {}; projectId {}] user[id: {}; email: {}]",
        () -> recordId.get(), () -> studyId.get(), () -> pid.get(), () -> user.getId(), () -> user.getEmail());
    String ret = checkStudyAccess(pid, studyId, redirectAttributes, true, user);
    if (ret != null)
      return ret;
    if (sForm.getNewChangeLog() == null || sForm.getNewChangeLog().isEmpty()) {
      log.debug("New Changelog is Missing - return to jsp with message");
      redirectAttributes.addFlashAttribute("errorMSG",
          messageSource.getMessage("record.no.changelog", null, LocaleContextHolder.getLocale()));
      return "redirect:/project/" + pid.get() + "/study/" + studyId.get() + "/record/" + recordId.get();
    }
    // SPSS FILE
    List<String> warnings = new ArrayList<>();
    List<String> errors = new ArrayList<>();
    boolean error = false;
    if (sForm.getSelectedFileType() != null && sForm.getSelectedFileType().equals("SPSS") && sForm.getSpssFile() != null
        && sForm.getSpssFile().getSize() > 0) {
      error = validateSPSSFile(pid, studyId, recordId, sForm, user, errors);
    } // CSV File
    else if (sForm.getSelectedFileType() != null && sForm.getSelectedFileType().equals("CSV")
        && sForm.getCsvFile() != null && sForm.getCsvFile().getSize() > 0) {
      validateCSVFile(pid, studyId, recordId, sForm, user, warnings, errors);
    } else {

    }
    sForm.setParsingError(error);
    sForm.setErrors(errors);
    sForm.setWarnings(warnings);
    return "redirect:/project/" + pid.get() + "/study/" + studyId.get() + "/record/" + recordId.get() + "/importReport";
  }

  /**
   * @param recordId
   * @param sForm
   * @param user
   * @param warnings
   * @param errors
   * @param error
   */
  private void validateCSVFile(final Optional<Long> pid, final Optional<Long> studyId, final Optional<Long> recordId,
      StudyForm sForm, final UserDTO user, List<String> warnings, List<String> errors) {
    FileDTO file = null;
    boolean init = true, error = false;
    try {
      file = fileUtil.buildFileDTO(pid.get(), studyId.get(), recordId.get(), 0, user.getId(), sForm.getCsvFile());
    } catch (Exception e) {
      error = true;
      log.error("Error creating FileDTO with input: [pid: {}; studyid: {}; recordid: {}; userid: {}; uploadedFile: {}]",
          () -> pid.get(), () -> studyId.get(), () -> recordId.get(), () -> 0, () -> user.getId(),
          () -> (sForm.getCsvFile() == null) ? "null" : sForm.getCsvFile().getOriginalFilename(), () -> e);
      errors.add(messageSource.getMessage("error.upload.creating.file", null, LocaleContextHolder.getLocale()));
    }
    try (CSVReader reader = new CSVReader(new InputStreamReader(sForm.getCsvFile().getInputStream(), "UTF-8"),
        (sForm.getCsvSeperator() == 't' ? '\t' : sForm.getCsvSeperator()),
        (sForm.getCsvQuoteChar() == 'q' ? '\"' : '\''))) {
      String[] nextLine;
      int numOfCol = 0, lineNumber = 1;
      List<List<Object>> matrix = new ArrayList<>();
      List<SPSSVarTDO> vars = new ArrayList<>();
      List<String> dateFormatList = new ArrayList<>();
      List<SPSSVarTypes> types = new ArrayList<SPSSVarTypes>();
      List<String[]> importMatrix = new ArrayList<>();
      int varCount = 1;
      if (sForm.isHeaderRow() && (nextLine = reader.readNext()) != null) {
        numOfCol = nextLine.length;
        List<String> head = new ArrayList<>();
        Set<String> store = new HashSet<String>();
        int i = 0;
        for (String s : nextLine) {
          SPSSVarTDO var = new SPSSVarTDO();
          if (s.startsWith("\uFEFF")) {
            s = s.substring(1);
            nextLine[i] = s;
          }
          if (s.isEmpty()) {
            s = "VAR_" + varCount++;
            warnings.add(
                "Nicht gesetzter Variablenname erkannt in Spalte:\"" + (i + 1) + "\". Umbenannt in: \"" + s + "\"");
          }
          if (!store.add(s.toUpperCase())) {
            int varNum = 1;
            String changedName = s + "_" + varNum;
            while (!store.add(changedName.toUpperCase())) {
              changedName = s + "_" + varNum++;
            }
            warnings.add("Doppelter Variablenname erkannt bei Variable \"" + s + "\"(Spalte:" + (i + 1)
                + "). Umbenannt in: \"" + changedName + "\"");
            s = changedName;
            // TODO JSP WARNUNG Var umbenannt
          }
          var.setName(s);
          vars.add(var);
          head.add(s);
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
          log.debug("CSV File corrupt - Number of Cols incorrect in [line: {}; current colums: {}, 1st row colums: {}",
              lineNumber, nextLine.length, numOfCol);
          errors.add("CSV File corrupt - Number of Cols incorrect in [line: " + lineNumber + "; current colums: "
              + nextLine.length + ", 1st row colums: " + numOfCol);
          error = true;
          break;
        }
        importMatrix.add(nextLine);
        List<Object> vector = new ArrayList<Object>();
        int column = 0;
        for (String s : nextLine) {
          if (s.startsWith("\uFEFF")) {
            s = s.substring(1);
          }
          String dateFormat = null;
          if ((sForm.isHeaderRow() && lineNumber > 2) || (!sForm.isHeaderRow() && lineNumber > 1)) {
            // Check Values of the following data matrix values
            if (types.get(column).equals(SPSSVarTypes.SPSS_FMT_F)
                || types.get(column).equals(SPSSVarTypes.SPSS_UNKNOWN)) {
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
            else if (types.get(column).equals(SPSSVarTypes.SPSS_FMT_DATE)
                || types.get(column).equals(SPSSVarTypes.SPSS_FMT_TIME)
                || types.get(column).equals(SPSSVarTypes.SPSS_FMT_DATE_TIME)) {
              dateFormat = DateUtil.determineDateFormat(s);
              if (!s.isEmpty() && (dateFormat == null || !dateFormat.equals(dateFormatList.get(column)))) {
                types.set(column, SPSSVarTypes.SPSS_FMT_A);
              }
            } else if (types.get(column).equals(SPSSVarTypes.SPSS_UNKNOWN)) {
              identifyRowType(sForm, dateFormatList, types, column, s);
            }
          } else {
            // Check values in first line of the data matrix
            identifyRowType(sForm, dateFormatList, types, column, s);
          }
          vector.add(s);
          column++;
        }
        matrix.add(vector);
        lineNumber++;
      }
      if (!sForm.isHeaderRow()) {
        for (int i = 1; i <= numOfCol; i++) {
          SPSSVarTDO var = new SPSSVarTDO();
          var.setName("var" + i);
          vars.add(var);
        }
      }
      // TODO FINAL PARSE
      RecordDTO record = new RecordDTO();
      if (!error) {
        file.setFilePath(null);
        parseResult(sForm, numOfCol, matrix, vars, types, dateFormatList, record);
        record.setNumberOfVariables(vars.size());
        record.setNumberOfCases(matrix.size());
        record.setEstimatedNofCases(matrix.size());
        record.setId(recordId.get());
        record.setChangedBy(user.getEmail());
        record.setChangeLog(sForm.getNewChangeLog());
        record.setFileEncoding("UTF-8");
        record.setFileCodePage(65001);
        record.setDateInfo(0);
        record.setDateNumOfElements(0);
        record.setVariables(vars);
        record.setDataMatrix(matrix);
        record.setDataMatrixJson(new Gson().toJson(record.getDataMatrix()));
        sForm.setRecord(record);
        sForm.setImportMatrix(importMatrix);
        sForm.setFile(file);
        System.out.println(sForm.getRecord());
        saveRecordToDBAndMinio(sForm);
      }
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  @RequestMapping(value = { "/{recordId}/importReport" }, method = RequestMethod.GET)
  public String showImportReport(@PathVariable final Optional<Long> pid, @PathVariable final Optional<Long> studyId,
      @PathVariable final Optional<Long> recordId, final ModelMap model, final RedirectAttributes redirectAttributes,
      @ModelAttribute("StudyForm") StudyForm sForm) {

    return "importRep";
  }

  /**
   * @param sForm
   * @param numOfCol
   * @param matrix
   * @param vars
   * @param types
   */
  private void parseResult(StudyForm sForm, int numOfCol, List<List<Object>> matrix, List<SPSSVarTDO> vars,
      List<SPSSVarTypes> types, List<String> dateFormatList, RecordDTO record) {
    int caseSize = 0;
    for (int i = 0; i < numOfCol; i++) {
      DateTimeFormatter formatter = null;
      int width = 0, dec = 0;
      SPSSVarTDO var = vars.get(i);
      SPSSVarTypes type = types.get(i);
      String dateFormat = dateFormatList.get(i).trim();
      // SET UNKNOWN TYPES TO ALPHANUMERIC
      if (type.equals(SPSSVarTypes.SPSS_UNKNOWN)) {
        type = SPSSVarTypes.SPSS_FMT_A;
        types.set(i, type);
      }
      for (List<Object> row : matrix) {
        String value = String.valueOf(row.get(i)).trim();
        try {
          if (dateFormat != null && !dateFormat.isEmpty())
            formatter = DateTimeFormatter.ofPattern(dateFormat);
          int curW = 0;
          switch (type) {
          case SPSS_FMT_DATE:
            if (formatter != null)
              row.set(i, LocalDate.parse(value, formatter).format(DateTimeFormatter.ofPattern("MM/dd/yyyy")));
            break;
          case SPSS_FMT_TIME:
            if (formatter != null) {
              LocalTime time = LocalTime.parse(value, formatter);
              curW = (time.getNano() != 0) ? 11 : (time.getSecond() != 0) ? 8 : 5;
              width = (width < curW ? curW : width);
              row.set(i, time.format(DateTimeFormatter.ofPattern("HH:mm:ss.SSS")));
            }
            break;
          case SPSS_FMT_DATE_TIME:
            if (formatter != null) {
              LocalDateTime dateTime = LocalDateTime.parse(value, formatter);
              curW = (dateTime.getNano() != 0) ? 23 : (dateTime.getSecond() != 0) ? 20 : 17;
              width = (width < curW ? curW : width);
              if (curW == 23)
                row.set(i, dateTime.format(DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss.SS")));
              else if (curW == 20)
                row.set(i, dateTime.format(DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss")));
              else if (curW == 17)
                row.set(i, dateTime.format(DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm")));
            }
            break;
          case SPSS_FMT_F:
            BigDecimal d = parseDouble(value, true);
            if (d != null) {
              int decT = d.stripTrailingZeros().scale();
              if (decT <= 0) {
                row.set(i, d.stripTrailingZeros().longValue());
              } else {
                row.set(i, d);
              }
              if (decT > dec) {
                dec = decT;
              }
            } else {
              row.set(i, "");
            }
            if (value.length() > width) {
              width = value.length();
            }
            break;
          case SPSS_FMT_A:
            if (value.length() > width) {
              width = value.length();
            }
            break;
          default:
            break;
          }
        } catch (Exception e) {
          log.warn(e);
        }
      }
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
      vars.set(i, var);
    }
  }

  /**
   * 
   * @param pid
   * @param studyId
   * @param recordId
   * @param sForm
   * @param user
   * @param errors
   * @return
   */
  private Boolean validateSPSSFile(final Optional<Long> pid, final Optional<Long> studyId,
      final Optional<Long> recordId, StudyForm sForm, final UserDTO user, List<String> errors) {
    FileDTO file = null;
    Boolean error = false;
    try {
      file = fileUtil.buildFileDTO(pid.get(), studyId.get(), recordId.get(), 0, user.getId(), sForm.getSpssFile());
    } catch (Exception e) {
      error = true;
      log.error("Error creating FileDTO with input: [pid: {}; studyid: {}; recordid: {}; userid: {}; uploadedFile: {}]",
          () -> pid.get(), () -> studyId.get(), () -> recordId.get(), () -> 0, () -> user.getId(),
          () -> (sForm.getSpssFile() == null) ? "null" : sForm.getSpssFile().getOriginalFilename(), () -> e);
      errors.add(messageSource.getMessage("error.upload.creating.file", null, LocaleContextHolder.getLocale()));
    }
    // SAVE TMP FILE!!!
    String path = fileUtil.saveFile(file, true);
    RecordDTO spssFile = null;
    if (path != null && !error) {
      try {
        spssFile = new RecordDTO(spss.readWholeSPSSFile(path));
      } catch (Error | Exception e) {
        error = true;
        log.error("ERROR: Reading SPSS file wasn't successful:", () -> e);
        errors.add(messageSource.getMessage("error.upload.spss.file", null, LocaleContextHolder.getLocale()));
      }
      fileUtil.deleteFile(Paths.get(path));
      if (!error && (file == null || spssFile == null)) {
        log.error("ERROR: FileDTO or RecordDTO empty: [FileDTO: {}; RecordDTO: {}]",
            (file == null) ? "null" : "not null", (spssFile == null) ? "null" : "not null");
        error = true;
        errors.add(messageSource.getMessage("error.upload.intern.error", null, LocaleContextHolder.getLocale()));
      }
      if (!error) {
        file.setFilePath(null);
        spssFile.setId(recordId.get());
        spssFile.setChangedBy(user.getEmail());
        spssFile.setChangeLog(sForm.getNewChangeLog());
        sForm.setRecord(spssFile);
        sForm.setFile(file);
        saveRecordToDBAndMinio(sForm);
      }
    } else {
      log.warn("Error: Not FilePath was set - Temporary File was not saved to local Filesystem");
      errors.add(messageSource.getMessage("error.upload.intern.error", null, LocaleContextHolder.getLocale()));
      error = true;
    }
    return error;
  }

  /**
   * @param sForm
   */
  private void saveRecordToDBAndMinio(StudyForm sForm) {
    Boolean error = false;
    RecordDTO spssFile = sForm.getRecord();
    FileDTO file = sForm.getFile();
    if (!error && spssFile != null && file != null) {
      TransactionStatus status = txManager.getTransaction(new DefaultTransactionDefinition());
      try {
        recordDAO.insertMetaData(spssFile);
        if (spssFile.getAttributes() != null)
          recordDAO.insertAttributes(spssFile.getAttributes(), spssFile.getVersionId(), 0);
        if (spssFile.getVersionId() > 0) {
          recordDAO.insertMatrix(spssFile);
          for (SPSSVarTDO var : spssFile.getVariables()) {
            long key = recordDAO.insertVariable(var, spssFile.getVersionId());
            if (var.getAttributes() != null)
              recordDAO.insertAttributes(var.getAttributes(), spssFile.getVersionId(), key);
            if (var.getValues() != null)
              recordDAO.insertVarLabels(var.getValues(), key);
          }
          file.setVersion(spssFile.getVersionId());
          MinioResult res = minioUtil.putFile(file);
          if (res.equals(MinioResult.OK)) {
            fileDAO.saveFile(file);
          } else if (res.equals(MinioResult.CONNECTION_ERROR)) {
            log.fatal("FATAL: No Connection to Minio Server - please check Settings or Server");
            error = true;
          } else {
            log.error("ERROR: During Saving File - MinioResult:", () -> res.name());
            error = true;
          }
        }
        // TODO commit
      } catch (Exception e) {
        error = true;
        if (file.getFilePath() != null && !file.getFilePath().isEmpty())
          minioUtil.deleteFile(file);
        log.error("ERROR: Saving record to DB wasn't sucessful! Exception:", () -> e);
      } finally {
        if (error) {
          txManager.rollback(status);
        } else {
          txManager.commit(status);
        }
      }
    }
  }

  @RequestMapping(value = { "/{recordId}" }, method = RequestMethod.POST)
  public String save(@PathVariable final Optional<Long> pid, @PathVariable final Optional<Long> studyId,
      @PathVariable final Optional<Long> recordId, final ModelMap model, final RedirectAttributes redirectAttributes,
      final MultipartHttpServletRequest request) {
    log.trace("Entering  save for [recordId: {}; studyId {}; projectId {}]", () -> recordId.get(), () -> studyId.get(),
        () -> pid.get());
    return "redirect:/project/" + pid.get() + "/study/" + studyId.get() + "/record/" + recordId.get();
  }

  /**
   * This function tries to parse the values to numbers. It checks if the string contains untypical chars, or if the
   * decimal separator is a comma instead of a dot. This functions is also used for checking purposes, therefore an
   * Exception is only thrown during parsing and not during checking procedure.
   * 
   * @param String
   *          with the value which has to be checked or parsed
   * @param Boolean
   *          if parsing (true) or check(false) procedure
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
          log.error(
              "ERROR: Parsing not successful - Number was checkted before parsing, but something went wrong: Number: {} ",
              val, e);
        }
      }
    }
    return ret;
  }

  /**
   * @param sForm
   * @param dateFormatList
   * @param types
   * @param column
   * @param s
   */
  private void identifyRowType(StudyForm sForm, List<String> dateFormatList, List<SPSSVarTypes> types, int column,
      String s) {
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
      if (dateFormat.equals("M/d/yyyy") || dateFormat.equals("M/d/yy") || dateFormat.equals("d.M.yyyy")
          || dateFormat.equals("d.M.yy") || dateFormat.equals("d-M-yyyy") || dateFormat.equals("d-M-yy")
          || dateFormat.equals("yyyy-M-d")) {
        types.set(column, SPSSVarTypes.SPSS_FMT_DATE);
      } else if (dateFormat.equals("H:m") || dateFormat.equals("H:m:s") || dateFormat.equals("H:m:s.S")
          || dateFormat.equals("H:m:s.SS") || dateFormat.equals("H:m:s.SSS")) {
        types.set(column, SPSSVarTypes.SPSS_FMT_TIME);
      } else {
        types.set(column, SPSSVarTypes.SPSS_FMT_DATE_TIME);
      }
    } else {
      // not empty and not parseable, set to ALPHANUMERIC as default
      types.set(column, SPSSVarTypes.SPSS_FMT_A);
    }
  }
}
