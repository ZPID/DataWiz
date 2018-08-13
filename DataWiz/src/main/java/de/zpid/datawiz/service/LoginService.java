package de.zpid.datawiz.service;

import de.zpid.datawiz.dao.UserDAO;
import de.zpid.datawiz.dto.UserDTO;
import de.zpid.datawiz.util.CustomUserDetails;
import de.zpid.datawiz.util.EmailUtil;
import de.zpid.datawiz.util.RegexUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.regex.Pattern;


/**
 * Service class for the Login controller to separate the web logic from the business logic.
 * <p>
 * This file is part of the DataWiz distribution (https://github.com/ZPID/DataWiz).
 * Copyright (c) 2018 <a href="https://leibniz-psychology.org/">Leibniz Institute for Psychology Information (ZPID)</a>.
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3.
 * <p>
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <a href="http://www.gnu.org/licenses/">http://www.gnu.org/licenses/</a>.
 *
 * @author Ronny Boelter
 * @version 1.0
 **/
@Service("LoginService")
public class LoginService implements UserDetailsService {

    private static Logger log = LogManager.getLogger(LoginService.class);
    private final UserDAO userDAO;
    protected final MessageSource messageSource;
    private final EmailUtil mail;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public LoginService(UserDAO userDAO, MessageSource messageSource, EmailUtil mail, PasswordEncoder passwordEncoder) {
        this.userDAO = userDAO;
        this.messageSource = messageSource;
        this.mail = mail;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Load user details from the DBS, depending on the transferred mail
     *
     * @param email {@link String} email
     * @return {@link UserDetails} Details of a  user
     * @throws UsernameNotFoundException Thrown if username not in DBS
     */
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(final String email) throws UsernameNotFoundException {
        log.trace("Entering loadUserByUsername with email: [{}]", () -> email);
        UserDTO user = null;
        try {
            user = userDAO.findByMail(email, true);
        } catch (Exception e) {
            log.fatal("Exception during loadUserByUsername Message: ", () -> e);
        }
        if (user == null)
            throw new UsernameNotFoundException("User not found email=" + email);
        log.trace("Leaving loadUserByUsername with email: [{}] successfully", () -> email);
        return new CustomUserDetails(user);
    }

    /**
     * Sends a password recovery mail to the mail adress of the passed user details
     *
     * @param person  {@link UserDTO} contains the user details such as the mail address
     * @param request {@link HttpServletRequest} is used to create the correct url for the reset link
     * @return {@link String} in case of an error, error codes are returned, otherwise null
     */
    public String sendPasswordRecoveryMail(final UserDTO person, final HttpServletRequest request) {
        log.trace("Entering sendPasswordRecoveryMail with email: [{}]", person.getEmail());
        String retErr = null;
        UserDTO user = null;
        if (person.getEmail() != null && !person.getEmail().trim().isEmpty()) {
            try {
                user = userDAO.findByMail(person.getEmail(), true);
            } catch (Exception e) {
                log.fatal("Exception during sendPasswordRecoveryMail Message[{}]]", e::getMessage);
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
                                            new Object[]{request.getRequestURL().toString().replace(request.getRequestURI(),
                                                    request.getContextPath()), person.getEmail(), person.getActivationCode()},
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
                            new Object[]{
                                    request.getRequestURL().toString().replace(request.getRequestURI(), request.getContextPath()),
                                    user.getEmail(), user.getPassword().replaceAll("[^a-zA-Z0-9]", "")},
                            LocaleContextHolder.getLocale()))) {
                retErr = "reset.password.email.error";
            }
        }
        log.trace("Leaving sendPasswordRecoveryMail with email: [{}] with errer [{}]", person.getEmail(), retErr);
        return retErr;
    }

    /**
     * Validates the passed password
     *
     * @param person {@link UserDTO} contains the user details such as the password
     * @param retMSG {@link List} of {@link String} with the possible validation error messages. More than one is possible
     * @return mapping to password.jsp on error, otherwise redirect to login
     */
    public String validateAndSavePassword(final UserDTO person, List<String> retMSG) {
        log.trace("Entering validateAndSavePassword with email: [{}]", person.getEmail());
        String ret = "redirect:/login";
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
        log.trace("Leaving validateAndSavePassword with email: [{}] with mapping [{}]", person.getEmail(), ret);
        return ret;
    }

    /**
     * Validates the reset password form
     *
     * @param email {@link String} email
     * @param code  {@link String} auto generated code
     * @return {@link String} Error or Success message
     */
    public String setPasswordResetForm(final String email, final String code) {
        log.trace("Entering validateAndSavePassword with email: [{}]", () -> email);
        UserDTO user;
        String retErr;
        if (!email.isEmpty() && !code.isEmpty()) {
            try {
                user = userDAO.findByMail(email, true);
            } catch (Exception e) {
                log.fatal("Exception during showSetPassword Message: ", () -> e);
                return "dbs.sql.exception";
            }
            if (user != null) {
                if (code.equals(user.getPassword().replaceAll("[^a-zA-Z0-9]", ""))) {
                    retErr = "reset.password.email.link.success";
                } else {
                    retErr = "reset.password.linkhash.failure";
                }
            } else {
                retErr = "reset.password.user.empty";
            }
        } else {
            retErr = "reset.password.user.empty";
        }
        log.trace("Leaving validateAndSavePassword with email: [{}] with error [{}]", () -> email, () -> retErr);
        return retErr;
    }

    /**
     * Sends an email using the mail utils sendSSLMail function
     *
     * @param email   {@link String} email address
     * @param subject {@link String} email subject
     * @param content {@link String} email content
     * @return true if mail was send, otherwise false
     */
    private boolean sendMail(final String email, final String subject, final String content) {
        log.trace("Entering sendMail with email: [{}]", () -> email);
        try {
            mail.sendSSLMail(email, subject, content);
        } catch (Exception e) {
            log.error("sendSSLMail ERROR for [email: {}; subject: {}; content: {}] Exception: ", () -> email, () -> subject,
                    () -> content, () -> e);
            return false;
        }
        log.trace("Entering sendMail with email: [{}] successfully", () -> email);
        return true;
    }

}
