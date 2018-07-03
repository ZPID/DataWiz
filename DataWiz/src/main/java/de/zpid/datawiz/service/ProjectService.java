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

@Service
public class ProjectService {

    @Autowired
    private MessageSource messageSource;
    @Autowired
    private ProjectDAO projectDAO;
    @Autowired
    private RoleDAO roleDAO;
    @Autowired
    private StudyDAO studyDAO;
    @Autowired
    private FileDAO fileDAO;
    @Autowired
    private ContributorDAO contributorDAO;
    @Autowired
    private FormTypesDAO formTypeDAO;
    @Autowired
    private DmpDAO dmpDAO;
    @Autowired
    private ClassPathXmlApplicationContext applicationContext;
    @Autowired
    private UserDAO userDAO;
    @Autowired
    private PlatformTransactionManager txManager;
    @Autowired
    private StudyService studyService;
    @Autowired
    private FileUtil fileUtil;
    @Autowired
    private MinioUtil minioUtil;

    private static Logger log = LogManager.getLogger(ProjectService.class);

    /**
     * @param pid
     * @param studyId
     * @param redirectAttributes
     * @param onlyWrite
     * @param user
     * @return
     */
    public String checkUserAccess(final Optional<Long> pid, final Optional<Long> studyId, final RedirectAttributes redirectAttributes, final boolean onlyWrite,
                                  final UserDTO user) {
        String ret = null;
        if (user == null) {
            log.warn(messageSource.getMessage("logging.user.auth.missing", null, Locale.ENGLISH));
            ret = "redirect:/login";
        }
        if (!pid.isPresent() || checkProjectRoles(user, pid.get(), (studyId != null && studyId.isPresent()) ? studyId.get() : -1, onlyWrite, true) == null) {
            log.warn(
                    "WARN: access denied because of: " + (!pid.isPresent() ? "missing project identifier" : "user [id: {}] has no rights to read/write study [id: {}]"),
                    () -> user.getId(), () -> (studyId != null && studyId.isPresent() ? studyId.get() : 0));
            redirectAttributes.addFlashAttribute("errorMSG", messageSource.getMessage("project.not.available", null, LocaleContextHolder.getLocale()));
            ret = !pid.isPresent() ? "redirect:/panel" : "redirect:/project/" + pid.get();
        }
        return ret;
    }


    /**
     * @param pid
     * @param studyId
     * @param redirectAttributes
     * @param onlyWrite
     * @param user
     * @return
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
     * @param redirectAttributes
     * @param projectId
     * @param admin
     * @return String with redirect entry if role is non-existent, and null if existent
     */
    public String checkProjectAdmin(final RedirectAttributes redirectAttributes, final long projectId, final UserDTO admin) {
        log.trace("Entering checkProjectAdmin for project [id: {}] and user [mail: {}] ", () -> projectId, () -> admin.getEmail());
        if (admin == null) {
            log.warn(messageSource.getMessage("logging.user.auth.missing", null, Locale.ENGLISH));
            return "redirect:/login";
        }
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
        log.trace("Method checkProjectAdmin completed");
        return ret;
    }

    /**
     * @return
     * @throws Exception
     */
    public ProjectForm createProjectForm() throws Exception {
        ProjectForm pForm = (ProjectForm) applicationContext.getBean("ProjectForm");
        pForm.setDataTypes(formTypeDAO.findAllByType(true, DWFieldTypes.DATATYPE));
        pForm.setCollectionModes(formTypeDAO.findAllByType(true, DWFieldTypes.COLLECTIONMODE));
        pForm.setMetaPurposes(formTypeDAO.findAllByType(true, DWFieldTypes.METAPORPOSE));
        return pForm;
    }

