package de.zpid.datawiz.controller;

import java.util.Optional;

import javax.mail.MessagingException;

import org.apache.log4j.Level;
import org.springframework.context.i18n.LocaleContextHolder;
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

/**
 * The Class AccessController.
 */
@Controller
@RequestMapping(value = "/access")
@SessionAttributes({ "ProjectForm", "subnaviActive" })
public class AccessController extends SuperController {

  public AccessController() {
    super();
    if (log.isEnabledFor(Level.INFO))
      log.info("Loading AccessController for mapping /access");
  }

  /**
   * Creates the project form.
   *
   * @return ProjectForm
   */
  @ModelAttribute("ProjectForm")
  public ProjectForm createProjectForm() {
    return (ProjectForm) context.getBean("ProjectForm");
  }

  /**
   * Show access page.
   *
   * @param projectId
   * @param model
   * @param reAtt
   * @return String 
   */
  @RequestMapping(value = { "", "/{projectId}" }, method = RequestMethod.GET)
  public String showAccessPage(@PathVariable final Optional<Long> projectId, final ModelMap model,
      final RedirectAttributes reAtt) {
    if (log.isEnabledFor(Level.DEBUG)) {
      log.debug("Entering showAccessPage for project [id:" + projectId.get() + "]");
    }
    if (!projectId.isPresent()) {
      reAtt.addFlashAttribute("errorMSG",
          messageSource.getMessage("roles.error.empty.form", null, LocaleContextHolder.getLocale()));
      return "redirect:/panel";
    }
    UserDTO user = UserUtil.getCurrentUser();
    ProjectForm pForm = createProjectForm();
    try {
      user.setGlobalRoles(roleDAO.findRolesByUserID(user.getId()));
      pForm = getProjectForm(pForm, projectId.get(), user, "ACCESS");
      if (pForm.getProject() != null && pForm.getProject().getId() > 0) {
        pForm.setSharedUser(userDAO.findGroupedByProject(pForm.getProject()));
        pForm.setRoleList(roleDAO.findAllProjectRoles());
        pForm.setPendingMails(projectDAO.findPendingInvitesByProjectID(projectId.get()));
        for (UserDTO tuser : pForm.getSharedUser()) {
          tuser.setGlobalRoles(roleDAO.findRolesByUserIDAndProjectID(tuser.getId(), projectId.get()));
        }
      }
    } catch (Exception e) {
      String redirectMessage = "";
      if (e instanceof DataWizException) {
        redirectMessage = "project.not.available";
        if (log.isEnabledFor(Level.WARN))
          log.warn("WARN: No Project available for user [email: " + user.getEmail() + "] and project [id: " + projectId
              + "]");
      } else if (e instanceof DataWizSecurityException) {
        redirectMessage = "project.access.denied";
        if (log.isEnabledFor(Level.WARN))
          log.warn("WARN: user [email: " + user.getEmail() + "] tried to get access to project [id: " + projectId
              + "] without having a role");
      } else {
        redirectMessage = "dbs.sql.exception";
        if (log.isEnabledFor(Level.ERROR))
          log.error("ERROR: Database error during database transaction, showAccessPage aborted - Exception:", e);
      }
      reAtt.addFlashAttribute("errorMSG",
          messageSource.getMessage(redirectMessage, null, LocaleContextHolder.getLocale()));
      return "redirect:/panel";
    }
    model.put("breadcrumpList", BreadCrumpUtil.generateBC("access"));
    model.put("subnaviActive", "ACCESS");
    model.put("ProjectForm", pForm);
    if (log.isEnabledFor(Level.DEBUG)) {
      log.debug("Method showAccessPage successfully completed");
    }
    return "access";
  }

  /**
   * Delete user from project.
   *
   * @param userId
   *          the user id
   * @param projectId
   *          the project id
   * @param reAtt
   *          the redirect attributes
   * @return String ({@link #string})
   */
  @RequestMapping(value = { "/{projectId}/deleteUser/{userId}" })
  public String deleteUserfromProject(@PathVariable final long userId, @PathVariable final long projectId,
      final RedirectAttributes reAtt, final ModelMap model) {
    if (log.isEnabledFor(Level.DEBUG)) {
      log.debug("Entering deleteUserfromProject [id:" + projectId + " userid:" + userId + "]");
    }
    UserDTO admin = UserUtil.getCurrentUser();
    String check = checkProjectAdmin(reAtt, projectId, admin);
    if (check != null) {
      return check;
    }
    UserRoleDTO role = new UserRoleDTO(Roles.REL_ROLE.toInt(), userId, projectId, 0, Roles.REL_ROLE.toString());
    try {
      roleDAO.deleteRole(role);
    } catch (Exception e) {
      if (log.isEnabledFor(Level.ERROR))
        log.error("ERROR: Database error during database transaction, deleteInvite aborted - Exception:", e);
      model.put("errorMSG", messageSource.getMessage("dbs.sql.exception", null, LocaleContextHolder.getLocale()));
      return "access";
    }
    if (log.isEnabledFor(Level.DEBUG)) {
      log.debug("Method deleteUserfromProject successfully completed");
    }
    return "redirect:/access/" + projectId;
  }

