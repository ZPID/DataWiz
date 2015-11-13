package de.zpid.datawiz.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.zpid.datawiz.dao.UserDao;
import de.zpid.datawiz.model.DataWizUser;
import de.zpid.datawiz.model.DataWizUserRoles;

@Service("userLoginService")
public class UserLoginService implements UserDetailsService {

  final Logger log = Logger.getLogger(UserLoginService.class);
  @Autowired
  private UserDao userDao;

  @Transactional(readOnly = true)
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    if (log.isDebugEnabled())
      log.debug("execute loadUserByUsername with email=" + email);
    DataWizUser user = userDao.findByMail(email);
    if (user == null) {
      throw new UsernameNotFoundException("User not found email=" + email);
    }
    return new User(user.getEmail(), user.getPassword(), getGrantedAuthorities(user));
  }

  private List<GrantedAuthority> getGrantedAuthorities(DataWizUser user) {
    if (log.isDebugEnabled())
      log.debug("execute getGrantedAuthorities for user=" + ((user != null) ? user.getEmail() : ""));
    List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
    for (DataWizUserRoles userProfile : user.getUserProfiles()) {
      System.out.println("DataWizUserRoles : " + userProfile);
      authorities.add(new SimpleGrantedAuthority("ROLE_" + userProfile.getType()));
    }
    return authorities;
  }

}
