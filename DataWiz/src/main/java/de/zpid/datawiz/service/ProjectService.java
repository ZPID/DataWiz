package de.zpid.datawiz.service;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.ui.ModelMap;
import org.springframework.util.FileCopyUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import de.zpid.datawiz.dao.ContributorDAO;
import de.zpid.datawiz.dao.DmpDAO;
import de.zpid.datawiz.dao.FileDAO;
import de.zpid.datawiz.dao.FormTypesDAO;
import de.zpid.datawiz.dao.ProjectDAO;
import de.zpid.datawiz.dao.RoleDAO;
import de.zpid.datawiz.dao.StudyDAO;
import de.zpid.datawiz.dao.UserDAO;
import de.zpid.datawiz.dto.ContributorDTO;
import de.zpid.datawiz.dto.DmpDTO;
import de.zpid.datawiz.dto.FileDTO;
import de.zpid.datawiz.dto.ProjectDTO;
import de.zpid.datawiz.dto.StudyDTO;
import de.zpid.datawiz.dto.UserDTO;
import de.zpid.datawiz.dto.UserRoleDTO;
import de.zpid.datawiz.enumeration.DWFieldTypes;
import de.zpid.datawiz.enumeration.DataWizErrorCodes;
import de.zpid.datawiz.enumeration.MinioResult;
import de.zpid.datawiz.enumeration.PageState;
import de.zpid.datawiz.enumeration.Roles;
import de.zpid.datawiz.exceptions.DataWizSystemException;
import de.zpid.datawiz.form.ProjectForm;
import de.zpid.datawiz.util.BreadCrumbUtil;
import de.zpid.datawiz.util.FileUtil;
import de.zpid.datawiz.util.ListUtil;
import de.zpid.datawiz.util.MinioUtil;
import de.zpid.datawiz.util.UserUtil;


/**
 * Service class for the Project controller to separate the web logic from the business logic.
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
 **/
@Service
public class ProjectService {

    private final MessageSource messageSource;
    private final ProjectDAO projectDAO;
    private final RoleDAO roleDAO;
    private final StudyDAO studyDAO;
    private final FileDAO fileDAO;
    private final ContributorDAO contributorDAO;
    private final FormTypesDAO formTypeDAO;
    private final DmpDAO dmpDAO;
    private final ClassPathXmlApplicationContext applicationContext;
    private final UserDAO userDAO;
    private final PlatformTransactionManager txManager;
    private final StudyService studyService;
    private final FileUtil fileUtil;
    private final MinioUtil minioUtil;

    private static Logger log = LogManager.getLogger(ProjectService.class);

    @Autowired
    public ProjectService(MessageSource messageSource, ProjectDAO projectDAO, RoleDAO roleDAO, StudyDAO studyDAO, FileDAO fileDAO,
                          ContributorDAO contributorDAO, FormTypesDAO formTypeDAO, DmpDAO dmpDAO, ClassPathXmlApplicationContext applicationContext,
                          UserDAO userDAO, PlatformTransactionManager txManager, StudyService studyService, MinioUtil minioUtil, FileUtil fileUtil) {
        this.messageSource = messageSource;
        this.projectDAO = projectDAO;
        this.roleDAO = roleDAO;
        this.studyDAO = studyDAO;
        this.fileDAO = fileDAO;
        this.contributorDAO = contributorDAO;
        this.formTypeDAO = formTypeDAO;
        this.dmpDAO = dmpDAO;
        this.applicationContext = applicationContext;
        this.userDAO = userDAO;
        this.txManager = txManager;
        this.studyService = studyService;
        this.minioUtil = minioUtil;
        this.fileUtil = fileUtil;
    }


