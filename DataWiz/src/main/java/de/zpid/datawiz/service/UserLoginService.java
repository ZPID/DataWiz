package de.zpid.datawiz.service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.zpid.datawiz.dao.UserDao;
import de.zpid.datawiz.dto.UserDTO;
import de.zpid.datawiz.dto.UserRoleDTO;
import de.zpid.datawiz.util.Roles;

@Service("userLoginService")
public class UserLoginService implements UserDetailsService {

  final Logger log = Logger.getLogger(UserLoginService.class);
  @Autowired
  private UserDao userDao;

  @Transactional(readOnly = true)
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    if (log.isDebugEnabled())
      log.debug("execute loadUserByUsername with email=" + email);
    UserDTO user = null;
    try {
      user = userDao.findByMail(email);
    } catch (DataAccessException | SQLException e) {
      log.debug("DataAccessException or  SQLException: " + e.getMessage());
    }
    if (user == null)
      throw new UsernameNotFoundException("User not found email=" + email);
    return new User(user.getEmail(), user.getPassword(), getGrantedAuthorities(user));
  }

  private List<GrantedAuthority> getGrantedAuthorities(UserDTO user) {
    if (log.isDebugEnabled())
      log.debug("execute getGrantedAuthorities for user=" + user.getEmail());
    List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
    for (UserRoleDTO userProfile : user.getGlobalRoles()) {
      if (userProfile.getType().equals(Roles.ADMIN.name()) || userProfile.getType().equals(Roles.USER.name()))
        authorities.add(new SimpleGrantedAuthority("ROLE_" + userProfile.getType()));
    }
    return authorities;
  }

}
