package de.zpid.datawiz.controller;

import java.sql.SQLException;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import de.zpid.datawiz.dao.UserDAO;
import de.zpid.datawiz.dto.UserDTO;
import de.zpid.datawiz.util.Roles;

@Controller
public class LoginController {

  private static final Logger log = Logger.getLogger(LoginController.class);
  private ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("spring.xml");

  @Autowired
  private UserDAO userDao;

  @Autowired
  private PasswordEncoder passwordEncoder;

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
    return "register";
  }

  @RequestMapping(value = { "/register" }, method = RequestMethod.POST)
  public String saveDataWizUser(@Valid @ModelAttribute("UserDTO") UserDTO person, BindingResult bindingResult,
      ModelMap model) {
    if (log.isDebugEnabled())
      log.debug("execute registerDataWizUser()- POST");
    try {
      // password check
      if (!person.getPassword().equals(person.getPassword_retyped())) {
        bindingResult.rejectValue("password", "passwords.not.equal");
        if (log.isDebugEnabled())
          log.debug("Password and retyped password not equal!");
      }
      // GTC(AGB) check
      if (!person.isCheckedGTC()) {
        bindingResult.rejectValue("checkedGTC", "email.already.exists");
        if (log.isDebugEnabled())
          log.debug("Email is already used for an account email:" + person.getEmail());
      }
      // Email exists check
      if (userDao.findByMail(person.getEmail(), false) != null) {
        bindingResult.rejectValue("email", "email.already.exists");
        if (log.isDebugEnabled())
          log.debug("Email is already used for an account email:" + person.getEmail());
      }
      // return to registerform if errors
      if (bindingResult.hasErrors()) {
        return "register";
      }
      // at this point registerform is valid
      person.setPassword(passwordEncoder.encode(person.getPassword()));
      userDao.saveOrUpdate(person, false);
    } catch (DataAccessException | SQLException e) {
      log.warn("DBS error during user registration: " + e.getStackTrace());
      model.put("errormsg", messageSource.getMessage("login.failed", null, LocaleContextHolder.getLocale()));
      return "error";
    }
    person.setPassword("");
    person.setPassword_retyped("");
    return "register_email";
  }

  @RequestMapping(value = "/activate/{mail}/{activationCode}", method = RequestMethod.GET)
  public String activateAccount(@PathVariable String mail, @PathVariable String activationCode, ModelMap model) {
    if (log.isDebugEnabled()) {
      log.debug("execute activateAccount email: " + mail + " code: " + activationCode);
    }
    try {
      UserDTO user = userDao.findByMail(mail, false);
      if (user != null && user.getActivationCode() != null && !user.getActivationCode().isEmpty()
          && user.getActivationCode().equals(activationCode)) {
        userDao.activateUserAccount(user);
        userDao.setRole(user.getId(), 0, Roles.USER.toInt());
      }
    } catch (DataAccessException | SQLException e) {
      log.warn("DBS error during user registration: " + e.getStackTrace());
      model.put("errormsg", messageSource.getMessage("login.failed", null, LocaleContextHolder.getLocale()));
      return "error";
    }
    return "redirect:/login?activated";
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