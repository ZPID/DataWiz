package de.zpid.datawiz.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
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
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import de.zpid.datawiz.dto.DataTableDTO;
import de.zpid.datawiz.dto.RecordDTO;
import de.zpid.datawiz.dto.UserDTO;
import de.zpid.datawiz.enumeration.DataWizErrorCodes;
import de.zpid.datawiz.enumeration.PageState;
import de.zpid.datawiz.exceptions.DWDownloadException;
import de.zpid.datawiz.exceptions.DataWizSystemException;
import de.zpid.datawiz.form.StudyForm;
import de.zpid.datawiz.service.ExceptionService;
import de.zpid.datawiz.service.ExportService;
import de.zpid.datawiz.service.ImportService;
import de.zpid.datawiz.service.ProjectService;
import de.zpid.datawiz.service.RecordService;
import de.zpid.datawiz.util.BreadCrumbUtil;
import de.zpid.datawiz.util.ObjectCloner;
import de.zpid.datawiz.util.StringUtil;
import de.zpid.datawiz.util.UserUtil;
import de.zpid.spss.SPSSIO;
import de.zpid.spss.dto.SPSSValueLabelDTO;
import de.zpid.spss.dto.SPSSVarDTO;
import de.zpid.spss.util.SPSSMissing;
import de.zpid.spss.util.SPSSVarTypes;

/**
 * Controller for mapping "/project/{pid}/study/{studyId}/record" <br />
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
 */
@Controller
@RequestMapping(value = { "/project/{pid}/study/{studyId}/record" })
@SessionAttributes({ "StudyForm", "subnaviActive", "breadcrumpList" })
public class RecordController {

	private static Logger log = LogManager.getLogger(RecordController.class);

	@Autowired
	private RecordService recordService;
	@Autowired
	private ExportService exportService;
	@Autowired
	private ImportService importService;
	@Autowired
	private MessageSource messageSource;
	@Autowired
	private Environment env;
	@Autowired
	private ClassPathXmlApplicationContext applicationContext;
	@Autowired
	private ExceptionService exceptionService;
	@Autowired
	private ProjectService projectService;
	@Autowired
	private SPSSIO spss;
	@Autowired
	private StringUtil stringUtil;

	/**
	 * Instantiates a new record controller.
	 */
	public RecordController() {
		super();
		log.info("Loading RecordController for mapping /project/{pid}/study/{sid}/record");
	}

	@ModelAttribute("StudyForm")
	private StudyForm createProjectForm() {
		return (StudyForm) applicationContext.getBean("StudyForm");
	}

	/**
	 * This function is called if a user selects the record meta-data, codebook, or matrix. The mapping depends on the passed "subpage" string.
	 * 
	 * @param pid
	 *          Project Identifier as {@link Optional}&lt;{@link Long}&gt;
	 * @param studyId
	 *          Study Identifier as {@link Optional}&lt;{@link Long}&gt;
	 * @param recordId
	 *          Record Identifier as {@link Optional}&lt;{@link Long}&gt;
	 * @param versionId
	 *          Version Identifier as {@link Optional}&lt;{@link Long}&gt;
	 * @param redirectAttributes
	 *          {@link RedirectAttributes}
	 * @param model
	 *          {@link ModelMap}
	 * @param subpage
	 *          Mapping variable as {@link String}: "codebook" for mapping to codebook.jsp, "data" for mapping to datamatrix.jsp, or empty for mapping to
	 *          record.jsp
	 * @return Mapping to recordjsp, codebook.jsp, or datamatrix.jsp (depends on subpage) on success, otherwise exception handling via
	 *         exceptionService.setErrorMessagesAndRedirects(...)
	 */
	@RequestMapping(value = { "", "/{recordId}", "/{recordId}/{subpage}", "/{recordId}/version/{versionId}",
	    "/{recordId}/version/{versionId}/{subpage}" }, method = RequestMethod.GET)
	public String showRecord(@PathVariable final Optional<Long> pid, @PathVariable final Optional<Long> studyId, @PathVariable final Optional<Long> recordId,
	    @PathVariable final Optional<Long> versionId, final RedirectAttributes redirectAttributes, final ModelMap model,
	    @PathVariable final Optional<String> subpage) {
		final UserDTO user = UserUtil.getCurrentUser();
		String ret;
		if (recordId.isPresent()) {
			log.trace("Entering showRecord(edit) for [recordId: {}; studyId {}; projectId {}]", () -> recordId.get(), () -> studyId.get(), () -> pid.get());
			ret = projectService.checkUserAccess(pid, studyId, redirectAttributes, false, user);
		} else {
			log.trace("Entering showRecord(create) ");
			ret = projectService.checkUserAccess(pid, studyId, redirectAttributes, true, user);
		}
		StudyForm sForm = null;
		List<String> parsingErrors = new ArrayList<String>();
		if (ret == null) {
			try {
				sForm = recordService.setStudyform(pid, studyId, recordId, versionId, subpage, parsingErrors);
			} catch (Exception e) {
				ret = exceptionService.setErrorMessagesAndRedirects(pid, studyId, recordId, model, redirectAttributes, e, "recordService.setStudyform");
			}
		}
		if (sForm != null) {
			model.put("StudyForm", sForm);
			model.put("recordSubMenu", true);
			model.put("breadcrumpList",
			    BreadCrumbUtil.generateBC(PageState.RECORDS,
			        new String[] { sForm.getProject().getTitle(), sForm.getStudy().getTitle(),
			            (sForm.getRecord() != null ? sForm.getRecord().getRecordName()
			                : messageSource.getMessage("record.new.record.breadcrump", null, LocaleContextHolder.getLocale())) },
			        new long[] { pid.get(), studyId.get() }, messageSource));
			if (sForm.getPageLoadMin() == 0) {
				sForm.setPageLoadMin(1);
			}
			if (subpage.isPresent() && subpage.get().equals("codebook")) {
				model.put("subnaviActive", PageState.RECORDVAR.name());
				model.put("errorCodeBookMSG", recordService.validateCodeBook(sForm));
				if (sForm.getPageLoadMax() == 0) {
					sForm.setPageLoadMax(sForm.getRecord().getNumberOfVariables() < 5 ? sForm.getRecord().getNumberOfVariables() : 5);
				}
				ret = "codebook";
				model.put("warnCodeBookMSG", recordService.setMessageString(sForm.getWarnings()));
			} else if (subpage.isPresent() && subpage.get().equals("data")) {
				model.put("subnaviActive", PageState.RECORDDATA.name());
				if (sForm.getPageLoadMax() == 0) {
					sForm.setPageLoadMax(sForm.getRecord().getNumberOfCases() < 100 ? (int) sForm.getRecord().getNumberOfCases() : 100);
				}
				ret = "datamatrix";
			} else {
				model.put("isSPSSLibLoaded", spss.isLibLoaded());
				String errormsg = recordService.validateCodeBook(sForm);
				if (errormsg != null && !errormsg.trim().isEmpty()) {
					model.put("errorMSG", messageSource.getMessage("record.spss.export.disabled", null, LocaleContextHolder.getLocale()));
					model.put("disableSPSSExport", true);
				} else {
					model.put("disableSPSSExport", false);
				}
				model.put("subnaviActive", PageState.RECORDMETA.name());
				ret = "record";
			}
		}
		if (log.isTraceEnabled())
			log.trace("Method showRecord completed - mapping to {}", ret);
		return ret;
	}

