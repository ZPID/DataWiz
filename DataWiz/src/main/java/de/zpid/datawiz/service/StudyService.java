package de.zpid.datawiz.service;

import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import de.zpid.datawiz.dao.ContributorDAO;
import de.zpid.datawiz.dao.FormTypesDAO;
import de.zpid.datawiz.dao.ProjectDAO;
import de.zpid.datawiz.dao.StudyConstructDAO;
import de.zpid.datawiz.dao.StudyDAO;
import de.zpid.datawiz.dao.StudyInstrumentDAO;
import de.zpid.datawiz.dao.StudyListTypesDAO;
import de.zpid.datawiz.dto.ContributorDTO;
import de.zpid.datawiz.dto.StudyDTO;
import de.zpid.datawiz.dto.UserDTO;
import de.zpid.datawiz.enumeration.DWFieldTypes;
import de.zpid.datawiz.enumeration.DataWizErrorCodes;
import de.zpid.datawiz.exceptions.DataWizSystemException;
import de.zpid.datawiz.form.StudyForm;

@Service
public class StudyService {

  private static Logger log = LogManager.getLogger(StudyService.class);

  @Autowired
  private ProjectService projectService;
  @Autowired
  private MessageSource messageSource;
  @Autowired
  private ContributorDAO contributorDAO;
  @Autowired
  private StudyListTypesDAO studyListTypesDAO;
  @Autowired
  private StudyConstructDAO studyConstructDAO;
  @Autowired
  private StudyInstrumentDAO studyInstrumentDAO;
  @Autowired
  private FormTypesDAO formTypeDAO;
  @Autowired
  ProjectDAO projectDAO;
  @Autowired
  StudyDAO studyDAO;
  @Autowired
  private int sessionTimeout;

  public String checkStudyAccess(final Optional<Long> pid, final Optional<Long> studyId,
      final RedirectAttributes redirectAttributes, final boolean onlyWrite, final UserDTO user) {
    if (user == null) {
      log.warn("Auth User Object == null - redirect to login");
      return "redirect:/login";
    }
    if (!pid.isPresent() || projectService.checkProjectRoles(user, pid.get(), studyId.isPresent() ? studyId.get() : -1,
        onlyWrite, true) == null) {
      log.warn(
          "WARN: access denied because of: " + (!pid.isPresent() ? "missing project identifier"
              : "user [id: {}] has no rights to read/write study [id: {}]"),
          () -> user.getId(), () -> studyId.isPresent() ? studyId.get() : 0);
      redirectAttributes.addFlashAttribute("errorMSG",
          messageSource.getMessage("project.not.available", null, LocaleContextHolder.getLocale()));
      return !pid.isPresent() ? "redirect:/panel" : "redirect:/project/" + pid.get();
    }
    return null;
  }

  /**
   * @param pid
   * @param studyId
   * @param redirectAttributes
   * @param user
   * @param accessState
   * @param sForm
   * @return
   * @throws Exception
   */
  public String setStudyForm(final Optional<Long> pid, final Optional<Long> studyId,
      final RedirectAttributes redirectAttributes, final UserDTO user, StudyForm sForm) throws Exception {
    String accessState = "disabled";
    sForm.setProject(projectDAO.findById(pid.get()));
    if (sForm.getProject() == null)
      throw new DataWizSystemException("No Project found for projectId " + pid.get(),
          DataWizErrorCodes.PROJECT_NOT_AVAILABLE);
    List<ContributorDTO> pContri = contributorDAO.findByProject(sForm.getProject(), false, true);
    if (studyId.isPresent()) {
      StudyDTO study = studyDAO.findById(studyId.get(), pid.get(), false, false);
      if (study != null) {
        setStudyDTO(studyId, study);
        sForm.setStudy(study);
        cleanContributorList(pContri, study.getContributors());
        accessState = updateAccessState(user, accessState, study);
      } else {
        throw new DataWizSystemException("No Study found for studyId " + studyId.get(),
            DataWizErrorCodes.STUDY_NOT_AVAILABLE);
      }
      sForm.setCollectionModes(formTypeDAO.findAllByType(true, DWFieldTypes.COLLECTIONMODE));
      sForm.setSourFormat(formTypeDAO.findAllByType(true, DWFieldTypes.DATAFORMAT));
      sForm.setProjectContributors(pContri);
    } else {
      accessState = "enabled";
    }
    return accessState;
  }

  /**
   * @param studyId
   * @param study
   * @throws Exception
   */
  public void setStudyDTO(final Optional<Long> studyId, final StudyDTO study) throws Exception {
    if (study != null && study.getId() > 0) {
      study.setContributors(contributorDAO.findByStudy(studyId.get()));
      study.setSoftware(studyListTypesDAO.findAllByStudyAndType(studyId.get(), DWFieldTypes.SOFTWARE));
      study.setPubOnData(studyListTypesDAO.findAllByStudyAndType(studyId.get(), DWFieldTypes.PUBONDATA));
      study.setConflInterests(studyListTypesDAO.findAllByStudyAndType(studyId.get(), DWFieldTypes.CONFLINTEREST));
      study.setRelTheorys(studyListTypesDAO.findAllByStudyAndType(studyId.get(), DWFieldTypes.RELTHEORY));
      study.setObjectives(studyListTypesDAO.findAllByStudyAndType(studyId.get(), DWFieldTypes.OBJECTIVES));
      study.setMeasOcc(studyListTypesDAO.findAllByStudyAndType(studyId.get(), DWFieldTypes.MEASOCCNAME));
      study.setInterArms(studyListTypesDAO.findAllByStudyAndType(studyId.get(), DWFieldTypes.INTERARMS));
      study.setConstructs(studyConstructDAO.findAllByStudy(studyId.get()));
      study.setInstruments(studyInstrumentDAO.findAllByStudy(studyId.get(), false));
      study.setEligibilities(studyListTypesDAO.findAllByStudyAndType(studyId.get(), DWFieldTypes.ELIGIBILITY));
      study.setUsedCollectionModes(
          formTypeDAO.findSelectedFormTypesByIdAndType(study.getId(), DWFieldTypes.COLLECTIONMODE, true));
      study.setUsedSourFormat(
          formTypeDAO.findSelectedFormTypesByIdAndType(study.getId(), DWFieldTypes.DATAFORMAT, true));
    }
  }

  /**
   * @param pContri
   * @param study
   */
  private void cleanContributorList(final List<ContributorDTO> pContri, final List<ContributorDTO> sContri) {
    if (pContri != null && sContri != null)
      for (Iterator<ContributorDTO> it = pContri.iterator(); it.hasNext();) {
        ContributorDTO ccontri = it.next();
        for (ContributorDTO scontri : sContri)
          if (scontri.getId() == ccontri.getId())
            it.remove();
      }
  }

  /**
   * @param user
   * @param accessState
   * @param study
   * @return
   */
  private String updateAccessState(final UserDTO user, String accessState, StudyDTO study) {
    if (study.isCurrentlyEdit() && study.getEditUserId() == user.getId()
        && (study.getEditSince().plusSeconds(sessionTimeout).compareTo(LocalDateTime.now()) >= 0)) {
      studyDAO.switchStudyLock(study.getId(), user.getId(), false);
      accessState = "enabled";
    }
    return accessState;
  }

}
