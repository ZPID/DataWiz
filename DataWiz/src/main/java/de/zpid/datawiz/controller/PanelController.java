package de.zpid.datawiz.controller;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
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
import de.zpid.datawiz.dao.StudyDAO;
import de.zpid.datawiz.dto.ProjectDTO;
import de.zpid.datawiz.dto.UserDTO;
import de.zpid.datawiz.form.ProjectForm;
import de.zpid.datawiz.util.CustomUserDetails;

@Controller
@RequestMapping(value = "/panel")
public class PanelController {

  @Autowired
  private ProjectDAO projectDAO;
  @Autowired
  private StudyDAO studyDAO;
  @Autowired
  private MessageSource messageSource;
  private static final Logger log = Logger.getLogger(PanelController.class);
  private ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("spring.xml");

  @ModelAttribute("ProjectForm")
  public ProjectForm createProjectForm() {
    return (ProjectForm) context.getBean("ProjectForm");
  }

  @RequestMapping(method = RequestMethod.GET)
  public String dashboardPage(ModelMap model) {
    if (log.isDebugEnabled()) {
      log.debug("execute dashboardPage()");
    }
    UserDTO user = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
        .getUser();
    List<ProjectForm> cpform = new ArrayList<ProjectForm>();
    try {
      List<ProjectDTO> cpdto = projectDAO.getAllByUserID(user);
      if (cpdto != null) {
        for (ProjectDTO pdto : cpdto) {
          System.out.println(pdto.getCreated());
          ProjectForm pform = createProjectForm();
          pform.setProject(pdto);
          pform.setStudies(studyDAO.getLatestStudyVersionsByProjectID(pdto));
          cpform.add(pform);
        }
      }
    } catch (Exception e) {
      log.error("DBS error during setting Users Dashboardpage for user : " + user.getEmail() + " Message:" + e);
      model.put("errormsg", messageSource.getMessage("dbs.sql.exception", null, LocaleContextHolder.getLocale()));
      return "error";
    }
    model.put("CProjectForm", cpform);
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
