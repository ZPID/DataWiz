package de.zpid.datawiz.controller;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.AbstractMap.SimpleEntry;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import de.zpid.datawiz.dto.FileDTO;
import de.zpid.datawiz.dto.RecordCompareDTO;
import de.zpid.datawiz.dto.RecordDTO;
import de.zpid.datawiz.dto.UserDTO;
import de.zpid.datawiz.enumeration.DWFieldTypes;
import de.zpid.datawiz.enumeration.MinioResult;
import de.zpid.datawiz.enumeration.PageState;
import de.zpid.datawiz.enumeration.VariableStatus;
import de.zpid.datawiz.exceptions.DWDownloadException;
import de.zpid.datawiz.form.StudyForm;
import de.zpid.datawiz.util.BreadCrumpUtil;
import de.zpid.datawiz.util.ExportUtil;
import de.zpid.datawiz.util.ITextUtil;
import de.zpid.datawiz.util.ImportUtil;
import de.zpid.datawiz.util.UserUtil;
import de.zpid.spss.dto.SPSSValueLabelDTO;
import de.zpid.spss.dto.SPSSVarDTO;
import de.zpid.spss.util.SPSSVarTypes;

@Controller
@RequestMapping(value = { "/record", "/project/{pid}/study/{studyId}/record" })
@SessionAttributes({ "StudyForm", "subnaviActive", "breadcrumpList" })
public class RecordController extends SuperController {

  private static Logger log = LogManager.getLogger(RecordController.class);

  @Autowired
  private PlatformTransactionManager txManager;
  @Autowired
  private ImportUtil importUtil;
  @Autowired
  private ExportUtil exportUtil;
  @Autowired
  private ITextUtil itextUtil;