    /**
     * Checks if a user has a suitable role to enter a project and/or study.
     *
     * @param pid                Project identifier as long
     * @param studyId            Study identifier as long
     * @param redirectAttributes {@link RedirectAttributes}
     * @param onlyWrite          true if an user tries to write a project or study
     * @param user               {@link UserDTO} contains user information
     * @return {@link String} with redirect mapping if access is not allowed for this user, otherwise null
     */
    public String checkUserAccess(final long pid, final long studyId, final RedirectAttributes redirectAttributes, final boolean onlyWrite,
                                  final UserDTO user) {
        String ret = null;
        if (user == null) {
            log.warn(messageSource.getMessage("logging.user.auth.missing", null, Locale.ENGLISH));
            ret = "redirect:/login";
        } else if (pid == 0 || checkProjectRoles(user, pid, studyId > 0 ? studyId : -1, onlyWrite, true) == null) {
            log.warn(
                    "WARN: access denied because of: " + (pid == 0 ? "missing project identifier" : "user [id: {}] has no rights to read/write study [id: {}]"),
                    user::getId, () -> (studyId > 0 ? studyId : 0));
            redirectAttributes.addFlashAttribute("errorMSG", messageSource.getMessage("project.not.available", null, LocaleContextHolder.getLocale()));
            ret = pid == 0 ? "redirect:/panel" : "redirect:/project/" + pid;
        }
        return ret;
    }

    /**
     * Checks if the passed UserDTO has the PROJECT_ADMIN role.
     *
     * @param redirectAttributes {@link RedirectAttributes}
     * @param projectId          Project identifier as long
     * @param admin              {@link UserDTO} contains user information
     * @return String with redirect entry if role is non-existent, and null if existent
     */
    public String checkProjectAdmin(final RedirectAttributes redirectAttributes, final long projectId, final UserDTO admin) {
        if (admin == null) {
            log.warn(messageSource.getMessage("logging.user.auth.missing", null, Locale.ENGLISH));
            return "redirect:/login";
        }
        log.trace("Entering checkProjectAdmin for project [id: {}] and user [mail: {}] ", () -> projectId, admin::getEmail);
        String ret = null;
        try {
            admin.setGlobalRoles(roleDAO.findRolesByUserID(admin.getId()));
            if (!admin.hasRole(Roles.PROJECT_ADMIN, Optional.of(projectId), false) && !admin.hasRole(Roles.ADMIN)) {
                redirectAttributes.addFlashAttribute("errorMSG", messageSource.getMessage("roles.access.denied", null, LocaleContextHolder.getLocale()));
                log.warn("SECURITY: User with email: " + admin.getEmail() + " tries change roles for project:" + projectId + " without having permission");
                ret = "redirect:/access/" + projectId;
            }
        } catch (Exception e) {
            log.error("ERROR: Database Error while getting roles - Exception:", e);
            ret = "redirect:/access/" + projectId;
        }
        log.trace("Leaving checkProjectAdmin for project [id: {}] and user [mail: {}] ", () -> projectId, admin::getEmail);
        return ret;
    }

    /**
     * Creates a new ProjectForm
     *
     * @return new {@link ProjectForm}
     */
    ProjectForm createProjectForm() {
        ProjectForm pForm = (ProjectForm) applicationContext.getBean("ProjectForm");
        pForm.setDataTypes(formTypeDAO.findAllByType(true, DWFieldTypes.DATATYPE));
        pForm.setCollectionModes(formTypeDAO.findAllByType(true, DWFieldTypes.COLLECTIONMODE));
        pForm.setMetaPurposes(formTypeDAO.findAllByType(true, DWFieldTypes.METAPORPOSE));
        return pForm;
    }

