package de.zpid.datawiz.util;

public class RegexUtil {

  /**
   * 
   * Java Regex : Validate Email Address http://howtodoinjava.com/2014/11/11/java-regex-validate-email-address/ Output:
   * user@domain.com : true user@domain.co.in : true user.name@domain.com : true user_name@domain.com : true
   * username@yahoo.corporate.in : true .username@yahoo.com : false username@yahoo.com. : false username@yahoo..com :
   * false username@yahoo.c : false username@yahoo.corporate : false
   */
  public static final String validEmail = "[\\w!#$%&’*+/=?`{|}~^-]+(?:\\.[\\w!#$%&’*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}";

  /**
   * Regex for only alphabetic characters with umlauts, blanks and hyphens.
   */
  public static final String alphabeticWithBlanksAndHypens = "([a-zA-ZöäüßÖÄÜ]+|[ ]|[\\-]|[.])";

  /**
   * Phonenumber regex for german DIN-Norm: Example +49 (0) 651 201-2877
   */
  public static final String phonenumberGermanDIN = "\\+\\d{1,4} (\\(\\d\\) ){0,1}\\d{2,5} \\d{3,15}[\\-]{0,1}\\d{1,10}";

  public static final String onlyDigits = "\\d";

  public static final String regexORCID = "(^\\s*$|\\d{4}-\\d{4}-\\d{4}-\\d{4})";

  public static final String size0to250 = "{0,250}";
  public static final String size0to50 = "{0,50}";
  public static final String size0to10 = "{0,10}";
}
