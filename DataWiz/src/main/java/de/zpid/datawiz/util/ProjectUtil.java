package de.zpid.datawiz.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Scope;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import de.zpid.datawiz.dao.ContributorDAO;
import de.zpid.datawiz.dao.DmpDAO;
import de.zpid.datawiz.dao.FileDAO;
import de.zpid.datawiz.dao.FormTypesDAO;
import de.zpid.datawiz.dao.ProjectDAO;
import de.zpid.datawiz.dao.RoleDAO;
import de.zpid.datawiz.dao.StudyDAO;
import de.zpid.datawiz.dao.TagDAO;
import de.zpid.datawiz.dto.DmpDTO;
import de.zpid.datawiz.dto.ProjectDTO;
import de.zpid.datawiz.dto.StudyDTO;
import de.zpid.datawiz.dto.UserDTO;
import de.zpid.datawiz.dto.UserRoleDTO;
import de.zpid.datawiz.enumeration.DelType;
import de.zpid.datawiz.enumeration.PageState;
import de.zpid.datawiz.enumeration.Roles;
import de.zpid.datawiz.exceptions.DataWizException;
import de.zpid.datawiz.exceptions.DataWizSecurityException;
import de.zpid.datawiz.form.ProjectForm;

@Service
@Scope("singleton")
public class ProjectUtil {

  @Autowired
  private MessageSource messageSource;
  @Autowired
  private ProjectDAO projectDAO;
  @Autowired
  private RoleDAO roleDAO;
  @Autowired
  private StudyDAO studyDAO;
  @Autowired
  private TagDAO tagDAO;
  @Autowired
  private FileDAO fileDAO;
  @Autowired
  private ContributorDAO contributorDAO;
  @Autowired
  private FormTypesDAO formTypeDAO;
  @Autowired
  private DmpDAO dmpDAO;

  private ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("spring.xml");

  private Logger log = LogManager.getLogger(getClass());

