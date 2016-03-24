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
import org.springframework.validation.BeanPropertyBindingResult;
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

// TODO: Auto-generated Javadoc
/**
 * The Class AccessController.
 */
@Controller
@RequestMapping(value = "/access")
@SessionAttributes({ "ProjectForm", "subnaviActive" })
public class AccessController {

  /** The user dao. */
  @Autowired
  private UserDAO userDAO;

  /** The message source. */
  @Autowired
  private MessageSource messageSource;

  /** The project dao. */
  @Autowired
  private ProjectDAO projectDAO;

  /** The role dao. */
  @Autowired
  private RoleDAO roleDao;

  /** The study dao. */
  @Autowired
  private StudyDAO studyDAO;

  /** The Constant log. */
  private static final Logger log = Logger.getLogger(AccessController.class);

  /** The context. */
  private ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("spring.xml");

  /**
   * Creates the project form.
   *
   * @return ProjectForm ({@link #projectForm})
   */
  @ModelAttribute("ProjectForm")
  public ProjectForm createProjectForm() {
    return (ProjectForm) context.getBean("ProjectForm");

  }

  /**
   * Show access page.
   *
   * @param projectId
   *          the project id
   * @param model
   *          the model
   * @param redirectAttributes
   *          the redirect attributes
   * @return String ({@link #string})
   */
  @RequestMapping(value = { "", "/{projectId}" }, method = RequestMethod.GET)
  public String showAccessPage(@PathVariable Optional<Long> projectId, ModelMap model,
      RedirectAttributes redirectAttributes, HttpServletRequest request) {
    if (log.isInfoEnabled()) {
      log.info("execute showAccessPage for project [id:" + projectId.get() + "]");
    }
    if (!projectId.isPresent()) {
      // TODO ausstieg wenn kein Projekt angelegt!!!!
      return "redirect:/panel";
    }
    UserDTO user = UserUtil.getCurrentUser();
    ProjectForm pForm = createProjectForm();
    try {
      user.setGlobalRoles(roleDao.findRolesByUserID(user.getId()));
      pForm = ProjectController.getProjectForm(pForm, projectId.get(), user, this.projectDAO, null, null, null,
          this.studyDAO, null, "ACCESS");
      if (pForm.getProject() != null && pForm.getProject().getId() > 0) {
        pForm.setSharedUser(userDAO.findGroupedByProject(pForm.getProject()));
        pForm.setRoleList(roleDao.findAllProjectRoles());
        pForm.setPendingMails(projectDAO.findPendingInvitesByProjectID(projectId.get()));
        for (UserDTO tuser : pForm.getSharedUser()) {
          tuser.setGlobalRoles(roleDao.findRolesByUserIDAndProjectID(tuser.getId(), projectId.get()));
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

  /**
   * Delete userfrom project.
   *
   * @param userId
   *          the user id
   * @param projectId
   *          the project id
   * @param redirectAttributes
   *          the redirect attributes
   * @return String ({@link #string})
   */
  @RequestMapping(value = { "/{projectId}/deleteUser/{userId}" })
  public String deleteUserfromProject(@PathVariable long userId, @PathVariable long projectId,
      RedirectAttributes redirectAttributes) {
    if (log.isInfoEnabled()) {
      log.info("execute deleteUserfromProject [id:" + projectId + " userid:" + userId + "]");
    }
    UserDTO admin = UserUtil.getCurrentUser();
    String check = checkProjectAdmin(redirectAttributes, projectId, admin);
    if (check != null) {
      return check;
    }
    UserRoleDTO role = new UserRoleDTO(Roles.REL_ROLE.toInt(), userId, projectId, 0, Roles.REL_ROLE.toString());
    try {
      roleDao.deleteRole(role);
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return "redirect:/access/" + projectId;
  }

  /**
   * 
   * @param model
   * @param projectId
   * @param mail
   * @param redirectAttributes
   * @param request
   * @param resend
   * @return
   */
  @RequestMapping(value = { "/{projectId}/deleteInvite/{mail}" }, method = RequestMethod.GET)
  public String deleteInvite(ModelMap model, @PathVariable long projectId, @PathVariable String mail,
      RedirectAttributes redirectAttributes, HttpServletRequest request, boolean resend) {
    if (log.isInfoEnabled()) {
      log.info("execute deleteInvite project [id:" + projectId + "] and user [mail:" + mail + "] ");
    }
    try {
      projectDAO.deleteInvitationEntree(projectId, mail);
    } catch (Exception e) {
      // TODO
      model.put("errorMSG", "error DBS");
      return "access";
    }
    return "redirect:/access/" + projectId;
  }

  /**
   * 
   * @param model
   * @param projectId
   * @param mail
   * @param redirectAttributes
   * @param request
   * @param resend
   * @return
   */
  @RequestMapping(value = { "/{projectId}/resendInvite/{mail}" }, method = RequestMethod.GET)
  public String resendInvite(ModelMap model, @PathVariable long projectId, @PathVariable String mail,
      RedirectAttributes redirectAttributes, HttpServletRequest request, boolean resend) {
    if (log.isInfoEnabled()) {
      log.info("execute resendInvitefor project [id:" + projectId + "] and user [mail:" + mail + "] ");
    }
    ProjectForm pForm = createProjectForm();
    try {
      ProjectDTO project = projectDAO.findById(projectId);
      if (project == null) {
        model.put("errorMSG", "empty project");
        return "access";
      }
      pForm.setProject(project);
      pForm.setDelMail(mail);
    } catch (Exception e) {
      model.put("errorMSG", "error DBS");
      return "access";
    }
    return addUserToProject(pForm, projectId, redirectAttributes, new BeanPropertyBindingResult(pForm, "ProjectForm"),
        request, true);
  }

  /**
   * Adds the user to project.
   *
   * @param pForm
   *          the form
   * @param projectId
   *          the project id
   * @param redirectAttributes
   *          the redirect attributes
   * @param bRes
   *          the b res
   * @param request
   *          the request
   * @return String ({@link #string})
   */
  @RequestMapping(value = { "/{projectId}" }, params = { "addUser" })
  public String addUserToProject(@ModelAttribute("ProjectForm") ProjectForm pForm, @PathVariable long projectId,
      RedirectAttributes redirectAttributes, BindingResult bRes, HttpServletRequest request, boolean resend) {
    if (log.isInfoEnabled()) {
      log.info("execute addUserToProject for project [id:" + projectId + "]");
    }
    // TODO RESEND request hierher umleiten und resent bool nutzen!
    UserDTO admin = UserUtil.getCurrentUser();
    String check = checkProjectAdmin(redirectAttributes, projectId, admin);
    if (check != null) {
      return check;
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
    UserDTO user = null;
    String linkhash = null;
    try {
      user = userDAO.findByMail(pForm.getDelMail(), false);
      if (resend != true)
        projectDAO.insertInviteEntree(projectId, pForm.getDelMail(), admin.getEmail());
      linkhash = projectDAO.getValFromInviteData(pForm.getDelMail(), projectId, "linkhash");
    } catch (Exception e) {
      if (e instanceof DuplicateKeyException) {
        bRes.reject("globalErrors", "doublette");
        return "access";
      }
      e.printStackTrace();
    }
    String subject = null;
    String content = null;
    String url = null;
    if (linkhash != null && !linkhash.isEmpty()) {
      if (user != null && user.getId() > 0) {
        if (log.isDebugEnabled()) {
          log.debug("User [email:" + user.getEmail()
              + "] has an active datawiz account - sending project invitation to primary email");
        }
        subject = "inv.mail.project.subject";
        content = "inv.mail.project.content";
        url = request.getRequestURL().toString().replace(request.getRequestURI(),
            request.getContextPath() + "/access/" + projectId + "/acceptInvite/" + pForm.getDelMail() + "/" + linkhash);
      } else {
        if (log.isDebugEnabled()) {
          log.debug("User [email:" + pForm.getDelMail()
              + "] hasn't an active datawiz account - sending datawiz invitation to email");
        }
        subject = "inv.mail.dw.subject";
        content = "inv.mail.dw.content";
        url = request.getRequestURL().toString().replace(request.getRequestURI(),
            request.getContextPath() + "/register/" + projectId + "/" + pForm.getDelMail() + "/" + linkhash);
      }
      if (subject != null && content != null && url != null) {
        try {
          EmailUtil.sendSSLMail(pForm.getDelMail(),
              messageSource.getMessage(subject, new Object[] { pForm.getProject().getTitle() },
                  LocaleContextHolder.getLocale()),
              messageSource.getMessage(content, new Object[] { adminName, pForm.getProject().getTitle(), url },
                  LocaleContextHolder.getLocale()));
        } catch (MessagingException e) {
          log.error("Mail error during user registration: " + e.getStackTrace());
          bRes.reject("globalErrors", "email");
          return "access";
        }
      } else {
        // TODO URL ERROR
      }
    } else {
      // TODO linkhash error
    }
    return "redirect:/access/" + projectId;
  }

  /**
   * This function is called from the invitation mail. <br />
   * Before it saves the REL_ROLE (Relation Role), it checks the project's availability and if the calling user equals
   * the user of the link address
   *
   * @param email
   *          the email
   * @param projectId
   *          the project id
   * @param linkhash
   *          the linkhash
   * @param redirectAttributes
   *          the redirect attributes
   * @param request
   *          the request
   * @return String
   */
  @RequestMapping(value = { "/{projectId}/acceptInvite/{email}/{linkhash}" })
  public String acceptInvite(@PathVariable String email, @PathVariable long projectId, @PathVariable String linkhash,
      RedirectAttributes redirectAttributes, HttpServletRequest request) {
    if (log.isInfoEnabled()) {
      log.info("execute accepptInvite User [email:" + email + " Project:" + projectId + "]");
    }
    UserDTO admin = UserUtil.getCurrentUser();
    if (!admin.getEmail().equals(email)) {
      log.warn("Invite acception failed because currentUser is not the invited User CurrentUser [email: "
          + admin.getEmail() + "] InvitedUser [email: " + email + "]");
      redirectAttributes.addFlashAttribute("errorMSG", "angemneldeter nutzer != nutzer im link");
      return "redirect:/panel";
    }
    String adminMail = null;
    ProjectDTO project = null;
    try {
      project = projectDAO.findById(projectId);
      if (project != null) {
        adminMail = projectDAO.getValFromInviteData(email, projectId, "invited_by");
        String hash = projectDAO.getValFromInviteData(email, projectId, "linkhash");
        if (hash != null && !hash.isEmpty() && !linkhash.isEmpty() && linkhash.trim().equals(hash.trim())) {
          UserRoleDTO role = new UserRoleDTO(Roles.REL_ROLE.toInt(), admin.getId(), projectId, 0,
              Roles.REL_ROLE.name());
          roleDao.setRole(role);
          projectDAO.deleteInvitationEntree(projectId, email);
        }
        StringBuffer url = request.getRequestURL();
        url = url.delete(url.indexOf(request.getRequestURI()), url.length()).append(request.getContextPath());
        EmailUtil.sendSSLMail(adminMail,
            messageSource.getMessage("accept.mail.admin.subject", null, LocaleContextHolder.getLocale()),
            messageSource.getMessage("accept.mail.admin.content",
                new Object[] { email, project.getTitle(), url + "/access/" + project.getId() },
                LocaleContextHolder.getLocale()));
        redirectAttributes.addFlashAttribute("infoMSG", "hinzugefügt");
      } else {
        redirectAttributes.addFlashAttribute("errorMSG", "Projekt leer - möglicherweise gelöscht");
      }
    } catch (Exception e) {
      if (e instanceof MessagingException) {
        redirectAttributes.addFlashAttribute("errorMSG", "Fehler beim emailversand");
      }
      redirectAttributes.addFlashAttribute("errorMSG", "Datenbankfehler");
    }
    return "redirect:/panel";
  }

  /**
   * Delete role from a project User <br />
   * It is not possible to delete the project owner - it's id is saved in the owner row in the dw_project table
   *
   * @param userId
   *          the user id
   * @param roleId
   *          the role id
   * @param projectId
   *          the project id
   * @param studyId
   *          the study id
   * @param redirectAttributes
   *          the redirect attributes
   * @param bRes
   *          the b res
   * @return String ({@link #string})
   */
  @RequestMapping(value = { "/{projectId}/delete/{userId}/{roleId}",
      "/{projectId}/delete/{userId}/{roleId}/{studyId}" })
  public String deleteRole(@PathVariable long userId, @PathVariable long roleId, @PathVariable long projectId,
      @PathVariable Optional<Long> studyId, RedirectAttributes redirectAttributes) {
    if (log.isInfoEnabled()) {
      log.info("execute deleteRole [Role:" + roleId + " User:" + userId + " Project:" + projectId + "]");
    }
    UserRoleDTO role = new UserRoleDTO(roleId, userId, projectId, studyId.isPresent() ? studyId.get() : 1, "");
    UserDTO admin = UserUtil.getCurrentUser();
    String check = checkProjectAdmin(redirectAttributes, projectId, admin);
    if (check != null) {
      return check;
    }
    try {
      // TODO nicht selber löschen
      ProjectDTO project = projectDAO.findById(projectId);
      if (project != null && project.getOwnerId() == userId) {
        // TODO ausstieg
        redirectAttributes.addFlashAttribute("errorMSG",
            messageSource.getMessage("project.access.denied", null, LocaleContextHolder.getLocale()));
        log.warn(" User " + admin.getEmail() + " tries to delete the project owner: " + userId);
      }
      roleDao.deleteRole(role);
    } catch (Exception e) {
      // TODO meldung an view
      log.warn("ERROR: Database Error while deleting Role - Exception:" + e.getMessage());
      return "access";
    }
    // TODO meldung bei erfolg -> fehler abfangen
    redirectAttributes.addFlashAttribute("infoMSG",
        messageSource.getMessage("project.access.denied", null, LocaleContextHolder.getLocale()));
    return "redirect:/access/" + projectId;
  }

  /**
   * Adds a role to a project user
   * 
   * 
   *
   * @param projectId
   * @param pForm
   * @param redirectAttributes
   * @param bRes
   * @return String
   */
  @RequestMapping(value = { "/{projectId}" }, params = { "addRole" })
  public String addRoleToProjectUser(@PathVariable long projectId, @ModelAttribute("ProjectForm") ProjectForm pForm,
      RedirectAttributes redirectAttributes, BindingResult bRes) {
    if (log.isInfoEnabled()) {
      log.info("execute addRoleToProjectUser for Project [id:" + projectId + "]");
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
              bRes.reject("globalErrors",
                  messageSource.getMessage("roles.error.study.set", null, LocaleContextHolder.getLocale()));
            }
          } else {
            bRes.reject("globalErrors",
                messageSource.getMessage("roles.error.no.role", null, LocaleContextHolder.getLocale()));
          }
          if (!bRes.hasErrors() && user != null && user.getGlobalRoles() != null) {
            for (UserRoleDTO roleTmp : user.getGlobalRoles()) {
              if (roleTmp.equals(newRole)) {
                bRes.reject("globalErrors",
                    messageSource.getMessage("roles.error.role.equal", null, LocaleContextHolder.getLocale()));
                break;
              }
            }
            if (!bRes.hasErrors())
              if (user.hasProjectRole(Roles.PROJECT_ADMIN, projectId)) {
                bRes.reject("globalErrors",
                    messageSource.getMessage("roles.error.global.admin", null, LocaleContextHolder.getLocale()));
              } else if (user.hasProjectRole(Roles.PROJECT_WRITER, projectId)
                  && (newRole.getType().equals(Roles.PROJECT_READER.name())
                      || newRole.getType().equals(Roles.DS_READER.name())
                      || newRole.getType().equals(Roles.DS_WRITER.name()))) {
                bRes.reject("globalErrors",
                    messageSource.getMessage("roles.error.global.write", null, LocaleContextHolder.getLocale()));
              } else if (user.hasProjectRole(Roles.PROJECT_READER, projectId)
                  && newRole.getType().equals(Roles.DS_READER.name())) {
                bRes.reject("globalErrors",
                    messageSource.getMessage("roles.error.global.read", null, LocaleContextHolder.getLocale()));
              }
          } else {
            if (!bRes.hasErrors())
              bRes.reject("globalErrors",
                  messageSource.getMessage("roles.error.user.not.found", null, LocaleContextHolder.getLocale()));
          }
          if (!bRes.hasErrors()) {
            int chk = roleDao.setRole(newRole);
            // cleanup roles
            if (chk > 0 && user.getGlobalRoles() != null)
              for (UserRoleDTO roleTmp : user.getGlobalRoles()) {
                if (newRole.getType().equals(Roles.PROJECT_ADMIN.name())
                    && !roleTmp.getType().equals(Roles.REL_ROLE.name()) && roleTmp.getProjectId() > 0) {
                  roleDao.deleteRole(roleTmp);
                } else if (newRole.getType().equals(Roles.PROJECT_READER.name())
                    && roleTmp.getType().equals(Roles.DS_READER.name())) {
                  roleDao.deleteRole(roleTmp);
                } else if (newRole.getType().equals(Roles.PROJECT_WRITER.name())
                    && (roleTmp.getType().equals(Roles.DS_WRITER.name())
                        || roleTmp.getType().equals(Roles.DS_READER.name())
                        || roleTmp.getType().equals(Roles.PROJECT_READER.name()))) {
                  roleDao.deleteRole(roleTmp);
                } else if (newRole.getType().equals(Roles.DS_WRITER.name())
                    && newRole.getStudyId() == roleTmp.getStudyId()
                    && roleTmp.getType().equals(Roles.DS_READER.name())) {
                  roleDao.deleteRole(roleTmp);
                }
              }
          }
        } catch (Exception e) {
          log.warn("ERROR: Database Error while setting Role - Exception:" + e.getMessage());
          bRes.reject("globalErrors",
              messageSource.getMessage("roles.error.db", null, LocaleContextHolder.getLocale()));
        }
      } else {
        bRes.reject("globalErrors", "email");
      }
    } else {
      log.warn("ERROR: add Role not successful because ProjectForm or Role empty");
      bRes.reject("globalErrors",
          messageSource.getMessage("roles.error.empty.form", null, LocaleContextHolder.getLocale()));
    }
    if (bRes.hasErrors())
      return "access";
    else
      return "redirect:/access/" + projectId;
  }

  /**
   * Checks if the passed UserDTO has the PROJECT_ADMIN role.
   *
   * @param redirectAttributes
   * @param projectId
   * @param admin
   * @return redirect String if role is non-existent, and null if existent
   */
  private String checkProjectAdmin(RedirectAttributes redirectAttributes, long projectId, UserDTO admin) {
    if (admin == null) {
      log.warn("Auth User Object == null - redirect to login");
      return "redirect:/login";
    }
    try {
      admin.setGlobalRoles(roleDao.findRolesByUserID(admin.getId()));
    } catch (Exception e) {
      e.printStackTrace();
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
}
