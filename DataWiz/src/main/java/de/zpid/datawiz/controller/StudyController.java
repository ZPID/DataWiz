package de.zpid.datawiz.controller;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.SmartValidator;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import de.zpid.datawiz.dto.ContributorDTO;
import de.zpid.datawiz.dto.StudyConstructDTO;
import de.zpid.datawiz.dto.StudyDTO;
import de.zpid.datawiz.dto.StudyInstrumentDTO;
import de.zpid.datawiz.dto.StudyListTypesDTO;
import de.zpid.datawiz.dto.UserDTO;
import de.zpid.datawiz.enumeration.PageState;
import de.zpid.datawiz.form.StudyForm;
import de.zpid.datawiz.service.ExceptionService;
import de.zpid.datawiz.service.ProjectService;
import de.zpid.datawiz.service.RecordService;
import de.zpid.datawiz.service.StudyService;
import de.zpid.datawiz.util.BreadCrumpUtil;
import de.zpid.datawiz.util.UserUtil;

@Controller
@RequestMapping(value = { "/study", "/project/{pid}/study" })
@SessionAttributes({ "StudyForm", "subnaviActive", "breadcrumpList", "disStudyContent" })
public class StudyController {

  @Autowired
  protected MessageSource messageSource;
  @Autowired
  private StudyService studyService;
  @Autowired
  private ExceptionService exceptionService;
  @Autowired
  protected ClassPathXmlApplicationContext applicationContext;
  @Autowired
  protected SmartValidator validator;
  @Autowired
  private RecordService recordService;
  @Autowired
  private ProjectService projectService;

  private static Logger log = LogManager.getLogger(StudyController.class);

  public StudyController() {
    super();
    log.info("Loading StudyController for mapping /study");
  }

  @ModelAttribute("StudyForm")
  protected StudyForm createStudyForm() {
    return (StudyForm) applicationContext.getBean("StudyForm");
  }

  /**
   * 
   * @param pid
   * @param studyId
   * @param model
   * @param redirectAttributes
   * @param jQueryMapS
   * @return
   */
  @RequestMapping(value = { "", "/{studyId}" }, method = RequestMethod.GET)
  public String showStudyPage(@ModelAttribute("StudyForm") StudyForm sForm, @PathVariable final Optional<Long> pid,
      @PathVariable final Optional<Long> studyId, final ModelMap model, final RedirectAttributes redirectAttributes) {
    String ret;
    final UserDTO user = UserUtil.getCurrentUser();
    if (studyId.isPresent()) {
      log.trace("Entering showStudyPage(edit) for study [id: {}]", () -> studyId.get());
      ret = projectService.checkUserAccess(pid, studyId, redirectAttributes, false, user);
    } else {
      log.trace("Entering showStudyPage(create) study");
      ret = projectService.checkUserAccess(pid, studyId, redirectAttributes, true, user);
    }
    String jQueryMap = "";
    if (sForm != null && sForm.getjQueryMap() != null && !sForm.getjQueryMap().isEmpty()) {
      jQueryMap = sForm.getjQueryMap();
    }
    sForm = createStudyForm();
    sForm.setjQueryMap(jQueryMap);
    String accessState = "disabled";
    if (ret == null) {
      try {
        accessState = studyService.setStudyForm(pid, studyId, redirectAttributes, user, sForm);
        studyService.createStudyBreadCrump(sForm.getProject().getTitle(),
            sForm.getStudy() != null ? sForm.getStudy().getTitle() : null, pid.get(), model);
        model.put("disStudyContent", accessState);
        model.put("StudyForm", sForm);
        model.put("studySubMenu", true);
        model.put("subnaviActive", PageState.STUDY.name());
        ret = "study";
      } catch (Exception e) {
        ret = exceptionService.setErrorMessagesAndRedirects(pid, studyId, null, model, redirectAttributes, e,
            "studyService.setStudyForm");
      }
    }
    log.trace("Method showStudyPage completed");
    return ret;
  }

  /**
   * 
   * @param pid
   * @param studyId
   * @param model
   * @param redirectAttributes
   * @return
   */
  @RequestMapping(value = { "/{studyId}/records" }, method = RequestMethod.GET)
  public String showRecordOverview(@PathVariable final Optional<Long> pid, @PathVariable final Optional<Long> studyId,
      final ModelMap model, final RedirectAttributes redirectAttributes) {
    final UserDTO user = UserUtil.getCurrentUser();
    log.trace("Entering showRecordOverview for study [id: {}] and user [email: {}]", () -> studyId.get(),
        () -> user.getEmail());
    String ret = projectService.checkUserAccess(pid, studyId, redirectAttributes, false, user);
    if (ret == null) {
      try {
        StudyForm sForm = createStudyForm();
        studyService.setRecordList(pid, studyId, redirectAttributes, sForm);
        model.put("breadcrumpList",
            BreadCrumpUtil.generateBC(PageState.STUDY,
                new String[] { sForm.getProject().getTitle(), sForm.getStudy().getTitle() }, new long[] { pid.get() },
                messageSource));
        model.put("studySubMenu", true);
        model.put("subnaviActive", PageState.RECORDS.name());
        model.put("StudyForm", sForm);
        ret = "records";
      } catch (Exception e) {
        ret = exceptionService.setErrorMessagesAndRedirects(pid, studyId, null, model, redirectAttributes, e,
            "studyService.setRecordList");
      }
    }
    return ret;
  }

