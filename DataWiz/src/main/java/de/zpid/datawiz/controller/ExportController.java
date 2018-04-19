package de.zpid.datawiz.controller;

import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;

import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import de.zpid.datawiz.dto.UserDTO;
import de.zpid.datawiz.enumeration.PageState;
import de.zpid.datawiz.exceptions.DWDownloadException;
import de.zpid.datawiz.exceptions.DataWizSystemException;
import de.zpid.datawiz.form.ExportProjectForm;
import de.zpid.datawiz.service.ExceptionService;
import de.zpid.datawiz.service.ExportService;
import de.zpid.datawiz.service.ProjectService;
import de.zpid.datawiz.util.BreadCrumpUtil;
import de.zpid.datawiz.util.StringUtil;
import de.zpid.datawiz.util.UserUtil;

/**
 * Controller for mapping "/export" <br />
 * <br />
 * This file is part of Datawiz.<br />
 * 
 * <b>Copyright 2017, Leibniz Institute for Psychology Information (ZPID), <a href="http://zpid.de" title="http://zpid.de">http://zpid.de</a>.</b><br />
 * <br />
 * <a rel="license" href= "http://creativecommons.org/licenses/by-nc-sa/4.0/"><img alt="Creative Commons License" style= "border-width:0" src=
 * "https://i.creativecommons.org/l/by-nc-sa/4.0/80x15.png" /></a><br />
 * <span xmlns:dct="http://purl.org/dc/terms/" property= "dct:title">Datawiz</span> by
 * <a xmlns:cc="http://creativecommons.org/ns#" href="zpid.de" property= "cc:attributionName" rel="cc:attributionURL"> Leibniz Institute for Psychology
 * Information (ZPID)</a> is licensed under a <a rel="license" href= "http://creativecommons.org/licenses/by-nc-sa/4.0/">Creative Commons
 * Attribution-NonCommercial-ShareAlike 4.0 International License</a>.
 * 
 * @author Ronny Boelter
 * @version 1.0
 */
@Controller
@RequestMapping(value = "/export")
@SessionAttributes({ "breadcrumpList", "ExportProjectForm" })
public class ExportController {

	private static Logger log = LogManager.getLogger(ExportController.class);

	@Autowired
	private MessageSource messageSource;
	@Autowired
	ExportService exportService;
	@Autowired
	private ExceptionService exceptionService;
	@Autowired
	private ProjectService projectService;
	@Autowired
	private StringUtil stringUtil;

	/**
	 * This function prepares the export data set. Therefore, it loads all studies and records of the project which wants to be exported.
	 * 
	 * @param pidpid
	 *          Project Identifier as {@link Optional}&lt;{@link Long}&gt;
	 * @param model
	 *          {@link ModelMap}
	 * @param redirectAttributes
	 *          {@link RedirectAttributes}
	 * @return Mapping to export.jsp on success, otherwise error mapping via {@link ExceptionService}
	 */
	@RequestMapping(value = { "", "/{pid}" }, method = RequestMethod.GET)
	public String showExportPage(@PathVariable final Optional<Long> pid, final ModelMap model, final RedirectAttributes redirectAttributes) {
		log.trace("Entering showExportPage for project [id: {}]", () -> pid);
		String ret = null;
		final UserDTO user = UserUtil.getCurrentUser();
		ret = projectService.checkUserAccess(pid, null, redirectAttributes, false, user);
		if (ret == null) {
			ExportProjectForm exportForm = null;
			try {
				exportForm = exportService.getExportForm(pid.get(), user);
			} catch (Exception e) {
				ret = exceptionService.setErrorMessagesAndRedirects(pid, null, null, model, redirectAttributes, e, "ExportController->showExportPage");
			}
			model.put("breadcrumpList", BreadCrumpUtil.generateBC(PageState.PROJECT, new String[] { exportForm.getProjectTitle() }, null, messageSource));
			model.put("subnaviActive", PageState.EXPORT.name());
			model.put("ExportProjectForm", exportForm);
			ret = "export";
		}
		log.trace("Leaving showExportPage for project [id: {}]", () -> pid);
		return ret;
	}

	/**
	 * 
	 * @param exportForm
	 * @param pid
	 * @param model
	 * @param response
	 * @param redirectAttributes
	 * @throws Exception
	 */
	@RequestMapping(value = { "", "/{pid}" }, method = RequestMethod.POST, produces = "application/zip")
	public void exportProject(@ModelAttribute("ExportProjectForm") ExportProjectForm exportForm, @PathVariable final Optional<Long> pid, final ModelMap model,
	    HttpServletResponse response, final RedirectAttributes redirectAttributes) throws Exception {
		final UserDTO user = UserUtil.getCurrentUser();
		List<Entry<String, byte[]>> files = null;
		if (exportForm != null && exportForm.getProjectId() > 0) {
			try {
				files = exportService.createExportFileList(exportForm, pid, user);
			} catch (Exception e) {

				files = null;
				if (e instanceof DataWizSystemException) {
					log.warn("Method exportProject->createExportFileList completed with an error [{}] - DWDownloadException thrown: ",
					    () -> ((DataWizSystemException) e).getErrorCode(), () -> e);
					throw (e);
				} else {
					log.error("Exception during EXPORT: ", () -> e);
					throw new DWDownloadException("export.error.exception.thown");
				}
			}
		}
		if (files != null) {
			StringBuilder res = new StringBuilder();
			byte[] export = exportService.exportZip(files, res);
			if (export != null) {
				try {
					response.setContentType("application/zip");
					response.setHeader("Content-Disposition", "attachment; filename=\"" + stringUtil.formatFilename(exportForm.getProjectTitle()) + ".zip\"");
					response.getOutputStream().write(export);
					response.flushBuffer();
				} catch (Exception e) {
					// TODO: handle exception
				}
				model.put("breadcrumpList", BreadCrumpUtil.generateBC(PageState.PROJECT, new String[] { exportForm.getProjectTitle() }, null, messageSource));
				model.put("subnaviActive", PageState.EXPORT.name());
			} else {
				log.warn("Method exportProject->exportZip completed with an error - DWDownloadException thrown: ", res.toString());
				throw new DWDownloadException(res.toString());
			}
		} else {
			throw new DWDownloadException("export.error.exception.thown");
		}
	}

}
