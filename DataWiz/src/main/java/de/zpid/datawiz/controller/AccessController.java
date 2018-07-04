package de.zpid.datawiz.controller;

import de.zpid.datawiz.dao.ProjectDAO;
import de.zpid.datawiz.dao.RoleDAO;
import de.zpid.datawiz.dao.UserDAO;
import de.zpid.datawiz.dto.ProjectDTO;
import de.zpid.datawiz.dto.UserDTO;
import de.zpid.datawiz.dto.UserRoleDTO;
import de.zpid.datawiz.enumeration.PageState;
import de.zpid.datawiz.enumeration.Roles;
import de.zpid.datawiz.form.ProjectForm;
import de.zpid.datawiz.service.ExceptionService;
import de.zpid.datawiz.service.ProjectService;
import de.zpid.datawiz.util.BreadCrumbUtil;
import de.zpid.datawiz.util.EmailUtil;
import de.zpid.datawiz.util.StringUtil;
import de.zpid.datawiz.util.UserUtil;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import java.sql.SQLException;
import java.util.Locale;
import java.util.Optional;

/**
 * This controller handles all calls to /access/*
 * <p>
 * This file is part of the DataWiz distribution (https://github.com/ZPID/DataWiz).
 * Copyright (c) 2018 <a href="https://leibniz-psychology.org/">Leibniz Institute for Psychology Information (ZPID)</a>.
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3.
 * <p>
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <a href="http://www.gnu.org/licenses/">http://www.gnu.org/licenses/</a>.
 *
 * @author Ronny Boelter
 * @version 1.0
 **/
@Controller
@RequestMapping(value = "/access")
@SessionAttributes({"ProjectForm", "subnaviActive"})
public class AccessController {

    private static final Logger log = LogManager.getLogger(AccessController.class);
    private final ExceptionService exceptionService;
    private final MessageSource messageSource;
    private final ClassPathXmlApplicationContext applicationContext;
    private final ProjectService projectService;
    private final HttpServletRequest request;
    private final Environment env;
    private final StringUtil stringUtil;
    // TODO SERVICE CLASS
    private final ProjectDAO projectDAO;
    private final RoleDAO roleDAO;
    private final UserDAO userDAO;

    @Autowired
    public AccessController(final ExceptionService exceptionService, final MessageSource messageSource,
                            final ClassPathXmlApplicationContext applicationContext, final ProjectService projectService,
                            final HttpServletRequest request, final Environment env, final StringUtil stringUtil,
                            final ProjectDAO projectDAO, final RoleDAO roleDAO, final UserDAO userDAO) {
        super();
        log.info("Loading AccessController for mapping /access");
        this.exceptionService = exceptionService;
        this.messageSource = messageSource;
        this.applicationContext = applicationContext;
        this.projectService = projectService;
        this.request = request;
        this.env = env;
        this.stringUtil = stringUtil;
        this.projectDAO = projectDAO;
        this.roleDAO = roleDAO;
        this.userDAO = userDAO;
    }

    @ModelAttribute("ProjectForm")
    private ProjectForm createProjectForm() {
        return (ProjectForm) applicationContext.getBean("ProjectForm");
    }

