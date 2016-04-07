package de.zpid.datawiz.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = { "/user" })
public class DataWizUserController extends SuperController {

  public DataWizUserController() {
    super();
    if (log.isInfoEnabled())
      log.info("Loading DataWizUserController for mapping /user");
  }
  
  

}
