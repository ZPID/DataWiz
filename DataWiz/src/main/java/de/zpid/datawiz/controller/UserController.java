package de.zpid.datawiz.controller;

import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import de.zpid.datawiz.dto.UserDTO;
import de.zpid.datawiz.enumeration.PageState;
import de.zpid.datawiz.enumeration.Roles;
import de.zpid.datawiz.util.BreadCrumpUtil;
import de.zpid.datawiz.util.UserUtil;

@Controller
@RequestMapping(value = { "/usersettings" })
public class UserController extends SuperController {

  public UserController() {
    super();
    if (log.isInfoEnabled())
      log.info("Loading DataWizUserController for mapping /usersettings");
  }

  @RequestMapping(value = { "", "/{userId}", }, method = RequestMethod.GET)
  public String showUserSettingPage(@PathVariable final Optional<Long> userId, ModelMap model) {
    final UserDTO auth = UserUtil.getCurrentUser();
    log.trace("Entering showUserSettingPage for user [id: {}]",
        () -> userId.isPresent() ? userId.get() : (auth != null && auth.getId() > 0) ? auth.getId() : "null");
    UserDTO user = null;
    if (userId.isPresent() && auth.hasRole(Roles.ADMIN)) {
      try {
        user = userDAO.findById(userId.get());
      } catch (Exception e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    } else {
      user = auth;
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

  @RequestMapping(method = RequestMethod.POST)
  public String saveUserSettings(@ModelAttribute("UserDTO") UserDTO user) {

    return "usersettings";
  }
}