    /**
     * Builds a complete Project form, depending on the passed {@link PageState} and user {@link Roles}
     *
     * @param pForm    {@link ProjectForm} This form is filled with information in this function
     * @param pid      Project identifier as long
     * @param user     {@link UserDTO} contains user information
     * @param call     {@link PageState} includes the information for which site the form has to be build
     * @param userRole {@link Roles} includes the role of the current user
     * @throws DataWizSystemException Throws one of the following exceptions:
     *                                PROJECT_NOT_AVAILABLE
     *                                USER_ACCESS_PROJECT_PERMITTED
     *                                MISSING_PID_ERROR
     */
    public void getProjectForm(final ProjectForm pForm, final long pid, final UserDTO user, final PageState call, final Roles userRole) throws DataWizSystemException {
        log.trace("Entering getProjectData for project [id: {}] and user [mail: {}] ", () -> pid, user::getEmail);
        // security access check!
        if (pid > 0) {
            if (userRole == null) {
                throw new DataWizSystemException(
                        "SECURITY: User with email: " + user.getEmail() + " tries to get access to project:" + pid + " without having the permissions to read",
                        DataWizErrorCodes.USER_ACCESS_PROJECT_PERMITTED);
            }
            final ProjectDTO pdto = projectDAO.findById(pid);
            if (pdto == null || pdto.getId() <= 0) {
                throw new DataWizSystemException("Project is empty for user=" + user.getEmail() + " and project=" + pid, DataWizErrorCodes.PROJECT_NOT_AVAILABLE);
            }
            pForm.setProject(pdto);
            // load all data if user has full project rights
            if (userRole.equals(Roles.ADMIN) || userRole.equals(Roles.PROJECT_ADMIN) || userRole.equals(Roles.PROJECT_READER)
                    || userRole.equals(Roles.PROJECT_WRITER)) {
                setSpecificPageData(pForm, call);
            } else if (userRole.equals(Roles.DS_READER) || userRole.equals(Roles.DS_WRITER)) {
                List<UserRoleDTO> userRoles = roleDAO.findRolesByUserIDAndProjectID(user.getId(), pid);
                List<StudyDTO> cStud = new ArrayList<>();
                for (UserRoleDTO role : userRoles) {
                    Roles uRole = Roles.valueOf(role.getType());
                    if (role.getStudyId() > 0 && (uRole.equals(Roles.DS_READER) || uRole.equals(Roles.DS_WRITER))) {
                        cStud.add(studyDAO.findById(role.getStudyId(), role.getProjectId(), true, false));
                    }
                }
                pForm.setStudies(cStud);
            }
            log.trace("Leaving getProjectData successfully for project [id: {}] and user [mail: {}] ", () -> pid, user::getEmail);
        } else {
            log.warn("ProjectID empty - NULL returned!");
            throw new DataWizSystemException("ProjectID or UserDTO is empty - getProjectForm() aborted!", DataWizErrorCodes.MISSING_PID_ERROR);
        }
    }

    /**
     * Is called by getProjectForm to load the form data for a specific page, depending on the  {@link PageState} call
     *
     * @param pForm {@link ProjectForm} This form is filled with information in this function
     * @param call  {@link PageState} includes the information for which site the form has to be build
     */
    private void setSpecificPageData(final ProjectForm pForm, final PageState call) {
        // load /project data
        if (call == null || call.equals(PageState.PROJECT)) {
            pForm.setContributors(ListUtil.addObject(contributorDAO.findByProject(pForm.getProject(), false, false), new ContributorDTO()));
            pForm.setPrimaryContributor(contributorDAO.findPrimaryContributorByProject(pForm.getProject()));
        } // load /project/xx/studies
        else if (call.equals(PageState.STUDIES)) {
            pForm.setStudies(studyDAO.findAllStudiesByProjectId(pForm.getProject()));
            pForm.setSharedUser(userDAO.findGroupedByProject(pForm.getProject().getId()));
            if (pForm.getStudies() != null)
                for (StudyDTO stud : pForm.getStudies()) {
                    stud.setContributors(contributorDAO.findByStudy(stud.getId()));
                }
        } // load /project/xx/material
        else if (call.equals(PageState.MATERIAL)) {
            pForm.setFiles(fileDAO.findProjectMaterialFiles(pForm.getProject()));
        } // load /dmp data
        else if (call.equals(PageState.DMP)) {
            DmpDTO dmp = dmpDAO.findByID(pForm.getProject());
            if (dmp != null && dmp.getId() > 0) {
                dmp.setUsedDataTypes(formTypeDAO.findSelectedFormTypesByIdAndType(dmp.getId(), DWFieldTypes.DATATYPE, false));
                dmp.setUsedCollectionModes(formTypeDAO.findSelectedFormTypesByIdAndType(dmp.getId(), DWFieldTypes.COLLECTIONMODE, false));
                dmp.setSelectedMetaPurposes(formTypeDAO.findSelectedFormTypesByIdAndType(dmp.getId(), DWFieldTypes.METAPORPOSE, false));
            }
            if (dmp == null || dmp.getId() <= 0) {
                dmp = (DmpDTO) applicationContext.getBean("DmpDTO");
            }
            pForm.setDmp(dmp);
            pForm.setDataTypes(formTypeDAO.findAllByType(true, DWFieldTypes.DATATYPE));
            pForm.setCollectionModes(formTypeDAO.findAllByType(true, DWFieldTypes.COLLECTIONMODE));
            pForm.setMetaPurposes(formTypeDAO.findAllByType(true, DWFieldTypes.METAPORPOSE));
            pForm.setPrimaryContributor(contributorDAO.findPrimaryContributorByProject(pForm.getProject()));
        } // load /access data
        else if (call.equals(PageState.ACCESS)) {
            pForm.setStudies(studyDAO.findAllStudiesByProjectId(pForm.getProject()));
        }
    }

