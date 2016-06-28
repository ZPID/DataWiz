package de.zpid.datawiz.controller;

import java.sql.SQLException;
import java.util.Optional;

import javax.validation.Valid;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import de.zpid.datawiz.dto.UserDTO;
import de.zpid.datawiz.enumeration.PageState;
import de.zpid.datawiz.enumeration.Roles;
import de.zpid.datawiz.util.BreadCrumpUtil;
import de.zpid.datawiz.util.UserUtil;

/**
 * Controller for mapping "/usersettings" <br />
 * <br />
 * This file is part of Datawiz.<br />
 * 
 * <b>Copyright 2016, Leibniz Institute for Psychology Information (ZPID),
 * <a href="http://zpid.de" title="http://zpid.de">http://zpid.de</a>.</b><br />
 * <br />
 * <a rel="license" href="http://creativecommons.org/licenses/by-nc-sa/4.0/"><img alt="Creative Commons License" style=
 * "border-width:0" src="https://i.creativecommons.org/l/by-nc-sa/4.0/80x15.png" /></a><br />
 * <span xmlns:dct="http://purl.org/dc/terms/" property="dct:title">Datawiz</span> by
 * <a xmlns:cc="http://creativecommons.org/ns#" href="zpid.de" property="cc:attributionName" rel="cc:attributionURL">
 * Leibniz Institute for Psychology Information (ZPID)</a> is licensed under a
 * <a rel="license" href="http://creativecommons.org/licenses/by-nc-sa/4.0/">Creative Commons
 * Attribution-NonCommercial-ShareAlike 4.0 International License</a>.
 * 
 * @author Ronny Boelter
 * @version 1.0
 *
 * 
 *
 */
@Controller
@RequestMapping(value = { "/usersettings" })
@SessionAttributes({ "UserDTO" })
public class UserController extends SuperController {

  private static Logger log = LogManager.getLogger(UserController.class);

  public UserController() {
    super();
    if (log.isInfoEnabled())
      log.info("Loading DataWizUserController for mapping /usersettings");
  }

  /**
   * 
   * @param userId
   * @param model
   * @param reAtt
   * @return
   */
  @RequestMapping(value = { "", "/{userId}", }, method = RequestMethod.GET)
  public String showUserSettingPage(@PathVariable final Optional<Long> userId, final ModelMap model,
      final RedirectAttributes reAtt) {
    final UserDTO auth = UserUtil.getCurrentUser();
    log.trace("Entering showUserSettingPage for user [id: {}]",
        () -> userId.isPresent() ? userId.get() : (auth != null && auth.getId() > 0) ? auth.getId() : "null");
    UserDTO user = null;
    try {
      if (userId.isPresent() && auth.hasRole(Roles.ADMIN)) {
        user = userDAO.findById(userId.get());
      } else {
        user = userDAO.findById(auth.getId());
      }
    } catch (Exception e) {
      log.error("ERROR: Database error during database transaction, saveUserSettings aborted - Exception:", e);
      reAtt.addFlashAttribute("globalErrors",
          messageSource.getMessage("dbs.sql.exception", null, LocaleContextHolder.getLocale()));
      return "redirect:/panel";
    }
    if (user == null) {
      log.warn("Auth User Object == null - redirect to login");
      return "redirect:/login";
    }
    model.put("breadcrumpList", BreadCrumpUtil.generateBC(PageState.USERSETTING, null, 0));
    model.put("UserDTO", user);
    log.trace("Method showUserSettingPage successfully completed");
    return "usersettings";
  }

  /**
   * 
   * @param user
   * @param bRes
   * @param reAtt
   * @return
   */
  @RequestMapping(method = RequestMethod.POST)
  public String saveUserSettings(@Valid @ModelAttribute("UserDTO") final UserDTO user, final BindingResult bRes,
      final RedirectAttributes reAtt) {
    log.trace("Entering saveUserSettings for user [id: {}]", () -> user != null ? user.getEmail() : null);
    if (user == null || user.getId() <= 0) {
      log.warn("UserDTO Object == null or UserID is not present");
      reAtt.addFlashAttribute("errorMSG",
          messageSource.getMessage("no.data.exception", null, LocaleContextHolder.getLocale()));
      return "redirect:/usersettings";
    }
    final UserDTO auth = UserUtil.getCurrentUser();
    if (auth == null || (!auth.hasRole(Roles.ADMIN) && auth.getId() != user.getId())) {
      log.warn("Auth User Object == null or user [email: {}] do not have the permission to edit user user [email: {}]",
          () -> auth != null ? auth.getEmail() : null, () -> user.getEmail());
      reAtt.addFlashAttribute("errorMSG",
          messageSource.getMessage("usersettings.error.noaccess", null, LocaleContextHolder.getLocale()));
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
        }
        changePWD = true;
      }
      if (bRes.hasErrors()) {
        bRes.reject("globalErrors",
            messageSource.getMessage("usersettings.save.error", null, LocaleContextHolder.getLocale()));
        return "usersettings";
      }
      user.setPassword(passwordEncoder.encode(user.getPassword()));
      userDAO.saveOrUpdate(user, changePWD);
      reAtt.addFlashAttribute("infoMSG",
          messageSource.getMessage(changePWD ? "usersettings.save.success.pw" : "usersettings.save.success", null,
              LocaleContextHolder.getLocale()));
    } catch (SQLException e) {
      log.error("ERROR: Database error during database transaction, saveUserSettings aborted - Exception:", e);
      reAtt.addFlashAttribute("globalErrors",
          messageSource.getMessage("dbs.sql.exception", null, LocaleContextHolder.getLocale()));
      return "redirect:/panel";
    }
    log.trace("Method saveUserSettings successfully completed");
    return "redirect:/usersettings";
  }
}
