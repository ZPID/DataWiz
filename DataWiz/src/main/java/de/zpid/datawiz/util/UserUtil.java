package de.zpid.datawiz.util;

import org.apache.log4j.Logger;
import org.springframework.security.core.context.SecurityContextHolder;

import de.zpid.datawiz.dto.UserDTO;

public class UserUtil {

  private static final Logger log = Logger.getLogger(UserUtil.class);

  public static UserDTO getCurrentUser() {
    UserDTO user = null;
    try {
      user = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
    } catch (Exception e) {
      log.warn("Error getting current authendicated User - Message: " + e.getMessage());
      user = null;
    }
    return user;
  }
}
