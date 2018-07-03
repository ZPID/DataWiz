package de.zpid.datawiz.controller;

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
import de.zpid.datawiz.service.*;
import de.zpid.datawiz.util.BreadCrumbUtil;
import de.zpid.datawiz.util.ObjectCloner;
import de.zpid.datawiz.util.StringUtil;
import de.zpid.datawiz.util.UserUtil;
import de.zpid.spss.SPSSIO;
import de.zpid.spss.dto.SPSSValueLabelDTO;
import de.zpid.spss.dto.SPSSVarDTO;
import de.zpid.spss.util.SPSSMissing;
import de.zpid.spss.util.SPSSVarTypes;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * This controller handles all calls to /project/{pid}/study/{studyId}/record/*
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
 * FIXME: StudyForm is stored in Session. This should be revised, because it causes errors on using multiple browser tabs!!!
 **/
@Controller
@RequestMapping(value = {"/project/{pid}/study/{studyId}/record"})
@SessionAttributes({"StudyForm", "subnaviActive", "breadcrumbList"})
public class RecordController {

    private static Logger log = LogManager.getLogger(RecordController.class);
    private RecordService recordService;
    private ExportService exportService;
    private ImportService importService;
    private MessageSource messageSource;
    private Environment env;
    private ClassPathXmlApplicationContext applicationContext;
    private ExceptionService exceptionService;
    private ProjectService projectService;
    private SPSSIO spss;
    private StringUtil stringUtil;


    /**
     * Instantiates a new record controller.
     */
    @Autowired
    public RecordController(RecordService recordService, ExportService exportService, ImportService importService, MessageSource messageSource,
                            Environment env, ClassPathXmlApplicationContext applicationContext, ExceptionService exceptionService,
                            ProjectService projectService, SPSSIO spss, StringUtil stringUtil) {
        super();
        log.info("Loading RecordController for mapping /project/{pid}/study/{sid}/record");
        this.recordService = recordService;
        this.exportService = exportService;
        this.importService = importService;
        this.messageSource = messageSource;
        this.env = env;
        this.applicationContext = applicationContext;
        this.exceptionService = exceptionService;
        this.projectService = projectService;
        this.spss = spss;
        this.stringUtil = stringUtil;
    }


