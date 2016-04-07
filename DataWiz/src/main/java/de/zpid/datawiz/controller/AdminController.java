package de.zpid.datawiz.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class AdminController extends SuperController {

  public AdminController() {
    super();
    if (log.isInfoEnabled())
      log.info("Loading AdminController for mapping /admin");
  }

  @RequestMapping(value = "/admin", method = RequestMethod.GET)
  public String adminPage(ModelMap model) {
    if (log.isDebugEnabled()) {
      log.debug("execute adminPage()");
    }

    return "admin/admin";
  }

}
