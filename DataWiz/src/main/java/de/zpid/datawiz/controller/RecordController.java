package de.zpid.datawiz.controller;

import java.util.ArrayList;
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
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import de.zpid.datawiz.dto.FileDTO;
import de.zpid.datawiz.dto.RecordCompareDTO;
import de.zpid.datawiz.dto.RecordDTO;
import de.zpid.datawiz.dto.UserDTO;
import de.zpid.datawiz.enumeration.MinioResult;
import de.zpid.datawiz.enumeration.PageState;
import de.zpid.datawiz.enumeration.VariableStatus;
import de.zpid.datawiz.form.StudyForm;
import de.zpid.datawiz.util.BreadCrumpUtil;
import de.zpid.datawiz.util.ImportUtil;
import de.zpid.datawiz.util.UserUtil;
import de.zpid.spss.dto.SPSSVarTDO;

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

  @RequestMapping(value = { "", "/{recordId}" }, method = RequestMethod.GET)
  public String showRecord(@PathVariable final Optional<Long> pid, @PathVariable final Optional<Long> studyId,
      @PathVariable final Optional<Long> recordId, final ModelMap model, final RedirectAttributes redirectAttributes) {
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
    if (recordId.isPresent()) {
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
        List<RecordDTO> rList = new ArrayList<RecordDTO>();
        rList.add(recordDAO.findRecordWithID(recordId.get(), 0));
        sForm.setRecords(rList);
      } catch (Exception e) {
        // TODO: handle exception
      }
    }
    model.put("breadcrumpList",
        BreadCrumpUtil.generateBC(PageState.RECORDS,
            new String[] { sForm.getProject().getTitle(), sForm.getStudy().getTitle(),
                (sForm.getRecords() != null && sForm.getRecords().get(0) != null
                    ? sForm.getRecords().get(0).getRecordName() : "TEST") },
            new long[] { pid.get(), studyId.get() }, messageSource));
    model.put("StudyForm", sForm);
    model.put("recordSubMenu", true);
    model.put("subnaviActive", PageState.RECORDMETA.name());
    log.trace("Method showRecord successfully completed");
    return "record";
  }

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
      lastVersion.setVariables(vars);
      if (vars != null) {
        if (vars.size() < sForm.getRecord().getVariables().size())
          sForm.getWarnings().add("Variablen hinzugefügt");
        else if (vars.size() > sForm.getRecord().getVariables().size())
          sForm.getWarnings().add("Variablen gelöscht");
        int position = 0;
        List<RecordCompareDTO> compList = new ArrayList<RecordCompareDTO>();
        for (SPSSVarTDO curr : sForm.getRecord().getVariables()) {
          RecordCompareDTO comp = (RecordCompareDTO) applicationContext.getBean("RecordCompareDTO");
          curr.setVarHandle(0.0);
          importUtil.compareVariable(vars, position, curr, comp, sForm.getSelectedFileType());
          comp.setMessage(messageSource.getMessage("import.check." + comp.getVarStatus().name(),
              new Object[] { comp.getMovedFrom(), comp.getMovedTo() }, LocaleContextHolder.getLocale()));
          compList.add(comp);
          position++;
        }
        if (vars.size() > sForm.getRecord().getVariables().size()) {
          for (int i = sForm.getRecord().getVariables().size() - 1; i < vars.size(); i++) {
            RecordCompareDTO comp = (RecordCompareDTO) applicationContext.getBean("RecordCompareDTO");
            if (!importUtil.findVarInList(sForm.getRecord().getVariables(), vars.get(i), comp,
                sForm.getSelectedFileType())) {
              comp.setVarStatus(VariableStatus.DELETED_VAR);
              comp.setBootstrapItemColor("warning");
              comp.setMessage("deleted variable");
            }
            comp.setMessage(messageSource.getMessage("import.check." + comp.getVarStatus().name(),
                new Object[] { comp.getMovedFrom(), comp.getMovedTo() }, LocaleContextHolder.getLocale()));
            compList.add(comp);
          }
        }
        // compList.forEach((s) -> System.out.println(s.toString()));
        sForm.setCompList(compList);
      }
      sForm.setPreviousRecordVersion(lastVersion);
      // sForm.getRecord().getVariables().forEach((s) -> System.out.println(s));
    } catch (Exception e) {
      e.printStackTrace();
    }

    // saveRecordToDBAndMinio(sForm);
    return "importRep";
  }

  @RequestMapping(value = { "/{recordId}" }, method = RequestMethod.POST)
  public String save(@PathVariable final Optional<Long> pid, @PathVariable final Optional<Long> studyId,
      @PathVariable final Optional<Long> recordId, final ModelMap model, final RedirectAttributes redirectAttributes,
      final MultipartHttpServletRequest request) {
    log.trace("Entering  save for [recordId: {}; studyId {}; projectId {}]", () -> recordId.get(), () -> studyId.get(),
        () -> pid.get());
    return "redirect:/project/" + pid.get() + "/study/" + studyId.get() + "/record/" + recordId.get();
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
        if (spssFile.getAttributes() != null)
          recordDAO.insertAttributes(spssFile.getAttributes(), spssFile.getVersionId(), 0);
        if (spssFile.getVersionId() > 0) {
          recordDAO.insertMatrix(spssFile);
          int position = 1;
          for (SPSSVarTDO var : spssFile.getVariables()) {
            if (var.getColumns() == 0)
              var.setColumns(var.getWidth());
            long varId = recordDAO.insertVariable(var, position++);
            recordDAO.insertVariableVersionRelation(varId, spssFile.getVersionId());
            if (var.getAttributes() != null)
              recordDAO.insertAttributes(var.getAttributes(), spssFile.getVersionId(), varId);
            if (var.getValues() != null)
              recordDAO.insertVarLabels(var.getValues(), varId);
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

}