    /**
     * This function is called if a user selects the record meta-data, codebook, or matrix. The mapping depends on the passed "subpage" string.
     *
     * @param pid                Project Identifier as {@link Optional}&lt;{@link Long}&gt;
     * @param studyId            Study Identifier as {@link Optional}&lt;{@link Long}&gt;
     * @param recordId           Record Identifier as {@link Optional}&lt;{@link Long}&gt;
     * @param versionId          Version Identifier as {@link Optional}&lt;{@link Long}&gt;
     * @param redirectAttributes {@link RedirectAttributes}
     * @param model              {@link ModelMap}
     * @param subpage            Mapping variable as {@link String}: "codebook" for mapping to codebook.jsp, "data" for mapping to datamatrix.jsp, or empty for mapping to
     *                           record.jsp
     * @return Mapping to record.jsp, codebook.jsp, or datamatrix.jsp (depends on subpage) on success, otherwise exception handling via
     * exceptionService.setErrorMessagesAndRedirects(...)
     */
    @RequestMapping(value = {"", "/{recordId}", "/{recordId}/{subpage}", "/{recordId}/version/{versionId}",
            "/{recordId}/version/{versionId}/{subpage}"}, method = RequestMethod.GET)
    public String showRecord(@PathVariable final long pid, @PathVariable final long studyId, @PathVariable final Optional<Long> recordId,
                             @PathVariable final Optional<Long> versionId, final RedirectAttributes redirectAttributes, final ModelMap model,
                             @PathVariable final Optional<String> subpage) {
        final UserDTO user = UserUtil.getCurrentUser();
        String ret;
        if (recordId.isPresent()) {
            log.trace("Entering showRecord(edit) for [recordId: {}; studyId {}; projectId {}]", recordId::get, () -> studyId, () -> pid);
            ret = projectService.checkUserAccess(pid, studyId, redirectAttributes, false, user);
        } else {
            log.trace("Entering showRecord(create) [studyId {}; projectId {}]", () -> studyId, () -> pid);
            ret = projectService.checkUserAccess(pid, studyId, redirectAttributes, true, user);
        }
        StudyForm sForm = null;
        List<String> parsingErrors = new ArrayList<>();
        if (ret == null) {
            try {
                sForm = recordService.setStudyform(pid, studyId, recordId.orElse(0L), versionId.orElse(0L), subpage.orElse(null), parsingErrors);
            } catch (Exception e) {
                ret = exceptionService.setErrorMessagesAndRedirects(pid, studyId, recordId.orElse(0L), model, redirectAttributes, e, "recordService.setStudyForm");
            }
        }
        if (sForm != null) {
            model.put("StudyForm", sForm);
            model.put("recordSubMenu", true);
            model.put("breadcrumbList",
                    BreadCrumbUtil.generateBC(PageState.RECORDS,
                            new String[]{sForm.getProject().getTitle(), sForm.getStudy().getTitle(),
                                    (sForm.getRecord() != null ? sForm.getRecord().getRecordName()
                                            : messageSource.getMessage("record.new.record.breadcrump", null, LocaleContextHolder.getLocale()))},
                            new long[]{pid, studyId}, messageSource));
            if (sForm.getPageLoadMin() == 0) {
                sForm.setPageLoadMin(1);
            }
            switch (subpage.orElse("")) {
                case "codebook":
                    model.put("subnaviActive", PageState.RECORDVAR.name());
                    model.put("errorCodeBookMSG", recordService.validateCodeBook(sForm));
                    if (sForm.getPageLoadMax() == 0) {
                        sForm.setPageLoadMax(sForm.getRecord().getNumberOfVariables() < 5 ? sForm.getRecord().getNumberOfVariables() : 5);
                    }
                    ret = "codebook";
                    model.put("warnCodeBookMSG", recordService.setMessageString(sForm.getWarnings()));
                    break;
                case "data":
                    model.put("subnaviActive", PageState.RECORDDATA.name());
                    if (sForm.getPageLoadMax() == 0) {
                        sForm.setPageLoadMax(sForm.getRecord().getNumberOfCases() < 100 ? (int) sForm.getRecord().getNumberOfCases() : 100);
                    }
                    ret = "datamatrix";
                    break;
                default:
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
                    break;
            }
        }
        log.trace("Method showRecord completed - mapping to {}", ret);
        return ret;
    }

    /**
     * This function is called after a user submits the upload form from the report.jsp. Before starting the upload process, it checks if the change-log is set.
     * If it is missing it redirects to record.jsp with missing change-log message.
     *
     * @param pid                Project Identifier as {@link Long}
     * @param studyId            Study Identifier as {@link Long}
     * @param recordId           Record Identifier as {@link Long}
     * @param sForm              {@link StudyForm}
     * @param redirectAttributes {@link RedirectAttributes}
     * @param model              {@link ModelMap}
     * @return Mapping to redirect:/importReport on success, otherwise error and mapping handling via exceptionService.setErrorMessagesAndRedirects(...)
     */
    @RequestMapping(value = {"/{recordId}"}, method = RequestMethod.POST, params = {"upload"})
    public String uploadFile(@PathVariable final long pid, @PathVariable long studyId, @PathVariable final long recordId,
                             @ModelAttribute("StudyForm") StudyForm sForm, final RedirectAttributes redirectAttributes, final ModelMap model) {
        final UserDTO user = UserUtil.getCurrentUser();
        log.trace("Entering  uploadFile for [recordId: {}; studyId {}; projectId {}] user[id: {}; email: {}]", () -> recordId, () -> studyId,
                () -> pid, user::getId, user::getEmail);
        String ret = projectService.checkUserAccess(pid, studyId, redirectAttributes, true, user);
        if (ret == null)
            if (sForm.getNewChangeLog() == null || sForm.getNewChangeLog().isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMSG", messageSource.getMessage("record.no.changelog", null, LocaleContextHolder.getLocale()));
                ret = "redirect:/project/" + pid + "/study/" + studyId + "/record/" + recordId;
            } else {
                try {
                    importService.importFile(pid, studyId, recordId, sForm, user);
                    ret = "redirect:/project/" + pid + "/study/" + studyId + "/record/" + recordId + "/importReport";
                } catch (DataWizSystemException e) {
                    ret = exceptionService.setErrorMessagesAndRedirects(pid, studyId, recordId, model, redirectAttributes, e, "importService.importFile");
                }
            }
        log.trace("Method uploadFile completed - mapping to {}", ret);
        return ret;
    }

