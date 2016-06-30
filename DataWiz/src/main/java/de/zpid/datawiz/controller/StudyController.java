package de.zpid.datawiz.controller;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import de.zpid.datawiz.dto.ContributorDTO;
import de.zpid.datawiz.dto.ProjectDTO;
import de.zpid.datawiz.dto.StudyConstructDTO;
import de.zpid.datawiz.dto.StudyDTO;
import de.zpid.datawiz.dto.StudyInstrumentDTO;
import de.zpid.datawiz.dto.StudyListTypesDTO;
import de.zpid.datawiz.dto.StudyObjectivesDTO;
import de.zpid.datawiz.dto.UserDTO;
import de.zpid.datawiz.enumeration.DWFieldTypes;
import de.zpid.datawiz.enumeration.PageState;
import de.zpid.datawiz.form.StudyForm;
import de.zpid.datawiz.util.BreadCrumpUtil;
import de.zpid.datawiz.util.UserUtil;

@Controller
@RequestMapping(value = { "/study", "/project/{pid}/study" })
@SessionAttributes({ "StudyForm", "subnaviActive", "breadcrumpList" })
public class StudyController extends SuperController {
  
  private static Logger log = LogManager.getLogger(StudyController.class);

  public StudyController() {
    super();
    log.info("Loading StudyController for mapping /study");
  }

  @RequestMapping(value = { "", "/{studyId}", }, method = RequestMethod.GET)
  public String showStudyPage(@PathVariable final Optional<Long> pid, @PathVariable final Optional<Long> studyId,
      ModelMap model) {
    if (studyId.isPresent()) {
      log.trace("Entering showStudyPage(edit) for study [id: {}]", () -> studyId.get());
    } else {
      log.trace("Entering showStudyPage(create) study");
    }
    if (!pid.isPresent()) {
      // TODO ausstieg - pid fehlt!!!
    }
    final UserDTO user = UserUtil.getCurrentUser();
    if (user == null) {
      log.warn("Auth User Object == null - redirect to login");
      return "redirect:/login";
    }
    StudyForm sForm = createStudyForm();
    try {
      if (pUtil.checkProjectRoles(user, pid.get(), false, true) == null) {
        // TODO
      }
      ProjectDTO project = projectDAO.findById(pid.get());
      if (project == null) {
        // TODO loggin
        return "redirect:/panel";
      }
      sForm.setProject(project);
      List<ContributorDTO> pContri = contributorDAO.findByProject(project, false, true);
      if (studyId.isPresent()) {
        final StudyDTO study = studyDAO.findById(studyId.get());
        setStudyDTO(studyId, study);
        sForm.setCollectionModes(formTypeDAO.findAllByType(true, DWFieldTypes.COLLECTIONMODE));
        sForm.setSourFormat(formTypeDAO.findAllByType(true, DWFieldTypes.DATAFORMAT));
        sForm.setStudy(study);
        cleanContributorList(pContri, study.getContributors());
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
    model.put("StudyForm", sForm);
    model.put("studySubMenu", true);
    model.put("jQueryMap", PageState.STUDYGENERAL);
    model.put("subnaviActive", PageState.STUDY.name());
    log.trace("Method showStudyPage successfully completed");
    return "study";
  }

  @RequestMapping(value = { "", "/{studyId}", }, method = RequestMethod.POST)
  public String saveStudy(@ModelAttribute("StudyForm") StudyForm sForm, ModelMap model,
      RedirectAttributes redirectAttributes, BindingResult bRes, @PathVariable final Optional<Long> studyId) {
    log.trace("Entering saveStudy");

    model.put("studySubMenu", true);
    model.put("jQueryMap", PageState.STUDYGENERAL);
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
  @RequestMapping(value = { "", "/{studyId}", }, method = RequestMethod.POST, params = "addContri")
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
  @RequestMapping(value = { "", "/{studyId}", }, method = RequestMethod.POST, params = "deleteContri")
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
  @RequestMapping(value = { "", "/{studyId}", }, method = RequestMethod.POST, params = "addSoftware")
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
  @RequestMapping(value = { "", "/{studyId}", }, method = RequestMethod.POST, params = "addPubOnData")
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
  @RequestMapping(value = { "", "/{studyId}", }, method = RequestMethod.POST, params = "addConflInterests")
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
  @RequestMapping(value = { "", "/{studyId}", }, method = RequestMethod.POST, params = "addObjectives")
  public String addObjectives(@ModelAttribute("StudyForm") StudyForm sForm, ModelMap model) {
    log.trace("Entering addObjectives");
    if (sForm.getStudy().getObjectives() == null)
      sForm.getStudy().setObjectives(new ArrayList<StudyObjectivesDTO>());
    sForm.getStudy().getObjectives().add((StudyObjectivesDTO) applicationContext.getBean("StudyObjectivesDTO"));
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
  @RequestMapping(value = { "", "/{studyId}", }, method = RequestMethod.POST, params = "addRelTheorys")
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
  @RequestMapping(value = { "", "/{studyId}", }, method = RequestMethod.POST, params = "addInterArms")
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
  @RequestMapping(value = { "", "/{studyId}", }, method = RequestMethod.POST, params = "addMeasOccName")
  public String addMeasOccName(@ModelAttribute("StudyForm") StudyForm sForm, ModelMap model) {
    log.trace("Entering addMeasOccName");
    if (sForm.getStudy().getMeasOccName() == null)
      sForm.getStudy().setMeasOccName(new ArrayList<StudyListTypesDTO>());
    sForm.getStudy().getMeasOccName().add((StudyListTypesDTO) applicationContext.getBean("StudyListTypesDTO"));
    model.put("studySubMenu", true);
    model.put("jQueryMap", PageState.STUDYDESIGN);
    return "study";
  }

  /**
   * 
   * appends a StudyConstructDTO to the List of Construct items. If the List is NULL, a new ArrayList is created before
   * the new item is appended.
   * 
   * @param sForm
   *          including all study data
   * @param model
   *          transporting the important parameter to the view
   * @return mapping to "study.jsp"
   */
  @RequestMapping(value = { "", "/{studyId}", }, method = RequestMethod.POST, params = "addConstruct")
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
  @RequestMapping(value = { "", "/{studyId}", }, method = RequestMethod.POST, params = "addInstrument")
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
  @RequestMapping(value = { "", "/{studyId}", }, method = RequestMethod.POST, params = "addEligibilities")
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
      study.setMeasOccName(studyListTypesDAO.findAllByStudyAndType(studyId.get(), DWFieldTypes.MEASOCCNAME));
      study.setInterArms(studyListTypesDAO.findAllByStudyAndType(studyId.get(), DWFieldTypes.INTERARMS));
      study.setObjectives(studyObjectivesDAO.findAllByStudy(studyId.get()));
      study.setConstructs(studyConstructDAO.findAllByStudy(studyId.get()));
      study.setInstruments(studyInstrumentDAO.findAllByStudy(studyId.get()));
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
