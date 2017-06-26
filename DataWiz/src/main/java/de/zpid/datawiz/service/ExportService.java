package de.zpid.datawiz.service;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.itextpdf.io.source.ByteArrayOutputStream;

import de.zpid.datawiz.dto.RecordDTO;
import de.zpid.datawiz.util.FileUtil;
import de.zpid.datawiz.util.ITextUtil;
import de.zpid.spss.SPSSIO;
import de.zpid.spss.dto.SPSSErrorDTO;
import de.zpid.spss.dto.SPSSValueLabelDTO;
import de.zpid.spss.dto.SPSSVarDTO;

@Component
public class ExportService {

  private static Logger log = LogManager.getLogger(ExportService.class);
  @Autowired
  protected MessageSource messageSource;
  @Autowired
  private SPSSIO spss;
  @Autowired
  private FileUtil fileUtil;
  @Autowired
  private ITextUtil itextUtil;

  /**
   * @param exportType
   * @param attachments
   * @param record
   * @param res
   * @param content
   * @return
   * @throws IOException
   */
  public byte[] getRecordExportContentAsByteArray(String exportType, Boolean attachments, RecordDTO record,
      StringBuilder res) throws IOException {
    byte[] content = null;
    if (exportType.equals("CSVMatrix")) {
      content = exportCSV(record, res, true);
    } else if (exportType.equals("CSVCodebook")) {
      content = exportCSV(record, res, false);
    } else if (exportType.equals("JSON")) {
      content = exportJSON(record, res);
    } else if (exportType.equals("SPSS")) {
      content = exportSPSSFile(record, res);
    } else if (exportType.equals("PDF")) {
      content = itextUtil.createPdf(record, false, attachments);
    } else if (exportType.equals("CSVZIP")) {
      List<Entry<String, byte[]>> files = new ArrayList<>();
      files.add(new SimpleEntry<String, byte[]>(record.getRecordName() + "_Matrix.csv", exportCSV(record, res, true)));
      files.add(
          new SimpleEntry<String, byte[]>(record.getRecordName() + "_Codebook.csv", exportCSV(record, res, false)));
      content = exportZip(files, res);
    }
    return content;
  }

