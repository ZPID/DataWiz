package de.zpid.datawiz.controller;

import java.util.ArrayList;
import java.util.List;
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
import org.springframework.web.bind.annotation.SessionAttributes;
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
 * <b>Copyright 2017, Leibniz Institute for Psychology Information (ZPID),
 * <a href="http://zpid.de" title="http://zpid.de">http://zpid.de</a>.</b><br />
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
@SessionAttributes({ "ProjectForm", "subnaviActive" })
public class ProjectController {

	@Autowired
	protected MessageSource messageSource;
	@Autowired
	protected ClassPathXmlApplicationContext applicationContext;
	@Autowired
	protected SmartValidator validator;
	@Autowired
	private ProjectService projectService;
	@Autowired
	private ExceptionService exceptionService;
	@Autowired
	private Environment env;

	@ModelAttribute("ProjectForm")
	protected ProjectForm createProjectForm() {
		return (ProjectForm) applicationContext.getBean("ProjectForm");
	}

	private static Logger log = LogManager.getLogger(ProjectController.class);

	public ProjectController() {
		super();
		log.info("Loading ProjectController for mapping /project");
	}

	/**
	 * 
	 * @param pid
	 * @param model
	 * @param redirectAttributes
	 * @return
	 */
	@RequestMapping(value = { "", "/{pid}" }, method = RequestMethod.GET)
	public String showProjectPage(@PathVariable Optional<Long> pid, ModelMap model, RedirectAttributes redirectAttributes) {
		log.trace("Entering showProjectPage for projectID= {}", () -> pid.isPresent() ? pid.get() : "null (new project)");
		UserDTO user = UserUtil.getCurrentUser();
		if (user == null) {
			log.warn("Auth User Object == null - redirect to login");
			return "redirect:/login";
		}
		ProjectForm pForm = createProjectForm();
		String name = messageSource.getMessage("breadcrumb.new.project", null, LocaleContextHolder.getLocale());
		if (pid.isPresent()) {
			try {
				Roles role = projectService.checkProjectRoles(user, pid.get(), 0, false, true);
				if (role != null && (!role.equals(Roles.ADMIN) && !role.equals(Roles.PROJECT_ADMIN) && !role.equals(Roles.PROJECT_READER)
				    && !role.equals(Roles.PROJECT_WRITER)) && (role.equals(Roles.DS_READER) || role.equals(Roles.DS_WRITER))) {
					redirectAttributes.addFlashAttribute("hideMenu", true);
					return "redirect:/project/" + pid.get() + "/studies";
				}
				projectService.getProjectForm(pForm, pid.get(), user, PageState.PROJECT, role);
				name = pForm.getProject().getTitle();
			} catch (Exception e) {
				log.warn("Exception during projectService.getProjectForm for [user: {}, pid: {}]", () -> user.getId(), () -> pid, () -> e);
				return exceptionService.setErrorMessagesAndRedirects(pid, null, null, model, redirectAttributes, e, "ProjectController.showProjectPage");
			}
		} else {
			List<ContributorDTO> cContri = new ArrayList<>();
			cContri.add(new ContributorDTO());
			pForm.setContributors(cContri);
		}
		model.put("breadcrumpList", BreadCrumpUtil.generateBC(PageState.PROJECT, new String[] { name }, null, messageSource));
		model.put("subnaviActive", PageState.PROJECT.name());
		model.put("ProjectForm", pForm);
		return "project";
	}

	/**
	 * 
	 * @param pid
	 * @param model
	 * @param redirectAttributes
	 * @return
	 */
	@RequestMapping(value = { "/{pid}/studies" }, method = RequestMethod.GET)
	public String showStudiesPage(@PathVariable Optional<Long> pid, ModelMap model, RedirectAttributes redirectAttributes) {
		log.trace("Entering showStudiesPage for projectID= {}", () -> pid.isPresent() ? pid.get() : "null");
		UserDTO user = UserUtil.getCurrentUser();
		if (user == null) {
			log.warn("Auth User Object == null - redirect to login");
			return "redirect:/login";
		}
		ProjectForm pForm = createProjectForm();
		if (!pid.isPresent()) {
			log.debug("Access denied for showStudiesPage with pid = null");
			redirectAttributes.addFlashAttribute("errorMSG", messageSource.getMessage("project.access.denied", null, LocaleContextHolder.getLocale()));
			return "redirect:/panel";
		}
		try {
			Roles role = projectService.checkProjectRoles(user, pid.get(), 0, false, true);
			if ((!role.equals(Roles.ADMIN) && !role.equals(Roles.PROJECT_ADMIN) && !role.equals(Roles.PROJECT_READER) && !role.equals(Roles.PROJECT_WRITER))
			    && (role.equals(Roles.DS_READER) || role.equals(Roles.DS_WRITER))) {
				model.put("hideMenu", true);
			}
			projectService.getProjectForm(pForm, pid.get(), user, PageState.STUDIES, role);
		} catch (Exception e) {
			log.warn("Exeption during projectService.getProjectForm - Exception: ", () -> e);
			return exceptionService.setErrorMessagesAndRedirects(pid, null, null, model, redirectAttributes, e, "ProjectController.showProjectPage");
		}
		model.put("breadcrumpList", BreadCrumpUtil.generateBC(PageState.PROJECT, new String[] { pForm.getProject().getTitle() }, null, messageSource));
		model.put("subnaviActive", PageState.STUDIES.name());
		model.put("ProjectForm", pForm);
		return "studies";
	}

