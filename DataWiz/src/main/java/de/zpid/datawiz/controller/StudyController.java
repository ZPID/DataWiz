package de.zpid.datawiz.controller;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.env.Environment;
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
import de.zpid.datawiz.dto.StudyConstructDTO;
import de.zpid.datawiz.dto.StudyDTO;
import de.zpid.datawiz.dto.StudyInstrumentDTO;
import de.zpid.datawiz.dto.StudyListTypesDTO;
import de.zpid.datawiz.dto.UserDTO;
import de.zpid.datawiz.enumeration.DataWizErrorCodes;
import de.zpid.datawiz.enumeration.PageState;
import de.zpid.datawiz.exceptions.DataWizSystemException;
import de.zpid.datawiz.form.StudyForm;
import de.zpid.datawiz.service.ExceptionService;
import de.zpid.datawiz.service.ProjectService;
import de.zpid.datawiz.service.RecordService;
import de.zpid.datawiz.service.StudyService;
import de.zpid.datawiz.util.BreadCrumpUtil;
import de.zpid.datawiz.util.UserUtil;

/**
 * Controller for mapping "/project/{pid}/study" <br />
 * <br />
 * This file is part of Datawiz.<br />
 * 
 * <b>Copyright 2017, Leibniz Institute for Psychology Information (ZPID),
 * <a href="http://zpid.de" title="http://zpid.de">http://zpid.de</a>.</b><br />
 * <br />
 * <a rel="license" href="http://creativecommons.org/licenses/by-nc-sa/4.0/"><img alt="Creative Commons License" style= "border-width:0" src=
 * "https://i.creativecommons.org/l/by-nc-sa/4.0/80x15.png" /></a><br />
 * <span xmlns:dct="http://purl.org/dc/terms/" property="dct:title">Datawiz</span> by
 * <a xmlns:cc="http://creativecommons.org/ns#" href="zpid.de" property="cc:attributionName" rel="cc:attributionURL"> Leibniz Institute for Psychology
 * Information (ZPID)</a> is licensed under a <a rel="license" href="http://creativecommons.org/licenses/by-nc-sa/4.0/">Creative Commons
 * Attribution-NonCommercial-ShareAlike 4.0 International License</a>.
 * 
 * @author Ronny Boelter
 * @version 1.0
 *
 * 
 *
 */
@Controller
@RequestMapping(value = { "/study", "/project/{pid}/study" })
@SessionAttributes({ "StudyForm", "subnaviActive", "breadcrumpList", "disStudyContent" })
public class StudyController {

	@Autowired
	private MessageSource messageSource;
	@Autowired
	private StudyService studyService;
	@Autowired
	private ExceptionService exceptionService;
	@Autowired
	private ClassPathXmlApplicationContext applicationContext;
	@Autowired
	private RecordService recordService;
	@Autowired
	private ProjectService projectService;
	@Autowired
	private Environment env;
	private static Logger log = LogManager.getLogger(StudyController.class);

	/**
	 * Instantiates a new study controller.
	 */
	public StudyController() {
		super();
		log.info("Loading StudyController for mapping /study");
	}

	/**
	 * Creates the study form.
	 *
	 * @return the study form
	 */
	@ModelAttribute("StudyForm")
	private StudyForm createStudyForm() {
		return (StudyForm) applicationContext.getBean("StudyForm");
	}

