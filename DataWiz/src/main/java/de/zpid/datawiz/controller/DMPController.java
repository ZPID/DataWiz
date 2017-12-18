package de.zpid.datawiz.controller;

import java.util.Locale;
import java.util.Optional;

import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ClassPathXmlApplicationContext;
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
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import de.zpid.datawiz.dto.DmpDTO;
import de.zpid.datawiz.dto.ProjectDTO;
import de.zpid.datawiz.dto.UserDTO;
import de.zpid.datawiz.enumeration.DataWizErrorCodes;
import de.zpid.datawiz.enumeration.DmpCategory;
import de.zpid.datawiz.enumeration.PageState;
import de.zpid.datawiz.enumeration.Roles;
import de.zpid.datawiz.exceptions.DWDownloadException;
import de.zpid.datawiz.exceptions.DataWizSystemException;
import de.zpid.datawiz.form.ProjectForm;
import de.zpid.datawiz.service.DMPService;
import de.zpid.datawiz.service.ExceptionService;
import de.zpid.datawiz.service.ProjectService;
import de.zpid.datawiz.util.BreadCrumpUtil;
import de.zpid.datawiz.util.UserUtil;

/**
 * Controller for mapping "/dmp" <br />
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
 */
@Controller
@RequestMapping(value = "/dmp")
@SessionAttributes({ "ProjectForm", "subnaviActive" })
public class DMPController {

	private static Logger log = LogManager.getLogger(DMPController.class);

	@Autowired
	private MessageSource messageSource;
	@Autowired
	private SmartValidator validator;
	@Autowired
	private ProjectService projectService;
	@Autowired
	private ExceptionService exceptionService;
	@Autowired
	private DMPService dmpService;
	@Autowired
	private ClassPathXmlApplicationContext applicationContext;

	public DMPController() {
		super();
		if (log.isEnabled(Level.INFO))
			log.info("Loading DMPController for mapping /dmp");
	}

	/**
	 * Creates the project form.
	 *
	 * @return {@link ProjectForm}
	 */
	@ModelAttribute("ProjectForm")
	private ProjectForm createProjectForm() {
		return (ProjectForm) applicationContext.getBean("ProjectForm");
	}

	/**
	 * This Function is called if an DMP is created without an existing ProjectID. (unused)
	 * 
	 * 
	 * @param model
	 * @return Mapping to dmp.jsp
	 */
	@RequestMapping(method = RequestMethod.GET)
	public String createDMP(ModelMap model) {
		log.trace("Entering createDMP without PID");
		model.put("subnaviActive", "DMP");
		ProjectForm pForm;
		try {
			pForm = projectService.createProjectForm();
		} catch (Exception e) {
			log.fatal("ERROR: Database error during projectService.createProjectForm - Exception:", () -> e);
			model.put("errorMSG", messageSource.getMessage("dbs.sql.exception", null, LocaleContextHolder.getLocale()));
			return "redirect:/panel";
		}
		model.put("breadcrumpList", BreadCrumpUtil.generateBC(PageState.PROJECT,
		    new String[] { messageSource.getMessage("breadcrumb.new.project", null, LocaleContextHolder.getLocale()) }, null, messageSource));
		model.put("subnaviActive", PageState.DMP.name());
		model.put("ProjectForm", pForm);
		log.trace("Method createDMP successfully completed");
		return "dmp";
	}

	/**
	 * 
	 * This function loads the DMP data for editing. Therefore, the pid is required. If no DMP is found with the given pid, or other errors occur, the
	 * exceptionService is called to handle the exceptions and redirect to the correct jsp.
	 * 
	 * @param pid
	 *          ProjectID
	 * @param pForm
	 *          ProjectForm
	 * @param model
	 *          ModelMap
	 * @param redirectAttributes
	 *          RedirectAttributes
	 * @return Mapping to dmp.jsp
	 */
	@RequestMapping(value = "/{pid}", method = RequestMethod.GET)
	public String editDMP(@PathVariable Optional<Long> pid, @ModelAttribute("ProjectForm") ProjectForm pForm, ModelMap model,
	    RedirectAttributes redirectAttributes) {
		log.trace("Entering editDMP for DMP [pid: {}]", () -> pid);
		UserDTO user = UserUtil.getCurrentUser();
		if (user == null) {
			log.warn("Auth User Object == null - redirect to login");
			return "redirect:/login";
		}
		String pName = "";
		try {
			projectService.getProjectForm(pForm, pid.get(), user, PageState.DMP, projectService.checkProjectRoles(user, pid.get(), 0, false, false));
			if (pForm != null && pForm.getProject() != null && pForm.getProject().getTitle() != null && !pForm.getProject().getTitle().trim().isEmpty()) {
				pName = pForm.getProject().getTitle();
			}
		} catch (Exception e) {
			return exceptionService.setErrorMessagesAndRedirects(pid, null, null, model, redirectAttributes, e, "dmpController.editDMP");
		}
		model.put("breadcrumpList", BreadCrumpUtil.generateBC(PageState.PROJECT, new String[] { pName }, null, messageSource));
		model.put("subnaviActive", PageState.DMP.name());
		model.put("ProjectForm", pForm);
		log.trace("Leaving editDMP for DMP [pid: {}]", () -> pid);
		return "dmp";
	}

