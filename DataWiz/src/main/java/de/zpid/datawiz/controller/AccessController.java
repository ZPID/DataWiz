package de.zpid.datawiz.controller;

import java.sql.SQLException;
import java.util.Optional;

import javax.mail.MessagingException;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
import de.zpid.datawiz.enumeration.PageState;
import de.zpid.datawiz.enumeration.Roles;
import de.zpid.datawiz.exceptions.DataWizException;
import de.zpid.datawiz.exceptions.DataWizSecurityException;
import de.zpid.datawiz.form.ProjectForm;
import de.zpid.datawiz.util.BreadCrumpUtil;
import de.zpid.datawiz.util.EmailUtil;
import de.zpid.datawiz.util.UserUtil;

/**
 * This file is part of Datawiz.<br />
 * 
 * <b>Copyright 2016, Leibniz Institute for Psychology Information (ZPID),
 * <a href="http://zpid.de" title="http://zpid.de">http://zpid.de</a>.</b><br />
 * <br />
 * <a rel="license" href="http://creativecommons.org/licenses/by-nc-sa/4.0/"><img alt="Creative Commons License" style=
 * "border-width:0" src="https://i.creativecommons.org/l/by-nc-sa/4.0/80x15.png" /></a><br />
 * <span xmlns:dct="http://purl.org/dc/terms/" property="dct:title">Datawiz</span> by
 * <a xmlns:cc="http://creativecommons.org/ns#" href="zpid.de" property="cc:attributionName" rel="cc:attributionURL">
 * Leibniz Institute for Psychology Information (ZPID)</a> is licensed under a
 * <a rel="license" href="http://creativecommons.org/licenses/by-nc-sa/4.0/">Creative Commons
 * Attribution-NonCommercial-ShareAlike 4.0 International License</a>.
 * 
 * @author Ronny Boelter
 * @version 1.0
 *
 */
@Controller
@RequestMapping(value = "/access")
@SessionAttributes({ "ProjectForm", "subnaviActive" })
public class AccessController extends SuperController {

  private static Logger log = LogManager.getLogger(AccessController.class);