	/**
	 * This function load the study data and passes it to study.jsp. It distinguishes between two cases: First: study identifier is present - edit study
	 * data; second study identifier is not present - create new study.
	 *
	 * @param sForm
	 *          {@link StudyForm}
	 * @param pid
	 *          Project Identifier as {@link Optional}&lt;{@link Long}&gt;
	 * @param studyId
	 *          Study Identifier as {@link Optional}&lt;{@link Long}&gt;
	 * @param model
	 *          {@link ModelMap}
	 * @param redirectAttributes
	 *          {@link RedirectAttributes}
	 * @return @return Mapping to study.jsp on success, otherwise exception handling and mapping via exceptionService.setErrorMessagesAndRedirects
	 */
	@RequestMapping(value = { "", "/{studyId}" }, method = RequestMethod.GET)
	public String showStudyPage(@ModelAttribute("StudyForm") StudyForm sForm, @PathVariable final Optional<Long> pid,
	    @PathVariable final Optional<Long> studyId, final ModelMap model, final RedirectAttributes redirectAttributes) {
		String ret;
		final UserDTO user = UserUtil.getCurrentUser();
		String jQueryMap = null;
		if (studyId.isPresent()) {
			log.trace("Entering showStudyPage(edit) for study [id: {}]", () -> studyId.get());
			ret = projectService.checkUserAccess(pid, studyId, redirectAttributes, false, user);
			if (sForm != null && sForm.getjQueryMap() != null && !sForm.getjQueryMap().isEmpty()) {
				jQueryMap = sForm.getjQueryMap();
			}
		} else {
			log.trace("Entering showStudyPage(create) study");
			ret = projectService.checkUserAccess(pid, studyId, redirectAttributes, true, user);
		}
		sForm = createStudyForm();
		sForm.setjQueryMap(jQueryMap);
		String accessState = "disabled";
		if (ret == null) {
			try {
				accessState = studyService.setStudyForm(pid, studyId, redirectAttributes, user, sForm);
				studyService.createStudyBreadCrump(sForm.getProject().getTitle(), sForm.getStudy() != null ? sForm.getStudy().getTitle() : null, pid.get(),
				    model);
				model.put("disStudyContent", accessState);
				model.put("StudyForm", sForm);
				model.put("studySubMenu", true);
				model.put("subnaviActive", PageState.STUDY.name());
				ret = "study";
			} catch (Exception e) {
				ret = exceptionService.setErrorMessagesAndRedirects(pid, studyId, null, model, redirectAttributes, e, "studyService.setStudyForm");
			}
		} else {
			log.warn("Unexpected event: User [email: {}] tries to showStudyPage for Study [id: {}] without permissions", () -> user.getEmail(),
			    () -> studyId);
		}
		log.trace("Method showStudyPage completed with mapping to \"{}\"", ret);
		return ret;
	}

	/**
	 * This function loads all records which belongs to the passed study
	 * 
	 * @param pid
	 *          Project Identifier as {@link Optional}&lt;{@link Long}&gt;
	 * @param studyId
	 *          Study Identifier as {@link Optional}&lt;{@link Long}&gt;
	 * @param model
	 *          {@link ModelMap}
	 * @param redirectAttributes
	 *          {@link RedirectAttributes}
	 * @return Mapping to record.jsp on success, otherwise exception handling and mapping via exceptionService.setErrorMessagesAndRedirects
	 */
	@RequestMapping(value = { "/{studyId}/records" }, method = RequestMethod.GET)
	public String showRecordOverview(@PathVariable final Optional<Long> pid, @PathVariable final Optional<Long> studyId, final ModelMap model,
	    final RedirectAttributes redirectAttributes) {
		final UserDTO user = UserUtil.getCurrentUser();
		log.trace("Entering showRecordOverview for study [id: {}] and user [email: {}]", () -> studyId.get(), () -> user.getEmail());
		String ret = projectService.checkUserAccess(pid, studyId, redirectAttributes, false, user);
		if (ret == null) {
			try {
				StudyForm sForm = createStudyForm();
				studyService.setRecordList(pid, studyId, redirectAttributes, sForm);
				model.put("breadcrumpList", BreadCrumpUtil.generateBC(PageState.STUDY,
				    new String[] { sForm.getProject().getTitle(), sForm.getStudy().getTitle() }, new long[] { pid.get() }, messageSource));
				model.put("studySubMenu", true);
				model.put("subnaviActive", PageState.RECORDS.name());
				model.put("StudyForm", sForm);
				ret = "records";
			} catch (Exception e) {
				ret = exceptionService.setErrorMessagesAndRedirects(pid, studyId, null, model, redirectAttributes, e, "studyService.setRecordList");
			}
		} else {
			log.warn("Unexpected event: User [email: {}] tries to showRecordOverview for Study [id: {}] without permissions", () -> user.getEmail(),
			    () -> studyId);
		}
		log.trace("Method showRecordOverview completed with mapping to \"{}\"", ret);
		return ret;
	}