	/**
	 * 
	 * @param pid
	 * @param studyId
	 * @param model
	 * @param redirectAttributes
	 * @return
	 */
	@RequestMapping(value = { "/{pid}/material", "/{pid}/study/{studyId}/material" }, method = RequestMethod.GET)
	public String showMaterialPage(@PathVariable Optional<Long> pid, @PathVariable Optional<Long> studyId, ModelMap model,
	    RedirectAttributes redirectAttributes) {
		log.trace("execute showMaterialPage for projectID= {}", () -> pid.isPresent() ? pid.get() : "null");
		UserDTO user = UserUtil.getCurrentUser();
		String ret = null;
		if (user == null) {
			log.warn("Auth User Object == null - redirect to login");
			ret = "redirect:/login";
		} else {
			if (!pid.isPresent()) {
				redirectAttributes.addFlashAttribute("errorMSG", messageSource.getMessage("project.access.denied", null, LocaleContextHolder.getLocale()));
				ret = "redirect:/panel";
			}
			ret = projectService.checkUserAccess(pid, studyId, redirectAttributes, false, user);
		}
		ProjectForm pForm = createProjectForm();
		if (ret == null) {
			if (projectService.setMaterialForm(pid, studyId, model, redirectAttributes, user, pForm)) {
				ret = "material";
				model.put("studyId", studyId.isPresent() ? studyId.get() : -1);
				model.put("projectId", pid.get());
				model.put("ProjectForm", pForm);
				model.put("subnaviActive", PageState.MATERIAL.name());
			} else {
				ret = "redirect:/panel";
			}
		}
		return ret;
	}

	/**
	 * 
	 * @param pForm
	 * @param bindingResult
	 * @param model
	 * @param redirectAttributes
	 * @return
	 */
	@RequestMapping(value = { "", "/{pid}" }, method = RequestMethod.POST)
	public String saveProject(@ModelAttribute("ProjectForm") ProjectForm pForm, BindingResult bindingResult, ModelMap model,
	    RedirectAttributes redirectAttributes) {
		log.trace("Entering saveProject");
		validator.validate(pForm, bindingResult, ProjectDTO.ProjectVal.class);
		validator.validate(pForm, bindingResult);
		List<ContributorDTO> cContri = projectService.validateContributors(pForm, bindingResult);
		if (bindingResult.hasErrors()) {
			if (log.isInfoEnabled()) {
				log.info("bindingResult has Errors " + bindingResult.getAllErrors().toString());
			}
			model.put("errorMSG", messageSource.getMessage("error.project.validation", null, LocaleContextHolder.getLocale()));
			return "project";
		}
		pForm.setContributors(cContri);
		DataWizErrorCodes state = projectService.saveOrUpdateProject(pForm);
		log.debug("Leaving saveProject with result: [code: {}]", () -> state.name());
		if (!state.equals(DataWizErrorCodes.OK)) {
			model.put("errorMSG", messageSource.getMessage("project.save.error." + state.name(), null, LocaleContextHolder.getLocale()));
			return "project";
		} else {
			redirectAttributes.addFlashAttribute("successMSG",
			    messageSource.getMessage("project.save.error." + state.name(), null, LocaleContextHolder.getLocale()));
			return "redirect:/project/" + pForm.getProject().getId();
		}
	}

	/**
	 * @param pForm
	 * @param model
	 * @return
	 */
	@RequestMapping(value = { "", "/{pid}" }, params = { "addContributor" }, method = RequestMethod.POST)
	public String addContributor(@ModelAttribute("ProjectForm") ProjectForm pForm, ModelMap model) {
		log.trace("Entering addContributor");
		if (pForm.getContributors() == null) {
			pForm.setContributors(new ArrayList<ContributorDTO>());
		}
		pForm.getContributors().add(0, (ContributorDTO) applicationContext.getBean("ContributorDTO"));
		return "project";
	}

