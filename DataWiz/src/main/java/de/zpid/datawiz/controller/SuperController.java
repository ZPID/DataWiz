package de.zpid.datawiz.controller;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.validation.SmartValidator;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import de.zpid.datawiz.dao.ContributorDAO;
import de.zpid.datawiz.dao.DmpDAO;
import de.zpid.datawiz.dao.FileDAO;
import de.zpid.datawiz.dao.FormTypesDAO;
import de.zpid.datawiz.dao.ProjectDAO;
import de.zpid.datawiz.dao.RoleDAO;
import de.zpid.datawiz.dao.StudyDAO;
import de.zpid.datawiz.dao.TagDAO;
import de.zpid.datawiz.dao.UserDAO;
import de.zpid.datawiz.dto.ProjectDTO;
import de.zpid.datawiz.dto.UserDTO;
import de.zpid.datawiz.dto.UserRoleDTO;
import de.zpid.datawiz.enumeration.DelType;
import de.zpid.datawiz.enumeration.Roles;
import de.zpid.datawiz.exceptions.DataWizException;
import de.zpid.datawiz.exceptions.DataWizSecurityException;
import de.zpid.datawiz.form.ProjectForm;
import de.zpid.datawiz.util.UserUtil;

public class SuperController {

  @Autowired
  protected MessageSource messageSource;
  @Autowired
  protected UserDAO userDAO;
  @Autowired
  protected ProjectDAO projectDAO;
  @Autowired
  protected RoleDAO roleDAO;
  @Autowired
  protected StudyDAO studyDAO;
  @Autowired
  protected TagDAO tagDAO;
  @Autowired
  protected FileDAO fileDAO;
  @Autowired
  protected ContributorDAO contributorDAO;
  @Autowired
  protected FormTypesDAO formTypeDAO;
  @Autowired
  protected DmpDAO dmpDAO;
  @Autowired
  protected SmartValidator validator;
  @Autowired
  protected HttpServletRequest request;

  protected ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("spring.xml");

  protected Logger log = Logger.getLogger(getClass());

  /**
   * Checks if the passed UserDTO has the PROJECT_ADMIN role.
   *
   * @param redirectAttributes
   * @param projectId
   * @param admin
   * @return String with redirect entry if role is non-existent, and null if existent
   */
  protected String checkProjectAdmin(RedirectAttributes redirectAttributes, long projectId, UserDTO admin) {
    if (log.isDebugEnabled()) {
      log.debug("Entering checkProjectAdmin for Project [id:" + projectId + "] an User [email: " + admin + "]");
    }
    if (admin == null) {
      log.warn("Auth User Object == null - redirect to login");
      return "redirect:/login";
    }
    try {
      admin.setGlobalRoles(roleDAO.findRolesByUserID(admin.getId()));
    } catch (Exception e) {
      log.fatal("ERROR: Database Error while getting roles - Exception:", e);
      return "redirect:/access/" + projectId;
    }
    if (!admin.hasProjectRole(Roles.PROJECT_ADMIN, projectId) && !admin.hasRole(Roles.ADMIN)) {
      redirectAttributes.addFlashAttribute("errorMSG",
          messageSource.getMessage("roles.access.denied", null, LocaleContextHolder.getLocale()));
      log.warn("SECURITY: User with email: " + admin.getEmail() + " tries change roles for project:" + projectId
          + " without having permission");
      return "redirect:/access/" + projectId;
    }
    return null;
  }

  /**
   * 
   * @param pid
   * @param user
   * @return
   * @throws Exception
   */
  protected ProjectForm getProjectForm(ProjectForm pForm, long pid, UserDTO user, String call) throws Exception {
    if (log.isDebugEnabled()) {
      log.debug("execute getProjectData");
    }
    // 1st - security access check!
    if (pid > 0 && user != null) {
      if (!user.hasRole(Roles.ADMIN) && !user.hasProjectRole(Roles.PROJECT_READER, pid)
          && !user.hasProjectRole(Roles.PROJECT_ADMIN, pid) && !user.hasProjectRole(Roles.PROJECT_WRITER, pid)) {
        throw new DataWizSecurityException("SECURITY: User with email: " + user.getEmail()
            + " tries to get access to project:" + pid + " without having the permissions to read");
      }
      ProjectDTO pdto = projectDAO.findById(pid);
      if (pdto == null || pdto.getId() <= 0) {
        throw new DataWizException("Project is empty for user=" + user.getEmail() + " and project=" + pid);
      }
      // 2nd - security access check!
      // if (user.getId() != pdto.getProjectRole().getUserId()) {
      // throw new DataWizSecurityException("SECURITY: User with email: " + user.getEmail()
      // + " tries to get access to project:" + pdto.getId() + " without having permissions to read");
      // }
      pForm.setProject(pdto);
      if (call == null || call.isEmpty() || call.equals("PROJECT")) {
        pForm.setFiles(fileDAO.getProjectFiles(pdto));
        pForm.setTags(new ArrayList<String>(tagDAO.getTagsByProjectID(pdto).values()));
        pForm.setStudies(studyDAO.getAllStudiesByProjectId(pdto));
        pForm.setContributors(contributorDAO.getByProject(pdto, false, false));
        pForm.setPrimaryContributor(contributorDAO.findPrimaryContributorByProject(pdto));
      } else if (call.equals("DMP")) {
        pForm.setDataTypes(formTypeDAO.getAllByType(true, DelType.datatype));
        pForm.setCollectionModes(formTypeDAO.getAllByType(true, DelType.collectionmode));
        pForm.setMetaPurposes(formTypeDAO.getAllByType(true, DelType.metaporpose));
        pForm.setPrimaryContributor(contributorDAO.findPrimaryContributorByProject(pdto));
      } else if (call.equals("ACCESS")) {
        pForm.setStudies(studyDAO.getAllStudiesByProjectId(pdto));
      }
      return pForm;
    } else {
      log.warn("ProjectID or UserDTO is empty - NULL returned!");
      return null;
    }
  }

  public boolean saveOrUpdateProject(ProjectForm pForm, ProjectDAO projectDAO, RoleDAO roleDAO) {
    boolean error = false;
    try {
      UserDTO user = UserUtil.getCurrentUser();
      if (pForm != null && pForm.getProject() != null && user != null) {
        if (pForm.getProject().getId() <= 0) {
          int chk = projectDAO.insertProject(pForm.getProject());
          if (chk > 0) {
            roleDAO.setRole(new UserRoleDTO(Roles.REL_ROLE.toInt(), user.getId(), chk, 0, Roles.REL_ROLE.name()));
            roleDAO.setRole(
                new UserRoleDTO(Roles.PROJECT_ADMIN.toInt(), user.getId(), chk, 0, Roles.PROJECT_ADMIN.name()));
            pForm.getProject().setId(chk);
          } else {
            error = true;
          }
        } else {
          projectDAO.updateProject(pForm.getProject());
        }
      } else {
        error = true;
      }
      UserUtil.getCurrentUser().setGlobalRoles(roleDAO.findRolesByUserID(user.getId()));
    } catch (Exception e) {
      log.error("Project saving not sucessful error:" + e.getMessage());
      error = true;
    }
    return error;
  }

}
