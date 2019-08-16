package de.zpid.datawiz.util;

import de.zpid.datawiz.dto.RecordDTO;
import de.zpid.datawiz.spss.SPSSValueLabelDTO;
import de.zpid.datawiz.spss.SPSSVarDTO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Component
public class CSVUtil {

    private static final Logger log = LogManager.getLogger(CSVUtil.class);
    private final StringUtil stringUtil;
    private final MessageSource messageSource;

    @Autowired
    public CSVUtil(StringUtil stringUtil, MessageSource messageSource) {
        this.stringUtil = stringUtil;
        this.messageSource = messageSource;
    }


    /**
     * This function transfers the record into a CSV String and returns it as byte array. It handles the CSV export for the Caodebook and the matrix (boolean
     * matrix = true).If some errors occur diring this process, null is returned and an error code is written to ResponseEntity<String> resp.
     *
     * @param record Complete copy of the requested record version
     * @param res    Reference parameter to handle export errors in the calling function
     * @return the csv string as byte array
     */
    public byte[] exportCSV(final RecordDTO record, StringBuilder res, final boolean matrix) {
        log.trace("Entering exportCSV for RecordDTO: [id: {}; version:{}] matrix[{}]", record::getId, record::getVersionId, () -> matrix);
        byte[] content = null;
        if (record.getVariables() != null && !record.getVariables().isEmpty() && record.getDataMatrix() != null) {
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
                log.warn("Error during CSV export: RecordDTO: [id: {}; version:{} matrix[{}]] Exception: {}", record::getId, record::getVersionId,
                        () -> matrix, () -> e);
                res.insert(0, "export.error.exception.thrown");
            }
        } else {
            res.insert(0, "export.recorddto.empty");
        }
        log.debug("Leaving exportCSV with result [{}]", res.toString().trim().isEmpty() ? "OK" : res.toString());
        return content;
    }

    /**
     * This function creates the CSV matrix (with variable names as headline) from the data matrix list of the record
     *
     * @param record Complete copy of the requested record version
     * @return StringBuilder which contains the Matrix as CSV
     */
    private StringBuilder recordMatrixToCSVString(RecordDTO record) {
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
                if (obj == null) {
                    csv.append(" ");
                } else if (obj instanceof Number)
                    csv.append(obj);
                else
                    csv.append("\"").append(((String) obj).replaceAll("\\r\\n|\\r|\\n", " ").replaceAll("\"", "\'")).append("\"");
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
     * @param record Complete copy of the requested record version
     * @return StringBuilder which contains the Codebook as CSV
     */
    private StringBuilder recordCodebookToCSVString(RecordDTO record) {
        StringBuilder csv = new StringBuilder();
        csv.append(
                "name,type,label,values,missingformat,missingValue1,missingValue2,missingValue3,width,decimals,measurelevel,role,attributes,dw_konstruct,dw_measocc,dw_instrument,dw_itemtext,dw_filtervariable\n");
        for (SPSSVarDTO var : record.getVariables()) {
            csv.append("\"").append(stringUtil.removeLineBreaksAndTabsString(var.getName())).append("\"");
            csv.append(",");
            if (var.getType() != null)
                csv.append("\"").append(messageSource.getMessage("spss.type." + var.getType(), null, Locale.ENGLISH)).append("\"");
            else
                csv.append("\"").append(messageSource.getMessage("spss.type.SPSS_UNKNOWN", null, Locale.ENGLISH)).append("\"");
            csv.append(",");
            if (var.getLabel() != null && !var.getLabel().isEmpty())
                csv.append("\"").append(stringUtil.removeLineBreaksAndTabsString(var.getLabel())).append("\"");
            csv.append(",");
            if (var.getValues() != null && var.getValues().size() > 0) {
                csv.append("\"");
                for (SPSSValueLabelDTO val : var.getValues()) {
                    csv.append("{");
                    csv.append(stringUtil.removeLineBreaksAndTabsString(val.getValue()));
                    csv.append("=");
                    csv.append(stringUtil.removeLineBreaksAndTabsString(val.getLabel()));
                    csv.append("}");
                }
                csv.append("\"");
            }
            csv.append(",");
            csv.append("\"").append(messageSource.getMessage("spss.missings." + var.getMissingFormat(), null, Locale.ENGLISH)).append("\"");
            csv.append(",");
            switch (var.getMissingFormat()) {
                case SPSS_NO_MISSVAL:
                    csv.append(",");
                    csv.append(",");
                    break;
                case SPSS_ONE_MISSVAL:
                    csv.append("\"").append(var.getMissingVal1()).append("\"");
                    csv.append(",");
                    csv.append(",");
                    break;
                case SPSS_TWO_MISSVAL:
                    csv.append("\"").append(var.getMissingVal1()).append("\"");
                    csv.append(",");
                    csv.append("\"").append(var.getMissingVal2()).append("\"");
                    csv.append(",");
                    break;
                case SPSS_THREE_MISSVAL:
                    csv.append("\"").append(var.getMissingVal1()).append("\"");
                    csv.append(",");
                    csv.append("\"").append(var.getMissingVal2()).append("\"");
                    csv.append(",");
                    csv.append("\"").append(var.getMissingVal3()).append("\"");
                    break;
                case SPSS_MISS_RANGE:
                    csv.append("\"").append(var.getMissingVal1()).append("\"");
                    csv.append(",");
                    csv.append("\"").append(var.getMissingVal2()).append("\"");
                    csv.append(",");
                    break;
                case SPSS_MISS_RANGEANDVAL:
                    csv.append("\"").append(var.getMissingVal1()).append("\"");
                    csv.append(",");
                    csv.append("\"").append(var.getMissingVal2()).append("\"");
                    csv.append(",");
                    csv.append("\"").append(var.getMissingVal3()).append("\"");
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
            csv.append("\"").append(messageSource.getMessage("spss.measureLevel." + var.getMeasureLevel(), null, Locale.ENGLISH)).append("\"");
            csv.append(",");
            csv.append("\"").append(messageSource.getMessage("spss.role." + var.getRole(), null, Locale.ENGLISH)).append("\"");
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
            Map<String, String> dwAttributes = sortDwAttributes(var);
            csv.append(",");
            if (!dwAttributes.get("dw_construct").isEmpty())
                csv.append("\"").append(stringUtil.removeLineBreaksAndTabsString(dwAttributes.get("dw_construct"))).append("\"");
            csv.append(",");
            if (!dwAttributes.get("dw_measocc").isEmpty())
                csv.append("\"").append(stringUtil.removeLineBreaksAndTabsString(dwAttributes.get("dw_measocc"))).append("\"");
            csv.append(",");
            if (!dwAttributes.get("dw_instrument").isEmpty())
                csv.append("\"").append(stringUtil.removeLineBreaksAndTabsString(dwAttributes.get("dw_instrument"))).append("\"");
            csv.append(",");
            if (!dwAttributes.get("dw_itemtext").isEmpty())
                csv.append("\"").append(stringUtil.removeLineBreaksAndTabsString(dwAttributes.get("dw_itemtext"))).append("\"");
            csv.append(",");
            if (!dwAttributes.get("dw_filtervar").isEmpty())
                csv.append(dwAttributes.get("dw_filtervar"));
            csv.append("\n");
        }
        return csv;
    }

    public Map<String, String> sortDwAttributes(SPSSVarDTO var) {
        Map<String, String> dwAttributes = new HashMap<>();
        dwAttributes.put("dw_construct", "");
        dwAttributes.put("dw_measocc", "");
        dwAttributes.put("dw_instrument", "");
        dwAttributes.put("dw_itemtext", "");
        dwAttributes.put("dw_filtervar", "");
        if (var.getDw_attributes() != null && var.getDw_attributes().size() > 0) {
            var.getDw_attributes().parallelStream().forEach(val -> {
                if (val.getLabel().equals("dw_construct") || val.getLabel().equals("dw_measocc") || val.getLabel().equals("dw_instrument")
                        || val.getLabel().equals("dw_itemtext") || val.getLabel().equals("dw_filtervar"))
                    dwAttributes.replace(val.getLabel(), val.getValue());
            });
        }
        return dwAttributes;
    }
}