	/**
	 * This function validates and saves the Study Data and returns errors to the view if the validation is not successful.
	 *
	 * @param sForm
	 *          StudyForm
	 * @param model
	 *          ModelMap
	 * @param redirectAttributes
	 *          RedirectAttributes
	 * @param bRes
	 *          BindingResult
	 * @param studyId
	 *          Study Identifier as Optional&lt;Long&gt;
	 * @param pid
	 *          Project Identifier as Optional&lt;Long&gt;
	 * @return Mapping to recirect:study on success, otherwise to study.jsp with error messages
	 */
	@RequestMapping(value = { "", "/{studyId}" }, method = RequestMethod.POST)
	public String saveStudy(@ModelAttribute("StudyForm") StudyForm sForm, ModelMap model, RedirectAttributes redirectAttributes, BindingResult bRes,
	    @PathVariable final Optional<Long> studyId, @PathVariable final Optional<Long> pid) {
		log.trace("Entering saveStudy for Study [id: {}]", () -> studyId);
		String ret;
		final UserDTO user = UserUtil.getCurrentUser();
		if (studyId.isPresent()) {
			log.trace("Entering saveStudy(edit) for study [id: {}]", () -> studyId.get());
			ret = projectService.checkUserAccess(pid, studyId, redirectAttributes, false, user);
		} else {
			log.trace("Entering saveStudy(create)");
			ret = projectService.checkUserAccess(pid, studyId, redirectAttributes, true, user);
		}
		if (ret == null) {
			Set<String> validateErrors = new HashSet<String>();
			boolean error = studyService.validateStudyForm(sForm, bRes, StudyDTO.StGeneralVal.class, PageState.STUDYGENERAL, validateErrors);
			error = studyService.validateStudyForm(sForm, bRes, StudyDTO.StDesignVal.class, PageState.STUDYDESIGN, validateErrors) ? true : error;
			error = studyService.validateStudyForm(sForm, bRes, StudyDTO.StEthicalVal.class, PageState.STUDYETHICAL, validateErrors) ? true : error;
			error = studyService.validateStudyForm(sForm, bRes, StudyDTO.StSampleVal.class, PageState.STUDYSAMPLE, validateErrors) ? true : error;
			error = studyService.validateStudyForm(sForm, bRes, StudyDTO.StSurveyVal.class, PageState.STUDYSURVEY, validateErrors) ? true : error;
			if (error) {
				model.put("studySubMenu", true);
				model.put("subnaviActive", PageState.STUDY.name());
				model.put("errorMSG", recordService.setMessageString(validateErrors));
				ret = "study";
			} else {
				StudyDTO study = studyService.saveStudyForm(sForm, studyId, pid, user);
				if (study != null)
					ret = "redirect:/project/" + pid.get() + "/study/" + study.getId();
				else {
					log.warn("Unexpected event: StudyForm is emtpy - Database Error During studyService.saveStudyForm - Transaction was rolled back");
					ret = "study";
				}
			}
		} else {
			log.warn("Unexpected event: User [email: {}] tries to saveStudy for Study [id: {}] without permissions", () -> user.getEmail(), () -> studyId);
		}
		log.trace("Method saveStudy completed with mapping to \"{}\"", ret);
		return ret;
	}

