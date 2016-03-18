package de.zpid.datawiz.controller;

import java.util.Optional;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
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
import de.zpid.datawiz.dto.ProjectDTO;
import de.zpid.datawiz.dto.UserDTO;
import de.zpid.datawiz.dto.UserRoleDTO;
import de.zpid.datawiz.enumeration.Roles;
import de.zpid.datawiz.exceptions.DataWizException;
import de.zpid.datawiz.exceptions.DataWizSecurityException;
import de.zpid.datawiz.form.ProjectForm;
import de.zpid.datawiz.util.BreadCrumpUtil;
import de.zpid.datawiz.util.EmailUtil;
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

  @RequestMapping(value = { "", "/{projectId}" }, method = RequestMethod.GET)
  public String showAccessPage(@PathVariable Optional<Integer> projectId, ModelMap model,
      RedirectAttributes redirectAttributes) {
    if (log.isDebugEnabled()) {
      log.debug("execute showAccessPage for project [id:" + projectId.get() + "]");
    }
    if (!projectId.isPresent()) {
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
      pForm = ProjectController.getProjectForm(pForm, projectId.get(), user, this.projectDAO, null, null, null,
          this.studyDAO, null, "ACCESS");
      if (pForm.getProject() != null && pForm.getProject().getId() > 0) {
        pForm.setSharedUser(userDAO.findGroupedByProject(pForm.getProject()));
        pForm.setRoleList(roleDao.getAllProjectRoles());
        for (UserDTO tuser : pForm.getSharedUser()) {
          tuser.setGlobalRoles(roleDao.getRolesByUserIDAndProjectID(tuser.getId(), projectId.get()));
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

  @RequestMapping(value = { "/{projectId}/deleteUser/{userId}" })
  public String deleteUserfromProject(@PathVariable int userId, @PathVariable int projectId,
      RedirectAttributes redirectAttributes) {
    if (log.isDebugEnabled()) {
      log.debug("execute deleteUserfromProject [id:" + projectId + " userid:" + userId + "]");
    }
    UserDTO admin = UserUtil.getCurrentUser();
    String check = checkProjectAdmin(redirectAttributes, projectId, admin);
    if (check != null) {
      return check;
    }

    return "redirect:/access/" + projectId;
  }

  @RequestMapping(value = { "/{projectId}" }, params = { "addUser" })
  public String addUserToProject(@ModelAttribute("ProjectForm") ProjectForm pForm, @PathVariable int projectId,
      RedirectAttributes redirectAttributes, BindingResult bRes, HttpServletRequest request) {
    if (log.isDebugEnabled()) {
      log.debug("execute addUserToProject for project [id:" + projectId + "]");
    }
    UserDTO admin = UserUtil.getCurrentUser();
    String check = checkProjectAdmin(redirectAttributes, projectId, admin);
    if (check != null) {
      return check;
    }
    UserDTO user = null;
    String linkhash = null;
    try {
      user = userDAO.findByMail(pForm.getDelMail(), false);
      projectDAO.addUsertoProject(projectId, pForm.getDelMail());
      linkhash = projectDAO.getInviteHash(pForm.getDelMail(), projectId);
    } catch (Exception e) {
      if (e instanceof DuplicateKeyException)
        // TODO
        System.out.println("doublette");
      e.printStackTrace();
    }
    String adminName = (admin.getLastName() != null && !admin.getLastName().isEmpty() ? admin.getLastName() : "");
    if (!adminName.isEmpty())
      adminName = (admin.getTitle() != null && !admin.getTitle().isEmpty() ? admin.getTitle() + " " : "")
          + (admin.getFirstName() != null && !admin.getFirstName().isEmpty() ? admin.getFirstName() + " " : "")
          + adminName;
    if (!adminName.isEmpty())
      adminName = adminName + "( " + admin.getEmail() + " )";
    else
      adminName = admin.getEmail();
    if (user != null && user.getId() > 0 && linkhash != null && !linkhash.isEmpty()) {
      try {
        EmailUtil.sendSSLMail(user.getEmail(),
            messageSource.getMessage("reg.mail.subject", null, LocaleContextHolder.getLocale()),
            messageSource.getMessage("roles.inv.dw.user.to.project", new Object[] { adminName,
                pForm.getProject().getTitle(), request.getRequestURL(), user.getEmail(), linkhash },
                LocaleContextHolder.getLocale()));
      } catch (MessagingException e) {
        log.error("Mail error during user registration: " + e.getStackTrace());
        bRes.reject("globalErrors", "email");
        return "access";
      }
    } else {
      // TODO -> Einladung zu datawiz senden!
    }

    return "redirect:/access/" + projectId;
  }

  @RequestMapping(value = { "/{projectId}/acceptInvite/{email}/{linkhash}/" })
  public String acceptInvite(@PathVariable String email, @PathVariable long projectId, @PathVariable String linkhash,
      RedirectAttributes redirectAttributes) {
    if (log.isDebugEnabled()) {
      log.debug("execute accepptInvite User [email:" + email + " Project:" + projectId + "]");
    }
    UserDTO admin = UserUtil.getCurrentUser();
    if (!admin.getEmail().equals(email)) {
      log.warn("Invite acception failed because currentUser is not the invited User CurrentUser [email: "
          + admin.getEmail() + "] InvitedUser [email: " + email + "]");
      redirectAttributes.addFlashAttribute("errorMSG", "Fehler in der Matrix!!!!");
    }
    try {
      String hash = projectDAO.getInviteHash(email, projectId);
      if (hash != null && !hash.isEmpty() && !linkhash.isEmpty() && linkhash.trim().equals(hash.trim())) {
        UserRoleDTO role = new UserRoleDTO(Roles.REL_ROLE.toInt(), admin.getId(), projectId, 0, Roles.REL_ROLE.name());
        roleDao.setRole(role);
        projectDAO.deleteInvitationEntree(projectId, email);
      }
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return "redirect:/panel";
  }

  @RequestMapping(value = { "/{projectId}/delete/{userId}/{roleId}",
      "/{projectId}/delete/{userId}/{roleId}/{studyId}" })
  public String deleteRole(@PathVariable long userId, @PathVariable long roleId, @PathVariable long projectId,
      @PathVariable Optional<Long> studyId, RedirectAttributes redirectAttributes, BindingResult bRes) {
    if (log.isDebugEnabled()) {
      log.debug("execute deleteRole [Role:" + roleId + " User:" + userId + " Project:" + projectId + "]");
    }
    UserRoleDTO role = new UserRoleDTO(roleId, userId, projectId, studyId.get(), "");
    UserDTO admin = UserUtil.getCurrentUser();
    String check = checkProjectAdmin(redirectAttributes, projectId, admin);
    if (check != null) {
      return check;
    }
    try {
      ProjectDTO project = projectDAO.findById(projectId);
      if (project != null && project.getOwnerId() == userId) {
        log.warn(" User " + admin.getEmail() + " tries to delete the project owner: " + userId);
        bRes.reject("globalErrors", "keine rolle");
        return "access";
      }
      roleDao.deleteRole(role);
    } catch (Exception e) {
      log.warn("ERROR: Database Error while deleting Role - Exception:" + e.getMessage());
      bRes.reject("globalErrors", "blabla");
      return "access";
    }
    return "redirect:/access/" + projectId;
  }

  /**
   * 
   * @param projectId
   * @param pForm
   * @param redirectAttributes
   * @param bRes
   * @return
   */
  @RequestMapping(value = { "/{projectId}" }, params = { "addRole" })
  public String addRoleToProjectUser(@PathVariable long projectId, @ModelAttribute("ProjectForm") ProjectForm pForm,
      RedirectAttributes redirectAttributes, BindingResult bRes) {
    if (log.isDebugEnabled()) {
      log.debug("execute addRoleToProjectUser for Project [id:" + projectId + "]");
    }
    UserDTO admin = UserUtil.getCurrentUser();
    String check = checkProjectAdmin(redirectAttributes, projectId, admin);
    if (check != null) {
      return check;
    }
    if (pForm != null && pForm.getNewRole() != null) {
      UserRoleDTO newRole = pForm.getNewRole();
      newRole.setProjectId(projectId);
      UserDTO user = null;
      if (newRole.getUserId() > 0) {
        try {
          user = userDAO.findById(newRole.getUserId());
          if (!bRes.hasErrors() && newRole.getType() != null && !newRole.getType().isEmpty()
              && !newRole.getType().equals("0")) {
            Roles role = Roles.valueOf(newRole.getType());
            newRole.setRoleId(role.toInt());
            if ((role.equals(Roles.DS_READER) || role.equals(Roles.DS_WRITER)) && newRole.getStudyId() < 1) {
              bRes.reject("globalErrors", "keine rolle");
            }
          } else {
            bRes.reject("globalErrors", "keine rolle");
          }
          if (!bRes.hasErrors() && user != null && user.getGlobalRoles() != null) {
            for (UserRoleDTO roleTmp : user.getGlobalRoles()) {
              if (roleTmp.equals(newRole)) {
                bRes.reject("globalErrors", "rolle vorhanden");
                break;
              }
            }
            if (!bRes.hasErrors())
              if (user.hasProjectRole(Roles.PROJECT_ADMIN, projectId)) {
                bRes.reject("globalErrors", "Projektadmin");
              } else if (user.hasProjectRole(Roles.PROJECT_WRITER, projectId)
                  && (newRole.getType().equals(Roles.PROJECT_READER.name())
                      || newRole.getType().equals(Roles.DS_READER.name())
                      || newRole.getType().equals(Roles.DS_WRITER.name()))) {
                bRes.reject("globalErrors", "globaler Writer > glober Raeder nicht sinnvoll, abstufung nicht sinnvoll");
              } else if (user.hasProjectRole(Roles.PROJECT_READER, projectId)
                  && newRole.getType().equals(Roles.DS_READER.name())) {
                bRes.reject("globalErrors", "globaler READER > abstufung nicht sinnvoll");
              }
          } else {
            bRes.reject("globalErrors", "benutzer nicht gefunden - wrong Pid");
          }
          if (!bRes.hasErrors())
            roleDao.setRole(newRole);
        } catch (Exception e) {
          log.warn("ERROR: Database Error while setting Role - Exception:" + e.getMessage());
          bRes.reject("globalErrors", "benutzer nicht gefunden");
        }
      } else {
        bRes.reject("globalErrors", "email");
      }
    } else {
      log.warn("ERROR: add Role not successful because ProjectForm or Role empty");
    }
    if (bRes.hasErrors())
      return "access";
    else
      return "redirect:/access/" + projectId;
  }

  private String checkProjectAdmin(RedirectAttributes redirectAttributes, long projectId, UserDTO admin) {
    if (admin == null) {
      log.warn("Auth User Object == null - redirect to login");
      return "redirect:/login";
    }
    if (!admin.hasProjectRole(Roles.PROJECT_ADMIN, projectId)) {
      redirectAttributes.addFlashAttribute("errorMSG",
          messageSource.getMessage("project.access.denied", null, LocaleContextHolder.getLocale()));
      log.warn("SECURITY: User with email: " + admin.getEmail() + " tries change roles for project:" + projectId
          + " without having permission");
      return "redirect:/panel";
    }
    return null;
  }
}
