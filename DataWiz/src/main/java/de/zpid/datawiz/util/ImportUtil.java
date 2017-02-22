package de.zpid.datawiz.util;

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
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Scope;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Repository;

import com.google.gson.Gson;
import com.opencsv.CSVReader;

import de.zpid.datawiz.dao.RecordDAO;
import de.zpid.datawiz.dto.FileDTO;
import de.zpid.datawiz.dto.RecordCompareDTO;
import de.zpid.datawiz.dto.RecordDTO;
import de.zpid.datawiz.dto.UserDTO;
import de.zpid.datawiz.enumeration.VariableStatus;
import de.zpid.datawiz.form.StudyForm;
import de.zpid.spss.SPSSIO;
import de.zpid.spss.dto.SPSSFileDTO;
import de.zpid.spss.dto.SPSSValueLabelDTO;
import de.zpid.spss.dto.SPSSVarDTO;
import de.zpid.spss.util.SPSSAligment;
import de.zpid.spss.util.SPSSMeasLevel;
import de.zpid.spss.util.SPSSMissing;
import de.zpid.spss.util.SPSSRoleCodes;
import de.zpid.spss.util.SPSSVarTypes;

@Repository
@Scope("singleton")
public class ImportUtil {

  private static Logger log = LogManager.getLogger(ImportUtil.class);
  final static String OS = System.getProperty("os.name").toLowerCase();
  @Autowired
  protected ClassPathXmlApplicationContext applicationContext;
  @Autowired
  private SPSSIO spss;
  @Autowired
  private FileUtil fileUtil;
  @Autowired
  protected MessageSource messageSource;
  @Autowired
  protected RecordDAO recordDAO;
  @Autowired
  protected MinioUtil minioUtil;