	/**
	 * This function is called after a user submits the upload form from the report.jsp. Before starting the upload process, it checks if the change-log is set.
	 * If it is missing it redirects to record.jsp with missing change-log message.
	 * 
	 * @param pid
	 *          Project Identifier as {@link Optional}&lt;{@link Long}&gt;
	 * @param studyId
	 *          Study Identifier as {@link Optional}&lt;{@link Long}&gt;
	 * @param recordId
	 *          Record Identifier as {@link Optional}&lt;{@link Long}&gt;
	 * @param sForm
	 *          {@link StudyForm}
	 * @param redirectAttributes
	 *          {@link RedirectAttributes}
	 * @param model
	 *          {@link ModelMap}
	 * @return Mapping to redirect:/importReport on success, otherwise error and mapping handling via exceptionService.setErrorMessagesAndRedirects(...)
	 */
	@RequestMapping(value = { "/{recordId}" }, method = RequestMethod.POST, params = { "upload" })
	public String uploadFile(@PathVariable final Optional<Long> pid, @PathVariable final Optional<Long> studyId, @PathVariable final Optional<Long> recordId,
	    @ModelAttribute("StudyForm") StudyForm sForm, final RedirectAttributes redirectAttributes, final ModelMap model) {
		final UserDTO user = UserUtil.getCurrentUser();
		log.trace("Entering  uploadFile for [recordId: {}; studyId {}; projectId {}] user[id: {}; email: {}]", () -> recordId.get(), () -> studyId.get(),
		    () -> pid.get(), () -> user.getId(), () -> user.getEmail());
		String ret = projectService.checkUserAccess(pid, studyId, redirectAttributes, true, user);
		if (ret == null) {
			if (sForm.getNewChangeLog() == null || sForm.getNewChangeLog().isEmpty()) {
				if (log.isDebugEnabled())
					log.debug("New Changelog is Missing - return to jsp with message");
				redirectAttributes.addFlashAttribute("errorMSG", messageSource.getMessage("record.no.changelog", null, LocaleContextHolder.getLocale()));
				ret = "redirect:/project/" + pid.get() + "/study/" + studyId.get() + "/record/" + recordId.get();
			}
		}
		if (ret == null) {
			try {
				importService.importFile(pid, studyId, recordId, sForm, user);
				ret = "redirect:/project/" + pid.get() + "/study/" + studyId.get() + "/record/" + recordId.get() + "/importReport";
			} catch (DataWizSystemException e) {
				ret = exceptionService.setErrorMessagesAndRedirects(pid, studyId, recordId, model, redirectAttributes, e, "importService.importFile");
			}
		}
		if (log.isTraceEnabled())
			log.trace("Method uploadFile completed - mapping to {}", ret);
		return ret;
	}

	/**
	 * This function is called if a user wants to delete a record. It checks if the user has the rights to delete the record. If the rights to delete are given
	 * recordService.deleteRecord(...) is called to delete the record finally from Database and Minio.
	 * 
	 * @param pid
	 *          Project Identifier as {@link Optional}&lt;{@link Long}&gt;
	 * @param studyId
	 *          Study Identifier as {@link Optional}&lt;{@link Long}&gt;
	 * @param recordId
	 *          Record Identifier as {@link Optional}&lt;{@link Long}&gt;
	 * @param sForm
	 *          {@link StudyForm}
	 * @param redirectAttributes
	 *          {@link RedirectAttributes}
	 * @param model
	 *          {@link ModelMap}
	 * @return Mapping to redirect/records on success, otherwise to record.jsp or error.jsp (Database Errors) with error messages
	 */
	@RequestMapping(value = { "/{recordId}/deleteRecord" }, method = RequestMethod.GET)
	public String deleteRecord(@PathVariable final Optional<Long> pid, @PathVariable final Optional<Long> studyId, @PathVariable final Optional<Long> recordId,
	    @ModelAttribute("StudyForm") StudyForm sForm, final RedirectAttributes redirectAttributes, final ModelMap model) {
		final UserDTO user = UserUtil.getCurrentUser();
		log.trace("Entering  deleteRecord for [recordId: {}; studyId {}; projectId {}] user[id: {}; email: {}]", () -> recordId.get(), () -> studyId.get(),
		    () -> pid.get(), () -> user.getId(), () -> user.getEmail());
		String ret = "redirect:/project/" + pid.get() + "/study/" + studyId.get() + "/records";
		try {
			recordService.deleteRecord(pid, studyId, recordId, user, true);
		} catch (DataWizSystemException e) {
			if (e.getErrorCode().equals(DataWizErrorCodes.DATABASE_ERROR)) {
				model.put("errormsg",
				    messageSource.getMessage("dbs.sql.exception",
				        new Object[] { env.getRequiredProperty("organisation.admin.email"), e.getMessage().replaceAll("\n", "").replaceAll("\"", "\'") },
				        LocaleContextHolder.getLocale()));
				ret = "error";
			} else {
				model.put("subnaviActive", PageState.RECORDMETA.name());
				model.put("recordSubMenu", true);
				model.put("errorMSG", messageSource.getMessage("record.not.deleted.error", null, LocaleContextHolder.getLocale()));
				ret = "record";
			}
		}
		if (log.isTraceEnabled())
			log.trace("Method deleteRecord completed - mapping to {}", ret);
		return ret;
	}

