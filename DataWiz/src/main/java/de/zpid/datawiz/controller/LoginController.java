package de.zpid.datawiz.controller;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ClassPathXmlApplicationContext;
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
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import de.zpid.datawiz.dto.UserDTO;

@Controller
public class LoginController {

  private static final Logger log = Logger.getLogger(LoginController.class);
  private ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("spring.xml");

//  @Autowired
//  private UserDAO userDao;

  @Autowired
  private MessageSource messageSource;

  @Autowired
  private HttpServletRequest request;

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

  @RequestMapping(value = "/login")
  public String loginPage(@RequestParam(value = "error", required = false) String error, ModelMap model) {
    if (log.isDebugEnabled()) {
      log.debug("execute loginPage()");
    }
    if (error != null) {
      model.put("error", getErrorMessage("SPRING_SECURITY_LAST_EXCEPTION"));
    }
    return "login";
  }

  @RequestMapping(value = { "/register" }, method = RequestMethod.GET)
  public String registerDataWizUser() {
    if (log.isDebugEnabled()) {
      log.debug("execute registerDataWizUser()- GET");
    }
    return "register_edit";
  }

  @RequestMapping(value = { "/register" }, method = RequestMethod.POST)
  public String saveDataWizUser(@Valid @ModelAttribute("UserDTO") UserDTO person, BindingResult bindingResult) {
    if (log.isDebugEnabled()) {
      log.debug("execute registerDataWizUser()- POST");
    }
    if (bindingResult.hasErrors()) {
      return "register_edit";
    }
    return "register_edit";
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
  public String logout(HttpServletRequest request, HttpServletResponse response) {
    if (log.isDebugEnabled()) {
      log.debug("execute logoutPage()");
    }
    String cookieName = "remember-me";
    Cookie cookie = new Cookie(cookieName, null);
    cookie.setMaxAge(0);
    cookie.setPath(StringUtils.hasLength(request.getContextPath()) ? request.getContextPath() : "/");
    response.addCookie(cookie);
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth != null) {
      new SecurityContextLogoutHandler().logout(request, response, auth);
    }
    return "redirect:/login?logout";
  }

  /**
   * 
   * @return Username of authenticated User
   */
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

  /**
   * Returns custom messages for Login Exceptions - Checks BadCredentialsException, LockedException,
   * AccountExpiredException and InternalAuthenticationServiceException. Input String
   * 
   * @param key
   *          SessionParameterKey
   * @return Custom ErrorMessage
   */
  private String getErrorMessage(String key) {
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