    /**
     * Entering access page.
     *
     * @param projectId Project Identifier as {@link Optional}&lt;{@link Long}&gt;
     * @param model     {@link ModelMap}
     * @param reAtt     {@link RedirectAttributes}
     * @return String mapping to access.jsp on success
     */
    @RequestMapping(value = {"", "/{projectId}"}, method = RequestMethod.GET)
    public String showAccessPage(@PathVariable final Optional<Long> projectId, final ModelMap model, final RedirectAttributes reAtt) {
        log.trace("Entering showAccessPage for project [id: {}]", () -> projectId.isPresent() ? projectId.get() : "null");
        if (!projectId.isPresent()) {
            reAtt.addFlashAttribute("errorMSG", messageSource.getMessage("roles.error.empty.form",
                    new Object[]{env.getRequiredProperty("organisation.admin.email")}, LocaleContextHolder.getLocale()));
            return "redirect:/panel";
        }
        final UserDTO user = UserUtil.getCurrentUser();
        if (user == null) {
            log.warn(messageSource.getMessage("logging.user.auth.missing", null, Locale.ENGLISH));
            return "redirect:/login";
        }
        final ProjectForm pForm = createProjectForm();
        String pName = "";
        try {
            user.setGlobalRoles(roleDAO.findRolesByUserID(user.getId()));
            projectService.getProjectForm(pForm, projectId.get(), user, PageState.ACCESS, projectService.checkProjectRoles(user, projectId.get(), 0, false, false));
            if (pForm.getProject() != null && pForm.getProject().getId() > 0) {
                pForm.setSharedUser(userDAO.findGroupedByProject(pForm.getProject().getId()));
                pForm.setRoleList(roleDAO.findAllProjectRoles());
                pForm.setPendingMails(projectDAO.findPendingInvitesByProjectID(projectId.get()));
                for (UserDTO tuser : pForm.getSharedUser()) {
                    tuser.setGlobalRoles(roleDAO.findRolesByUserIDAndProjectID(tuser.getId(), projectId.get()));
                }
                if (pForm.getProject().getTitle() != null && !pForm.getProject().getTitle().trim().isEmpty()) {
                    pName = pForm.getProject().getTitle();
                }
            }
        } catch (Exception e) {
            return exceptionService.setErrorMessagesAndRedirects(projectId, Optional.empty(), Optional.empty(), model, reAtt, e, "accessController.showAccessPage");
        }
        model.put("breadcrumbList", BreadCrumbUtil.generateBC(PageState.PROJECT, new String[]{pName}, null, messageSource));
        model.put("subnaviActive", PageState.ACCESS.name());
        model.put("ProjectForm", pForm);
        log.trace("Method showAccessPage successfully completed");
        return "access";
    }

