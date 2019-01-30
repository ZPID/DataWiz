package de.zpid.datawiz.controller;

import de.zpid.datawiz.dao.ProjectDAO;
import de.zpid.datawiz.dao.RoleDAO;
import de.zpid.datawiz.dao.UserDAO;
import de.zpid.datawiz.dto.UserDTO;
import de.zpid.datawiz.dto.UserRoleDTO;
import de.zpid.datawiz.enumeration.PageState;
import de.zpid.datawiz.enumeration.Roles;
import de.zpid.datawiz.util.CaptchaUtil;
import de.zpid.datawiz.service.LoginService;
import de.zpid.datawiz.util.BreadCrumbUtil;
import de.zpid.datawiz.util.ClientInfo;
import de.zpid.datawiz.util.EmailUtil;
import de.zpid.datawiz.util.UserUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.env.Environment;
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
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


/**
 * This controller handles the user login and registration, and the mapping to the sides that do not require a login.
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
 * <p>
 * TODO Service layer: to separate the DBS logic from the web logic!
 **/
@Controller
@SessionAttributes("UserDTO")
public class LoginController {

    private static final Logger log = LogManager.getLogger(LoginController.class);
    private final PlatformTransactionManager txManager;
    private final EmailUtil mail;
    private final LoginService loginService;
    private final MessageSource messageSource;
    private final ClassPathXmlApplicationContext applicationContext;
    private final Environment env;
    private final HttpServletRequest request;
    private final EmailUtil emailUtil;
    private final ProjectDAO projectDAO;
    private final RoleDAO roleDAO;
    private final UserDAO userDAO;
    private final PasswordEncoder passwordEncoder;
    private final ClientInfo clientInfo;
    private final CaptchaUtil captchaUtil;


    @Autowired
    public LoginController(final PlatformTransactionManager txManager, final EmailUtil mail, final LoginService loginService,
                           final MessageSource messageSource, final ClassPathXmlApplicationContext applicationContext,
                           final Environment env, final HttpServletRequest request, final EmailUtil emailUtil,
                           final ProjectDAO projectDAO, final RoleDAO roleDAO, final UserDAO userDAO, final PasswordEncoder passwordEncoder,
                           final ClientInfo clientInfo, final CaptchaUtil captchaUtil) {
        super();
        log.info("Loading LoginUserController for mapping /login");
        this.txManager = txManager;
        this.mail = mail;
        this.loginService = loginService;
        this.messageSource = messageSource;
        this.applicationContext = applicationContext;
        this.env = env;
        this.request = request;
        this.emailUtil = emailUtil;
        this.projectDAO = projectDAO;
        this.roleDAO = roleDAO;
        this.userDAO = userDAO;
        this.passwordEncoder = passwordEncoder;
        this.clientInfo = clientInfo;
        this.captchaUtil = captchaUtil;

    }

    @ModelAttribute("UserDTO")
    public UserDTO createUserDTO() {
        return (UserDTO) applicationContext.getBean("UserDTO");
    }

    /**
     * This function handles the calls to the welcome.jsp and do not require a logged in user
     *
     * @param model {@link ModelMap}
     * @return Mapping to welcome.jsp
     */
    @RequestMapping(value = {"/", "/home"})
    public String homePage(final ModelMap model) {
        log.trace("Entering homePage");
        model.put("breadcrumbList", "");
        return "welcome";
    }

    /**
     * This function initializes the login form and forwards errors if something went wrong during the user login
     *
     * @param error {@link String} if an error occurred during login
     * @param model {@link ModelMap}
     * @return Mapping to login.jsp
     */
    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String loginPage(@RequestParam(value = "error", required = false) String error, ModelMap model) {
        log.trace("Entering loginPage " + (error != null ? "with login error" : ""));
        if (error != null) {
            model.put("error", getErrorMessage());
        }
        model.put("breadcrumbList", BreadCrumbUtil.generateBC(PageState.LOGIN, null, null, messageSource));
        return "login";
    }