    /**
     * Saves a new project into the dbs or updates an existing project from the dbs
     *
     * @param pForm {@link ProjectForm} contains the project data
     * @return Returns {@link DataWizErrorCodes}:
     * OK on success, otherwise:
     * MISSING_PID_ERROR
     * NO_DATA_ERROR
     * DATABASE_ERROR
     */
    public DataWizErrorCodes saveOrUpdateProject(final ProjectForm pForm) {
        log.trace("Entering saveOrUpdateProject for project [id: {}] ", () -> pForm.getProject().getId());
        DataWizErrorCodes success = DataWizErrorCodes.OK;
        TransactionStatus status = txManager.getTransaction(new DefaultTransactionDefinition());
        try {
            UserDTO user = UserUtil.getCurrentUser();
            if (pForm != null && pForm.getProject() != null && user != null) {
                if (pForm.getProject().getId() <= 0) {
                    pForm.getProject().setOwnerId(user.getId());
                    pForm.getProject().setLastUserId(user.getId());
                    int chk = projectDAO.insertProject(pForm.getProject());
                    if (chk > 0) {
                        roleDAO.saveRole(new UserRoleDTO(Roles.REL_ROLE.toInt(), user.getId(), chk, 0, Roles.REL_ROLE.name()));
                        roleDAO.saveRole(new UserRoleDTO(Roles.PROJECT_ADMIN.toInt(), user.getId(), chk, 0, Roles.PROJECT_ADMIN.name()));
                        pForm.getProject().setId(chk);
                    } else {
                        success = DataWizErrorCodes.MISSING_PID_ERROR;
                    }
                } else {
                    pForm.getProject().setLastUserId(user.getId());
                    projectDAO.updateProject(pForm.getProject());
                }
                List<ContributorDTO> contriDBList = contributorDAO.findByProject(pForm.getProject(), false, false);
                if (pForm.getContributors() != null && !pForm.getContributors().isEmpty() && contriDBList != null && !contriDBList.isEmpty()) {
                    Iterator<ContributorDTO> itt = contriDBList.iterator();
                    while (itt.hasNext()) {
                        ContributorDTO contriDB = itt.next();
                        for (ContributorDTO contri : pForm.getContributors()) {
                            if (contri.getId() == contriDB.getId()) {
                                itt.remove();
                                break;
                            }
                        }
                    }
                }
                if (contriDBList != null) {
                    for (ContributorDTO contri : contriDBList) {
                        contributorDAO.deleteContributor(contri);
                    }
                }
                int sort = 1;
                if (pForm.getContributors() != null) {
                    for (ContributorDTO contri : pForm.getContributors()) {
                        contri.setProjectId(pForm.getProject().getId());
                        if (contri.getPrimaryContributor() == null || !contri.getPrimaryContributor())
                            contri.setSort(sort++);
                        else
                            contri.setSort(0);
                        if (contri.getId() <= 0) {
                            contributorDAO.insertContributor(contri);
                            contributorDAO.insertProjectRelation(contri);
                        } else {
                            contributorDAO.updateContributor(contri);
                        }
                    }
                }
            } else {
                success = DataWizErrorCodes.NO_DATA_ERROR;
            }
            if (UserUtil.getCurrentUser() != null && user != null)
                UserUtil.getCurrentUser().setGlobalRoles(roleDAO.findRolesByUserID(user.getId()));
            txManager.commit(status);
        } catch (Exception e) {
            txManager.rollback(status);
            log.error("Project saving not sucessful error:", () -> e);
            success = DataWizErrorCodes.DATABASE_ERROR;
        }
        log.trace("Leaving saveOrUpdateProject with message[{}]", success::name);
        return success;
    }