	/**
	 * This function is called from the dmp jsp to save the DMP form. DMP form is split in several parts, they are checked separately for changes, and
	 * only saved if changes occurred. This should minimize the traffic to the database and the writing operations. If an error will occur during the
	 * saving and checking process, this error will be send to the view and displayed. If the user tries to submit an unchanged DMP, nothing will be
	 * saved, and the user gets an info message.
	 * 
	 * @param pForm
	 *          ProjectForm
	 * @param model
	 *          ModelMap
	 * @param redirectAttributes
	 *          RedirectAttributes
	 * @param bRes
	 *          BindingResult
	 * @param pid
	 *          ProjectID
	 * @return mapping to the dmp.jsp if an error occurred, or if the form is unchanged <br>
	 *         redirect mapping to the GET function if saving was successful
	 */
	@RequestMapping(value = { "", "/{pid}" }, method = RequestMethod.POST)
	public String saveDMP(@ModelAttribute("ProjectForm") ProjectForm pForm, ModelMap model, RedirectAttributes redirectAttributes, BindingResult bRes,
	    @PathVariable final Optional<Long> pid) {
		log.trace("Entering saveDMP for DMP [pid: {}]", () -> pid);
		UserDTO user = UserUtil.getCurrentUser();
		boolean hasErrors = false;
		if (!pid.isPresent() || projectService.checkProjectRoles(user, pid.get(), 0, true, false) == null) {
			bRes.reject("globalErrors", messageSource.getMessage("project.save.globalerror.not.successful", null, LocaleContextHolder.getLocale()));
			hasErrors = true;
		}
		Boolean unChanged = true;
		validator.validate(pForm, bRes, ProjectDTO.ProjectVal.class);
		if (bRes.hasErrors() || pForm.getProject() == null || pForm.getProject().getTitle().isEmpty()) {
			log.debug("bindingResult has Errors = ProjectName not given!");
			hasErrors = true;
		}
		hasErrors = dmpService.saveNewDMPSetID(pForm, hasErrors);
		if (!hasErrors) {
			if (pForm.getDmp().isAdminChanged()) {
				hasErrors = dmpService.saveDMPDataPart(pForm, bRes, DmpCategory.ADMIN, DmpDTO.AdminVal.class);
				unChanged = false;
			}
			if (pForm.getDmp().isResearchChanged()) {
				hasErrors = (dmpService.saveDMPDataPart(pForm, bRes, DmpCategory.RESEARCH, DmpDTO.ResearchVal.class) || hasErrors) ? true : false;
				unChanged = false;
			}
			if (pForm.getDmp().isMetaChanged()) {
				hasErrors = (dmpService.saveDMPDataPart(pForm, bRes, DmpCategory.META, DmpDTO.MetaVal.class) || hasErrors) ? true : false;
				unChanged = false;
			}
			if (pForm.getDmp().isSharingChanged()) {
				hasErrors = (dmpService.saveDMPDataPart(pForm, bRes, DmpCategory.SHARING, DmpDTO.SharingVal.class) || hasErrors) ? true : false;
				unChanged = false;
			}
			if (pForm.getDmp().isStorageChanged()) {
				hasErrors = (dmpService.saveDMPDataPart(pForm, bRes, DmpCategory.STORAGE, DmpDTO.StorageVal.class) || hasErrors) ? true : false;
				unChanged = false;
			}
			if (pForm.getDmp().isOrganizationChanged()) {
				hasErrors = (dmpService.saveDMPDataPart(pForm, bRes, DmpCategory.ORGANIZATION, DmpDTO.OrganizationVal.class) || hasErrors) ? true : false;
				unChanged = false;
			}
			if (pForm.getDmp().isEthicalChanged()) {
				hasErrors = (dmpService.saveDMPDataPart(pForm, bRes, DmpCategory.ETHICAL, DmpDTO.EthicalVal.class) || hasErrors) ? true : false;
				unChanged = false;
			}
			if (pForm.getDmp().isCostsChanged()) {
				hasErrors = (dmpService.saveDMPDataPart(pForm, bRes, DmpCategory.COSTS, DmpDTO.CostsVal.class) || hasErrors) ? true : false;
				unChanged = false;
			}
		}
		String ret = "redirect:/dmp/" + pForm.getDmp().getId();
		if (hasErrors) {
			model.put("errorMSG", messageSource.getMessage("dmp.save.error.msg", null, LocaleContextHolder.getLocale()));
			ret = "dmp";
		} else if (unChanged) {
			model.put("infoMSG", messageSource.getMessage("dmp.save.no.changes.msg", null, LocaleContextHolder.getLocale()));
			ret = "dmp";
		} else
			redirectAttributes.addFlashAttribute("successMSG", messageSource.getMessage("dmp.save.success.msg", null, LocaleContextHolder.getLocale()));
		log.trace("Leaving saveDMP for DMP [pid: {}] with result: [error: {}; unchanged: {}; mapping: {}]", pid, hasErrors, unChanged, ret);
		return ret;

	}

