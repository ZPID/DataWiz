package de.zpid.datawiz.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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
import de.zpid.datawiz.service.RecordService;
import de.zpid.datawiz.service.StudyService;
import de.zpid.datawiz.util.BreadCrumpUtil;
import de.zpid.datawiz.util.ObjectCloner;
import de.zpid.datawiz.util.UserUtil;
import de.zpid.spss.dto.SPSSVarDTO;
import de.zpid.spss.util.SPSSMissing;
import de.zpid.spss.util.SPSSVarTypes;

@Controller
@RequestMapping(value = { "/project/{pid}/study/{studyId}/record" })
@SessionAttributes({ "StudyForm", "subnaviActive", "breadcrumpList" })
public class RecordController {

  private static Logger log = LogManager.getLogger(RecordController.class);

  @Autowired
  private RecordService recordService;
  @Autowired
  private StudyService studyService;
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

  public RecordController() {
    super();
    log.info("Loading RecordController for mapping /project/{pid}/study/{sid}/record");
  }

  /**
   * 
   * @param pid
   * @param studyId
   * @param recordId
   * @param versionId
   * @param model
   * @param redirectAttributes
   * @param subpage
   * @return
   */
  @RequestMapping(value = { "", "/{recordId}", "/{recordId}/{subpage}", "/{recordId}/version/{versionId}",
      "/{recordId}/version/{versionId}/{subpage}" }, method = RequestMethod.GET)
  public String showRecord(@PathVariable final Optional<Long> pid, @PathVariable final Optional<Long> studyId,
      @PathVariable final Optional<Long> recordId, @PathVariable final Optional<Long> versionId, final ModelMap model,
      final RedirectAttributes redirectAttributes, @PathVariable final Optional<String> subpage) {
    final UserDTO user = UserUtil.getCurrentUser();
    String ret;
    if (recordId.isPresent()) {
      log.trace("Entering showRecord(edit) for [recordId: {}; studyId {}; projectId {}]", () -> recordId.get(),
          () -> studyId.get(), () -> pid.get());
      ret = studyService.checkStudyAccess(pid, studyId, redirectAttributes, false, user);
    } else {
      log.trace("Entering showRecord(create) ");
      ret = studyService.checkStudyAccess(pid, studyId, redirectAttributes, true, user);
    }
    StudyForm sForm = null;
    List<String> parsingErrors = new ArrayList<String>();
    if (ret == null) {
      try {
        sForm = recordService.setStudyform(pid, studyId, recordId, versionId, subpage, parsingErrors);
      } catch (Exception e) {
        ret = exceptionService.setErrorMessagesAndRedirects(pid, studyId, recordId, model, redirectAttributes, e,
            "recordService.setStudyform");
      }
    }
    if (sForm != null && ret == null) {
      model.put("StudyForm", sForm);
      model.put("recordSubMenu", true);
      model.put("breadcrumpList", BreadCrumpUtil.generateBC(PageState.RECORDS,
          new String[] { sForm.getProject().getTitle(), sForm.getStudy().getTitle(),
              (sForm.getRecord() != null ? sForm.getRecord().getRecordName()
                  : messageSource.getMessage("record.new.record.breadcrump", null, LocaleContextHolder.getLocale())) },
          new long[] { pid.get(), studyId.get() }, messageSource));
      if (sForm.getPageLoadMin() == 0) {
        sForm.setPageLoadMin(1);
      }
      if (subpage.isPresent() && subpage.get().equals("codebook")) {
        model.put("subnaviActive", PageState.RECORDVAR.name());
        model.put("errorMSG", recordService.validateCodeBook(sForm));
        if (sForm.getPageLoadMax() == 0) {
          sForm.setPageLoadMax(
              sForm.getRecord().getNumberOfVariables() < 100 ? sForm.getRecord().getNumberOfVariables() : 100);
        }
        ret = "codebook";
        model.put("warnCodeBookMSG", recordService.setMessageString(sForm.getWarnings()));
      } else if (subpage.isPresent() && subpage.get().equals("data")) {
        model.put("subnaviActive", PageState.RECORDDATA.name());
        if (sForm.getPageLoadMax() == 0) {
          sForm.setPageLoadMax(
              sForm.getRecord().getNumberOfCases() < 100 ? (int) sForm.getRecord().getNumberOfCases() : 100);
        }
        ret = "datamatrix";
      } else {
        String errormsg = recordService.validateCodeBook(sForm);
        if (errormsg != null && !errormsg.trim().isEmpty()) {
          model.put("errorMSG",
              messageSource.getMessage("record.spss.export.disabled", null, LocaleContextHolder.getLocale()));
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
   * 
   * @param pid
   * @param studyId
   * @param recordId
   * @param model
   * @param redirectAttributes
   * @param sForm
   * @return
   */
  @RequestMapping(value = { "/{recordId}" }, method = RequestMethod.POST, params = { "upload" })
  public String uploadFile(@PathVariable final Optional<Long> pid, @PathVariable final Optional<Long> studyId,
      @PathVariable final Optional<Long> recordId, final ModelMap model, final RedirectAttributes redirectAttributes,
      @ModelAttribute("StudyForm") StudyForm sForm) {
    final UserDTO user = UserUtil.getCurrentUser();
    log.trace("Entering  uploadFile for [recordId: {}; studyId {}; projectId {}] user[id: {}; email: {}]",
        () -> recordId.get(), () -> studyId.get(), () -> pid.get(), () -> user.getId(), () -> user.getEmail());
    String ret = studyService.checkStudyAccess(pid, studyId, redirectAttributes, true, user);
    if (ret == null) {
      if (sForm.getNewChangeLog() == null || sForm.getNewChangeLog().isEmpty()) {
        if (log.isDebugEnabled())
          log.debug("New Changelog is Missing - return to jsp with message");
        redirectAttributes.addFlashAttribute("errorMSG",
            messageSource.getMessage("record.no.changelog", null, LocaleContextHolder.getLocale()));
        ret = "redirect:/project/" + pid.get() + "/study/" + studyId.get() + "/record/" + recordId.get();
      }
    }
    if (ret == null) {
      try {
        importService.importFile(pid, studyId, recordId, sForm, user);
        ret = "redirect:/project/" + pid.get() + "/study/" + studyId.get() + "/record/" + recordId.get()
            + "/importReport";
      } catch (DataWizSystemException e) {
        ret = exceptionService.setErrorMessagesAndRedirects(pid, studyId, recordId, model, redirectAttributes, e,
            "importService.importFile");
      }
    }
    if (log.isTraceEnabled())
      log.trace("Method uploadFile completed - mapping to {}", ret);
    return ret;
  }

  /**
   * 
   * @param pid
   * @param studyId
   * @param recordId
   * @param model
   * @param redirectAttributes
   * @param sForm
   * @return
   */
  @RequestMapping(value = { "/{recordId}/deleteRecord" }, method = RequestMethod.GET)
  public String deleteRecord(@PathVariable final Optional<Long> pid, @PathVariable final Optional<Long> studyId,
      @PathVariable final Optional<Long> recordId, final ModelMap model, final RedirectAttributes redirectAttributes,
      @ModelAttribute("StudyForm") StudyForm sForm) {
    final UserDTO user = UserUtil.getCurrentUser();
    log.trace("Entering  deleteRecord for [recordId: {}; studyId {}; projectId {}] user[id: {}; email: {}]",
        () -> recordId.get(), () -> studyId.get(), () -> pid.get(), () -> user.getId(), () -> user.getEmail());
    if(recordService.deleteRecord(pid, studyId, recordId, user)){      
      return "redirect:/project/" + pid.get() + "/study/" + studyId.get() + "/records";
    }
    else {
      model.put("subnaviActive", PageState.RECORDMETA.name());
      model.put("recordSubMenu", true);
      model.put("errorMSG",
          messageSource.getMessage("record.not.deleted.error", null, LocaleContextHolder.getLocale()));
      return "/record";
    }

  }

  /**
   * 
   * @param pid
   * @param studyId
   * @param recordId
   * @param model
   * @param redirectAttributes
   * @param sForm
   * @return
   */
  @RequestMapping(value = { "/{recordId}/importReport" }, method = RequestMethod.GET)
  public String showImportReport(@PathVariable final Optional<Long> pid, @PathVariable final Optional<Long> studyId,
      @PathVariable final Optional<Long> recordId, final ModelMap model, final RedirectAttributes redirectAttributes,
      @ModelAttribute("StudyForm") StudyForm sForm) {
    final UserDTO user = UserUtil.getCurrentUser();
    log.trace("Entering  showImportReport for [recordId: {}; studyId {}; projectId {}] user[id: {}; email: {}]",
        () -> recordId.get(), () -> studyId.get(), () -> pid.get(), () -> user.getId(), () -> user.getEmail());
    String ret = studyService.checkStudyAccess(pid, studyId, redirectAttributes, true, user);
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
   * 
   * @param pid
   * @param studyId
   * @param recordId
   * @param model
   * @param redirectAttributes
   * @param sForm
   * @return
   */
  @RequestMapping(value = { "/{recordId}" }, method = RequestMethod.POST)
  public String saveImport(@PathVariable final Optional<Long> pid, @PathVariable final Optional<Long> studyId,
      @PathVariable final Optional<Long> recordId, final ModelMap model, final RedirectAttributes redirectAttributes,
      @ModelAttribute("StudyForm") StudyForm sForm) {
    final UserDTO user = UserUtil.getCurrentUser();
    log.trace("Entering  saveImport for [recordId: {}; studyId {}; projectId {}] user[id: {}; email: {}]",
        () -> recordId.get(), () -> studyId.get(), () -> pid.get(), () -> user.getId(), () -> user.getEmail());
    String ret = studyService.checkStudyAccess(pid, studyId, redirectAttributes, true, user);
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
            redirectAttributes.addFlashAttribute("errorMSG",
                messageSource.getMessage("minio.connection.exception",
                    new Object[] { env.getRequiredProperty("organisation.admin.email"), e.getMessage() },
                    LocaleContextHolder.getLocale()));
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
   * @param pid
   * @param studyId
   * @param recordId
   * @param sForm
   * @param redirectAttributes
   * @param model
   * @return
   */
  @RequestMapping(value = { "", "/{recordId}", "/{recordId}/version/{versionId}" }, params = "saveMetaData")
  public String saveRecordMetaData(@PathVariable final Optional<Long> pid, @PathVariable final Optional<Long> studyId,
      @PathVariable final Optional<Long> recordId, @ModelAttribute("StudyForm") StudyForm sForm,
      final RedirectAttributes redirectAttributes, final ModelMap model) {
    final UserDTO user = UserUtil.getCurrentUser();
    if (recordId.isPresent())
      log.trace("Entering saveRecordMetaData(update) for [recordId: {}; studyId {}; projectId {}]",
          () -> recordId.get(), () -> studyId.get(), () -> pid.get());
    else
      log.trace("Entering saveRecordMetaData(create) for [studyId {}; projectId {}]", () -> studyId.get(),
          () -> pid.get());
    String ret = studyService.checkStudyAccess(pid, studyId, redirectAttributes, true, user);
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
        model.put("errorMSG", messageSource.getMessage("dbs.sql.exception",
            new Object[] { env.getRequiredProperty("organisation.admin.email") }, LocaleContextHolder.getLocale()));
        ret = "record";
      }
    }
    if (log.isTraceEnabled())
      log.trace("Method saveRecordMetaData completed - mapping to {}", ret);
    return ret;
  }

  /**
   * 
   * @param model
   * @param sForm
   * @param pid
   * @param studyId
   * @param recordId
   * @param varId
   * @param modal
   * @return
   */
  @RequestMapping(value = { "{recordId}/version/{versionId}/codebook/modal" })
  public String loadAjaxModal(final ModelMap model, @ModelAttribute("StudyForm") StudyForm sForm,
      @PathVariable final Long pid, @PathVariable final Long studyId, @PathVariable final Long recordId,
      @RequestParam(value = "varId", required = true) long varId,
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
   * 
   * @param model
   * @param varVal
   * @param sForm
   * @return
   */
  @RequestMapping(value = { "/{recordId}/version/{versionId}/codebook" }, params = "setValues")
  public String setValuesToStudyForm(final ModelMap model, @ModelAttribute("VarValues") SPSSVarDTO varVal,
      @ModelAttribute("StudyForm") StudyForm sForm) {
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
    if (log.isTraceEnabled())
      log.trace("Method setValuesToStudyForm completed - mapping to codebook");
    return "codebook";
  }

  /**
   * 
   * @param model
   * @param varVal
   * @param sForm
   * @return
   */
  @RequestMapping(value = { "/{recordId}/version/{versionId}/codebook" }, params = "setMissings")
  public String setMissingsToStudyForm(final ModelMap model, @ModelAttribute("VarValues") SPSSVarDTO varVal,
      @ModelAttribute("StudyForm") StudyForm sForm) {
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
    if (log.isTraceEnabled())
      log.trace("Method setMissingsToStudyForm completed - mapping to codebook");
    return "codebook";
  }

  /**
   * 
   * @param model
   * @param response
   * @param redirectAttributes
   * @param versionId
   * @param recordId
   * @param pid
   * @param studyId
   * @param exportType
   * @param attachments
   * @throws Exception
   */
  @RequestMapping(value = { "{recordId}/version/{versionId}/export/{exportType}" })
  public void exportRecord(final ModelMap model, HttpServletResponse response, RedirectAttributes redirectAttributes,
      @PathVariable long versionId, @PathVariable long recordId, @PathVariable final Optional<Long> pid,
      @PathVariable final Optional<Long> studyId, @PathVariable String exportType,
      @RequestParam(value = "attachments", required = false) Boolean attachments) throws Exception {
    log.trace("Entering exportRecord for [recordid: {}, version: {}, exporttye: {}] ", () -> recordId, () -> versionId,
        () -> exportType);
    UserDTO user = UserUtil.getCurrentUser();
    RecordDTO record = null;
    StringBuilder res = new StringBuilder();
    byte[] content = null;
    if (user == null || studyService.checkStudyAccess(pid, studyId, redirectAttributes, false, user) != null) {
      log.warn("Auth User Object empty or User is permitted to download this file");
      throw new DWDownloadException("export.access.denied");
    } else {
      try {
        record = recordService.loadRecordExportData(versionId, recordId, exportType, res);
      } catch (Exception e) {
        record = null;
        res.insert(0, "dbs.sql.exception");
        log.error("ERROR: Getting record from DB wasn't sucessful! Record[recordId:{}; VersionId:{}] Exception:",
            () -> recordId, () -> versionId, () -> e);
      }
      if (record != null) {
        try {
          content = exportService.getRecordExportContentAsByteArray(exportType, attachments, record, res);
        } catch (Exception e) {
          record = null;
          res.insert(0, "export.error.exception.thown");
          log.error("ERROR: Exception thrown at exportService.getRecordExportContentAsByteArray", () -> recordId,
              () -> versionId, () -> e);
        }
      }
      if (res.toString().trim().isEmpty() && record != null && content != null) {
        switch (exportType) {
        case "CSVMatrix":
          response.setContentType("text/csv");
          response.setHeader("Content-Disposition",
              "attachment; filename=\"" + record.getRecordName() + "_Matrix.csv\"");
          break;
        case "CSVCodebook":
          response.setContentType("text/csv");
          response.setHeader("Content-Disposition",
              "attachment; filename=\"" + record.getRecordName() + "_Codebook.csv\"");
          break;
        case "JSON":
          response.setContentType("application/json");
          response.setHeader("Content-Disposition", "attachment; filename=\"" + record.getRecordName() + ".json\"");
          break;
        case "SPSS":
          response.setContentType("application/sav");
          response.setHeader("Content-Disposition", "attachment; filename=\"" + record.getRecordName() + ".sav\"");
          break;
        case "PDF":
          response.setContentType("application/pdf");
          response.setHeader("Content-Disposition", "attachment; filename=\"" + record.getRecordName() + ".pdf\"");
          break;
        case "CSVZIP":
          response.setContentType("application/zip");
          response.setHeader("Content-Disposition", "attachment; filename=\"" + record.getRecordName() + ".zip\"");
          break;
        }
        response.setContentLength(content.length);
        response.getOutputStream().write(content);
        response.flushBuffer();
        if (log.isTraceEnabled())
          log.trace("Method exportRecord completed successfully");
      } else {
        log.warn("Method exportRecord completed with an error - DWDownloadException thrown: ", res.toString());
        throw new DWDownloadException(res.toString());
      }
    }
  }

  /**
   * 
   * @param model
   * @param sForm
   * @return
   */
  @RequestMapping(value = {
      "/{recordId}/version/{versionId}/{pagestate}" }, method = RequestMethod.POST, params = "setNumofVars")
  public String setNumofVars(final ModelMap model, @ModelAttribute("StudyForm") StudyForm sForm,
      @PathVariable final Optional<Long> pid, @PathVariable final Optional<Long> studyId,
      @PathVariable final Optional<Long> recordId, @PathVariable final String pagestate) {
    log.trace("Entering setNumofVars");
    if (sForm == null || sForm.getRecord() == null || sForm.getRecord().getId() == 0) {
      log.warn(
          "Setting setNumofVars failed - (sForm == null || sForm.getRecord() == null || sForm.getRecord().getId() == 0)");
    }
    model.put("errorMSG", recordService.validateCodeBook(sForm));
    model.put("warnCodeBookMSG", recordService.setMessageString(sForm.getWarnings()));
    model.put("recordSubMenu", true);
    model.put("breadcrumpList", BreadCrumpUtil.generateBC(PageState.RECORDS,
        new String[] { sForm.getProject().getTitle(), sForm.getStudy().getTitle(),
            (sForm.getRecord() != null ? sForm.getRecord().getRecordName()
                : messageSource.getMessage("record.new.record.breadcrump", null, LocaleContextHolder.getLocale())) },
        new long[] { pid.get(), studyId.get() }, messageSource));
    if (pagestate.equals("codebook")) {
      if (sForm.getPageLoadMin() < 1)
        sForm.setPageLoadMin(1);
      if (sForm.getPageLoadMax() > sForm.getRecord().getNumberOfVariables())
        sForm.setPageLoadMax(sForm.getRecord().getNumberOfVariables());
      model.put("subnaviActive", PageState.RECORDVAR.name());
      return "codebook";
    } else {
      if (sForm.getPageLoadMin() < 1)
        sForm.setPageLoadMin(1);
      if (sForm.getPageLoadMax() > sForm.getRecord().getNumberOfCases())
        sForm.setPageLoadMax((int) sForm.getRecord().getNumberOfCases());
      model.put("subnaviActive", PageState.RECORDDATA.name());
      return "datamatrix";
    }
  }

  /**
   * 
   * @param model
   * @param sForm
   * @return
   */
  @RequestMapping(value = { "{recordId}/version/{versionId}/asyncSubmit" })
  public @ResponseBody ResponseEntity<String> setFormAsync(final ModelMap model,
      @ModelAttribute("StudyForm") StudyForm sForm) {
    log.trace("Entering setFormAsync");
    if (sForm == null || sForm.getRecord() == null || sForm.getRecord().getId() == 0) {
      log.warn(
          "Setting Variables Async failed - (sForm == null || sForm.getRecord() == null || sForm.getRecord().getId() == 0)");
      return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
    }
    return new ResponseEntity<String>("{}", HttpStatus.OK);
  }

  /**
   * 
   * @param model
   * @param varVal
   * @param sForm
   * @return
   */
  @RequestMapping(value = { "/{recordId}/version/{versionId}/codebook" }, params = "setGlobalMissings")
  public String setGlobalMissingsToStudyForm(final ModelMap model, @ModelAttribute("VarValues") StudyForm varVal,
      @ModelAttribute("StudyForm") StudyForm sForm) {
    log.trace("Entering setGlobalMissings for record [id: {}]", () -> sForm.getRecord().getId());
    List<SPSSVarDTO> missings = varVal.getViewVars();
    for (SPSSVarDTO var : sForm.getRecord().getVariables()) {
      if (!missings.get(0).getMissingFormat().equals(SPSSMissing.SPSS_UNKNOWN)
          && RecordDTO.simplifyVarTypes(var.getType()).equals(SPSSVarTypes.SPSS_FMT_A)) {
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
    if (log.isTraceEnabled())
      log.trace("Method setGlobalMissingsToStudyForm completed - mapping to codebook");
    return "codebook";
  }

  /**
   * 
   * @param pid
   * @param studyId
   * @param versionId
   * @param recordId
   * @param sForm
   * @param redirectAttributes
   * @return
   */
  @RequestMapping(value = {
      "/{recordId}/version/{versionId}/codebook" }, method = RequestMethod.POST, params = "saveCodebook")
  public String saveCodebook(@PathVariable final Optional<Long> pid, @PathVariable final Optional<Long> studyId,
      @PathVariable long versionId, @PathVariable long recordId, @ModelAttribute("StudyForm") StudyForm sForm,
      final RedirectAttributes redirectAttributes, final ModelMap model) {
    UserDTO user = UserUtil.getCurrentUser();
    log.trace("Entering saveCodebook for record [id: {}; current_version{}] and User [email: {}]", () -> recordId,
        () -> versionId, () -> user.getEmail());
    String ret = studyService.checkStudyAccess(pid, studyId, redirectAttributes, true, user);
    if (ret == null) {
      if (sForm.getPageLoadMin() < 1)
        sForm.setPageLoadMin(1);
      if (sForm.getPageLoadMax() > sForm.getRecord().getNumberOfVariables())
        sForm.setPageLoadMax(sForm.getRecord().getNumberOfVariables());
      RecordDTO currentVersion = sForm.getRecord();
      Set<String> parsingErrors = new HashSet<String>();
      Set<String> parsingWarings = new HashSet<String>();
      String infoMSG = null;
      try {
        RecordDTO copy = (RecordDTO) ObjectCloner.deepCopy(currentVersion);
        recordService.validateAndPrepareCodebookForm(copy, parsingErrors, parsingWarings, sForm.getNewChangeLog(),
            false);
        currentVersion = copy;
        infoMSG = recordService.compareAndSaveCodebook(currentVersion, parsingErrors, sForm.getNewChangeLog());
        if (infoMSG.equals("record.codebook.saved"))
          redirectAttributes.addFlashAttribute("successMSG",
              messageSource.getMessage(infoMSG, null, LocaleContextHolder.getLocale()));
        else {
          redirectAttributes.addFlashAttribute("infoMSG",
              messageSource.getMessage(infoMSG, null, LocaleContextHolder.getLocale()));
        }
        ret = "redirect:/project/" + sForm.getProject().getId() + "/study/" + sForm.getStudy().getId() + "/record/"
            + currentVersion.getId() + "/version/" + currentVersion.getVersionId() + "/codebook";
      } catch (DataWizSystemException e) {
        model.put("infoMSG",
            messageSource.getMessage("record.codebook.not.saved", null, LocaleContextHolder.getLocale()));
        model.put("warnCodeBookMSG", recordService.setMessageString(sForm.getWarnings()));
        if (e.getErrorCode().equals(DataWizErrorCodes.DATABASE_ERROR)) {
          log.fatal("Database Exception during saveCodebook - Code {}; Message: {}", () -> e.getErrorCode(),
              () -> e.getMessage(), () -> e);
          model
              .put("errorMSG",
                  messageSource.getMessage("dbs.sql.exception",
                      new Object[] { env.getRequiredProperty("organisation.admin.email"),
                          e.getMessage().replaceAll("\n", "").replaceAll("\"", "\'") },
                      LocaleContextHolder.getLocale()));
        } else {
          log.debug("Parsing Exception during saveCodebook - Code {}; Message: {}", () -> e.getErrorCode(),
              () -> e.getMessage());
          model.put("errorMSG",
              recordService.setMessageString(parsingErrors.parallelStream().collect(Collectors.toList())));
        }
        model.put("subnaviActive", PageState.RECORDVAR.name());
        model.put("recordSubMenu", true);
        ret = "codebook";
      } catch (ClassNotFoundException | IOException e) {
        log.fatal("ClassNotFoundException |  IOException during saveCodebook - Message:", () -> e);
        model
            .put("errorMSG",
                messageSource
                    .getMessage("global.error.internal",
                        new Object[] { env.getRequiredProperty("organisation.admin.email"),
                            e.getMessage().replaceAll("\n", "").replaceAll("\"", "\'") },
                        LocaleContextHolder.getLocale()));
        model.put("subnaviActive", PageState.RECORDVAR.name());
        model.put("recordSubMenu", true);
        ret = "codebook";
      }
    }
    if (log.isTraceEnabled())
      log.trace("Method saveCodebook completed - mapping to {}", ret);
    return ret;
  }

}