    /**
     * This function initializes the register form in two different ways. The first is when the user clicks on register via DataWiz.
     * The second option is when a user is invited to a project but not yet registered in the system.
     * Then the user will receive an e-mail with a link to the registration form. This link contains information such as
     * the ID of the project to which it was submitted and the email address used for the invitation, as well as a hash to validate the data.
     *
     * @param model     {@link ModelMap}
     * @param projectId Project Identifier as {@link Optional}&lt;{@link Long}&gt;
     * @param email     E-Mail as {@link Optional}&lt;{@link String}&gt;
     * @param linkhash  Hash-Value as {@link Optional}&lt;{@link String}&gt;
     * @return Mapping to register.jsp or if an user is already logged on, redirect mapping to panel
     */
    @RequestMapping(value = {"/register", "/register/{projectId}/{email}/{linkhash}"}, method = RequestMethod.GET)
    public String registerDataWizUser(final ModelMap model, @PathVariable final Optional<Long> projectId, @PathVariable final Optional<String> email,
                                      @PathVariable final Optional<String> linkhash) {
        String ret = "register";
        if (UserUtil.getCurrentUser() != null) {
            log.trace("Entering registerDataWizUser with logged-in user");
            ret = "redirect:/panel";
        } else {
            if (projectId.isPresent() && email.isPresent() && linkhash.isPresent()) {
                log.trace("Entering registerDataWizUser from registration email with [pid: {}; email: {}; linkHash:{ }]", projectId::get, email::get, linkhash::get);
                UserDTO user = createUserDTO();
                user.setEmail(email.get());
                user.setSecEmail(email.get());
                user.setComments(String.valueOf(projectId.get()));
                user.setActivationCode(linkhash.get());
                model.put("UserDTO", user);
            } else {
                log.trace("Entering registerDataWizUser");
                model.put("UserDTO", createUserDTO());
            }
            model.put("breadcrumbList", BreadCrumbUtil.generateBC(PageState.REGISTER, null, null, messageSource));
        }
        log.trace("Leaving registerDataWizUser with mapping to {}", ret);
        return ret;
    }

    /**
     * This function evaluates the received data from the user registration form. If the information entered is correct
     * and no constrains are violated, the data will be stored in the database and the user will receive an email with a
     * confirmation link.
     *
     * @param person        {@link UserDTO} includes the registration information
     * @param bindingResult {@link BindingResult} for Validation handling
     * @param model         {@link ModelMap}
     * @return Redirect mapping to login with success message on success, mapping to register.jsp on validation errors or mapping to error.jsp on db or mail errors
     */
    @RequestMapping(value = {"/register"}, method = RequestMethod.POST)
    public String saveDataWizUser(@Valid @ModelAttribute("UserDTO") final UserDTO person, final BindingResult bindingResult, final ModelMap model) {
        log.trace("Entering saveDataWizUser for new user [email: {}]", person::getEmail);
        String ret;
        String response = request.getParameter("g-recaptcha-response");
        if (env.getRequiredProperty("google.recaptcha.enabled").equals("true")) {
            switch (captchaUtil.processResponse(response)) {
                case CAPTCHA_EMPTY:
                    log.debug("Error Captcha Empty for User [{}]", person::getEmail);
                    model.addAttribute("captcha_err", messageSource.getMessage("captcha.error.empty", null, LocaleContextHolder.getLocale()));
                    bindingResult.reject("captcha.error.empty");
                    break;
                case CAPTCHA_FAILURE:
                    log.warn("Error Captcha Failure for User [{}]", person::getEmail);
                    model.addAttribute("captcha_err", messageSource.getMessage("captcha.error.failure", null, LocaleContextHolder.getLocale()));
                    bindingResult.reject("captcha.error.failure");
                    break;
                case CAPTCHA_OK:
                    log.debug("Captcha successful for User [{}]", person::getEmail);
                    break;
            }
        }
        if (person.getPassword() == null || person.getPassword_retyped() == null) {
            bindingResult.rejectValue("password", "passwords.not.equal");
        } else if (!person.getPassword().equals(person.getPassword_retyped())) {
            bindingResult.rejectValue("password", "passwords.not.equal");
        }
        if (!person.isCheckedGTC()) {
            bindingResult.rejectValue("checkedGTC", "register.gtc.net.set");
        }
        try {
            if (userDAO.findByMail(person.getEmail(), false) != null) {
                bindingResult.rejectValue("email", "email.already.exists");
            }
        } catch (Exception e) {
            log.fatal("DBS error during user registration: ", () -> e);
            bindingResult.rejectValue("email", "dbs.sql.exception");
        }
        if (emailUtil.isFakeMail(person.getEmail()))
            bindingResult.rejectValue("email", "error.email.fake");
        if (bindingResult.hasErrors()) {
            log.debug("UserDTO has Errors - return to register form");
            model.put("breadcrumbList", BreadCrumbUtil.generateBC(PageState.REGISTER, null, null, messageSource));
            ret = "register";
        } else {
            try {
                long projectId = 0;
                try {
                    projectId = (person.getComments() != null && !person.getComments().isEmpty()) ? Long.parseLong(person.getComments()) : -1;
                    person.setComments(null);
                } catch (Exception e) {
                    log.debug("ProjectId which is temporary stored in comments is not a number : {}", person.getComments());
                }
                System.err.println(person.getSecEmail());
                if (person.getSecEmail() != null && !person.getSecEmail().isEmpty() && projectId > 0) {
                    if (!person.getSecEmail().equals(person.getEmail())) {
                        projectDAO.updateInvitationEntity(projectId, person.getSecEmail(), person.getEmail());
                    }
                    person.setSecEmail(null);
                }
                person.setPassword(passwordEncoder.encode(person.getPassword()));
                userDAO.saveOrUpdate(person, false);
                UserDTO personDB = userDAO.findByMail(person.getEmail(), false);
                mail.sendSSLMail(personDB.getEmail(), messageSource.getMessage("reg.mail.subject", null, LocaleContextHolder.getLocale()),
                        messageSource.getMessage("reg.mail.content", new Object[]{
                                        request.getRequestURL().toString().replace(request.getRequestURI(), request.getContextPath()), personDB.getEmail(), personDB.getActivationCode()},
                                LocaleContextHolder.getLocale()));
                ret = "redirect:/login?activationmail";
            } catch (Exception e) {
                log.fatal("DBS or Mail error during user registration: ", () -> e);
                model.put("errormsg", messageSource.getMessage("dbs.sql.exception", null, LocaleContextHolder.getLocale()));
                ret = "error";
            }
        }
        log.trace("Leaving saveDataWizUser with mapping to {}", ret);
        return ret;
    }