  /**
   * Deletes an open project invitation
   * 
   * @param model
   * @param projectId
   * @param mail
   * @param reAtt
   * @param request
   * @param resend
   * @return redirect to access/{projectId} if successful and to access.jsp if an error has occurred
   */
  @RequestMapping(value = { "/{projectId}/deleteInvite/{mail}" }, method = RequestMethod.GET)
  public String deleteInvite(final ModelMap model, @PathVariable long projectId, @PathVariable final String mail,
      final RedirectAttributes reAtt, final boolean resend) {
    if (log.isEnabledFor(Level.DEBUG)) {
      log.debug("Entering deleteInvite project [id:" + projectId + "] and user [mail:" + mail + "] ");
    }
    try {
      projectDAO.deleteInvitationEntree(projectId, mail);
    } catch (Exception e) {
      if (log.isEnabledFor(Level.ERROR))
        log.error("ERROR: Database error during database transaction, deleteInvite aborted - Exception:", e);
      model.put("errorMSG", messageSource.getMessage("dbs.sql.exception", null, LocaleContextHolder.getLocale()));
      return "access";
    }
    if (log.isEnabledFor(Level.DEBUG)) {
      log.debug("Method deleteInvite successfully completed");
    }
    return "redirect:/access/" + projectId;
  }

  /**
   * Resends an invitation email to the submitted email For sending, the addUserToProject Method is used
   * 
   * @param model
   * @param projectId
   * @param mail
   * @param reAtt
   * @param resend
   * @return addUserToProject Method
   */
  @RequestMapping(value = { "/{projectId}/resendInvite/{mail}" }, method = RequestMethod.GET)
  public String resendInvite(final ModelMap model, @PathVariable final long projectId, @PathVariable final String mail,
      final RedirectAttributes reAtt, final boolean resend) {
    if (log.isEnabledFor(Level.DEBUG)) {
      log.debug("Entering resendInvite for project [id:" + projectId + "] and user [mail:" + mail + "] ");
    }
    ProjectForm pForm = createProjectForm();
    try {
      ProjectDTO project = projectDAO.findById(projectId);
      if (project == null) {
        if (log.isEnabledFor(Level.WARN))
          log.warn("WARN: no project found for id:" + projectId);
        model.put("errorMSG",
            messageSource.getMessage("roles.error.empty.form", null, LocaleContextHolder.getLocale()));
        model.put("errorMSG", "empty project");
        return "access";
      }
      pForm.setProject(project);
      pForm.setDelMail(mail);
    } catch (Exception e) {
      if (log.isEnabledFor(Level.ERROR))
        log.error("ERROR: Database error during database transaction, resendInvite aborted - Exception:", e);
      model.put("errorMSG", messageSource.getMessage("dbs.sql.exception", null, LocaleContextHolder.getLocale()));
      return "access";
    }
    if (log.isEnabledFor(Level.DEBUG)) {
      log.debug("Method resendInvite successfully completed");
    }
    return addUserToProject(pForm, projectId, reAtt, new BeanPropertyBindingResult(pForm, "ProjectForm"), true);
  }