  /**
   * Checks if the passed UserDTO has the PROJECT_ADMIN role.
   *
   * @param redirectAttributes
   * @param projectId
   * @param admin
   * @return String with redirect entry if role is non-existent, and null if existent
   */
  public String checkProjectAdmin(final RedirectAttributes redirectAttributes, final long projectId,
      final UserDTO admin) {
    log.trace("Entering checkProjectAdmin(SuperController) for project [id: {}] and user [mail: {}] ", () -> projectId,
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
    log.trace("Method checkProjectAdmin(SuperController) completed");
    return ret;
  }

  /**
   * 
   * @param pid
   * @param user
   * @return
   * @throws Exception
   */
  public void getProjectForm(final ProjectForm pForm, final long pid, final UserDTO user, final PageState call,
      Roles userRole) throws Exception {
    log.trace("Entering getProjectData(SuperController) for project [id: {}] and user [mail: {}] ", () -> pid,
        () -> user.getEmail());
    // security access check!
    if (pid > 0 && user != null) {
      if (userRole == null) {
        throw new DataWizSecurityException("SECURITY: User with email: " + user.getEmail()
            + " tries to get access to project:" + pid + " without having the permissions to read");
      }
      final ProjectDTO pdto = projectDAO.findById(pid);
      if (pdto == null || pdto.getId() <= 0) {
        throw new DataWizException("Project is empty for user=" + user.getEmail() + " and project=" + pid);
      }
      pForm.setProject(pdto);
      // load all data if user has full project rights
      if (userRole.equals(Roles.ADMIN) || userRole.equals(Roles.PROJECT_ADMIN) || userRole.equals(Roles.PROJECT_READER)
          || userRole.equals(Roles.PROJECT_WRITER)) {
        // load /project data
        if (call == null || call.equals(PageState.PROJECT)) {
          pForm.setFiles(fileDAO.getProjectFiles(pdto));
          pForm.setTags(new ArrayList<String>(tagDAO.getTagsByProjectID(pdto).values()));
          pForm.setStudies(studyDAO.findAllStudiesByProjectId(pdto));
          pForm.setContributors(contributorDAO.findByProject(pdto, false, false));
          pForm.setPrimaryContributor(contributorDAO.findPrimaryContributorByProject(pdto));
        } // load /dmp data
        else if (call.equals(PageState.DMP)) {
          DmpDTO dmp = dmpDAO.getByID(pForm.getProject());
          if (dmp != null && dmp.getId() > 0) {
            dmp.setUsedDataTypes(dmpDAO.getDMPUsedDataTypes(dmp.getId(), DelType.DATATYPE));
            dmp.setUsedCollectionModes(dmpDAO.getDMPUsedDataTypes(dmp.getId(), DelType.COLLECTIONMODE));
            dmp.setSelectedMetaPurposes(dmpDAO.getDMPUsedDataTypes(dmp.getId(), DelType.METAPORPOSE));
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
          pForm.setDataTypes(formTypeDAO.getAllByType(true, DelType.DATATYPE));
          pForm.setCollectionModes(formTypeDAO.getAllByType(true, DelType.COLLECTIONMODE));
          pForm.setMetaPurposes(formTypeDAO.getAllByType(true, DelType.METAPORPOSE));
          pForm.setPrimaryContributor(contributorDAO.findPrimaryContributorByProject(pdto));
        } // load /access data
        else if (call.equals(PageState.ACCESS)) {
          pForm.setStudies(studyDAO.findAllStudiesByProjectId(pdto));
        } // load /export data
        else if (call.equals(PageState.EXPORT)) {

        }
      } else if (userRole.equals(Roles.DS_READER) || userRole.equals(Roles.DS_WRITER)) {
        List<UserRoleDTO> userRoles = roleDAO.findRolesByUserIDAndProjectID(user.getId(), pid);
        List<StudyDTO> cStud = new ArrayList<StudyDTO>();
        for (UserRoleDTO role : userRoles) {
          Roles uRole = Roles.valueOf(role.getType());
          if (role.getStudyId() > 0 && (uRole.equals(Roles.DS_READER) || uRole.equals(Roles.DS_WRITER))) {
            cStud.add(studyDAO.findById(role.getStudyId()));
          }
        }
        pForm.setStudies(cStud);
      }
      log.trace("Method getProjectData(SuperController) successfully completed");
    } else {
      log.warn("ProjectID or UserDTO is empty - NULL returned!");
      throw new DataWizException("ProjectID or UserDTO is empty - getProjectForm(SuperController) aborted!");
    }
  }

  /**
   * 
   * @param pForm
   * @return
   */
  public boolean saveOrUpdateProject(final ProjectForm pForm) {
    log.trace("Entering saveOrUpdateProject(SuperController) for project [id: {}] ", () -> pForm.getProject().getId());
    boolean success = true;
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
            success = false;
          }
        } else {
          projectDAO.updateProject(pForm.getProject());
        }
      } else {
        success = false;
      }
      if (UserUtil.getCurrentUser() != null)
        UserUtil.getCurrentUser().setGlobalRoles(roleDAO.findRolesByUserID(user.getId()));
    } catch (Exception e) {
      log.error("Project saving not sucessful error:", e);
      success = false;
    }
    log.trace("Method saveOrUpdateProject(SuperController) completed with sucess={}", success);
    return success;
  }

  /**
   * 
   * @param user
   * @param pid
   * @param onlyWrite
   * @param withDSRights
   * @return
   */
  public Roles checkProjectRoles(final UserDTO user, final long pid, final boolean onlyWrite,
      final boolean withDSRights) {
    log.trace(
        "Entering checkProjectRoles(SuperController) for user [id: {}], project [id: {}], with Rights to write only [{}] and Studyrights [{}] ",
        () -> user.getId(), () -> pid, () -> onlyWrite, () -> withDSRights);
    try {
      if (user.hasRole(Roles.ADMIN)) {
        log.debug("User {} is DataWiz Admin", () -> user.getEmail());
        return Roles.ADMIN;
      }
      final List<UserRoleDTO> userRoles = roleDAO.findRolesByUserIDAndProjectID(user.getId(), pid);
      if (userRoles == null || userRoles.size() <= 0)
        return null;
      userRoles.sort((o1, o2) -> Long.compare(o1.getRoleId(), o2.getRoleId()));
      for (UserRoleDTO role : userRoles) {
        Roles uRole = Roles.valueOf(role.getType());
        if (uRole.equals(Roles.PROJECT_ADMIN)) {
          log.debug("User {} has PROJECT_ADMIN role for project [id: {}]", () -> user.getEmail(), () -> pid);
          return Roles.PROJECT_ADMIN;
        } else if (uRole.equals(Roles.PROJECT_WRITER)) {
          log.debug("User {} has PROJECT_WRITER role for project [id: {}]", () -> user.getEmail(), () -> pid);
          return Roles.PROJECT_WRITER;
        } else if (!onlyWrite && uRole.equals(Roles.PROJECT_READER)) {
          log.debug("User {} has PROJECT_READER role for project [id: {}]", () -> user.getEmail(), () -> pid);
          return Roles.PROJECT_READER;
        } else if (withDSRights && uRole.equals(Roles.DS_WRITER)) {
          log.debug("User {} has DS_WRITER role for project [id: {}]", () -> user.getEmail(), () -> pid);
          return Roles.DS_WRITER;
        } else if (!onlyWrite && withDSRights && uRole.equals(Roles.DS_READER)) {
          log.debug("User {} has DS_READER role for project [id: {}]", () -> user.getEmail(), () -> pid);
          return Roles.DS_READER;
        }
      }
    } catch (Exception e) {
      log.error("checkProjectRoles not sucessful -> return null! error:", e);
      return null;
    }
    log.debug("Method gcheckProjectRoles(SuperController) ended without result for user [id: {}] and project [id: {}]",
        () -> user.getId(), () -> pid);
    return null;
  }

}