    /**
     * This function is called if a user wants to delete a record. It checks if the user has the rights to delete the record. If the rights to delete are given
     * recordService.deleteRecord(...) is called to delete the record finally from Database and Minio.
     *
     * @param pid      Project Identifier as long
     * @param studyId  Study Identifier as long
     * @param recordId Record Identifier as long
     * @param sForm    {@link StudyForm}
     * @param model    {@link ModelMap}
     * @return Mapping to redirect/records on success, otherwise to record.jsp or error.jsp (Database Errors) with error messages
     */
    @RequestMapping(value = {"/{recordId}/deleteRecord"}, method = RequestMethod.GET)
    public String deleteRecord(@PathVariable final long pid, @PathVariable final long studyId, @PathVariable final long recordId,
                               @ModelAttribute("StudyForm") StudyForm sForm, final ModelMap model) {
        final UserDTO user = UserUtil.getCurrentUser();
        log.trace("Entering  deleteRecord for [recordId: {}; studyId {}; projectId {}] user[id: {}; email: {}]", () -> recordId, () -> studyId,
                () -> pid, user::getId, user::getEmail);
        String ret = "redirect:/project/" + pid + "/study/" + studyId + "/records";
        try {
            recordService.deleteRecord(pid, studyId, recordId, user, true);
        } catch (DataWizSystemException e) {
            if (e.getErrorCode().equals(DataWizErrorCodes.DATABASE_ERROR)) {
                model.put("errormsg",
                        messageSource.getMessage("dbs.sql.exception",
                                new Object[]{env.getRequiredProperty("organisation.admin.email"), e.getMessage().replaceAll("\n", "").replaceAll("\"", "\'")},
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
     * @param pid                Project Identifier as long
     * @param studyId            Study Identifier as long
     * @param recordId           Record Identifier as long
     * @param sForm              {@link StudyForm}
     * @param redirectAttributes {@link RedirectAttributes}
     * @return Mapping to "importReport.jsp" on success, otherwise redirect to /record with error messages.
     */
    @RequestMapping(value = {"/{recordId}/importReport"}, method = RequestMethod.GET)
    public String showImportReport(@PathVariable final long pid, @PathVariable final long studyId,
                                   @PathVariable final long recordId, @ModelAttribute("StudyForm") StudyForm sForm, final RedirectAttributes redirectAttributes) {
        final UserDTO user = UserUtil.getCurrentUser();
        log.trace("Entering  showImportReport for [recordId: {}; studyId {}; projectId {}] user[id: {}; email: {}]", () -> recordId, () -> studyId,
                () -> pid, user::getId, user::getEmail);
        String ret = projectService.checkUserAccess(pid, studyId, redirectAttributes, true, user);
        if (sForm == null || sForm.getRecord() == null) {
            log.debug("Record Object is empty - Session timeout");
            redirectAttributes.addFlashAttribute("errorMSG",
                    "Ihre Anfrage an den Server enthielt keine Daten, wahrscheinlich waren Sie zu lange inaktiv und die Session ist abgelaufen.");
            ret = "redirect:/project/" + pid + "/study/" + studyId + "/record/" + recordId;
        }
        if (ret == null) {
            try {
                importService.loadImportReport(recordId, sForm);
                ret = "importRep";
            } catch (Exception e) {
                log.error("Exception during recordService.setStudyform Message: ", () -> e);
                if (e instanceof IOException || e instanceof ClassNotFoundException) {
                    redirectAttributes.addFlashAttribute("errorMSG", messageSource.getMessage("global.error.internal",
                            new Object[]{env.getRequiredProperty("organisation.admin.email")}, LocaleContextHolder.getLocale()));
                } else {
                    redirectAttributes.addFlashAttribute("errorMSG", messageSource.getMessage("dbs.sql.exception",
                            new Object[]{env.getRequiredProperty("organisation.admin.email")}, LocaleContextHolder.getLocale()));
                }
                ret = "redirect:/project/" + pid + "/study/" + studyId + "/record/" + recordId;
            }
        }
        if (log.isTraceEnabled())
            log.trace("Method showImportReport completed - mapping to {}", ret);
        return ret;
    }

    /**
     * This function is used by DataTables.js to load the DataMatrix asynchronously to improve the page load speed.
     *
     * @param sForm {@link StudyForm}
     * @return Selected Part of the Data Matrix
     */
    @RequestMapping(value = {"/{recordId}/getMatrixAsync/{state}", "/{recordId}/version/{versionId}/getMatrixAsync/{state}"}, method = RequestMethod.POST)
    public @ResponseBody
    String getMatrixAsync(@PathVariable final Optional<Double> recordId, @PathVariable final Optional<Double> versionId,
                          @ModelAttribute("StudyForm") final StudyForm sForm, final HttpServletRequest request, @PathVariable final String state) {
        String search = request.getParameter("search[value]");
        String start = request.getParameter("start");
        String length = request.getParameter("length");
        String draw = request.getParameter("draw");
        log.trace("Entering getMatrixAsync for importReport with Parameter [state: {}; draw: {}; start: {}; length: {}; search: {}] for record [id: {}; version: {}]", () -> state, () -> draw,
                () -> start, () -> length, () -> search, () -> recordId, () -> versionId);
        StringBuilder err = new StringBuilder();
        DataTableDTO datatable = new DataTableDTO();
        int startI = 0, lengthI = 0;
        try {
            if (draw != null && !draw.isEmpty())
                datatable.setDraw(Integer.parseInt(draw));
            else {
                err.append("\n ")
                        .append(messageSource.getMessage("record.matrix.async.error", new Object[]{"Parameter 'draw' is not set!"}, LocaleContextHolder.getLocale()));
            }
            if (start != null && !start.isEmpty())
                startI = Integer.parseInt(start);
            else {
                err.append("\n ")
                        .append(messageSource.getMessage("record.matrix.async.error", new Object[]{"Parameter 'start' is not set!"}, LocaleContextHolder.getLocale()));
            }
            if (length != null && !length.isEmpty())
                lengthI = Integer.parseInt(length);
            else {
                err.append("\n ")
                        .append(messageSource.getMessage("record.matrix.async.error", new Object[]{"Parameter 'length' is not given!"}, LocaleContextHolder.getLocale()));
            }
        } catch (Exception e) {
            log.warn("Error during parsing String paramater" + e);
            err.append("\n ")
                    .append(messageSource.getMessage("record.matrix.async.error", new Object[]{"Parsing Error: " + e.getMessage()}, LocaleContextHolder.getLocale()));
        }
        if (err.length() == 0) {
            List<List<Object>> list = null;
            if (state.equals("import") && sForm.getImportMatrix() != null) {
                List<List<Object>> list_t = new ArrayList<>();
                sForm.getImportMatrix().forEach(row ->
                        list_t.add(new ArrayList<>(Arrays.asList(row)))
                );
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
     * @param pid                Project Identifier as {@link Optional}&lt;{@link Long}&gt;
     * @param studyId            Study Identifier as {@link Optional}&lt;{@link Long}&gt;
     * @param recordId           Record Identifier as {@link Optional}&lt;{@link Long}&gt;
     * @param sForm              {@link StudyForm}
     * @param redirectAttributes {@link RedirectAttributes}
     * @return Redirect mapping to /record on success and on error, but with error messages if errors occurred.
     */
    @RequestMapping(value = {"/{recordId}"}, method = RequestMethod.POST)
    public String saveImport(@PathVariable final long pid, @PathVariable final long studyId, @PathVariable final long recordId,
                             @ModelAttribute("StudyForm") StudyForm sForm, final RedirectAttributes redirectAttributes) {
        final UserDTO user = UserUtil.getCurrentUser();
        log.trace("Entering  saveImport for [recordId: {}; studyId {}; projectId {}] user[id: {}; email: {}]", () -> recordId, () -> studyId,
                () -> pid, user::getId, user::getEmail);
        String ret = projectService.checkUserAccess(pid, studyId, redirectAttributes, true, user);
        if (ret == null) {
            ret = "redirect:/project/" + pid + "/study/" + studyId + "/record/" + recordId;
            try {
                recordService.sortVariablesAndSetMetaData(sForm);
                recordService.saveRecordToDBAndMinio(sForm);
            } catch (Exception e) {
                if (e instanceof DataWizSystemException) {
                    log.warn("Exception thrown during sortVariablesAndSetMetaData or saveRecordToDBAndMinio:", () -> e);
                    if (((DataWizSystemException) e).getErrorCode().equals(DataWizErrorCodes.NO_DATA_ERROR))
                        redirectAttributes.addFlashAttribute("errorMSG", messageSource.getMessage("global.error.internal",
                                new Object[]{env.getRequiredProperty("organisation.admin.email")}, LocaleContextHolder.getLocale()));
                    else if (((DataWizSystemException) e).getErrorCode().equals(DataWizErrorCodes.MINIO_SAVE_ERROR))
                        redirectAttributes.addFlashAttribute("errorMSG", messageSource.getMessage("minio.connection.exception",
                                new Object[]{env.getRequiredProperty("organisation.admin.email"), e.getMessage()}, LocaleContextHolder.getLocale()));
                } else {
                    log.fatal("Database-Exception thrown during sortVariablesAndSetMetaData or saveRecordToDBAndMinio:", () -> e);
                    redirectAttributes.addFlashAttribute("errorMSG", messageSource.getMessage("dbs.sql.exception",
                            new Object[]{env.getRequiredProperty("organisation.admin.email")}, LocaleContextHolder.getLocale()));
                }
            }
        }
        if (log.isTraceEnabled())
            log.trace("Method saveImport completed - mapping to {}", ret);
        return ret;
    }

    /**
     * This function is called if a user saves the record meta data on record.jsp
     *
     * @param pid                Project Identifier as long
     * @param studyId            Study Identifier as long
     * @param recordId           Record Identifier as {@link Optional}&lt;{@link Long}&gt;
     * @param sForm              {@link StudyForm}
     * @param redirectAttributes {@link RedirectAttributes}
     * @param model              {@link ModelMap}
     * @return "record.jsp" on error with error messages, or redirect to /record on success.
     */
    @RequestMapping(value = {"", "/{recordId}", "/{recordId}/version/{versionId}"}, params = "saveMetaData")
    public String saveRecordMetaData(@PathVariable final long pid, @PathVariable final long studyId,
                                     @PathVariable final Optional<Long> recordId, @PathVariable final Optional<Double> versionId,
                                     @ModelAttribute("StudyForm") final StudyForm sForm, final RedirectAttributes redirectAttributes,
                                     final ModelMap model) {
        final UserDTO user = UserUtil.getCurrentUser();
        if (recordId.isPresent())
            log.trace("Entering saveRecordMetaData(update) for [recordId: {}; version: {}; studyId {}; projectId {}]", () -> recordId, () -> versionId, () -> studyId, () -> pid);
        else
            log.trace("Entering saveRecordMetaData(create) for [studyId {}; projectId {}]", () -> studyId, () -> pid);
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
                recordService.insertOrUpdateRecordMetadata(studyId, recordId.orElse(0L), sForm, user);
                ret = "redirect:/project/" + pid + "/study/" + studyId + "/record/" + sForm.getRecord().getId();
            } catch (Exception e) {
                log.fatal("ERROR: Saving record to DB wasn't successful! Exception:", () -> e);
                model.put("errorMSG", messageSource.getMessage("dbs.sql.exception", new Object[]{env.getRequiredProperty("organisation.admin.email")},
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
     * @param model {@link ModelMap}
     * @param sForm {@link StudyForm}
     * @param varId Variable Identifier as {@link Long};
     * @param modal "missing" for missing modal, "values" for value-label modal - type: {@link String};
     * @return "forms/codebookModalGlobalValues.jsp" if global value-label is selected, "forms/codebookModalGlobalMissings.jsp" if global missing is selected, or
     * "forms/codebookModalContent.jsp" if single missing or value-label is selected
     */
    @RequestMapping(value = {"{recordId}/version/{versionId}/codebook/modal"})
    public String loadAjaxModal(@PathVariable final Optional<Long> recordId, @PathVariable final Optional<Double> versionId,
                                final ModelMap model, @ModelAttribute("StudyForm") final StudyForm sForm, @RequestParam(value = "varId") final long varId,
                                @RequestParam(value = "modal") final String modal) {
        log.trace("Entering loadAjaxModal [{}] for variable [recordId: {}; version: {}; id: {}]", () -> modal, () -> recordId, () -> versionId, () -> varId);
        String ret = "forms/codebookModalContent";
        if (varId == -1) {
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
        } else {
            for (SPSSVarDTO var : sForm.getRecord().getVariables()) {
                if (var.getId() == varId) {
                    model.put("VarValues", var);
                    break;
                }
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
     * @param model  {@link ModelMap}
     * @param varVal {@link StudyForm}
     * @param sForm  {@link StudyForm}
     * @return Mapping to codebook.jsp
     */
    @RequestMapping(value = {"/{recordId}/version/{versionId}/codebook"}, params = "setValues")
    public String setValuesToStudyForm(@PathVariable final Optional<Long> recordId, @PathVariable final Optional<Double> versionId,
                                       final ModelMap model, @ModelAttribute("VarValues") final SPSSVarDTO varVal, @ModelAttribute("StudyForm") final StudyForm sForm) {
        log.trace("Entering setValuesToStudyForm for variable [recordId: {}; version: {}; id: {}]", () -> recordId, () -> versionId, varVal::getId);
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
     * @param model  {@link ModelMap}
     * @param varVal {@link StudyForm}
     * @param sForm  {@link StudyForm}
     * @return Mapping to codebook.jsp
     */
    @RequestMapping(value = {"/{recordId}/version/{versionId}/codebook"}, params = "setMissings")
    public String setMissingsToStudyForm(@PathVariable final Optional<Long> recordId, @PathVariable final Optional<Double> versionId,
                                         final ModelMap model, @ModelAttribute("VarValues") final SPSSVarDTO varVal, @ModelAttribute("StudyForm") final StudyForm sForm) {
        log.trace("Entering setMissingsToStudyForm for variable [recordId: {}; version: {}; id: {}]", () -> recordId, () -> versionId, varVal::getId);

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
     * This function is called from the record.jsp (exportModal). The user can select different export formats and this function handles this request. At first,
     * it checks by using projectService.checkUserAccess(...), if the user has the permission to export the record, than it loads the record data by using
     * recordService.loadRecordExportData(...) and finally it uses exportService.getRecordExportContentAsByteArray(...) to create a bytes array from the record
     * data, which is written into the response. Because of the fact, that this function is called with target="_blank" and that exceptions are handled with the
     * ExceptionController, it was decided that this function can throw exceptions.
     *
     * @param response           {@link HttpServletResponse}
     * @param redirectAttributes {@link RedirectAttributes}
     * @param versionId          Version Identifier as long
     * @param recordId           Record Identifier as long
     * @param pid                Project Identifier as long
     * @param studyId            Study Identifier as long
     * @param exportType         {@link String}
     * @param attachments        {@link Boolean}
     * @throws Exception Download Exceptions
     */
    @RequestMapping(value = {"{recordId}/version/{versionId}/export/{exportType}"})
    public void exportRecord(final HttpServletResponse response, final RedirectAttributes redirectAttributes, @PathVariable final long versionId,
                             @PathVariable final long recordId, @PathVariable final long pid, @PathVariable final long studyId, @PathVariable final String exportType,
                             @RequestParam(value = "attachments", required = false) Boolean attachments) throws Exception {
        log.trace("Entering exportRecord for [recordId: {}, version: {}, exportType: {}] ", () -> recordId, () -> versionId, () -> exportType);
        UserDTO user = UserUtil.getCurrentUser();
        RecordDTO record;
        StringBuilder res = new StringBuilder();
        byte[] content = null;
        if (user == null || projectService.checkUserAccess(pid, studyId, redirectAttributes, false, user) != null) {
            log.warn("Auth User Object empty or User is permitted to download this file");
            throw new DWDownloadException("export.access.denied");
        }
        try {
            record = recordService.loadRecordExportData(versionId, recordId, exportType, res, pid);
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
                content = exportService.getRecordExportContentAsByteArray(pid, exportType, attachments, record, res);
            } catch (Exception e) {
                record = null;
                res.insert(0, "export.error.exception.thrown");
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
     * @param model     {@link ModelMap}
     * @param sForm     {@link StudyForm}
     * @param pid       Project Identifier as {@link Optional}&lt;{@link Long}&gt;
     * @param studyId   Study Identifier as {@link Optional}&lt;{@link Long}&gt;
     * @param recordId  Record Identifier as {@link Optional}&lt;{@link Long}&gt;
     * @param pagestate {@link String}
     * @return Depends on pagestate, mapping to datamatrix.jsp or codebook.jsp.
     */
    @RequestMapping(value = {"/{recordId}/version/{versionId}/{pagestate}"}, method = RequestMethod.POST, params = "setNumofVars")
    public String setNumofVars(final ModelMap model, @ModelAttribute("StudyForm") StudyForm sForm, @PathVariable final long pid,
                               @PathVariable final long studyId, @PathVariable final Optional<Long> recordId,
                               @PathVariable final Optional<Double> versionId, @PathVariable final String pagestate) {
        log.trace("Entering setNumofVars [recordId: {}; version: {}]", () -> recordId, () -> versionId);
        String ret;
        if (sForm == null || sForm.getRecord() == null || sForm.getRecord().getId() == 0) {
            log.warn("Setting setNumofVars failed - (sForm == null || sForm.getRecord() == null || sForm.getRecord().getId() == 0)");
            model.put("errorMSG", messageSource.getMessage("record.codebook.server.error", null, LocaleContextHolder.getLocale()));
            ret = pagestate.equals("codebook") ? pagestate : "datamatrix";
        } else {
            ret = "codebook";
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
            model.put("breadcrumbList",
                    BreadCrumbUtil.generateBC(PageState.RECORDS,
                            new String[]{sForm.getProject().getTitle(), sForm.getStudy().getTitle(),
                                    (sForm.getRecord() != null ? sForm.getRecord().getRecordName()
                                            : messageSource.getMessage("record.new.record.breadcrump", null, LocaleContextHolder.getLocale()))},
                            new long[]{pid, studyId}, messageSource));
            if (log.isTraceEnabled())
                log.trace("Method setNumofVars completed - mapping to {}", ret);
        }
        return ret;
    }

    /**
     * To prevent loss of data, this function is asynchronous called if a user opens a modal (missing, value, global missing/value), because the HTML form which
     * is included in the codebook.jsp would not be submitted, and all by the user entered information would be lost.
     *
     * @param sForm {@link StudyForm}
     * @return HTTP Status as @ResponseBody ResponseEntity - HttpStatus.OK on success, otherwise HttpStatus.BAD_REQUEST
     */
    @RequestMapping(value = {"{recordId}/version/{versionId}/asyncSubmit"})
    public @ResponseBody
    ResponseEntity<String> setFormAsync(@ModelAttribute("StudyForm") final StudyForm sForm) {
        log.trace("Entering setFormAsync");
        if (sForm == null || sForm.getRecord() == null || sForm.getRecord().getId() == 0) {
            log.warn("Setting Variables Async failed - (sForm == null || sForm.getRecord() == null || sForm.getRecord().getId() == 0) returned status: {}",
                    HttpStatus.BAD_REQUEST::toString);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        log.trace("Method setFormAsync completed with status: {}", HttpStatus.OK::toString);
        return new ResponseEntity<>("{}", HttpStatus.OK);
    }


    @RequestMapping(value = {"{recordId}/version/{versionId}/copyCellValue"})
    public @ResponseBody
    String copyCellValue(@ModelAttribute("StudyForm") StudyForm sForm, @RequestParam("type") final String type,
                         @RequestParam("varId") final long varId) {
        log.trace("Entering copyCellValue for [type {}; varId: {}]", () -> type, () -> varId);
        String gson;
        try {
            gson = new Gson().toJson(recordService.getVariableValues(varId));
        } catch (Exception e) {
            gson = "[]";
        }
        log.trace("Method copyCellValue completed with satus: {}", HttpStatus.OK::toString);
        return gson;
    }

    @RequestMapping(value = {"{recordId}/version/{versionId}/pasteCellValue"})
    public @ResponseBody
    ResponseEntity<String> pasteCellValue(@ModelAttribute("StudyForm") StudyForm sForm,
                                          @RequestParam("type") final String type, @RequestParam("varId") final long varId, @RequestParam("valContent") final String valContent) {
        log.trace("Entering pasteCellValue for [type {}; varId: {}]", () -> type, () -> varId);
        SPSSVarDTO var = sForm.getRecord().getVariables().parallelStream().filter(var_t -> var_t.getId() == varId).findFirst().orElse(null);
        List<SPSSValueLabelDTO> values = new Gson().fromJson(valContent, new TypeToken<ArrayList<SPSSValueLabelDTO>>() {
        }.getType());
        if (var != null && values != null) {
            var.setValues(values);
        }
        log.trace("Method pasteCellValue completed with satus: {}", HttpStatus.OK::toString);
        return new ResponseEntity<>("{}", HttpStatus.OK);
    }

    /**
     * This function sets the global missing entries from the missing modal into the StudyForm. It uses two StudyForms because the modal isn't included into the
     * HTML form of the codebook.jsp. Therefore, the values of the StudyForm from the missing modal (varval) have to be set into the original StudyForm.
     *
     * @param model  {@link ModelMap}
     * @param varVal {@link StudyForm}
     * @param sForm  {@link StudyForm}
     * @return Mapping to codebook.jsp
     */
    @RequestMapping(value = {"/{recordId}/version/{versionId}/codebook"}, params = "setGlobalMissings")
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
     * recordService.compareAndSaveRecord(...) is called. If the new codebook and the codebook which is stored in the database do not differ, nothing is saved.
     *
     * @param pid                Project Identifier as {@link Optional}&lt;{@link Long}&gt;
     * @param studyId            Study Identifier as {@link Optional}&lt;{@link Long}&gt;
     * @param versionId          Version Identifier as {@link Optional}&lt;{@link Long}&gt;
     * @param recordId           Record Identifier as {@link Optional}&lt;{@link Long}&gt;
     * @param sForm              {@link StudyForm}
     * @param redirectAttributes {@link RedirectAttributes}
     * @param model              {@link ModelMap}
     * @return If validation has errors or other errors occurred, returning to record.jsp with error messages, otherwise mapping to redirect/codebook with
     * success/info message
     */
    @RequestMapping(value = {"/{recordId}/version/{versionId}/codebook"}, method = RequestMethod.POST, params = "saveCodebook")
    public String saveCodebook(@PathVariable final long pid, @PathVariable final long studyId, @PathVariable long versionId,
                               @PathVariable long recordId, @ModelAttribute("StudyForm") StudyForm sForm, final RedirectAttributes redirectAttributes, final ModelMap model) {
        UserDTO user = UserUtil.getCurrentUser();
        log.trace("Entering saveCodebook for record [id: {}; current_version{}] and User [email: {}]", () -> recordId, () -> versionId, user::getEmail);
        String ret = projectService.checkUserAccess(pid, studyId, redirectAttributes, true, user);
        if (ret == null) {
            if (sForm.getPageLoadMin() < 1)
                sForm.setPageLoadMin(1);
            if (sForm.getPageLoadMax() > sForm.getRecord().getNumberOfVariables())
                sForm.setPageLoadMax(sForm.getRecord().getNumberOfVariables());
            RecordDTO currentVersion = sForm.getRecord();
            Set<String> parsingErrors = new HashSet<>();
            Set<String> parsingWarnings = new HashSet<>();
            String infoMSG;
            try {
                RecordDTO copy = (RecordDTO) ObjectCloner.deepCopy(currentVersion);
                recordService.validateAndPrepareCodebookForm(copy, parsingErrors, parsingWarnings, sForm.getNewChangeLog(), false, sForm.isIgnoreValidationErrors());
                currentVersion = copy;
                infoMSG = recordService.compareAndSaveRecord(currentVersion, sForm.getNewChangeLog(), pid);
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
                    log.fatal("Database Exception during saveCodebook - Code {}; Message: {}", e::getErrorCode, e::getMessage, () -> e);
                    model.put("errorMSG",
                            messageSource.getMessage("dbs.sql.exception",
                                    new Object[]{env.getRequiredProperty("organisation.admin.email"), e.getMessage().replaceAll("\n", "").replaceAll("\"", "\'")},
                                    LocaleContextHolder.getLocale()));
                } else {
                    log.debug("Parsing Exception during saveCodebook - Code {}; Message: {}", e::getErrorCode, e::getMessage);
                    model.put("errorCodeBookMSG", recordService.setMessageString(parsingErrors.parallelStream().collect(Collectors.toList())));
                }
                model.put("subnaviActive", PageState.RECORDVAR.name());
                model.put("recordSubMenu", true);
                ret = "codebook";
            } catch (ClassNotFoundException | IOException e) {
                log.fatal("ClassNotFoundException |  IOException during saveCodebook - Message:", () -> e);
                model.put("errorMSG",
                        messageSource.getMessage("global.error.internal",
                                new Object[]{env.getRequiredProperty("organisation.admin.email"), e.getMessage().replaceAll("\n", "").replaceAll("\"", "\'")},
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