    /**
     * Checks the roles for a user and a project or study
     *
     * @param user         {@link UserDTO} contains user information
     * @param pid          Project identifier as long
     * @param studyId      Study identifier as long
     * @param onlyWrite    true if an user tries to write a project or study
     * @param withDSRights true if study roles have to be checked
     * @return The role {@link Roles} which a user has for the specific project
     */
    public Roles checkProjectRoles(final UserDTO user, final long pid, final long studyId, final boolean onlyWrite, final boolean withDSRights) {
        log.trace("Entering checkProjectRoles(SuperController) for user [id: {}], project [id: {}], with Rights to write only [{}] and Studyrights [{}] ",
                user::getId, () -> pid, () -> onlyWrite, () -> withDSRights);
        try {
            if (user.hasRole(Roles.ADMIN)) {
                log.debug("User {} is DataWiz Admin", user::getEmail);
                return Roles.ADMIN;
            }
            final List<UserRoleDTO> userRoles = roleDAO.findRolesByUserIDAndProjectID(user.getId(), pid);
            if (userRoles == null || userRoles.size() <= 0)
                return null;
            userRoles.sort(Comparator.comparingLong(UserRoleDTO::getRoleId));
            for (UserRoleDTO role : userRoles) {
                Roles uRole = Roles.valueOf(role.getType());
                if (uRole.equals(Roles.PROJECT_ADMIN)) {
                    log.debug("User {} has PROJECT_ADMIN role for project [id: {}]", user::getEmail, () -> pid);
                    return Roles.PROJECT_ADMIN;
                } else if (uRole.equals(Roles.PROJECT_WRITER)) {
                    log.debug("User {} has PROJECT_WRITER role for project [id: {}]", user::getEmail, () -> pid);
                    return Roles.PROJECT_WRITER;
                } else if (!onlyWrite && uRole.equals(Roles.PROJECT_READER)) {
                    log.debug("User {} has PROJECT_READER role for project [id: {}]", user::getEmail, () -> pid);
                    return Roles.PROJECT_READER;
                } else if (withDSRights && uRole.equals(Roles.DS_WRITER) && (studyId == 0 || (studyId > 0 && studyId == role.getStudyId()))) {
                    log.debug("User {} has DS_WRITER role for project [id: {}] and study [id: {}]", user::getEmail, () -> pid, () -> studyId);
                    return Roles.DS_WRITER;
                } else if (!onlyWrite && withDSRights && uRole.equals(Roles.DS_READER) && (studyId == 0 || (studyId > 0 && studyId == role.getStudyId()))) {
                    log.debug("User {} has DS_READER role for project [id: {}] and study [id: {}]", user::getEmail, () -> pid, () -> studyId);
                    return Roles.DS_READER;
                }
            }
        } catch (Exception e) {
            log.error("checkProjectRoles not sucessful -> return null! error:", e);
            return null;
        }
        log.debug("Leaving checkProjectRoles without result for user [id: {}] and project [id: {}]", user::getId, () -> pid);
        return null;
    }

