package de.zpid.datawiz.controller;

import de.zpid.datawiz.dto.*;
import de.zpid.datawiz.enumeration.DataWizErrorCodes;
import de.zpid.datawiz.enumeration.PageState;
import de.zpid.datawiz.enumeration.Roles;
import de.zpid.datawiz.exceptions.DWDownloadException;
import de.zpid.datawiz.exceptions.DataWizSystemException;
import de.zpid.datawiz.form.StudyForm;
import de.zpid.datawiz.service.ExceptionService;
import de.zpid.datawiz.service.ProjectService;
import de.zpid.datawiz.service.RecordService;
import de.zpid.datawiz.service.StudyService;
import de.zpid.datawiz.util.BreadCrumbUtil;
import de.zpid.datawiz.util.UserUtil;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * This controller handles all calls to /project/{pid}/study/*
 * <p>
 * This file is part of the DataWiz distribution (https://github.com/ZPID/DataWiz).
 * Copyright (c) 2018 <a href="https://leibniz-psychology.org/">Leibniz Institute for Psychology Information (ZPID)</a>.
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3.
 * <p>
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <a href="http://www.gnu.org/licenses/">http://www.gnu.org/licenses/</a>.
 *
 * @author Ronny Boelter
 * @version 1.0
 * FIXME: StudyForm is stored in Session. This should be revised, because this causes errors on using multiple browser tabs!!!
 **/
@Controller
@RequestMapping(value = {"/study", "/project/{pid}/study"})
@SessionAttributes({"StudyForm", "subnaviActive", "breadcrumbList", "disStudyContent", "ProjectList"})
public class StudyController {

    private final MessageSource messageSource;
    private final StudyService studyService;
    private final ExceptionService exceptionService;
    private final ClassPathXmlApplicationContext applicationContext;
    private final RecordService recordService;
    private final ProjectService projectService;
    private final Environment env;
    private static final Logger log = LogManager.getLogger(StudyController.class);

