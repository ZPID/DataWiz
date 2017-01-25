package de.zpid.datawiz.controller;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
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
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
import de.zpid.datawiz.form.StudyForm;
import de.zpid.datawiz.util.BreadCrumpUtil;
import de.zpid.datawiz.util.ImportUtil;
import de.zpid.datawiz.util.UserUtil;
import de.zpid.spss.dto.SPSSValueLabelDTO;
import de.zpid.spss.dto.SPSSVarTDO;
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
    String accessState = "disabled";
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
      List<RecordDTO> rList = new ArrayList<RecordDTO>();
      if (recordId.isPresent()) {
        RecordDTO rec = (recordDAO.findRecordWithID(recordId.get(),
            (versionId.isPresent() && versionId.get() > 0 ? versionId.get() : 0)));
        if (rec != null) {
          rec.setVariables(recordDAO.findVariablesByVersionID(rec.getVersionId()));
          rec.setAttributes(recordDAO.findRecordAttributes(rec.getVersionId(), true));
          if (rec.getVariables() != null && rec.getVariables().size() > 0) {
            for (SPSSVarTDO var : rec.getVariables()) {
              List<SPSSValueLabelDTO> attributes = recordDAO.findVariableAttributes(var.getId(), false);
              setDataWizAttributes(attributes);
              var.setAttributes(attributes);
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
        sForm.setPreviousRecordVersion(rec);
      }
      sForm.setRecords(rList);
    } catch (Exception e) {
      // TODO: handle exception
    }
    model.put("breadcrumpList", BreadCrumpUtil.generateBC(PageState.RECORDS,
        new String[] { sForm.getProject().getTitle(), sForm.getStudy().getTitle(),
            (sForm.getPreviousRecordVersion() != null ? sForm.getPreviousRecordVersion().getRecordName() : "TEST") },
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
      List<SPSSVarTDO> vars = recordDAO.findVariablesByVersionID(lastVersion.getVersionId());
      for (SPSSVarTDO var : vars) {
        var.setAttributes(recordDAO.findVariableAttributes(var.getId(), false));
        var.setValues(recordDAO.findVariableValues(var.getId(), false));
      }
      lastVersion.setVariables(vars);
      sForm.setPreviousRecordVersion(lastVersion);
      importUtil.compareVarVersion(sForm);
    } catch (Exception e) {
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
    List<SPSSVarTDO> newVars = sForm.getRecord().getVariables();
    List<SPSSVarTDO> prevVars = sForm.getPreviousRecordVersion().getVariables();
    Boolean CSV = sForm.getSelectedFileType() == null ? false
        : sForm.getSelectedFileType().equals("CSV") ? true : false;
    if (compList != null) {
      int position = 0;
      for (RecordCompareDTO comp : compList) {
        SPSSVarTDO newVar = null, prevVar = null;
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
          }
        }

        position++;
      }
    }
    saveRecordToDBAndMinio(sForm);
    return "redirect:/project/" + pid.get() + "/study/" + studyId.get() + "/record/" + recordId.get();
  }

  @RequestMapping(value = { "/{recordId}/modal" })
  public String loadAjaxModal(final ModelMap model, @ModelAttribute("StudyForm") StudyForm sForm,
      @PathVariable final Long pid, @PathVariable final Long studyId, @PathVariable final Long recordId,
      @RequestParam(value = "varId", required = true) long varId,
      @RequestParam(value = "modal", required = true) String modal) {
    log.trace("loadAjaxModal [{}] for variable [id: {}]", () -> modal, () -> varId);
    String ret = "forms/codebookModalContent";
    if (sForm == null || sForm.getPreviousRecordVersion() == null) {
      log.warn("WARN: StudyForm is empty - Session timed out");
      model.put("modalPid", pid);
      model.put("modalStudyId", studyId);
      model.put("modalRecordId", recordId);
      return "forms/modalError";
    }
    if (varId != -1) {
      for (SPSSVarTDO var : sForm.getPreviousRecordVersion().getVariables()) {
        if (var.getId() == varId) {
          model.put("VarValues", var);
          break;
        }
      }
    } else if (varId == -1) {
      if (modal.equals("values")) {
        ret = "forms/codebookModalGlobalValues";
        SPSSVarTDO var = new SPSSVarTDO();
        var.setId(varId);
        model.put("VarValues", var);
      } else if (modal.equals("missings")) {
        ret = "forms/codebookModalGlobalMissings";
        List<SPSSVarTDO> varL = new ArrayList<>();
        StudyForm varForm = (StudyForm) applicationContext.getBean("StudyForm");
        for (int i = 0; i < 2; i++) {
          SPSSVarTDO var = new SPSSVarTDO();
          varL.add(var);
        }
        varForm.setViewVars(varL);
        model.put("VarValues", varForm);
      }

    }
    model.put("modalView", modal);
    return ret;
  }

  @RequestMapping(value = { "/{recordId}" }, params = "setValues")
  public String setValues(final ModelMap model, @ModelAttribute("VarValues") SPSSVarTDO varVal,
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
      for (SPSSVarTDO var : sForm.getPreviousRecordVersion().getVariables()) {
        if (var.getId() == varVal.getId()) {
          var.setValues(varVal.getValues());
          model.remove("VarValues");
          break;
        }
      }
    } else if (varVal.getId() == -1) {
      for (SPSSVarTDO var : sForm.getPreviousRecordVersion().getVariables()) {
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
  public String setType(final ModelMap model, @ModelAttribute("VarValues") SPSSVarTDO varVal,
      @ModelAttribute("StudyForm") StudyForm sForm) {
    log.trace("setType for variable [id: {}]", () -> varVal.getId());
    model.put("recordSubMenu", true);
    model.put("subnaviActive", PageState.RECORDVAR.name());
    System.out.println(varVal.getType());
    return "codebook";
  }

  @RequestMapping(value = { "/{recordId}" }, params = "setMissings")
  public String setMissings(final ModelMap model, @ModelAttribute("VarValues") SPSSVarTDO varVal,
      @ModelAttribute("StudyForm") StudyForm sForm) {
    log.trace("setMissings for variable [id: {}]", () -> varVal.getId());
    model.put("recordSubMenu", true);
    model.put("subnaviActive", PageState.RECORDVAR.name());
    for (SPSSVarTDO var : sForm.getPreviousRecordVersion().getVariables()) {
      if (var.getId() == varVal.getId()) {
        var.setMissingFormat(varVal.getMissingFormat());
        switchMissingType(varVal, var);
        model.remove("VarValues");
        break;
      }
    }
    return "codebook";
  }

  @RequestMapping(value = { "/{recordId}" }, params = "setGlobalMissings")
  public String setGlobalMissings(final ModelMap model, @ModelAttribute("VarValues") StudyForm varVal,
      @ModelAttribute("StudyForm") StudyForm sForm) {
    log.trace("setGlobalMissings for record [id: {}]", () -> sForm.getRecord().getId());
    model.put("recordSubMenu", true);
    model.put("subnaviActive", PageState.RECORDVAR.name());
    List<SPSSVarTDO> missings = varVal.getViewVars();
    for (SPSSVarTDO var : sForm.getPreviousRecordVersion().getVariables()) {
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

  /**
   * @param varVal
   * @param var
   */
  private void switchMissingType(SPSSVarTDO varVal, SPSSVarTDO var) {
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

  @RequestMapping(value = { "/{recordId}" }, method = RequestMethod.POST, params = "saveCodebook")
  public String saveCodebook(final ModelMap model, @ModelAttribute("StudyForm") StudyForm sForm) {
    log.trace("saveCodebook");
    model.put("recordSubMenu", true);
    model.put("subnaviActive", PageState.RECORDVAR.name());
    sForm.getPreviousRecordVersion().getVariables()
        .forEach((s) -> s.getAttributes().forEach((t) -> System.out.println(t)));
    return "codebook";
  }

  /**
   * @param sForm
   */
  private void saveRecordToDBAndMinio(StudyForm sForm) {
    Boolean error = false;
    RecordDTO spssFile = sForm.getRecord();
    FileDTO file = sForm.getFile();
    if (!error && spssFile != null && file != null) {
      TransactionStatus status = txManager.getTransaction(new DefaultTransactionDefinition());
      try {
        recordDAO.insertMetaData(spssFile);
        setRecordDWAttributes(spssFile.getAttributes());
        recordDAO.insertAttributes(spssFile.getAttributes(), spssFile.getVersionId(), 0);
        if (spssFile.getVersionId() > 0) {
          recordDAO.insertMatrix(spssFile);
          int position = 0;
          for (SPSSVarTDO var : spssFile.getVariables()) {
            if (var.getColumns() == 0)
              var.setColumns(var.getWidth());
            if (!sForm.getCompList().get(position).getVarStatus().equals(VariableStatus.EQUAL)
                && !sForm.getCompList().get(position).getVarStatus().equals(VariableStatus.EQUAL_CSV)
                && !sForm.getCompList().get(position).getVarStatus().equals(VariableStatus.MOVED)
                && !sForm.getCompList().get(position).getVarStatus().equals(VariableStatus.MOVED_CSV)) {
              // TODO POSITION UND ÄNDERUNGEN/Warnungen IN REL TABLE !!!!!
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

  private void setRecordDWAttributes(List<SPSSValueLabelDTO> attr) {
    if (attr == null) {
      attr = new ArrayList<SPSSValueLabelDTO>();
    }
    Iterator<SPSSValueLabelDTO> itt = attr.iterator();
    while (itt.hasNext()) {
      SPSSValueLabelDTO attribute = itt.next();
      if (!attribute.getValue().startsWith("@") || attribute.getValue().equals("@dw_construct")
          || attribute.getValue().equals("@dw_measocc") || attribute.getValue().equals("@dw_instrument")
          || attribute.getValue().equals("@dw_itemtextt") || attribute.getValue().equals("@dw_filtervar")) {
        itt.remove();
      }
    }
  }

  /**
   * @param attributes
   */
  private void setDataWizAttributes(List<SPSSValueLabelDTO> attributes) {
    boolean dw_construct = false, dw_measocc = false, dw_instrument = false, dw_itemtext = false, dw_filtervar = false;
    for (SPSSValueLabelDTO att : attributes) {
      if (att.getLabel().equals("dw_construct")) {
        dw_construct = true;
      } else if (att.getLabel().equals("dw_measocc")) {
        dw_measocc = true;
      } else if (att.getLabel().equals("dw_instrument")) {
        dw_instrument = true;
      } else if (att.getLabel().equals("dw_itemtext")) {
        dw_itemtext = true;
      } else if (att.getLabel().equals("dw_filtervar")) {
        dw_filtervar = true;
      }
    }
    if (!dw_construct) {
      attributes.add(new SPSSValueLabelDTO("dw_construct", "k4"));
    }
    if (!dw_measocc) {
      attributes.add(new SPSSValueLabelDTO("dw_measocc", "drei"));
    }
    if (!dw_instrument) {
      attributes.add(new SPSSValueLabelDTO("dw_instrument", "Instrument 3"));
    }
    if (!dw_itemtext) {
      attributes.add(new SPSSValueLabelDTO("dw_itemtext", ""));
    }
    if (!dw_filtervar) {
      attributes.add(new SPSSValueLabelDTO("dw_filtervar", "0"));
    }
  }
}