  public AccessController() {
    super();
    log.info("Loading AccessController for mapping /access");
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
    log.trace("Entering showAccessPage for project [id: {}]", () -> projectId.isPresent() ? projectId.get() : "null");
    if (!projectId.isPresent()) {
      reAtt.addFlashAttribute("errorMSG",
          messageSource.getMessage("roles.error.empty.form", null, LocaleContextHolder.getLocale()));
      return "redirect:/panel";
    }
    final UserDTO user = UserUtil.getCurrentUser();
    if (user == null) {
      log.warn("Auth User Object == null - redirect to login");
      return "redirect:/login";
    }
    final ProjectForm pForm = createProjectForm();
    try {
      user.setGlobalRoles(roleDAO.findRolesByUserID(user.getId()));
      pUtil.getProjectForm(pForm, projectId.get(), user, PageState.ACCESS,
          pUtil.checkProjectRoles(user, projectId.get(), 0, false, false));
      if (pForm.getProject() != null && pForm.getProject().getId() > 0) {
        pForm.setSharedUser(userDAO.findGroupedByProject(pForm.getProject().getId()));
        pForm.setRoleList(roleDAO.findAllProjectRoles());
        pForm.setPendingMails(projectDAO.findPendingInvitesByProjectID(projectId.get()));
        for (UserDTO tuser : pForm.getSharedUser()) {
          tuser.setGlobalRoles(roleDAO.findRolesByUserIDAndProjectID(tuser.getId(), projectId.get()));
        }
      }
    } catch (Exception e) {
      String redirectMessage;
      if (e instanceof DataWizException) {
        redirectMessage = "project.not.available";
        log.warn("WARN: No Project available for user [email: {}] and project [id: {}]", () -> user.getEmail(),
            () -> projectId.get());
      } else if (e instanceof DataWizSecurityException) {
        redirectMessage = "project.access.denied";
        log.warn("WARN: user [email: {}] tried to get access to project [id: {}] without having a role",
            () -> user.getEmail(), () -> projectId.get());
      } else {
        redirectMessage = "dbs.sql.exception";
        log.error("ERROR: Database error during database transaction, showAccessPage aborted - Exception:", e);
      }
      reAtt.addFlashAttribute("errorMSG",
          messageSource.getMessage(redirectMessage, null, LocaleContextHolder.getLocale()));
      return "redirect:/panel";
    }
    model.put("breadcrumpList", BreadCrumpUtil.generateBC(PageState.ACCESS, null, 0));
    model.put("subnaviActive", PageState.ACCESS.name());
    model.put("ProjectForm", pForm);
    log.trace("Method showAccessPage successfully completed");
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
   * @return String
   */
  @RequestMapping(value = { "/{projectId}/deleteUser/{userId}" })
  public String deleteUserfromProject(@PathVariable final long userId, @PathVariable final long projectId,
      final RedirectAttributes reAtt, final ModelMap model) {
    log.trace("Entering deleteUserfromProject [id: {}; userid: {}]", () -> projectId, () -> userId);
    final UserDTO admin = UserUtil.getCurrentUser();
    final String check = pUtil.checkProjectAdmin(reAtt, projectId, admin);
    if (check != null) {
      return check;
    }
    if (userId == admin.getId()) {
      log.warn("WARN: User {} tries to delete itself from project", () -> admin.getEmail());
      return "access";
    }
    UserRoleDTO role = new UserRoleDTO(Roles.REL_ROLE.toInt(), userId, projectId, 0, Roles.REL_ROLE.toString());
    try {
      roleDAO.deleteRole(role);
    } catch (Exception e) {
      log.error("ERROR: Database error during database transaction, deleteInvite aborted - Exception:", e);
      model.put("errorMSG", messageSource.getMessage("dbs.sql.exception", null, LocaleContextHolder.getLocale()));
      return "access";
    }
    log.trace("Method deleteUserfromProject successfully completed");
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
    log.trace("Entering deleteInvite project [id: {}] and user [mail: {}] ", () -> projectId, () -> mail);
    final UserDTO admin = UserUtil.getCurrentUser();
    final String check = pUtil.checkProjectAdmin(reAtt, projectId, admin);
    if (check != null) {
      return check;
    }
    try {
      projectDAO.deleteInvitationEntree(projectId, mail);
    } catch (Exception e) {
      log.error("ERROR: Database error during database transaction, deleteInvite aborted - Exception: ", e);
      model.put("errorMSG", messageSource.getMessage("dbs.sql.exception", null, LocaleContextHolder.getLocale()));
      return "access";
    }
    log.trace("Method deleteInvite successfully completed");
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
    log.trace("Entering resendInvite for project [id: {}] and user [mail: {}] ", () -> projectId, () -> mail);
    final UserDTO admin = UserUtil.getCurrentUser();
    final String check = pUtil.checkProjectAdmin(reAtt, projectId, admin);
    if (check != null) {
      return check;
    }
    ProjectForm pForm = createProjectForm();
    try {
      ProjectDTO project = projectDAO.findById(projectId);
      if (project == null) {
        log.warn("WARN: no project found for id: {}", () -> projectId);
        model.put("errorMSG",
            messageSource.getMessage("roles.error.empty.form", null, LocaleContextHolder.getLocale()));
        model.put("errorMSG", "empty project");
        return "access";
      }
      pForm.setProject(project);
      pForm.setDelMail(mail);
    } catch (Exception e) {
      log.error("ERROR: Database error during database transaction, resendInvite aborted - Exception:", e);
      model.put("errorMSG", messageSource.getMessage("dbs.sql.exception", null, LocaleContextHolder.getLocale()));
      return "access";
    }
    log.trace("Method resendInvite successfully completed");
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
    log.trace("Entering addUserToProject for project [id: {}]", () -> projectId);
    final UserDTO admin = UserUtil.getCurrentUser();
    final String check = pUtil.checkProjectAdmin(reAtt, projectId, admin);
    if (check != null) {
      return check;
    }
    String adminName = createAdminName(admin);
    UserDTO user = null;
    String linkhash = null;
    try {
      user = userDAO.findByMail(pForm.getDelMail(), false);
      if (resend != true)
        projectDAO.insertInviteEntree(projectId, pForm.getDelMail(), admin.getEmail());
      linkhash = projectDAO.findValFromInviteData(pForm.getDelMail(), projectId, "linkhash");
    } catch (Exception e) {
      if (e instanceof DuplicateKeyException) {
        bRes.reject("globalErrors",
            messageSource.getMessage("roles.error.doublet", null, LocaleContextHolder.getLocale()));
      }
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
          if (log.isEnabled(Level.DEBUG)) {
            log.debug("User [email: {}] has an active datawiz account - sending project invitation to primary email",
                user.getEmail());
          }
          subject = "inv.mail.project.subject";
          content = "inv.mail.project.content";
          url = request.getRequestURL().toString().replace(request.getRequestURI(), request.getContextPath()
              + "/access/" + projectId + "/acceptInvite/" + pForm.getDelMail() + "/" + linkhash);
        } else {
          if (log.isEnabled(Level.DEBUG)) {
            log.debug("User [email: {}] hasn't an active datawiz account - sending datawiz invitation to email",
                pForm.getDelMail());
          }
          subject = "inv.mail.dw.subject";
          content = "inv.mail.dw.content";
          url = request.getRequestURL().toString().replace(request.getRequestURI(),
              request.getContextPath() + "/register/" + projectId + "/" + pForm.getDelMail() + "/" + linkhash);
        }
        if (subject != null && content != null && url != null) {
          try {
            EmailUtil mail = new EmailUtil(env);
            mail.sendSSLMail(pForm.getDelMail(),
                messageSource.getMessage(subject, new Object[] { pForm.getProject().getTitle() },
                    LocaleContextHolder.getLocale()),
                messageSource.getMessage(content, new Object[] { adminName, pForm.getProject().getTitle(), url },
                    LocaleContextHolder.getLocale()));
          } catch (Exception e) {
            log.error("ERROR: Mail error, Mail was not sent - Exception:", e);
            bRes.reject("globalErrors",
                messageSource.getMessage("send.mail.exception", null, LocaleContextHolder.getLocale()));
          }
        } else {
          log.warn("WARN: add User not successful something went wrot with the requestURL url: {}", url);
          bRes.reject("globalErrors",
              messageSource.getMessage("roles.error.empty.form", null, LocaleContextHolder.getLocale()));
        }
      } else {
        log.warn("WARN: add User not successful because the hashcode is empty");
        bRes.reject("globalErrors",
            messageSource.getMessage("roles.error.empty.form", null, LocaleContextHolder.getLocale()));
      }
    }
    if (bRes.hasErrors()) {
      log.trace("Method addUserToProject completed with errors - return to access.jsp");
      return "access";
    }
    log.trace("Method addUserToProject successfully completed");
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
    log.trace("Entering accepptInvite for project [id: {}] and user [mail: {}] ", () -> projectId, () -> email);
    UserDTO admin = UserUtil.getCurrentUser();
    if (!admin.getEmail().equals(email)) {
      if (log.isEnabled(Level.WARN))
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
        adminMail = projectDAO.findValFromInviteData(email, projectId, "invited_by");
        if (adminMail != null && !adminMail.isEmpty()) {
          String hash = projectDAO.findValFromInviteData(email, projectId, "linkhash");
          if (hash != null && !hash.isEmpty() && !linkhash.isEmpty() && linkhash.trim().equals(hash.trim())) {
            UserRoleDTO role = new UserRoleDTO(Roles.REL_ROLE.toInt(), admin.getId(), projectId, 0,
                Roles.REL_ROLE.name());
            roleDAO.saveRole(role);
            projectDAO.deleteInvitationEntree(projectId, email);
          }
          StringBuffer url = request.getRequestURL();
          url = url.delete(url.indexOf(request.getRequestURI()), url.length()).append(request.getContextPath());
          EmailUtil mail = new EmailUtil(env);
          mail.sendSSLMail(adminMail,
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
        log.warn("WARN: ProjectDTO is empty for [id: {}]", () -> projectId);
        reAtt.addFlashAttribute("errorMSG",
            messageSource.getMessage("roles.error.empty.form", null, LocaleContextHolder.getLocale()));
      }
    } catch (Exception e) {
      if (e instanceof MessagingException) {
        log.error("ERROR: Mail error, Mail was not sent - Exception:", e);
        reAtt.addFlashAttribute("errorMSG",
            messageSource.getMessage("send.mail.exception", null, LocaleContextHolder.getLocale()));
      } else {
        log.error("ERROR: Database error during database transaction, acceptInvite aborted - Exception:", e);
        reAtt.addFlashAttribute("errorMSG",
            messageSource.getMessage("dbs.sql.exception", null, LocaleContextHolder.getLocale()));
      }
    }
    log.trace("Method acceptInvite successfully completed");
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
    log.trace("Entering deleteRole [Role: {}; User: {}; Project: {}]", () -> roleId, () -> userId, () -> projectId);
    final UserRoleDTO role = new UserRoleDTO(roleId, userId, projectId, studyId.isPresent() ? studyId.get() : 1, "");
    final UserDTO admin = UserUtil.getCurrentUser();
    final String check = pUtil.checkProjectAdmin(reAtt, projectId, admin);
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
        log.warn("WARN: Delete role not successful - no project found for id: {} and user: {}", () -> projectId,
            () -> admin.getEmail());
      } else if (admin.getId() == userId) {
        msgType = "errorMSG";
        msgTxt = "roles.error.self.delete";
        log.warn("WARN: User {} tries to delete its own role", () -> admin.getEmail());
      } else if (project.getOwnerId() == userId) {
        msgType = "errorMSG";
        msgTxt = "roles.error.owner.delete";
        log.warn("WARN: User {} tries to delete the project owner: {}", () -> admin.getEmail(), () -> userId);
      } else {
        msgType = "infoMSG";
        msgTxt = "roles.success.del.role";
        roleDAO.deleteRole(role);
      }
    } catch (Exception e) {
      msgType = "errorMSG";
      msgTxt = "project.access.denied";
      log.error("ERROR: Database error during database transaction, role not deleted - Exception:", e);
      model.put("errorMSG", messageSource.getMessage("roles.error.db", null, LocaleContextHolder.getLocale()));
      return "access";
    }
    reAtt.addFlashAttribute(msgType, messageSource.getMessage(msgTxt, null, LocaleContextHolder.getLocale()));
    log.trace("Method deleteRole successfully completed");
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
    log.trace("Entering addRoleToProjectUser for Project [id:" + projectId + "]");
    final UserDTO admin = UserUtil.getCurrentUser();
    final String check = pUtil.checkProjectAdmin(reAtt, projectId, admin);
    if (check != null) {
      return check;
    }
    UserDTO user = null;
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
                log.debug("Role allocation aborted because, because user always has this role {}", () -> newRole);
                break;
              }
            }
            if (!bRes.hasErrors())
              if (user.hasRole(Roles.PROJECT_ADMIN, Optional.of(projectId), false)) {
                bRes.reject("globalErrors",
                    messageSource.getMessage("roles.error.global.admin", null, LocaleContextHolder.getLocale()));
                log.debug("Role allocation aborted because user has Role {}", () -> Roles.PROJECT_ADMIN.name());
              } else if (user.hasRole(Roles.PROJECT_WRITER, Optional.of(projectId), false)
                  && (newRole.getType().equals(Roles.PROJECT_READER.name())
                      || newRole.getType().equals(Roles.DS_READER.name())
                      || newRole.getType().equals(Roles.DS_WRITER.name()))) {
                bRes.reject("globalErrors",
                    messageSource.getMessage("roles.error.global.write", null, LocaleContextHolder.getLocale()));
                log.debug("Role allocation aborted because user has Role {} and doesn't need {}",
                    () -> Roles.PROJECT_WRITER.name(), () -> newRole);
              } else if (user.hasRole(Roles.PROJECT_READER, Optional.of(projectId), false)
                  && newRole.getType().equals(Roles.DS_READER.name())) {
                bRes.reject("globalErrors",
                    messageSource.getMessage("roles.error.global.read", null, LocaleContextHolder.getLocale()));
                log.debug("Role allocation aborted because user has Role {} and doesn't need {}",
                    () -> Roles.PROJECT_READER.name(), () -> newRole);
              }
          } else {
            if (!bRes.hasErrors()) {
              log.warn("WARN: no user found for userId: {}", () -> newRole.getUserId());
              bRes.reject("globalErrors",
                  messageSource.getMessage("roles.error.user.not.found", null, LocaleContextHolder.getLocale()));
            }
          }
          if (!bRes.hasErrors()) {
            cleanupRoles(user, newRole);
          }
        } catch (Exception e) {
          log.error("ERROR: Database error during database transaction, role not saved - Exception:", e);
          bRes.reject("globalErrors",
              messageSource.getMessage("roles.error.db", null, LocaleContextHolder.getLocale()));
        }
      } else {
        log.warn("WARN: add Role not successful because role has no userid - role: {}", () -> newRole);
        bRes.reject("globalErrors",
            messageSource.getMessage("roles.error.empty.form", null, LocaleContextHolder.getLocale()));
      }
    } else {
      log.warn("WARN: add Role not successful because ProjectForm is empty");
      bRes.reject("globalErrors",
          messageSource.getMessage("roles.error.empty.form", null, LocaleContextHolder.getLocale()));
    }
    if (bRes.hasErrors()) {
      log.trace("Method addRoleToProjectUser completed with errors - return to access.jsp");
      return "access";
    }
    reAtt.addFlashAttribute("infoMSG", messageSource.getMessage("roles.success.add.role",
        new Object[] { user.getEmail() }, LocaleContextHolder.getLocale()));
    log.trace("Method addRoleToProjectUser successfully completed");
    return "redirect:/access/" + projectId;
  }

  /**
   * This function tries to set the name of an admin if the needed fiels are set, otherwise it return the email of the
   * admin
   * 
   * @param admin
   * @return
   */
  private String createAdminName(final UserDTO admin) {
    String adminName = (admin.getLastName() != null && !admin.getLastName().isEmpty() ? admin.getLastName() : "");
    if (!adminName.isEmpty())
      adminName = (admin.getTitle() != null && !admin.getTitle().isEmpty() ? admin.getTitle() + " " : "")
          + (admin.getFirstName() != null && !admin.getFirstName().isEmpty() ? admin.getFirstName() + " " : "")
          + adminName;
    if (!adminName.isEmpty())
      adminName = adminName + "( " + admin.getEmail() + " )";
    else
      adminName = admin.getEmail();
    return adminName;
  }

  /**
   * Checks if the new role has a higher priority as roles which are already set, because it is not useful to have
   * global rights and additional study rights for example
   * 
   * @param user
   * @param newRole
   * @throws SQLException
   */
  private void cleanupRoles(final UserDTO user, final UserRoleDTO newRole) throws SQLException {
    System.out.println(newRole);
    int chk = roleDAO.saveRole(newRole);
    if (chk > 0 && user.getGlobalRoles() != null) {
      for (UserRoleDTO roleTmp : user.getGlobalRoles()) {
        if (roleTmp.getProjectId() == newRole.getProjectId()) {
          System.out.println(roleTmp);
          if (newRole.getType().equals(Roles.PROJECT_ADMIN.name()) && !roleTmp.getType().equals(Roles.REL_ROLE.name())
              && roleTmp.getProjectId() > 0) {
            roleDAO.deleteRole(roleTmp);
          } else if (newRole.getType().equals(Roles.PROJECT_READER.name())
              && roleTmp.getType().equals(Roles.DS_READER.name())) {
            roleDAO.deleteRole(roleTmp);
          } else if (newRole.getType().equals(Roles.PROJECT_WRITER.name())
              && (roleTmp.getType().equals(Roles.DS_WRITER.name()) || roleTmp.getType().equals(Roles.DS_READER.name())
                  || roleTmp.getType().equals(Roles.PROJECT_READER.name()))) {
            roleDAO.deleteRole(roleTmp);
          } else if (newRole.getType().equals(Roles.DS_WRITER.name()) && newRole.getStudyId() == roleTmp.getStudyId()
              && roleTmp.getType().equals(Roles.DS_READER.name())) {
            roleDAO.deleteRole(roleTmp);
          }
        }
      }
    }
  }
}
