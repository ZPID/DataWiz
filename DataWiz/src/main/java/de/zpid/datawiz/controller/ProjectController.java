package de.zpid.datawiz.controller;

import de.zpid.datawiz.dto.ContributorDTO;
import de.zpid.datawiz.dto.ProjectDTO;
import de.zpid.datawiz.dto.UserDTO;
import de.zpid.datawiz.enumeration.DataWizErrorCodes;
import de.zpid.datawiz.enumeration.PageState;
import de.zpid.datawiz.enumeration.Roles;
import de.zpid.datawiz.enumeration.SavedState;
import de.zpid.datawiz.exceptions.DataWizSystemException;
import de.zpid.datawiz.form.ProjectForm;
import de.zpid.datawiz.service.ExceptionService;
import de.zpid.datawiz.service.ProjectService;
import de.zpid.datawiz.util.BreadCrumbUtil;
import de.zpid.datawiz.util.UserUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.SmartValidator;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * This controller handles all calls to /project/*, except of /project/{pid}/study/*
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
@RequestMapping(value = "/project")
public class ProjectController {

    private final MessageSource messageSource;
    private final ClassPathXmlApplicationContext applicationContext;
    private final SmartValidator validator;
    private final ProjectService projectService;
    private final ExceptionService exceptionService;
    private final Environment env;

    /**
     * Creates the project form.
     *
     * @return {@link ProjectForm}
     */
    @ModelAttribute("ProjectForm")
    private ProjectForm createProjectForm() {
        return (ProjectForm) applicationContext.getBean("ProjectForm");
    }

    private static final Logger log = LogManager.getLogger(ProjectController.class);

    /**
     * Instantiates a new project controller.
     */
    @Autowired
    public ProjectController(MessageSource messageSource, ClassPathXmlApplicationContext applicationContext, SmartValidator validator,
                             ProjectService projectService, ExceptionService exceptionService, Environment env) {
        super();
        log.info("Loading ProjectController for mapping /project");
        this.messageSource = messageSource;
        this.applicationContext = applicationContext;
        this.validator = validator;
        this.projectService = projectService;
        this.exceptionService = exceptionService;
        this.env = env;
    }

    /**
     * This function handles the calls to the project.jsp (/project/{pid}). It distinguishes between the different user roles. If a user has no access to the
     * project and dmp meta data, but has access to a study, it automatically redirects to the study overview.
     *
     * @param pid                Project Identifier as {@link Optional}&lt;{@link Long}&gt;
     * @param model              {@link ModelMap}
     * @param redirectAttributes {@link RedirectAttributes}
     * @return Mapping to project.jsp on success, otherwise redirect mapping to login on authentication errors, or to panel on access denied, or internal errors.
     */
    @RequestMapping(value = {"", "/{pid}"}, method = RequestMethod.GET)
    public String showProjectPage(@PathVariable final Optional<Long> pid, final ModelMap model, final RedirectAttributes redirectAttributes) {
        log.trace("Entering showProjectPage for project [id: {}}", () -> pid.isPresent() ? pid.get() : "null (new project)");
        UserDTO user = UserUtil.getCurrentUser();
        ProjectForm pForm = createProjectForm();
        String name = messageSource.getMessage("breadcrumb.new.project", null, LocaleContextHolder.getLocale());
        String ret = "project";
        if (user == null) {
            log.warn(messageSource.getMessage("logging.user.auth.missing", null, Locale.ENGLISH));
            ret = "redirect:/login";
        } else if (pid.isPresent()) {
            Roles role = projectService.checkProjectRoles(user, pid.get(), 0, false, true);
            if (role != null
                    && (!role.equals(Roles.ADMIN) && !role.equals(Roles.PROJECT_ADMIN) && !role.equals(Roles.PROJECT_READER) && !role.equals(Roles.PROJECT_WRITER))
                    && (role.equals(Roles.DS_READER) || role.equals(Roles.DS_WRITER))) {
                redirectAttributes.addFlashAttribute("hideMenu", true);
                ret = "redirect:/project/" + pid.get() + "/studies";
            }
            try {
                projectService.getProjectForm(pForm, pid.get(), user, PageState.PROJECT, role);
                name = pForm.getProject().getTitle();
            } catch (Exception e) {
                log.warn("Exception during projectService.getProjectForm for [user: {}, pid: {}]", user::getId, () -> pid, () -> e);
                ret = exceptionService.setErrorMessagesAndRedirects(pid, Optional.empty(), Optional.empty(), model, redirectAttributes, e, "ProjectController.showProjectPage");
            }
        } else {
            model.put("hideMenu", true);
            List<ContributorDTO> cContri = new ArrayList<>();
            cContri.add(new ContributorDTO());
            pForm.setContributors(cContri);
        }
        model.put("breadcrumbList", BreadCrumbUtil.generateBC(PageState.PROJECT, new String[]{name}, null, messageSource));
        model.put("subnaviActive", PageState.PROJECT.name());
        model.put("ProjectForm", pForm);
        log.trace("Leaving showProjectPage for project [id: {}]", () -> pid.isPresent() ? pid.get() : "null (new project)");
        return ret;
    }

    /**
     * This function handles the mapping to a project's study overview (studies.jsp).
     *
     * @param pid                Project Identifier as {@link Optional}&lt;{@link Long}&gt;
     * @param model              {@link ModelMap}
     * @param redirectAttributes {@link RedirectAttributes}
     * @return Mapping to studies.jsp on success, otherwise redirect mapping to login on authentication errors, or to panel on access denied, or internal errors
     */
    @RequestMapping(value = {"/{pid}/studies"}, method = RequestMethod.GET)
    public String showStudiesPage(@PathVariable final Optional<Long> pid, final ModelMap model, final RedirectAttributes redirectAttributes) {
        log.trace("Entering showStudiesPage for project [id: {}]", () -> pid.isPresent() ? pid.get() : "null");
        final UserDTO user = UserUtil.getCurrentUser();
        String ret = "studies";
        if (user == null) {
            log.warn(messageSource.getMessage("logging.user.auth.missing", null, Locale.ENGLISH));
            ret = "redirect:/login";
        } else if (!pid.isPresent()) {
            log.warn(messageSource.getMessage("logging.pid.not.present", null, Locale.ENGLISH));
            redirectAttributes.addFlashAttribute("errorMSG", messageSource.getMessage("project.access.denied", null, LocaleContextHolder.getLocale()));
            ret = "redirect:/panel";
        } else {
            ProjectForm pForm = createProjectForm();
            Roles role = projectService.checkProjectRoles(user, pid.get(), 0, false, true);
            if (role != null) {
                if ((!role.equals(Roles.ADMIN) && !role.equals(Roles.PROJECT_ADMIN) && !role.equals(Roles.PROJECT_READER) && !role.equals(Roles.PROJECT_WRITER))
                        && (role.equals(Roles.DS_READER) || role.equals(Roles.DS_WRITER))) {
                    model.put("hideMenu", true);
                }
                try {
                    projectService.getProjectForm(pForm, pid.get(), user, PageState.STUDIES, role);
                    model.put("breadcrumbList", BreadCrumbUtil.generateBC(PageState.PROJECT, new String[]{pForm.getProject().getTitle()}, null, messageSource));
                    model.put("subnaviActive", PageState.STUDIES.name());
                    model.put("ProjectForm", pForm);
                } catch (Exception e) {
                    log.warn("Exeption during projectService.getProjectForm - Exception: ", () -> e);
                    ret = exceptionService.setErrorMessagesAndRedirects(pid, Optional.empty(), Optional.empty(), model, redirectAttributes, e, "ProjectController.showProjectPage");
                }
            } else {
                log.warn(messageSource.getMessage("logging.user.permitted", new Object[]{user.getId(), "project", pid}, Locale.ENGLISH));
                redirectAttributes.addFlashAttribute("errorMSG", messageSource.getMessage("project.access.denied", null, LocaleContextHolder.getLocale()));
                ret = "redirect:/panel";
            }
        }
        log.trace("Leaving showStudiesPage for projectID= {}", () -> pid.isPresent() ? pid.get() : "null");
        return ret;
    }

    /**
     * This function handles the mapping to the project/study additional materials page (material.jsp). It loads the additional materials depending on the call.
     * If the study identifier is present, it loads the study additional materials, otherwise the project additional materials.
     *
     * @param pid                Project Identifier as {@link Optional}&lt;{@link Long}&gt;
     * @param studyId            Study Identifier as {@link Optional}&lt;{@link Long}&gt;
     * @param model              {@link ModelMap}
     * @param redirectAttributes {@link RedirectAttributes}
     * @return Mapping to material.jsp on success, otherwise redirect mapping to login on authentication errors, or to panel on access denied, or internal errors
     */
    @RequestMapping(value = {"/{pid}/material", "/{pid}/study/{studyId}/material"}, method = RequestMethod.GET)
    public String showMaterialPage(@PathVariable Optional<Long> pid, @PathVariable Optional<Long> studyId, ModelMap model,
                                   RedirectAttributes redirectAttributes) {
        log.trace("Entering showMaterialPage for project [id: {}]", () -> pid.isPresent() ? pid.get() : "null");
        UserDTO user = UserUtil.getCurrentUser();
        String ret;
        if (user == null) {
            log.warn(messageSource.getMessage("logging.user.auth.missing", null, Locale.ENGLISH));
            ret = "redirect:/login";
        } else if (!pid.isPresent()) {
            log.warn(messageSource.getMessage("logging.pid.not.present", null, Locale.ENGLISH));
            redirectAttributes.addFlashAttribute("errorMSG", messageSource.getMessage("project.access.denied", null, LocaleContextHolder.getLocale()));
            ret = "redirect:/panel";
        } else {
            ProjectForm pForm = createProjectForm();
            ret = projectService.checkUserAccess(pid.orElse(0L), studyId.orElse(0L), redirectAttributes, false, user);
            if (ret != null) {
                redirectAttributes.addFlashAttribute("errorMSG", messageSource.getMessage("project.access.denied", null, LocaleContextHolder.getLocale()));
            } else {
                try {
                    projectService.setMaterialForm(pid.orElse(0L), studyId.orElse(0L), model, user, pForm);
                    ret = "material";
                    model.put("studyId", studyId.isPresent() ? studyId.get() : -1);
                    model.put("projectId", pid.get());
                    model.put("ProjectForm", pForm);
                    model.put("subnaviActive", PageState.MATERIAL.name());
                } catch (Exception e) {
                    log.warn("Error in setMaterialForm Message: ", () -> e);
                    exceptionService.setErrorMessagesAndRedirects(pid, studyId, Optional.empty(), model, redirectAttributes, e, "projectService.setMaterialForm");
                }
            }
        }
        log.trace("Leaving showMaterialPage for project [id: {}]", () -> pid.isPresent() ? pid.get() : "null");
        return ret;
    }

    /**
     * This function is called when a user saves the project meta data. Before saving via projectService.saveOrUpdateProject, the project meta data are validated.
     * If validation has errors, it returns to project.jsp and puts error messages to the view.
     *
     * @param pForm              {@link ProjectForm}
     * @param bindingResult      {@link BindingResult}
     * @param model              {@link ModelMap}
     * @param redirectAttributes {@link RedirectAttributes}
     * @return On success redirect mapping to /project, on error mapping to project.jsp with error messages
     */
    @RequestMapping(value = {"", "/{pid}"}, method = RequestMethod.POST)
    public String saveProject(@ModelAttribute("ProjectForm") final ProjectForm pForm, final BindingResult bindingResult, final ModelMap model,
                              final RedirectAttributes redirectAttributes) {
        String ret = "project";
        if (pForm != null && pForm.getProject() != null) {
            log.trace("Entering saveProject for project [id: {}]", () -> (pForm.getProject().getId() == 0 ? "new project" : pForm.getProject().getId()));
            if (pForm.getProject().getId() > 0) {
                String access = projectService.checkUserAccess(pForm.getProject().getId(), 0, redirectAttributes, true, UserUtil.getCurrentUser());
                ret = (access == null) ? ret : access;
            } else {
                model.put("hideMenu", true);
            }
            if (ret.equals("project")) {
                validator.validate(pForm, bindingResult, ProjectDTO.ProjectVal.class);
                validator.validate(pForm, bindingResult);
                List<ContributorDTO> cContri = null;
                try {
                    cContri = projectService.validateContributors(pForm, bindingResult);
                } catch (Exception e) {
                    bindingResult.reject("globalErrors",
                            messageSource.getMessage("logging.internal.error", new Object[]{e.getMessage()}, LocaleContextHolder.getLocale()));
                    log.warn(messageSource.getMessage("logging.project.error", null, Locale.ENGLISH), () -> e);
                }
                if (bindingResult.hasErrors()) {
                    log.debug("bindingResult has Errors " + bindingResult.getAllErrors().toString());
                } else {
                    pForm.setContributors(cContri);
                    DataWizErrorCodes state = projectService.saveOrUpdateProject(pForm);
                    log.debug("Leaving saveProject with result: [code: {}]", state::name);
                    if (!state.equals(DataWizErrorCodes.OK)) {
                        model.put("errorMSG", messageSource.getMessage("project.save.error." + state.name(), null, LocaleContextHolder.getLocale()));
                    } else {
                        redirectAttributes.addFlashAttribute("successMSG",
                                messageSource.getMessage("project.save.error." + state.name(), null, LocaleContextHolder.getLocale()));
                        ret = "redirect:/project/" + pForm.getProject().getId();
                    }
                }
            }
            log.trace("Leaving saveProject for project [id: {}]", () -> (pForm.getProject().getId() == 0 ? "new project" : pForm.getProject().getId()));
        } else {
            model.put("errorMSG", messageSource.getMessage("project.save.error.NO_DATA_ERROR", null, LocaleContextHolder.getLocale()));
            log.warn(messageSource.getMessage("logging.missing.form.error", null, Locale.ENGLISH));
        }
        if (ret.equals("project")) {
            model.put("subnaviActive", PageState.PROJECT.name());
            String title = pForm != null && pForm.getProject() != null ? pForm.getProject().getTitle() : "";
            model.put("breadcrumbList", BreadCrumbUtil.generateBC(PageState.PROJECT, new String[]{title}, null, messageSource));
        }
        return ret;
    }

    /**
     * This function is used by the dropzone in the material.jsp of the study and project material pages. It saves an uploaded file by using
     * projectService.saveMaterialToMinoAndDB and returns a HttpStatus.
     *
     * @param request            {@link MultipartHttpServletRequest}
     * @param redirectAttributes {@link RedirectAttributes}
     * @param pid                Project Identifier as {@link Optional}&lt;{@link Long}&gt;
     * @param studyId            Study Identifier as {@link Optional}&lt;{@link Long}&gt;
     * @return Success: HttpStatus.OK <br />
     * Database Error: HttpStatus.CONFLICT <br />
     * Minio or Auth error: HttpStatus.INTERNAL_SERVER_ERROR
     */
    @RequestMapping(value = {"/{pid}/upload", "/{pid}/study/{studyId}/upload"}, method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity<String> uploadFile(MultipartHttpServletRequest request, RedirectAttributes redirectAttributes,
                                      @PathVariable Optional<Long> pid, @PathVariable Optional<Long> studyId) {
        log.trace("Entering uploadFile for Project or Study [pid: {}, studyId: {}]", () -> pid, () -> studyId);
        if (!pid.isPresent()) {
            log.warn("ProjectForm is empty or missing values");
            return new ResponseEntity<>("{\"error\" : \"" + messageSource.getMessage("project.not.available", null, LocaleContextHolder.getLocale()) + "\"}",
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
        UserDTO user = UserUtil.getCurrentUser();
        if (user == null) {
            log.warn(messageSource.getMessage("logging.user.auth.missing", null, Locale.ENGLISH));
            return new ResponseEntity<>("{\"error\" : \"" + messageSource.getMessage("project.not.available", null, LocaleContextHolder.getLocale()) + "\"}",
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if (projectService.checkUserAccess(pid.orElse(0L), studyId.orElse(0L), redirectAttributes, true, user) != null) {
            log.warn(messageSource.getMessage("logging.user.auth.missing", null, Locale.ENGLISH));
            return new ResponseEntity<>("{\"error\" : \"" + messageSource.getMessage("project.access.denied", null, LocaleContextHolder.getLocale()) + "\"}",
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
        switch (projectService.saveMaterialToMinioAndDB(request, pid.orElse(0L), studyId.orElse(0L), user)) {
            case MINIO_SAVE_ERROR:
                return new ResponseEntity<>("{\"error\" : \"" + messageSource.getMessage("minio.connection.exception.upload",
                        new Object[]{env.getRequiredProperty("organisation.admin.email")}, LocaleContextHolder.getLocale()) + "\"}", HttpStatus.INTERNAL_SERVER_ERROR);
            case DATABASE_ERROR:
                return new ResponseEntity<>("{\"error\" : \""
                        + messageSource.getMessage("dbs.sql.exception", new Object[]{env.getRequiredProperty("organisation.admin.email")}, LocaleContextHolder.getLocale())
                        + "\"}", HttpStatus.CONFLICT);
            default:
                log.trace("Leaving uploadFile for Project or Study [pid: {}, studyId: {}]", () -> pid, () -> studyId);
                return new ResponseEntity<>("", HttpStatus.OK);
        }
    }

    /**
     * This function is called after the upload of one or multiple files has finished to reload the file list and show the recently uploaded files.
     *
     * @param pid                Project Identifier as {@link Optional}&lt;{@link Long}&gt;
     * @param studyId            Study Identifier as {@link Optional}&lt;{@link Long}&gt;
     * @param redirectAttributes {@link RedirectAttributes}
     * @return Mapping to material.jsp for project files or study files - depending on the state of the study identifier (set = )
     */
    @RequestMapping(value = {"/{pid}/multisaved", "/{pid}/study/{studyId}/multisaved"})
    public String multiSaved(@PathVariable long pid, @PathVariable Optional<Long> studyId, RedirectAttributes redirectAttributes) {
        log.trace("Entering multiSaved for [pid: {}, studyid: {}]", () -> pid, () -> studyId);
        redirectAttributes.addFlashAttribute("saveState", SavedState.SUCCESS.toString());
        redirectAttributes.addFlashAttribute("saveStateMsg", messageSource.getMessage("material.upload.successful", null, LocaleContextHolder.getLocale()));
        String ret;
        if (studyId.isPresent()) {
            ret = "redirect:/project/" + pid + "/study/" + studyId.get() + "/material";
        } else {
            redirectAttributes.addFlashAttribute("jQueryMap", "material");
            ret = "redirect:/project/" + pid + "/material";
        }
        log.trace("Leaving multiSaved for [pid: {}, studyid: {}]", () -> pid, () -> studyId);
        return ret;
    }

    /**
     * This function is called if a user wants to download a file from the material pages.
     *
     * @param docId    Document Identifier as {@link Long}
     * @param response {@link HttpServletResponse}
     * @return Return the file as download, or an error message in case of an error.
     * @throws DataWizSystemException DataBase or Minio Exceptions
     */
    @RequestMapping(value = {"/{pid}/download/{docId}", "/{pid}/study/{studyId}/download/{docId}"}, method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity<String> downloadDocument(@PathVariable long docId, HttpServletResponse response) throws DataWizSystemException {
        log.trace("Entering downloadDocument [id: {}]", () -> docId);
        UserDTO user = UserUtil.getCurrentUser();
        ResponseEntity<String> resp = new ResponseEntity<>("{}", HttpStatus.OK);
        if (user == null) {
            log.warn(messageSource.getMessage("logging.user.auth.missing", null, Locale.ENGLISH));
            resp = new ResponseEntity<>("{}", HttpStatus.UNAUTHORIZED);
        } else {
            switch (projectService.prepareMaterialDownload(docId, response)) {
                case MINIO_READ_ERROR:
                    throw new DataWizSystemException(messageSource.getMessage("minio.connection.exception.upload",
                            new Object[]{env.getRequiredProperty("organisation.admin.email")}, LocaleContextHolder.getLocale()), DataWizErrorCodes.MINIO_READ_ERROR);
                case DATABASE_ERROR:
                    throw new DataWizSystemException(messageSource.getMessage("dbs.sql.exception", new Object[]{env.getRequiredProperty("organisation.admin.email")},
                            LocaleContextHolder.getLocale()), DataWizErrorCodes.DATABASE_ERROR);
                default:
                    log.debug("Leaving downloadDocument with result: {}", resp.getStatusCode());
                    break;
            }
        }
        return resp;
    }

    @RequestMapping(value = {"/{pid}/material/updateDescription", "/{pid}/study/{studyId}/material/updateDescription"}, method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity<String> updateMaterialDescription(@RequestParam("txt") String txt, @RequestParam("docId") long docId) {
        log.trace("Entering updateMaterialDescription for File [id: {}]", () -> docId);
        ResponseEntity<String> resp = new ResponseEntity<>("{}", HttpStatus.OK);
        projectService.saveFileDescription(docId, txt);

        return resp;
    }


    /**
     * This functions deletes a saved document from database and minio. It is called from the material pages (project and study).
     *
     * @param pid                Project Identifier as {@link Long}
     * @param docId              Document Identifier as {@link Long}
     * @param studyId            Study Identifier as {@link Optional}&lt;{@link Long}&gt;
     * @param redirectAttributes {@link RedirectAttributes}
     * @return Redirect to material.jsp. In case of an error, with error message
     */
    @RequestMapping(value = {"/{pid}/delDoc/{docId}", "/{pid}/study/{studyId}/delDoc/{docId}"}, method = RequestMethod.GET)
    public String deleteDocument(@PathVariable long pid, @PathVariable long docId, @PathVariable Optional<Long> studyId,
                                 RedirectAttributes redirectAttributes) {
        log.trace("Entering deleteDocument [id: {}]", () -> docId);
        UserDTO user = UserUtil.getCurrentUser();
        String ret;
        if (user == null) {
            log.warn(messageSource.getMessage("logging.user.auth.missing", null, Locale.ENGLISH));
            ret = "redirect:/login";
        } else {
            switch (projectService.deleteMaterialfromMinioAndDB(docId)) {
                case MINIO_SAVE_ERROR:
                    redirectAttributes.addFlashAttribute("errorMSG", messageSource.getMessage("material.delete.minio.error", null, LocaleContextHolder.getLocale()));
                case DATABASE_ERROR:
                    redirectAttributes.addFlashAttribute("errorMSG", messageSource.getMessage("material.delete.db.error", null, LocaleContextHolder.getLocale()));
                default:
                    redirectAttributes.addFlashAttribute("successMSG", messageSource.getMessage("material.delete.successful", null, LocaleContextHolder.getLocale()));
                    break;
            }
            redirectAttributes.addFlashAttribute("jQueryMap", "material");
            ret = "redirect:/project/" + pid + "/material";
            if (studyId.isPresent())
                return "redirect:/project/" + pid + "/study/" + studyId.get() + "/material";
        }
        return ret;
    }

    /**
     * This function builds the thumb-nail images and put them into the response via projectService.scaleAndSetThumbnail(...). It is called from the material
     * pages (project and study).
     *
     * @param pid      Project Identifier as {@link Long}
     * @param imgId    Image/File Identifier as {@link Long}
     * @param response {@link HttpServletResponse}
     */
    @RequestMapping(value = {"/{pid}/img/{imgId}", "/{pid}/study/{studyId}/img/{imgId}"}, method = RequestMethod.GET)
    public void setThumbnailImage(@PathVariable long pid, @PathVariable long imgId, HttpServletResponse response) {
        final int thumbHeight = 300;
        final int maxWidth = 600;
        try {
            projectService.scaleAndSetThumbnail(imgId, response, thumbHeight, maxWidth);
        } catch (Exception e) {
            log.warn("Warn: setThumbnailImage throws an exception: ", () -> e);
        }
    }

    /**
     * This function is called if a user wants to delete a whole project with all of its dependencies. It checks if the user has the rights to delete the project.
     * If the rights to delete are given projectService.deleteProject(...) is called to delete the project finally from Database and Minio (material and records).
     *
     * @param pid                Project Identifier as {@link Optional}&lt;{@link Long}&gt;
     * @param redirectAttributes {@link RedirectAttributes}
     * @param model              {@link ModelMap}
     * @return Mapping to "redirect:/panel" n success, otherwise to "project.jsp on error, or error.jsp on database error"
     */
    @RequestMapping(value = {"", "/{pid}/deleteProject"})
    public String deleteProject(@PathVariable final Optional<Long> pid, final ModelMap model, final RedirectAttributes redirectAttributes) {
        UserDTO user = UserUtil.getCurrentUser();
        log.trace("Entering  deleteProject for [projectId: {}] user[id: {}; email: {}]", pid::get, user::getId, user::getEmail);
        String ret = "redirect:/panel";
        try {
            projectService.deleteProject(pid.orElse(0L), user);
        } catch (DataWizSystemException e) {
            if (e.getErrorCode().equals(DataWizErrorCodes.DATABASE_ERROR)) {
                model.put("errormsg",
                        messageSource.getMessage("dbs.sql.exception",
                                new Object[]{env.getRequiredProperty("organisation.admin.email"), e.getMessage().replaceAll("\n", "").replaceAll("\"", "\'")},
                                LocaleContextHolder.getLocale()));
                ret = "error";
            } else if (e.getErrorCode().equals(DataWizErrorCodes.STUDY_DELETE_ERROR)) {
                model.put("subnaviActive", PageState.RECORDMETA.name());
                model.put("recordSubMenu", true);
                model.put("errorMSG",
                        messageSource.getMessage("project.study.delete.error", new Object[]{e.getMessage(), e.getErrorCode()}, LocaleContextHolder.getLocale()));
                ret = "project";
            } else {
                model.put("subnaviActive", PageState.RECORDMETA.name());
                model.put("recordSubMenu", true);
                model.put("errorMSG",
                        messageSource.getMessage("project.not.deleted.error", new Object[]{e.getMessage(), e.getErrorCode()}, LocaleContextHolder.getLocale()));
                ret = "project";
            }
        }
        if (log.isTraceEnabled())
            log.trace("Method deleteProject completed - mapping to {}", ret);
        return ret;
    }

}
