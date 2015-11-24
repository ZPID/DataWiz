package de.zpid.datawiz.controller;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;

import de.zpid.datawiz.dao.ProjectDAO;
import de.zpid.datawiz.dto.ProjectDTO;
import de.zpid.datawiz.dto.UserDTO;
import de.zpid.datawiz.form.ProjectForm;
import de.zpid.datawiz.util.Roles;
import de.zpid.datawiz.util.UserUtil;

@Controller
@RequestMapping(value = "/project")
@SessionAttributes("ProjectForm")
public class ProjectController {

  @Autowired
  private ProjectDAO projectDAO;

  private static final Logger log = Logger.getLogger(ProjectController.class);
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
    // ProjectForm pform = createAdministrationForm();
    // UserDTO user = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
    // .getUser();
    // try {
    // pform.setProjects(projectDAO.getAllByUserID(user.getId()));
    // } catch (SQLException e) {
    // e.printStackTrace();
    // }
    // model.put("project_roles", Roles.class);
    // model.put("ProjectForm", pform);
    return "panel";
  }

  @RequestMapping(value = "/{pid}", method = RequestMethod.GET)
  public String editProject(@PathVariable String pid, ModelMap model) {
    if (log.isDebugEnabled()) {
      log.debug("execute editProject for projectID=" + pid);
    }
    UserDTO user = UserUtil.getCurrentUser();
    if (user == null) {
      log.warn("Auth User Object == null - redirect to login");
      return "redirect:/login";
    }
    ProjectForm pForm = createAdministrationForm();
    ProjectDTO pDTO = null;
    try {
      pDTO = projectDAO.findByIdWithRole(pid, String.valueOf(user.getId()));
      if (pDTO == null || pDTO.getId() <= 0 || pDTO.getProjectRole() == null
          || pDTO.getProjectRole().getUserId() <= 0) {
        log.warn("Project or project_role is empty for user=" + user.getEmail() + " and project=" + pid);
      } else {
        if (!user.hasRole(Roles.ADMIN) || (user.getId() != pDTO.getProjectRole().getUserId())) {
          log.warn("User with email: " + user.getEmail() + " tries to get access to project:" + pDTO.getId() + " but has no ");
        }
      }

    } catch (DataAccessException | SQLException e) {
      e.printStackTrace();
    }
    List<ProjectDTO> pdto = new ArrayList<ProjectDTO>();

    model.put("ProjectForm", pForm);
    return "project";
  }

  @RequestMapping(value = "/{pid}", method = RequestMethod.POST)
  public String saveProject(@PathVariable String pid, @ModelAttribute("ProjectForm") ProjectForm pform) {
    if (log.isDebugEnabled()) {
      log.debug("execute saveProject");
    }
    System.out.println(pid);
    return "project";
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
