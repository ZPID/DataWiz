package de.zpid.datawiz.controller;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;

import de.zpid.datawiz.dto.ProjectDTO;
import de.zpid.datawiz.dto.StudyDTO;
import de.zpid.datawiz.dto.UserDTO;
import de.zpid.datawiz.dto.UserRoleDTO;
import de.zpid.datawiz.enumeration.PageState;
import de.zpid.datawiz.enumeration.Roles;
import de.zpid.datawiz.form.ProjectForm;
import de.zpid.datawiz.util.BreadCrumpUtil;
import de.zpid.datawiz.util.UserUtil;

@Controller
@RequestMapping(value = "/panel")
@SessionAttributes({ "breadcrumpList" })
public class PanelController extends SuperController {

  private static Logger log = LogManager.getLogger(PanelController.class);

  public PanelController() {
    super();
    if (log.isInfoEnabled())
      log.info("Loading PanelController for mapping /panel");
  }

  @ModelAttribute("ProjectForm")
  public ProjectForm createProjectForm() {
    return (ProjectForm) applicationContext.getBean("ProjectForm");
  }

  @RequestMapping(method = RequestMethod.GET)
  public String dashboardPage(ModelMap model) {
    if (log.isTraceEnabled()) {
      log.trace("execute dashboardPage()");
    }
    UserDTO user = UserUtil.getCurrentUser();
    List<ProjectForm> cpform = new ArrayList<ProjectForm>();
    try {
      List<ProjectDTO> cpdto = null;
      if (user.hasRole(Roles.ADMIN)) {
        cpdto = projectDAO.findAll();
      } else {
        cpdto = projectDAO.findAllByUserID(user);
      }
      if (cpdto != null) {
        for (ProjectDTO pdto : cpdto) {
          ProjectForm pform = createProjectForm();
          pform.setProject(pdto);
          if (user.hasRole(Roles.ADMIN) || user.hasRole(Roles.PROJECT_ADMIN, pdto.getId(), false)
              || user.hasRole(Roles.PROJECT_READER, pdto.getId(), false)
              || user.hasRole(Roles.PROJECT_WRITER, pdto.getId(), false)) {
            pform.setStudies(studyDAO.findAllStudiesByProjectId(pdto));
          } else if (user.hasRole(Roles.DS_READER, pdto.getId(), false)
              || user.hasRole(Roles.DS_WRITER, pdto.getId(), false)) {
            List<UserRoleDTO> userRoles = roleDAO.findRolesByUserIDAndProjectID(user.getId(), pdto.getId());
            List<StudyDTO> cStud = new ArrayList<StudyDTO>();
            userRoles.parallelStream().forEach(role -> {
              Roles uRole = Roles.valueOf(role.getType());
              if (role.getStudyId() > 0 && (uRole.equals(Roles.DS_READER) || uRole.equals(Roles.DS_WRITER))) {
                try {
                  cStud.add(studyDAO.findById(role.getStudyId(), role.getProjectId(), true, false));
                } catch (Exception e) {
                  // TODO Auto-generated catch block
                  e.printStackTrace();
                }
              }
            });
            pform.setStudies(cStud);
          }
          List<Boolean> par = new ArrayList<>();
          pform.getStudies().parallelStream().forEach(stud -> {
            try {
              stud.setContributors(contributorDAO.findByStudy(stud.getId()));
            } catch (Exception e) {
              // TODO Auto-generated catch block
              par.add(false);
              e.printStackTrace();
            }
          });
          pform.setContributors(contributorDAO.findByProject(pdto, false, true));
          List<UserDTO> sharedUser = userDAO.findGroupedByProject(pdto.getId());
          sharedUser.parallelStream().forEach(shared -> {
            try {
              shared.setGlobalRoles(roleDAO.findRolesByUserIDAndProjectID(shared.getId(), pdto.getId()));
            } catch (SQLException e) {
              // TODO Auto-generated catch block
              e.printStackTrace();
            }
          });
          pform.setSharedUser(sharedUser);
          cpform.add(pform);
        }
      }
    } catch (Exception e) {
      log.fatal("DBS error during setting Users Dashboardpage for user : {} Message: {}", () -> user.getEmail(),
          () -> e.getMessage());
      model.put("errormsg", messageSource.getMessage("dbs.sql.exception",
          new Object[] { env.getRequiredProperty("organisation.admin.email"), e }, LocaleContextHolder.getLocale()));
      return "error";
    }
    model.put("breadcrumpList", BreadCrumpUtil.generateBC(PageState.PANEL, null, null, messageSource));
    model.put("CProjectForm", cpform);
    return "panel";
  }
}
