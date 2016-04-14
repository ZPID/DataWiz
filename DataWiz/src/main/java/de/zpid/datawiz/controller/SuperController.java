package de.zpid.datawiz.controller;

import java.util.ArrayList;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.env.Environment;
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
import de.zpid.datawiz.dto.DmpDTO;
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
  @Autowired
  protected Environment env;

  protected ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("spring.xml");

  protected Logger log = LogManager.getLogger(getClass());

  /**
   * Checks if the passed UserDTO has the PROJECT_ADMIN role.
   *
   * @param redirectAttributes
   * @param projectId
   * @param admin
   * @return String with redirect entry if role is non-existent, and null if existent
   */
  protected String checkProjectAdmin(final RedirectAttributes redirectAttributes, final long projectId,
      final UserDTO admin) {
    log.trace("Entering checkProjectAdmin for project [id: {}] and user [mail: {}] ", () -> projectId,
        () -> admin.getEmail());
    if (admin == null) {
      log.warn("Auth User Object == null - redirect to login");
      return "redirect:/login";
    }
    String ret = null;
    try {
      admin.setGlobalRoles(roleDAO.findRolesByUserID(admin.getId()));
      if (!admin.hasRole(Roles.PROJECT_ADMIN, Optional.of(projectId), false) && !admin.hasRole(Roles.ADMIN)) {
        redirectAttributes.addFlashAttribute("errorMSG",
            messageSource.getMessage("roles.access.denied", null, LocaleContextHolder.getLocale()));
        log.warn("SECURITY: User with email: " + admin.getEmail() + " tries change roles for project:" + projectId
            + " without having permission");
        ret = "redirect:/access/" + projectId;
      }
    } catch (Exception e) {
      log.error("ERROR: Database Error while getting roles - Exception:", e);
      ret = "redirect:/access/" + projectId;
    }
    log.trace("Method checkProjectAdmin completed");
    return ret;
  }

  /**
   * 
   * @param pid
   * @param user
   * @return
   * @throws Exception
   */
  protected void getProjectForm(final ProjectForm pForm, final long pid, final UserDTO user, final String call)
      throws Exception {
    log.trace("Entering getProjectData for project [id: {}] and user [mail: {}] ", () -> pid, () -> user.getEmail());
    // security access check!
    if (pid > 0 && user != null) {
      if (!checkProjectRoles(user, pid, false, true)) {
        throw new DataWizSecurityException("SECURITY: User with email: " + user.getEmail()
            + " tries to get access to project:" + pid + " without having the permissions to read");
      }
      final ProjectDTO pdto = projectDAO.findById(pid);
      if (pdto == null || pdto.getId() <= 0) {
        throw new DataWizException("Project is empty for user=" + user.getEmail() + " and project=" + pid);
      }
      pForm.setProject(pdto);
      // load all data if user has full project rights
      if (checkProjectRoles(user, pid, false, false)) {
        // load /project data
        if (call == null || call.isEmpty() || call.equals("PROJECT")) {
          pForm.setFiles(fileDAO.getProjectFiles(pdto));
          pForm.setTags(new ArrayList<String>(tagDAO.getTagsByProjectID(pdto).values()));
          pForm.setStudies(studyDAO.findAllStudiesByProjectId(pdto));
          pForm.setContributors(contributorDAO.findByProject(pdto, false, false));
          pForm.setPrimaryContributor(contributorDAO.findPrimaryContributorByProject(pdto));
        } // load /dmp data
        else if (call.equals("DMP")) {
          DmpDTO dmp = dmpDAO.getByID(pForm.getProject());
          if (dmp != null && dmp.getId() > 0) {
            dmp.setUsedDataTypes(dmpDAO.getDMPUsedDataTypes(dmp.getId(), DelType.datatype));
            dmp.setUsedCollectionModes(dmpDAO.getDMPUsedDataTypes(dmp.getId(), DelType.collectionmode));
            dmp.setSelectedMetaPurposes(dmpDAO.getDMPUsedDataTypes(dmp.getId(), DelType.metaporpose));
            dmp.setAdminChanged(false);
            dmp.setResearchChanged(false);
            dmp.setMetaChanged(false);
            dmp.setSharingChanged(false);
            dmp.setStorageChanged(false);
            dmp.setOrganizationChanged(false);
            dmp.setEthicalChanged(false);
            dmp.setCostsChanged(false);
          }
          if (dmp == null || dmp.getId() <= 0) {
            dmp = (DmpDTO) context.getBean("DmpDTO");
          }
          pForm.setDmp(dmp);
          pForm.setDataTypes(formTypeDAO.getAllByType(true, DelType.datatype));
          pForm.setCollectionModes(formTypeDAO.getAllByType(true, DelType.collectionmode));
          pForm.setMetaPurposes(formTypeDAO.getAllByType(true, DelType.metaporpose));
          pForm.setPrimaryContributor(contributorDAO.findPrimaryContributorByProject(pdto));
        } // load /access data
        else if (call.equals("ACCESS")) {
          pForm.setStudies(studyDAO.findAllStudiesByProjectId(pdto));
        }
      }
      log.trace("Method getProjectData successfully completed");
    } else {
      log.warn("ProjectID or UserDTO is empty - NULL returned!");
      throw new DataWizException("ProjectID or UserDTO is empty - getProjectForm aborted!");
    }
  }

  protected boolean saveOrUpdateProject(ProjectForm pForm, ProjectDAO projectDAO, RoleDAO roleDAO) {
    boolean error = false;
    try {
      UserDTO user = UserUtil.getCurrentUser();
      if (pForm != null && pForm.getProject() != null && user != null) {
        if (pForm.getProject().getId() <= 0) {
          pForm.getProject().setOwnerId(user.getId());
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
      log.error("Project saving not sucessful error:", e);
      error = true;
    }
    return error;
  }

  protected boolean checkProjectRoles(final UserDTO user, final long pid, final boolean onlyWrite,
      final boolean withDSRights) {
    if (user.hasRole(Roles.ADMIN)) {
      log.debug("User {} is DataWiz Admin");
      return true;
    } else if (user.hasRole(Roles.PROJECT_ADMIN, Optional.of(pid), false)) {
      log.debug("User {} has PROJECT_ADMIN role for project [id: {}]", () -> user.getEmail(), () -> pid);
      return true;
    } else if (user.hasRole(Roles.PROJECT_WRITER, Optional.of(pid), false)) {
      log.debug("User {} has PROJECT_WRITER role for project [id: {}]", () -> user.getEmail(), () -> pid);
      return true;
    } else if (!onlyWrite && user.hasRole(Roles.PROJECT_READER, Optional.of(pid), false)) {
      log.debug("User {} has PROJECT_READER role for project [id: {}]", () -> user.getEmail(), () -> pid);
      return true;
    } else if (withDSRights && user.hasRole(Roles.DS_WRITER, Optional.of(pid), false)) {
      log.debug("User {} has DS_WRITER role for project [id: {}]", () -> user.getEmail(), () -> pid);
      return true;
    } else if (!onlyWrite && withDSRights && user.hasRole(Roles.DS_READER, Optional.of(pid), false)) {
      log.debug("User {} has DS_READER role for project [id: {}]", () -> user.getEmail(), () -> pid);
      return true;
    }
    return false;
  }

}