  public byte[] exportZip(List<Entry<String, byte[]>> files, StringBuilder res) {
    log.trace("Entering exportZip for Num of File: [{}]", () -> files.size());
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
      res.insert(0, "export.error.exception.thown");
    }
    log.debug("Leaving exportZip with result [{}]", res.toString().trim().isEmpty() ? "OK" : res.toString());
    return content;
  }

  /**
   * This function prepares the record DTO for the export and transfers it to the SPSS IO Module. If the return of the
   * SPSS IO Module is valid, a byte[] which contains the spss file is returned. If some errors occur diring this
   * process, null is returned and an error code is written to ResponseEntity<String> resp.
   * 
   * @param record
   *          Complete copy of the requested record version
   * @param resp
   *          Reference parameter to handle export errors in the calling function
   * @return the spss file as byte array
   */
  public byte[] exportSPSSFile(RecordDTO record, StringBuilder res) {
    log.trace("Entering exportSPSSFile for RecordDTO: [id: {}; version:{}]", () -> record.getId(),
        () -> record.getVersionId());
    byte[] content = null;
    record.setFileIdString(record.getRecordName() + "_Version_(" + record.getVersionId() + ")");
    record.setErrors(new LinkedList<>());
    String dir = fileUtil.setFolderPath("temp");
    String filename = UUID.randomUUID().toString() + ".sav";
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
    if (record != null && record.getVariables() != null && record.getDataMatrix() != null) {
      try {
        record.getVariables().parallelStream().forEach(var -> {
          var.setDecimals(var.getDecimals() > 16 ? 16 : var.getDecimals());
        });
        if (!Files.exists(Paths.get(dir)))
          Files.createDirectories(Paths.get(dir));
        spss.writeSPSSFile(record, dir + filename);
        List<SPSSErrorDTO> errors = record.getErrors();
        if (errors.size() > 0) {
          for (SPSSErrorDTO error : errors)
            if (error.getError().getNumber() > 0) {
              res.insert(0, "export.error.spss.error");
              break;
            }
        }
        if (Files.exists(Paths.get(dir + filename))) {
          content = Files.readAllBytes(Paths.get(dir + filename));
        } else {
          if (res.length() == 0)
            res.insert(0, "export.error.file.not.exist");
        }
      } catch (Exception e) {
        log.warn("Error during SPSS export: RecordDTO: [id: {}; version:{}] Error: {}; Exception: {}",
            () -> record.getId(), () -> record.getVersionId(), () -> res.toString(), () -> e);
        e.printStackTrace();
        res.insert(0, "export.error.exception.thown");
      } finally {
        if (Files.exists(Paths.get(dir + filename)))
          fileUtil.deleteFile(Paths.get(dir + filename));
      }
    } else {
      res.insert(0, "export.recorddto.empty");
    }
    log.debug("Leaving exportSPSSFile with result [{}]", res.toString().trim().isEmpty() ? "OK" : res.toString());
    return content;
  }

  /**
   * This function transfers the record into a CSV String and returns it as byte array. It handles the CSV export for
   * the Caodebook and the matrix (boolean matrix = true).If some errors occur diring this process, null is returned and
   * an error code is written to ResponseEntity<String> resp.
   * 
   * @param record
   *          Complete copy of the requested record version
   * @param resp
   *          Reference parameter to handle export errors in the calling function
   * @return the csv string as byte array
   */
  public byte[] exportCSV(final RecordDTO record, StringBuilder res, final boolean matrix) {
    log.trace("Entering exportCSV for RecordDTO: [id: {}; version:{}] matrix[{}]", () -> record.getId(),
        () -> record.getVersionId(), () -> matrix);
    byte[] content = null;
    if (record != null && record.getVariables() != null && record.getDataMatrix() != null) {
      try {
        StringBuilder csv;
        if (matrix)
          csv = recordMatrixToCSVString(record);
        else
          csv = recordCodebookToCSVString(record);
        if (csv != null && csv.length() > 0) {
          content = csv.toString().getBytes(Charset.forName("UTF-8"));
        } else {
          res.insert(0, "export.csv.string.empty");
        }
      } catch (Exception e) {
        log.warn("Error during CSV export: RecordDTO: [id: {}; version:{} matrix[{}]] Exception: {}",
            () -> record.getId(), () -> record.getVersionId(), () -> matrix, () -> e);
        res.insert(0, "export.error.exception.thown");
      }
    } else {
      res.insert(0, "export.recorddto.empty");
    }
    log.debug("Leaving exportCSV with result [{}]", res.toString().trim().isEmpty() ? "OK" : res.toString());
    return content;
  }

  /**
   * This function transfers the record into a JSON String and returns it as byte array. If some errors occur diring
   * this process, null is returned and an error code is written to ResponseEntity<String> resp.
   * 
   * @param record
   *          Complete copy of the requested record version
   * @param resp
   *          Reference parameter to handle export errors in the calling function
   * @return the csv string as byte array
   */
  public byte[] exportJSON(RecordDTO record, StringBuilder res) {
    log.trace("Entering exportJSON for RecordDTO: [id: {}; version:{}]", () -> record.getId(),
        () -> record.getVersionId());
    byte[] content = null;
    if (record != null && record.getVariables() != null && record.getDataMatrix() != null) {
      try {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(record);
        content = json.getBytes(Charset.forName("UTF-8"));
        if (json != null && json.length() > 0) {
          content = json.getBytes(Charset.forName("UTF-8"));
        } else {
          res.insert(0, "export.json.string.empty");
        }
      } catch (Exception e) {
        log.warn("Error during JSON export: RecordDTO: [id: {}; version:{}] Exception: ", () -> record.getId(),
            () -> record.getVersionId(), () -> e);
        res.insert(0, "export.error.exception.thown");
      }
    } else {
      res.insert(0, "export.recorddto.empty");
    }
    log.debug("Leaving exportJSON with result [{}]", res.toString().trim().isEmpty() ? "OK" : res.toString());
    return content;
  }

  /**
   * This function creates the CSV matrix (with variable names as headline) from the data matrix list of the record
   * 
   * @param record
   *          Complete copy of the requested record version
   * @return StringBuilder which contains the Matrix as CSV
   */
  public StringBuilder recordMatrixToCSVString(RecordDTO record) {
    StringBuilder csv = new StringBuilder();
    int vars = record.getVariables().size();
    int varcount = 1;
    for (SPSSVarDTO var : record.getVariables()) {
      csv.append(var.getName());
      if (vars > varcount)
        csv.append(",");
      else if (vars == varcount) {
        csv.append("\n");
      }
      varcount++;
    }
    for (List<Object> row : record.getDataMatrix()) {
      if (row.size() != vars) {
        csv = null;
        break;
      }
      varcount = 1;
      for (Object obj : row) {
        if (obj instanceof Number)
          csv.append(obj);
        else
          csv.append("\"" + ((String) obj).replaceAll("\"", "\'") + "\"");
        if (vars > varcount++)
          csv.append(",");
      }
      csv.append("\n");
    }
    return csv;
  }

  /**
   * This function creates the complete Codebook and returns it as StringBuilder Object.
   * 
   * @param record
   *          Complete copy of the requested record version
   * @return StringBuilder which contains the Codebook as CSV
   */
  public StringBuilder recordCodebookToCSVString(RecordDTO record) {
    StringBuilder csv = new StringBuilder();
    csv.append(
        "name,type,label,values,missingformat,missingValue1,missingValue2,missingValue3,width,decimals,measurelevel,role,attributes,dw_konstruct,dw_measocc,dw_instrument,dw_itemtext,dw_filtervariable\n");
    for (SPSSVarDTO var : record.getVariables()) {
      csv.append("\"" + cleanString(var.getName()) + "\"");
      csv.append(",");
      if (var.getType() != null)
        csv.append("\"" + messageSource.getMessage("spss.type." + var.getType(), null, Locale.ENGLISH) + "\"");
      else
        csv.append("\"" + messageSource.getMessage("spss.type.SPSS_UNKNOWN", null, Locale.ENGLISH) + "\"");
      csv.append(",");
      if (var.getLabel() != null && !var.getLabel().isEmpty())
        csv.append("\"" + cleanString(var.getLabel()) + "\"");
      csv.append(",");
      if (var.getValues() != null && var.getValues().size() > 0) {
        csv.append("\"");
        for (SPSSValueLabelDTO val : var.getValues()) {
          csv.append("{");
          csv.append(cleanString(val.getValue()));
          csv.append("=");
          csv.append(cleanString(val.getLabel()));
          csv.append("}");
        }
        csv.append("\"");
      }
      csv.append(",");
      csv.append(
          "\"" + messageSource.getMessage("spss.missings." + var.getMissingFormat(), null, Locale.ENGLISH) + "\"");
      csv.append(",");
      switch (var.getMissingFormat()) {
      case SPSS_NO_MISSVAL:
        csv.append(",");
        csv.append(",");
        break;
      case SPSS_ONE_MISSVAL:
        csv.append("\"" + var.getMissingVal1() + "\"");
        csv.append(",");
        csv.append(",");
        break;
      case SPSS_TWO_MISSVAL:
        csv.append("\"" + var.getMissingVal1() + "\"");
        csv.append(",");
        csv.append("\"" + var.getMissingVal2() + "\"");
        csv.append(",");
        break;
      case SPSS_THREE_MISSVAL:
        csv.append("\"" + var.getMissingVal1() + "\"");
        csv.append(",");
        csv.append("\"" + var.getMissingVal2() + "\"");
        csv.append(",");
        csv.append("\"" + var.getMissingVal3() + "\"");
        break;
      case SPSS_MISS_RANGE:
        csv.append("\"" + var.getMissingVal1() + "\"");
        csv.append(",");
        csv.append("\"" + var.getMissingVal2() + "\"");
        csv.append(",");
        break;
      case SPSS_MISS_RANGEANDVAL:
        csv.append("\"" + var.getMissingVal1() + "\"");
        csv.append(",");
        csv.append("\"" + var.getMissingVal2() + "\"");
        csv.append(",");
        csv.append("\"" + var.getMissingVal3() + "\"");
        break;
      case SPSS_UNKNOWN:
        csv.append(",");
        csv.append(",");
        break;
      }
      csv.append(",");
      csv.append(var.getWidth());
      csv.append(",");
      csv.append(var.getDecimals());
      csv.append(",");
      csv.append(
          "\"" + messageSource.getMessage("spss.measureLevel." + var.getMeasureLevel(), null, Locale.ENGLISH) + "\"");
      csv.append(",");
      csv.append("\"" + messageSource.getMessage("spss.role." + var.getRole(), null, Locale.ENGLISH) + "\"");
      csv.append(",");
      if (var.getAttributes() != null && var.getAttributes().size() > 0) {
        csv.append("\"");
        for (SPSSValueLabelDTO val : var.getAttributes()) {
          csv.append("{");
          csv.append(val.getValue());
          csv.append("=");
          csv.append(val.getLabel());
          csv.append("}");
        }
        csv.append("\"");
      }
      String dw_construct = "", dw_measocc = "", dw_instrument = "", dw_itemtext = "", dw_filtervar = "";
      if (var.getDw_attributes() != null && var.getDw_attributes().size() > 0) {
        for (SPSSValueLabelDTO val : var.getDw_attributes()) {
          switch (val.getLabel()) {
          case "dw_construct":
            dw_construct = val.getValue();
            break;
          case "dw_measocc":
            dw_measocc = val.getValue();
            break;
          case "dw_instrument":
            dw_instrument = val.getValue();
            break;
          case "dw_itemtext":
            dw_itemtext = val.getValue();
            break;
          case "dw_filtervar":
            dw_filtervar = val.getValue();
            break;
          }
        }
      }
      csv.append(",");
      if (!dw_construct.isEmpty())
        csv.append("\"" + cleanString(dw_construct) + "\"");
      csv.append(",");
      if (!dw_measocc.isEmpty())
        csv.append("\"" + cleanString(dw_measocc) + "\"");
      csv.append(",");
      if (!dw_instrument.isEmpty())
        csv.append("\"" + cleanString(dw_instrument) + "\"");
      csv.append(",");
      if (!dw_itemtext.isEmpty())
        csv.append("\"" + cleanString(dw_itemtext) + "\"");
      csv.append(",");
      if (!dw_filtervar.isEmpty())
        csv.append(dw_filtervar);
      csv.append("\n");
    }
    return csv;
  }

  /**
   * This function removes all line-break and tabulator commands from the passed String.
   * 
   * @param s
   *          String, which has to be cleaned
   * @return Cleaned String
   */
  private String cleanString(String s) {
    return s.replaceAll("(\\r|\\n|\\t)", " ").replaceAll("\"", "'");
  }
}