	/**
	 * This function switches the edit mode. The study has a lock, so that only one user can edit the study data at the same time. If the study is
	 * locked by a user, other user have to wait until the user unlock the study, or the session time is over and the study automatically unlocks.
	 *
	 * @param sForm
	 *          the s form
	 * @param model
	 *          the model
	 * @param redirectAttributes
	 *          the redirect attributes
	 * @param pid
	 *          the pid
	 * @param studyId
	 *          the study id
	 * @return the string
	 */
	@RequestMapping(value = { "/{studyId}/switchEditMode" })
	public String switchEditMode(@ModelAttribute("StudyForm") StudyForm sForm, ModelMap model, RedirectAttributes redirectAttributes,
	    @PathVariable final Optional<Long> pid, @PathVariable final Optional<Long> studyId) {
		log.trace("Entering switchEditMode for Study [id: {}]", () -> studyId);
		UserDTO user = UserUtil.getCurrentUser();
		String ret = projectService.checkUserAccess(pid, studyId, redirectAttributes, true, user);
		if (ret != null)
			log.warn("Unexpected event: User [email: {}] tries to switchEditMode for Study [id: {}] without permissions", () -> user.getEmail(),
			    () -> studyId);
		if ((sForm == null || sForm.getStudy() == null || sForm.getProject() == null || sForm.getProject().getId() == 0) && ret == null) {
			log.warn("Unexpected event: StudyForm is emtpy - Maybe Session Timeout");
			ret = "redirect:/project/" + pid.get() + "/study/" + studyId.get();
		}
		if (ret == null) {
			ret = "study";
			try {
				model.put("disStudyContent", studyService.checkActualLock(pid, studyId, user, (String) model.get("disStudyContent"), model));
				model.put("studySubMenu", true);
				model.put("subnaviActive", PageState.STUDY.name());
			} catch (Exception e) {
				ret = exceptionService.setErrorMessagesAndRedirects(pid, studyId, null, model, redirectAttributes, e, "studyService.checkActualLock");
			}
		}
		log.trace("Method switchEditMode completed with mapping to \"{}\"", ret);
		return ret;
	}