    @Autowired
    public StudyController(MessageSource messageSource, StudyService studyService, ExceptionService exceptionService,
                           ClassPathXmlApplicationContext applicationContext, RecordService recordService, ProjectService projectService, Environment env) {
        super();
        log.info("Loading StudyController for mapping /study");
        this.messageSource = messageSource;
        this.studyService = studyService;
        this.exceptionService = exceptionService;
        this.applicationContext = applicationContext;
        this.recordService = recordService;
        this.projectService = projectService;
        this.env = env;
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
     * This function load the study data and passes it to study.jsp. It distinguishes between two cases: First: study identifier is present - edit study data;
     * second study identifier is not present - create new study.
     *
     * @param sForm              {@link StudyForm}
     * @param pid                Project Identifier as long
     * @param studyId            Study Identifier as {@link Optional}&lt;{@link Long}&gt;
     * @param model              {@link ModelMap}
     * @param redirectAttributes {@link RedirectAttributes}
     * @return @return Mapping to study.jsp on success, otherwise exception handling and mapping via exceptionService.setErrorMessagesAndRedirects
     */
    @RequestMapping(value = {"", "/{studyId}"}, method = RequestMethod.GET)
    public String showStudyPage(@ModelAttribute("StudyForm") StudyForm sForm, @PathVariable final long pid, @PathVariable final Optional<Long> studyId,
                                final ModelMap model, final RedirectAttributes redirectAttributes) {
        String ret;
        final UserDTO user = UserUtil.getCurrentUser();
        String jQueryMap = null;
        if (studyId.isPresent()) {
            log.trace("Entering showStudyPage(edit) for study [pid: {}; sid: {}]", studyId::get);
            ret = projectService.checkUserAccess(pid, studyId.orElse(0L), redirectAttributes, false, user);
            if (sForm != null && sForm.getjQueryMap() != null && !sForm.getjQueryMap().isEmpty()) {
                jQueryMap = sForm.getjQueryMap();
            }
        } else {
            log.trace("Entering showStudyPage(create) study");
            ret = projectService.checkUserAccess(pid, studyId.orElse(0L), redirectAttributes, true, user);
        }
        sForm = createStudyForm();
        sForm.setjQueryMap(jQueryMap);
        if (ret == null) {
            try {
                String accessState = studyService.setStudyForm(pid, studyId.orElse(0L), user, sForm);
                studyService.createStudyBreadCrump(sForm.getProject().getTitle(), sForm.getStudy() != null ? sForm.getStudy().getTitle() : null, pid, model);
                model.put("disStudyContent", accessState);
                model.put("StudyForm", sForm);
                model.put("studySubMenu", true);
                model.put("subnaviActive", PageState.STUDY.name());
                model.put("ProjectList", projectService.getAdminProjectList(user));
                ret = "study";
            } catch (Exception e) {
                ret = exceptionService.setErrorMessagesAndRedirects(pid, studyId.orElse(0L), 0, model, redirectAttributes, e, "studyService.setStudyForm");
            }
        } else {
            log.warn("Unexpected event: User [email: {}] tries to showStudyPage for Study [id: {}] without permissions", user::getEmail, () -> studyId);
        }
        log.trace("Method showStudyPage completed with mapping to \"{}\"", ret);
        return ret;
    }

    /**
     * This function loads all records which belongs to the passed study
     *
     * @param pid                Project Identifier as long
     * @param studyId            Study Identifier as long
     * @param model              {@link ModelMap}
     * @param redirectAttributes {@link RedirectAttributes}
     * @return Mapping to record.jsp on success, otherwise exception handling and mapping via exceptionService.setErrorMessagesAndRedirects
     */
    @RequestMapping(value = {"/{studyId}/records"}, method = RequestMethod.GET)
    public String showRecordOverview(@PathVariable final long pid, @PathVariable final long studyId, final ModelMap model,
                                     final RedirectAttributes redirectAttributes) {
        final UserDTO user = UserUtil.getCurrentUser();
        log.trace("Entering showRecordOverview for study [id: {}] and user [email: {}]", () -> studyId, user::getEmail);
        String ret = projectService.checkUserAccess(pid, studyId, redirectAttributes, false, user);
        if (ret == null) {
            try {
                StudyForm sForm = createStudyForm();
                studyService.setRecordList(pid, studyId, sForm);
                model.put("breadcrumbList", BreadCrumbUtil.generateBC(PageState.STUDY, new String[]{sForm.getProject().getTitle(), sForm.getStudy().getTitle()},
                        new long[]{pid}, messageSource));
                model.put("studySubMenu", true);
                model.put("subnaviActive", PageState.RECORDS.name());
                model.put("StudyForm", sForm);
                ret = "records";
            } catch (Exception e) {
                ret = exceptionService.setErrorMessagesAndRedirects(pid, studyId, 0, model, redirectAttributes, e, "studyService.setRecordList");
            }
        } else {
            log.warn("Unexpected event: User [email: {}] tries to showRecordOverview for Study [id: {}] without permissions", user::getEmail, () -> studyId);
        }
        log.trace("Method showRecordOverview completed with mapping to \"{}\"", ret);
        return ret;
    }

    /**
     * This function validates and saves the Study Data and returns errors to the view if the validation is not successful.
     *
     * @param sForm              StudyForm
     * @param model              ModelMap
     * @param redirectAttributes RedirectAttributes
     * @param bRes               BindingResult
     * @param studyId            Study Identifier as Optional&lt;Long&gt;
     * @param pid                Project Identifier as long
     * @return Mapping to recirect:study on success, otherwise to study.jsp with error messages
     */
    @RequestMapping(value = {"", "/{studyId}"}, method = RequestMethod.POST)
    public String saveStudy(@ModelAttribute("StudyForm") final StudyForm sForm, final ModelMap model, final RedirectAttributes redirectAttributes, final BindingResult bRes,
                            @PathVariable final Optional<Long> studyId, @PathVariable final long pid) {
        log.trace("Entering saveStudy for Study [id: {}]", () -> studyId);
        String ret;
        final UserDTO user = UserUtil.getCurrentUser();
        if (studyId.isPresent()) {
            log.trace("Entering saveStudy(edit) for study [id: {}]", studyId::get);
            ret = projectService.checkUserAccess(pid, studyId.orElse(1L), redirectAttributes, false, user);
        } else {
            log.trace("Entering saveStudy(create)");
            ret = projectService.checkUserAccess(pid, studyId.orElse(1L), redirectAttributes, true, user);
        }
        if (ret == null) {
            Set<String> validateErrors = new HashSet<>();
            boolean error = studyService.validateStudyForm(sForm, bRes, StudyDTO.StGeneralVal.class, PageState.STUDYGENERAL, validateErrors);
            error = studyService.validateStudyForm(sForm, bRes, StudyDTO.StDesignVal.class, PageState.STUDYDESIGN, validateErrors) || error;
            error = studyService.validateStudyForm(sForm, bRes, StudyDTO.StEthicalVal.class, PageState.STUDYETHICAL, validateErrors) || error;
            error = studyService.validateStudyForm(sForm, bRes, StudyDTO.StSampleVal.class, PageState.STUDYSAMPLE, validateErrors) || error;
            error = studyService.validateStudyForm(sForm, bRes, StudyDTO.StSurveyVal.class, PageState.STUDYSURVEY, validateErrors) || error;
            if (error) {
                model.put("studySubMenu", true);
                model.put("subnaviActive", PageState.STUDY.name());
                model.put("errorMSG", recordService.setMessageString(validateErrors));
                ret = "study";
            } else {
                StudyDTO study;
                try {
                    study = studyService.saveStudyForm(sForm, studyId.orElse(0L), pid, user, false);
                    ret = "redirect:/project/" + pid + "/study/" + study.getId();
                } catch (DataWizSystemException e) {
                    if (e.getErrorCode().equals(DataWizErrorCodes.STUDY_NOT_AVAILABLE)) {
                        log.error("Unexpected event: Internal Error saveStudyForm - Transaction was rolled back [Message: {}; Code: {}]", e::getMessage,
                                e::getErrorCode, () -> e);
                        model.put("errorMSG", messageSource.getMessage("error.study.save.unavailable", null, LocaleContextHolder.getLocale()));
                    } else if (e.getErrorCode().equals(DataWizErrorCodes.SESSION_ERROR)) {
                        log.warn("Unexpected event: Internal Error saveStudyForm - Transaction was rolled back [Message: {}; Code: {}]", e::getMessage,
                                e::getErrorCode, () -> e);
                        model.put("errorMSG", messageSource.getMessage("error.study.save.session", null, LocaleContextHolder.getLocale()));
                    } else {
                        log.fatal("Unexpected event: Database Error During saveStudyForm - Transaction was rolled back cause: {}", e::getCause, () -> e);
                        model.put("errorMSG", messageSource.getMessage("error.study.duplicate.dbs", null, LocaleContextHolder.getLocale()));
                    }
                    model.put("studySubMenu", true);
                    model.put("subnaviActive", PageState.STUDY.name());
                    ret = "study";
                }
            }
        } else {
            log.warn("Unexpected event: User [email: {}] tries to saveStudy for Study [id: {}] without permissions", user::getEmail, () -> studyId);
        }
        log.trace("Method saveStudy completed with mapping to \"{}\"", ret);
        return ret;
    }

    /**
     * This function switches the edit mode. The study has a lock, so that only one user can edit the study data at the same time. If the study is locked by a
     * user, other user have to wait until the user unlock the study, or the session time is over and the study automatically unlocks.
     *
     * @param model              {@link ModelMap}
     * @param redirectAttributes {@link RedirectAttributes}
     * @param pid                Project identifier as long
     * @param studyId            Study identifier as long
     * @return Redirect to study on success
     */
    @RequestMapping(value = {"/{studyId}/switchEditMode"})
    public String switchEditMode(final ModelMap model, final RedirectAttributes redirectAttributes, @PathVariable final long pid,
                                 @PathVariable final long studyId) {
        log.trace("Entering switchEditMode for Study [id: {}]", () -> studyId);
        UserDTO user = UserUtil.getCurrentUser();
        String ret = projectService.checkUserAccess(pid, studyId, redirectAttributes, true, user);
        if (ret != null)
            log.warn("Unexpected event: User [email: {}] tries to switchEditMode for Study [id: {}] without permissions", user::getEmail, () -> studyId);
        if (ret == null) {
            ret = "redirect:/project/" + pid + "/study/" + studyId;
            try {
                studyService.checkActualLock(pid, studyId, user, (String) model.get("disStudyContent"), redirectAttributes);
            } catch (Exception e) {
                ret = exceptionService.setErrorMessagesAndRedirects(pid, studyId, 0, model, redirectAttributes, e, "studyService.checkActualLock");
            }
        }
        log.trace("Method switchEditMode completed with mapping to \"{}\"", ret);
        return ret;
    }

    /**
     * Appends a ContributorDTO to the list of study contributors items taken from the existing project contributors. After appending the item, it will be deleted
     * from the project contributor list. If the study list is NULL, a new ArrayList is created before a new item is appended.
     *
     * @param sForm {@link StudyForm}
     * @param model {@link ModelMap}
     * @return mapping to "study.jsp"
     */
    @RequestMapping(value = {"", "/{studyId}"}, method = RequestMethod.POST, params = "addContri")
    public String addContributor(@ModelAttribute("StudyForm") StudyForm sForm, ModelMap model) {
        log.trace("Entering addContributor");
        if (sForm.getStudy().getContributors() == null)
            sForm.getStudy().setContributors(new ArrayList<>());
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
     * Deletes the selected ContributorDTO from the study contributors list. Before deleting, the selected item is appended to the project contributor list for
     * re-selection. If the study list is NULL, a new ArrayList is created before a new item is appended.
     *
     * @param sForm {@link StudyForm}
     * @param model {@link ModelMap}
     * @return mapping to "study.jsp"
     */
    @RequestMapping(value = {"", "/{studyId}"}, method = RequestMethod.POST, params = "deleteContri")
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
     * Appends a StudyListTypesDTO to the List of Software items. If the List is NULL, a new ArrayList is created before the new item is appended.
     *
     * @param sForm {@link StudyForm}
     * @param model {@link ModelMap}
     * @return mapping to "study.jsp"
     */
    @RequestMapping(value = {"", "/{studyId}"}, method = RequestMethod.POST, params = "addSoftware")
    public String addSoftware(@ModelAttribute("StudyForm") StudyForm sForm, ModelMap model) {
        log.trace("Entering addSoftware");
        if (sForm.getStudy().getSoftware() == null)
            sForm.getStudy().setSoftware(new ArrayList<>());
        sForm.getStudy().getSoftware().add((StudyListTypesDTO) applicationContext.getBean("StudyListTypesDTO"));
        model.put("studySubMenu", true);
        model.put("subnaviActive", PageState.STUDY.name());
        log.trace("Method addSoftware completed with mapping to study.jsp");
        return "study";
    }

    /**
     * Appends a StudyListTypesDTO to the List of PubOnData items. If the List is NULL, a new ArrayList is created before the new item is appended.
     *
     * @param sForm {@link StudyForm}
     * @param model {@link ModelMap}
     * @return mapping to "study.jsp"
     */
    @RequestMapping(value = {"", "/{studyId}"}, method = RequestMethod.POST, params = "addPubOnData")
    public String addPubOnData(@ModelAttribute("StudyForm") StudyForm sForm, ModelMap model) {
        log.trace("Entering addPubOnData");
        if (sForm.getStudy().getPubOnData() == null)
            sForm.getStudy().setPubOnData(new ArrayList<>());
        sForm.getStudy().getPubOnData().add((StudyListTypesDTO) applicationContext.getBean("StudyListTypesDTO"));
        model.put("studySubMenu", true);
        model.put("subnaviActive", PageState.STUDY.name());
        log.trace("Method addPubOnData completed with mapping to study.jsp");
        return "study";
    }

    /**
     * Appends a StudyListTypesDTO to the List of ConflInterests items. If the List is NULL, a new ArrayList is created before the new item is appended.
     *
     * @param sForm {@link StudyForm}
     * @param model {@link ModelMap}
     * @return mapping to "study.jsp"
     */
    @RequestMapping(value = {"", "/{studyId}"}, method = RequestMethod.POST, params = "addConflInterests")
    public String addConflInterests(@ModelAttribute("StudyForm") StudyForm sForm, ModelMap model) {
        log.trace("Entering addConflInterests");
        if (sForm.getStudy().getConflInterests() == null)
            sForm.getStudy().setConflInterests(new ArrayList<>());
        sForm.getStudy().getConflInterests().add((StudyListTypesDTO) applicationContext.getBean("StudyListTypesDTO"));
        model.put("studySubMenu", true);
        model.put("subnaviActive", PageState.STUDY.name());
        log.trace("Method addConflInterests completed with mapping to study.jsp");
        return "study";
    }

    /**
     * Appends a StudyObjectivesDTO to the List of Objectives items. If the List is NULL, a new ArrayList is created before the new item is appended.
     *
     * @param sForm {@link StudyForm}
     * @param model {@link ModelMap}
     * @return mapping to "study.jsp"
     */
    @RequestMapping(value = {"", "/{studyId}"}, method = RequestMethod.POST, params = "addObjectives")
    public String addObjectives(@ModelAttribute("StudyForm") StudyForm sForm, ModelMap model) {
        log.trace("Entering addObjectives");
        if (sForm.getStudy().getObjectives() == null)
            sForm.getStudy().setObjectives(new ArrayList<>());
        sForm.getStudy().getObjectives().add((StudyListTypesDTO) applicationContext.getBean("StudyListTypesDTO"));
        model.put("studySubMenu", true);
        model.put("subnaviActive", PageState.STUDY.name());
        log.trace("Method addObjectives completed with mapping to study.jsp");
        return "study";
    }

    /**
     * Appends a StudyListTypesDTO to the List of RelTheorys items. If the List is NULL, a new ArrayList is created before the new item is appended.
     *
     * @param sForm {@link StudyForm}
     * @param model {@link ModelMap}
     * @return mapping to "study.jsp"
     */
    @RequestMapping(value = {"", "/{studyId}"}, method = RequestMethod.POST, params = "addRelTheorys")
    public String addRelTheorys(@ModelAttribute("StudyForm") StudyForm sForm, ModelMap model) {
        log.trace("Entering addRelTheorys");
        if (sForm.getStudy().getRelTheorys() == null)
            sForm.getStudy().setRelTheorys(new ArrayList<>());
        sForm.getStudy().getRelTheorys().add((StudyListTypesDTO) applicationContext.getBean("StudyListTypesDTO"));
        model.put("studySubMenu", true);
        model.put("subnaviActive", PageState.STUDY.name());
        log.trace("Method addRelTheorys completed with mapping to study.jsp");
        return "study";
    }

    /**
     * Appends a StudyListTypesDTO to the List of InterArms items. If the List is NULL, a new ArrayList is created before the new item is appended.
     *
     * @param sForm {@link StudyForm}
     * @param model {@link ModelMap}
     * @return mapping to "study.jsp"
     */
    @RequestMapping(value = {"", "/{studyId}"}, method = RequestMethod.POST, params = "addInterArms")
    public String addInterArms(@ModelAttribute("StudyForm") StudyForm sForm, ModelMap model) {
        log.trace("Entering addInterArms");
        if (sForm.getStudy().getInterArms() == null)
            sForm.getStudy().setInterArms(new ArrayList<>());
        sForm.getStudy().getInterArms().add((StudyListTypesDTO) applicationContext.getBean("StudyListTypesDTO"));
        model.put("studySubMenu", true);
        model.put("subnaviActive", PageState.STUDY.name());
        log.trace("Method addInterArms completed with mapping to study.jsp");
        return "study";
    }

    /**
     * Appends a StudyListTypesDTO to the List of MeasOccName items. If the List is NULL, a new ArrayList is created before the new item is appended.
     *
     * @param sForm {@link StudyForm}
     * @param model {@link ModelMap}
     * @return mapping to "study.jsp"
     */
    @RequestMapping(value = {"", "/{studyId}"}, method = RequestMethod.POST, params = "addMeasOccName")
    public String addMeasOccName(@ModelAttribute("StudyForm") StudyForm sForm, ModelMap model) {
        log.trace("Entering addMeasOccName");
        if (sForm.getStudy().getMeasOcc() == null)
            sForm.getStudy().setMeasOcc(new ArrayList<>());
        sForm.getStudy().getMeasOcc().add((StudyListTypesDTO) applicationContext.getBean("StudyListTypesDTO"));
        model.put("studySubMenu", true);
        model.put("subnaviActive", PageState.STUDY.name());
        log.trace("Method addMeasOccName completed with mapping to study.jsp");
        return "study";
    }

    /**
     * Appends a studyInstrumentDAO to the List of Construct items. If the List is NULL, a new ArrayList is created before the new item is appended.
     *
     * @param sForm {@link StudyForm}
     * @param model {@link ModelMap}
     * @return mapping to "study.jsp"
     */
    @RequestMapping(value = {"", "/{studyId}"}, method = RequestMethod.POST, params = "addConstruct")
    public String addConstruct(@ModelAttribute("StudyForm") StudyForm sForm, ModelMap model) {
        log.trace("Entering addConstruct");
        if (sForm.getStudy().getConstructs() == null)
            sForm.getStudy().setConstructs(new ArrayList<>());
        sForm.getStudy().getConstructs().add((StudyConstructDTO) applicationContext.getBean("StudyConstructDTO"));
        model.put("studySubMenu", true);
        model.put("subnaviActive", PageState.STUDY.name());
        log.trace("Method addConstruct completed with mapping to study.jsp");
        return "study";
    }

    /**
     * Appends a StudyInstrumentDTO to the List of Instrument items. If the List is NULL, a new ArrayList is created before the new item is appended.
     *
     * @param sForm {@link StudyForm}
     * @param model {@link ModelMap}
     * @return mapping to "study.jsp"
     */
    @RequestMapping(value = {"", "/{studyId}"}, method = RequestMethod.POST, params = "addInstrument")
    public String addInstrument(@ModelAttribute("StudyForm") StudyForm sForm, ModelMap model) {
        log.trace("Entering addInstrument");
        if (sForm.getStudy().getInstruments() == null)
            sForm.getStudy().setInstruments(new ArrayList<>());
        sForm.getStudy().getInstruments().add((StudyInstrumentDTO) applicationContext.getBean("StudyInstrumentDTO"));
        model.put("studySubMenu", true);
        model.put("subnaviActive", PageState.STUDY.name());
        log.trace("Method addInstrument completed with mapping to study.jsp");
        return "study";
    }

    /**
     * Appends a StudyListTypesDTO to the List of Eligibilities items. If the List is NULL, a new ArrayList is created before the new item is appended.
     *
     * @param sForm {@link StudyForm}
     * @param model {@link ModelMap}
     * @return mapping to "study.jsp"
     */
    @RequestMapping(value = {"", "/{studyId}"}, method = RequestMethod.POST, params = "addEligibilities")
    public String addEligibilities(@ModelAttribute("StudyForm") StudyForm sForm, ModelMap model) {
        log.trace("Entering addEligibilities");
        if (sForm.getStudy().getEligibilities() == null)
            sForm.getStudy().setEligibilities(new ArrayList<>());
        sForm.getStudy().getEligibilities().add((StudyListTypesDTO) applicationContext.getBean("StudyListTypesDTO"));
        model.put("studySubMenu", true);
        model.put("subnaviActive", PageState.STUDY.name());
        log.trace("Method addEligibilities completed with mapping to study.jsp");
        return "study";
    }

    /**
     * Delete whole study and dependencies (records, material etc.)
     *
     * @param pid     Project Identifier as long
     * @param studyId Study Identifier as long
     * @param model   {@link ModelMap}
     * @return Mapping to study list on success
     */
    @RequestMapping(value = {"", "/{studyId}/deleteStudy"})
    public String deleteStudy(@PathVariable final long pid, @PathVariable final long studyId, final ModelMap model) {
        UserDTO user = UserUtil.getCurrentUser();
        log.trace("Entering deleteStudy for Study [id: {}] and User [email: {}]", () -> studyId, user::getEmail);
        String ret = "redirect:/project/" + pid + "/studies";
        try {
            studyService.deleteStudy(pid, studyId, user, true);
        } catch (DataWizSystemException e) {
            // log messages are thrown in studyService.deleteStudy
            if (e.getErrorCode().equals(DataWizErrorCodes.DATABASE_ERROR)) {
                model.put("errormsg",
                        messageSource.getMessage("dbs.sql.exception",
                                new Object[]{env.getRequiredProperty("organisation.admin.email"), e.getMessage().replaceAll("\n", "").replaceAll("\"", "\'")},
                                LocaleContextHolder.getLocale()));
                ret = "error";
            } else if (e.getErrorCode().equals(DataWizErrorCodes.RECORD_DELETE_ERROR)) {
                model.put("studySubMenu", true);
                model.put("subnaviActive", PageState.STUDY.name());
                model.put("errorMSG",
                        messageSource.getMessage("study.record.delete.error", new Object[]{e.getMessage(), e.getErrorCode()}, LocaleContextHolder.getLocale()));
                ret = "study";
            } else {
                model.put("studySubMenu", true);
                model.put("subnaviActive", PageState.STUDY.name());
                model.put("errorMSG",
                        messageSource.getMessage("study.not.deleted.error", new Object[]{e.getMessage(), e.getErrorCode()}, LocaleContextHolder.getLocale()));
                ret = "study";
            }
        }
        log.trace("Method deleteStudy completed with mapping to \"{}\"", ret);
        return ret;
    }

    /**
     * Duplicates the passed study and all of its meta data into the same or another selected project
     *
     * @param pid      Project identifier of the current project as long
     * @param studyId  Study identifier as long
     * @param model    {@link ModelMap}
     * @param selected Project identifier of project where the study has to be duplicated
     * @return Mapping to new study on success
     */
    @RequestMapping(value = {"/{studyId}/duplicate"}, method = RequestMethod.GET)
    public String duplicateStudy(@PathVariable long pid, @PathVariable long studyId, ModelMap model, @RequestParam("selected") long selected) {
        log.trace("Entering duplicateStudy for study [id:{}] to project [id: {}]", () -> studyId, () -> selected);
        final UserDTO user = UserUtil.getCurrentUser();
        String ret;
        try {
            StudyDTO study = studyService.copyStudy(pid, studyId, selected, user);
            ret = "redirect:/project/" + study.getProjectId() + "/study/" + study.getId();
        } catch (Exception e) {
            model.put("studySubMenu", true);
            model.put("subnaviActive", PageState.STUDY.name());
            ret = "study";
            if (e instanceof DataWizSystemException) {
                log.warn("DataWizSystemException thrown by function studyService.copyStudy Code: {}", ((DataWizSystemException) e)::getErrorCode, () -> e);
                switch (((DataWizSystemException) e).getErrorCode()) {
                    case MISSING_PID_ERROR:
                        model.put("errorMSG", messageSource.getMessage("error.study.duplicate.pid", null, LocaleContextHolder.getLocale()));
                        break;
                    case MISSING_STUDYID_ERROR:
                        model.put("errorMSG", messageSource.getMessage("error.study.duplicate.studyid", null, LocaleContextHolder.getLocale()));
                        break;
                    case NO_DATA_ERROR:
                        model.put("errorMSG", messageSource.getMessage("error.study.duplicate.selected", null, LocaleContextHolder.getLocale()));
                        break;
                    case USER_ACCESS_STUDY_PERMITTED:
                        model.put("errorMSG", messageSource.getMessage("error.study.duplicate.access", null, LocaleContextHolder.getLocale()));
                        break;
                    case DATABASE_ERROR:
                        model.put("errorMSG", messageSource.getMessage("error.study.duplicate.dbs", null, LocaleContextHolder.getLocale()));
                        ret = "error";
                        break;
                    case STUDY_NOT_AVAILABLE:
                        model.put("errorMSG", messageSource.getMessage("error.study.duplicate.studyid", null, LocaleContextHolder.getLocale()));
                        break;
                    default:
                        model.put("errorMSG", messageSource.getMessage("error.study.duplicate.unknown", null, LocaleContextHolder.getLocale()));
                        break;
                }
            } else {
                log.warn("Exception thrown by function studyService.copyStudy: ", () -> e);
                model.put("errorMSG", messageSource.getMessage("error.study.duplicate.dbs", null, LocaleContextHolder.getLocale()));
                ret = "error";
            }
        }
        log.trace("Leaving duplicateStudy for study [id:{}] to project [id: {}]", () -> studyId, () -> selected);
        return ret;
    }

    /**
     * Exports a study in ODT-Format.
     *
     * @param pid      Project identifier as long
     * @param studyId  Study identifier as long
     * @param type     Export type identifier as {@link String}
     * @param response {@link HttpServletResponse}
     * @throws DWDownloadException DBS and IO Exceptions
     */
    @RequestMapping(value = {"/{studyId}/exportStudy/{type}"}, method = RequestMethod.GET)
    public void exportStudyODF(@PathVariable final long pid, @PathVariable final long studyId, @PathVariable final String type,
                               final HttpServletResponse response) throws DWDownloadException {
        log.trace("Entering exportStudyODF for Study [studyID: {}] and exportType [{}]", () -> studyId, () -> type);
        UserDTO user = UserUtil.getCurrentUser();
        if (user == null
                || (!user.hasRole(Roles.PROJECT_READER, pid, false) && !user.hasRole(Roles.PROJECT_ADMIN, pid, false) && !user.hasRole(Roles.DS_READER, studyId, true)
                && !user.hasRole(Roles.DS_WRITER, studyId, true) && !user.hasRole(Roles.PROJECT_WRITER, pid, false) && !user.hasRole(Roles.ADMIN))) {
            log.warn("Auth User Object empty or User is permitted to download this file");
            throw new DWDownloadException("export.access.denied");
        }
        byte[] content;
        try {
            // TODO? locale is set to German - should be changed if the form inputs are available in English
            content = studyService.createStudyExport(pid, studyId, type, Locale.GERMAN);
        } catch (Exception e) {
            log.warn("Exception during dmpService.createDMPExport Message: ", () -> e);
            if (e instanceof DataWizSystemException) {
                if (((DataWizSystemException) e).getErrorCode().equals(DataWizErrorCodes.MISSING_PID_ERROR))
                    throw new DWDownloadException("export.odt.error.project");
                else
                    throw new DWDownloadException("export.odt.error.study");
            } else {
                throw new DWDownloadException("dbs.sql.exception");
            }
        }
        try {
            response.setContentType("application/vnd.oasis.opendocument.text");
            response.setHeader("Content-Disposition", "attachment; filename=\"datawiz_export_" + type + ".odt\"");
            response.setContentLength(content.length);
            response.getOutputStream().write(content);
            response.flushBuffer();
            log.trace("Leaving exportStudyODF for Study [studyID: {}] and exportType [{}]", () -> studyId, () -> type);
        } catch (Exception e) {
            log.warn("Exception during creating response ", () -> e);
            throw new DWDownloadException("export.error.response");
        }

    }

}