  public RecordController() {
    super();
    log.info("Loading RecordController for mapping /project/{pid}/study/{sid}/record");
  }

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
      ret = checkStudyAccess(pid, studyId, redirectAttributes, false, user);
    } else {
      log.trace("Entering showRecord(create) ");
      ret = checkStudyAccess(pid, studyId, redirectAttributes, true, user);
    }
    if (ret != null)
      return ret;
    StudyForm sForm = createStudyForm();
    try {
      sForm.setProject(projectDAO.findById(pid.get()));
      if (sForm.getProject() == null) {
        log.warn("No Project found for projectId {}", () -> pid.get());
        redirectAttributes.addFlashAttribute("errorMSG",
            messageSource.getMessage("project.not.available", null, LocaleContextHolder.getLocale()));
        return "redirect:/panel";
      }
      sForm.setStudy(studyDAO.findById(studyId.get(), pid.get(), true, false));
      if (sForm.getStudy() == null) {
        log.warn("No Study found for studyId {}", () -> studyId.get());
        redirectAttributes.addFlashAttribute("errorMSG",
            messageSource.getMessage("record.not.available", null, LocaleContextHolder.getLocale()));
        return "redirect:/project/" + pid.get() + "/studies";
      }
      if (subpage.isPresent() && subpage.get().equals("codebook")) {
        sForm.getStudy().setConstructs(studyConstructDAO.findAllByStudy(studyId.get()));
        sForm.getStudy().setMeasOcc(studyListTypesDAO.findAllByStudyAndType(studyId.get(), DWFieldTypes.MEASOCCNAME));
        sForm.getStudy().setInstruments(studyInstrumentDAO.findAllByStudy(studyId.get(), true));
      }
      if (recordId.isPresent()) {
        RecordDTO rec = (recordDAO.findRecordWithID(recordId.get(),
            (versionId.isPresent() && versionId.get() > 0 ? versionId.get() : 0)));
        if (rec != null) {
          rec.setVariables(recordDAO.findVariablesByVersionID(rec.getVersionId()));
          rec.setAttributes(recordDAO.findRecordAttributes(rec.getVersionId(), true));
          if (rec.getVariables() != null && rec.getVariables().size() > 0) {
            for (SPSSVarDTO var : rec.getVariables()) {
              var.setAttributes(recordDAO.findVariableAttributes(var.getId(), false));
              importUtil.sortVariableAttributes(var);
              if (subpage.isPresent() && subpage.get().equals("codebook"))
                var.setValues(recordDAO.findVariableValues(var.getId(), true));
              else
                var.setValues(recordDAO.findVariableValues(var.getId(), false));
            }
          }
          rec.setDataMatrixJson(recordDAO.findMatrixByVersionId(rec.getVersionId()));
          if (rec.getDataMatrixJson() != null && !rec.getDataMatrixJson().isEmpty())
            rec.setDataMatrix(new Gson().fromJson(rec.getDataMatrixJson(), new TypeToken<List<List<Object>>>() {
            }.getType()));
        } else {
          log.warn("No Record found for recordId {}", () -> recordId.get());
          redirectAttributes.addFlashAttribute("errorMSG",
              messageSource.getMessage("record.not.available", null, LocaleContextHolder.getLocale()));
          return "redirect:/project/" + pid.get() + "/study/" + studyId.get() + "/records";
        }
        sForm.setRecord(rec);
      }
    } catch (Exception e) {
      // TODO: handle exception
    }
    model.put("breadcrumpList",
        BreadCrumpUtil.generateBC(PageState.RECORDS,
            new String[] { sForm.getProject().getTitle(), sForm.getStudy().getTitle(),
                (sForm.getRecord() != null ? sForm.getRecord().getRecordName() : "TEST") },
            new long[] { pid.get(), studyId.get() }, messageSource));
    model.put("StudyForm", sForm);
    model.put("recordSubMenu", true);
    log.trace("Method showRecord successfully completed");
    // sForm.getPreviousRecordVersion().getAttributes().forEach((s) -> System.out.println(s));
    if (subpage.isPresent() && subpage.get().equals("codebook")) {
      model.put("subnaviActive", PageState.RECORDVAR.name());
      return "codebook";
    } else if (subpage.isPresent() && subpage.get().equals("data")) {
      model.put("subnaviActive", PageState.RECORDDATA.name());
      return "datamatrix";
    } else {
      model.put("subnaviActive", PageState.RECORDMETA.name());
      return "record";
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
  @RequestMapping(value = { "/{recordId}" }, method = RequestMethod.POST, params = { "upload" })
  public String uploadFile(@PathVariable final Optional<Long> pid, @PathVariable final Optional<Long> studyId,
      @PathVariable final Optional<Long> recordId, final ModelMap model, final RedirectAttributes redirectAttributes,
      @ModelAttribute("StudyForm") StudyForm sForm) {
    final UserDTO user = UserUtil.getCurrentUser();
    log.trace("Entering  uploadFile for [recordId: {}; studyId {}; projectId {}] user[id: {}; email: {}]",
        () -> recordId.get(), () -> studyId.get(), () -> pid.get(), () -> user.getId(), () -> user.getEmail());
    String ret = checkStudyAccess(pid, studyId, redirectAttributes, true, user);
    if (ret != null)
      return ret;
    if (sForm.getNewChangeLog() == null || sForm.getNewChangeLog().isEmpty()) {
      log.debug("New Changelog is Missing - return to jsp with message");
      redirectAttributes.addFlashAttribute("errorMSG",
          messageSource.getMessage("record.no.changelog", null, LocaleContextHolder.getLocale()));
      return "redirect:/project/" + pid.get() + "/study/" + studyId.get() + "/record/" + recordId.get();
    }
    // SPSS FILE
    List<String> warnings = new ArrayList<>();
    List<String> errors = new ArrayList<>();
    boolean error = false;
    if (sForm.getSelectedFileType() != null && sForm.getSelectedFileType().equals("SPSS") && sForm.getSpssFile() != null
        && sForm.getSpssFile().getSize() > 0) {
      error = importUtil.validateSPSSFile(pid, studyId, recordId, sForm, user, errors);
    } // CSV File
    else if (sForm.getSelectedFileType() != null && sForm.getSelectedFileType().equals("CSV")
        && sForm.getCsvFile() != null && sForm.getCsvFile().getSize() > 0) {
      error = importUtil.validateCSVFile(pid, studyId, recordId, sForm, user, warnings, errors);
    } else {

    }
    sForm.setParsingError(error);
    sForm.setErrors(errors);
    if (!error)
      sForm.setWarnings(warnings);
    return "redirect:/project/" + pid.get() + "/study/" + studyId.get() + "/record/" + recordId.get() + "/importReport";
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
    String ret = checkStudyAccess(pid, studyId, redirectAttributes, true, user);
    if (ret != null)
      return ret;
    try {
      RecordDTO lastVersion = recordDAO.findRecordWithID(recordId.get(), 0);
      List<SPSSVarDTO> vars = recordDAO.findVariablesByVersionID(lastVersion.getVersionId());
      for (SPSSVarDTO var : vars) {
        var.setAttributes(recordDAO.findVariableAttributes(var.getId(), false));
        var.setValues(recordDAO.findVariableValues(var.getId(), false));
        importUtil.sortVariableAttributes(var);
      }
      lastVersion.setVariables(vars);
      sForm.setPreviousRecordVersion(lastVersion);
      importUtil.compareVarVersion(sForm);
    } catch (Exception e) {
      // TODO
      e.printStackTrace();
    }
    return "importRep";
  }

  @RequestMapping(value = { "/{recordId}" }, method = RequestMethod.POST)
  public String save(@PathVariable final Optional<Long> pid, @PathVariable final Optional<Long> studyId,
      @PathVariable final Optional<Long> recordId, final ModelMap model, final RedirectAttributes redirectAttributes,
      @ModelAttribute("StudyForm") StudyForm sForm) {
    log.trace("Entering  save for [recordId: {}; studyId {}; projectId {}]", () -> recordId.get(), () -> studyId.get(),
        () -> pid.get());
    List<RecordCompareDTO> compList = sForm.getCompList();
    List<SPSSVarDTO> newVars = sForm.getRecord().getVariables();
    List<SPSSVarDTO> prevVars = sForm.getPreviousRecordVersion().getVariables();
    Boolean CSV = sForm.getSelectedFileType() == null ? false
        : sForm.getSelectedFileType().equals("CSV") ? true : false;
    if (compList != null) {
      int position = 0;
      for (RecordCompareDTO comp : compList) {
        SPSSVarDTO newVar = null, prevVar = null;
        if (comp.getVarStatus().equals(VariableStatus.EQUAL) || comp.getVarStatus().equals(VariableStatus.EQUAL_CSV)) {
          newVars.get(position).setId(prevVars.get(position).getId());
        } else if (comp.getVarStatus().equals(VariableStatus.MOVED)
            || comp.getVarStatus().equals(VariableStatus.MOVED_CSV)
            || comp.getVarStatus().equals(VariableStatus.MOVED)) {
          newVars.get(position).setId(prevVars.get(comp.getMovedFrom() - 1).getId());
        } else if (comp.getVarStatus().equals(VariableStatus.MOVED_AND_META_CHANGED)
            || comp.getVarStatus().equals(VariableStatus.MOVED_AND_META_CHANGED_CSV)
            || comp.getVarStatus().equals(VariableStatus.MOVED_AND_TYPE_CHANGED)) {
          newVar = newVars.get(position);
          prevVar = prevVars.get(comp.getMovedFrom() - 1);
        } else {
          newVar = newVars.get(position);
          prevVar = position < prevVars.size() ? prevVars.get(position) : null;
        }

        if (comp.isKeepExpMeta() && newVar != null && prevVar != null) {
          if (CSV) {
            newVar.setLabel(prevVar.getLabel());
            newVar.setMissingFormat(prevVar.getMissingFormat());
            newVar.setMissingVal1(prevVar.getMissingVal1());
            newVar.setMissingVal2(prevVar.getMissingVal2());
            newVar.setMissingVal3(prevVar.getMissingVal3());
            newVar.setColumns(prevVar.getColumns());
            newVar.setAligment(prevVar.getAligment());
            newVar.setMeasureLevel(prevVar.getMeasureLevel());
            newVar.setRole(prevVar.getRole());
            newVar.setNumOfAttributes(prevVar.getNumOfAttributes());
            newVar.setValues(prevVar.getValues());
            newVar.setAttributes(prevVar.getAttributes());
          } else {
            if (prevVar.getDw_attributes() != null)
              removeEmptyDWAttributes(prevVar.getDw_attributes());
            newVar.getAttributes().addAll(prevVar.getDw_attributes());
          }
        } else if (!comp.isKeepExpMeta() && !CSV && newVar != null) {
          if (newVar.getDw_attributes() != null)
            removeEmptyDWAttributes(newVar.getDw_attributes());
          if (newVar.getAttributes() == null)
            newVar.setAttributes(new LinkedList<>());
          newVar.getAttributes().addAll(newVar.getDw_attributes());
        }
        position++;
      }
    }
    saveRecordToDBAndMinio(sForm);
    return "redirect:/project/" + pid.get() + "/study/" + studyId.get() + "/record/" + recordId.get();
  }

  @RequestMapping(value = { "", "/{recordId}" }, params = "saveMetaData")
  private String saveRecordMetaData(@PathVariable final Optional<Long> pid, @PathVariable final Optional<Long> studyId,
      @PathVariable final Optional<Long> recordId, @ModelAttribute("StudyForm") StudyForm sForm,
      final RedirectAttributes redirectAttributes, final ModelMap model) {
    final UserDTO user = UserUtil.getCurrentUser();
    if (recordId.isPresent())
      log.trace("Entering saveRecordMetaData(update) for [recordId: {}; studyId {}; projectId {}]",
          () -> recordId.get(), () -> studyId.get(), () -> pid.get());
    else
      log.trace("Entering saveRecordMetaData(create) for [studyId {}; projectId {}]", () -> studyId.get(),
          () -> pid.get());
    String ret = checkStudyAccess(pid, studyId, redirectAttributes, true, user);
    if (ret != null)
      return ret;
    if (sForm.getRecord().getRecordName() != null && !sForm.getRecord().getRecordName().isEmpty()) {
      try {
        if (!recordId.isPresent() && sForm.getRecord().getId() <= 0) {
          sForm.getRecord().setCreatedBy(user.getEmail());
          sForm.getRecord().setStudyId(studyId.get());
          recordDAO.insertRecordMetaData(sForm.getRecord());
        } else {
          recordDAO.updateRecordMetaData(sForm.getRecord());
        }
      } catch (Exception e) {
        log.error("ERROR: Saving record to DB wasn't sucessful! Exception:", () -> e);
        redirectAttributes.addFlashAttribute("errorMSG", messageSource.getMessage("dbs.sql.exception",
            new Object[] { env.getRequiredProperty("organisation.admin.email") }, LocaleContextHolder.getLocale()));
      }
    } else {
      redirectAttributes.addFlashAttribute("errorMSG",
          messageSource.getMessage("record.name.missing", null, LocaleContextHolder.getLocale()));
    }
    return "redirect:/project/" + pid.get() + "/study/" + studyId.get() + "/record/" + sForm.getRecord().getId();
  }

  private void removeEmptyDWAttributes(List<SPSSValueLabelDTO> attributes) {
    Iterator<SPSSValueLabelDTO> itt = attributes.iterator();
    while (itt.hasNext()) {
      SPSSValueLabelDTO att = itt.next();
      if (att.getValue() == null || att.getValue().trim().isEmpty())
        itt.remove();
    }
  }

  @RequestMapping(value = { "{recordId}/version/{versionId}/modal" })
  public String loadAjaxModal(final ModelMap model, @ModelAttribute("StudyForm") StudyForm sForm,
      @PathVariable final Long pid, @PathVariable final Long studyId, @PathVariable final Long recordId,
      @RequestParam(value = "varId", required = true) long varId,
      @RequestParam(value = "modal", required = true) String modal) {
    log.trace("loadAjaxModal [{}] for variable [id: {}]", () -> modal, () -> varId);
    String ret = "forms/codebookModalContent";
    if (sForm == null || sForm.getRecord() == null) {
      log.warn("WARN: StudyForm is empty - Session timed out");
      model.put("modalPid", pid);
      model.put("modalStudyId", studyId);
      model.put("modalRecordId", recordId);
      return "forms/modalError";
    }
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
    return ret;
  }

  @RequestMapping(value = { "/{recordId}/version/{versionId}/codebook" }, params = "setValues")
  public String setValues(final ModelMap model, @ModelAttribute("VarValues") SPSSVarDTO varVal,
      @ModelAttribute("StudyForm") StudyForm sForm) {
    log.trace("setValues for variable [id: {}]", () -> varVal.getId());
    Iterator<SPSSValueLabelDTO> itt = varVal.getValues().iterator();
    while (itt.hasNext()) {
      SPSSValueLabelDTO val = itt.next();
      if ((val.getLabel() == null || val.getLabel().trim().isEmpty())
          && (val.getValue() == null || val.getValue().trim().isEmpty())) {
        itt.remove();
      }
    }
    if (varVal.getId() > 0) {
      for (SPSSVarDTO var : sForm.getRecord().getVariables()) {
        if (var.getId() == varVal.getId()) {
          var.setValues(varVal.getValues());
          model.remove("VarValues");
          break;
        }
      }
    } else if (varVal.getId() == -1) {
      for (SPSSVarDTO var : sForm.getRecord().getVariables()) {
        List<SPSSValueLabelDTO> global = new ArrayList<>();
        boolean setGlobal = false;
        for (SPSSValueLabelDTO val : varVal.getValues()) {
          SPSSValueLabelDTO newVal = new SPSSValueLabelDTO();
          newVal.setLabel(val.getLabel());
          newVal.setValue(val.getValue());
          SPSSVarTypes type = SPSSVarTypes.fromInt((int) val.getId());
          if (type.equals(SPSSVarTypes.SPSS_FMT_A)
              && RecordDTO.simplifyVarTypes(var.getType()).equals(SPSSVarTypes.SPSS_FMT_A)) {
            setGlobal = true;
            global.add(newVal);
          } else if (type.equals(SPSSVarTypes.SPSS_FMT_F)
              && RecordDTO.simplifyVarTypes(var.getType()).equals(SPSSVarTypes.SPSS_FMT_F)) {
            setGlobal = true;
            global.add(newVal);
          } else if (type.equals(SPSSVarTypes.SPSS_FMT_DATE)
              && RecordDTO.simplifyVarTypes(var.getType()).equals(SPSSVarTypes.SPSS_FMT_DATE)) {
            setGlobal = true;
            global.add(newVal);
          }
        }
        if (setGlobal)
          var.setValues(global);
      }
      model.remove("VarValues");
    }
    model.put("recordSubMenu", true);
    model.put("subnaviActive", PageState.RECORDVAR.name());
    return "codebook";
  }

  @RequestMapping(value = { "/{recordId}" }, params = "setType")
  public String setType(final ModelMap model, @ModelAttribute("VarValues") SPSSVarDTO varVal,
      @ModelAttribute("StudyForm") StudyForm sForm) {
    log.trace("setType for variable [id: {}]", () -> varVal.getId());
    model.put("recordSubMenu", true);
    model.put("subnaviActive", PageState.RECORDVAR.name());
    System.out.println(varVal.getType());
    return "codebook";
  }

  @RequestMapping(value = { "/{recordId}/version/{versionId}/codebook" }, params = "setMissings")
  public String setMissings(final ModelMap model, @ModelAttribute("VarValues") SPSSVarDTO varVal,
      @ModelAttribute("StudyForm") StudyForm sForm) {
    log.trace("setMissings for variable [id: {}]", () -> varVal.getId());
    model.put("recordSubMenu", true);
    model.put("subnaviActive", PageState.RECORDVAR.name());
    for (SPSSVarDTO var : sForm.getRecord().getVariables()) {
      if (var.getId() == varVal.getId()) {
        var.setMissingFormat(varVal.getMissingFormat());
        switchMissingType(varVal, var);
        model.remove("VarValues");
        break;
      }
    }
    return "codebook";
  }

  @RequestMapping(value = { "{recordId}/version/{versionId}/export/{exportType}" })
  public void exportRecord(final ModelMap model, HttpServletResponse response, RedirectAttributes redirectAttributes,
      @PathVariable long versionId, @PathVariable long recordId, @PathVariable final Optional<Long> pid,
      @PathVariable final Optional<Long> studyId, @PathVariable String exportType,
      @RequestParam(value = "attachments", required = false) Boolean attachments) throws Exception {
    log.trace("exportRecord - " + recordId + " - " + versionId + " - " + exportType);
    UserDTO user = UserUtil.getCurrentUser();
    RecordDTO record = null;
    byte[] content = null;
    StringBuilder res = new StringBuilder();
    if (user == null || checkStudyAccess(pid, studyId, redirectAttributes, false, user) != null) {
      log.warn("Auth User Object == null - redirect to login");
      // TODO
      res.insert(0, "project.access.denied");
    } else {
      try {
        record = recordDAO.findRecordWithID(recordId, versionId);
        record.setDataMatrixJson(recordDAO.findMatrixByVersionId(versionId));
        record.setVariables(recordDAO.findVariablesByVersionID(versionId));
        record.setAttributes(recordDAO.findRecordAttributes(versionId, true));
        record.setErrors(null);
        for (SPSSVarDTO var : record.getVariables()) {
          var.setAttributes(recordDAO.findVariableAttributes(var.getId(), true));
          if (!exportType.equals("SPSS"))
            importUtil.sortVariableAttributes(var);
          var.setValues(recordDAO.findVariableValues(var.getId(), true));
        }
        if (record.getDataMatrixJson() != null && !record.getDataMatrixJson().isEmpty()) {
          record.setDataMatrix(new Gson().fromJson(record.getDataMatrixJson(), new TypeToken<List<List<Object>>>() {
          }.getType()));
          record.setDataMatrixJson(null);
        }
      } catch (Exception e) {
        record = null;
        res.insert(0, "dbs.sql.exception");
        log.error("ERROR: Getting record from DB wasn't sucessful! Record[recordId:{}; VersionId:{}] Exception:",
            () -> recordId, () -> versionId, () -> e);
      }
      if (record != null) {
        if (exportType.equals("CSVMatrix")) {
          content = exportUtil.exportCSV(record, res, true);
        } else if (exportType.equals("CSVCodebook")) {
          content = exportUtil.exportCSV(record, res, false);
        } else if (exportType.equals("JSON")) {
          content = exportUtil.exportJSON(record, res);
        } else if (exportType.equals("SPSS")) {
          content = exportUtil.exportSPSSFile(record, res);
        } else if (exportType.equals("PDF")) {
          System.out.println(attachments);
          content = itextUtil.createPdf(record, false, attachments);
        } else if (exportType.equals("CSVZIP")) {
          List<Entry<String, byte[]>> files = new ArrayList<>();
          files.add(new SimpleEntry<String, byte[]>(record.getRecordName() + "_Matrix.csv",
              exportUtil.exportCSV(record, res, true)));
          files.add(new SimpleEntry<String, byte[]>(record.getRecordName() + "_Codebook.csv",
              exportUtil.exportCSV(record, res, false)));
          content = exportUtil.exportZip(files, res);
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
      } else {
        throw new DWDownloadException(res.toString());
      }
    }
  }

  @RequestMapping(value = { "{recordId}/version/{versionId}/asyncSubmit" })
  public @ResponseBody ResponseEntity<String> setFormAsync(final ModelMap model,
      @ModelAttribute("StudyForm") StudyForm sForm) {
    log.trace("setFormAsync");
    if (sForm == null || sForm.getRecord() == null || sForm.getRecord().getId() == 0) {
      log.warn(
          "Setting Variables Async failed - (sForm == null || sForm.getRecord() == null || sForm.getRecord().getId() == 0)");
      // TODO throw exception
      return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
    }
    return new ResponseEntity<String>("{}", HttpStatus.OK);
  }

  @RequestMapping(value = { "/{recordId}/version/{versionId}/codebook" }, params = "setGlobalMissings")
  public String setGlobalMissings(final ModelMap model, @ModelAttribute("VarValues") StudyForm varVal,
      @ModelAttribute("StudyForm") StudyForm sForm) {
    log.trace("setGlobalMissings for record [id: {}]", () -> sForm.getRecord().getId());
    model.put("recordSubMenu", true);
    model.put("subnaviActive", PageState.RECORDVAR.name());
    List<SPSSVarDTO> missings = varVal.getViewVars();
    for (SPSSVarDTO var : sForm.getRecord().getVariables()) {
      if (RecordDTO.simplifyVarTypes(var.getType()).equals(SPSSVarTypes.SPSS_FMT_A)) {
        var.setMissingFormat(missings.get(0).getMissingFormat());
        switchMissingType(missings.get(0), var);
      } else if (RecordDTO.simplifyVarTypes(var.getType()).equals(SPSSVarTypes.SPSS_FMT_F)) {
        var.setMissingFormat(missings.get(1).getMissingFormat());
        switchMissingType(missings.get(1), var);
      } else if (RecordDTO.simplifyVarTypes(var.getType()).equals(SPSSVarTypes.SPSS_FMT_DATE)) {
        var.setMissingFormat(missings.get(2).getMissingFormat());
        switchMissingType(missings.get(2), var);
      }
    }
    return "codebook";
  }

  @RequestMapping(value = { "/{recordId}/version/{versionId}" }, method = RequestMethod.POST, params = "saveCodebook")
  public String saveCodebook(@PathVariable final Optional<Long> pid, @PathVariable final Optional<Long> studyId,
      @PathVariable long versionId, @PathVariable long recordId, @ModelAttribute("StudyForm") StudyForm sForm,
      final RedirectAttributes redirectAttributes) {
    UserDTO user = UserUtil.getCurrentUser();
    log.trace("Entering saveCodebook for record [id: {}; current_version{}] and User [email: {}]", () -> recordId,
        () -> versionId, () -> user.getEmail());
    String ret = checkStudyAccess(pid, studyId, redirectAttributes, true, user);
    if (ret != null)
      return ret;
    RecordDTO lastVersion = null;
    RecordDTO currentVersion = sForm.getRecord();
    TransactionStatus status = txManager.getTransaction(new DefaultTransactionDefinition());
    try {
      lastVersion = (recordDAO.findRecordWithID(sForm.getRecord().getId(), 0));
      lastVersion.setVariables(recordDAO.findVariablesByVersionID(lastVersion.getVersionId()));
      lastVersion.setAttributes(recordDAO.findRecordAttributes(lastVersion.getVersionId(), true));
      lastVersion.setDataMatrixJson(recordDAO.findMatrixByVersionId(lastVersion.getVersionId()));
      if (lastVersion.getVariables() != null && lastVersion.getVariables().size() > 0) {
        for (SPSSVarDTO var : lastVersion.getVariables()) {
          var.setAttributes(recordDAO.findVariableAttributes(var.getId(), false));
          importUtil.sortVariableAttributes(var);
          var.setValues(recordDAO.findVariableValues(var.getId(), true));
        }
      }
      if (currentVersion != null && lastVersion != null && currentVersion.getVariables() != null
          && lastVersion.getVariables() != null && !currentVersion.getVariables().equals(lastVersion.getVariables())) {
        recordDAO.insertCodeBookMetaData(currentVersion);
        int i = 0;
        for (SPSSVarDTO var : currentVersion.getVariables()) {
          if (var.equals(lastVersion.getVariables().get(i++))) {
            recordDAO.insertVariableVersionRelation(var.getId(), currentVersion.getVersionId(), var.getPosition(),
                messageSource.getMessage("import.check.EQUAL", null, LocaleContextHolder.getLocale()));
          } else {
            long varId = recordDAO.insertVariable(var);
            recordDAO.insertVariableVersionRelation(varId, currentVersion.getVersionId(), var.getPosition(),
                messageSource.getMessage("import.check.NEW_VAR", null, LocaleContextHolder.getLocale()));
            if (var.getAttributes() != null) {
              removeEmptyDWAttributes(var.getDw_attributes());
              var.getAttributes().addAll(var.getDw_attributes());
              recordDAO.insertAttributes(var.getAttributes(), currentVersion.getVersionId(), varId);
            }
            if (var.getValues() != null)
              recordDAO.insertVarLabels(var.getValues(), varId);
          }
        }
        recordDAO.insertMatrix(currentVersion);
        txManager.commit(status);
        redirectAttributes.addFlashAttribute("infoMSG",
            messageSource.getMessage("record.codebook.saved", null, LocaleContextHolder.getLocale()));
      } else if (currentVersion == null || lastVersion == null || currentVersion.getVariables() == null
          || lastVersion.getVariables() == null) {
        redirectAttributes.addFlashAttribute("infoMSG",
            messageSource.getMessage("record.codebook.data.corrupt", null, LocaleContextHolder.getLocale()));
      } else if (currentVersion.getVariables().equals(lastVersion.getVariables())) {
        redirectAttributes.addFlashAttribute("infoMSG",
            messageSource.getMessage("record.codebook.versions.equal", null, LocaleContextHolder.getLocale()));
      }

    } catch (Exception e) {
      // TODO
      log.error("ERROR", e);
      redirectAttributes.addFlashAttribute("errorMSG",
          messageSource.getMessage("record.codebook.server.error", null, LocaleContextHolder.getLocale()));
      txManager.rollback(status);
    }
    return "redirect:/project/" + sForm.getProject().getId() + "/study/" + sForm.getStudy().getId() + "/record/"
        + currentVersion.getId() + "/version/" + currentVersion.getVersionId() + "/codebook";
  }

  /**
   * Saves the Record to the DB and the uploaded file to the Minio System. Transaction Manager is used, to eventually
   * rollback the transmission if an error occurs.
   * 
   * @param sForm
   *          StudyForm, which contains the Record
   * 
   */
  private void saveRecordToDBAndMinio(StudyForm sForm) {
    Boolean error = false;
    RecordDTO spssFile = sForm.getRecord();
    FileDTO file = sForm.getFile();
    if (!error && spssFile != null && file != null) {
      TransactionStatus status = txManager.getTransaction(new DefaultTransactionDefinition());
      try {
        recordDAO.insertCodeBookMetaData(spssFile);
        removeUselessFileAttributes(spssFile.getAttributes());
        recordDAO.insertAttributes(spssFile.getAttributes(), spssFile.getVersionId(), 0);
        if (spssFile.getVersionId() > 0) {
          recordDAO.insertMatrix(spssFile);
          int position = 0;
          for (SPSSVarDTO var : spssFile.getVariables()) {
            if (var.getColumns() == 0)
              var.setColumns(var.getWidth());
            if (!sForm.getCompList().get(position).getVarStatus().equals(VariableStatus.EQUAL)
                && !sForm.getCompList().get(position).getVarStatus().equals(VariableStatus.EQUAL_CSV)
                && !sForm.getCompList().get(position).getVarStatus().equals(VariableStatus.MOVED)
                && !sForm.getCompList().get(position).getVarStatus().equals(VariableStatus.MOVED_CSV)) {
              long varId = recordDAO.insertVariable(var);
              recordDAO.insertVariableVersionRelation(varId, spssFile.getVersionId(), var.getPosition(),
                  sForm.getCompList().get(position).getMessage());
              if (var.getAttributes() != null) {
                recordDAO.insertAttributes(var.getAttributes(), spssFile.getVersionId(), varId);
              }
              if (var.getValues() != null)
                recordDAO.insertVarLabels(var.getValues(), varId);
            } else {
              recordDAO.insertVariableVersionRelation(var.getId(), spssFile.getVersionId(), var.getPosition(),
                  sForm.getCompList().get(position).getMessage());
            }
            position++;
          }
          file.setVersion(spssFile.getVersionId());
          MinioResult res = minioUtil.putFile(file);
          if (res.equals(MinioResult.OK)) {
            fileDAO.saveFile(file);
          } else if (res.equals(MinioResult.CONNECTION_ERROR)) {
            log.fatal("FATAL: No Connection to Minio Server - please check Settings or Server");
            error = true;
          } else {
            log.error("ERROR: During Saving File - MinioResult:", () -> res.name());
            error = true;
          }
        }
      } catch (Exception e) {
        error = true;
        if (file.getFilePath() != null && !file.getFilePath().isEmpty())
          minioUtil.deleteFile(file);
        log.error("ERROR: Saving record to DB wasn't sucessful! Exception:", () -> e);
      } finally {
        if (error) {
          txManager.rollback(status);
        } else {
          txManager.commit(status);
        }
      }
    }
  }

  /**
   * This function removes all useless file attributes (not variable attributes). After that, the list only contains the
   * user specific attributes.
   * 
   * @param attr
   *          List of file attributes
   */
  private void removeUselessFileAttributes(List<SPSSValueLabelDTO> attr) {
    if (attr == null) {
      attr = new ArrayList<SPSSValueLabelDTO>();
    }
    Iterator<SPSSValueLabelDTO> itt = attr.iterator();
    while (itt.hasNext()) {
      SPSSValueLabelDTO attribute = itt.next();
      if (!attribute.getValue().startsWith("@") || attribute.getValue().equals("@dw_construct")
          || attribute.getValue().equals("@dw_measocc") || attribute.getValue().equals("@dw_instrument")
          || attribute.getValue().equals("@dw_itemtext") || attribute.getValue().equals("@dw_filtervar")) {
        itt.remove();
      }
    }
  }

  /**
   * This function copies the needed missing types from the temporary SPSSvarDTO varval (used for the missing modal), to
   * the SPSSVarVar of the current edited record. The needed missing values are selected by the missing type.
   * 
   * @param varVal
   *          Temporary SPSSVarDTO of the missing modal form
   * @param var
   *          SPSSVarDTO of the sForm record
   */
  private void switchMissingType(SPSSVarDTO varVal, SPSSVarDTO var) {
    switch (varVal.getMissingFormat()) {
    case SPSS_NO_MISSVAL:
      var.setMissingVal1(null);
      var.setMissingVal2(null);
      var.setMissingVal3(null);
      break;
    case SPSS_ONE_MISSVAL:
      var.setMissingVal1(varVal.getMissingVal1());
      var.setMissingVal2(null);
      var.setMissingVal3(null);
      break;
    case SPSS_TWO_MISSVAL:
      var.setMissingVal1(varVal.getMissingVal1());
      var.setMissingVal2(varVal.getMissingVal2());
      var.setMissingVal3(null);
      break;
    case SPSS_THREE_MISSVAL:
      var.setMissingVal1(varVal.getMissingVal1());
      var.setMissingVal2(varVal.getMissingVal2());
      var.setMissingVal3(varVal.getMissingVal3());
      break;
    case SPSS_MISS_RANGE:
      var.setMissingVal1(varVal.getMissingVal1());
      var.setMissingVal2(varVal.getMissingVal2());
      var.setMissingVal3(null);
      break;
    case SPSS_MISS_RANGEANDVAL:
      var.setMissingVal1(varVal.getMissingVal1());
      var.setMissingVal2(varVal.getMissingVal2());
      var.setMissingVal3(varVal.getMissingVal3());
      break;
    default:
      log.warn("MissingFormat not known - " + varVal.getMissingFormat());
      break;
    }
  }

}