	/**
	 * Appends a ContributorDTO to the list of study contributors items taken from the existing project contributors. After appending the item, it will
	 * be deleted from the project contributor list. If the study list is NULL, a new ArrayList is created before a new item is appended.
	 * 
	 * @param sForm
	 *          StudyForm
	 * @param model
	 *          ModelMap
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
		log.trace("Method addContributor completed with mapping to study.jsp");
		return "study";
	}

	/**
	 * 
	 * Deletes the selected ContributorDTO from the study contributors list. Before deleting, the selected item is appended to the project contributor
	 * list for re-selection. If the study list is NULL, a new ArrayList is created before a new item is appended.
	 * 
	 * @param sForm
	 *          StudyForm
	 * @param model
	 *          ModelMap
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
		log.trace("Method deleteContributor completed with mapping to study.jsp");
		return "study";
	}

	/**
	 * 
	 * Appends a StudyListTypesDTO to the List of Software items. If the List is NULL, a new ArrayList is created before the new item is appended.
	 * 
	 * @param sForm
	 *          StudyForm
	 * @param model
	 *          ModelMap
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
		log.trace("Method addSoftware completed with mapping to study.jsp");
		return "study";
	}

	/**
	 * 
	 * Appends a StudyListTypesDTO to the List of PubOnData items. If the List is NULL, a new ArrayList is created before the new item is appended.
	 * 
	 * @param sForm
	 *          StudyForm
	 * @param model
	 *          ModelMap
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
		log.trace("Method addPubOnData completed with mapping to study.jsp");
		return "study";
	}

	/**
	 * 
	 * Appends a StudyListTypesDTO to the List of ConflInterests items. If the List is NULL, a new ArrayList is created before the new item is appended.
	 * 
	 * @param sForm
	 *          StudyForm
	 * @param model
	 *          ModelMap
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
		log.trace("Method addConflInterests completed with mapping to study.jsp");
		return "study";
	}

	/**
	 * 
	 * Appends a StudyObjectivesDTO to the List of Objectives items. If the List is NULL, a new ArrayList is created before the new item is appended.
	 * 
	 * @param sForm
	 *          StudyForm
	 * @param model
	 *          ModelMap
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
		log.trace("Method addObjectives completed with mapping to study.jsp");
		return "study";
	}

	/**
	 * 
	 * Appends a StudyListTypesDTO to the List of RelTheorys items. If the List is NULL, a new ArrayList is created before the new item is appended.
	 * 
	 * @param sForm
	 *          StudyForm
	 * @param model
	 *          ModelMap
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
		log.trace("Method addRelTheorys completed with mapping to study.jsp");
		return "study";
	}

	/**
	 * 
	 * Appends a StudyListTypesDTO to the List of InterArms items. If the List is NULL, a new ArrayList is created before the new item is appended.
	 * 
	 * @param sForm
	 *          StudyForm
	 * @param model
	 *          ModelMap
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
		log.trace("Method addInterArms completed with mapping to study.jsp");
		return "study";
	}

	/**
	 * 
	 * Appends a StudyListTypesDTO to the List of MeasOccName items. If the List is NULL, a new ArrayList is created before the new item is appended.
	 * 
	 * @param sForm
	 *          StudyForm
	 * @param model
	 *          ModelMap
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
		log.trace("Method addMeasOccName completed with mapping to study.jsp");
		return "study";
	}

	/**
	 * 
	 * Appends a studyInstrumentDAO to the List of Construct items. If the List is NULL, a new ArrayList is created before the new item is appended.
	 * 
	 * @param sForm
	 *          StudyForm
	 * @param model
	 *          ModelMap
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
		log.trace("Method addConstruct completed with mapping to study.jsp");
		return "study";
	}

	/**
	 * 
	 * Appends a StudyInstrumentDTO to the List of Instrument items. If the List is NULL, a new ArrayList is created before the new item is appended.
	 * 
	 * @param sForm
	 *          StudyForm
	 * @param model
	 *          ModelMap
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
		log.trace("Method addInstrument completed with mapping to study.jsp");
		return "study";
	}

	/**
	 * 
	 * Appends a StudyListTypesDTO to the List of Eligibilities items. If the List is NULL, a new ArrayList is created before the new item is appended.
	 * 
	 * @param sForm
	 *          StudyForm
	 * @param model
	 *          ModelMap
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
		log.trace("Method addEligibilities completed with mapping to study.jsp");
		return "study";
	}

	/**
	 * Delete whole study and dependencies (records, material etc.)
	 *
	 * @param pid
	 *          Project Identifier as Optional&lt;Long&gt;
	 * @param studyId
	 *          Study Identifier as Optional&lt;Long&gt;
	 * @param model
	 *          ModelMap
	 * @param redirectAttributes
	 *          RedirectAttributes
	 * @return the string
	 */
	@RequestMapping(value = { "", "/{studyId}/deleteStudy" })
	public String deleteStudy(@PathVariable final Optional<Long> pid, @PathVariable final Optional<Long> studyId, final ModelMap model,
	    final RedirectAttributes redirectAttributes) {
		UserDTO user = UserUtil.getCurrentUser();
		log.trace("Entering deleteStudy for Study [id: {}] and User [email: {}]", () -> studyId.get(), () -> user.getEmail());
		String ret = "redirect:/project/" + pid.get() + "/studies";
		try {
			studyService.deleteStudy(pid, studyId, user, true);
		} catch (DataWizSystemException e) {
			// log messages are thrown in studyService.deleteStudy
			if (e.getErrorCode().equals(DataWizErrorCodes.DATABASE_ERROR)) {
				model.put("errormsg",
				    messageSource.getMessage("dbs.sql.exception",
				        new Object[] { env.getRequiredProperty("organisation.admin.email"), e.getMessage().replaceAll("\n", "").replaceAll("\"", "\'") },
				        LocaleContextHolder.getLocale()));
				ret = "error";
			} else if (e.getErrorCode().equals(DataWizErrorCodes.RECORD_DELETE_ERROR)) {
				model.put("subnaviActive", PageState.RECORDMETA.name());
				model.put("recordSubMenu", true);
				model.put("errorMSG", messageSource.getMessage("study.record.delete.error", new Object[] { e.getMessage(), e.getErrorCode() },
				    LocaleContextHolder.getLocale()));
				ret = "study";
			} else {
				model.put("subnaviActive", PageState.RECORDMETA.name());
				model.put("recordSubMenu", true);
				model.put("errorMSG",
				    messageSource.getMessage("study.not.deleted.error", new Object[] { e.getMessage(), e.getErrorCode() }, LocaleContextHolder.getLocale()));
				ret = "study";
			}
		}
		log.trace("Method deleteStudy completed with mapping to \"{}\"", ret);
		return ret;
	}
}
