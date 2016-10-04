package de.zpid.datawiz.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import de.zpid.datawiz.dto.FileDTO;
import de.zpid.datawiz.dto.RecordDTO;
import de.zpid.datawiz.dto.UserDTO;
import de.zpid.datawiz.enumeration.MinioResult;
import de.zpid.datawiz.enumeration.PageState;
import de.zpid.datawiz.form.StudyForm;
import de.zpid.datawiz.util.BreadCrumpUtil;
import de.zpid.datawiz.util.UserUtil;

@Controller
@RequestMapping(value = { "/record", "/project/{pid}/study/{studyId}/record" })
@SessionAttributes({ "StudyForm", "subnaviActive", "breadcrumpList" })
public class RecordController extends SuperController {

  private static Logger log = LogManager.getLogger(RecordController.class);

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
        rList.add(recordDAO.findRecordWithID(recordId.get()));
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
    try {
      if (sForm.getSelectedFileType() != null && sForm.getSelectedFileType().equals("SPSS")) {
        if (sForm.getSpssFile() != null && sForm.getSpssFile().getSize() > 0) {
          if (sForm.getSpssFile() != null) {
            FileDTO file = fileUtil.buildFileDTO(pid.get(), studyId.get(), recordId.get(), 0, user.getId(), sForm.getSpssFile());
            MinioResult res = minioUtil.putFile(file);
            if (res.equals(MinioResult.OK)) {
              fileDAO.saveFile(file);
            } else if (res.equals(MinioResult.CONNECTION_ERROR)) {
              log.error("ERROR: No Connection to Minio Server - please check Settings or Server");
              return "";
            } else {
              log.error("ERROR: During Saving File - MinioResult:", () -> res.name());
              return "";
            }
          } else {

          }
        } else if (sForm.getSelectedFileType() != null && sForm.getSelectedFileType().equals("CSV")) {

        } else {

        }
      }
    } catch (Exception e) {

    }
    System.out.println(sForm.getSpssFile().getContentType() + " - " + sForm.getSpssFile().getOriginalFilename());
    System.out.println(sForm.getCsvFile().getContentType());
    System.out.println(sForm.getCodeBookFile().getContentType());
    return "redirect:/project/" + pid.get() + "/study/" + studyId.get() + "/record/" + recordId.get();
  }

  @RequestMapping(value = { "/{recordId}" }, method = RequestMethod.POST)
  public String save(@PathVariable final Optional<Long> pid, @PathVariable final Optional<Long> studyId,
      @PathVariable final Optional<Long> recordId, final ModelMap model, final RedirectAttributes redirectAttributes,
      final MultipartHttpServletRequest request) {
    log.trace("Entering  save for [recordId: {}; studyId {}; projectId {}]", () -> recordId.get(), () -> studyId.get(),
        () -> pid.get());
    return "redirect:/project/" + pid.get() + "/study/" + studyId.get() + "/record/" + recordId.get();
  }

}
