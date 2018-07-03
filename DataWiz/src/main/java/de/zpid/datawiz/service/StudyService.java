package de.zpid.datawiz.service;

import de.zpid.datawiz.dao.*;
import de.zpid.datawiz.dto.*;
import de.zpid.datawiz.enumeration.DWFieldTypes;
import de.zpid.datawiz.enumeration.DataWizErrorCodes;
import de.zpid.datawiz.enumeration.PageState;
import de.zpid.datawiz.enumeration.Roles;
import de.zpid.datawiz.exceptions.DataWizSystemException;
import de.zpid.datawiz.form.StudyForm;
import de.zpid.datawiz.util.BreadCrumbUtil;
import de.zpid.datawiz.util.ListUtil;
import de.zpid.datawiz.util.ODFUtil;
import de.zpid.datawiz.util.StringUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.SmartValidator;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;


/**
 * Service Class for StudyController
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
 */
@Service
public class StudyService {

    private static Logger log = LogManager.getLogger(StudyService.class);

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
    private ProjectDAO projectDAO;
    @Autowired
    private StudyDAO studyDAO;
    @Autowired
    private RecordDAO recordDAO;
    @Autowired
    private int sessionTimeout;
    @Autowired
    private PlatformTransactionManager txManager;
    @Autowired
    RecordService recordService;
    @Autowired
    private SmartValidator validator;
    @Autowired
    private ODFUtil odfUtil;
    @Autowired
    private ClassPathXmlApplicationContext applicationContext;
    @Autowired
    private UserDAO userDAO;
    @Autowired
    private StringUtil stringUtil;

