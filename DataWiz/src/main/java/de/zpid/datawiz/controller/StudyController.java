package de.zpid.datawiz.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
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
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import de.zpid.datawiz.dto.ContributorDTO;
import de.zpid.datawiz.dto.ProjectDTO;
import de.zpid.datawiz.dto.StudyConstructDTO;
import de.zpid.datawiz.dto.StudyDTO;
import de.zpid.datawiz.dto.StudyInstrumentDTO;
import de.zpid.datawiz.dto.StudyListTypesDTO;
import de.zpid.datawiz.dto.UserDTO;
import de.zpid.datawiz.enumeration.DWFieldTypes;
import de.zpid.datawiz.enumeration.PageState;
import de.zpid.datawiz.form.StudyForm;
import de.zpid.datawiz.util.BreadCrumpUtil;
import de.zpid.datawiz.util.ListUtil;
import de.zpid.datawiz.util.UserUtil;

@Controller
@RequestMapping(value = { "/study", "/project/{pid}/study" })
@SessionAttributes({ "StudyForm", "subnaviActive", "breadcrumpList", "disStudyContent" })
public class StudyController extends SuperController {

  @Autowired
  private PlatformTransactionManager txManager;
  @Autowired
  private int sessionTimeout;

  private static Logger log = LogManager.getLogger(StudyController.class);

  public StudyController() {
    super();
    log.info("Loading StudyController for mapping /study");
  }

