package de.zpid.datawiz.util;

import java.util.HashMap;
import java.util.Map;

public class DateUtil {

  private static String MONTH = "(0?[1-9]|1[0-2])";
  private static String DAYS = "(0?[1-9]|1[0-9]|2[0-9]|3[0-1])";
  private static String HOUR = "(0?[0-9]|1[0-9]|2[0-3])";
  private static String MIN_SEC = "(0[0-9]|1[0-9]|2[0-9]|3[0-9]|4[0-9]|5[0-9])";
  private static final Map<String, String> DATE_FORMAT_REGEXPS=new HashMap<String,String>(){private static final long serialVersionUID=-425311619273631872L;{
  // M/d/yyyy
  put("^"+MONTH+"\\/"+DAYS+"\\/\\d{4}$","M/d/yyyy");put("^"+MONTH+"\\/"+DAYS+"\\/\\d{4}\\s"+HOUR+"\\:"+MIN_SEC+"$","M/d/yyyy H:m");put("^"+MONTH+"\\/"+DAYS+"\\/\\d{4}\\s"+HOUR+"\\:"+MIN_SEC+"\\:"+MIN_SEC+"$","M/d/yyyy H:m:s");put("^"+MONTH+"\\/"+DAYS+"\\/\\d{4}\\s"+HOUR+"\\:"+MIN_SEC+"\\:"+MIN_SEC+"\\.\\d{1}$","M/d/yyyy H:m:s.S");put("^"+MONTH+"\\/"+DAYS+"\\/\\d{4}\\s"+HOUR+"\\:"+MIN_SEC+"\\:"+MIN_SEC+"\\.\\d{2}$","M/d/yyyy H:m:s.SS");put("^"+MONTH+"\\/"+DAYS+"\\/\\d{4}\\s"+HOUR+"\\:"+MIN_SEC+"\\:"+MIN_SEC+"\\.\\d{3}$","M/d/yyyy H:m:s.SSS");
  // M/d/yy
  put("^"+MONTH+"\\/"+DAYS+"\\/\\d{2}$","M/d/yy");put("^"+MONTH+"\\/"+DAYS+"\\/\\d{2}\\s"+HOUR+"\\:"+MIN_SEC+"$","M/d/yy H:m");put("^"+MONTH+"\\/"+DAYS+"\\/\\d{2}\\s"+HOUR+"\\:"+MIN_SEC+"\\:"+MIN_SEC+"$","M/d/yy H:m:s");put("^"+MONTH+"\\/"+DAYS+"\\/\\d{2}\\s"+HOUR+"\\:"+MIN_SEC+"\\:"+MIN_SEC+"\\.\\d{1}$","M/d/yy H:m:s.S");put("^"+MONTH+"\\/"+DAYS+"\\/\\d{2}\\s"+HOUR+"\\:"+MIN_SEC+"\\:"+MIN_SEC+"\\.\\d{2}$","M/d/yy H:m:s.SS");put("^"+MONTH+"\\/"+DAYS+"\\/\\d{2}\\s"+HOUR+"\\:"+MIN_SEC+"\\:"+MIN_SEC+"\\.\\d{3}$","M/d/yy H:m:s.SSS");
  // d.M.yyyy
  put("^"+DAYS+"\\."+MONTH+"\\.\\d{4}$","d.M.yyyy");put("^"+DAYS+"\\."+MONTH+"\\.\\d{4}\\s"+HOUR+"\\:"+MIN_SEC+"$","d.M.yyyy H:m");put("^"+DAYS+"\\."+MONTH+"\\.\\d{4}\\s"+HOUR+"\\:"+MIN_SEC+"\\:"+MIN_SEC+"$","d.M.yyyy H:m:s");put("^"+DAYS+"\\."+MONTH+"\\.\\d{4}\\s"+HOUR+"\\:"+MIN_SEC+"\\:"+MIN_SEC+"\\.\\d{1}$","d.M.yyyy H:m:s.S");put("^"+DAYS+"\\."+MONTH+"\\.\\d{4}\\s"+HOUR+"\\:"+MIN_SEC+"\\:"+MIN_SEC+"\\.\\d{2}$","d.M.yyyy H:m:s.SS");put("^"+DAYS+"\\."+MONTH+"\\.\\d{4}\\s"+HOUR+"\\:"+MIN_SEC+"\\:"+MIN_SEC+"\\.\\d{3}$","d.M.yyyy H:m:s.SSS");
  // d.M.yy
  put("^"+DAYS+"\\."+MONTH+"\\.\\d{2}$","d.M.yy");put("^"+DAYS+"\\."+MONTH+"\\.\\d{2}\\s"+HOUR+"\\:"+MIN_SEC+"$","d.M.yy H:m");put("^"+DAYS+"\\."+MONTH+"\\.\\d{2}\\s"+HOUR+"\\:"+MIN_SEC+"\\:"+MIN_SEC+"$","d.M.yy H:m:s");put("^"+DAYS+"\\."+MONTH+"\\.\\d{2}\\s"+HOUR+"\\:"+MIN_SEC+"\\:"+MIN_SEC+"\\.\\d{1}$","d.M.yy H:m:s.S");put("^"+DAYS+"\\."+MONTH+"\\.\\d{2}\\s"+HOUR+"\\:"+MIN_SEC+"\\:"+MIN_SEC+"\\.\\d{2}$","d.M.yy H:m:s.SS");put("^"+DAYS+"\\."+MONTH+"\\.\\d{2}\\s"+HOUR+"\\:"+MIN_SEC+"\\:"+MIN_SEC+"\\.\\d{3}$","d.M.yy H:m:s.SSS");
  // d-M-yyyy
  put("^"+DAYS+"-"+MONTH+"-\\d{4}$","d-M-yyyy");put("^"+DAYS+"-"+MONTH+"-\\d{4}\\s"+HOUR+"\\:"+MIN_SEC+"$","d-M-yyyy H:m");put("^"+DAYS+"-"+MONTH+"-\\d{4}\\s"+HOUR+"\\:"+MIN_SEC+"\\:"+MIN_SEC+"$","d-M-yyyy H:m:s");put("^"+DAYS+"-"+MONTH+"-\\d{4}\\s"+HOUR+"\\:"+MIN_SEC+"\\:"+MIN_SEC+"\\.\\d{1}$","d-M-yyyy H:m:s.S");put("^"+DAYS+"-"+MONTH+"-\\d{4}\\s"+HOUR+"\\:"+MIN_SEC+"\\:"+MIN_SEC+"\\.\\d{2}$","d-M-yyyy H:m:s.SS");put("^"+DAYS+"-"+MONTH+"-\\d{4}\\s"+HOUR+"\\:"+MIN_SEC+"\\:"+MIN_SEC+"\\.\\d{3}$","d-M-yyyy H:m:s.SSS");
  // d-M-yy
  put("^"+DAYS+"-"+MONTH+"-\\d{2}$","d-M-yy");put("^"+DAYS+"-"+MONTH+"-\\d{2}\\s"+HOUR+"\\:"+MIN_SEC+"$","d-M-yy H:m");put("^"+DAYS+"-"+MONTH+"-\\d{2}\\s"+HOUR+"\\:"+MIN_SEC+"\\:"+MIN_SEC+"$","d-M-yy H:m:s");put("^"+DAYS+"-"+MONTH+"-\\d{2}\\s"+HOUR+"\\:"+MIN_SEC+"\\:"+MIN_SEC+"\\.\\d{1}$","d-M-yy H:m:s.S");put("^"+DAYS+"-"+MONTH+"-\\d{2}\\s"+HOUR+"\\:"+MIN_SEC+"\\:"+MIN_SEC+"\\.\\d{2}$","d-M-yy H:m:s.SS");put("^"+DAYS+"-"+MONTH+"-\\d{2}\\s"+HOUR+"\\:"+MIN_SEC+"\\:"+MIN_SEC+"\\.\\d{3}$","d-M-yy H:m:s.SSS");
  // yyyy-M-d
  put("^\\d{4}-"+MONTH+"-"+DAYS+"$","yyyy-M-d");put("^\\d{4}-"+MONTH+"-"+DAYS+"\\s"+HOUR+"\\:"+MIN_SEC+"$","yyyy-M-d H:m");put("^\\d{4}-"+MONTH+"-"+DAYS+"\\s"+HOUR+"\\:"+MIN_SEC+"\\:"+MIN_SEC+"$","yyyy-M-d H:m:s");put("^\\d{4}-"+MONTH+"-"+DAYS+"\\s"+HOUR+"\\:"+MIN_SEC+"\\:"+MIN_SEC+"\\.\\d{1}$","yyyy-M-d H:m:s.S");put("^\\d{4}-"+MONTH+"-"+DAYS+"\\s"+HOUR+"\\:"+MIN_SEC+"\\:"+MIN_SEC+"\\.\\d{2}$","yyyy-M-d H:m:s.SS");put("^\\d{4}-"+MONTH+"-"+DAYS+"\\s"+HOUR+"\\:"+MIN_SEC+"\\:"+MIN_SEC+"\\.\\d{3}$","yyyy-M-d H:m:s.SSS");
  // TIME
  put("^"+HOUR+"\\:"+MIN_SEC+"$","H:m"); // type 21
  put("^"+HOUR+"\\:"+MIN_SEC+"\\:"+MIN_SEC+"$","H:m:s"); // type 21
  put("^"+HOUR+"\\:"+MIN_SEC+"\\:"+MIN_SEC+"\\.\\d{1}$","H:m:s.S"); // type 21
  put("^"+HOUR+"\\:"+MIN_SEC+"\\:"+MIN_SEC+"\\.\\d{2}$","H:m:s.SS"); // type 21
  put("^"+HOUR+"\\:"+MIN_SEC+"\\:"+MIN_SEC+"\\.\\d{3}$","H:m:s.SSS"); // type 21
  }};



  /**
   * Determine SimpleDateFormat pattern matching with the given date string. Returns null if format is unknown.
   * 
   * @param dateString
   *          The date string to determine the SimpleDateFormat pattern for.
   * @return The matching SimpleDateFormat pattern, or null if format is unknown.
   * @see SimpleDateFormat
   */
  public static String determineDateFormat(String dateString) {
    for (String regexp : DATE_FORMAT_REGEXPS.keySet()) {
      if (dateString.toLowerCase().matches(regexp)) {
        return DATE_FORMAT_REGEXPS.get(regexp);
      }
    }
    return null; // Unknown format.
  }
}