    /**
     * This function is called from invitation mail link. It needs the email of the account which has to be activated and
     * a random generated UUID to authenticate that mail address
     *
     * @param mail           {@link String} contains the email address for the user which hast du be activated
     * @param activationCode {@link String} contains a generated code to authenticate the user
     * @param model          {@link ModelMap}
     * @return Redirect mapping to login on success, otherwise mapping to error.jsp
     */
    @RequestMapping(value = "/activate/{mail}/{activationCode}", method = RequestMethod.GET)
    public String activateAccount(@PathVariable final String mail, @PathVariable final String activationCode, final ModelMap model) {
        log.trace("Entering activateAccount for user [mail: {}] with [code: {}]", () -> mail, () -> activationCode);
        String ret;
        TransactionStatus status = txManager.getTransaction(new DefaultTransactionDefinition());
        try {
            UserDTO user = userDAO.findByMail(mail, false);
            if (user != null && user.getActivationCode() != null && !user.getActivationCode().isEmpty() && user.getActivationCode().equals(activationCode)) {
                userDAO.activateUserAccount(user);
                roleDAO.saveRole(new UserRoleDTO(Roles.USER.toInt(), user.getId(), 0, 0, Roles.USER.name()));
                long pid = Long.parseLong(projectDAO.findValFromInviteData(mail, activationCode, "project_id"));
                if (pid > 0) {
                    UserRoleDTO role = new UserRoleDTO(Roles.REL_ROLE.toInt(), user.getId(), pid, 0, Roles.REL_ROLE.name());
                    roleDAO.saveRole(role);
                    projectDAO.deleteInvitationEntity(pid, mail);
                }
            }
            txManager.commit(status);
            ret = "redirect:/login?activated";
        } catch (Exception e) {
            txManager.rollback(status);
            log.fatal("DBS error during user registration: ", () -> e);
            model.put("errormsg", messageSource.getMessage("login.failed", null, LocaleContextHolder.getLocale()));
            ret = "error";
        }
        log.trace("Leaving activateAccount with mapping to [{}]", ret);
        return ret;
    }