  /**
   * @param recordId
   * @param sForm
   * @param user
   * @param warnings
   * @param errors
   * @param error
   */
  public Boolean validateCSVFile(final Optional<Long> pid, final Optional<Long> studyId, final Optional<Long> recordId,
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
    if (!error) {
      try (CSVReader reader = new CSVReader(new InputStreamReader(sForm.getCsvFile().getInputStream(), "UTF-8"),
          (sForm.getCsvSeperator() == 't' ? '\t' : sForm.getCsvSeperator()),
          (sForm.getCsvQuoteChar() == 'q' ? '\"' : '\''))) {
        String[] nextLine;
        int numOfCol = 0, lineNumber = 1;
        List<List<Object>> matrix = new ArrayList<>();
        List<SPSSVarDTO> vars = new ArrayList<>();
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
            s = s.trim();
            SPSSVarDTO var = new SPSSVarDTO();
            var.setPosition(i + 1);
            if (s.startsWith("\uFEFF")) {
              s = s.substring(1);
              nextLine[i] = s;
            }
            if (s.isEmpty()) {
              s = "VAR_" + varCount++;
              warnings.add(messageSource.getMessage("warning.var.name.empty", new Object[] { (i + 1), s },
                  LocaleContextHolder.getLocale()));
            }
            if (!store.add(s.toUpperCase())) {
              int varNum = 1;
              String changedName = s + "_" + varNum;
              while (!store.add(changedName.toUpperCase())) {
                changedName = s + "_" + varNum++;
              }
              warnings.add(messageSource.getMessage("warning.var.name.doublette",
                  new Object[] { s, (i + 1), changedName }, LocaleContextHolder.getLocale()));
              s = changedName;
            }
            var.setName(s);
            var.setMissingFormat(SPSSMissing.SPSS_NO_MISSVAL);
            var.setAligment(SPSSAligment.SPSS_ALIGN_LEFT);
            var.setMeasureLevel(SPSSMeasLevel.SPSS_MLVL_UNK);
            var.setRole(SPSSRoleCodes.SPSS_ROLE_INPUT);
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
            log.debug(
                "CSV File corrupt - Number of Cols incorrect in [line: {}; current colums: {}, 1st row colums: {}",
                lineNumber, nextLine.length, numOfCol);
            errors.add(messageSource.getMessage("error.upload.csv.corrupt",
                new Object[] { lineNumber, numOfCol, nextLine.length }, LocaleContextHolder.getLocale()));
            error = true;
            break;
          }
          importMatrix.add(nextLine);
          List<Object> vector = new ArrayList<Object>();
          int column = 0;
          for (String s : nextLine) {
            s = s.trim();
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
            SPSSVarDTO var = new SPSSVarDTO();
            var.setName("var" + i);
            var.setPosition(i + 1);
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
        }
      } catch (IOException e) {
        error = true;
        log.error("Error reading CSV File with input: [uploadedFile name: {}; size: {}; contentType: {}]",
            () -> (sForm.getCsvFile() == null) ? "null" : sForm.getCsvFile().getOriginalFilename(),
            () -> (sForm.getCsvFile() == null) ? "null" : sForm.getCsvFile().getSize(),
            () -> (sForm.getCsvFile() == null) ? "null" : sForm.getCsvFile().getContentType(), () -> e);
        errors.add(messageSource.getMessage("error.upload.internal.error", null, LocaleContextHolder.getLocale()));
      }
    }
    return error;
  }

  /**
   * @param vars
   * @param position
   * @param curr
   * @param comp
   */
  public void compareVariable(final List<SPSSVarDTO> vars, final int position, final SPSSVarDTO curr,
      final RecordCompareDTO comp, final String selectedFileType) {
    SPSSVarDTO savedVar;
    if ((position) < vars.size()) {
      savedVar = vars.get(position);
    } else {
      savedVar = new SPSSVarDTO();
    }
    long savedId = savedVar.getId();
    savedVar.setId(0);
    if (!curr.equals(savedVar)) {
      // var not equal -
      if (curr.getType().equals(savedVar.getType()) && curr.getName().equals(savedVar.getName())) {
        // Same TYPE AND NAME -> save new VAR with old documentation, constructs and new verion_id
        if (selectedFileType.equals("CSV") && curr.getDecimals() == savedVar.getDecimals()
            && curr.getWidth() == savedVar.getWidth()) {
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
        boolean found = false;
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
  }

  /**
   * @param varList
   * @param var
   * @param comp
   * @param savedId
   * @param found
   * @return
   */
  public boolean findVarInList(List<SPSSVarDTO> varList, SPSSVarDTO var, RecordCompareDTO comp,
      final String selectedFileType) {
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
      } else if (selectedFileType.equals("CSV") && var.getType().equals(savedtmp.getType())
          && var.getName().equals(savedtmp.getName()) && var.getDecimals() == savedtmp.getDecimals()
          && var.getWidth() == savedtmp.getWidth()) {
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
   * @param sForm
   * @param numOfCol
   * @param matrix
   * @param vars
   * @param types
   */
  public void parseResult(StudyForm sForm, int numOfCol, List<List<Object>> matrix, List<SPSSVarDTO> vars,
      List<SPSSVarTypes> types, List<String> dateFormatList, RecordDTO record) {
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
  public Boolean validateSPSSFile(final Optional<Long> pid, final Optional<Long> studyId, final Optional<Long> recordId,
      StudyForm sForm, final UserDTO user, List<String> errors) {
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
      SPSSFileDTO spssTMP;
      if ((spssTMP = spss.readWholeSPSSFile(path)) != null)
        spssFile = new RecordDTO(spssTMP);
      else {
        log.error("ERROR: Reading SPSS file wasn't successful");
        errors.add(messageSource.getMessage("error.upload.spss.file", null, LocaleContextHolder.getLocale()));
        error = true;
      }
      fileUtil.deleteFile(Paths.get(path));
      if (!error && (file == null || spssFile == null)) {
        log.error("ERROR: FileDTO or RecordDTO empty: [FileDTO: {}; RecordDTO: {}]",
            (file == null) ? "null" : "not null", (spssFile == null) ? "null" : "not null");
        error = true;
        errors.add(messageSource.getMessage("error.upload.internal.error", null, LocaleContextHolder.getLocale()));
      }
      if (!error) {
        file.setFilePath(null);
        spssFile.setId(recordId.get());
        spssFile.setChangedBy(user.getEmail());
        spssFile.setChangeLog(sForm.getNewChangeLog());
        for (SPSSVarDTO var : spssFile.getVariables()) {
          sortVariableAttributes(var);
        }
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

  public void sortVariableAttributes(SPSSVarDTO var) {
    boolean dw_construct = false, dw_measocc = false, dw_instrument = false, dw_itemtext = false, dw_filtervar = false;
    Iterator<SPSSValueLabelDTO> itt = var.getAttributes().iterator();
    List<SPSSValueLabelDTO> dw_attr = new ArrayList<>();
    while (itt.hasNext()) {
      SPSSValueLabelDTO att = itt.next();
      if (att.getLabel().equals("dw_construct")) {
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

  /**
   * @param sForm
   * @throws Exception
   */
  public void compareVarVersion(StudyForm sForm) throws Exception {
    List<SPSSVarDTO> vars = new ArrayList<>();
    for (SPSSVarDTO tmp : sForm.getPreviousRecordVersion().getVariables()) {
      vars.add((SPSSVarDTO) ObjectCloner.deepCopy(tmp));
    }
    int listDiff = vars.size() - sForm.getRecord().getVariables().size();
    // TODO
    if (listDiff != 0 && sForm.getWarnings().stream()
        .filter(match -> "Variablenanzahl geändert hinzugefügt".trim().equals(match)).count() == 0) {
      sForm.getWarnings().add("Variablenanzahl geändert hinzugefügt".trim());
    }
    int position = 0;
    List<RecordCompareDTO> compList = new ArrayList<RecordCompareDTO>();
    for (SPSSVarDTO curr : sForm.getRecord().getVariables()) {
      RecordCompareDTO comp = (RecordCompareDTO) applicationContext.getBean("RecordCompareDTO");
      curr.setVarHandle(0.0);
      compareVariable(vars, position, curr, comp, sForm.getSelectedFileType());
      comp.setMessage(messageSource.getMessage("import.check." + comp.getVarStatus().name(),
          new Object[] { comp.getMovedFrom(), comp.getMovedTo() }, LocaleContextHolder.getLocale()));
      compList.add(comp);
      position++;
    }
    position = 0;
    List<RecordCompareDTO> compList2 = new ArrayList<RecordCompareDTO>();
    for (SPSSVarDTO curr : vars) {
      RecordCompareDTO comp = (RecordCompareDTO) applicationContext.getBean("RecordCompareDTO");
      compareVariable(sForm.getRecord().getVariables(), position, curr, comp, sForm.getSelectedFileType());
      compList2.add(comp);
      position++;
    }
    position = 0;
    List<SPSSVarDTO> delVars = new ArrayList<>();
    int delcount = 0;
    for (RecordCompareDTO comp : compList2) {
      if (position >= compList.size()) {
        SPSSVarDTO del = vars.get(position - delcount++);
        delVars.add(del);
        vars.remove(del);
      } else {
        if (comp.getVarStatus().equals(VariableStatus.NEW_VAR)
            && !compList.get(position).getVarStatus().equals(VariableStatus.NEW_VAR)) {
          SPSSVarDTO del = vars.get(position - delcount++);
          delVars.add(del);
          vars.remove(del);
        }
      }
      position++;
    }
    sForm.setDelVars(delVars);
    for (RecordCompareDTO rc : compList) {
      if (rc.getVarStatus().equals(VariableStatus.MOVED)
          || rc.getVarStatus().equals(VariableStatus.MOVED_AND_META_CHANGED)
          || rc.getVarStatus().equals(VariableStatus.MOVED_AND_META_CHANGED_CSV)
          || rc.getVarStatus().equals(VariableStatus.MOVED_AND_TYPE_CHANGED)
          || rc.getVarStatus().equals(VariableStatus.MOVED_CSV)) {
        SPSSVarDTO moved = sForm.getPreviousRecordVersion().getVariables().get(rc.getMovedFrom() - 1);
        vars.remove(moved);
        if (vars.size() < rc.getMovedTo() - 1) {
          for (int i = vars.size(); i < rc.getMovedTo() - 1; i++)
            vars.add(new SPSSVarDTO());
        }
        vars.add(rc.getMovedTo() - 1, moved);
      }
    }
    sForm.setCompList(compList);
    sForm.setViewVars(vars);
  }

}