	/**
	 * This function is redirect called from uploadFile(...). It validates the imported Record and compares it to the last saved version.
	 * 
	 * @param pid
	 *          Project Identifier as {@link Optional}&lt;{@link Long}&gt;
	 * @param studyId
	 *          Study Identifier as {@link Optional}&lt;{@link Long}&gt;
	 * @param recordId
	 *          Record Identifier as {@link Optional}&lt;{@link Long}&gt;
	 * @param sForm
	 *          {@link StudyForm}
	 * @param redirectAttributes
	 *          {@link RedirectAttributes}
	 * @return Mapping to "importReport.jsp" on success, otherwise redirect to /record with error messages.
	 */
	@RequestMapping(value = { "/{recordId}/importReport" }, method = RequestMethod.GET)
	public String showImportReport(@PathVariable final Optional<Long> pid, @PathVariable final Optional<Long> studyId,
	    @PathVariable final Optional<Long> recordId, @ModelAttribute("StudyForm") StudyForm sForm, final RedirectAttributes redirectAttributes) {
		final UserDTO user = UserUtil.getCurrentUser();
		log.trace("Entering  showImportReport for [recordId: {}; studyId {}; projectId {}] user[id: {}; email: {}]", () -> recordId.get(), () -> studyId.get(),
		    () -> pid.get(), () -> user.getId(), () -> user.getEmail());
		String ret = projectService.checkUserAccess(pid, studyId, redirectAttributes, true, user);
		if (sForm == null || sForm.getRecord() == null) {
			log.debug("Record Object is empty - Session timeout");
			redirectAttributes.addFlashAttribute("errorMSG",
			    "Ihre Anfrage an den Server enthielt keine Daten, wahrscheinlich waren Sie zu lange inaktiv und die Session ist abgelaufen.");
			ret = "redirect:/project/" + pid.get() + "/study/" + studyId.get() + "/record/" + recordId.get();
		}
		if (ret == null) {
			try {
				importService.loadImportReport(recordId, sForm);
				ret = "importRep";
			} catch (Exception e) {
				log.error("Exception during recordService.setStudyform Message: ", () -> e);
				if (e instanceof IOException || e instanceof ClassNotFoundException) {
					redirectAttributes.addFlashAttribute("errorMSG", messageSource.getMessage("global.error.internal",
					    new Object[] { env.getRequiredProperty("organisation.admin.email") }, LocaleContextHolder.getLocale()));
				} else {
					redirectAttributes.addFlashAttribute("errorMSG", messageSource.getMessage("dbs.sql.exception",
					    new Object[] { env.getRequiredProperty("organisation.admin.email") }, LocaleContextHolder.getLocale()));
				}
				ret = "redirect:/project/" + pid.get() + "/study/" + studyId.get() + "/record/" + recordId.get();
			}
		}
		if (log.isTraceEnabled())
			log.trace("Method showImportReport completed - mapping to {}", ret);
		return ret;
	}

	/**
	 * This function is used by DataTables.js to load the DataMatrix asynchronously to improve the page load speed.
	 * 
	 * @param sForm
	 *          {@link StudyForm}
	 * @return
	 */
	@RequestMapping(value = { "/{recordId}/getMatrixAsync/{state}", "/{recordId}/version/{versionId}/getMatrixAsync/{state}" }, method = RequestMethod.POST)
	public @ResponseBody String getMatrixAsync(@ModelAttribute("StudyForm") StudyForm sForm, HttpServletRequest request, @PathVariable final String state) {
		String search = request.getParameter("search[value]");
		String start = request.getParameter("start");
		String length = request.getParameter("length");
		String draw = request.getParameter("draw");
		log.trace("Entering getMatrixAsync for importReport with Parameter [state: {}; draw: {}; start: {}; length: {}; search: {}]", () -> state, () -> draw,
		    () -> start, () -> length, () -> search);
		StringBuilder err = new StringBuilder();
		DataTableDTO datatable = new DataTableDTO();
		int startI = 0, lengthI = 0;
		try {
			if (draw != null && !draw.isEmpty())
				datatable.setDraw(Integer.parseInt(draw));
			else {
				err.append(
				    "\n " + messageSource.getMessage("record.matrix.async.error", new Object[] { "Parameter 'draw' is not set!" }, LocaleContextHolder.getLocale()));
			}
			if (start != null && !start.isEmpty())
				startI = Integer.parseInt(start);
			else {
				err.append(
				    "\n " + messageSource.getMessage("record.matrix.async.error", new Object[] { "Parameter 'start' is not set!" }, LocaleContextHolder.getLocale()));
			}
			if (length != null && !length.isEmpty())
				lengthI = Integer.parseInt(length);
			else {
				err.append("\n "
				    + messageSource.getMessage("record.matrix.async.error", new Object[] { "Parameter 'length' is not given!" }, LocaleContextHolder.getLocale()));
			}
		} catch (Exception e) {
			log.warn("Error during parsing String paramater" + e);
			err.append(
			    "\n " + messageSource.getMessage("record.matrix.async.error", new Object[] { "Parsing Error: " + e.getMessage() }, LocaleContextHolder.getLocale()));
		}
		if (err.length() == 0) {
			List<List<Object>> list = null;
			if (state.equals("import") && sForm.getImportMatrix() != null) {
				List<List<Object>> list_t = new ArrayList<>();
				sForm.getImportMatrix().forEach(row -> {
					list_t.add(new ArrayList<Object>(Arrays.asList(row)));
				});
				list = list_t;
			} else if (state.equals("final")) {
				if (sForm != null && sForm.getRecord() != null && sForm.getRecord().getDataMatrix() != null && !sForm.getRecord().getDataMatrix().isEmpty())
					list = sForm.getRecord().getDataMatrix();
			}
			if (list != null) {
				datatable.setRecordsTotal(list.size());
				if (search != null && !search.isEmpty()) {
					List<List<Object>> searchList = new ArrayList<>();
					list.forEach(row -> {
						AtomicBoolean found = new AtomicBoolean(false);
						row.parallelStream().forEach(itm -> {
							if (!found.get() && itm != null && String.valueOf(itm).trim().contains(search))
								found.set(true);
						});
						if (found.get())
							searchList.add(row);
					});
					datatable.setRecordsFiltered(searchList.size());
					datatable.setData(searchList.subList(startI, (((startI + lengthI) >= searchList.size()) ? searchList.size() : (startI + lengthI))));
				} else {
					datatable.setRecordsFiltered(list.size());
					datatable.setData(list.subList(startI, (((startI + lengthI) >= list.size()) ? list.size() : (startI + lengthI))));
				}
			}
		}
		datatable.setError(err.toString());
		log.trace("Leaving getMatrixAsync for importReport with Parameter [draw: {}; start: {}; length: {}; search: {}] with result [listSize: {}]", () -> draw,
		    () -> start, () -> length, () -> search, () -> (datatable.getData() == null ? "null" : datatable.getData().size()));
		return new Gson().toJson(datatable);
	}

