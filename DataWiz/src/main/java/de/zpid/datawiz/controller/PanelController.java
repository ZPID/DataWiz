package de.zpid.datawiz.controller;

import java.sql.SQLException;

import javax.validation.Valid;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import de.zpid.datawiz.dao.ProjectDAO;
import de.zpid.datawiz.dto.UserDTO;
import de.zpid.datawiz.form.ProjectForm;
import de.zpid.datawiz.util.CustomUserDetails;
import de.zpid.datawiz.util.Roles;

@Controller
@RequestMapping(value = "/panel")
public class PanelController {

  @Autowired
  private ProjectDAO projectDAO;

  private static final Logger log = Logger.getLogger(PanelController.class);
  private ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("spring.xml");

  @ModelAttribute("ProjectForm")
  public ProjectForm createAdministrationForm() {
    return (ProjectForm) context.getBean("ProjectForm");
  }

  @RequestMapping(method = RequestMethod.GET)
  public String dashboardPage(ModelMap model) {
    if (log.isDebugEnabled()) {
      log.debug("execute dashboardPage()");
    }
    ProjectForm pform = createAdministrationForm();
    UserDTO user = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
        .getUser();
    try {
      pform.setProjects(projectDAO.getAllByUserID(user.getId()));
    } catch (SQLException e) {
      e.printStackTrace();
    }
    model.put("project_roles", Roles.class);
    model.put("ProjectForm", pform);
    return "panel";
  }

  @RequestMapping(method = RequestMethod.POST)
  public String doPost(@Valid @ModelAttribute("UserDTO") UserDTO person, BindingResult bindingResult) {
    if (log.isDebugEnabled()) {
      log.debug("execute dashboardPage()");
    }
    if (bindingResult.hasErrors()) {
      return "panel";
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
