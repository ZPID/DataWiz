package de.zpid.datawiz.controller;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class AdminController {

  private static final Logger log = Logger.getLogger(LoginController.class);
  // private ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("spring.xml");

  // @Autowired
  // private UserDAO userDao;

  @RequestMapping(value = "/admin", method = RequestMethod.GET)
  public String adminPage(ModelMap model) {
    if (log.isDebugEnabled()) {
      log.debug("execute adminPage()");
    }

    return "admin/admin";
  }

}
