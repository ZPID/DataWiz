package de.zpid.datawiz.controller;

import java.sql.SQLException;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.dao.DataAccessException;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import de.zpid.datawiz.dao.UserDao;
import de.zpid.datawiz.dto.UserDTO;
import de.zpid.datawiz.dto.UserRoleDTO;

@Controller
public class LoginController {

  private static final Logger log = Logger.getLogger(LoginController.class);
  private ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("spring.xml");

  @Autowired
  private UserDao userDao;

  @Autowired
  private MessageSource messageSource;

  @ModelAttribute("UserDTO")
  public UserDTO createAdministrationForm() {
    return (UserDTO) context.getBean("UserDTO");
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
  public String loginPage(@RequestParam(value = "error", required = false) String error, ModelMap model,
      HttpServletRequest request) {
    if (log.isDebugEnabled()) {
      log.debug("execute loginPage()");
    }
    if (error != null) {
      model.put("error", getErrorMessage(request, "SPRING_SECURITY_LAST_EXCEPTION"));
    }
    return "login";
  }

  @RequestMapping(value = { "/register" }, method = RequestMethod.GET)
  public String registerDataWizUser() {
    if (log.isDebugEnabled()) {
      log.debug("execute registerDataWizUser()- GET");
    }
    return "";
  }

  @RequestMapping(value = { "/register" }, method = RequestMethod.POST)
  public String saveDataWizUser() {
    if (log.isDebugEnabled()) {
      log.debug("execute registerDataWizUser()- POST");
    }
    return "";
  }

  @RequestMapping(value = "/admin", method = RequestMethod.GET)
  public String adminPage(ModelMap model) {
    if (log.isDebugEnabled()) {
      log.debug("execute adminPage()");
    }
    model.addAttribute("user", getPrincipal());
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
    user.setState("Active");
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

  @RequestMapping(value = "/Access_Denied", method = RequestMethod.GET)
  public String accessDeniedPage(ModelMap model) {
    if (log.isDebugEnabled()) {
      log.debug("execute accessDeniedPage()");
    }
    model.addAttribute("user", getPrincipal());
    return "accessDenied";
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

  private String getErrorMessage(HttpServletRequest request, String key) {
    Exception exception = (Exception) request.getSession().getAttribute(key);
    String error = "";
    if (exception instanceof BadCredentialsException) {
      error = messageSource.getMessage("login.failed", null, LocaleContextHolder.getLocale());
    } else if (exception instanceof LockedException) {
      error = messageSource.getMessage("login.locked", null, LocaleContextHolder.getLocale());
    } else if (exception instanceof AccountExpiredException) {
      error = messageSource.getMessage("login.expired", null, LocaleContextHolder.getLocale());
    } else if (exception instanceof InternalAuthenticationServiceException) {
      error = messageSource.getMessage("login.system.error", null, LocaleContextHolder.getLocale());
    } else {
      error = messageSource.getMessage("login.failed", null, LocaleContextHolder.getLocale());
    }
    return error;
  }
}