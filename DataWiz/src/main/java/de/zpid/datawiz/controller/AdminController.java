package de.zpid.datawiz.controller;

import java.sql.SQLException;
import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import de.zpid.datawiz.dao.UserDAO;
import de.zpid.datawiz.dto.UserDTO;
import de.zpid.datawiz.dto.UserRoleDTO;

@Controller
public class AdminController {
  
  private static final Logger log = Logger.getLogger(LoginController.class);
  // private ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("spring.xml");

  @Autowired
  private UserDAO userDao;
  
  @RequestMapping(value = "/admin", method = RequestMethod.GET)
  public String adminPage(ModelMap model) {
    if (log.isDebugEnabled()) {
      log.debug("execute adminPage()");
    }
    UserDTO user = null;
    try {
      user = userDao.findByMail("123@qwe.dewf");
    } catch (DataAccessException | SQLException e1) {
      // TODO Auto-generated catch block
      e1.printStackTrace();
    }
    if (user == null) {
      user = new UserDTO();
    }
    user.setEmail("asdsdg");
    user.setPassword("123");
    user.setAccountState("Active");
    user.setFirstName("Test");
    user.setLastName("hase");
    UserRoleDTO prof = new UserRoleDTO();
    prof.setRoleId(2);
    ArrayList<UserRoleDTO> hset = new ArrayList<UserRoleDTO>();
    hset.add(prof);
    user.setGlobalRoles(hset);
    log.error("1");
    try {
      userDao.saveOrUpdate(user);
    } catch (Exception e) {
      log.warn("email not unique = " + e);
      // return "welcome";
    }
    return "admin/admin";
  }

}
