package de.zpid.datawiz.service;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Scope;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import de.zpid.datawiz.dao.UserDAO;
import de.zpid.datawiz.dto.UserDTO;
import de.zpid.datawiz.util.CustomUserDetails;
import de.zpid.datawiz.util.EmailUtil;
import de.zpid.datawiz.util.RegexUtil;

@Component("LoginService")
@Scope("singleton")
public class LoginService implements UserDetailsService {

  private static Logger log = LogManager.getLogger(LoginService.class);
  @Autowired
  private UserDAO userDAO;
  @Autowired
  protected MessageSource messageSource;
  @Autowired
  private EmailUtil mail;
  @Autowired
  protected PasswordEncoder passwordEncoder;

  @Transactional(readOnly = true)
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    if (log.isTraceEnabled())
      log.trace("execute loadUserByUsername with email: [{}]", () -> email);
    UserDTO user = null;
    try {
      user = userDAO.findByMail(email, true);
    } catch (Exception e) {
      log.fatal("Exception during loadUserByUsername Message: ", () -> e);
    }
    if (user == null)
      throw new UsernameNotFoundException("User not found email=" + email);
    return new CustomUserDetails(user);
  }

  /**
   * @param person
   * @param request
   * @return
   */
  public String sendPasswordRecoveryMail(UserDTO person, HttpServletRequest request) {
    String retErr = null;
    if (person != null) {
      UserDTO user = null;
      if (person.getEmail() != null && !person.getEmail().trim().isEmpty()) {
        try {
          user = userDAO.findByMail(person.getEmail(), true);
        } catch (SQLException e) {
          log.fatal("Exception during sendPasswordRecoveryMail Message[{}]]", () -> e.getMessage());
          retErr = "dbs.sql.exception";
        }
      } else {
        retErr = "reset.password.email.emtpy";
      }
      if (user == null || user.getEmail() == null || user.getEmail().isEmpty()) {
        retErr = "reset.password.no.account";
      } else {
        if (user.getAccountState().equals("LOCKED") && user.getActivationCode().isEmpty()) {
          retErr = "login.locked";
        } else if (user.getAccountState().equals("LOCKED") && !user.getActivationCode().isEmpty()) {
          if (user.getEmail() != null && !user.getEmail().isEmpty())
            if (sendMail(user.getEmail(),
                messageSource.getMessage("reg.mail.subject", null, LocaleContextHolder.getLocale()),
                messageSource
                    .getMessage("reg.mail.content",
                        new Object[] { request.getRequestURL().toString().replace(request.getRequestURI(),
                            request.getContextPath()), person.getEmail(), person.getActivationCode() },
                        LocaleContextHolder.getLocale()))) {
              retErr = "reset.password.notactivated.send";
            } else {
              retErr = "reset.password.notactivated.notsend";
            }
        } else if (user.getAccountState().equals("EXPIRED")) {
          retErr = "login.expired";
        }
      }
      if (retErr == null) {
        String sendTo = user.getEmail();
        retErr = "reset.password.email.send";
        if (person.getSecEmail() != null && !person.getSecEmail().isEmpty() && person.getSecEmail().equals("second")) {
          if ((user.getSecEmail() == null || user.getSecEmail().isEmpty()))
            retErr = "reset.password.no.secemail";
          else {
            sendTo = user.getSecEmail();
            person.setEmail(user.getSecEmail());
          }
        }
        if (!sendMail(sendTo,
            messageSource.getMessage("reset.password.mail.subject", null, LocaleContextHolder.getLocale()),
            messageSource.getMessage("reset.password.mail.content",
                new Object[] {
                    request.getRequestURL().toString().replace(request.getRequestURI(), request.getContextPath()),
                    user.getEmail(), user.getPassword().replaceAll("[^a-zA-Z0-9]", "") },
                LocaleContextHolder.getLocale()))) {
          retErr = "reset.password.email.error";
        }
      }
    } else {
      retErr = "reset.password.internal.error";
    }
    return retErr;
  }

  /**
   * @param person
   * @param retMSG
   * @return
   */
  public String validateAndSavePassword(final UserDTO person, List<String> retMSG) {
    String ret = "redirect:/login";
    if (person != null) {
      if (person.getPassword() == null || person.getPassword().isEmpty()) {
        ret = "password";
        retMSG.add(0, "reset.passwort.passwd.not.set");
      } else if (person.getPassword_retyped() == null || person.getPassword_retyped().isEmpty()) {
        ret = "password";
        retMSG.add(0, "reset.passwort.retypepasswd.not.set");
      } else if (!person.getPassword().equals(person.getPassword_retyped())) {
        ret = "password";
        retMSG.add(0, "passwords.not.equal");
      } else if (person.getPassword().length() < 8) {
        ret = "password";
        retMSG.add(0, "passwords.too.short");
      } else if (!Pattern.compile(RegexUtil.PASSWORDREGEX).matcher(person.getPassword()).find()) {
        ret = "password";
        retMSG.add(0, "reset.password.too.easy");
      } else {
        try {
          person.setPassword(passwordEncoder.encode(person.getPassword()));
          userDAO.updatePassword(person);
          retMSG.add(0, "reset.password.success");
        } catch (Exception e) {
          log.fatal("DBS Exception during validateAndSavePassword Message: ", () -> e);
          retMSG.add(0, e.getMessage());
          ret = "error";
        }
      }
    }
    return ret;
  }

  /**
   * @param email
   * @param code
   * @return
   */
  public String setPasswordResetForm(final Optional<String> email, final Optional<String> code) {
    UserDTO user = null;
    String retErr = null;
    if (email.isPresent() && code.isPresent()) {
      try {
        user = userDAO.findByMail(email.get(), true);
      } catch (SQLException e) {
        log.fatal("Exception during showSetPassword Message: ", () -> e);
        retErr = "dbs.sql.exception";
      }
      if (user != null) {
        if (code.get().equals(user.getPassword().replaceAll("[^a-zA-Z0-9]", ""))) {
          retErr = "reset.password.email.link.success";
        } else {
          retErr = "reset.password.linkhash.failure";
        }
      } else {
        retErr = "reset.password.user.emtpy";
      }
    } else {
      retErr = "reset.password.email.emtpy";
    }
    return retErr;
  }

  /**
   * @param email
   * @param subject
   * @param content
   */
  private boolean sendMail(String email, String subject, String content) {
    try {
      mail.sendSSLMail(email, subject, content);
    } catch (Exception e) {
      log.error("sendSSLMail ERROR for [email: {}; subject: {}; content: {}] Exception: ", () -> email, () -> subject,
          () -> content, () -> e);
      return false;
    }
    return true;
  }

}