    /**
     * Validates the contributor list and the primary contributor
     *
     * @param pForm         {@link ProjectForm} contains the project data
     * @param bindingResult {@link BindingResult} contains validation error messages
     * @return {@link List} of {@link ContributorDTO}
     */
    public List<ContributorDTO> validateContributors(final ProjectForm pForm, final BindingResult bindingResult) {
        List<ContributorDTO> cContri = new ArrayList<>();
        AtomicInteger contriCount = new AtomicInteger(0);
        if (pForm.getContributors() != null) {
            Iterator<ContributorDTO> itt = pForm.getContributors().iterator();
            while (itt.hasNext()) {
                ContributorDTO contri = itt.next();
                if (contri != null && (contri.getFirstName() == null || contri.getLastName() == null)) {
                    itt.remove();
                } else if (contri != null && (!contri.getTitle().trim().equals("") || !contri.getOrcid().trim().equals("") || !contri.getInstitution().trim().equals("")
                        || !contri.getDepartment().trim().equals("") || !contri.getFirstName().trim().equals("") || !contri.getLastName().trim().equals(""))) {
                    if (contri.getFirstName().trim().equals("")) {
                        bindingResult.rejectValue("contributors[" + contriCount.get() + "].firstName", "error.contributor.lastName.firstname");
                    }
                    if (contri.getLastName().trim().equals("")) {
                        bindingResult.rejectValue("contributors[" + contriCount.get() + "].lastName", "error.contributor.lastName.firstname");
                    }
                    cContri.add(contri);
                    contriCount.incrementAndGet();
                }
            }
        }
        if (pForm.getPrimaryContributor() != null
                && (!pForm.getPrimaryContributor().getTitle().trim().equals("") || !pForm.getPrimaryContributor().getOrcid().trim().equals("")
                || !pForm.getPrimaryContributor().getInstitution().trim().equals("") || !pForm.getPrimaryContributor().getDepartment().trim().equals("")
                || !pForm.getPrimaryContributor().getFirstName().trim().equals("") || !pForm.getPrimaryContributor().getLastName().trim().equals(""))) {
            if (pForm.getPrimaryContributor().getFirstName().trim().equals(""))
                bindingResult.rejectValue("primaryContributor.firstName", "error.contributor.lastName.firstname");
            if (pForm.getPrimaryContributor().getLastName().trim().equals(""))
                bindingResult.rejectValue("primaryContributor.lastName", "error.contributor.lastName.firstname");
            pForm.getPrimaryContributor().setPrimaryContributor(true);
            cContri.add(pForm.getPrimaryContributor());
        }
        return cContri;
    }

    /**
     * @param pid     Project identifier as long
     * @param studyId Study identifier as long
     * @param model   {@link ModelMap}
     * @param user    {@link UserDTO} contains user information
     * @param pForm   {@link ProjectForm} This form is filled with information in this function
     * @throws DataWizSystemException Exception thrown by setMaterialForm
     */
    public void setMaterialForm(final long pid, final long studyId, final ModelMap model, final UserDTO user, final ProjectForm pForm) throws DataWizSystemException {
        Roles role = checkProjectRoles(user, pid, 0, false, true);
        getProjectForm(pForm, pid, user, PageState.MATERIAL, role);
        if (studyId <= 0) {
            model.put("breadcrumbList", BreadCrumbUtil.generateBC(PageState.PROJECT, new String[]{pForm.getProject().getTitle()}, null, messageSource));
            model.put("studyId", -1);
        } else {
            StudyDTO study = studyDAO.findById(studyId, pid, true, false);
            studyService.createStudyBreadCrump(pForm.getProject().getTitle(), study.getTitle(), pid, model);
            pForm.setFiles(fileDAO.findStudyMaterialFiles(pid, studyId));
            model.put("studySubMenu", true);
        }
    }