	/*
	 * @RequestMapping(value = { "/{recordId}/getLabelAsync", "/{recordId}/version/{versionId}/getLabelAsync" }, method = RequestMethod.GET) public @ResponseBody
	 * String getLabelAsync(@ModelAttribute("StudyForm") StudyForm sForm) { log.trace("Entering getLabelAsync"); List<DataTableColumnDTO> label = new
	 * ArrayList<>(); sForm.getRecord().getVariables().forEach(var -> { DataTableColumnDTO dtc = new DataTableColumnDTO(); dtc.setId(var.getName());
	 * dtc.setTitle(var.getName()); label.add(dtc); }); return new Gson().toJson(label); }
	 */

	/**
	 * This function saves the import if the user selects "save import" on importReport.jsp. Therefore, recordService.sortVariablesAndSetMetaData(sForm) is called
	 * to set the selected meta data (keep or dismiss saved meta data) and after that the record is saved to DB and Minio via
	 * recordService.saveRecordToDBAndMinio(sForm).
	 * 
	 * @param pid
	 *          Project Identifier as {@link Optional}&lt;{@link Long}&gt;
	 * @param studyId
	 *          Study Identifier as {@link Optional}&lt;{@link Long}&gt;
	 * @param recordId
	 *          Record Identifier as {@link Optional}&lt;{@link Long}&gt;
	 * @param sForm
	 *          {@link StudyForm}
	 * @param redirectAttributes
	 *          {@link RedirectAttributes}
	 * @return Redirect mapping to /record on success and on error, but with error messages if errors occurred.
	 */
	@RequestMapping(value = { "/{recordId}" }, method = RequestMethod.POST)
	public String saveImport(@PathVariable final Optional<Long> pid, @PathVariable final Optional<Long> studyId, @PathVariable final Optional<Long> recordId,
	    @ModelAttribute("StudyForm") StudyForm sForm, final RedirectAttributes redirectAttributes) {
		final UserDTO user = UserUtil.getCurrentUser();
		log.trace("Entering  saveImport for [recordId: {}; studyId {}; projectId {}] user[id: {}; email: {}]", () -> recordId.get(), () -> studyId.get(),
		    () -> pid.get(), () -> user.getId(), () -> user.getEmail());
		String ret = projectService.checkUserAccess(pid, studyId, redirectAttributes, true, user);
		if (ret == null) {
			ret = "redirect:/project/" + pid.get() + "/study/" + studyId.get() + "/record/" + recordId.get();
			try {
				recordService.sortVariablesAndSetMetaData(sForm);
				recordService.saveRecordToDBAndMinio(sForm);
			} catch (Exception e) {
				if (e instanceof DataWizSystemException) {
					log.warn("Exception thrown during sortVariablesAndSetMetaData or saveRecordToDBAndMinio:", () -> e);
					if (((DataWizSystemException) e).getErrorCode().equals(DataWizErrorCodes.NO_DATA_ERROR))
						redirectAttributes.addFlashAttribute("errorMSG", messageSource.getMessage("global.error.internal",
						    new Object[] { env.getRequiredProperty("organisation.admin.email") }, LocaleContextHolder.getLocale()));
					else if (((DataWizSystemException) e).getErrorCode().equals(DataWizErrorCodes.MINIO_SAVE_ERROR))
						redirectAttributes.addFlashAttribute("errorMSG", messageSource.getMessage("minio.connection.exception",
						    new Object[] { env.getRequiredProperty("organisation.admin.email"), e.getMessage() }, LocaleContextHolder.getLocale()));
				} else {
					log.fatal("Database-Exception thrown during sortVariablesAndSetMetaData or saveRecordToDBAndMinio:", () -> e);
					redirectAttributes.addFlashAttribute("errorMSG", messageSource.getMessage("dbs.sql.exception",
					    new Object[] { env.getRequiredProperty("organisation.admin.email") }, LocaleContextHolder.getLocale()));
				}
			}
		}
		if (log.isTraceEnabled())
			log.trace("Method saveImport completed - mapping to {}", ret);
		return ret;
	}

	/**
	 * 
	 * This function is called if a user saves the record meta data on record.jsp
	 * 
	 * @param pid
	 *          Project Identifier as {@link Optional}&lt;{@link Long}&gt;
	 * @param studyId
	 *          Study Identifier as {@link Optional}&lt;{@link Long}&gt;
	 * @param recordId
	 *          Record Identifier as {@link Optional}&lt;{@link Long}&gt;
	 * @param sForm
	 *          {@link StudyForm}
	 * @param redirectAttributes
	 *          {@link RedirectAttributes}
	 * @param model
	 *          {@link ModelMap}
	 * @return "record.jsp" on error with error messages, or redirect to /record on success.
	 */
	@RequestMapping(value = { "", "/{recordId}", "/{recordId}/version/{versionId}" }, params = "saveMetaData")
	public String saveRecordMetaData(@PathVariable final Optional<Long> pid, @PathVariable final Optional<Long> studyId,
	    @PathVariable final Optional<Long> recordId, @ModelAttribute("StudyForm") StudyForm sForm, final RedirectAttributes redirectAttributes,
	    final ModelMap model) {
		final UserDTO user = UserUtil.getCurrentUser();
		if (recordId.isPresent())
			log.trace("Entering saveRecordMetaData(update) for [recordId: {}; studyId {}; projectId {}]", () -> recordId.get(), () -> studyId.get(), () -> pid.get());
		else
			log.trace("Entering saveRecordMetaData(create) for [studyId {}; projectId {}]", () -> studyId.get(), () -> pid.get());
		String ret = projectService.checkUserAccess(pid, studyId, redirectAttributes, true, user);
		if (ret == null && (sForm.getRecord().getRecordName() == null || sForm.getRecord().getRecordName().isEmpty())) {
			model.put("errorMSG", messageSource.getMessage("record.name.missing", null, LocaleContextHolder.getLocale()));
			model.put("recordSubMenu", true);
			model.put("subnaviActive", PageState.RECORDMETA.name());
			ret = "record";
		}
		if (ret == null && sForm.getRecord().getDescription().length() > 2000) {
			model.put("errorMSG", messageSource.getMessage("record.desc.size", null, LocaleContextHolder.getLocale()));
			model.put("recordSubMenu", true);
			model.put("subnaviActive", PageState.RECORDMETA.name());
			ret = "record";
		}
		if (ret == null) {
			try {
				recordService.insertOrUpdateRecordMetadata(studyId, recordId, sForm, user);
				ret = "redirect:/project/" + pid.get() + "/study/" + studyId.get() + "/record/" + sForm.getRecord().getId();
			} catch (Exception e) {
				log.fatal("ERROR: Saving record to DB wasn't sucessful! Exception:", () -> e);
				model.put("errorMSG", messageSource.getMessage("dbs.sql.exception", new Object[] { env.getRequiredProperty("organisation.admin.email") },
				    LocaleContextHolder.getLocale()));
				ret = "record";
			}
		}
		if (log.isTraceEnabled())
			log.trace("Method saveRecordMetaData completed - mapping to {}", ret);
		return ret;
	}

