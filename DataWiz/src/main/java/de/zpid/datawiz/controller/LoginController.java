package de.zpid.datawiz.controller;

import java.nio.file.Paths;
import java.util.Optional;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
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
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.sun.jna.Platform;

import de.zpid.datawiz.dto.RecordDTO;
import de.zpid.datawiz.dto.UserDTO;
import de.zpid.datawiz.dto.UserRoleDTO;
import de.zpid.datawiz.enumeration.Roles;
import de.zpid.datawiz.util.EmailUtil;
import de.zpid.datawiz.util.UserUtil;
import de.zpid.spss.SPSSIO;

@Controller
@SessionAttributes("UserDTO")
public class LoginController extends SuperController {

  @Autowired
  PlatformTransactionManager txManager;

  @Autowired
  SPSSIO spss;

  private static Logger log = LogManager.getLogger(LoginController.class);

  public LoginController() {
    super();
    if (log.isInfoEnabled())
      log.info("Loading LoginUserController for mapping /login");
  }

  @Autowired
  private PasswordEncoder passwordEncoder;

  @ModelAttribute("UserDTO")
  public UserDTO createUserObject() {
    return (UserDTO) applicationContext.getBean("UserDTO");
  }

  /**
   * mapping to "/" to build content outside of the protected areas
   * 
   * @param model
   * @return
   */
  @RequestMapping(value = { "/", "/home" })
  public String homePage(ModelMap model) {
    if (log.isDebugEnabled()) {
      log.debug("execute homePage()");
      try {
        log.info("SPSS LIB loaded: " + spss.isLibLoaded());
        if (spss.isLibLoaded()) {
          RecordDTO spssFile;
          if (Platform.isWindows()) {
            spssFile = new RecordDTO(
                spss.readWholeSPSSFile(Paths.get("C:\\Users\\ronny\\OneDrive\\ZPID\\Test.sav").toString()));
          } else {
            spssFile = new RecordDTO(spss.readWholeSPSSFile(Paths.get("/home/rb/Downloads/Test.sav").toString()));

          }
          log.debug(spssFile);
          spssFile.getVariables().forEach((s) -> log.debug(s));
          spssFile.getErrors().forEach((s) -> System.out.println(s));
        }
      } catch (Error e) {
        System.out.println("lol1");
      } catch (Exception e) {
        System.out.println("lol2");
        e.printStackTrace();
      }
    }
    // CustomResourceLoader res = new CustomResourceLoader();
    // try {
    // res.loadResourceData();
    // } catch (IOException e) {
    // // TODO Auto-generated catch block
    // e.printStackTrace();
    // }

    model.addAttribute("greeting", "Hi, Welcome to mysite");
    return "welcome";
  }

  /**
   * Initialize the register form and throws errors to the view if something went wrong during the user login
   * 
   * @param error
   * @param model
   * @return
   */
  @RequestMapping(value = "/login", method = RequestMethod.GET)
  public String loginPage(@RequestParam(value = "error", required = false) String error, ModelMap model) {
    if (log.isDebugEnabled()) {
      log.debug("execute loginPage()");
    }
    if (error != null) {
      model.put("error", getErrorMessage("SPRING_SECURITY_LAST_EXCEPTION"));
    }
    return "login";
  }

  /**
   * Initialize the register form
   * 
   * @return
   */
  @RequestMapping(value = { "/register", "/register/{projectId}/{email}/{linkhash}" }, method = RequestMethod.GET)
  public String registerDataWizUser(ModelMap model, @PathVariable Optional<Long> projectId,
      @PathVariable Optional<String> email, @PathVariable Optional<String> linkhash) {
    if (log.isDebugEnabled()) {
      log.debug("execute registerDataWizUser()- GET");
    }
    UserDTO admin = UserUtil.getCurrentUser();
    if (admin != null) {
      log.warn("Auth User Object not null - no registration needed - redirect panel");
      return "redirect:/panel";
    }
    if (projectId.isPresent() && email.isPresent() && linkhash.isPresent()) {
      UserDTO user = createUserObject();
      user.setEmail(email.get());
      user.setSecEmail(email.get());
      user.setComments(String.valueOf(projectId.get()));
      user.setActivationCode(linkhash.get());
      model.put("UserDTO", user);
    } else {
      model.put("UserDTO", createUserObject());
    }
    return "register";
  }

