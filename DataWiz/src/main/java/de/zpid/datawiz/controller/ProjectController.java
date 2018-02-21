package de.zpid.datawiz.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import javax.servlet.http.HttpServletResponse;

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
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
import de.zpid.datawiz.util.BreadCrumpUtil;
import de.zpid.datawiz.util.UserUtil;

/**
 * Controller for mapping "/project" <br />
 * <br />
 * This file is part of Datawiz.<br />
 * 
 * <b>Copyright 2017, Leibniz Institute for Psychology Information (ZPID), <a href="http://zpid.de" title="http://zpid.de">http://zpid.de</a>.</b><br />
 * <br />
 * <a rel="license" href="http://creativecommons.org/licenses/by-nc-sa/4.0/"><img alt="Creative Commons License" style= "border-width:0" src=
 * "https://i.creativecommons.org/l/by-nc-sa/4.0/80x15.png" /></a><br />
 * <span xmlns:dct="http://purl.org/dc/terms/" property="dct:title">Datawiz</span> by
 * <a xmlns:cc="http://creativecommons.org/ns#" href="zpid.de" property="cc:attributionName" rel="cc:attributionURL"> Leibniz Institute for Psychology
 * Information (ZPID)</a> is licensed under a <a rel="license" href="http://creativecommons.org/licenses/by-nc-sa/4.0/">Creative Commons
 * Attribution-NonCommercial-ShareAlike 4.0 International License</a>.
 * 
 * @author Ronny Boelter
 * @version 1.0
 *
 */
@Controller
@RequestMapping(value = "/project")
public class ProjectController {

	@Autowired
	private MessageSource messageSource;
	@Autowired
	private ClassPathXmlApplicationContext applicationContext;
	@Autowired
	private SmartValidator validator;
	@Autowired
	private ProjectService projectService;
	@Autowired
	private ExceptionService exceptionService;
	@Autowired
	private Environment env;

	/**
	 * Creates the project form.
	 *
	 * @return {@link ProjectForm}
	 */
	@ModelAttribute("ProjectForm")
	private ProjectForm createProjectForm() {
		return (ProjectForm) applicationContext.getBean("ProjectForm");
	}

	private static Logger log = LogManager.getLogger(ProjectController.class);

	/**
	 * Instantiates a new project controller.
	 */
	public ProjectController() {
		super();
		log.info("Loading ProjectController for mapping /project");
	}