  @RequestMapping(value = { "", "/{studyId}", }, method = RequestMethod.GET)
  public String showStudyPage(@PathVariable final Optional<Long> pid, @PathVariable final Optional<Long> studyId,
      final ModelMap model, final RedirectAttributes redirectAttributes,
      @ModelAttribute("jQueryMapS") String jQueryMapS) {
    String ret;
    final UserDTO user = UserUtil.getCurrentUser();
    if (studyId.isPresent()) {
      log.trace("Entering showStudyPage(edit) for study [id: {}]", () -> studyId.get());
      ret = checkStudyAccess(pid, studyId, redirectAttributes, false, user);
    } else {
      log.trace("Entering showStudyPage(create) study");
      ret = checkStudyAccess(pid, studyId, redirectAttributes, true, user);
    }
    if (ret != null)
      return ret;
    String accessState = "disabled";
    StudyForm sForm = createStudyForm();
    try {
      ProjectDTO project = projectDAO.findById(pid.get());
      if (project == null) {
        log.trace("Entering showStudyPage(create) study");
        return "redirect:/panel";
      }
      sForm.setProject(project);
      List<ContributorDTO> pContri = contributorDAO.findByProject(project, false, true);
      if (studyId.isPresent()) {
        StudyDTO study = studyDAO.findById(studyId.get(), pid.get(), false);
        if (study != null) {
          setStudyDTO(studyId, study);
          sForm.setStudy(study);
          cleanContributorList(pContri, study.getContributors());
          accessState = updateAccessState(user, accessState, study);
        }
        sForm.setCollectionModes(formTypeDAO.findAllByType(true, DWFieldTypes.COLLECTIONMODE));
        sForm.setSourFormat(formTypeDAO.findAllByType(true, DWFieldTypes.DATAFORMAT));
      } else {
        accessState = "enabled";
      }
      sForm.setProjectContributors(pContri);
    } catch (Exception e) {
      // TODO
      log.warn(e);
    }
    // TODO Empty in ressoures
    model
        .put("breadcrumpList",
            BreadCrumpUtil.generateBC(PageState.STUDY,
                new String[] { sForm.getProject().getTitle(),
                    (sForm.getStudy() != null && sForm.getStudy().getTitle() != null
                        && !sForm.getStudy().getTitle().isEmpty() ? sForm.getStudy().getTitle() : "empty") },
                pid.get()));
    model.put("disStudyContent", accessState);
    model.put("StudyForm", sForm);
    model.put("studySubMenu", true);
    if (jQueryMapS == null || jQueryMapS.trim().isEmpty())
      model.put("jQueryMap", PageState.STUDYGENERAL);
    else
      model.put("jQueryMap", PageState.STUDYDESIGN);
    model.put("subnaviActive", PageState.STUDY.name());
    log.trace("Method showStudyPage successfully completed");
    return "study";
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

  @RequestMapping(value = { "", "/{studyId}" }, method = RequestMethod.POST)
  public String saveStudy(@ModelAttribute("StudyForm") StudyForm sForm, ModelMap model,
      RedirectAttributes redirectAttributes, BindingResult bRes, @PathVariable final Optional<Long> studyId,
      @PathVariable final Optional<Long> pid, @RequestParam("jQueryMap") String jQueryMap) {
    log.trace("Entering saveStudy");
    TransactionStatus status = txManager.getTransaction(new DefaultTransactionDefinition());
    StudyDTO study = null;
    if (sForm != null)
      study = sForm.getStudy();
    if (study != null && pid.isPresent()) {
      try {
        final UserDTO user = UserUtil.getCurrentUser();
        if (studyId.isPresent() && study.getId() > 0) {
          // update Study
          studyDAO.updateStudy(sForm.getStudy(), false, user.getId());
        } else {
          // TODO insert Study
        }
        // update Contibutors
        List<ContributorDTO> dbList = contributorDAO.findByStudy(studyId.get());
        if (!ListUtil.equalsWithoutOrder(dbList, study.getContributors())) {
          contributorDAO.deleteFromStudy(dbList);
          contributorDAO.insertIntoStudy(study.getContributors(), study.getId());
        }
        // update SOFTWARE
        updateStudyListItems(study.getId(), study.getSoftware(), DWFieldTypes.SOFTWARE);
        // update PUBONDATA
        updateStudyListItems(study.getId(), study.getPubOnData(), DWFieldTypes.PUBONDATA);
        // update CONFLINTEREST
        updateStudyListItems(study.getId(), study.getConflInterests(), DWFieldTypes.CONFLINTEREST);
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
        txManager.commit(status);
      } catch (Exception e) {
        log.error("ERROR", e);
        txManager.rollback(status);
      }
    } else {
      // TODO study null
    }
    redirectAttributes.addFlashAttribute("jQueryMapS", jQueryMap);
    return "redirect:/project/" + pid.get() + "/study/" + studyId.get();
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
    List<StudyInstrumentDTO> dbtmp = studyInstrumentDAO.findAllByStudy(studyId);
    if (!ListUtil.equalsWithoutOrder(dbtmp, list)) {
      List<StudyInstrumentDTO> insert = new ArrayList<>();
      List<StudyInstrumentDTO> delete = new ArrayList<>();
      List<StudyInstrumentDTO> update = new ArrayList<>();
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
              if (!tmp.getTitle().equals(tmp2.getTitle()) || !tmp.getAuthor().equals(tmp2.getAuthor())
                  || !tmp.getCitation().equals(tmp2.getCitation()) || !tmp.getSummary().equals(tmp2.getSummary())
                  || !tmp.getTheoHint().equals(tmp2.getTheoHint()) || !tmp.getStructure().equals(tmp2.getStructure())
                  || !tmp.getConstruction().equals(tmp2.getConstruction()) || !tmp.getNorm().equals(tmp2.getNorm())
                  || !tmp.getObjectivity().equals(tmp2.getObjectivity())
                  || !tmp.getReliability().equals(tmp2.getReliability())
                  || !tmp.getValidity().equals(tmp2.getValidity())) {
                update.add(tmp);
              }
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

  @RequestMapping(value = { "/{studyId}/switchEditMode" })
  public String switchEditMode(@ModelAttribute("StudyForm") StudyForm sForm, ModelMap model,
      RedirectAttributes redirectAttributes, @PathVariable final Optional<Long> pid,
      @PathVariable final Optional<Long> studyId) {
    log.trace("Entering changeStudyLock");
    StudyDTO study = sForm.getStudy();
    UserDTO user = UserUtil.getCurrentUser();
    String ret = checkStudyAccess(pid, studyId, redirectAttributes, true, user);
    if (ret != null)
      return ret;
    String actLock = (String) model.get("disStudyContent");
    StudyDTO currLock = null;
    try {
      currLock = studyDAO.findById(study.getId(), pid.get(), true);
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    if (currLock != null && (!currLock.isCurrentlyEdit()
        || (currLock.getEditSince().plusSeconds(sessionTimeout).compareTo(LocalDateTime.now()) < 0)
        || (currLock.getEditSince().plusSeconds(sessionTimeout).compareTo(LocalDateTime.now()) >= 0
            && user.getId() == currLock.getEditUserId()))) {
      if (actLock == null || actLock.isEmpty() || actLock.equals("enabled")) {
        if (studyDAO.switchStudyLock(study.getId(), user.getId(), true) > 0)
          actLock = "disabled";
      } else {
        if (studyDAO.switchStudyLock(study.getId(), user.getId(), false) > 0)
          actLock = "enabled";
      }
      model.put("disStudyContent", actLock);
    }
    model.put("studySubMenu", true);
    model.put("jQueryMap", model.get("jQueryMap"));
    return "study";
  }

  /**
   * Appends a ContributorDTO to the list of study contributors items taken from the existing project contributors.
   * After appending the item, it will be deleted from the project contributor list. If the study list is NULL, a new
   * ArrayList is created before a new item is appended.
   * 
   * @param sForm
   *          including all study data
   * @param model
   *          transporting the important parameter to the view
   * @return mapping to "study.jsp"
   */
  @RequestMapping(value = { "", "/{studyId}" }, method = RequestMethod.POST, params = "addContri")
  public String addContributor(@ModelAttribute("StudyForm") StudyForm sForm, ModelMap model) {
    log.trace("Entering addContributor");
    if (sForm.getStudy().getContributors() == null)
      sForm.getStudy().setContributors(new ArrayList<ContributorDTO>());
    if (sForm.getHiddenVar() >= 0 && sForm.getProjectContributors().size() > 0) {
      sForm.getStudy().getContributors().add(sForm.getProjectContributors().get(sForm.getHiddenVar()));
      sForm.getProjectContributors().remove(sForm.getHiddenVar());
      sForm.setHiddenVar(-1);
    }
    model.put("studySubMenu", true);
    model.put("jQueryMap", PageState.STUDYGENERAL);
    return "study";
  }

  /**
   * 
   * Deletes the selected ContributorDTO from the study contributors list. Before deleting, the selected item is
   * appended to the project contributor list for re-selection. If the study list is NULL, a new ArrayList is created
   * before a new item is appended.
   * 
   * @param sForm
   *          including all study data
   * @param model
   *          transporting the important parameter to the view
   * @return mapping to "study.jsp"
   */
  @RequestMapping(value = { "", "/{studyId}" }, method = RequestMethod.POST, params = "deleteContri")
  public String deleteContributor(@ModelAttribute("StudyForm") StudyForm sForm, ModelMap model) {
    log.trace("Entering deleteContributor");
    if (sForm.getDelPos() >= 0 && sForm.getStudy().getContributors().size() > 0) {
      sForm.getProjectContributors().add(sForm.getStudy().getContributors().get(sForm.getDelPos()));
      sForm.getStudy().getContributors().remove(sForm.getDelPos());
    }
    model.put("studySubMenu", true);
    model.put("jQueryMap", PageState.STUDYGENERAL);
    return "study";
  }

  /**
   * 
   * appends a StudyListTypesDTO to the List of Software items. If the List is NULL, a new ArrayList is created before
   * the new item is appended.
   * 
   * @param sForm
   *          including all study data
   * @param model
   *          transporting the important parameter to the view
   * @return mapping to "study.jsp"
   */
  @RequestMapping(value = { "", "/{studyId}" }, method = RequestMethod.POST, params = "addSoftware")
  public String addSoftware(@ModelAttribute("StudyForm") StudyForm sForm, ModelMap model) {
    log.trace("Entering addSoftware");
    if (sForm.getStudy().getSoftware() == null)
      sForm.getStudy().setSoftware(new ArrayList<StudyListTypesDTO>());
    sForm.getStudy().getSoftware().add((StudyListTypesDTO) applicationContext.getBean("StudyListTypesDTO"));
    model.put("studySubMenu", true);
    model.put("jQueryMap", PageState.STUDYGENERAL);
    return "study";
  }

  /**
   * 
   * appends a StudyListTypesDTO to the List of PubOnData items. If the List is NULL, a new ArrayList is created before
   * the new item is appended.
   * 
   * @param sForm
   *          including all study data
   * @param model
   *          transporting the important parameter to the view
   * @return mapping to "study.jsp"
   */
  @RequestMapping(value = { "", "/{studyId}" }, method = RequestMethod.POST, params = "addPubOnData")
  public String addPubOnData(@ModelAttribute("StudyForm") StudyForm sForm, ModelMap model) {
    log.trace("Entering addPubOnData");
    if (sForm.getStudy().getPubOnData() == null)
      sForm.getStudy().setPubOnData(new ArrayList<StudyListTypesDTO>());
    sForm.getStudy().getPubOnData().add((StudyListTypesDTO) applicationContext.getBean("StudyListTypesDTO"));
    model.put("studySubMenu", true);
    model.put("jQueryMap", PageState.STUDYGENERAL);
    return "study";
  }

  /**
   * 
   * appends a StudyListTypesDTO to the List of ConflInterests items. If the List is NULL, a new ArrayList is created
   * before the new item is appended.
   * 
   * @param sForm
   *          including all study data
   * @param model
   *          transporting the important parameter to the view
   * @return mapping to "study.jsp"
   */
  @RequestMapping(value = { "", "/{studyId}" }, method = RequestMethod.POST, params = "addConflInterests")
  public String addConflInterests(@ModelAttribute("StudyForm") StudyForm sForm, ModelMap model) {
    log.trace("Entering addConflInterests");
    if (sForm.getStudy().getConflInterests() == null)
      sForm.getStudy().setConflInterests(new ArrayList<StudyListTypesDTO>());
    sForm.getStudy().getConflInterests().add((StudyListTypesDTO) applicationContext.getBean("StudyListTypesDTO"));
    model.put("studySubMenu", true);
    model.put("jQueryMap", PageState.STUDYGENERAL);
    return "study";
  }

  /**
   * 
   * appends a StudyObjectivesDTO to the List of Objectives items. If the List is NULL, a new ArrayList is created
   * before the new item is appended.
   * 
   * @param sForm
   *          including all study data
   * @param model
   *          transporting the important parameter to the view
   * @return mapping to "study.jsp"
   */
  @RequestMapping(value = { "", "/{studyId}" }, method = RequestMethod.POST, params = "addObjectives")
  public String addObjectives(@ModelAttribute("StudyForm") StudyForm sForm, ModelMap model) {
    log.trace("Entering addObjectives");
    if (sForm.getStudy().getObjectives() == null)
      sForm.getStudy().setObjectives(new ArrayList<StudyListTypesDTO>());
    sForm.getStudy().getObjectives().add((StudyListTypesDTO) applicationContext.getBean("StudyListTypesDTO"));
    model.put("studySubMenu", true);
    model.put("jQueryMap", PageState.STUDYDESIGN);
    return "study";
  }

  /**
   * 
   * appends a StudyListTypesDTO to the List of RelTheorys items. If the List is NULL, a new ArrayList is created before
   * the new item is appended.
   * 
   * @param sForm
   *          including all study data
   * @param model
   *          transporting the important parameter to the view
   * @return mapping to "study.jsp"
   */
  @RequestMapping(value = { "", "/{studyId}" }, method = RequestMethod.POST, params = "addRelTheorys")
  public String addRelTheorys(@ModelAttribute("StudyForm") StudyForm sForm, ModelMap model) {
    log.trace("Entering addRelTheorys");
    if (sForm.getStudy().getRelTheorys() == null)
      sForm.getStudy().setRelTheorys(new ArrayList<StudyListTypesDTO>());
    sForm.getStudy().getRelTheorys().add((StudyListTypesDTO) applicationContext.getBean("StudyListTypesDTO"));
    model.put("studySubMenu", true);
    model.put("jQueryMap", PageState.STUDYDESIGN);
    return "study";
  }

  /**
   * 
   * appends a StudyListTypesDTO to the List of InterArms items. If the List is NULL, a new ArrayList is created before
   * the new item is appended.
   * 
   * @param sForm
   *          including all study data
   * @param model
   *          transporting the important parameter to the view
   * @return mapping to "study.jsp"
   */
  @RequestMapping(value = { "", "/{studyId}" }, method = RequestMethod.POST, params = "addInterArms")
  public String addInterArms(@ModelAttribute("StudyForm") StudyForm sForm, ModelMap model) {
    log.trace("Entering addInterArms");
    if (sForm.getStudy().getInterArms() == null)
      sForm.getStudy().setInterArms(new ArrayList<StudyListTypesDTO>());
    sForm.getStudy().getInterArms().add((StudyListTypesDTO) applicationContext.getBean("StudyListTypesDTO"));
    model.put("studySubMenu", true);
    model.put("jQueryMap", PageState.STUDYDESIGN);
    return "study";
  }

  /**
   * 
   * appends a StudyListTypesDTO to the List of MeasOccName items. If the List is NULL, a new ArrayList is created
   * before the new item is appended.
   * 
   * @param sForm
   *          including all study data
   * @param model
   *          transporting the important parameter to the view
   * @return mapping to "study.jsp"
   */
  @RequestMapping(value = { "", "/{studyId}" }, method = RequestMethod.POST, params = "addMeasOccName")
  public String addMeasOccName(@ModelAttribute("StudyForm") StudyForm sForm, ModelMap model) {
    log.trace("Entering addMeasOccName");
    if (sForm.getStudy().getMeasOcc() == null)
      sForm.getStudy().setMeasOcc(new ArrayList<StudyListTypesDTO>());
    sForm.getStudy().getMeasOcc().add((StudyListTypesDTO) applicationContext.getBean("StudyListTypesDTO"));
    model.put("studySubMenu", true);
    model.put("jQueryMap", PageState.STUDYDESIGN);
    return "study";
  }

  /**
   * 
   * appends a studyInstrumentDAO to the List of Construct items. If the List is NULL, a new ArrayList is created before
   * the new item is appended.
   * 
   * @param sForm
   *          including all study data
   * @param model
   *          transporting the important parameter to the view
   * @return mapping to "study.jsp"
   */
  @RequestMapping(value = { "", "/{studyId}" }, method = RequestMethod.POST, params = "addConstruct")
  public String addConstruct(@ModelAttribute("StudyForm") StudyForm sForm, ModelMap model) {
    log.trace("Entering addConstruct");
    if (sForm.getStudy().getConstructs() == null)
      sForm.getStudy().setConstructs(new ArrayList<StudyConstructDTO>());
    sForm.getStudy().getConstructs().add((StudyConstructDTO) applicationContext.getBean("StudyConstructDTO"));
    model.put("studySubMenu", true);
    model.put("jQueryMap", PageState.STUDYDESIGN);
    return "study";
  }

  /**
   * 
   * appends a StudyInstrumentDTO to the List of Instrument items. If the List is NULL, a new ArrayList is created
   * before the new item is appended.
   * 
   * @param sForm
   *          including all study data
   * @param model
   *          transporting the important parameter to the view
   * @return mapping to "study.jsp"
   */
  @RequestMapping(value = { "", "/{studyId}" }, method = RequestMethod.POST, params = "addInstrument")
  public String addInstrument(@ModelAttribute("StudyForm") StudyForm sForm, ModelMap model) {
    log.trace("Entering addInstrument");
    if (sForm.getStudy().getInstruments() == null)
      sForm.getStudy().setInstruments(new ArrayList<StudyInstrumentDTO>());
    sForm.getStudy().getInstruments().add((StudyInstrumentDTO) applicationContext.getBean("StudyInstrumentDTO"));
    model.put("studySubMenu", true);
    model.put("jQueryMap", PageState.STUDYDESIGN);
    return "study";
  }

  /**
   * 
   * appends a StudyListTypesDTO to the List of Eligibilities items. If the List is NULL, a new ArrayList is created
   * before the new item is appended.
   * 
   * @param sForm
   *          including all study data
   * @param model
   *          transporting the important parameter to the view
   * @return mapping to "study.jsp"
   */
  @RequestMapping(value = { "", "/{studyId}" }, method = RequestMethod.POST, params = "addEligibilities")
  public String addEligibilities(@ModelAttribute("StudyForm") StudyForm sForm, ModelMap model) {
    log.trace("Entering addEligibilities");
    if (sForm.getStudy().getEligibilities() == null)
      sForm.getStudy().setEligibilities(new ArrayList<StudyListTypesDTO>());
    sForm.getStudy().getEligibilities().add((StudyListTypesDTO) applicationContext.getBean("StudyListTypesDTO"));
    model.put("studySubMenu", true);
    model.put("jQueryMap", PageState.STUDYSAMPLE);
    return "study";
  }

  /**
   * 
   * @param pid
   * @param studyId
   * @param redirectAttributes
   * @param onlyWrite
   * @return
   */
  private String checkStudyAccess(final Optional<Long> pid, final Optional<Long> studyId,
      final RedirectAttributes redirectAttributes, final boolean onlyWrite, final UserDTO user) {
    if (user == null) {
      log.warn("Auth User Object == null - redirect to login");
      return "redirect:/login";
    }
    if (!pid.isPresent() || pUtil.checkProjectRoles(user, pid.get(), studyId.isPresent() ? studyId.get() : -1,
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
   * @param studyId
   * @param study
   * @throws Exception
   */
  private void setStudyDTO(final Optional<Long> studyId, final StudyDTO study) throws Exception {
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
      study.setInstruments(studyInstrumentDAO.findAllByStudy(studyId.get()));
      study.setEligibilities(studyListTypesDAO.findAllByStudyAndType(studyId.get(), DWFieldTypes.ELIGIBILITY));
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
}