  /**
   * Function for user registration. After the validation of the required form fields, the function saves the new user
   * into the database and sends an email to the given emailadress to complete registration
   * 
   * @param person
   * @param bindingResult
   * @param model
   * @return
   */
  @RequestMapping(value = { "/register" }, method = RequestMethod.POST)
  public String saveDataWizUser(@Valid @ModelAttribute("UserDTO") UserDTO person, BindingResult bindingResult,
      ModelMap model) {
    log.trace("execute registerDataWizUser()- POST {}", person);
    try {
      // password check
      if (person.getPassword() == null || person.getPassword_retyped() == null) {
        // TOTO password msg
        bindingResult.rejectValue("password", "passwords.not.equal");
        log.debug("Passwords are emtpy");
      } else if (!person.getPassword().equals(person.getPassword_retyped())) {
        bindingResult.rejectValue("password", "passwords.not.equal");
        if (log.isDebugEnabled())
          log.debug("Password and retyped password not equal!");
      }
      // GTC(AGB) check TODO TEXT
      if (!person.isCheckedGTC()) {
        bindingResult.rejectValue("checkedGTC", "email.already.exists");
        if (log.isDebugEnabled())
          log.debug("Email is already used for an account email:" + person.getEmail());
      }
      // Email exists check
      if (userDAO.findByMail(person.getEmail(), false) != null) {
        bindingResult.rejectValue("email", "email.already.exists");
        if (log.isDebugEnabled())
          log.debug("Email is already used for an account email:" + person.getEmail());
      }
      // return to registerform if errors
      if (bindingResult.hasErrors()) {
        log.trace("UserDTO has Errors - return to register form");
        return "register";
      }
      // at this point registerform is valid
      String chkmail = person.getSecEmail();
      long projectId = 0;
      try {
        projectId = (person.getComments() != null && !person.getComments().isEmpty())
            ? Long.parseLong(person.getComments()) : null;
        person.setComments(null);
      } catch (Exception e) {
        log.warn("ProjectId which is temporary stored in comments is not a number");
      }
      if (chkmail != null && !chkmail.isEmpty() && projectId > 0) {
        person.setSecEmail(null);
        if (!chkmail.equals(person.getEmail())) {
          projectDAO.updateInvitationEntree(projectId, chkmail, person.getEmail());
        }
      }
      person.setPassword(passwordEncoder.encode(person.getPassword()));
      userDAO.saveOrUpdate(person, false);
      person = userDAO.findByMail(person.getEmail(), false);
    } catch (Exception e) {
      log.error("DBS error during user registration: ", e);
      model.put("errormsg", messageSource.getMessage("dbs.sql.exception", null, LocaleContextHolder.getLocale()));
      return "error";
    }
    // registration mail
    if (person != null && person.getId() > 0) {
      try {
        EmailUtil mail = new EmailUtil(env);
        mail.sendSSLMail(person.getEmail(),
            messageSource.getMessage("reg.mail.subject", null,
                LocaleContextHolder.getLocale()),
            messageSource.getMessage("reg.mail.content",
                new Object[] {
                    request.getRequestURL().toString().replace(request.getRequestURI(), request.getContextPath()),
                    person.getEmail(), person.getActivationCode() },
                LocaleContextHolder.getLocale()));
      } catch (Exception e) {
        log.error("Mail error during user registration: ", e);
        model.put("errormsg", messageSource.getMessage("send.mail.exception", null, LocaleContextHolder.getLocale()));
        return "error";
      }
    }
    return "redirect:/login?activationmail";
  }

  /**
   * Activation endpoint which needs the email of the account which has to be activated and a random generated UUID to
   * authenticate that mail address
   * 
   * @param mail
   * @param activationCode
   * @param model
   * @return
   */
  @RequestMapping(value = "/activate/{mail}/{activationCode}", method = RequestMethod.GET)
  public String activateAccount(@PathVariable final String mail, @PathVariable final String activationCode,
      ModelMap model) {
    if (log.isDebugEnabled()) {
      log.debug("execute activateAccount email: " + mail + " code: " + activationCode);
    }
    TransactionDefinition transactionDefinition = new DefaultTransactionDefinition();
    TransactionStatus status = txManager.getTransaction(transactionDefinition);
    try {
      UserDTO user = userDAO.findByMail(mail, false);
      if (user != null && user.getActivationCode() != null && !user.getActivationCode().isEmpty()
          && user.getActivationCode().equals(activationCode)) {
        userDAO.activateUserAccount(user);
        roleDAO.saveRole(new UserRoleDTO(Roles.USER.toInt(), user.getId(), 0, 0, Roles.USER.name()));
        long pid = Long.parseLong(projectDAO.findValFromInviteData(mail, activationCode, "project_id"));
        if (pid > 0) {
          UserRoleDTO role = new UserRoleDTO(Roles.REL_ROLE.toInt(), user.getId(), pid, 0, Roles.REL_ROLE.name());
          roleDAO.saveRole(role);
          projectDAO.deleteInvitationEntree(pid, mail);
        }
      }
      txManager.commit(status);
    } catch (Exception e) {
      txManager.rollback(status);
      log.warn("DBS error during user registration: " + e);
      model.put("errormsg", messageSource.getMessage("login.failed", null, LocaleContextHolder.getLocale()));
      return "error";
    }
    return "redirect:/login?activated";
  }

  /**
   * This mapping is used if unauthenticated users try to access protected areas
   * 
   * @param model
   * @return
   */
  @RequestMapping(value = "/Access_Denied")
  public String accessDeniedPage(ModelMap model) {
    if (log.isDebugEnabled()) {
      log.debug("execute accessDeniedPage() - " + request.getHeader("referer") + " - " + request.getAuthType() + " - "
          + request.getPathInfo());
    }
    try {
      model.addAttribute("user", getPrincipal());
    } catch (Exception e) {
      return "redirect:/login";
    }
    return "accessDenied";
  }

  /**
   * Checks out the currently authenticated user from the Spring security SecurityContextLogoutHandler and deletes the
   * remember-me cookie
   * 
   * @param request
   * @param response
   * @return
   */
  @RequestMapping(value = "/logout")
  public String logout(ModelMap model, HttpServletResponse response) {
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
    model.put("UserDTO", createUserObject());
    return "redirect:/login?logout";
  }

  /**
   * Returns the name of the currently authenticated User
   * 
   * @return
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