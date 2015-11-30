package de.zpid.datawiz.service;

import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.zpid.datawiz.dao.UserDAO;
import de.zpid.datawiz.dto.UserDTO;
import de.zpid.datawiz.util.CustomUserDetails;

@Service("userLoginService")
public class UserLoginService implements UserDetailsService {

  final Logger log = Logger.getLogger(UserLoginService.class);
  @Autowired
  private UserDAO userDao;

  @Transactional(readOnly = true)
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    if (log.isDebugEnabled())
      log.debug("execute loadUserByUsername with email=" + email);
    UserDTO user = null;
    try {
      user = userDao.findByMail(email, true);
    } catch (DataAccessException | SQLException e) {
      log.debug("DataAccessException or  SQLException: " + e.getMessage());
    }
    if (user == null)
      throw new UsernameNotFoundException("User not found email=" + email);
    return new CustomUserDetails(user);
  }
}