	/**
	 * This function is called asynchronously if a user opens a modal in the codebook. It load the content for global or single missing, or value-label fields,
	 * depending on the given recordID.
	 * 
	 * @param model
	 *          {@link ModelMap}
	 * @param sForm
	 *          {@link StudyForm}
	 * 
	 * @param pid
	 *          Project Identifier as {@link Long};
	 * @param studyId
	 *          Study Identifier {@link Long};
	 * @param recordId
	 *          Record Identifier as {@link Long};
	 * @param varId
	 *          Variable Identifier as {@link Long};
	 * @param modal
	 *          "missing" for missing modal, "values" for value-label modal - type: {@link String};
	 * @return "forms/codebookModalGlobalValues.jsp" if global value-label is selected, "forms/codebookModalGlobalMissings.jsp" if global missing is selected, or
	 *         "forms/codebookModalContent.jsp" if single missing or value-label is selected
	 */
	@RequestMapping(value = { "{recordId}/version/{versionId}/codebook/modal" })
	public String loadAjaxModal(final ModelMap model, @ModelAttribute("StudyForm") StudyForm sForm, @PathVariable final Long pid,
	    @PathVariable final Long studyId, @PathVariable final Long recordId, @RequestParam(value = "varId", required = true) long varId,
	    @RequestParam(value = "modal", required = true) String modal) {
		log.trace("Entering loadAjaxModal [{}] for variable [id: {}]", () -> modal, () -> varId);
		String ret = "forms/codebookModalContent";
		if (varId != -1) {
			for (SPSSVarDTO var : sForm.getRecord().getVariables()) {
				if (var.getId() == varId) {
					model.put("VarValues", var);
					break;
				}
			}
		} else if (varId == -1) {
			if (modal.equals("values")) {
				ret = "forms/codebookModalGlobalValues";
				SPSSVarDTO var = new SPSSVarDTO();
				var.setId(varId);
				model.put("VarValues", var);
			} else if (modal.equals("missings")) {
				ret = "forms/codebookModalGlobalMissings";
				List<SPSSVarDTO> varL = new ArrayList<>();
				StudyForm varForm = (StudyForm) applicationContext.getBean("StudyForm");
				for (int i = 0; i < 2; i++) {
					SPSSVarDTO var = new SPSSVarDTO();
					varL.add(var);
				}
				varForm.setViewVars(varL);
				model.put("VarValues", varForm);
			}
		}
		model.put("modalView", modal);
		if (log.isTraceEnabled())
			log.trace("Method loadAjaxModal completed - mapping to {}", ret);
		return ret;
	}

	/**
	 * This function sets the value entries from the value modal into the StudyForm. It uses two StudyForms because the modal isn't included into the HTML form of
	 * the codebook.jsp. Therefore, the values of the StudyForm from the value modal (varval) have to be set into the original StudyForm.
	 * 
	 * @param model
	 *          {@link ModelMap}
	 * @param varVal
	 *          {@link StudyForm}
	 * @param sForm
	 *          {@link StudyForm}
	 * @return Mapping to codebook.jsp
	 */
	@RequestMapping(value = { "/{recordId}/version/{versionId}/codebook" }, params = "setValues")
	public String setValuesToStudyForm(final ModelMap model, @ModelAttribute("VarValues") SPSSVarDTO varVal, @ModelAttribute("StudyForm") StudyForm sForm) {
		log.trace("Entering setValuesToStudyForm for variable [id: {}]", () -> varVal.getId());
		recordService.setVariableValues(varVal, sForm);
		model.put("errorMSG", recordService.validateCodeBook(sForm));
		if (sForm.getWarnings() != null && sForm.getWarnings().size() > 0) {
			StringBuilder sb = new StringBuilder();
			sForm.getWarnings().forEach(s -> {
				sb.append(s);
				sb.append("<br />");
			});
			model.put("infoMSG", sb.toString());
		}
		model.remove("VarValues");
		model.put("recordSubMenu", true);
		model.put("subnaviActive", PageState.RECORDVAR.name());
		log.trace("Method setValuesToStudyForm completed - mapping to codebook");
		return "codebook";
	}

	/**
	 * This function sets the missing entries from the missing modal into the StudyForm. It uses two StudyForms because the modal isn't included into the HTML
	 * form of the codebook.jsp. Therefore, the values of the StudyForm from the missing modal (varval) have to be set into the original StudyForm.
	 * 
	 * @param model
	 *          {@link ModelMap}
	 * @param varVal
	 *          {@link StudyForm}
	 * @param sForm
	 *          {@link StudyForm}
	 * @return Mapping to codebook.jsp
	 */
	@RequestMapping(value = { "/{recordId}/version/{versionId}/codebook" }, params = "setMissings")
	public String setMissingsToStudyForm(final ModelMap model, @ModelAttribute("VarValues") SPSSVarDTO varVal, @ModelAttribute("StudyForm") StudyForm sForm) {
		log.trace("Entering setMissingsToStudyForm for variable [id: {}]", () -> varVal.getId());

		for (SPSSVarDTO var : sForm.getRecord().getVariables()) {
			if (var.getId() == varVal.getId()) {
				var.setMissingFormat(varVal.getMissingFormat());
				recordService.switchMissingType(varVal, var);
				model.remove("VarValues");
				break;
			}
		}
		model.put("errorMSG", recordService.validateCodeBook(sForm));
		model.put("warnCodeBookMSG", recordService.setMessageString(sForm.getWarnings()));
		model.put("recordSubMenu", true);
		model.put("subnaviActive", PageState.RECORDVAR.name());
		log.trace("Method setMissingsToStudyForm completed - mapping to codebook");
		return "codebook";
	}