	/**
	 * This function is called if a user wants to download a data-management-plan export file (ODF). If no error occurs during creation, the desired
	 * file will be made available for download. If an error occurs, an exception is thrown and displayed to the user.
	 * 
	 * @param pid
	 *          ProjectID
	 * @param type
	 *          Optional<String> Export type to identify the ODF file that the user wants to download.
	 * @param redirectAttributes
	 *          RedirectAttributes
	 * @param response
	 *          HttpServletResponse
	 * @throws DWDownloadException
	 *           The link to this function is called with the "_blank" HTML parameter, therefore it is useful to throw Exceptions. DWDownload-Exception
	 *           are captured and processed by the ExceptionHandlerController.
	 */
	@RequestMapping(value = { "/{pid}/exportDMP/{type}" }, method = RequestMethod.GET)
	public void exportDMPODF(@PathVariable final Optional<Long> pid, @PathVariable final Optional<String> type,
	    final RedirectAttributes redirectAttributes, final HttpServletResponse response) throws DWDownloadException {
		log.trace("Entering exportDMPODF for DMP [pid: {}] and exportType [{}]", () -> pid, () -> type);
		UserDTO user = UserUtil.getCurrentUser();
		if (user == null || (!user.hasRole(Roles.PROJECT_READER, pid, false) && !user.hasRole(Roles.PROJECT_ADMIN, pid, false)
		    && !user.hasRole(Roles.PROJECT_WRITER, pid, false) && !user.hasRole(Roles.ADMIN))) {
			log.warn("Auth User Object empty or User is permitted to download this file");
			throw new DWDownloadException("export.access.denied");
		}
		byte[] content = null;
		try {
			// TODO? locale is set to German - should be changed if the form inputs are available in English
			content = dmpService.createDMPExport(pid, type, Locale.GERMAN);
		} catch (Exception e) {
			log.warn("Exception during dmpService.createDMPExport Message: ", () -> e);
			if (e instanceof DataWizSystemException) {
				if (((DataWizSystemException) e).getErrorCode().equals(DataWizErrorCodes.NO_DATA_ERROR))
					throw new DWDownloadException("export.odt.error.dmp");
				else
					throw new DWDownloadException("export.odt.error.project");
			} else {
				throw new DWDownloadException("dbs.sql.exception");
			}
		}
		try {
			response.setContentType("application/vnd.oasis.opendocument.text");
			response.setHeader("Content-Disposition", "attachment; filename=\"testodf.odt\"");
			response.setContentLength(content.length);
			response.getOutputStream().write(content);
			response.flushBuffer();
			log.trace("Leaving exportDMPODF for DMP [pid: {}] and exportType [{}]", () -> pid, () -> type);
		} catch (Exception e) {
			log.warn("Exception during creating response ", () -> e);
			throw new DWDownloadException("export.error.response");
		}

	}

	/**
	 * To check the Internet connection, this function is asynchronously called before saving the DMP Data.
	 * 
	 * @return
	 */
	@RequestMapping(value = { "/checkConnection" })
	public ResponseEntity<Object> checkConnection() {
		log.trace("checkConnection");
		return new ResponseEntity<Object>(HttpStatus.OK);
	}
}