    /**
     * This function is called if a user tries to access an area to which he has no authorization.
     * TODO beautify accessDenied.jsp
     *
     * @param model {@link ModelMap}
     * @return Mapping to accessDenied.jsp
     */
    @RequestMapping(value = "/Access_Denied")
    public String accessDeniedPage(final ModelMap model) {
        log.error("Entering accessDeniedPage from[url:{}; user: {}; client: {}", request::getContextPath, () -> getPrincipal(), () -> clientInfo.getClientInfo(request));
        try {
            model.addAttribute("user", getPrincipal());
        } catch (Exception e) {
            return "redirect:/login";
        }
        model.addAttribute("exceptionTitle", messageSource.getMessage("error.404.title", null, LocaleContextHolder.getLocale()));
        model.addAttribute("errormsg", messageSource.getMessage("error.404.msg", new Object[]{env.getRequiredProperty("organisation.admin.email")}, LocaleContextHolder.getLocale()));
        model.addAttribute("exception", "Sorry, You are not allowed to access this page!");
        return "error";
    }

    /**
     * This function initializes the password request form
     *
     * @param model {@link ModelMap}
     * @return Mapping to password.jsp
     */
    @RequestMapping(value = "/login/passwordrequest", method = RequestMethod.GET)
    public String requestResetPassword(final ModelMap model) {
        log.trace("Entering requestResetPassword");
        model.put("setemailview", true);
        model.put("UserDTO", createUserDTO());
        return "password";
    }

    /**
     * This function is the first step of the password recovery process. The mail, or alternative mail address of an user
     * account has to be submitted to this function. If an user is found which belongs to the passed mail, a mail is sent
     * to this address which includes instructions for the next step.
     *
     * @param person {@link UserDTO} contains the mail address to which the mail has to be sent
     * @param model  {@link ModelMap}
     * @return Mapping to password.jsp with a success or error message.
     */
    @RequestMapping(value = "/login/passwordrequest", method = RequestMethod.POST)
    public String sendPasswordResetRequest(@ModelAttribute("UserDTO") final UserDTO person, final ModelMap model) {
        log.trace("Entering sendPasswordResetRequest for user[mail: {}]", person::getEmail);
        String ret = "password";
        String retErr = loginService.sendPasswordRecoveryMail(person, request);
        if (retErr != null && retErr.equals("dbs.sql.exception")) {
            ret = "error";
            model.put("errormsg",
                    messageSource.getMessage(retErr, new Object[]{env.getRequiredProperty("organisation.admin.email"), ""}, LocaleContextHolder.getLocale()));
        } else if (retErr != null && (retErr.equals("reset.password.no.secemail") || retErr.equals("reset.password.email.send"))) {
            model.put("successMSG", messageSource.getMessage(retErr, new Object[]{person.getEmail()}, LocaleContextHolder.getLocale()));
            model.put("setemailview", true);
            model.put("sendSuccess", true);
        } else {
            if (retErr != null)
                model.put("errorMSG", messageSource.getMessage(retErr,
                        new Object[]{person.getEmail(), env.getRequiredProperty("organisation.admin.email"), ""}, LocaleContextHolder.getLocale()));
            model.put("setemailview", true);
        }
        log.trace("Leaving sendPasswordResetRequest with mapping [{}]", ret);
        return ret;
    }

    /**
     * This function is the second step of the password recovery process and it is called from the mail, which is sent
     * from the sendPasswordResetRequest function. It initializes the form where the user can set its new password.
     *
     * @param model {@link ModelMap}
     * @param email {@link String} contains the email address for the account for which the password has to be reset
     * @param code  {@link String} contains a generated code to authenticate the user
     * @return Mapping to password.jsp with success message if the link was valid, otherwise with an error message
     */
    @RequestMapping(value = {"/login/resetpwd/{email}/{code}"})
    public String showSetPassword(ModelMap model, @PathVariable final Optional<String> email, @PathVariable final Optional<String> code) {
        log.trace("Entering showSetPassword for user: [{}]", () -> email);
        String ret = "password";
        String retErr = loginService.setPasswordResetForm(email.orElse(""), code.orElse(""));
        if (retErr == null || retErr.equals("reset.password.email.link.success")) {
            if (retErr != null)
                model.put("successMSG", messageSource.getMessage(retErr, null, LocaleContextHolder.getLocale()));
            model.put("setemailview", false);
        } else if (retErr.equals("dbs.sql.exception")) {
            ret = "error";
            model.put("errormsg",
                    messageSource.getMessage(retErr, new Object[]{env.getRequiredProperty("organisation.admin.email"), ""}, LocaleContextHolder.getLocale()));
        } else {
            model.put("errorMSG", messageSource.getMessage(retErr, null, LocaleContextHolder.getLocale()));
            model.put("setemailview", true);
            model.put("sendSuccess", true);
        }
        log.trace("Leaving showSetPassword with mapping [{}]", ret);
        return ret;
    }