	/**
	 * 
	 * This function is called from the record.jsp (exportModal). The user can select different export formats and this function handles this request. At first,
	 * it checks by using projectService.checkUserAccess(...), if the user has the permission to export the record, than it loads the record data by using
	 * recordService.loadRecordExportData(...) and finally it uses exportService.getRecordExportContentAsByteArray(...) to create a bytes array from the record
	 * data, which is written into the response. Because of the fact, that this function is called with target="_blank" and that exceptions are handled with the
	 * ExceptionController, it was decided that this function can throw exceptions.
	 * 
	 * @param model
	 *          {@link ModelMap}
	 * @param response
	 *          {@link HttpServletResponse}
	 * @param redirectAttributes
	 *          {@link RedirectAttributes}
	 * @param versionId
	 *          Version Identifier as {@link Optional}&lt;{@link Long}&gt;
	 * @param recordId
	 *          Record Identifier as {@link Optional}&lt;{@link Long}&gt;
	 * @param pid
	 *          Project Identifier as {@link Optional}&lt;{@link Long}&gt;
	 * @param studyId
	 *          Study Identifier as {@link Optional}&lt;{@link Long}&gt;
	 * @param exportType
	 *          {@link String}
	 * @param attachments
	 *          {@link Boolean}
	 * @throws Exception
	 */
	@RequestMapping(value = { "{recordId}/version/{versionId}/export/{exportType}" })
	public void exportRecord(final ModelMap model, HttpServletResponse response, RedirectAttributes redirectAttributes, @PathVariable long versionId,
	    @PathVariable long recordId, @PathVariable final Optional<Long> pid, @PathVariable final Optional<Long> studyId, @PathVariable String exportType,
	    @RequestParam(value = "attachments", required = false) Boolean attachments) throws Exception {
		log.trace("Entering exportRecord for [recordid: {}, version: {}, exporttye: {}] ", () -> recordId, () -> versionId, () -> exportType);
		UserDTO user = UserUtil.getCurrentUser();
		RecordDTO record = null;
		StringBuilder res = new StringBuilder();
		byte[] content = null;
		if (user == null || projectService.checkUserAccess(pid, studyId, redirectAttributes, false, user) != null) {
			log.warn("Auth User Object empty or User is permitted to download this file");
			throw new DWDownloadException("export.access.denied");
		}
		try {
			record = recordService.loadRecordExportData(versionId, recordId, exportType, res, pid.get());
			if (!res.toString().trim().isEmpty()) {
				record = null;
			}
		} catch (Exception e) {
			record = null;
			res.insert(0, "dbs.sql.exception");
			log.error("ERROR: Getting record from DB wasn't sucessful! Record[recordId:{}; VersionId:{}] Exception:", () -> recordId, () -> versionId, () -> e);
		}
		if (record != null) {
			try {
				content = exportService.getRecordExportContentAsByteArray(pid.get(), exportType, attachments, record, res);
			} catch (Exception e) {
				record = null;
				res.insert(0, "export.error.exception.thown");
				log.error("ERROR: Exception thrown at exportService.getRecordExportContentAsByteArray", () -> e);
			}
		}
		if (record != null && content != null) {
			switch (exportType) {
			case "CSVMatrix":
				response.setContentType("text/csv");
				response.setHeader("Content-Disposition", "attachment; filename=\"" + stringUtil.formatFilename(record.getRecordName()) + "_Matrix.csv\"");
				break;
			case "CSVCodebook":
				response.setContentType("text/csv");
				response.setHeader("Content-Disposition", "attachment; filename=\"" + stringUtil.formatFilename(record.getRecordName()) + "_Codebook.csv\"");
				break;
			case "JSON":
				response.setContentType("application/json");
				response.setHeader("Content-Disposition", "attachment; filename=\"" + stringUtil.formatFilename(record.getRecordName()) + ".json\"");
				break;
			case "SPSS":
				response.setContentType("application/sav");
				response.setHeader("Content-Disposition", "attachment; filename=\"" + stringUtil.formatFilename(record.getRecordName()) + ".sav\"");
				break;
			case "PDF":
				response.setContentType("application/pdf");
				response.setHeader("Content-Disposition", "attachment; filename=\"" + stringUtil.formatFilename(record.getRecordName()) + ".pdf\"");
				break;
			case "CSVZIP":
				response.setContentType("application/zip");
				response.setHeader("Content-Disposition", "attachment; filename=\"" + stringUtil.formatFilename(record.getRecordName()) + ".zip\"");
				break;
			}
			response.setContentLength(content.length);
			response.getOutputStream().write(content);
			response.flushBuffer();
			log.trace("Method exportRecord completed successfully");
		} else {
			log.warn("Method exportRecord completed with an error - DWDownloadException thrown: ", res.toString());
			throw new DWDownloadException(res.toString());
		}
	}

