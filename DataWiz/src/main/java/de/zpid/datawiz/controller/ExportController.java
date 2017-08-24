package de.zpid.datawiz.controller;

import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.Optional;

import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import de.zpid.datawiz.dto.UserDTO;
import de.zpid.datawiz.enumeration.DataWizErrorCodes;
import de.zpid.datawiz.enumeration.PageState;
import de.zpid.datawiz.exceptions.DWDownloadException;
import de.zpid.datawiz.exceptions.DataWizSystemException;
import de.zpid.datawiz.form.ExportProjectForm;
import de.zpid.datawiz.service.ExceptionService;
import de.zpid.datawiz.service.ExportService;
import de.zpid.datawiz.util.BreadCrumpUtil;
import de.zpid.datawiz.util.UserUtil;

@Controller
@RequestMapping(value = "/export")
@SessionAttributes({ "breadcrumpList", "ExportProjectForm" })
public class ExportController {

	private static Logger log = LogManager.getLogger(ExportController.class);

	@Autowired
	protected MessageSource messageSource;
	@Autowired
	ExportService exportService;
	@Autowired
	private ExceptionService exceptionService;

	@RequestMapping(value = { "", "/{pid}" }, method = RequestMethod.GET)
	public String showExportPage(@PathVariable final Optional<Long> pid, final ModelMap model, final RedirectAttributes reAtt,
	    final RedirectAttributes redirectAttributes) {
		log.trace("Entering showExportPage for project [id: {}]", () -> pid.isPresent() ? pid.get() : "null");
		if (!pid.isPresent()) {
			reAtt.addFlashAttribute("errorMSG", messageSource.getMessage("roles.error.empty.form", null, LocaleContextHolder.getLocale()));
			return "redirect:/panel";
		}
		final UserDTO user = UserUtil.getCurrentUser();
		if (user == null) {
			log.warn("Auth User Object == null - redirect to login");
			return "redirect:/login";
		}
		ExportProjectForm exportForm = null;
		try {
			if (pid.isPresent())
				exportForm = exportService.getExportForm(pid.get(), user);
			else
				throw new DataWizSystemException(messageSource.getMessage("logging.pid.not.present", null, Locale.ENGLISH),
				    DataWizErrorCodes.MISSING_PID_ERROR);
		} catch (Exception e) {
			exceptionService.setErrorMessagesAndRedirects(pid, null, null, model, redirectAttributes, e, "ExportController->showExportPage");
		}
		model.put("breadcrumpList", BreadCrumpUtil.generateBC(PageState.PROJECT, new String[] { exportForm.getProjectTitle() }, null, messageSource));
		model.put("subnaviActive", PageState.EXPORT.name());
		model.put("ExportProjectForm", exportForm);
		log.trace("Method showExportPage successfully completed");
		return "export";
	}

	@RequestMapping(value = { "", "/{pid}" }, method = RequestMethod.POST, produces = "application/zip")
	public void exportProject(@ModelAttribute("ExportProjectForm") ExportProjectForm exportForm, @PathVariable final Optional<Long> pid,
	    final ModelMap model, final RedirectAttributes reAtt, HttpServletResponse response) throws Exception {

		final UserDTO user = UserUtil.getCurrentUser();

		List<Entry<String, byte[]>> files = null;

		// TODO CHECK RIGHTS
		if (exportForm != null && exportForm.getProjectId() > 0) {
			try {
				files = exportService.createExportFileList(exportForm, pid, user);
			} catch (Exception e) {
				log.warn("Method exportProject->createExportFileList completed with an error - DWDownloadException thrown: ", () -> e);
				files = null;
			}
		}
		if (files != null) {
			StringBuilder res = new StringBuilder();
			byte[] export = exportService.exportZip(files, res);
			if (export != null) {
				response.setContentType("application/zip");
				response.setHeader("Content-Disposition", "attachment; filename=\"" + exportService.formatFilename(exportForm.getProjectTitle()) + ".zip\"");
				response.getOutputStream().write(export);
				response.flushBuffer();
				model.put("breadcrumpList", BreadCrumpUtil.generateBC(PageState.PROJECT, new String[] { exportForm.getProjectTitle() }, null, messageSource));
				model.put("subnaviActive", PageState.EXPORT.name());
			} else {
				log.warn("Method exportProject->exportZip completed with an error - DWDownloadException thrown: ", res.toString());
				throw new DWDownloadException(res.toString());
			}
		} else {
			// TODO
			throw new DWDownloadException("logging.selected.file.empty");
		}

	}

}