    /**
     * This function is the last step of the password recovery process. It validates and saves the new password for the passed user.
     *
     * @param person             {@link UserDTO} contains the mail address to which the mail has to be sent
     * @param model              {@link ModelMap}
     * @param redirectAttributes {@link RedirectAttributes}
     * @return Redirect mapping to login on success, to error.jsp on db errors, or to password.jsp on other errors
     */
    @RequestMapping(value = "/login/passwordrequest", method = RequestMethod.POST, params = "setPassword")
    public String saveNewPassword(@ModelAttribute("UserDTO") final UserDTO person, final ModelMap model, final RedirectAttributes redirectAttributes) {
        log.trace("Entering saveNewPassword for user [mail {}]", person::getEmail);
        List<String> retMSG = new ArrayList<>();
        String ret = loginService.validateAndSavePassword(person, retMSG);
        switch (ret) {
            case "redirect:/login":
                redirectAttributes.addFlashAttribute("successMSG",
                        messageSource.getMessage(retMSG.get(0), new Object[]{person.getEmail()}, LocaleContextHolder.getLocale()));
                break;
            case "error":
                model.put("errormsg",
                        messageSource.getMessage("dbs.sql.exception",
                                new Object[]{env.getRequiredProperty("organisation.admin.email"), retMSG.get(0).replaceAll("\n", "").replaceAll("\"", "\'")},
                                LocaleContextHolder.getLocale()));
                break;
            default:
                model.put("setemailview", false);
                model.put("errorMSG", messageSource.getMessage(retMSG.get(0), null, LocaleContextHolder.getLocale()));
                break;
        }
        log.trace("Leaving saveNewPassword with mapping [{}]", ret);
        return ret;
    }

    /**
     * Checks out the currently authenticated user from the Spring security SecurityContextLogoutHandler and deletes the remember-me cookie
     *
     * @param response {@link HttpServletResponse}
     * @return Redirect mapping to redirect:/login?logout
     */
    @RequestMapping(value = "/logout")
    public String logout(ModelMap model, HttpServletResponse response) {
        if (log.isTraceEnabled()) {
            log.trace("execute logoutPage()");
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
        model.put("UserDTO", createUserDTO());
        return "redirect:/login?logout";
    }

    /**
     * Returns the name of the currently authenticated User
     *
     * @return User name as {@link String}
     */
    private String getPrincipal() {
        String userName;
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            userName = ((UserDetails) principal).getUsername();
        } else {
            userName = principal.toString();
        }
        return userName;
    }

    /**
     * Returns custom messages for Login Exceptions - Checks BadCredentialsException, LockedException, AccountExpiredException and
     * InternalAuthenticationServiceException.
     *
     * @return Custom ErrorMessage as {@link String}
     */
    private String getErrorMessage() {
        Exception exception = (Exception) request.getSession().getAttribute("SPRING_SECURITY_LAST_EXCEPTION");
        String error;
        if (exception instanceof BadCredentialsException) {
            error = messageSource.getMessage("login.failed", new Object[]{env.getRequiredProperty("organisation.admin.email")}, LocaleContextHolder.getLocale());
        } else if (exception instanceof LockedException) {
            error = messageSource.getMessage("login.locked", new Object[]{env.getRequiredProperty("organisation.admin.email")}, LocaleContextHolder.getLocale());
        } else if (exception instanceof AccountExpiredException) {
            error = messageSource.getMessage("login.expired", new Object[]{env.getRequiredProperty("organisation.admin.email")}, LocaleContextHolder.getLocale());
        } else if (exception instanceof InternalAuthenticationServiceException) {
            error = messageSource.getMessage("login.system.error", new Object[]{env.getRequiredProperty("organisation.admin.email")},
                    LocaleContextHolder.getLocale());
        } else {
            error = messageSource.getMessage("login.failed", new Object[]{env.getRequiredProperty("organisation.admin.email")}, LocaleContextHolder.getLocale());
        }
        return error;
    }
}