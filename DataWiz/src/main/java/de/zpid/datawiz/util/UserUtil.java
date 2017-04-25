package de.zpid.datawiz.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.context.SecurityContextHolder;

import de.zpid.datawiz.dto.UserDTO;

public class UserUtil {

  private static Logger log = LogManager.getLogger(UserUtil.class);

  public static UserDTO getCurrentUser() {
    UserDTO user = null;
    try {
      if (SecurityContextHolder.getContext().getAuthentication() != null
          && SecurityContextHolder.getContext().getAuthentication().getPrincipal() != null
          && SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof CustomUserDetails)
        user = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
    } catch (Exception e) {
      log.warn("Error getting current authendicated User - Message: " + e.getMessage());
      user = null;
    }
    return user;
  }

  public static boolean setCurrentUser(UserDTO user) {
    boolean set = false;
    if (user != null) {
      try {
        if (SecurityContextHolder.getContext().getAuthentication() != null
            && SecurityContextHolder.getContext().getAuthentication().getPrincipal() != null
            && SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof CustomUserDetails) {
          ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).setUser(user);
          set = true;
        }
      } catch (Exception e) {
        log.warn("Error setting current authendicated User - Message: " + e.getMessage());
      }
    }
    return set;
  }
}