    /**
     * Scales images for thumbnail usage by using fileUtil.buildThumbNailAndSetToResponse
     *
     * @param imgId       Image identifier as long
     * @param response    {@link HttpServletResponse}
     * @param thumbHeight height of the new thumbnail as int
     * @param maxWidth    max-width of the new thumbnail as int
     * @throws IOException thrown by fileUtil.buildThumbNailAndSetToResponse
     */
    public void scaleAndSetThumbnail(final long imgId, final HttpServletResponse response, final int thumbHeight, final int maxWidth) throws IOException {
        FileDTO file = fileDAO.findById(imgId);
        if (minioUtil.getFile(file, false).equals(MinioResult.OK)) {
            fileUtil.buildThumbNailAndSetToResponse(response, file, thumbHeight, maxWidth);
        }
    }

    /**
     * Saves Material to the Minio System and the Meta-Information to the DBS System.
     *
     * @param request {@link MultipartHttpServletRequest} contains the uploaded material
     * @param pid     Project identifier as long
     * @param studyId Study identifier as long
     * @param user    {@link UserDTO} contains user information
     * @return {@link DataWizErrorCodes} Returns one of the following codes:
     * OK (File saved successfully)
     * MINIO_SAVE_ERROR
     * DATABASE_ERROR
     */
    public DataWizErrorCodes saveMaterialToMinioAndDB(final MultipartHttpServletRequest request, final long pid, final long studyId, final UserDTO user) {
        FileDTO file = null;
        DataWizErrorCodes code = DataWizErrorCodes.OK;
        try {
            Iterator<String> itr = request.getFileNames();
            while (itr.hasNext()) {
                String filename = itr.next();
                final MultipartFile mpf = request.getFile(filename);
                if (mpf != null) {
                    file = fileUtil.buildFileDTO(pid, studyId, 0, 0, user.getId(), mpf);
                    MinioResult res = minioUtil.putFile(file, false);
                    if (res.equals(MinioResult.OK)) {
                        fileDAO.saveFile(file);
                    } else {
                        log.warn("ERROR: During saveToMinioAndDB - MinioResult: {}", res::name);
                        code = DataWizErrorCodes.MINIO_SAVE_ERROR;
                    }
                }
            }
        } catch (Exception e) {
            if (file != null && minioUtil.getFile(file, false).equals(MinioResult.OK)) {
                minioUtil.deleteFile(file);
            }
            log.warn("Exception during file saveToMinioAndDB: ", () -> e);
            code = DataWizErrorCodes.DATABASE_ERROR;
        }
        return code;
    }

    /**
     * Prepares material for  download. Gets the file from Minio und the meta information from the dbs system.
     *
     * @param docId    file identifier as long
     * @param response {@link HttpServletResponse} Response with the file stream
     * @return {@link DataWizErrorCodes} Returns one of the following codes:
     * OK (File loaded successfully)
     * MINIO_READ_ERROR
     * DATABASE_ERROR
     */
    public DataWizErrorCodes prepareMaterialDownload(final long docId, final HttpServletResponse response) {
        FileDTO file;
        DataWizErrorCodes code = DataWizErrorCodes.OK;
        try {
            file = fileDAO.findById(docId);
            MinioResult res = minioUtil.getFile(file, false);
            if (res.equals(MinioResult.OK)) {
                response.setContentType(file.getContentType());
                response.setHeader("Content-Disposition", "attachment; filename=\"" + file.getFileName() + "\"");
                response.setContentLength(file.getContent().length);
                FileCopyUtils.copy(file.getContent(), response.getOutputStream());
            } else {
                log.warn("ERROR: During prepareMaterialDownload - MinioResult: {}", res::name);
                code = DataWizErrorCodes.MINIO_READ_ERROR;
            }
        } catch (Exception e) {
            log.warn("Exception during file prepareMaterialDownload: ", () -> e);
            code = DataWizErrorCodes.DATABASE_ERROR;
        }
        return code;
    }