    /**
     * @param pid
     * @param studyId
     * @param redirectAttributes
     * @param user
     * @param sForm
     * @return
     * @throws Exception
     */
    public String setStudyForm(final long pid, final long studyId, final RedirectAttributes redirectAttributes, final UserDTO user,
                               final StudyForm sForm) throws DataWizSystemException {
        if (pid <= 0)
            throw new DataWizSystemException(messageSource.getMessage("logging.pid.not.present", null, Locale.ENGLISH), DataWizErrorCodes.MISSING_PID_ERROR);
        String accessState = "disabled";
        ProjectDTO project;
        StudyDTO study = null;
        List<ContributorDTO> pContri = null;
        try {
            project = projectDAO.findById(pid);
            if (project != null)
                pContri = contributorDAO.findByProject(project, false, true);
            sForm.setCollectionModes(formTypeDAO.findAllByType(true, DWFieldTypes.COLLECTIONMODE));
            sForm.setSourFormat(formTypeDAO.findAllByType(true, DWFieldTypes.DATAFORMAT));
            if (studyId > 0) {
                study = studyDAO.findById(studyId, pid, false, false);
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
            throw new DataWizSystemException(messageSource.getMessage("logging.database.error", new Object[]{e.getMessage()}, Locale.ENGLISH),
                    DataWizErrorCodes.DATABASE_ERROR);
        }
        if (project == null) {
            throw new DataWizSystemException(messageSource.getMessage("logging.project.not.found", new Object[]{pid}, Locale.ENGLISH),
                    DataWizErrorCodes.PROJECT_NOT_AVAILABLE);
        }
        if (studyId > 0) {
            if (study != null && study.getId() > 0) {
                cleanContributorList(pContri, study.getContributors());
                sForm.setStudy(study);
            } else {
                throw new DataWizSystemException(messageSource.getMessage("logging.study.not.found", new Object[]{studyId}, Locale.ENGLISH),
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
     * @param pid
     * @param studyId
     * @param redirectAttributes
     * @param sForm
     * @throws DataWizSystemException
     */
    public void setRecordList(final long pid, final long studyId, final RedirectAttributes redirectAttributes, final StudyForm sForm)
            throws DataWizSystemException {
        ProjectDTO project;
        StudyDTO study;
        try {
            project = projectDAO.findById(pid);
            study = studyDAO.findById(studyId, pid, true, false);
            sForm.setRecords(recordDAO.findRecordsWithStudyID(studyId));
        } catch (Exception e) {
            throw new DataWizSystemException(messageSource.getMessage("logging.database.error", new Object[]{e.getMessage()}, Locale.ENGLISH),
                    DataWizErrorCodes.DATABASE_ERROR);
        }
        if (project == null) {
            throw new DataWizSystemException(messageSource.getMessage("logging.project.not.found", new Object[]{pid}, Locale.ENGLISH),
                    DataWizErrorCodes.PROJECT_NOT_AVAILABLE);
        }
        if (study == null || study.getId() <= 0) {
            throw new DataWizSystemException(messageSource.getMessage("logging.study.not.found", new Object[]{studyId}, Locale.ENGLISH),
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
    public String checkActualLock(final long pid, final long studyId, UserDTO user, String actLock,
                                  final RedirectAttributes redirectAttributes) throws DataWizSystemException {
        if (pid <= 0)
            throw new DataWizSystemException(messageSource.getMessage("logging.pid.not.present", null, Locale.ENGLISH), DataWizErrorCodes.MISSING_PID_ERROR);
        if (studyId <= 0)
            throw new DataWizSystemException(messageSource.getMessage("logging.studyid.not.present", null, Locale.ENGLISH), DataWizErrorCodes.MISSING_STUDYID_ERROR);
        StudyDTO study = null;
        try {
            study = studyDAO.findById(studyId, pid, true, true);
        } catch (Exception e) {
            throw new DataWizSystemException(messageSource.getMessage("logging.database.error", new Object[]{e.getMessage()}, Locale.ENGLISH),
                    DataWizErrorCodes.DATABASE_ERROR);
        }
        if (study == null || study.getId() <= 0) {
            throw new DataWizSystemException(messageSource.getMessage("logging.study.not.found", new Object[]{studyId}, Locale.ENGLISH),
                    DataWizErrorCodes.STUDY_NOT_AVAILABLE);
        }
        if ((!study.isCurrentlyEdit() || (study.getEditSince().plusSeconds(sessionTimeout).compareTo(LocalDateTime.now()) < 0)
                || (study.getEditSince().plusSeconds(sessionTimeout).compareTo(LocalDateTime.now()) >= 0 && user.getId() == study.getEditUserId()))) {
            try {
                if (actLock == null || actLock.isEmpty() || actLock.equals("enabled")) {
                    if (studyDAO.switchStudyLock(studyId, user.getId(), true) > 0)
                        actLock = "disabled";
                } else {
                    if (studyDAO.switchStudyLock(studyId, user.getId(), false) > 0)
                        actLock = "enabled";
                }
            } catch (Exception e) {
                throw new DataWizSystemException(messageSource.getMessage("logging.database.error", new Object[]{e.getMessage()}, Locale.ENGLISH),
                        DataWizErrorCodes.DATABASE_ERROR);
            }
        } else {
            if (study.getEditUserId() > 0) {
                UserDTO currUser;
                try {
                    currUser = userDAO.findById(study.getEditUserId());

                    redirectAttributes.addFlashAttribute("errorMSG",
                            messageSource.getMessage(
                                    "study.access.lock.msg", new Object[]{study.getEditSince().format(DateTimeFormatter.ofPattern("HH:mm")),
                                            stringUtil.createUserNameString(currUser), study.getEditSince().format(DateTimeFormatter.ofPattern("a"))},
                                    LocaleContextHolder.getLocale()));
                } catch (Exception e) {
                    throw new DataWizSystemException(messageSource.getMessage("logging.database.error", new Object[]{e.getMessage()}, Locale.ENGLISH),
                            DataWizErrorCodes.DATABASE_ERROR);
                }
                if (currUser != null) {

                }
            }
        }
        return actLock;
    }

    /**
     * @param sForm
     * @param studyId
     * @param pid
     * @param user
     * @return
     * @throws DataWizSystemException
     */
    // TODO EXCEPTION OR MESSAGES
    public StudyDTO saveStudyForm(StudyForm sForm, final long studyId, final long pid, final UserDTO user, final boolean createCopy)
            throws DataWizSystemException {
        TransactionStatus status = txManager.getTransaction(new DefaultTransactionDefinition());
        StudyDTO study = null;
        if (sForm != null)
            study = sForm.getStudy();
        if (study != null && pid > 0) {
            try {
                study.setLastUserId(user.getId());
                if (studyId > 0) {
                    if (study.getId() != studyId)
                        throw new DataWizSystemException(messageSource.getMessage("logging.session.error", new Object[]{}, LocaleContextHolder.getLocale()),
                                DataWizErrorCodes.SESSION_ERROR);
                    studyDAO.update(study, false);
                } else {
                    study.setProjectId(pid);
                    studyDAO.insert(study, false);
                }
                // update Contibutors
                if (!createCopy) {
                    List<ContributorDTO> dbList = contributorDAO.findByStudy(study.getId());
                    if (!ListUtil.equalsWithoutOrder(dbList, study.getContributors())) {
                        if (dbList != null)
                            contributorDAO.deleteFromStudy(dbList);
                        if (study.getContributors() != null)
                            contributorDAO.insertStudyRelation(study.getContributors(), study.getId());
                    }
                }
                // update SOFTWARE
                updateStudyListItems(study.getId(), study.getSoftware(), DWFieldTypes.SOFTWARE, createCopy);
                // update PUBONDATA
                updateStudyListItems(study.getId(), study.getPubOnData(), DWFieldTypes.PUBONDATA, createCopy);
                // update CONFLINTEREST
                updateStudyListItems(study.getId(), study.getConflInterests(), DWFieldTypes.CONFLINTEREST, createCopy);
                // update OBJECTIVES
                updateStudyListItems(study.getId(), study.getObjectives(), DWFieldTypes.OBJECTIVES, createCopy);
                // update RELTHEORY
                updateStudyListItems(study.getId(), study.getRelTheorys(), DWFieldTypes.RELTHEORY, createCopy);
                // update INTERARMS
                updateStudyListItems(study.getId(), study.getInterArms(), DWFieldTypes.INTERARMS, createCopy);
                // update MEASOCCNAME
                updateStudyListItems(study.getId(), study.getMeasOcc(), DWFieldTypes.MEASOCCNAME, createCopy);
                // update CONSTRUCTS
                updateConstructsItems(study.getId(), study.getConstructs(), createCopy);
                // update INSTRUMENTS
                updateInstrumentItems(study.getId(), study.getInstruments(), createCopy);
                // update MEASOCCNAME
                updateStudyListItems(study.getId(), study.getEligibilities(), DWFieldTypes.ELIGIBILITY, createCopy);
                // update usedCollectionModes
                List<Integer> collectionModes = formTypeDAO.findSelectedFormTypesByIdAndType(study.getId(), DWFieldTypes.COLLECTIONMODE, true);
                if (!ListUtil.equalsWithoutOrder(collectionModes, study.getUsedCollectionModes())) {
                    if (collectionModes != null && collectionModes.size() > 0) {
                        formTypeDAO.deleteSelectedFormType(study.getId(), collectionModes, true);
                    }
                    if (study.getUsedCollectionModes() != null && study.getUsedCollectionModes().size() > 0) {
                        formTypeDAO.insertSelectedFormType(study.getId(), study.getUsedCollectionModes(), true);
                    }
                }
                // update UsedSourFormat
                List<Integer> sourFormat = formTypeDAO.findSelectedFormTypesByIdAndType(study.getId(), DWFieldTypes.DATAFORMAT, true);
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
                txManager.rollback(status);
                throw new DataWizSystemException(messageSource.getMessage("logging.database.error", new Object[]{e.getMessage()}, LocaleContextHolder.getLocale()),
                        DataWizErrorCodes.DATABASE_ERROR, e);
            }
        } else {
            throw new DataWizSystemException(messageSource.getMessage("logging.study.not.found", new Object[]{studyId}, LocaleContextHolder.getLocale()),
                    DataWizErrorCodes.STUDY_NOT_AVAILABLE);
        }
        return study;
    }

    /**
     * @param studyId
     * @param type
     * @throws Exception
     */
    private void updateStudyListItems(final Long studyId, List<StudyListTypesDTO> list, final DWFieldTypes type, final boolean createCopy) throws Exception {
        List<StudyListTypesDTO> dbtmp = studyListTypesDAO.findAllByStudyAndType(studyId, type);
        if (!ListUtil.equalsWithoutOrder(dbtmp, list)) {
            List<StudyListTypesDTO> insert = new ArrayList<>();
            List<StudyListTypesDTO> delete = new ArrayList<>();
            List<StudyListTypesDTO> update = new ArrayList<>();
            if (list != null && list.size() > 0)
                for (StudyListTypesDTO tmp : list) {
                    if (createCopy || (tmp.getId() <= 0 && !tmp.getText().trim().isEmpty())) {
                        tmp.setStudyid(studyId);
                        tmp.setType(type);
                        tmp.setSort(0);
                        tmp.setTimetable(false);
                        insert.add(tmp);
                    } else if (tmp.getId() > 0 && tmp.getText().trim().isEmpty()) {
                        dbtmp.remove(tmp);
                        delete.add(tmp);
                    } else if (tmp.getId() > 0 && !tmp.getText().trim().isEmpty()) {
                        for (ListIterator<StudyListTypesDTO> iter = dbtmp.listIterator(); iter.hasNext(); ) {
                            StudyListTypesDTO tmp2 = iter.next();
                            if (tmp.getId() == tmp2.getId()) {
                                if (!tmp.getText().equals(tmp2.getText())
                                        || (type.equals(DWFieldTypes.MEASOCCNAME) && (tmp.getSort() != tmp2.getSort() || tmp.isTimetable() == tmp2.isTimetable()))) {
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

    private void updateConstructsItems(final Long studyId, List<StudyConstructDTO> list, final boolean createCopy) throws Exception {
        List<StudyConstructDTO> dbtmp = studyConstructDAO.findAllByStudy(studyId);
        if (!ListUtil.equalsWithoutOrder(dbtmp, list)) {
            List<StudyConstructDTO> insert = new ArrayList<>();
            List<StudyConstructDTO> delete = new ArrayList<>();
            List<StudyConstructDTO> update = new ArrayList<>();
            if (list != null && list.size() > 0)
                for (StudyConstructDTO tmp : list) {
                    if (createCopy || (tmp.getId() <= 0 && !tmp.getName().trim().isEmpty())) {
                        tmp.setStudyId(studyId);
                        insert.add(tmp);
                    } else if (tmp.getId() > 0 && tmp.getName().trim().isEmpty()) {
                        delete.add(tmp);
                    } else if (tmp.getId() > 0 && !tmp.getName().trim().isEmpty()) {
                        for (ListIterator<StudyConstructDTO> iter = dbtmp.listIterator(); iter.hasNext(); ) {
                            StudyConstructDTO tmp2 = iter.next();
                            if (tmp.getId() == tmp2.getId()) {
                                if (!tmp.getName().equals(tmp2.getName()) || !tmp.getType().equals(tmp2.getType()) || !tmp.getOther().equals(tmp2.getOther())) {
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

    private void updateInstrumentItems(final Long studyId, List<StudyInstrumentDTO> list, final boolean createCopy) throws Exception {
        List<StudyInstrumentDTO> dbtmp = studyInstrumentDAO.findAllByStudy(studyId, false);
        if (!ListUtil.equalsWithoutOrder(dbtmp, list)) {
            List<StudyInstrumentDTO> insert = new ArrayList<>();
            List<StudyInstrumentDTO> delete = new ArrayList<>();
            List<StudyInstrumentDTO> update = new ArrayList<>();
            if (list != null && list.size() > 0)
                for (StudyInstrumentDTO tmp : list) {
                    if (createCopy || (tmp.getId() <= 0 && !tmp.getTitle().trim().isEmpty())) {
                        tmp.setStudyId(studyId);
                        insert.add(tmp);
                    } else if (tmp.getId() > 0 && tmp.getTitle().trim().isEmpty()) {
                        delete.add(tmp);
                    } else if (tmp.getId() > 0 && !tmp.getTitle().trim().isEmpty()) {
                        for (ListIterator<StudyInstrumentDTO> iter = dbtmp.listIterator(); iter.hasNext(); ) {
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
     * @param study
     * @throws Exception
     */
    public void setStudyDTO(final StudyDTO study) throws Exception {
        if (study != null && study.getId() > 0) {
            study.setContributors(contributorDAO.findByStudy(study.getId()));
            study.setSoftware(ListUtil.addObject(studyListTypesDAO.findAllByStudyAndType(study.getId(), DWFieldTypes.SOFTWARE), new StudyListTypesDTO()));
            study.setPubOnData(ListUtil.addObject(studyListTypesDAO.findAllByStudyAndType(study.getId(), DWFieldTypes.PUBONDATA), new StudyListTypesDTO()));
            study.setConflInterests(ListUtil.addObject(studyListTypesDAO.findAllByStudyAndType(study.getId(), DWFieldTypes.CONFLINTEREST), new StudyListTypesDTO()));
            study.setRelTheorys(ListUtil.addObject(studyListTypesDAO.findAllByStudyAndType(study.getId(), DWFieldTypes.RELTHEORY), new StudyListTypesDTO()));
            study.setObjectives(ListUtil.addObject(studyListTypesDAO.findAllByStudyAndType(study.getId(), DWFieldTypes.OBJECTIVES), new StudyListTypesDTO()));
            study.setMeasOcc(ListUtil.addObject(studyListTypesDAO.findAllByStudyAndType(study.getId(), DWFieldTypes.MEASOCCNAME), new StudyListTypesDTO()));
            study.setInterArms(ListUtil.addObject(studyListTypesDAO.findAllByStudyAndType(study.getId(), DWFieldTypes.INTERARMS), new StudyListTypesDTO()));
            study.setConstructs(ListUtil.addObject(studyConstructDAO.findAllByStudy(study.getId()), new StudyConstructDTO()));
            study.setInstruments(ListUtil.addObject(studyInstrumentDAO.findAllByStudy(study.getId(), false), new StudyInstrumentDTO()));
            study.setEligibilities(ListUtil.addObject(studyListTypesDAO.findAllByStudyAndType(study.getId(), DWFieldTypes.ELIGIBILITY), new StudyListTypesDTO()));
            study.setUsedCollectionModes(formTypeDAO.findSelectedFormTypesByIdAndType(study.getId(), DWFieldTypes.COLLECTIONMODE, true));
            study.setUsedSourFormat(formTypeDAO.findSelectedFormTypesByIdAndType(study.getId(), DWFieldTypes.DATAFORMAT, true));
        }
    }

    public void setStudyDTOExport(final StudyDTO study) throws Exception {
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
            study.setUsedCollectionModes(formTypeDAO.findSelectedFormTypesByIdAndType(study.getId(), DWFieldTypes.COLLECTIONMODE, true));
            study.setUsedSourFormat(formTypeDAO.findSelectedFormTypesByIdAndType(study.getId(), DWFieldTypes.DATAFORMAT, true));
        }
    }


    private void cleanContributorList(final List<ContributorDTO> pContri, final List<ContributorDTO> sContri) {
        if (pContri != null && sContri != null)
            for (Iterator<ContributorDTO> it = pContri.iterator(); it.hasNext(); ) {
                ContributorDTO ccontri = it.next();
                for (ContributorDTO scontri : sContri)
                    if (scontri.getId() == ccontri.getId())
                        it.remove();
            }
    }


    public void createStudyBreadCrump(final String projectName, final String studyName, final long pid, final ModelMap model) {
        model
                .put("breadcrumbList",
                        BreadCrumbUtil
                                .generateBC(PageState.STUDY,
                                        new String[]{projectName,
                                                (studyName != null && !studyName.trim().isEmpty() ? studyName
                                                        : messageSource.getMessage("study.new.study.breadcrump", null, LocaleContextHolder.getLocale()))},
                                        new long[]{pid}, messageSource));
    }

    /**
     * @param pid
     * @param studyId
     * @param user
     * @return
     */
    // TODO FEHLERMELDEUNGEN
    public void deleteStudy(final long pid, final long studyId, final UserDTO user, final Boolean singleCommit)
            throws DataWizSystemException {
        TransactionStatus status = null;
        if (singleCommit)
            status = txManager.getTransaction(new DefaultTransactionDefinition());
        if (studyId <= 0) {
            log.warn("StudyId emtpy - study delete aborted");
            throw new DataWizSystemException(messageSource.getMessage("logging.studyid.not.presentt", null, Locale.ENGLISH), DataWizErrorCodes.MISSING_STUDYID_ERROR);
        }
        if (pid <= 0) {
            log.warn("ProjectId emtpy - study delete aborted");
            throw new DataWizSystemException(messageSource.getMessage("logging.pid.not.present", null, Locale.ENGLISH), DataWizErrorCodes.MISSING_PID_ERROR);
        }
        if (user.hasRole(Roles.ADMIN) || user.hasRole(Roles.PROJECT_ADMIN, pid, false)) {
            try {
                StudyDTO study = studyDAO.findById(studyId, pid, true, true);
                if (study != null) {
                    List<RecordDTO> records = recordDAO.findRecordsWithStudyID(studyId);
                    if (records != null && !records.isEmpty()) {
                        for (RecordDTO rec : records) {
                            recordService.deleteRecord(pid, studyId, rec.getId(), user, false);
                        }
                    }
                    studyDAO.deleteStudy(study.getId());
                    if (singleCommit)
                        txManager.commit(status);
                } else {
                    log.warn("No Study found for studyID {}", () -> studyId);
                    throw new DataWizSystemException(messageSource.getMessage("logging.study.not.found", new Object[]{studyId}, Locale.ENGLISH),
                            DataWizErrorCodes.STUDY_NOT_AVAILABLE);
                }
            } catch (Exception e) {
                if (singleCommit)
                    txManager.rollback(status);
                if (e instanceof DataWizSystemException) {
                    log.warn("DeleteStudy DataWizSystemException:", () -> e);
                    if (((DataWizSystemException) e).getErrorCode().equals(DataWizErrorCodes.STUDY_NOT_AVAILABLE)) {
                        throw (DataWizSystemException) e;
                    } else {
                        throw new DataWizSystemException(messageSource.getMessage("logging.record.delete.error",
                                new Object[]{((DataWizSystemException) e).getErrorCode(), e.getMessage()}, Locale.ENGLISH), DataWizErrorCodes.RECORD_DELETE_ERROR, e);
                    }
                }
                log.fatal("DeleteStudy Database-Exception:", () -> e);
                throw new DataWizSystemException(messageSource.getMessage("logging.database.error", new Object[]{e.getMessage()}, Locale.ENGLISH),
                        DataWizErrorCodes.DATABASE_ERROR, e);
            }
        } else {
            log.warn("User [email:{}; id: {}] tried to delete Study [projectId: {}; studyId: {}]", user::getEmail, user::getId, () -> pid,
                    () -> studyId);
            throw new DataWizSystemException(
                    messageSource.getMessage("logging.user.permitted", new Object[]{user.getEmail(), "study", studyId}, Locale.ENGLISH),
                    DataWizErrorCodes.USER_ACCESS_STUDY_PERMITTED);
        }
    }

    /**
     * Validate study form.
     *
     * @param sForm          the s form
     * @param bRes           the b res
     * @param cls            the cls
     * @param state          the state
     * @param validateErrors the validate errors
     * @return true, if successful
     */
    public boolean validateStudyForm(final StudyForm sForm, final BindingResult bRes, final Class<?> cls, final PageState state, Set<String> validateErrors) {
        boolean error = false;
        if (sForm != null && bRes != null) {
            BeanPropertyBindingResult bResTmp = new BeanPropertyBindingResult(sForm, bRes.getObjectName());
            validator.validate(sForm, bResTmp, cls);
            List<ObjectError> errors = new ArrayList<>();
            if (bResTmp != null && bResTmp.hasErrors()) {
                bResTmp.getAllErrors().parallelStream().forEach(err -> errors.add(err));
                Iterator<ObjectError> itt = errors.iterator();
                while (itt.hasNext()) {
                    ObjectError err = itt.next();
                    if (!state.equals(PageState.STUDYGENERAL) && err.getCodes() != null && err.getCodes()[0] != null
                            && (err.getCodes()[0].contains("software") || err.getCodes()[0].contains("pubOnData") || err.getCodes()[0].contains("conflInterests"))) {
                        itt.remove();
                    } else if (!state.equals(PageState.STUDYDESIGN) && err.getCodes() != null && err.getCodes()[0] != null && (err.getCodes()[0].contains("objectives")
                            || err.getCodes()[0].contains("relTheorys") || err.getCodes()[0].contains("measOcc") || err.getCodes()[0].contains("interArms"))) {
                        itt.remove();
                    } else if (!state.equals(PageState.STUDYSAMPLE) && err.getCodes() != null && err.getCodes()[0] != null
                            && err.getCodes()[0].contains("eligibilities")) {
                        itt.remove();
                    }
                }
            }
            if (!errors.isEmpty()) {
                error = true;
                switch (state) {
                    case STUDYGENERAL:
                        validateErrors.add(messageSource.getMessage("study.record.error.global.general", null, LocaleContextHolder.getLocale()));
                        break;
                    case STUDYDESIGN:
                        validateErrors.add(messageSource.getMessage("study.record.error.global.design", null, LocaleContextHolder.getLocale()));
                        break;
                    case STUDYETHICAL:
                        validateErrors.add(messageSource.getMessage("study.record.error.global.ethical", null, LocaleContextHolder.getLocale()));
                        break;
                    case STUDYSAMPLE:
                        validateErrors.add(messageSource.getMessage("study.record.error.global.sample", null, LocaleContextHolder.getLocale()));
                        break;
                    case STUDYSURVEY:
                        validateErrors.add(messageSource.getMessage("study.record.error.global.survey", null, LocaleContextHolder.getLocale()));
                        break;
                    default:
                        validateErrors.add(messageSource.getMessage("study.record.error.global.default", null, LocaleContextHolder.getLocale()));
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
     * @param pid
     * @param studyId
     * @param selected
     * @param user
     * @return
     * @throws Exception
     * @throws DataWizSystemException
     */
    public StudyDTO copyStudy(final long pid, final long studyId, final long selected, final UserDTO user) throws Exception, DataWizSystemException {
        if (pid <= 0)
            throw new DataWizSystemException(messageSource.getMessage("logging.pid.not.present", null, LocaleContextHolder.getLocale()),
                    DataWizErrorCodes.MISSING_PID_ERROR);
        if (studyId <= 0)
            throw new DataWizSystemException(messageSource.getMessage("logging.studyid.not.present", null, LocaleContextHolder.getLocale()),
                    DataWizErrorCodes.MISSING_STUDYID_ERROR);
        if (selected <= 0)
            throw new DataWizSystemException(messageSource.getMessage("logging.param.not.present", new Object[]{"selected"}, LocaleContextHolder.getLocale()),
                    DataWizErrorCodes.NO_DATA_ERROR);
        if (user == null || (!user.hasRole(Roles.ADMIN) && !user.hasRole(Roles.PROJECT_ADMIN, pid, false))) {
            throw new DataWizSystemException(messageSource.getMessage("logging.user.permitted", new Object[]{user != null ? user.getId() : null, "study", studyId},
                    LocaleContextHolder.getLocale()), DataWizErrorCodes.USER_ACCESS_STUDY_PERMITTED);
        }
        StudyDTO study = studyDAO.findById(studyId, pid, false, false);
        if (study != null) {
            setStudyDTOExport(study);
            study.setTitle(messageSource.getMessage("study.duplicate.prefix", null, LocaleContextHolder.getLocale()) + study.getTitle());
            StudyForm sForm = (StudyForm) applicationContext.getBean("StudyForm");
            study.setId(0);
            study.setProjectId(selected);
            sForm.setStudy(study);
            study = saveStudyForm(sForm, 0, selected, user, true);
        }
        return study;
    }

    /**
     * TODO
     *
     * @param pid
     * @param type
     * @param locale
     * @return
     * @throws Exception
     */
    public byte[] createStudyExport(final long pid, final long studyId, final String type, final Locale locale) throws Exception {
        StudyForm sForm = (StudyForm) applicationContext.getBean("StudyForm");
        if (pid > 0 && studyId > 0) {
            StudyDTO study = studyDAO.findById(studyId, pid, false, false);
            if (study != null) {
                setStudyDTOExport(study);
                sForm.setStudy(study);
            }
        } else if (pid <= 0)
            throw new DataWizSystemException("Error Missing PID Parameter", DataWizErrorCodes.MISSING_PID_ERROR);
        else
            throw new DataWizSystemException("Error Missing StudyID Parameter", DataWizErrorCodes.MISSING_STUDYID_ERROR);
        byte[] content = null;
        switch (type) {
            case "PreReg":
                content = odfUtil.createPreRegistrationDoc(sForm, locale);
                break;
            case "PsychData":
                content = odfUtil.createPsychdataDoc(sForm, locale);
                break;
            default:
                break;
        }
        return content;
    }

}
