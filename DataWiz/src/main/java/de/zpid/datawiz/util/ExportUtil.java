package de.zpid.datawiz.util;

import java.nio.charset.Charset;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;

import de.zpid.datawiz.dto.RecordDTO;
import de.zpid.spss.dto.SPSSValueLabelDTO;
import de.zpid.spss.dto.SPSSVarTDO;

@Repository
public class ExportUtil {

  private static Logger log = LogManager.getLogger(ExportUtil.class);
  @Autowired
  protected MessageSource messageSource;

  /**
   * 
   * @param record
   * @param response
   * @return
   */
  public ResponseEntity<String> exportCSVMatrix(RecordDTO record, HttpServletResponse response) {
    ResponseEntity<String> resp = new ResponseEntity<String>("{}", HttpStatus.OK);
    if (record != null && record.getVariables() != null && record.getDataMatrix() != null) {
      try {
        StringBuilder csv = recordMatrixToCSVString(record);
        if (csv != null && csv.length() > 0) {
          response.setContentType("application/csv");
          response.setHeader("Content-Disposition", "attachment; filename=\"" + record.getRecordName() + ".json\"");
          response.setContentLength(csv.toString().length());
          response.getOutputStream().write(csv.toString().getBytes(Charset.forName("UTF-8")));
        } else {
          resp = new ResponseEntity<String>("{}", HttpStatus.CONFLICT);
        }
      } catch (Exception e) {
        resp = new ResponseEntity<String>("{}", HttpStatus.CONFLICT);
      }
    } else {
      resp = new ResponseEntity<String>("{}", HttpStatus.CONFLICT);
    }
    return resp;
  }

  public StringBuilder recordMatrixToCSVString(RecordDTO record) {
    StringBuilder csv = new StringBuilder();
    int vars = record.getVariables().size();
    int varcount = 1;
    for (SPSSVarTDO var : record.getVariables()) {
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
        csv.append(obj);
        if (vars > varcount++)
          csv.append(",");
      }
      csv.append("\n");
    }
    return csv;
  }

  /**
   * 
   * @param record
   * @param response
   * @return
   */
  public ResponseEntity<String> exportCSVCodeBook(RecordDTO record, HttpServletResponse response) {
    ResponseEntity<String> resp = new ResponseEntity<String>("{}", HttpStatus.OK);
    if (record != null && record.getVariables() != null && record.getDataMatrix() != null) {
      try {
        StringBuilder csv = recordCodebookToCSVString(record);
        if (csv != null && csv.length() > 0) {
          response.setContentType("application/csv");
          response.setHeader("Content-Disposition", "attachment; filename=\"" + record.getRecordName() + ".json\"");
          response.setContentLength(csv.toString().length());
          response.getOutputStream().write(csv.toString().getBytes(Charset.forName("UTF-8")));
        } else {
          resp = new ResponseEntity<String>("{}", HttpStatus.CONFLICT);
        }
      } catch (Exception e) {
        log.warn(e);
        resp = new ResponseEntity<String>("{}", HttpStatus.CONFLICT);
      }
    } else {
      resp = new ResponseEntity<String>("{}", HttpStatus.CONFLICT);
    }
    return resp;
  }

  /**
   * 
   * @param record
   * @return
   */
  public StringBuilder recordCodebookToCSVString(RecordDTO record) {
    StringBuilder csv = new StringBuilder();
    csv.append(
        "name,type,label,values,missingformat,missingValue1,missingValue2,missingValue3,width,decimals,measurelevel,role,attributes,dw_konstruct,dw_measocc,dw_instrument,dw_itemtext,dw_filtervariable\n");
    for (SPSSVarTDO var : record.getVariables()) {
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

  private String cleanString(String s) {
    return s.replaceAll("(\\r|\\n|\\t)", " ").replaceAll("\"", "'");
  }
}
