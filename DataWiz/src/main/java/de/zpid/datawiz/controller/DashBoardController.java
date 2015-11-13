package de.zpid.datawiz.controller;

import javax.validation.Valid;

import org.apache.log4j.Logger;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import de.zpid.datawiz.model.DataWizUser;

@Controller
@RequestMapping(value = "/dashboard")
public class DashBoardController {

  private static final Logger log = Logger.getLogger(DashBoardController.class);
  private ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("spring.xml");

  @ModelAttribute("DataWizUser")
  public DataWizUser createAdministrationForm() {
    return (DataWizUser) context.getBean("DataWizUser");
  }

  @RequestMapping(method = RequestMethod.GET)
  public String dashboardPage() {
    if (log.isDebugEnabled()) {
      log.debug("execute dashboardPage()");
    }
    return "test";
  }

  @RequestMapping(method = RequestMethod.POST)
  public String doPost(@Valid @ModelAttribute("DataWizUser") DataWizUser person, BindingResult bindingResult) {
    if (log.isDebugEnabled()) {
      log.debug("execute dashboardPage()");
    }
    if (bindingResult.hasErrors()) {
      return "test";
    }
    return "welcome";
  }

  @RequestMapping(value = "/{qw}", method = RequestMethod.GET)
  public String maptest(@PathVariable String qw) {
    if (log.isDebugEnabled()) {
      log.debug("execute maptest()");
    }
    System.out.println(qw);
    return "welcome";
  }

  @RequestMapping(value = "/{qw}/{wert}", method = RequestMethod.GET)
  public String maptest2(@PathVariable String qw, @PathVariable String wert) {
    if (log.isDebugEnabled()) {
      log.debug("execute maptest2()");
    }
    System.out.println(qw);
    System.out.println(wert);
    return "welcome";
  }

}