	/**
	 * 
	 * @param pForm
	 * @param model
	 * @return
	 */
	@RequestMapping(value = { "", "/{pid}" }, params = { "deleteContributor" }, method = RequestMethod.POST)
	public String deleteContributor(@ModelAttribute("ProjectForm") ProjectForm pForm, ModelMap model) {
		log.trace("Entering deleteContributor");
		if (pForm.getContributors() == null) {
			pForm.setContributors(new ArrayList<ContributorDTO>());
		}
		try {
			if (!projectService.deleteContributor(pForm))
				model.put("errorMSG", messageSource.getMessage("error.project.delete.contributor", null, LocaleContextHolder.getLocale()));
		} catch (Exception e) {
			model.put("errorMSG", messageSource.getMessage("error.project.delete.contributor", null, LocaleContextHolder.getLocale()));
			log.warn("Error deleting Contributor for [pid: {}]", () -> pForm.getProject().getId(), () -> e);
		}
		model.put("successMSG", messageSource.getMessage("project.delete.contributor.success", null, LocaleContextHolder.getLocale()));
		return "project";
	}

	/**
	 * 
	 * @param request
	 * @param pForm
	 * @param redirectAttributes
	 * @param pid
	 * @param studyId
	 * @return
	 */
	@RequestMapping(value = { "/{pid}/upload", "/{pid}/study/{studyId}/upload" }, method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<String> uploadFile(MultipartHttpServletRequest request, @ModelAttribute("ProjectForm") ProjectForm pForm,
	    RedirectAttributes redirectAttributes, @PathVariable Optional<Long> pid, @PathVariable Optional<Long> studyId) {
		log.trace("Entering uploadFile for Project or Study [pid: {}, studyId: {}]", () -> pid, () -> studyId);
		if (!pid.isPresent()) {
			log.warn("ProjectForm is empty or missing values");
			return new ResponseEntity<String>(
			    "{\"error\" : \"" + messageSource.getMessage("project.not.available", null, LocaleContextHolder.getLocale()) + "\"}",
			    HttpStatus.INTERNAL_SERVER_ERROR);
		}
		UserDTO user = UserUtil.getCurrentUser();
		if (user == null) {
			log.warn("Auth User Object == null - redirect to login");
			return new ResponseEntity<String>(
			    "{\"error\" : \"" + messageSource.getMessage("project.not.available", null, LocaleContextHolder.getLocale()) + "\"}",
			    HttpStatus.INTERNAL_SERVER_ERROR);
		}
		if (projectService.checkUserAccess(pid, studyId, redirectAttributes, true, user) != null) {
			log.warn("Auth User Object == null - redirect to login");
			return new ResponseEntity<String>(
			    "{\"error\" : \"" + messageSource.getMessage("project.access.denied", null, LocaleContextHolder.getLocale()) + "\"}",
			    HttpStatus.INTERNAL_SERVER_ERROR);
		}
		switch (projectService.saveMaterialToMinoAndDB(request, pid, studyId, user)) {
		case MINIO_SAVE_ERROR:
			return new ResponseEntity<String>(
			    "{\"error\" : \"" + messageSource.getMessage("minio.connection.exception.upload", null, LocaleContextHolder.getLocale()) + "\"}",
			    HttpStatus.INTERNAL_SERVER_ERROR);
		case DATABASE_ERROR:
			return new ResponseEntity<String>(
			    "{\"error\" : \"" + messageSource.getMessage("dbs.sql.exception", null, LocaleContextHolder.getLocale()) + "\"}", HttpStatus.CONFLICT);
		default:
			log.trace("Method uploadFile successfully completed");
			return new ResponseEntity<String>("", HttpStatus.OK);
		}
	}

	/**
	 * 
	 * @param pForm
	 * @param model
	 * @param redirectAttributes
	 * @return
	 */
	@RequestMapping(value = { "/{pid}/multisaved", "/{pid}/study/{studyId}/multisaved" })
	public String multiSaved(@ModelAttribute("ProjectForm") ProjectForm pForm, @PathVariable Optional<Long> pid, @PathVariable Optional<Long> studyId,
	    RedirectAttributes redirectAttributes, ModelMap model) {
		if (log.isDebugEnabled()) {
			log.debug("Entering multiSaved for [pid: {}, studyid: {}]", () -> pid, () -> studyId);
		}
		redirectAttributes.addFlashAttribute("saveState", SavedState.SUCCESS.toString());
		redirectAttributes.addFlashAttribute("saveStateMsg",
		    messageSource.getMessage("material.upload.successful", null, LocaleContextHolder.getLocale()));
		if (studyId.isPresent()) {
			return "redirect:/project/" + pid.get() + "/study/" + studyId.get() + "/material";
		} else {
			redirectAttributes.addFlashAttribute("jQueryMap", "material");
			return "redirect:/project/" + pid.get() + "/material";
		}
	}

	/**
	 * 
	 * @param docId
	 * @param response
	 * @param redirectAttributes
	 * @return
	 */
	@RequestMapping(value = { "/{pid}/download/{docId}", "/{pid}/study/{studyId}/download/{docId}" }, method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<String> downloadDocument(@PathVariable long pid, @PathVariable long docId, HttpServletResponse response,
	    RedirectAttributes redirectAttributes) {
		log.trace("Entering downloadDocument [id: {}]", () -> docId);
		UserDTO user = UserUtil.getCurrentUser();
		ResponseEntity<String> resp = new ResponseEntity<String>("{}", HttpStatus.OK);
		if (user == null) {
			log.warn("Auth User Object == null - redirect to login");
			resp = new ResponseEntity<String>("{}", HttpStatus.UNAUTHORIZED);
		} else {
			switch (projectService.prepareMaterialDownload(docId, response)) {
			case MINIO_SAVE_ERROR:
				resp = new ResponseEntity<String>(
				    "{\"error\" : \"" + messageSource.getMessage("minio.connection.exception.upload", null, LocaleContextHolder.getLocale()) + "\"}",
				    HttpStatus.INTERNAL_SERVER_ERROR);
			case DATABASE_ERROR:
				resp = new ResponseEntity<String>(
				    "{\"error\" : \"" + messageSource.getMessage("dbs.sql.exception", null, LocaleContextHolder.getLocale()) + "\"}", HttpStatus.CONFLICT);
			default:
				log.debug("Leaving downloadDocument with result: {}", resp.getStatusCode());
				break;
			}
		}
		return resp;
	}

	/**
	 * 
	 * @param pid
	 *          Project Identifier as {@link Optional}&lt;{@link Long}&gt;
	 * @param docId
	 *          Document Identifier as {@link Long}
	 * @param studyId
	 *          Study Identifier as {@link Optional}&lt;{@link Long}&gt;
	 * @param redirectAttributes
	 *          {@link RedirectAttributes}
	 * @return
	 */
	@RequestMapping(value = { "/{pid}/delDoc/{docId}", "/{pid}/study/{studyId}/delDoc/{docId}" }, method = RequestMethod.GET)
	public String deleteDocument(@PathVariable Optional<Long> pid, @PathVariable long docId, @PathVariable Optional<Long> studyId,
	    RedirectAttributes redirectAttributes) {
		log.trace("Entering deleteDocument [id: {}]", () -> docId);
		UserDTO user = UserUtil.getCurrentUser();
		if (user == null) {
			log.warn("Auth User Object == null - redirect to login");
			return "redirect:/login";
		}
		switch (projectService.deleteMaterialfromMinioAndDB(docId)) {
		case MINIO_SAVE_ERROR:
			redirectAttributes.addFlashAttribute("errorMSG",
			    messageSource.getMessage("material.delete.minio.error", null, LocaleContextHolder.getLocale()));
		case DATABASE_ERROR:
			redirectAttributes.addFlashAttribute("errorMSG", messageSource.getMessage("material.delete.db.error", null, LocaleContextHolder.getLocale()));
		default:
			redirectAttributes.addFlashAttribute("successMSG",
			    messageSource.getMessage("material.delete.successful", null, LocaleContextHolder.getLocale()));
			break;
		}
		redirectAttributes.addFlashAttribute("jQueryMap", "material");
		if (studyId.isPresent())
			return "redirect:/project/" + pid.get() + "/study/" + studyId.get() + "/material";
		return "redirect:/project/" + pid.get() + "/material";
	}

	/**
	 * This function builds the thumb-nail images and put them into the response via projectService.scaleAndSetThumbnail(...). It is called from the
	 * material pages (project and study).
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
	 * This function is called if a user wants to delete a whole project with all of its dependencies. It checks if the user has the rights to delete
	 * the project. If the rights to delete are given projectService.deleteProject(...) is called to delete the project finally from Database and Minio
	 * (material and records).
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
				model.put("errorMSG", messageSource.getMessage("project.study.delete.error", new Object[] { e.getMessage(), e.getErrorCode() },
				    LocaleContextHolder.getLocale()));
				ret = "project";
			} else {
				model.put("subnaviActive", PageState.RECORDMETA.name());
				model.put("recordSubMenu", true);
				model.put("errorMSG", messageSource.getMessage("project.not.deleted.error", new Object[] { e.getMessage(), e.getErrorCode() },
				    LocaleContextHolder.getLocale()));
				ret = "project";
			}
		}
		if (log.isTraceEnabled())
			log.trace("Method deleteProject completed - mapping to {}", ret);
		return ret;
	}

}
