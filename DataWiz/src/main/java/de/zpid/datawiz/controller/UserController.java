package de.zpid.datawiz.controller;

import de.zpid.datawiz.dao.UserDAO;
import de.zpid.datawiz.dto.UserDTO;
import de.zpid.datawiz.enumeration.PageState;
import de.zpid.datawiz.enumeration.Roles;
import de.zpid.datawiz.util.BreadCrumbUtil;
import de.zpid.datawiz.util.EmailUtil;
import de.zpid.datawiz.util.UserUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.sql.SQLException;
import java.util.Locale;
import java.util.Optional;

/**
 * This controller handles all calls to /usersettings/*
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
 * TODO Missing service layer: to separate the DBS logic from the web logic!
 **/
@Controller
@RequestMapping(value = {"/usersettings"})
@SessionAttributes({"UserDTO"})
public class UserController {

    private final PasswordEncoder passwordEncoder;
    private final MessageSource messageSource;
    private final UserDAO userDAO;
    private final EmailUtil emailUtil;
    private final Environment env;

    private static final Logger log = LogManager.getLogger(UserController.class);

    /**
     * Instantiates a new user controller.
     */
    @Autowired
    public UserController(Environment env, EmailUtil emailUtil, UserDAO userDAO, MessageSource messageSource, PasswordEncoder passwordEncoder) {
        super();
        if (log.isInfoEnabled())
            log.info("Loading DataWizUserController for mapping /usersettings");
        this.env = env;
        this.emailUtil = emailUtil;
        this.userDAO = userDAO;
        this.messageSource = messageSource;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * This function loads the user settings via UserDao an forwards them to usersettings.jsp
     *
     * @param userId Optional&lt;long&gt;
     * @param model  ModelMap
     * @param reAtt  RedirectAttributes
     * @return usersetting.jsp on sucess, otherwise redirect:/panel on DB error or redirect:/login on Auth error
     */

    @RequestMapping(value = {"", "/{userId}",}, method = RequestMethod.GET)
    public String showUserSettingPage(@PathVariable final Optional<Long> userId, final ModelMap model, final RedirectAttributes reAtt) {
        final UserDTO auth = UserUtil.getCurrentUser();
        log.trace("Entering showUserSettingPage for user [id: {}]",
                () -> userId.orElseGet(() -> (auth != null && auth.getId() > 0) ? auth.getId() : 0L));
        UserDTO user;
        try {
            if (userId.isPresent() && auth.hasRole(Roles.ADMIN)) {
                user = userDAO.findById(userId.get());
            } else {
                user = userDAO.findById(auth.getId());
            }
        } catch (Exception e) {
            log.error("ERROR: Database error during database transaction, saveUserSettings aborted - Exception:", e);
            reAtt.addFlashAttribute("globalErrors", messageSource.getMessage("dbs.sql.exception", null, LocaleContextHolder.getLocale()));
            return "redirect:/panel";
        }
        if (user == null) {
            log.warn(messageSource.getMessage("logging.user.auth.missing", null, Locale.ENGLISH));
            return "redirect:/login";
        }
        model.put("breadcrumbList", BreadCrumbUtil.generateBC(PageState.USERSETTING, null, null, messageSource));
        model.put("UserDTO", user);
        log.trace("Method showUserSettingPage successfully completed");
        return "usersettings";
    }

    /**
     * This function validates the User Settings before they where send to the UserDAO for saving. If saving is successful, the new settings are stored
     * in the Session, to prevent that the user has to log-out and log-in.
     *
     * @param user  UserDTO
     * @param bRes  BindingResult
     * @param reAtt RedirectAttributes
     * @return redirect to /usersettings on success, or usersettings.jsp on error with error messages
     */
    @RequestMapping(method = RequestMethod.POST)
    public String saveUserSettings(@Valid @ModelAttribute("UserDTO") final UserDTO user, final BindingResult bRes, final RedirectAttributes reAtt) {
        log.trace("Entering saveUserSettings for user [id: {}]", () -> user != null ? user.getEmail() : null);
        if (user == null || user.getId() <= 0) {
            log.warn("UserDTO Object == null or UserID is not present");
            reAtt.addFlashAttribute("errorMSG", messageSource.getMessage("no.data.exception",
                    new Object[]{env.getRequiredProperty("organisation.admin.email")}, LocaleContextHolder.getLocale()));
            return "redirect:/usersettings";
        }
        final UserDTO auth = UserUtil.getCurrentUser();
        if (auth == null || (!auth.hasRole(Roles.ADMIN) && auth.getId() != user.getId())) {
            log.warn("Auth User Object == null or user [email: {}] do not have the permission to edit user user [email: {}]",
                    () -> auth != null ? auth.getEmail() : null, user::getEmail);
            reAtt.addFlashAttribute("errorMSG", messageSource.getMessage("usersettings.error.noaccess", null, LocaleContextHolder.getLocale()));
            return "redirect:/panel";
        }
        try {
            boolean changePWD = false;
            if (!user.getPassword().isEmpty()) {
                if (user.getPassword_old().isEmpty() || user.getPassword_retyped().isEmpty()) {
                    bRes.rejectValue("password", "passwords.not.given");
                    bRes.rejectValue("password_retyped", "passwords.not.given");
                    bRes.rejectValue("password_old", "passwords.not.given");
                } else if (user.getPassword().length() < 6) {
                    bRes.rejectValue("password", "passwords.too.short");
                } else if (!user.getPassword().equals(user.getPassword_retyped())) {
                    bRes.rejectValue("password", "passwords.not.equal");
                    bRes.rejectValue("password_retyped", "passwords.not.equal");
                } else if (!passwordEncoder.matches(user.getPassword_old(), userDAO.findPasswordbyId(user.getId()))) {
                    bRes.rejectValue("password_old", "passwords.old.not.match");
                } else {
                    user.setPassword(passwordEncoder.encode(user.getPassword()));
                    changePWD = true;
                }
            }
            if (emailUtil.isFakeMail(user.getEmail()))
                bRes.rejectValue("email", "error.email.fake");
            if (emailUtil.isFakeMail(user.getSecEmail()))
                bRes.rejectValue("secEmail", "error.email.fake");
            UserDTO userdb = userDAO.findByMail(user.getEmail(), false);
            if (userdb != null && userdb.getId() != user.getId()) {
                bRes.reject("globalErrors", messageSource.getMessage("usersettings.save.error.email", null, LocaleContextHolder.getLocale()));
            }

            if (bRes.hasErrors()) {
                bRes.reject("globalErrors", messageSource.getMessage("usersettings.save.error", null, LocaleContextHolder.getLocale()));
                return "usersettings";
            }
            userDAO.saveOrUpdate(user, changePWD);
            if (UserUtil.getCurrentUser().getId() == user.getId())
                UserUtil.setCurrentUser(userDAO.findByMail(user.getEmail(), true));
            reAtt.addFlashAttribute("infoMSG",
                    messageSource.getMessage(changePWD ? "usersettings.save.success.pw" : "usersettings.save.success", null, LocaleContextHolder.getLocale()));
        } catch (Exception e) {
            log.error("ERROR: Database error during database transaction, saveUserSettings aborted - Exception:", e);
            reAtt.addFlashAttribute("globalErrors", messageSource.getMessage("dbs.sql.exception", null, LocaleContextHolder.getLocale()));
            return "redirect:/panel";
        }
        log.trace("Method saveUserSettings successfully completed");
        return "redirect:/usersettings";
    }

}