    /**
     * @param pid
     * @param user
     * @return
     * @throws Exception
     */
    public void getProjectForm(final ProjectForm pForm, final long pid, final UserDTO user, final PageState call, Roles userRole) throws Exception {
        log.trace("Entering getProjectData(SuperController) for project [id: {}] and user [mail: {}] ", () -> pid, () -> user.getEmail());
        // security access check!
        if (pid > 0 && user != null) {
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
                List<StudyDTO> cStud = new ArrayList<StudyDTO>();
                for (UserRoleDTO role : userRoles) {
                    Roles uRole = Roles.valueOf(role.getType());
                    if (role.getStudyId() > 0 && (uRole.equals(Roles.DS_READER) || uRole.equals(Roles.DS_WRITER))) {
                        cStud.add(studyDAO.findById(role.getStudyId(), role.getProjectId(), true, false));
                    }
                }
                pForm.setStudies(cStud);
            }
            log.trace("Method getProjectData(SuperController) successfully completed");
        } else {
            log.warn("ProjectID or UserDTO is empty - NULL returned!");
            if (pid <= 0)
                throw new DataWizSystemException("ProjectID or UserDTO is empty - getProjectForm() aborted!", DataWizErrorCodes.MISSING_PID_ERROR);
            else
                throw new DataWizSystemException("ProjectID or UserDTO is empty - getProjectForm() aborted!", DataWizErrorCodes.MISSING_UID_ERROR);
        }
    }

    /**
     * @param pForm
     * @param call
     * @throws Exception
     */
    private void setSpecificPageData(final ProjectForm pForm, final PageState call) throws Exception {
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
        } // load /export data
        else if (call.equals(PageState.EXPORT)) {

        }
    }

    /**
     * @param pForm
     * @return
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
                for (ContributorDTO contri : contriDBList) {
                    contributorDAO.deleteContributor(contri);
                }
                int sort = 1;
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
            } else {
                success = DataWizErrorCodes.NO_DATA_ERROR;
            }
            if (UserUtil.getCurrentUser() != null)
                UserUtil.getCurrentUser().setGlobalRoles(roleDAO.findRolesByUserID(user.getId()));
            txManager.commit(status);
        } catch (Exception e) {
            txManager.rollback(status);
            log.error("Project saving not sucessful error:", () -> e);
            success = DataWizErrorCodes.DATABASE_ERROR;
        }
        log.trace("Method saveOrUpdateProject completed with sucess={}", success.name());
        return success;
    }

    /**
     * @param user
     * @param pid
     * @param studyid
     * @param onlyWrite
     * @param withDSRights
     * @return
     */
    public Roles checkProjectRoles(final UserDTO user, final long pid, final long studyid, final boolean onlyWrite, final boolean withDSRights) {
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
            userRoles.sort((o1, o2) -> Long.compare(o1.getRoleId(), o2.getRoleId()));
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
                } else if (withDSRights && uRole.equals(Roles.DS_WRITER) && (studyid == 0 || (studyid > 0 && studyid == role.getStudyId()))) {
                    log.debug("User {} has DS_WRITER role for project [id: {}] and study [id: {}]", user::getEmail, () -> pid, () -> studyid);
                    return Roles.DS_WRITER;
                } else if (!onlyWrite && withDSRights && uRole.equals(Roles.DS_READER) && (studyid == 0 || (studyid > 0 && studyid == role.getStudyId()))) {
                    log.debug("User {} has DS_READER role for project [id: {}] and study [id: {}]", user::getEmail, () -> pid, () -> studyid);
                    return Roles.DS_READER;
                }
            }
        } catch (Exception e) {
            log.error("checkProjectRoles not sucessful -> return null! error:", e);
            return null;
        }
        log.debug("Method gcheckProjectRoles ended without result for user [id: {}] and project [id: {}]", () -> user.getId(), () -> pid);
        return null;
    }

    /**
     * @param pForm
     * @param bindingResult
     */
    public List<ContributorDTO> validateContributors(ProjectForm pForm, BindingResult bindingResult) throws Exception {
        List<ContributorDTO> cContri = new ArrayList<ContributorDTO>();
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

            pForm.getContributors().forEach(contri -> {

            });
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
     * @param pid
     * @param studyId
     * @param model
     * @param redirectAttributes
     * @param user
     * @param pForm
     * @return
     * @throws Exception
     */
    public void setMaterialForm(Optional<Long> pid, Optional<Long> studyId, ModelMap model, RedirectAttributes redirectAttributes, UserDTO user,
                                ProjectForm pForm) throws Exception {
        Roles role = checkProjectRoles(user, pid.get(), 0, false, true);
        getProjectForm(pForm, pid.get(), user, PageState.MATERIAL, role);
        if (!studyId.isPresent()) {
            model.put("breadcrumbList", BreadCrumbUtil.generateBC(PageState.PROJECT, new String[]{pForm.getProject().getTitle()}, null, messageSource));
            model.put("studyId", -1);
        } else {
            StudyDTO study = studyDAO.findById(studyId.get(), pid.get(), true, false);
            studyService.createStudyBreadCrump(pForm.getProject().getTitle(), study.getTitle(), pid.get(), model);
            pForm.setFiles(fileDAO.findStudyMaterialFiles(pid.get(), studyId.get()));
            model.put("studySubMenu", true);
        }
    }

    /**
     * @param imgId
     * @param response
     * @param thumbHeight
     * @param maxWidth
     * @throws Exception
     * @throws IOException
     */
    public void scaleAndSetThumbnail(long imgId, HttpServletResponse response, final int thumbHeight, final int maxWidth) throws Exception, IOException {
        FileDTO file = fileDAO.findById(imgId);
        // fileUtil.setFileBytes(file);
        if (minioUtil.getFile(file, false).equals(MinioResult.OK)) {
            fileUtil.buildThumbNailAndSetToResponse(response, file, thumbHeight, maxWidth);
        }
    }

    /**
     * @param pForm
     * @throws Exception
     */
    @Deprecated
    public boolean deleteContributor(ProjectForm pForm) throws Exception {
        if (pForm.getContributors() != null && pForm.getContributors().size() >= pForm.getDelPos()) {
            ContributorDTO selected = pForm.getContributors().get(pForm.getDelPos());
            pForm.getContributors().remove(pForm.getDelPos());
            if (selected.getId() > 0)
                return (contributorDAO.deleteContributor(selected) > 0);
        }
        return true;
    }

    /**
     * @param request
     * @param pid
     * @param studyId
     * @param user
     */
    public DataWizErrorCodes saveMaterialToMinoAndDB(MultipartHttpServletRequest request, Optional<Long> pid, Optional<Long> studyId, UserDTO user) {
        FileDTO file = null;
        DataWizErrorCodes code = DataWizErrorCodes.OK;
        try {
            Iterator<String> itr = request.getFileNames();
            while (itr.hasNext()) {
                String filename = itr.next();
                final MultipartFile mpf = request.getFile(filename);
                if (mpf != null) {
                    file = fileUtil.buildFileDTO(pid.get(), studyId.isPresent() ? studyId.get() : 0, 0, 0, user.getId(), mpf);
                    // String filePath = fileUtil.saveFile(file);
                    MinioResult res = minioUtil.putFile(file, false);
                    if (res.equals(MinioResult.OK)) {
                        fileDAO.saveFile(file);
                    } else {
                        log.warn("ERROR: During saveToMinoAndDB - MinioResult: {}", () -> res.name());
                        code = DataWizErrorCodes.MINIO_SAVE_ERROR;
                    }
                }
            }
        } catch (Exception e) {
            if (file != null && minioUtil.getFile(file, false).equals(MinioResult.OK)) {
                minioUtil.deleteFile(file);
            }
            log.warn("Exception during file saveToMinoAndDB: ", () -> e);
            code = DataWizErrorCodes.DATABASE_ERROR;
        }
        return code;
    }

    /**
     * @param docId
     * @param response
     * @return
     */
    public DataWizErrorCodes prepareMaterialDownload(long docId, HttpServletResponse response) {
        FileDTO file = null;
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
                log.warn("ERROR: During prepareMaterialDownload - MinioResult: {}", () -> res.name());
                code = DataWizErrorCodes.MINIO_READ_ERROR;
            }
        } catch (Exception e) {
            log.warn("Exception during file prepareMaterialDownload: ", () -> e);
            code = DataWizErrorCodes.DATABASE_ERROR;
        }
        return code;
    }

    /**
     * @param docId
     * @return
     */
    public DataWizErrorCodes deleteMaterialfromMinioAndDB(long docId) {
        DataWizErrorCodes code = DataWizErrorCodes.OK;
        try {
            MinioResult res = minioUtil.deleteFile(fileDAO.findById(docId));
            if (res.equals(MinioResult.OK)) {
                fileDAO.deleteFile(docId);
            } else {
                log.warn("ERROR: During deleteMaterialfromMinioAndDB - MinioResult: {}", () -> res.name());
                code = DataWizErrorCodes.MINIO_SAVE_ERROR;
            }
        } catch (Exception e) {
            log.error("WARN: deleteDocument [id: {}] not successful because of DB Error - Message: {}", () -> docId, () -> e.getMessage());
            code = DataWizErrorCodes.DATABASE_ERROR;
        }
        return code;
    }

    /**
     * @param pid
     * @param user
     * @return
     */
    public void deleteProject(final Optional<Long> pid, final UserDTO user) throws DataWizSystemException {
        TransactionStatus status = txManager.getTransaction(new DefaultTransactionDefinition());
        if (!pid.isPresent()) {
            log.warn("ProjectId emtpy - project delete aborted");
            throw new DataWizSystemException(messageSource.getMessage("logging.pid.not.present", null, Locale.ENGLISH), DataWizErrorCodes.MISSING_PID_ERROR);
        }
        try {
            ProjectDTO project = projectDAO.findById(pid.get());
            if (user.hasRole(Roles.ADMIN) || user.hasRole(Roles.PROJECT_ADMIN, pid.get(), false)) {
                if (project != null) {
                    List<ContributorDTO> cContri = contributorDAO.findByProject(project, false, true);
                    List<StudyDTO> studies = studyDAO.findAllStudiesByProjectId(project);
                    if (studies != null && !studies.isEmpty()) {
                        for (StudyDTO study : studies) {
                            studyService.deleteStudy(pid.orElse(0L), study.getId(), user, false);
                        }
                    }
                    projectDAO.deleteProject(project.getId());
                    if (cContri != null && !cContri.isEmpty())
                        for (ContributorDTO contri : cContri) {
                            contributorDAO.deleteContributor(contri);
                        }
                    txManager.commit(status);
                } else {
                    log.warn("No Project found for PID {}", () -> pid.get());
                    throw new DataWizSystemException(messageSource.getMessage("logging.project.not.found", new Object[]{pid.get()}, Locale.ENGLISH),
                            DataWizErrorCodes.PROJECT_NOT_AVAILABLE);
                }
            } else {
                log.warn("User [email:{}; id: {}] tried to delete Project [projectId: {}]", () -> user.getEmail(), () -> user.getId(), () -> pid.get());
                throw new DataWizSystemException(
                        messageSource.getMessage("logging.user.permitted", new Object[]{user.getEmail(), "project", pid.get()}, Locale.ENGLISH),
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

    public List<ProjectDTO> getAdminProjectList(final UserDTO user) throws Exception {
        return projectDAO.findAllByAdminRole(user.getId());
    }
}
