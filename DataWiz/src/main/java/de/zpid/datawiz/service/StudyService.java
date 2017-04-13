package de.zpid.datawiz.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import de.zpid.datawiz.dao.ContributorDAO;
import de.zpid.datawiz.dao.FormTypesDAO;
import de.zpid.datawiz.dao.ProjectDAO;
import de.zpid.datawiz.dao.RecordDAO;
import de.zpid.datawiz.dao.StudyConstructDAO;
import de.zpid.datawiz.dao.StudyDAO;
import de.zpid.datawiz.dao.StudyInstrumentDAO;
import de.zpid.datawiz.dao.StudyListTypesDAO;
import de.zpid.datawiz.dto.ContributorDTO;
import de.zpid.datawiz.dto.ProjectDTO;
import de.zpid.datawiz.dto.StudyConstructDTO;
import de.zpid.datawiz.dto.StudyDTO;
import de.zpid.datawiz.dto.StudyInstrumentDTO;
import de.zpid.datawiz.dto.StudyListTypesDTO;
import de.zpid.datawiz.dto.UserDTO;
import de.zpid.datawiz.enumeration.DWFieldTypes;
import de.zpid.datawiz.enumeration.DataWizErrorCodes;
import de.zpid.datawiz.enumeration.PageState;
import de.zpid.datawiz.exceptions.DataWizSystemException;
import de.zpid.datawiz.form.StudyForm;
import de.zpid.datawiz.util.BreadCrumpUtil;
import de.zpid.datawiz.util.ListUtil;

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
  RecordDAO recordDAO;
  @Autowired
  private int sessionTimeout;
  @Autowired
  private PlatformTransactionManager txManager;

  /**
   * 
   * @param pid
   * @param studyId
   * @param redirectAttributes
   * @param onlyWrite
   * @param user
   * @return
   */
  public String checkStudyAccess(final Optional<Long> pid, final Optional<Long> studyId,
      final RedirectAttributes redirectAttributes, final boolean onlyWrite, final UserDTO user) {
    String ret = null;
    if (user == null) {
      log.warn("Auth User Object == null - redirect to login");
      ret = "redirect:/login";
    }
    if (!pid.isPresent() || projectService.checkProjectRoles(user, pid.get(), studyId.isPresent() ? studyId.get() : -1,
        onlyWrite, true) == null) {
      log.warn(
          "WARN: access denied because of: " + (!pid.isPresent() ? "missing project identifier"
              : "user [id: {}] has no rights to read/write study [id: {}]"),
          () -> user.getId(), () -> studyId.isPresent() ? studyId.get() : 0);
      redirectAttributes.addFlashAttribute("errorMSG",
          messageSource.getMessage("project.not.available", null, LocaleContextHolder.getLocale()));
      ret = !pid.isPresent() ? "redirect:/panel" : "redirect:/project/" + pid.get();
    }
    return ret;
  }

  /**
   * 
   * @param pid
   * @param studyId
   * @param redirectAttributes
   * @param user
   * @param sForm
   * @return
   * @throws Exception
   */
  public String setStudyForm(final Optional<Long> pid, final Optional<Long> studyId,
      final RedirectAttributes redirectAttributes, final UserDTO user, StudyForm sForm) throws DataWizSystemException {
    if (!pid.isPresent())
      throw new DataWizSystemException(messageSource.getMessage("logging.pid.not.present", null, Locale.ENGLISH),
          DataWizErrorCodes.MISSING_PID_ERROR);
    String accessState = "disabled";
    ProjectDTO project = null;
    StudyDTO study = null;
    List<ContributorDTO> pContri = null;
    try {
      project = projectDAO.findById(pid.get());
      if (project != null)
        pContri = contributorDAO.findByProject(project, false, true);
      sForm.setCollectionModes(formTypeDAO.findAllByType(true, DWFieldTypes.COLLECTIONMODE));
      sForm.setSourFormat(formTypeDAO.findAllByType(true, DWFieldTypes.DATAFORMAT));
      if (studyId.isPresent()) {
        study = studyDAO.findById(studyId.get(), pid.get(), false, false);
        if (study != null) {
          setStudyDTO(study);
          if (study.isCurrentlyEdit() && study.getEditUserId() == user.getId()
              && (study.getEditSince().plusSeconds(sessionTimeout).compareTo(LocalDateTime.now()) >= 0)) {
            studyDAO.switchStudyLock(study.getId(), user.getId(), false);
            accessState = "enabled";
          }
        }
      }
    } catch (Exception e) {
      throw new DataWizSystemException(
          messageSource.getMessage("logging.database.error", new Object[] { e.getMessage() }, Locale.ENGLISH),
          DataWizErrorCodes.DATABASE_ERROR);
    }
    if (project == null) {
      throw new DataWizSystemException(
          messageSource.getMessage("logging.project.not.found", new Object[] { pid.get() }, Locale.ENGLISH),
          DataWizErrorCodes.PROJECT_NOT_AVAILABLE);
    }
    if (studyId.isPresent()) {
      if (study != null && study.getId() > 0) {
        cleanContributorList(pContri, study.getContributors());
        sForm.setStudy(study);
      } else {
        throw new DataWizSystemException(
            messageSource.getMessage("logging.study.not.found", new Object[] { studyId.get() }, Locale.ENGLISH),
            DataWizErrorCodes.STUDY_NOT_AVAILABLE);
      }
    } else {
      accessState = "enabled";
    }
    sForm.setProject(project);
    sForm.setProjectContributors(pContri);
    return accessState;
  }

  /**
   * 
   * @param pid
   * @param studyId
   * @param redirectAttributes
   * @param sForm
   * @throws DataWizSystemException
   */
  public void setRecordList(final Optional<Long> pid, final Optional<Long> studyId,
      final RedirectAttributes redirectAttributes, StudyForm sForm) throws DataWizSystemException {
    if (!pid.isPresent())
      throw new DataWizSystemException(messageSource.getMessage("logging.pid.not.present", null, Locale.ENGLISH),
          DataWizErrorCodes.MISSING_PID_ERROR);
    if (!studyId.isPresent())
      throw new DataWizSystemException(messageSource.getMessage("logging.studyid.not.present", null, Locale.ENGLISH),
          DataWizErrorCodes.MISSING_STUDYID_ERROR);
    ProjectDTO project = null;
    StudyDTO study = null;
    try {
      project = projectDAO.findById(pid.get());
      study = studyDAO.findById(studyId.get(), pid.get(), true, false);
      sForm.setRecords(recordDAO.findRecordsWithStudyID(studyId.get()));
    } catch (Exception e) {
      throw new DataWizSystemException(
          messageSource.getMessage("logging.database.error", new Object[] { e.getMessage() }, Locale.ENGLISH),
          DataWizErrorCodes.DATABASE_ERROR);
    }
    if (project == null) {
      throw new DataWizSystemException(
          messageSource.getMessage("logging.project.not.found", new Object[] { pid.get() }, Locale.ENGLISH),
          DataWizErrorCodes.PROJECT_NOT_AVAILABLE);
    }
    if (study == null || study.getId() <= 0) {
      throw new DataWizSystemException(
          messageSource.getMessage("logging.study.not.found", new Object[] { studyId.get() }, Locale.ENGLISH),
          DataWizErrorCodes.STUDY_NOT_AVAILABLE);
    }
    sForm.setProject(project);
    sForm.setStudy(study);
  }

  /**
   * @param pid
   * @param studyId
   * @param user
   * @param actLock
   * @return
   * @throws DataWizSystemException
   * @throws NoSuchMessageException
   */
  public String checkActualLock(final Optional<Long> pid, final Optional<Long> studyId, UserDTO user, String actLock,
      final ModelMap model) throws DataWizSystemException {
    if (!pid.isPresent())
      throw new DataWizSystemException(messageSource.getMessage("logging.pid.not.present", null, Locale.ENGLISH),
          DataWizErrorCodes.MISSING_PID_ERROR);
    if (!studyId.isPresent())
      throw new DataWizSystemException(messageSource.getMessage("logging.studyid.not.present", null, Locale.ENGLISH),
          DataWizErrorCodes.MISSING_STUDYID_ERROR);
    StudyDTO study = null;
    try {
      study = studyDAO.findById(studyId.get(), pid.get(), true, true);
    } catch (Exception e) {
      throw new DataWizSystemException(
          messageSource.getMessage("logging.database.error", new Object[] { e.getMessage() }, Locale.ENGLISH),
          DataWizErrorCodes.DATABASE_ERROR);
    }
    if (study == null || study.getId() <= 0) {
      throw new DataWizSystemException(
          messageSource.getMessage("logging.study.not.found", new Object[] { studyId.get() }, Locale.ENGLISH),
          DataWizErrorCodes.STUDY_NOT_AVAILABLE);
    }
    if ((!study.isCurrentlyEdit()
        || (study.getEditSince().plusSeconds(sessionTimeout).compareTo(LocalDateTime.now()) < 0)
        || (study.getEditSince().plusSeconds(sessionTimeout).compareTo(LocalDateTime.now()) >= 0
            && user.getId() == study.getEditUserId()))) {
      try {
        if (actLock == null || actLock.isEmpty() || actLock.equals("enabled")) {
          if (studyDAO.switchStudyLock(studyId.get(), user.getId(), true) > 0)
            actLock = "disabled";
        } else {
          if (studyDAO.switchStudyLock(studyId.get(), user.getId(), false) > 0)
            actLock = "enabled";
        }
      } catch (Exception e) {
        throw new DataWizSystemException(
            messageSource.getMessage("logging.database.error", new Object[] { e.getMessage() }, Locale.ENGLISH),
            DataWizErrorCodes.DATABASE_ERROR);
      }
    } else {
      // TODO extra meldefenster!!!!
      model.put("errorMSG",
          "Studie momentan in Bearbeitung durch " + study.getEditUserId() + " since" + study.getEditSince());
    }
    return actLock;
  }

  /**
   * @param sForm
   * @param studyId
   * @param pid
   * @param user
   * @return
   */
  public StudyDTO saveStudyForm(StudyForm sForm, final Optional<Long> studyId, final Optional<Long> pid,
      final UserDTO user) {
    TransactionStatus status = txManager.getTransaction(new DefaultTransactionDefinition());
    StudyDTO study = null;
    if (sForm != null)
      study = sForm.getStudy();
    if (study != null && pid.isPresent()) {
      try {
        study.setLastUserId(user.getId());
        if (studyId.isPresent() && study.getId() > 0) {
          // update Study
          studyDAO.update(study, false);
        } else {
          study.setProjectId(pid.get());
          studyDAO.insert(study, false);
        }
        // update Contibutors
        List<ContributorDTO> dbList = contributorDAO.findByStudy(study.getId());
        if (!ListUtil.equalsWithoutOrder(dbList, study.getContributors())) {
          if (dbList != null)
            contributorDAO.deleteFromStudy(dbList);
          if (study.getContributors() != null)
            contributorDAO.insertIntoStudy(study.getContributors(), study.getId());
        }
        // update SOFTWARE
        updateStudyListItems(study.getId(), study.getSoftware(), DWFieldTypes.SOFTWARE);
        // update PUBONDATA
        updateStudyListItems(study.getId(), study.getPubOnData(), DWFieldTypes.PUBONDATA);
        // update CONFLINTEREST
        updateStudyListItems(study.getId(), study.getConflInterests(), DWFieldTypes.CONFLINTEREST);
        // update OBJECTIVES
        updateStudyListItems(study.getId(), study.getObjectives(), DWFieldTypes.OBJECTIVES);
        // update RELTHEORY
        updateStudyListItems(study.getId(), study.getRelTheorys(), DWFieldTypes.RELTHEORY);
        // update INTERARMS
        updateStudyListItems(study.getId(), study.getInterArms(), DWFieldTypes.INTERARMS);
        // update MEASOCCNAME
        updateStudyListItems(study.getId(), study.getMeasOcc(), DWFieldTypes.MEASOCCNAME);
        // update CONSTRUCTS
        updateConstructsItems(study.getId(), study.getConstructs());
        // update INSTRUMENTS
        updateInstrumentItems(study.getId(), study.getInstruments());
        // update MEASOCCNAME
        updateStudyListItems(study.getId(), study.getEligibilities(), DWFieldTypes.ELIGIBILITY);
        // update usedCollectionModes
        List<Integer> collectionModes = formTypeDAO.findSelectedFormTypesByIdAndType(study.getId(),
            DWFieldTypes.COLLECTIONMODE, true);
        if (!ListUtil.equalsWithoutOrder(collectionModes, study.getUsedCollectionModes())) {
          if (collectionModes != null && collectionModes.size() > 0) {
            formTypeDAO.deleteSelectedFormType(study.getId(), collectionModes, true);
          }
          if (study.getUsedCollectionModes() != null && study.getUsedCollectionModes().size() > 0) {
            formTypeDAO.insertSelectedFormType(study.getId(), study.getUsedCollectionModes(), true);
          }
        }
        // update UsedSourFormat
        List<Integer> sourFormat = formTypeDAO.findSelectedFormTypesByIdAndType(study.getId(), DWFieldTypes.DATAFORMAT,
            true);
        if (!ListUtil.equalsWithoutOrder(sourFormat, study.getUsedSourFormat())) {
          if (sourFormat != null && sourFormat.size() > 0) {
            formTypeDAO.deleteSelectedFormType(study.getId(), sourFormat, true);
          }
          if (study.getUsedSourFormat() != null && study.getUsedSourFormat().size() > 0) {
            formTypeDAO.insertSelectedFormType(study.getId(), study.getUsedSourFormat(), true);
          }
        }
        txManager.commit(status);
      } catch (Exception e) {
        log.error("ERROR", e);
        txManager.rollback(status);
      }
    } else {
      // TODO study null
    }
    return study;
  }

  /**
   * @param studyId
   * @param study
   * @param type
   * @throws Exception
   */
  private void updateStudyListItems(final Long studyId, List<StudyListTypesDTO> list, DWFieldTypes type)
      throws Exception {
    List<StudyListTypesDTO> dbtmp = studyListTypesDAO.findAllByStudyAndType(studyId, type);
    if (!ListUtil.equalsWithoutOrder(dbtmp, list)) {
      List<StudyListTypesDTO> insert = new ArrayList<>();
      List<StudyListTypesDTO> delete = new ArrayList<>();
      List<StudyListTypesDTO> update = new ArrayList<>();
      if (list != null && list.size() > 0)
        for (StudyListTypesDTO tmp : list) {
          if (tmp.getId() <= 0 && !tmp.getText().trim().isEmpty()) {
            tmp.setStudyid(studyId);
            tmp.setType(type);
            tmp.setSort(0);
            tmp.setTimetable(false);
            insert.add(tmp);
          } else if (tmp.getId() > 0 && tmp.getText().trim().isEmpty()) {
            dbtmp.remove(tmp);
            delete.add(tmp);
          } else if (tmp.getId() > 0 && !tmp.getText().trim().isEmpty()) {
            for (ListIterator<StudyListTypesDTO> iter = dbtmp.listIterator(); iter.hasNext();) {
              StudyListTypesDTO tmp2 = iter.next();
              if (tmp.getId() == tmp2.getId()) {
                if (!tmp.getText().equals(tmp2.getText()) || (type.equals(DWFieldTypes.MEASOCCNAME)
                    && (tmp.getSort() != tmp2.getSort() || tmp.isTimetable() == tmp2.isTimetable()))) {
                  update.add(tmp);
                }
                iter.remove();
              }
            }
          }
        }
      if (delete.size() > 0)
        studyListTypesDAO.delete(delete);
      if (update.size() > 0)
        studyListTypesDAO.update(update);
      if (insert.size() > 0)
        studyListTypesDAO.insert(insert);
    }
  }

  private void updateConstructsItems(final Long studyId, List<StudyConstructDTO> list) throws Exception {
    List<StudyConstructDTO> dbtmp = studyConstructDAO.findAllByStudy(studyId);
    if (!ListUtil.equalsWithoutOrder(dbtmp, list)) {
      List<StudyConstructDTO> insert = new ArrayList<>();
      List<StudyConstructDTO> delete = new ArrayList<>();
      List<StudyConstructDTO> update = new ArrayList<>();
      if (list != null && list.size() > 0)
        for (StudyConstructDTO tmp : list) {
          if (tmp.getId() <= 0 && !tmp.getName().trim().isEmpty()) {
            tmp.setStudyId(studyId);
            insert.add(tmp);
          } else if (tmp.getId() > 0 && tmp.getName().trim().isEmpty()) {
            delete.add(tmp);
          } else if (tmp.getId() > 0 && !tmp.getName().trim().isEmpty()) {
            for (ListIterator<StudyConstructDTO> iter = dbtmp.listIterator(); iter.hasNext();) {
              StudyConstructDTO tmp2 = iter.next();
              if (tmp.getId() == tmp2.getId()) {
                if (!tmp.getName().equals(tmp2.getName()) || !tmp.getType().equals(tmp2.getType())
                    || !tmp.getOther().equals(tmp2.getOther())) {
                  update.add(tmp);
                }
                iter.remove();
              }
            }
          }
        }
      if (delete.size() > 0)
        studyConstructDAO.delete(delete);
      if (update.size() > 0)
        studyConstructDAO.update(update);
      if (insert.size() > 0)
        studyConstructDAO.insert(insert);
    }
  }

  private void updateInstrumentItems(final Long studyId, List<StudyInstrumentDTO> list) throws Exception {
    List<StudyInstrumentDTO> dbtmp = studyInstrumentDAO.findAllByStudy(studyId, false);
    if (!ListUtil.equalsWithoutOrder(dbtmp, list)) {
      List<StudyInstrumentDTO> insert = new ArrayList<>();
      List<StudyInstrumentDTO> delete = new ArrayList<>();
      List<StudyInstrumentDTO> update = new ArrayList<>();
      if (list != null && list.size() > 0)
        for (StudyInstrumentDTO tmp : list) {
          if (tmp.getId() <= 0 && !tmp.getTitle().trim().isEmpty()) {
            tmp.setStudyId(studyId);
            insert.add(tmp);
          } else if (tmp.getId() > 0 && tmp.getTitle().trim().isEmpty()) {
            delete.add(tmp);
          } else if (tmp.getId() > 0 && !tmp.getTitle().trim().isEmpty()) {
            for (ListIterator<StudyInstrumentDTO> iter = dbtmp.listIterator(); iter.hasNext();) {
              StudyInstrumentDTO tmp2 = iter.next();
              if (tmp.getId() == tmp2.getId()) {
                if (!tmp2.equals(tmp))
                  update.add(tmp);
                iter.remove();
              }
            }
          }
        }
      if (delete.size() > 0)
        studyInstrumentDAO.delete(delete);
      if (update.size() > 0)
        studyInstrumentDAO.update(update);
      if (insert.size() > 0)
        studyInstrumentDAO.insert(insert);
    }
  }

  /**
   * 
   * @param study
   * @throws Exception
   */
  public void setStudyDTO(final StudyDTO study) throws Exception {
    if (study != null && study.getId() > 0) {
      study.setContributors(contributorDAO.findByStudy(study.getId()));
      study.setSoftware(studyListTypesDAO.findAllByStudyAndType(study.getId(), DWFieldTypes.SOFTWARE));
      study.setPubOnData(studyListTypesDAO.findAllByStudyAndType(study.getId(), DWFieldTypes.PUBONDATA));
      study.setConflInterests(studyListTypesDAO.findAllByStudyAndType(study.getId(), DWFieldTypes.CONFLINTEREST));
      study.setRelTheorys(studyListTypesDAO.findAllByStudyAndType(study.getId(), DWFieldTypes.RELTHEORY));
      study.setObjectives(studyListTypesDAO.findAllByStudyAndType(study.getId(), DWFieldTypes.OBJECTIVES));
      study.setMeasOcc(studyListTypesDAO.findAllByStudyAndType(study.getId(), DWFieldTypes.MEASOCCNAME));
      study.setInterArms(studyListTypesDAO.findAllByStudyAndType(study.getId(), DWFieldTypes.INTERARMS));
      study.setConstructs(studyConstructDAO.findAllByStudy(study.getId()));
      study.setInstruments(studyInstrumentDAO.findAllByStudy(study.getId(), false));
      study.setEligibilities(studyListTypesDAO.findAllByStudyAndType(study.getId(), DWFieldTypes.ELIGIBILITY));
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
   * @param sForm
   * @param pid
   * @param model
   */
  public void createStudyBreadCrump(final String projectName, final String studyName, final long pid,
      final ModelMap model) {
    model
        .put("breadcrumpList",
            BreadCrumpUtil.generateBC(PageState.STUDY,
                new String[] { projectName, (studyName != null && !studyName.trim().isEmpty() ? studyName
                    : messageSource.getMessage("study.new.study.breadcrump", null, LocaleContextHolder.getLocale())) },
                new long[] { pid }, messageSource));
  }

}
