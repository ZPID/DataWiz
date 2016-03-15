package de.zpid.datawiz.controller;

import java.util.Optional;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import de.zpid.datawiz.dao.ProjectDAO;
import de.zpid.datawiz.dao.RoleDAO;
import de.zpid.datawiz.dao.StudyDAO;
import de.zpid.datawiz.dao.UserDAO;
import de.zpid.datawiz.dto.UserDTO;
import de.zpid.datawiz.dto.UserRoleDTO;
import de.zpid.datawiz.enumeration.Roles;
import de.zpid.datawiz.exceptions.DataWizException;
import de.zpid.datawiz.exceptions.DataWizSecurityException;
import de.zpid.datawiz.form.ProjectForm;
import de.zpid.datawiz.util.BreadCrumpUtil;
import de.zpid.datawiz.util.UserUtil;

@Controller
@RequestMapping(value = "/access")
@SessionAttributes({ "ProjectForm", "subnaviActive" })
public class AccessController {

  @Autowired
  private UserDAO userDAO;
  @Autowired
  private MessageSource messageSource;
  @Autowired
  private ProjectDAO projectDAO;
  @Autowired
  private RoleDAO roleDao;
  @Autowired
  private StudyDAO studyDAO;

  private static final Logger log = Logger.getLogger(AccessController.class);
  private ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("spring.xml");

  /**
   * 
   * @return
   */
  @ModelAttribute("ProjectForm")
  public ProjectForm createProjectForm() {
    return (ProjectForm) context.getBean("ProjectForm");

  }

  @RequestMapping(value = { "", "/{pid}" }, method = RequestMethod.GET)
  public String handleGETWithParam(@PathVariable Optional<Integer> pid, ModelMap model,
      RedirectAttributes redirectAttributes) {
    if (log.isDebugEnabled()) {
      log.debug("execute createProject - GET");
    }
    if (!pid.isPresent()) {
      // TODO ausstieg wenn kein Projekt angelegt!!!!
      return "redirect:/panel";
    }
    UserDTO user = UserUtil.getCurrentUser();
    if (user == null) {
      log.warn("Auth User Object == null - redirect to login");
      return "redirect:/login";
    }
    ProjectForm pForm = createProjectForm();
    try {
      pForm = ProjectController.getProjectForm(pForm, pid.get().toString(), user, this.projectDAO, null, null, null,
          this.studyDAO, null, "ACCESS");
      if (pForm.getProject() != null && pForm.getProject().getId() > 0) {
        pForm.setSharedUser(userDAO.findGroupedByProject(pForm.getProject()));
        pForm.setRoleList(roleDao.getAllProjectRoles());
        for (UserDTO tuser : pForm.getSharedUser()) {
          tuser.setGlobalRoles(roleDao.getRolesByUserIDAndProjectID(tuser.getId(), pid.get()));
        }
      }
    } catch (Exception e) {
      log.warn(e.getMessage());
      String redirectMessage = "";
      if (e instanceof DataWizException) {
        redirectMessage = "project.not.available";
      } else if (e instanceof DataWizSecurityException) {
        redirectMessage = "project.access.denied";
      } else {
        redirectMessage = "dbs.sql.exception";
      }
      redirectAttributes.addFlashAttribute("errorMSG",
          messageSource.getMessage(redirectMessage, null, LocaleContextHolder.getLocale()));
      return "redirect:/panel";
    }
    model.put("breadcrumpList", BreadCrumpUtil.generateBC("access"));
    model.put("subnaviActive", "ACCESS");
    model.put("ProjectForm", pForm);
    return "access";
  }

  @RequestMapping(value = { "/{projectId}/delete/{userId}/{roleId}",
      "/{projectId}/delete/{userId}/{roleId}/{studyId}" })
  public String deleteRole(@PathVariable int userId, @PathVariable int roleId, @PathVariable int projectId,
      @PathVariable Optional<Integer> studyId, RedirectAttributes redirectAttributes) {
    if (log.isDebugEnabled()) {
      log.debug("execute deleteRole [Role:" + roleId + " User:" + userId + " Project:" + projectId + "]");
    }

    return "redirect:/access/" + projectId;
  }

  @RequestMapping(value = { "/{projectId}/deleteUser/{userId}" })
  public String deleteUserfromProject(@PathVariable int userId, @PathVariable int projectId,
      RedirectAttributes redirectAttributes) {
    if (log.isDebugEnabled()) {
      log.debug("execute deleteUserfromProject ");
    }

    return "redirect:/access/" + projectId;
  }

  @RequestMapping(value = { "/{pid}" }, params = { "addRole" })
  public String addRoleToProjectUser(@PathVariable int pid, @ModelAttribute("ProjectForm") ProjectForm pForm,
      RedirectAttributes redirectAttributes) {
    Boolean err = false;
    if (pForm != null && pForm.getNewRole() != null) {
      UserRoleDTO newRole = pForm.getNewRole();
      if (log.isDebugEnabled()) {
        log.debug("execute addRoleToProjectUser [User:" + newRole.getUserId() + " Project:" + pid + " Type:"
            + newRole.getType() + " Study: " + newRole.getStudyId() + "]");
      }
      newRole.setProjectId(pid);
      if (newRole.getType() != null && !newRole.getType().isEmpty() && !newRole.getType().equals("0")) {
        newRole.setRoleId(Roles.valueOf(newRole.getType()).toInt());
      } else {
        err = true;
      }
      UserDTO user = null;
      if (newRole.getUserId() > 0) {
        try {
          user = userDAO.findById(newRole.getUserId());
        } catch (Exception e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
        if (user != null && user.getGlobalRoles() != null) {
          for (UserRoleDTO roleTmp : user.getGlobalRoles()) {
            if (roleTmp.equals(newRole)) {
              err = true;
              break;
            }
          }
        }
      } else {
        err = true;
      }
    } else {
      err = true;
    }

    return err ? "access" : "redirect:/access/" + pid;
  }
}