    /**
     * Deletes material from Minio und DB System
     *
     * @param docId File identifier as long
     * @return {@link DataWizErrorCodes} Returns one of the following codes:
     * * OK (File deleted successfully)
     * * MINIO_SAVE_ERROR
     * * DATABASE_ERROR
     */
    public DataWizErrorCodes deleteMaterialfromMinioAndDB(long docId) {
        DataWizErrorCodes code = DataWizErrorCodes.OK;
        try {
            MinioResult res = minioUtil.deleteFile(fileDAO.findById(docId));
            if (res.equals(MinioResult.OK)) {
                fileDAO.deleteFile(docId);
            } else {
                log.warn("ERROR: During deleteMaterialfromMinioAndDB - MinioResult: {}", res::name);
                code = DataWizErrorCodes.MINIO_SAVE_ERROR;
            }
        } catch (Exception e) {
            log.error("WARN: deleteDocument [id: {}] not successful because of DB Error - Message: {}", () -> docId, e::getMessage);
            code = DataWizErrorCodes.DATABASE_ERROR;
        }
        return code;
    }

    /**
     * @param pid
     * @param user
     * @throws DataWizSystemException
     */
    public void deleteProject(final long pid, final UserDTO user) throws DataWizSystemException {
        TransactionStatus status = txManager.getTransaction(new DefaultTransactionDefinition());
        if (pid <= 0) {
            log.warn("ProjectId empty - project delete aborted");
            throw new DataWizSystemException(messageSource.getMessage("logging.pid.not.present", null, Locale.ENGLISH), DataWizErrorCodes.MISSING_PID_ERROR);
        }
        try {
            ProjectDTO project = projectDAO.findById(pid);
            if (user.hasRole(Roles.ADMIN) || user.hasRole(Roles.PROJECT_ADMIN, pid, false)) {
                if (project != null) {
                    List<ContributorDTO> cContri = contributorDAO.findByProject(project, false, true);
                    List<StudyDTO> studies = studyDAO.findAllStudiesByProjectId(project);
                    if (studies != null && !studies.isEmpty()) {
                        for (StudyDTO study : studies) {
                            studyService.deleteStudy(pid, study.getId(), user, false);
                        }
                    }
                    projectDAO.deleteProject(project.getId());
                    if (cContri != null && !cContri.isEmpty())
                        for (ContributorDTO contri : cContri) {
                            contributorDAO.deleteContributor(contri);
                        }
                    txManager.commit(status);
                } else {
                    log.warn("No Project found for PID {}", () -> pid);
                    throw new DataWizSystemException(messageSource.getMessage("logging.project.not.found", new Object[]{pid}, Locale.ENGLISH),
                            DataWizErrorCodes.PROJECT_NOT_AVAILABLE);
                }
            } else {
                log.warn("User [email:{}; id: {}] tried to delete Project [projectId: {}]", user::getEmail, user::getId, () -> pid);
                throw new DataWizSystemException(
                        messageSource.getMessage("logging.user.permitted", new Object[]{user.getEmail(), "project", pid}, Locale.ENGLISH),
                        DataWizErrorCodes.USER_ACCESS_PROJECT_PERMITTED);
            }
        } catch (Exception e) {
            txManager.rollback(status);
            if (e instanceof DataWizSystemException) {
                log.warn("DeleteProject DataWizSystemException:", () -> e);
                if (((DataWizSystemException) e).getErrorCode().equals(DataWizErrorCodes.PROJECT_NOT_AVAILABLE)
                        || ((DataWizSystemException) e).getErrorCode().equals(DataWizErrorCodes.USER_ACCESS_PROJECT_PERMITTED)) {
                    throw (DataWizSystemException) e;
                } else {
                    throw new DataWizSystemException(messageSource.getMessage("logging.study.delete.error",
                            new Object[]{((DataWizSystemException) e).getErrorCode(), e.getMessage()}, Locale.ENGLISH), DataWizErrorCodes.STUDY_DELETE_ERROR, e);
                }
            }
            log.fatal("DeleteStudy Database-Exception:", () -> e);
            throw new DataWizSystemException(messageSource.getMessage("logging.database.error", new Object[]{e.getMessage()}, Locale.ENGLISH),
                    DataWizErrorCodes.DATABASE_ERROR, e);
        }
    }

    /**
     * @param user
     * @return
     */
    public List<ProjectDTO> getAdminProjectList(final UserDTO user) {
        return projectDAO.findAllByAdminRole(user.getId());
    }
}