	/**
	 * This function handles the calls to the project.jsp (/project/{pid}). It distinguishes between the different user roles. If a user has no access to the
	 * project and dmp meta data, but has access to a study, it automatically redirects to the study overview.
	 * 
	 * @param pid
	 *          Project Identifier as {@link Optional}&lt;{@link Long}&gt;
	 * @param model
	 *          {@link ModelMap}
	 * @param redirectAttributes
	 *          {@link RedirectAttributes}
	 * @return Mapping to project.jsp on success, otherwise redirect mapping to login on authentication errors, or to panel on access denied, or internal errors.
	 */
	@RequestMapping(value = { "", "/{pid}" }, method = RequestMethod.GET)
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
				log.warn("Exception during projectService.getProjectForm for [user: {}, pid: {}]", () -> user.getId(), () -> pid, () -> e);
				ret = exceptionService.setErrorMessagesAndRedirects(pid, null, null, model, redirectAttributes, e, "ProjectController.showProjectPage");
			}
		} else {
			model.put("hideMenu", true);
			List<ContributorDTO> cContri = new ArrayList<>();
			cContri.add(new ContributorDTO());
			pForm.setContributors(cContri);
		}
		model.put("breadcrumpList", BreadCrumpUtil.generateBC(PageState.PROJECT, new String[] { name }, null, messageSource));
		model.put("subnaviActive", PageState.PROJECT.name());
		model.put("ProjectForm", pForm);
		log.trace("Leaving showProjectPage for project [id: {}]", () -> pid.isPresent() ? pid.get() : "null (new project)");
		return ret;
	}

	/**
	 * This function handles the mapping to a project's study overview (studies.jsp).
	 * 
	 * @param pid
	 *          Project Identifier as {@link Optional}&lt;{@link Long}&gt;
	 * @param model
	 *          {@link ModelMap}
	 * @param redirectAttributes
	 *          {@link RedirectAttributes}
	 * @return Mapping to studies.jsp on success, otherwise redirect mapping to login on authentication errors, or to panel on access denied, or internal errors
	 */
	@RequestMapping(value = { "/{pid}/studies" }, method = RequestMethod.GET)
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
					model.put("breadcrumpList", BreadCrumpUtil.generateBC(PageState.PROJECT, new String[] { pForm.getProject().getTitle() }, null, messageSource));
					model.put("subnaviActive", PageState.STUDIES.name());
					model.put("ProjectForm", pForm);
				} catch (Exception e) {
					log.warn("Exeption during projectService.getProjectForm - Exception: ", () -> e);
					ret = exceptionService.setErrorMessagesAndRedirects(pid, null, null, model, redirectAttributes, e, "ProjectController.showProjectPage");
				}
			} else {
				log.warn(messageSource.getMessage("logging.user.permitted", new Object[] { user.getId(), "project", pid }, Locale.ENGLISH));
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
	 * @param pid
	 *          Project Identifier as {@link Optional}&lt;{@link Long}&gt;
	 * @param studyId
	 *          Study Identifier as {@link Optional}&lt;{@link Long}&gt;
	 * @param model
	 *          {@link ModelMap}
	 * @param redirectAttributes
	 *          {@link RedirectAttributes}
	 * @return Mapping to material.jsp on success, otherwise redirect mapping to login on authentication errors, or to panel on access denied, or internal errors
	 */
	@RequestMapping(value = { "/{pid}/material", "/{pid}/study/{studyId}/material" }, method = RequestMethod.GET)
	public String showMaterialPage(@PathVariable Optional<Long> pid, @PathVariable Optional<Long> studyId, ModelMap model,
	    RedirectAttributes redirectAttributes) {
		log.trace("Entering showMaterialPage for project [id: {}]", () -> pid.isPresent() ? pid.get() : "null");
		UserDTO user = UserUtil.getCurrentUser();
		String ret = null;
		if (user == null) {
			log.warn(messageSource.getMessage("logging.user.auth.missing", null, Locale.ENGLISH));
			ret = "redirect:/login";
		} else if (!pid.isPresent()) {
			log.warn(messageSource.getMessage("logging.pid.not.present", null, Locale.ENGLISH));
			redirectAttributes.addFlashAttribute("errorMSG", messageSource.getMessage("project.access.denied", null, LocaleContextHolder.getLocale()));
			ret = "redirect:/panel";
		} else {
			ProjectForm pForm = createProjectForm();
			ret = projectService.checkUserAccess(pid, studyId, redirectAttributes, false, user);
			if (ret != null) {
				redirectAttributes.addFlashAttribute("errorMSG", messageSource.getMessage("project.access.denied", null, LocaleContextHolder.getLocale()));
			} else {
				try {
					projectService.setMaterialForm(pid, studyId, model, redirectAttributes, user, pForm);
					ret = "material";
					model.put("studyId", studyId.isPresent() ? studyId.get() : -1);
					model.put("projectId", pid.get());
					model.put("ProjectForm", pForm);
					model.put("subnaviActive", PageState.MATERIAL.name());
				} catch (Exception e) {
					log.warn("Error in setMaterialForm Message: ", () -> e);
					exceptionService.setErrorMessagesAndRedirects(pid, studyId, null, model, redirectAttributes, e, "projectService.setMaterialForm");
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
	 * @param pForm
	 *          {@link ProjectForm}
	 * @param bindingResult
	 *          {@link BindingResult}
	 * @param model
	 *          {@link ModelMap}
	 * @param redirectAttributes
	 *          {@link RedirectAttributes}
	 * @return On success redirect mapping to /project, on error mapping to project.jsp with error messages
	 */
	@RequestMapping(value = { "", "/{pid}" }, method = RequestMethod.POST)
	public String saveProject(@ModelAttribute("ProjectForm") final ProjectForm pForm, final BindingResult bindingResult, final ModelMap model,
	    final RedirectAttributes redirectAttributes) {
		String ret = "project";
		if (pForm != null && pForm.getProject() != null) {
			log.trace("Entering saveProject for project [id: {}]", () -> (pForm.getProject().getId() == 0 ? "new project" : pForm.getProject().getId()));
			if (pForm.getProject().getId() > 0) {
				String access = projectService.checkUserAccess(Optional.of(pForm.getProject().getId()), null, redirectAttributes, true, UserUtil.getCurrentUser());
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
					    messageSource.getMessage("logging.internal.error", new Object[] { e.getMessage() }, LocaleContextHolder.getLocale()));
					log.warn(messageSource.getMessage("logging.project.error", null, Locale.ENGLISH), () -> e);
				}
				if (bindingResult.hasErrors()) {
					log.debug("bindingResult has Errors " + bindingResult.getAllErrors().toString());
				} else {
					pForm.setContributors(cContri);
					DataWizErrorCodes state = projectService.saveOrUpdateProject(pForm);
					log.debug("Leaving saveProject with result: [code: {}]", () -> state.name());
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
			model.put("breadcrumpList", BreadCrumpUtil.generateBC(PageState.PROJECT, new String[] { pForm.getProject().getTitle() }, null, messageSource));
		}
		return ret;
	}

	/**
	 * This function is used by the dropzone in the material.jsp of the study and project material pages. It saves an uploaded file by using
	 * projectService.saveMaterialToMinoAndDB and returns a HttpStatus.
	 * 
	 * @param request
	 *          {@link MultipartHttpServletRequest}
	 * 
	 * @param redirectAttributes
	 *          {@link RedirectAttributes}
	 * @param pid
	 *          Project Identifier as {@link Optional}&lt;{@link Long}&gt;
	 * @param studyId
	 *          Study Identifier as {@link Optional}&lt;{@link Long}&gt;
	 * @return Success: HttpStatus.OK <br />
	 *         Database Error: HttpStatus.CONFLICT <br />
	 *         Minio or Auth error: HttpStatus.INTERNAL_SERVER_ERROR
	 */
	@RequestMapping(value = { "/{pid}/upload", "/{pid}/study/{studyId}/upload" }, method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<String> uploadFile(MultipartHttpServletRequest request, RedirectAttributes redirectAttributes,
	    @PathVariable Optional<Long> pid, @PathVariable Optional<Long> studyId) {
		log.trace("Entering uploadFile for Project or Study [pid: {}, studyId: {}]", () -> pid, () -> studyId);
		if (!pid.isPresent()) {
			log.warn("ProjectForm is empty or missing values");
			return new ResponseEntity<String>("{\"error\" : \"" + messageSource.getMessage("project.not.available", null, LocaleContextHolder.getLocale()) + "\"}",
			    HttpStatus.INTERNAL_SERVER_ERROR);
		}
		UserDTO user = UserUtil.getCurrentUser();
		if (user == null) {
			log.warn(messageSource.getMessage("logging.user.auth.missing", null, Locale.ENGLISH));
			return new ResponseEntity<String>("{\"error\" : \"" + messageSource.getMessage("project.not.available", null, LocaleContextHolder.getLocale()) + "\"}",
			    HttpStatus.INTERNAL_SERVER_ERROR);
		}
		if (projectService.checkUserAccess(pid, studyId, redirectAttributes, true, user) != null) {
			log.warn(messageSource.getMessage("logging.user.auth.missing", null, Locale.ENGLISH));
			return new ResponseEntity<String>("{\"error\" : \"" + messageSource.getMessage("project.access.denied", null, LocaleContextHolder.getLocale()) + "\"}",
			    HttpStatus.INTERNAL_SERVER_ERROR);
		}
		switch (projectService.saveMaterialToMinoAndDB(request, pid, studyId, user)) {
		case MINIO_SAVE_ERROR:
			return new ResponseEntity<String>("{\"error\" : \"" + messageSource.getMessage("minio.connection.exception.upload",
			    new Object[] { env.getRequiredProperty("organisation.admin.email") }, LocaleContextHolder.getLocale()) + "\"}", HttpStatus.INTERNAL_SERVER_ERROR);
		case DATABASE_ERROR:
			return new ResponseEntity<String>("{\"error\" : \""
			    + messageSource.getMessage("dbs.sql.exception", new Object[] { env.getRequiredProperty("organisation.admin.email") }, LocaleContextHolder.getLocale())
			    + "\"}", HttpStatus.CONFLICT);
		default:
			log.trace("Leaving uploadFile for Project or Study [pid: {}, studyId: {}]", () -> pid, () -> studyId);
			return new ResponseEntity<String>("", HttpStatus.OK);
		}
	}

	/**
	 * This function is called after the upload of one or multiple files has finished to reload the file list and show the recently uploaded files.
	 * 
	 * 
	 * @param pid
	 *          Project Identifier as {@link Optional}&lt;{@link Long}&gt;
	 * @param studyId
	 *          Study Identifier as {@link Optional}&lt;{@link Long}&gt;
	 * @param redirectAttributes
	 *          {@link RedirectAttributes}
	 * @return Mapping to material.jsp for project files or study files - depending on the state of the study identifier (set = )
	 */
	@RequestMapping(value = { "/{pid}/multisaved", "/{pid}/study/{studyId}/multisaved" })
	public String multiSaved(@PathVariable Optional<Long> pid, @PathVariable Optional<Long> studyId, RedirectAttributes redirectAttributes) {
		log.trace("Entering multiSaved for [pid: {}, studyid: {}]", () -> pid, () -> studyId);
		redirectAttributes.addFlashAttribute("saveState", SavedState.SUCCESS.toString());
		redirectAttributes.addFlashAttribute("saveStateMsg", messageSource.getMessage("material.upload.successful", null, LocaleContextHolder.getLocale()));
		String ret = null;
		if (studyId.isPresent()) {
			ret = "redirect:/project/" + pid.get() + "/study/" + studyId.get() + "/material";
		} else {
			redirectAttributes.addFlashAttribute("jQueryMap", "material");
			ret = "redirect:/project/" + pid.get() + "/material";
		}
		log.trace("Leaving multiSaved for [pid: {}, studyid: {}]", () -> pid, () -> studyId);
		return ret;
	}

	/**
	 * This function is called if a user wants to download a file from the material pages.
	 * 
	 * @param docId
	 *          Document Identifier as {@link Long}
	 * @param response
	 *          {@link HttpServletResponse}
	 * @param redirectAttributes
	 *          {@link RedirectAttributes}
	 * @return Return the file as download, or an error message in case of an error.
	 * @throws DataWizSystemException
	 */
	@RequestMapping(value = { "/{pid}/download/{docId}", "/{pid}/study/{studyId}/download/{docId}" }, method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<String> downloadDocument(@PathVariable long pid, @PathVariable long docId, HttpServletResponse response,
	    RedirectAttributes redirectAttributes) throws DataWizSystemException {
		log.trace("Entering downloadDocument [id: {}]", () -> docId);
		UserDTO user = UserUtil.getCurrentUser();
		ResponseEntity<String> resp = new ResponseEntity<String>("{}", HttpStatus.OK);
		if (user == null) {
			log.warn(messageSource.getMessage("logging.user.auth.missing", null, Locale.ENGLISH));
			resp = new ResponseEntity<String>("{}", HttpStatus.UNAUTHORIZED);
		} else {
			switch (projectService.prepareMaterialDownload(docId, response)) {

			case MINIO_READ_ERROR:
				throw new DataWizSystemException(messageSource.getMessage("minio.connection.exception.upload",
				    new Object[] { env.getRequiredProperty("organisation.admin.email") }, LocaleContextHolder.getLocale()), DataWizErrorCodes.MINIO_READ_ERROR);
			case DATABASE_ERROR:
				throw new DataWizSystemException(messageSource.getMessage("dbs.sql.exception", new Object[] { env.getRequiredProperty("organisation.admin.email") },
				    LocaleContextHolder.getLocale()), DataWizErrorCodes.DATABASE_ERROR);
			default:
				log.debug("Leaving downloadDocument with result: {}", resp.getStatusCode());
				break;
			}
		}
		return resp;
	}

	/**
	 * This functions deletes a saved document from database and minio. It is called from the material pages (project and study).
	 * 
	 * @param pid
	 *          Project Identifier as {@link Optional}&lt;{@link Long}&gt;
	 * @param docId
	 *          Document Identifier as {@link Long}
	 * @param studyId
	 *          Study Identifier as {@link Optional}&lt;{@link Long}&gt;
	 * @param redirectAttributes
	 *          {@link RedirectAttributes}
	 * @return Redirect to material.jsp. In case of an error, with error message
	 */
	@RequestMapping(value = { "/{pid}/delDoc/{docId}", "/{pid}/study/{studyId}/delDoc/{docId}" }, method = RequestMethod.GET)
	public String deleteDocument(@PathVariable Optional<Long> pid, @PathVariable long docId, @PathVariable Optional<Long> studyId,
	    RedirectAttributes redirectAttributes) {
		log.trace("Entering deleteDocument [id: {}]", () -> docId);
		UserDTO user = UserUtil.getCurrentUser();
		if (user == null) {
			log.warn(messageSource.getMessage("logging.user.auth.missing", null, Locale.ENGLISH));
			return "redirect:/login";
		}
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
		if (studyId.isPresent())
			return "redirect:/project/" + pid.get() + "/study/" + studyId.get() + "/material";
		return "redirect:/project/" + pid.get() + "/material";
	}

	/**
	 * This function builds the thumb-nail images and put them into the response via projectService.scaleAndSetThumbnail(...). It is called from the material
	 * pages (project and study).
	 * 
	 * @param pid
	 *          Project Identifier as {@link Long}
	 * @param imgId
	 *          Image/File Identifier as {@link Long}
	 * @param response
	 *          {@link HttpServletResponse}
	 */
	@RequestMapping(value = { "/{pid}/img/{imgId}", "/{pid}/study/{studyId}/img/{imgId}" }, method = RequestMethod.GET)
	private void setThumbnailImage(@PathVariable long pid, @PathVariable long imgId, HttpServletResponse response) {
		final int thumbHeight = 98;
		final int maxWidth = 160;
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
	 * @param pid
	 *          Project Identifier as {@link Optional}&lt;{@link Long}&gt;
	 * @param redirectAttributes
	 *          {@link RedirectAttributes}
	 * @param model
	 *          {@link ModelMap}
	 * @return Mapping to "redirect:/panel" n success, otherwise to "project.jsp on error, or error.jsp on database error"
	 */
	@RequestMapping(value = { "", "/{pid}/deleteProject" })
	public String deleteProject(@PathVariable final Optional<Long> pid, final ModelMap model, final RedirectAttributes redirectAttributes) {
		UserDTO user = UserUtil.getCurrentUser();
		log.trace("Entering  deleteProject for [projectId: {}] user[id: {}; email: {}]", () -> pid.get(), () -> user.getId(), () -> user.getEmail());
		String ret = "redirect:/panel";
		try {
			projectService.deleteProject(pid, user);
		} catch (DataWizSystemException e) {
			if (e.getErrorCode().equals(DataWizErrorCodes.DATABASE_ERROR)) {
				model.put("errormsg",
				    messageSource.getMessage("dbs.sql.exception",
				        new Object[] { env.getRequiredProperty("organisation.admin.email"), e.getMessage().replaceAll("\n", "").replaceAll("\"", "\'") },
				        LocaleContextHolder.getLocale()));
				ret = "error";
			} else if (e.getErrorCode().equals(DataWizErrorCodes.STUDY_DELETE_ERROR)) {
				model.put("subnaviActive", PageState.RECORDMETA.name());
				model.put("recordSubMenu", true);
				model.put("errorMSG",
				    messageSource.getMessage("project.study.delete.error", new Object[] { e.getMessage(), e.getErrorCode() }, LocaleContextHolder.getLocale()));
				ret = "project";
			} else {
				model.put("subnaviActive", PageState.RECORDMETA.name());
				model.put("recordSubMenu", true);
				model.put("errorMSG",
				    messageSource.getMessage("project.not.deleted.error", new Object[] { e.getMessage(), e.getErrorCode() }, LocaleContextHolder.getLocale()));
				ret = "project";
			}
		}
		if (log.isTraceEnabled())
			log.trace("Method deleteProject completed - mapping to {}", ret);
		return ret;
	}

}