  /**
   * 
   * @param sForm
   * @param model
   * @param redirectAttributes
   * @param bRes
   * @param studyId
   * @param pid
   * @return
   */
  @RequestMapping(value = { "", "/{studyId}" }, method = RequestMethod.POST)
  public String saveStudy(@ModelAttribute("StudyForm") StudyForm sForm, ModelMap model,
      RedirectAttributes redirectAttributes, BindingResult bRes, @PathVariable final Optional<Long> studyId,
      @PathVariable final Optional<Long> pid) {
    String ret;
    final UserDTO user = UserUtil.getCurrentUser();
    if (studyId.isPresent()) {
      log.trace("Entering saveStudy(edit) for study [id: {}]", () -> studyId.get());
      ret = projectService.checkUserAccess(pid, studyId, redirectAttributes, false, user);
    } else {
      log.trace("Entering saveStudy(create)");
      ret = projectService.checkUserAccess(pid, studyId, redirectAttributes, true, user);
    }
    if (ret != null)
      return ret;
    Set<String> validateErrors = new HashSet<String>();
    boolean error = validateStudyForm(sForm, bRes, StudyDTO.StGeneralVal.class, PageState.STUDYGENERAL, validateErrors);
    error = validateStudyForm(sForm, bRes, StudyDTO.StDesignVal.class, PageState.STUDYDESIGN, validateErrors) ? true
        : error;
    error = validateStudyForm(sForm, bRes, StudyDTO.StEthicalVal.class, PageState.STUDYETHICAL, validateErrors) ? true
        : error;
    error = validateStudyForm(sForm, bRes, StudyDTO.StSampleVal.class, PageState.STUDYSAMPLE, validateErrors) ? true
        : error;
    error = validateStudyForm(sForm, bRes, StudyDTO.StSurveyVal.class, PageState.STUDYSURVEY, validateErrors) ? true
        : error;
    if (error) {
      model.put("studySubMenu", true);
      model.put("subnaviActive", PageState.STUDY.name());
      model.put("errorMSG", recordService.setMessageString(validateErrors));
      return "study";
    }
    StudyDTO study = studyService.saveStudyForm(sForm, studyId, pid, user);
    return "redirect:/project/" + pid.get() + "/study/" + study.getId();
  }

  // TODO error TXT
  private boolean validateStudyForm(final StudyForm sForm, final BindingResult bRes, final Class<?> cls,
      final PageState state, Set<String> validateErrors) {
    boolean error = false;
    if (sForm != null && bRes != null) {
      BeanPropertyBindingResult bResTmp = new BeanPropertyBindingResult(sForm, bRes.getObjectName());
      validator.validate(sForm, bResTmp, cls);
      //bResTmp.getAllErrors().forEach(System.out::println);
      if (bResTmp.hasErrors()) {
        error = true;
        switch (state) {
        case STUDYGENERAL:
          validateErrors.add("ERROR GENERAL");
          break;
        case STUDYDESIGN:
          validateErrors.add("ERROR DESIGN");
          break;
        case STUDYETHICAL:
          validateErrors.add("ERROR ETHICAL");
          break;
        case STUDYSAMPLE:
          validateErrors.add("ERROR SAMPLE");
          break;
        case STUDYSURVEY:
          validateErrors.add("ERROR SURvey");
          break;
        default:
          validateErrors.add("ERROR DEFAULT");
          break;
        }
        for (ObjectError errtmp : bResTmp.getAllErrors()) {
          bRes.addError(errtmp);
        }
      }
    } else {
      error = true;
    }
    return error;
  }

  /**
   * 
   * @param sForm
   * @param model
   * @param redirectAttributes
   * @param pid
   * @param studyId
   * @return
   */
  @RequestMapping(value = { "/{studyId}/switchEditMode" })
  public String switchEditMode(@ModelAttribute("StudyForm") StudyForm sForm, ModelMap model,
      RedirectAttributes redirectAttributes, @PathVariable final Optional<Long> pid,
      @PathVariable final Optional<Long> studyId) {
    log.trace("Entering changeStudyLock");
    UserDTO user = UserUtil.getCurrentUser();
    String ret = projectService.checkUserAccess(pid, studyId, redirectAttributes, true, user);
    if ((sForm == null || sForm.getStudy() == null || sForm.getProject() == null || sForm.getProject().getId() == 0)
        && ret == null)
      ret = "redirect:/project/" + pid.get() + "/study/" + studyId.get();
    if (ret == null) {
      ret = "study";
      try {
        model.put("disStudyContent",
            studyService.checkActualLock(pid, studyId, user, (String) model.get("disStudyContent"), model));
        model.put("studySubMenu", true);
        model.put("subnaviActive", PageState.STUDY.name());
      } catch (Exception e) {
        ret = exceptionService.setErrorMessagesAndRedirects(pid, studyId, null, model, redirectAttributes, e,
            "studyService.checkActualLock");
      }
    }
    return ret;
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
    model.put("subnaviActive", PageState.STUDY.name());
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
    model.put("subnaviActive", PageState.STUDY.name());
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
    model.put("subnaviActive", PageState.STUDY.name());
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
    model.put("subnaviActive", PageState.STUDY.name());
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
    model.put("subnaviActive", PageState.STUDY.name());
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
    model.put("subnaviActive", PageState.STUDY.name());
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
    model.put("subnaviActive", PageState.STUDY.name());
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
    model.put("subnaviActive", PageState.STUDY.name());
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
    model.put("subnaviActive", PageState.STUDY.name());
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
    model.put("subnaviActive", PageState.STUDY.name());
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
    model.put("subnaviActive", PageState.STUDY.name());
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
    model.put("subnaviActive", PageState.STUDY.name());
    return "study";
  }

}
