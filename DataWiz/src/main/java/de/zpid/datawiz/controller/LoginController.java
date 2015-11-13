package de.zpid.datawiz.controller;

import java.util.HashSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import de.zpid.datawiz.dao.UserDao;
import de.zpid.datawiz.model.DataWizUser;
import de.zpid.datawiz.model.DataWizUserRoles;

@Controller
public class LoginController {

  private static final Logger log = Logger.getLogger(LoginController.class);
  private ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("spring.xml");

  @Autowired
  private UserDao userDao;

  @ModelAttribute("DataWizUser")
  public DataWizUser createAdministrationForm() {
    return (DataWizUser) context.getBean("DataWizUser");
  }

  @RequestMapping(value = { "/", "/home" }, method = RequestMethod.GET)
  public String homePage(ModelMap model) {
    if (log.isDebugEnabled()) {
      log.debug("execute homePage()");
    }
    model.addAttribute("greeting", "Hi, Welcome to mysite");
    return "welcome";
  }

  @RequestMapping(value = "/login", method = RequestMethod.GET)
  public String loginPage() {
    if (log.isDebugEnabled()) {
      log.debug("execute loginPage()");
    }
    return "login";
  }

  @RequestMapping(value = "/admin", method = RequestMethod.GET)
  public String adminPage(ModelMap model) {
    if (log.isDebugEnabled()) {
      log.debug("execute adminPage()");
    }
    model.addAttribute("user", getPrincipal());
    DataWizUser user = userDao.findByMail("123@qwe.dewf");
    if (user == null) {
      user = new DataWizUser();
    }
    user.setEmail("asdsdg");
    user.setPassword("123");
    user.setState("Active");
    user.setFirstName("Test");
    user.setLastName("hase");
    DataWizUserRoles prof = new DataWizUserRoles();
    prof.setId(2);
    HashSet<DataWizUserRoles> hset = new HashSet<DataWizUserRoles>();
    hset.add(prof);
    user.setUserProfiles(hset);
    log.error("1");
    try {
      userDao.saveOrUpdate(user);
    } catch (Exception e) {
      log.warn("email not unique = " + e);
      //return "welcome";
    }
    return "admin/admin";
  }

  @RequestMapping(value = "/Access_Denied", method = RequestMethod.GET)
  public String accessDeniedPage(ModelMap model) {
    if (log.isDebugEnabled()) {
      log.debug("execute accessDeniedPage()");
    }
    model.addAttribute("user", getPrincipal());
    return "accessDenied";
  }

  @RequestMapping(value = "/admin/{qw}", method = RequestMethod.GET)
  public String loginPage2(@PathVariable String qw) {
    System.out.println(qw);
    return "login";
  }

  @RequestMapping(value = "/logout", method = RequestMethod.GET)
  public String logoutPage(HttpServletRequest request, HttpServletResponse response) {
    if (log.isDebugEnabled()) {
      log.debug("execute logoutPage()");
    }
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth != null) {
      new SecurityContextLogoutHandler().logout(request, response, auth);
    }
    return "redirect:/login?logout";
  }

  private String getPrincipal() {
    String userName = null;
    Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    if (principal instanceof UserDetails) {
      userName = ((UserDetails) principal).getUsername();
    } else {
      userName = principal.toString();
    }
    return userName;
  }
}