    /**
     * Function which is called if a User should be deleted from the project
     *
     * @param userId    User Identifier as {@link Long}
     * @param projectId Project Identifier as {@link Long}
     * @param reAtt     the redirect attributes {@link RedirectAttributes}
     * @return Redirect mapping showAccessPage on success
     */
    @RequestMapping(value = {"/{projectId}/deleteUser/{userId}"})
    public String deleteUserFromProject(@PathVariable final long userId, @PathVariable final long projectId, final RedirectAttributes reAtt,
                                        final ModelMap model) {
        log.trace("Entering deleteUserFromProject [id: {}; userid: {}]", () -> projectId, () -> userId);
        final UserDTO admin = UserUtil.getCurrentUser();
        final String check = projectService.checkProjectAdmin(reAtt, projectId, admin);
        if (check != null) {
            return check;
        }
        if (userId == admin.getId()) {
            log.warn("WARN: User {} tries to delete itself from project", admin::getEmail);
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
        log.trace("Method deleteUserFromProject successfully completed");
        return "redirect:/access/" + projectId;
    }

    /**
     * This function deletes the passed Mail from the invitation list
     *
     * @param model     {@link ModelMap}
     * @param projectId Project Identifier as {@link Long}
     * @param mail      Mail Address which has to be deleted as {@link String}
     * @param reAtt     {@link RedirectAttributes}
     * @return Redirect mapping showAccessPage on success
     */
    @RequestMapping(value = {"/{projectId}/deleteInvite/{mail}"}, method = RequestMethod.GET)
    public String deleteInvite(final ModelMap model, @PathVariable long projectId, @PathVariable final String mail, final RedirectAttributes reAtt) {
        log.trace("Entering deleteInvite project [id: {}] and user [mail: {}] ", () -> projectId, () -> mail);
        final UserDTO admin = UserUtil.getCurrentUser();
        final String check = projectService.checkProjectAdmin(reAtt, projectId, admin);
        if (check != null) {
            return check;
        }
        try {
            projectDAO.deleteInvitationEntity(projectId, mail);
        } catch (Exception e) {
            log.error("ERROR: Database error during database transaction, deleteInvite aborted - Exception: ", e);
            model.put("errorMSG", messageSource.getMessage("dbs.sql.exception", null, LocaleContextHolder.getLocale()));
            return "access";
        }
        log.trace("Method deleteInvite successfully completed");
        return "redirect:/access/" + projectId;
    }

    /**
     * This function re-sends a new invitation to the passed Mail. Therefore, it calls the addUserToProject function but passes the resend param as true.
     *
     * @param model     {@link ModelMap}
     * @param projectId Project Identifier as {@link Long}
     * @param mail      Mail Address as {@link String}
     * @param reAtt     {@link RedirectAttributes}
     * @return String from addUserToProject function - Redirect mapping showAccessPage on success
     */
    @RequestMapping(value = {"/{projectId}/resendInvite/{mail}"}, method = RequestMethod.GET)
    public String resendInvite(final ModelMap model, @PathVariable final long projectId, @PathVariable final String mail, final RedirectAttributes reAtt) {
        log.trace("Entering resendInvite for project [id: {}] and user [mail: {}] ", () -> projectId, () -> mail);
        final UserDTO admin = UserUtil.getCurrentUser();
        final String check = projectService.checkProjectAdmin(reAtt, projectId, admin);
        if (check != null) {
            return check;
        }
        ProjectForm pForm = createProjectForm();
        try {
            ProjectDTO project = projectDAO.findById(projectId);
            if (project == null) {
                log.warn("WARN: no project found for id: {}", () -> projectId);
                model.put("errorMSG", messageSource.getMessage("roles.error.empty.form", new Object[]{env.getRequiredProperty("organisation.admin.email")},
                        LocaleContextHolder.getLocale()));
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
     * Adds a user to project or sending a reminder mail if boolean resend is true. Checks if an user exists for the submitted mail address: if yes, a project
     * invitation is send to the user, if no, a DataWiz invitation is sent to the submitted email
     *
     * @param pForm     {@link ProjectForm} Includes necessary Project information
     * @param projectId Project Identifier as {@link Long}
     * @param reAtt     {@link RedirectAttributes}
     * @param bRes      {@link BindingResult}
     * @param resend    {@link Boolean} If true, only a new invitation is sent
     * @return Redirect mapping showAccessPage on success
     */
    @RequestMapping(value = {"/{projectId}"}, params = {"addUser"})
    public String addUserToProject(@ModelAttribute("ProjectForm") final ProjectForm pForm, @PathVariable final long projectId, final RedirectAttributes reAtt,
                                   final BindingResult bRes, final boolean resend) {
        log.trace("Entering addUserToProject for project [id: {}]", () -> projectId);
        final UserDTO admin = UserUtil.getCurrentUser();
        final String check = projectService.checkProjectAdmin(reAtt, projectId, admin);
        if (check != null) {
            return check;
        }
        String adminName = stringUtil.createUserNameString(admin);
        UserDTO user = null;
        String linkhash = null;
        try {
            user = userDAO.findByMail(pForm.getDelMail(), false);
            if (!resend)
                projectDAO.insertInviteEntity(projectId, pForm.getDelMail(), admin.getEmail());
            linkhash = projectDAO.findValFromInviteData(pForm.getDelMail(), projectId, "linkHash");
        } catch (Exception e) {
            if (e instanceof DuplicateKeyException) {
                bRes.reject("globalErrors", messageSource.getMessage("roles.error.doublet", null, LocaleContextHolder.getLocale()));
            }
            log.error("ERROR: Database error during database transaction, addUserToProject aborted - Exception:", e);
            reAtt.addFlashAttribute("errorMSG", messageSource.getMessage("dbs.sql.exception", null, LocaleContextHolder.getLocale()));
        }
        String subject, content, url;
        if (!bRes.hasErrors()) {
            if (linkhash != null && !linkhash.isEmpty()) {
                if (user != null && user.getId() > 0) {
                    if (log.isEnabled(Level.DEBUG)) {
                        log.debug("User [email: {}] has an active datawiz account - sending project invitation to primary email", user.getEmail());
                    }
                    subject = "inv.mail.project.subject";
                    content = "inv.mail.project.content";
                    url = request.getRequestURL().toString().replace(request.getRequestURI(),
                            request.getContextPath() + "/access/" + projectId + "/acceptInvite/" + pForm.getDelMail() + "/" + linkhash);
                } else {
                    if (log.isEnabled(Level.DEBUG)) {
                        log.debug("User [email: {}] hasn't an active datawiz account - sending datawiz invitation to email", pForm::getDelMail);
                    }
                    subject = "inv.mail.dw.subject";
                    content = "inv.mail.dw.content";
                    url = request.getRequestURL().toString().replace(request.getRequestURI(),
                            request.getContextPath() + "/register/" + projectId + "/" + pForm.getDelMail() + "/" + linkhash);
                }
                try {
                    EmailUtil mail = new EmailUtil(env);
                    mail.sendSSLMail(pForm.getDelMail(),
                            messageSource.getMessage(subject, new Object[]{pForm.getProject().getTitle()}, LocaleContextHolder.getLocale()),
                            messageSource.getMessage(content, new Object[]{adminName, pForm.getProject().getTitle(), url}, LocaleContextHolder.getLocale()));
                } catch (Exception e) {
                    projectDAO.deleteInvitationEntity(projectId, pForm.getDelMail());
                    log.error("ERROR: Mail error, Mail was not sent - Exception: {}", e::getMessage);
                    bRes.reject("globalErrors", messageSource.getMessage("send.mail.exception", null, LocaleContextHolder.getLocale()));
                }
            } else {
                log.warn("WARN: add User not successful because the hashcode is empty");
                bRes.reject("globalErrors", messageSource.getMessage("roles.error.empty.form", new Object[]{env.getRequiredProperty("organisation.admin.email")},
                        LocaleContextHolder.getLocale()));
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
     * This function is called from the invitation mail link. <br />
     * Before it saves the REL_ROLE (Relation Role), it checks the project's availability and if the calling user equals the user of the link address <br />
     * If the invitation is successful, a mail is sent to the inviting administrator, to remember him to add access rights to the new project user
     *
     * @param email     Mail Address as {@link String}
     * @param projectId Project Identifier as {@link Long}
     * @param linkHash  LinkHash Address as {@link String}
     * @param reAtt     {@link RedirectAttributes}
     * @return Redirect to /panel, because the user has no rights to read or write yet
     */
    @RequestMapping(value = {"/{projectId}/acceptInvite/{email}/{linkHash}"})
    public String acceptInvite(@PathVariable final String email, @PathVariable final long projectId, @PathVariable final String linkHash,
                               final RedirectAttributes reAtt) {
        log.trace("Entering acceptInvite for project [id: {}] and user [mail: {}] ", () -> projectId, () -> email);
        UserDTO admin = UserUtil.getCurrentUser();
        if (!admin.getEmail().equals(email)) {
            if (log.isEnabled(Level.WARN))
                log.warn("WARN: Invite acceptation failed because currentUser is not the invited User - CurrentUser: [email: " + admin.getEmail()
                        + "] InvitedUser: [email: " + email + "]");
            reAtt.addFlashAttribute("errorMSG", messageSource.getMessage("roles.wrong.account", null, LocaleContextHolder.getLocale()));
            return "redirect:/panel";
        }
        String adminMail;
        ProjectDTO project;
        try {
            project = projectDAO.findById(projectId);
            if (project != null) {
                adminMail = projectDAO.findValFromInviteData(email, projectId, "invited_by");
                if (adminMail != null && !adminMail.isEmpty()) {
                    String hash = projectDAO.findValFromInviteData(email, projectId, "linkHash");
                    if (hash != null && !hash.isEmpty() && !linkHash.isEmpty() && linkHash.trim().equals(hash.trim())) {
                        UserRoleDTO role = new UserRoleDTO(Roles.REL_ROLE.toInt(), admin.getId(), projectId, 0, Roles.REL_ROLE.name());
                        roleDAO.saveRole(role);
                        projectDAO.deleteInvitationEntity(projectId, email);
                    }
                    StringBuffer url = request.getRequestURL();
                    url = url.delete(url.indexOf(request.getRequestURI()), url.length()).append(request.getContextPath());
                    EmailUtil mail = new EmailUtil(env);
                    mail.sendSSLMail(adminMail, messageSource.getMessage("accept.mail.admin.subject", null, LocaleContextHolder.getLocale()),
                            messageSource.getMessage("accept.mail.admin.content",
                                    new Object[]{email, project.getTitle(), url.toString() + "/access/" + String.valueOf(project.getId())}, LocaleContextHolder.getLocale()));
                    reAtt.addFlashAttribute("infoMSG", messageSource.getMessage("roles.success.accept", null, LocaleContextHolder.getLocale()));
                } else {
                    reAtt.addFlashAttribute("infoMSG", messageSource.getMessage("roles.error.accept", null, LocaleContextHolder.getLocale()));
                }
            } else {
                log.warn("WARN: ProjectDTO is empty for [id: {}]", () -> projectId);
                reAtt.addFlashAttribute("errorMSG", messageSource.getMessage("roles.error.empty.form",
                        new Object[]{env.getRequiredProperty("organisation.admin.email")}, LocaleContextHolder.getLocale()));
            }
        } catch (Exception e) {
            if (e instanceof MessagingException) {
                log.error("ERROR: Mail error, Mail was not sent - Exception:", e);
                reAtt.addFlashAttribute("errorMSG", messageSource.getMessage("send.mail.exception", null, LocaleContextHolder.getLocale()));
            } else {
                log.error("ERROR: Database error during database transaction, acceptInvite aborted - Exception:", e);
                reAtt.addFlashAttribute("errorMSG", messageSource.getMessage("dbs.sql.exception", null, LocaleContextHolder.getLocale()));
            }
        }
        log.trace("Method acceptInvite successfully completed");
        return "redirect:/panel";
    }

    /**
     * Delete role from a project User <br />
     * It is not possible to delete the project owner - its identifier is saved in the owner row in the dw_project table
     *
     * @param userId    User Identifier as {@link Long}
     * @param roleId    Role Identifier as {@link Long}
     * @param projectId Project Identifier as {@link Long}
     * @param studyId   Study Identifier as {@link Optional}&lt;{@link Long}&gt;
     * @param reAtt     {@link RedirectAttributes}
     * @return String redirect to access/{projectID} with reAtt for the different success or error states
     */
    @RequestMapping(value = {"/{projectId}/delete/{userId}/{roleId}", "/{projectId}/delete/{userId}/{roleId}/{studyId}"})
    public String deleteRole(@PathVariable final long userId, @PathVariable final long roleId, @PathVariable final long projectId,
                             @PathVariable final Optional<Long> studyId, final RedirectAttributes reAtt, final ModelMap model) {
        log.trace("Entering deleteRole [Role: {}; User: {}; Project: {}]", () -> roleId, () -> userId, () -> projectId);
        final UserRoleDTO role = new UserRoleDTO(roleId, userId, projectId, studyId.isPresent() ? studyId.get() : 1, "");
        final UserDTO admin = UserUtil.getCurrentUser();
        final String check = projectService.checkProjectAdmin(reAtt, projectId, admin);
        if (check != null) {
            return check;
        }
        String msgType, msgTxt;
        try {
            ProjectDTO project = projectDAO.findById(projectId);
            if (project == null) {
                msgType = "errorMSG";
                msgTxt = "project.access.denied";
                log.warn("WARN: Delete role not successful - no project found for id: {} and user: {}", () -> projectId, admin::getEmail);
            } else if (admin.getId() == userId) {
                msgType = "errorMSG";
                msgTxt = "roles.error.self.delete";
                log.warn("WARN: User {} tries to delete its own role", admin::getEmail);
            } else if (project.getOwnerId() == userId) {
                msgType = "errorMSG";
                msgTxt = "roles.error.owner.delete";
                log.warn("WARN: User {} tries to delete the project owner: {}", admin::getEmail, () -> userId);
            } else {
                msgType = "infoMSG";
                msgTxt = "roles.success.del.role";
                roleDAO.deleteRole(role);
            }
        } catch (Exception e) {
            log.error("ERROR: Database error during database transaction, role not deleted - Exception:", e);
            model.put("errorMSG",
                    messageSource.getMessage("roles.error.db", new Object[]{env.getRequiredProperty("organisation.admin.email")}, LocaleContextHolder.getLocale()));
            return "access";
        }
        reAtt.addFlashAttribute(msgType, messageSource.getMessage(msgTxt, null, LocaleContextHolder.getLocale()));
        log.trace("Method deleteRole successfully completed");
        return "redirect:/access/" + projectId;
    }

    /**
     * This function is called if a role has to be set to a user. Before it sets the role, it checks the already given role and decides if it is useful to set the new role.
     *
     * @param projectId Project Identifier as {@link Long}
     * @param pForm     {@link ProjectForm} Includes necessary Project information
     * @param reAtt     {@link RedirectAttributes}
     * @param bRes      {@link BindingResult}
     * @return Redirect mapping showAccessPage on success
     */
    @RequestMapping(value = {"/{projectId}"}, params = {"addRole"})
    public String addRoleToProjectUser(@PathVariable final long projectId, @ModelAttribute("ProjectForm") final ProjectForm pForm, final RedirectAttributes reAtt,
                                       final BindingResult bRes) {
        log.trace("Entering addRoleToProjectUser for Project [id:" + projectId + "]");
        final UserDTO admin = UserUtil.getCurrentUser();
        final String check = projectService.checkProjectAdmin(reAtt, projectId, admin);
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
                    if (!bRes.hasErrors() && newRole.getType() != null && !newRole.getType().isEmpty() && !newRole.getType().equals("0")) {
                        Roles role = Roles.valueOf(newRole.getType());
                        newRole.setRoleId(role.toInt());
                        if ((role.equals(Roles.DS_READER) || role.equals(Roles.DS_WRITER)) && newRole.getStudyId() < 1) {
                            bRes.reject("globalErrors", messageSource.getMessage("roles.error.study.set", null, LocaleContextHolder.getLocale()));
                        }
                    } else {
                        bRes.reject("globalErrors", messageSource.getMessage("roles.error.no.role", null, LocaleContextHolder.getLocale()));
                    }
                    if (!bRes.hasErrors() && user != null && user.getGlobalRoles() != null) {
                        for (UserRoleDTO roleTmp : user.getGlobalRoles()) {
                            if (roleTmp.equals(newRole)) {
                                bRes.reject("globalErrors", messageSource.getMessage("roles.error.role.equal", null, LocaleContextHolder.getLocale()));
                                log.debug("Role allocation aborted because, because user always has this role {}", () -> newRole);
                                break;
                            }
                        }
                        if (!bRes.hasErrors())
                            if (user.hasRole(Roles.PROJECT_ADMIN, Optional.of(projectId), false)) {
                                bRes.reject("globalErrors", messageSource.getMessage("roles.error.global.admin", null, LocaleContextHolder.getLocale()));
                                log.debug("Role allocation aborted because user has Role {}", Roles.PROJECT_ADMIN::name);
                            } else if (user.hasRole(Roles.PROJECT_WRITER, Optional.of(projectId), false) && (newRole.getType().equals(Roles.PROJECT_READER.name())
                                    || newRole.getType().equals(Roles.DS_READER.name()) || newRole.getType().equals(Roles.DS_WRITER.name()))) {
                                bRes.reject("globalErrors", messageSource.getMessage("roles.error.global.write", null, LocaleContextHolder.getLocale()));
                                log.debug("Role allocation aborted because user has Role {} and doesn't need {}", Roles.PROJECT_WRITER::name, () -> newRole);
                            } else if (user.hasRole(Roles.PROJECT_READER, Optional.of(projectId), false) && newRole.getType().equals(Roles.DS_READER.name())) {
                                bRes.reject("globalErrors", messageSource.getMessage("roles.error.global.read", null, LocaleContextHolder.getLocale()));
                                log.debug("Role allocation aborted because user has Role {} and doesn't need {}", Roles.PROJECT_READER::name, () -> newRole);
                            }
                    } else {
                        if (!bRes.hasErrors()) {
                            log.warn("WARN: no user found for userId: {}", newRole::getUserId);
                            bRes.reject("globalErrors", messageSource.getMessage("roles.error.user.not.found", null, LocaleContextHolder.getLocale()));
                        }
                    }
                    if (!bRes.hasErrors()) {
                        cleanupRoles(user, newRole);
                    }
                } catch (Exception e) {
                    log.error("ERROR: Database error during database transaction, role not saved - Exception:", e);
                    bRes.reject("globalErrors", messageSource.getMessage("roles.error.db", new Object[]{env.getRequiredProperty("organisation.admin.email")},
                            LocaleContextHolder.getLocale()));
                }
            } else {
                log.warn("WARN: add Role not successful because role has no userid - role: {}", () -> newRole);
                bRes.reject("globalErrors", messageSource.getMessage("roles.error.empty.form", new Object[]{env.getRequiredProperty("organisation.admin.email")},
                        LocaleContextHolder.getLocale()));
            }
        } else {
            log.warn("WARN: add Role not successful because ProjectForm is empty");
            bRes.reject("globalErrors", messageSource.getMessage("roles.error.empty.form", new Object[]{env.getRequiredProperty("organisation.admin.email")},
                    LocaleContextHolder.getLocale()));
        }
        if (bRes.hasErrors()) {
            log.trace("Method addRoleToProjectUser completed with errors - return to access.jsp");
            return "access";
        }
        reAtt.addFlashAttribute("infoMSG", messageSource.getMessage("roles.success.add.role", new Object[]{user != null && user.getEmail() != null ? user.getEmail() : ""}, LocaleContextHolder.getLocale()));
        log.trace("Method addRoleToProjectUser successfully completed");
        return "redirect:/access/" + projectId;
    }


    //TODO Service funtion

    /**
     * Checks if the new role has a higher priority as roles which are already set, because it is not useful to have global rights and additional study rights for
     * example
     *
     * @param user    User object as {@link UserDTO}
     * @param newRole UserRole object as {@link UserRoleDTO}
     * @throws SQLException Database Exception
     */
    private void cleanupRoles(final UserDTO user, final UserRoleDTO newRole) throws SQLException {
        System.out.println(newRole);
        int chk = roleDAO.saveRole(newRole);
        if (chk > 0 && user.getGlobalRoles() != null) {
            for (UserRoleDTO roleTmp : user.getGlobalRoles()) {
                if (roleTmp.getProjectId() == newRole.getProjectId()) {
                    System.out.println(roleTmp);
                    if (newRole.getType().equals(Roles.PROJECT_ADMIN.name()) && !roleTmp.getType().equals(Roles.REL_ROLE.name()) && roleTmp.getProjectId() > 0) {
                        roleDAO.deleteRole(roleTmp);
                    } else if (newRole.getType().equals(Roles.PROJECT_READER.name()) && roleTmp.getType().equals(Roles.DS_READER.name())) {
                        roleDAO.deleteRole(roleTmp);
                    } else if (newRole.getType().equals(Roles.PROJECT_WRITER.name()) && (roleTmp.getType().equals(Roles.DS_WRITER.name())
                            || roleTmp.getType().equals(Roles.DS_READER.name()) || roleTmp.getType().equals(Roles.PROJECT_READER.name()))) {
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