	/**
	 * To avoid long loading times in case of a huge amount of variables or cases, this function limits the amount of variables and cases which are shown in the
	 * view. It is called when the user change and submit the Amount of shown vars or cases int the codebook.jsp or matrix.jsp.
	 * 
	 * @param model
	 *          {@link ModelMap}
	 * @param sForm
	 *          {@link StudyForm}
	 * @param pid
	 *          Project Identifier as {@link Optional}&lt;{@link Long}&gt;
	 * @param studyId
	 *          Study Identifier as {@link Optional}&lt;{@link Long}&gt;
	 * @param recordId
	 *          Record Identifier as {@link Optional}&lt;{@link Long}&gt;
	 * @param pagestate
	 *          {@link String}
	 * @return Depends on pagestate, mapping to datamatrix.jsp or codebook.jsp.
	 */
	@RequestMapping(value = { "/{recordId}/version/{versionId}/{pagestate}" }, method = RequestMethod.POST, params = "setNumofVars")
	public String setNumofVars(final ModelMap model, @ModelAttribute("StudyForm") StudyForm sForm, @PathVariable final Optional<Long> pid,
	    @PathVariable final Optional<Long> studyId, @PathVariable final Optional<Long> recordId, @PathVariable final String pagestate) {
		log.trace("Entering setNumofVars");
		if (sForm == null || sForm.getRecord() == null || sForm.getRecord().getId() == 0) {
			log.warn("Setting setNumofVars failed - (sForm == null || sForm.getRecord() == null || sForm.getRecord().getId() == 0)");
		}
		String ret = "codebook";
		if (pagestate.equals("codebook")) {
			model.put("errorMSG", recordService.validateCodeBook(sForm));
			model.put("warnCodeBookMSG", recordService.setMessageString(sForm.getWarnings()));
			if (sForm.getPageLoadMin() < 1)
				sForm.setPageLoadMin(1);
			if (sForm.getPageLoadMax() > sForm.getRecord().getNumberOfVariables())
				sForm.setPageLoadMax(sForm.getRecord().getNumberOfVariables());
			model.put("subnaviActive", PageState.RECORDVAR.name());
		} else {
			if (sForm.getPageLoadMin() < 1)
				sForm.setPageLoadMin(1);
			if (sForm.getPageLoadMax() > sForm.getRecord().getNumberOfCases())
				sForm.setPageLoadMax((int) sForm.getRecord().getNumberOfCases());
			model.put("subnaviActive", PageState.RECORDDATA.name());
			ret = "datamatrix";
		}
		model.put("recordSubMenu", true);
		model.put("breadcrumpList",
		    BreadCrumbUtil.generateBC(PageState.RECORDS,
		        new String[] { sForm.getProject().getTitle(), sForm.getStudy().getTitle(),
		            (sForm.getRecord() != null ? sForm.getRecord().getRecordName()
		                : messageSource.getMessage("record.new.record.breadcrump", null, LocaleContextHolder.getLocale())) },
		        new long[] { pid.get(), studyId.get() }, messageSource));
		if (log.isTraceEnabled())
			log.trace("Method setNumofVars completed - mapping to {}", ret);
		return ret;
	}

	/**
	 * To prevent loss of data, this function is asynchronous called if a user opens a modal (missing, value, global missing/value), because the HTML form which
	 * is included in the codebook.jsp would not be submitted, and all by the user entered information would be lost.
	 * 
	 * @param model
	 *          {@link ModelMap}
	 * @param sForm
	 *          {@link StudyForm}
	 * @return HTTP Status as @ResponseBody ResponseEntity&lt;String&gt; - HttpStatus.OK on success, otherwise HttpStatus.BAD_REQUEST
	 */
	@RequestMapping(value = { "{recordId}/version/{versionId}/asyncSubmit" })
	public @ResponseBody ResponseEntity<String> setFormAsync(final ModelMap model, @ModelAttribute("StudyForm") StudyForm sForm) {
		log.trace("Entering setFormAsync");
		if (sForm == null || sForm.getRecord() == null || sForm.getRecord().getId() == 0) {
			log.warn("Setting Variables Async failed - (sForm == null || sForm.getRecord() == null || sForm.getRecord().getId() == 0) returned status: {}",
			    () -> HttpStatus.BAD_REQUEST.toString());
			return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
		}
		log.trace("Method setFormAsync completed with satus: {}", () -> HttpStatus.OK.toString());
		return new ResponseEntity<String>("{}", HttpStatus.OK);
	}

	@RequestMapping(value = { "{recordId}/version/{versionId}/copyCellValue" })
	public @ResponseBody String copyCellValue(final ModelMap model, @ModelAttribute("StudyForm") StudyForm sForm, @RequestParam("type") final String type,
	    @RequestParam("varId") final long varId) {
		log.trace("Entering copyCellValue for [type {}; varId: {}]", () -> type, () -> varId);
		String gson = null;
		try {
			gson = new Gson().toJson(recordService.getVariableValues(varId));
		} catch (Exception e) {
			gson = "[]";
		}
		log.trace("Method copyCellValue completed with satus: {}", () -> HttpStatus.OK.toString());
		return gson;
	}

	@RequestMapping(value = { "{recordId}/version/{versionId}/pasteCellValue" })
	public @ResponseBody ResponseEntity<String> pasteCellValue(final ModelMap model, @ModelAttribute("StudyForm") StudyForm sForm,
	    @RequestParam("type") final String type, @RequestParam("varId") final long varId, @RequestParam("valContent") final String valContent) {
		log.trace("Entering pasteCellValue for [type {}; varId: {}]", () -> type, () -> varId);
		SPSSVarDTO var = sForm.getRecord().getVariables().parallelStream().filter(var_t -> var_t.getId() == varId).findFirst().orElse(null);
		List<SPSSValueLabelDTO> values = new Gson().fromJson(valContent, new TypeToken<ArrayList<SPSSValueLabelDTO>>() {
		}.getType());
		if (var != null && values != null) {
			var.setValues(values);
		}
		log.trace("Method pasteCellValue completed with satus: {}", () -> HttpStatus.OK.toString());
		return new ResponseEntity<String>("{}", HttpStatus.OK);
	}

	/**
	 * This function sets the global missing entries from the missing modal into the StudyForm. It uses two StudyForms because the modal isn't included into the
	 * HTML form of the codebook.jsp. Therefore, the values of the StudyForm from the missing modal (varval) have to be set into the original StudyForm.
	 * 
	 * @param model
	 *          {@link ModelMap}
	 * @param varVal
	 *          {@link StudyForm}
	 * @param sForm
	 *          {@link StudyForm}
	 * @return Mapping to codebook.jsp
	 */
	@RequestMapping(value = { "/{recordId}/version/{versionId}/codebook" }, params = "setGlobalMissings")
	public String setGlobalMissingsToStudyForm(final ModelMap model, @ModelAttribute("VarValues") StudyForm varVal,
	    @ModelAttribute("StudyForm") StudyForm sForm) {
		log.trace("Entering setGlobalMissings for record [id: {}]", () -> sForm.getRecord().getId());
		List<SPSSVarDTO> missings = varVal.getViewVars();
		for (SPSSVarDTO var : sForm.getRecord().getVariables()) {
			if (!missings.get(0).getMissingFormat().equals(SPSSMissing.SPSS_UNKNOWN) && RecordDTO.simplifyVarTypes(var.getType()).equals(SPSSVarTypes.SPSS_FMT_A)) {
				var.setMissingFormat(missings.get(0).getMissingFormat());
				recordService.switchMissingType(missings.get(0), var);
			} else if (!missings.get(1).getMissingFormat().equals(SPSSMissing.SPSS_UNKNOWN)
			    && RecordDTO.simplifyVarTypes(var.getType()).equals(SPSSVarTypes.SPSS_FMT_F)) {
				var.setMissingFormat(missings.get(1).getMissingFormat());
				recordService.switchMissingType(missings.get(1), var);
			}
		}
		model.put("errorMSG", recordService.validateCodeBook(sForm));
		model.put("warnCodeBookMSG", recordService.setMessageString(sForm.getWarnings()));
		model.put("recordSubMenu", true);
		model.put("subnaviActive", PageState.RECORDVAR.name());
		log.trace("Method setGlobalMissingsToStudyForm completed - mapping to codebook");
		return "codebook";
	}

