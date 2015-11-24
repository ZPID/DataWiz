package de.zpid.datawiz.util;

public class RegexUtil {

  /**
   * 
   * Java Regex : Validate Email Address
   * http://howtodoinjava.com/2014/11/11/java-regex-validate-email-address/
   * Output: 
   * user@domain.com :               true
   * user@domain.co.in :             true
   * user.name@domain.com :          true
   * user_name@domain.com :          true
   * username@yahoo.corporate.in :   true 
   *.username@yahoo.com :            false
   * username@yahoo.com. :           false
   * username@yahoo..com :           false
   * username@yahoo.c :              false
   * username@yahoo.corporate :      false
   */
  public static final String validEmail = "[\\w!#$%&’*+/=?`{|}~^-]+(?:\\.[\\w!#$%&’*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}";

  /**
   * Regex for only alphabetic characters with umlauts, blanks and hyphens. 
   */
  public static final String regexStringWithoutNumber = "^$|[a-zA-ZöäüßÖÄÜ -]";
  
  public static final String size0to250 = "{0,250}";
  
}