  /**
   * Adds a user to project or sending a reminder mail if boolean resend is true. Checks if an user exists for the
   * submitted mail address: if yes, a project invitation is send to the user, if no, a datawiz invitation is sent to
   * the submitted email
   *
   * @param pForm
   * @param projectId
   * @param reAtt
   * @param bRes
   * @param resend
   * @return redirect to access/{projectId} if successful and to access.jsp if an error has occurred
   */
  @RequestMapping(value = { "/{projectId}" }, params = { "addUser" })
  public String addUserToProject(@ModelAttribute("ProjectForm") final ProjectForm pForm,
      @PathVariable final long projectId, final RedirectAttributes reAtt, final BindingResult bRes,
      final boolean resend) {
    if (log.isEnabledFor(Level.DEBUG)) {
      log.debug("Entering addUserToProject for project [id:" + projectId + "]");
    }
    UserDTO admin = UserUtil.getCurrentUser();
    String check = checkProjectAdmin(reAtt, projectId, admin);
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
        bRes.reject("globalErrors",
            messageSource.getMessage("roles.error.doublet", null, LocaleContextHolder.getLocale()));
      }
      if (log.isEnabledFor(Level.ERROR))
        log.error("ERROR: Database error during database transaction, addUserToProject aborted - Exception:", e);
      reAtt.addFlashAttribute("errorMSG",
          messageSource.getMessage("dbs.sql.exception", null, LocaleContextHolder.getLocale()));
    }
    String subject = null;
    String content = null;
    String url = null;
    if (!bRes.hasErrors()) {
      if (linkhash != null && !linkhash.isEmpty()) {
        if (user != null && user.getId() > 0) {
          if (log.isEnabledFor(Level.DEBUG)) {
            log.debug("User [email:" + user.getEmail()
                + "] has an active datawiz account - sending project invitation to primary email");
          }
          subject = "inv.mail.project.subject";
          content = "inv.mail.project.content";
          url = request.getRequestURL().toString().replace(request.getRequestURI(), request.getContextPath()
              + "/access/" + projectId + "/acceptInvite/" + pForm.getDelMail() + "/" + linkhash);
        } else {
          if (log.isEnabledFor(Level.DEBUG)) {
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
            if (log.isEnabledFor(Level.ERROR))
              log.error("ERROR: Mail error, Mail was not sent - Exception:", e);
            bRes.reject("globalErrors",
                messageSource.getMessage("send.mail.exception", null, LocaleContextHolder.getLocale()));
          }
        } else {
          if (log.isEnabledFor(Level.WARN))
            log.warn("WARN: add User not successful something went wrot with the requestURL url:" + url);
          bRes.reject("globalErrors",
              messageSource.getMessage("roles.error.empty.form", null, LocaleContextHolder.getLocale()));
        }
      } else {
        if (log.isEnabledFor(Level.WARN))
          log.warn("WARN: add User not successful because the hashcode is empty");
        bRes.reject("globalErrors",
            messageSource.getMessage("roles.error.empty.form", null, LocaleContextHolder.getLocale()));
      }
    }
    if (bRes.hasErrors()) {
      if (log.isEnabledFor(Level.DEBUG)) {
        log.debug("Method addUserToProject completed with errors - return to access.jsp");
      }
      return "access";
    }
    if (log.isEnabledFor(Level.DEBUG)) {
      log.debug("Method addUserToProject successfully completed");
    }
    return "redirect:/access/" + projectId;
  }

  /**
   * This function is called from the invitation mail. <br />
   * Before it saves the REL_ROLE (Relation Role), it checks the project's availability and if the calling user equals
   * the user of the link address <br />
   * If the invitation is successful, a mail is sent to the inviting administrator, to remember him to add access rights
   * to the new project user
   *
   * @param email
   * @param projectId
   * @param linkhash
   * @param reAtt
   * @param request
   * @return redirect to the panel, because the user has no rights to read or write yet
   */
  @RequestMapping(value = { "/{projectId}/acceptInvite/{email}/{linkhash}" })
  public String acceptInvite(@PathVariable final String email, @PathVariable final long projectId,
      @PathVariable final String linkhash, final RedirectAttributes reAtt) {
    if (log.isEnabledFor(Level.DEBUG)) {
      log.debug("Entering accepptInvite User [email:" + email + " Project:" + projectId + "]");
    }
    UserDTO admin = UserUtil.getCurrentUser();
    if (!admin.getEmail().equals(email)) {
      if (log.isEnabledFor(Level.WARN))
        log.warn("WARN: Invite acception failed because currentUser is not the invited User - CurrentUser: [email: "
            + admin.getEmail() + "] InvitedUser: [email: " + email + "]");
      reAtt.addFlashAttribute("errorMSG",
          messageSource.getMessage("roles.wrong.account", null, LocaleContextHolder.getLocale()));
      return "redirect:/panel";
    }
    String adminMail;
    ProjectDTO project;
    try {
      project = projectDAO.findById(projectId);
      if (project != null) {
        adminMail = projectDAO.getValFromInviteData(email, projectId, "invited_by");
        if (adminMail != null && !adminMail.isEmpty()) {
          String hash = projectDAO.getValFromInviteData(email, projectId, "linkhash");
          if (hash != null && !hash.isEmpty() && !linkhash.isEmpty() && linkhash.trim().equals(hash.trim())) {
            UserRoleDTO role = new UserRoleDTO(Roles.REL_ROLE.toInt(), admin.getId(), projectId, 0,
                Roles.REL_ROLE.name());
            roleDAO.setRole(role);
            projectDAO.deleteInvitationEntree(projectId, email);
          }
          StringBuffer url = request.getRequestURL();
          url = url.delete(url.indexOf(request.getRequestURI()), url.length()).append(request.getContextPath());
          EmailUtil.sendSSLMail(adminMail,
              messageSource.getMessage("accept.mail.admin.subject", null, LocaleContextHolder.getLocale()),
              messageSource.getMessage("accept.mail.admin.content",
                  new Object[] { email, project.getTitle(), url + "/access/" + project.getId() },
                  LocaleContextHolder.getLocale()));
          reAtt.addFlashAttribute("infoMSG",
              messageSource.getMessage("roles.success.accept", null, LocaleContextHolder.getLocale()));
        } else {
          reAtt.addFlashAttribute("infoMSG",
              messageSource.getMessage("roles.error.accept", null, LocaleContextHolder.getLocale()));
        }
      } else {
        if (log.isEnabledFor(Level.WARN))
          log.warn("WARN: ProjectDTO is empty for [id: " + projectId + "]");
        reAtt.addFlashAttribute("errorMSG",
            messageSource.getMessage("roles.error.empty.form", null, LocaleContextHolder.getLocale()));
      }
    } catch (Exception e) {
      if (e instanceof MessagingException) {
        if (log.isEnabledFor(Level.ERROR))
          log.error("ERROR: Mail error, Mail was not sent - Exception:", e);
        reAtt.addFlashAttribute("errorMSG",
            messageSource.getMessage("send.mail.exception", null, LocaleContextHolder.getLocale()));
      } else {
        if (log.isEnabledFor(Level.ERROR))
          log.error("ERROR: Database error during database transaction, acceptInvite aborted - Exception:", e);
        reAtt.addFlashAttribute("errorMSG",
            messageSource.getMessage("dbs.sql.exception", null, LocaleContextHolder.getLocale()));
      }
    }
    if (log.isEnabledFor(Level.DEBUG)) {
      log.debug("Method acceptInvite successfully completed");
    }
    return "redirect:/panel";
  }

  /**
   * Delete role from a project User <br />
   * It is not possible to delete the project owner - its identifier is saved in the owner row in the dw_project table
   *
   * @param userId
   * @param roleId
   * @param projectId
   * @param studyId
   * @param reAtt
   * @param bRes
   * @return String redirect to access/{projectID} with reAtt for the different success or error states
   */
  @RequestMapping(value = { "/{projectId}/delete/{userId}/{roleId}",
      "/{projectId}/delete/{userId}/{roleId}/{studyId}" })
  public String deleteRole(@PathVariable final long userId, @PathVariable final long roleId,
      @PathVariable final long projectId, @PathVariable final Optional<Long> studyId, final RedirectAttributes reAtt,
      final ModelMap model) {
    if (log.isEnabledFor(Level.DEBUG)) {
      log.debug("Entering deleteRole [Role:" + roleId + " User:" + userId + " Project:" + projectId + "]");
    }
    UserRoleDTO role = new UserRoleDTO(roleId, userId, projectId, studyId.isPresent() ? studyId.get() : 1, "");
    UserDTO admin = UserUtil.getCurrentUser();
    String check = checkProjectAdmin(reAtt, projectId, admin);
    if (check != null) {
      return check;
    }
    String msgType;
    String msgTxt;
    try {
      ProjectDTO project = projectDAO.findById(projectId);
      if (project == null) {
        msgType = "errorMSG";
        msgTxt = "project.access.denied";
        if (log.isEnabledFor(Level.WARN))
          log.warn("WARN: Delete role not successful - no project found for id: " + projectId + " and user: "
              + admin.getEmail());
      } else if (admin.getId() == userId) {
        msgType = "errorMSG";
        msgTxt = "roles.error.self.delete";
        if (log.isEnabledFor(Level.WARN))
          log.warn("WARN: User " + admin.getEmail() + " tries to delete its own role");
      } else if (project.getOwnerId() == userId) {
        msgType = "errorMSG";
        msgTxt = "roles.error.owner.delete";
        if (log.isEnabledFor(Level.WARN))
          log.warn("WARN: User " + admin.getEmail() + " tries to delete the project owner: " + userId);
      } else {
        msgType = "infoMSG";
        msgTxt = "roles.success.del.role";
        roleDAO.deleteRole(role);
      }
    } catch (Exception e) {
      msgType = "errorMSG";
      msgTxt = "project.access.denied";
      if (log.isEnabledFor(Level.ERROR))
        log.error("ERROR: Database error during database transaction, role not deleted - Exception:", e);
      model.put("errorMSG", messageSource.getMessage("roles.error.db", null, LocaleContextHolder.getLocale()));
      return "access";
    }
    reAtt.addFlashAttribute(msgType, messageSource.getMessage(msgTxt, null, LocaleContextHolder.getLocale()));
    if (log.isEnabledFor(Level.DEBUG)) {
      log.debug("Method deleteRole successfully completed");
    }
    return "redirect:/access/" + projectId;
  }

  /**
   * Adds a role to a project user
   *
   * @param projectId
   * @param pForm
   * @param reAtt
   * @param bRes
   * @return String
   */
  @RequestMapping(value = { "/{projectId}" }, params = { "addRole" })
  public String addRoleToProjectUser(@PathVariable final long projectId,
      @ModelAttribute("ProjectForm") final ProjectForm pForm, final RedirectAttributes reAtt,
      final BindingResult bRes) {
    if (log.isEnabledFor(Level.DEBUG)) {
      log.debug("Entering addRoleToProjectUser for Project [id:" + projectId + "]");
    }
    UserDTO admin = UserUtil.getCurrentUser();
    String check = checkProjectAdmin(reAtt, projectId, admin);
    UserDTO user = null;
    if (check != null) {
      return check;
    }
    if (pForm != null && pForm.getNewRole() != null) {
      UserRoleDTO newRole = pForm.getNewRole();
      newRole.setProjectId(projectId);

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
            if (!bRes.hasErrors()) {
              if (log.isEnabledFor(Level.WARN))
                log.warn("WARN: no user found for userId: " + newRole.getUserId());
              bRes.reject("globalErrors",
                  messageSource.getMessage("roles.error.user.not.found", null, LocaleContextHolder.getLocale()));
            }
          }
          if (!bRes.hasErrors()) {
            int chk = roleDAO.setRole(newRole);
            // cleanup roles
            if (chk > 0 && user.getGlobalRoles() != null) {
              for (UserRoleDTO roleTmp : user.getGlobalRoles()) {
                if (newRole.getType().equals(Roles.PROJECT_ADMIN.name())
                    && !roleTmp.getType().equals(Roles.REL_ROLE.name()) && roleTmp.getProjectId() > 0) {
                  roleDAO.deleteRole(roleTmp);
                } else if (newRole.getType().equals(Roles.PROJECT_READER.name())
                    && roleTmp.getType().equals(Roles.DS_READER.name())) {
                  roleDAO.deleteRole(roleTmp);
                } else if (newRole.getType().equals(Roles.PROJECT_WRITER.name())
                    && (roleTmp.getType().equals(Roles.DS_WRITER.name())
                        || roleTmp.getType().equals(Roles.DS_READER.name())
                        || roleTmp.getType().equals(Roles.PROJECT_READER.name()))) {
                  roleDAO.deleteRole(roleTmp);
                } else if (newRole.getType().equals(Roles.DS_WRITER.name())
                    && newRole.getStudyId() == roleTmp.getStudyId()
                    && roleTmp.getType().equals(Roles.DS_READER.name())) {
                  roleDAO.deleteRole(roleTmp);
                }
              }
            }
          }
        } catch (Exception e) {
          if (log.isEnabledFor(Level.ERROR))
            log.error("ERROR: Database error during database transaction, role not saved - Exception:", e);
          bRes.reject("globalErrors",
              messageSource.getMessage("roles.error.db", null, LocaleContextHolder.getLocale()));
        }
      } else {
        if (log.isEnabledFor(Level.WARN)) {
          log.warn("WARN: add Role not successful because role has no userid - role:" + newRole);
        }
        bRes.reject("globalErrors",
            messageSource.getMessage("roles.error.empty.form", null, LocaleContextHolder.getLocale()));
      }
    } else {
      if (log.isEnabledFor(Level.WARN))
        log.warn("WARN: add Role not successful because ProjectForm is empty Projectform: " + pForm);
      bRes.reject("globalErrors",
          messageSource.getMessage("roles.error.empty.form", null, LocaleContextHolder.getLocale()));
    }
    if (bRes.hasErrors()) {
      if (log.isEnabledFor(Level.DEBUG)) {
        log.debug("Method addRoleToProjectUser completed with errors - return to access.jsp");
      }
      return "access";
    }
    if (log.isEnabledFor(Level.DEBUG)) {
      log.debug("Method addRoleToProjectUser successfully completed");
    }
    reAtt.addFlashAttribute("infoMSG", messageSource.getMessage("roles.success.add.role", new Object[] { user.getEmail() },
        LocaleContextHolder.getLocale()));
    return "redirect:/access/" + projectId;
  }
}