	/**
	 * This function is called if a user wants to save the codebook. It uses recordService.validateAndPrepareCodebookForm(...) for validation. For comparing the
	 * new codebook with the codebook which is saved in the database, and for finally saving the codebook to the database if no errors occur,
	 * recordService.compareAndSaveCodebook(...) is called. If the new codebook and the codebook which is stored in the database do not differ, nothing is saved.
	 * 
	 * @param pid
	 *          Project Identifier as {@link Optional}&lt;{@link Long}&gt;
	 * @param studyId
	 *          Study Identifier as {@link Optional}&lt;{@link Long}&gt;
	 * @param versionId
	 *          Version Identifier as {@link Optional}&lt;{@link Long}&gt;
	 * @param recordId
	 *          Record Identifier as {@link Optional}&lt;{@link Long}&gt;
	 * @param sForm
	 *          {@link StudyForm}
	 * @param redirectAttributes
	 *          {@link RedirectAttributes}
	 * @param model
	 *          {@link ModelMap}
	 * @return If validation has errors or other errors occurred, returning to record.jsp with error messages, otherwise mapping to redirect/codebook with
	 *         success/info message
	 */
	@RequestMapping(value = { "/{recordId}/version/{versionId}/codebook" }, method = RequestMethod.POST, params = "saveCodebook")
	public String saveCodebook(@PathVariable final Optional<Long> pid, @PathVariable final Optional<Long> studyId, @PathVariable long versionId,
	    @PathVariable long recordId, @ModelAttribute("StudyForm") StudyForm sForm, final RedirectAttributes redirectAttributes, final ModelMap model) {
		UserDTO user = UserUtil.getCurrentUser();
		log.trace("Entering saveCodebook for record [id: {}; current_version{}] and User [email: {}]", () -> recordId, () -> versionId, () -> user.getEmail());
		String ret = projectService.checkUserAccess(pid, studyId, redirectAttributes, true, user);
		if (ret == null) {
			if (sForm.getPageLoadMin() < 1)
				sForm.setPageLoadMin(1);
			if (sForm.getPageLoadMax() > sForm.getRecord().getNumberOfVariables())
				sForm.setPageLoadMax(sForm.getRecord().getNumberOfVariables());
			RecordDTO currentVersion = sForm.getRecord();
			Set<String> parsingErrors = new HashSet<String>();
			Set<String> parsingWarnings = new HashSet<String>();
			String infoMSG = null;
			try {
				RecordDTO copy = (RecordDTO) ObjectCloner.deepCopy(currentVersion);
				recordService.validateAndPrepareCodebookForm(copy, parsingErrors, parsingWarnings, sForm.getNewChangeLog(), false, sForm.isIgnoreValidationErrors());
				currentVersion = copy;
				infoMSG = recordService.compareAndSaveCodebook(currentVersion, parsingErrors, sForm.getNewChangeLog(), pid.get());
				if (infoMSG.equals("record.codebook.saved"))
					redirectAttributes.addFlashAttribute("successMSG", messageSource.getMessage(infoMSG, null, LocaleContextHolder.getLocale()));
				else {
					redirectAttributes.addFlashAttribute("infoMSG", messageSource.getMessage(infoMSG, null, LocaleContextHolder.getLocale()));
				}
				ret = "redirect:/project/" + sForm.getProject().getId() + "/study/" + sForm.getStudy().getId() + "/record/" + currentVersion.getId() + "/version/"
				    + currentVersion.getVersionId() + "/codebook";
			} catch (DataWizSystemException e) {
				model.put("infoMSG", messageSource.getMessage("record.codebook.not.saved", null, LocaleContextHolder.getLocale()));
				model.put("warnCodeBookMSG", recordService.setMessageString(sForm.getWarnings()));
				if (e.getErrorCode().equals(DataWizErrorCodes.DATABASE_ERROR)) {
					log.fatal("Database Exception during saveCodebook - Code {}; Message: {}", () -> e.getErrorCode(), () -> e.getMessage(), () -> e);
					model.put("errorMSG",
					    messageSource.getMessage("dbs.sql.exception",
					        new Object[] { env.getRequiredProperty("organisation.admin.email"), e.getMessage().replaceAll("\n", "").replaceAll("\"", "\'") },
					        LocaleContextHolder.getLocale()));
				} else {
					log.debug("Parsing Exception during saveCodebook - Code {}; Message: {}", () -> e.getErrorCode(), () -> e.getMessage());
					model.put("errorCodeBookMSG", recordService.setMessageString(parsingErrors.parallelStream().collect(Collectors.toList())));
				}
				model.put("subnaviActive", PageState.RECORDVAR.name());
				model.put("recordSubMenu", true);
				ret = "codebook";
			} catch (ClassNotFoundException | IOException e) {
				log.fatal("ClassNotFoundException |  IOException during saveCodebook - Message:", () -> e);
				model.put("errorMSG",
				    messageSource.getMessage("global.error.internal",
				        new Object[] { env.getRequiredProperty("organisation.admin.email"), e.getMessage().replaceAll("\n", "").replaceAll("\"", "\'") },
				        LocaleContextHolder.getLocale()));
				model.put("subnaviActive", PageState.RECORDVAR.name());
				model.put("recordSubMenu", true);
				ret = "codebook";
			}
		}
		log.trace("Method saveCodebook completed - mapping to {}", ret);
		return ret;
	}

}
