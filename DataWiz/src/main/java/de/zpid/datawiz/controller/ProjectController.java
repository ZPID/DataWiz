package de.zpid.datawiz.controller;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.FileCopyUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.SmartValidator;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import de.zpid.datawiz.dto.ContributorDTO;
import de.zpid.datawiz.dto.FileDTO;
import de.zpid.datawiz.dto.ProjectDTO;
import de.zpid.datawiz.dto.UserDTO;
import de.zpid.datawiz.enumeration.DataWizErrorCodes;
import de.zpid.datawiz.enumeration.MinioResult;
import de.zpid.datawiz.enumeration.PageState;
import de.zpid.datawiz.enumeration.Roles;
import de.zpid.datawiz.enumeration.SavedState;
import de.zpid.datawiz.exceptions.DataWizException;
import de.zpid.datawiz.exceptions.DataWizSecurityException;
import de.zpid.datawiz.form.ProjectForm;
import de.zpid.datawiz.service.ExceptionService;
import de.zpid.datawiz.service.ProjectService;
import de.zpid.datawiz.service.StudyService;
import de.zpid.datawiz.util.BreadCrumpUtil;
import de.zpid.datawiz.util.UserUtil;

@Controller
@RequestMapping(value = "/project")
@SessionAttributes({ "ProjectForm", "subnaviActive" })
public class ProjectController extends SuperController {

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
  private ProjectService projectService;

  @ModelAttribute("ProjectForm")
  protected ProjectForm createProjectForm() {
    return (ProjectForm) applicationContext.getBean("ProjectForm");
  }

  private static Logger log = LogManager.getLogger(ProjectController.class);

  public ProjectController() {
    super();
    log.info("Loading ProjectController for mapping /project");
  }

  /**
   * 
   * @param pid
   * @param model
   * @param redirectAttributes
   * @return
   */
  @RequestMapping(value = { "", "/{pid}" }, method = RequestMethod.GET)
  public String showProjectPage(@PathVariable Optional<Long> pid, ModelMap model,
      RedirectAttributes redirectAttributes) {
    log.debug("execute showProjectPage for projectID= {}", () -> pid.isPresent() ? pid.get() : "null");
    UserDTO user = UserUtil.getCurrentUser();
    if (user == null) {
      log.warn("Auth User Object == null - redirect to login");
      return "redirect:/login";
    }
    ProjectForm pForm = createProjectForm();
    String name = messageSource.getMessage("breadcrumb.new.project", null, LocaleContextHolder.getLocale());
    if (pid.isPresent()) {
      try {
        Roles role = projectService.checkProjectRoles(user, pid.get(), 0, false, true);
        if ((!role.equals(Roles.ADMIN) && !role.equals(Roles.PROJECT_ADMIN) && !role.equals(Roles.PROJECT_READER)
            && !role.equals(Roles.PROJECT_WRITER)) && (role.equals(Roles.DS_READER) || role.equals(Roles.DS_WRITER))) {
          redirectAttributes.addFlashAttribute("hideMenu", true);
          return "redirect:/project/" + pid.get() + "/studies";
        }
        projectService.getProjectForm(pForm, pid.get(), user, PageState.PROJECT, role);
        name = pForm.getProject().getTitle();
      } catch (Exception e) {
        log.warn("Exception during projectService.getProjectForm for [user: {}, pid: {}]", () -> user.getId(),
            () -> pid, () -> e);
        String redirectMessage = "";
        if (e instanceof DataWizException) {
          redirectMessage = "project.not.available";
        } else if (e instanceof DataWizSecurityException) {
          redirectMessage = "project.access.denied";
        } else {
          redirectMessage = "dbs.sql.exception";
        }
        redirectAttributes.addFlashAttribute("errorMSG",
            messageSource.getMessage(redirectMessage, null, LocaleContextHolder.getLocale()));
        return "redirect:/panel";
      }
    } else {
      List<ContributorDTO> cContri = new ArrayList<>();
      cContri.add(new ContributorDTO());
      pForm.setContributors(cContri);
    }
    model.put("breadcrumpList",
        BreadCrumpUtil.generateBC(PageState.PROJECT, new String[] { name }, null, messageSource));
    model.put("subnaviActive", PageState.PROJECT.name());
    model.put("ProjectForm", pForm);
    return "project";
  }

  /**
   * 
   * @param pid
   * @param model
   * @param redirectAttributes
   * @return
   */
  @RequestMapping(value = { "/{pid}/studies" }, method = RequestMethod.GET)
  public String showStudiesPage(@PathVariable Optional<Long> pid, ModelMap model,
      RedirectAttributes redirectAttributes) {
    log.debug("execute showStudiesPage for projectID= {}", () -> pid.isPresent() ? pid.get() : "null");
    UserDTO user = UserUtil.getCurrentUser();
    if (user == null) {
      log.warn("Auth User Object == null - redirect to login");
      return "redirect:/login";
    }
    ProjectForm pForm = createProjectForm();
    if (!pid.isPresent()) {
      redirectAttributes.addFlashAttribute("errorMSG",
          messageSource.getMessage("project.access.denied", null, LocaleContextHolder.getLocale()));
      return "redirect:/panel";
    }
    try {
      Roles role = projectService.checkProjectRoles(user, pid.get(), 0, false, true);
      if ((!role.equals(Roles.ADMIN) && !role.equals(Roles.PROJECT_ADMIN) && !role.equals(Roles.PROJECT_READER)
          && !role.equals(Roles.PROJECT_WRITER)) && (role.equals(Roles.DS_READER) || role.equals(Roles.DS_WRITER))) {
        model.put("hideMenu", true);
      }
      projectService.getProjectForm(pForm, pid.get(), user, PageState.STUDIES, role);
    } catch (Exception e) {
      // TODO
      log.warn(e.getMessage());
      String redirectMessage = "";
      if (e instanceof DataWizException) {
        redirectMessage = "project.not.available";
      } else if (e instanceof DataWizSecurityException) {
        redirectMessage = "project.access.denied";
      } else {
        redirectMessage = "dbs.sql.exception";
      }
      redirectAttributes.addFlashAttribute("errorMSG",
          messageSource.getMessage(redirectMessage, null, LocaleContextHolder.getLocale()));
      return "redirect:/panel";
    }
    model.put("breadcrumpList", BreadCrumpUtil.generateBC(PageState.PROJECT,
        new String[] { pForm.getProject().getTitle() }, null, messageSource));
    model.put("subnaviActive", PageState.STUDIES.name());
    model.put("ProjectForm", pForm);
    return "studies";
  }

  /**
   * 
   * @param pid
   * @param studyId
   * @param model
   * @param redirectAttributes
   * @return
   */
  @RequestMapping(value = { "/{pid}/material", "/{pid}/study/{studyId}/material" }, method = RequestMethod.GET)
  public String showMaterialPage(@PathVariable Optional<Long> pid, @PathVariable Optional<Long> studyId, ModelMap model,
      RedirectAttributes redirectAttributes) {
    log.debug("execute showMaterialPage for projectID= {}", () -> pid.isPresent() ? pid.get() : "null");
    UserDTO user = UserUtil.getCurrentUser();
    String ret = null;
    if (user == null) {
      log.warn("Auth User Object == null - redirect to login");
      ret = "redirect:/login";
    } else {
      if (!pid.isPresent()) {
        redirectAttributes.addFlashAttribute("errorMSG",
            messageSource.getMessage("project.access.denied", null, LocaleContextHolder.getLocale()));
        ret = "redirect:/panel";
      }
      ret = projectService.checkUserAccess(pid, studyId, redirectAttributes, false, user);
    }
    ProjectForm pForm = createProjectForm();
    if (ret == null) {
      if (projectService.setMaterialForm(pid, studyId, model, redirectAttributes, user, pForm)) {
        ret = "material";
        model.put("studyId", studyId.isPresent() ? studyId.get() : -1);
        model.put("projectId", pid.get());
        model.put("ProjectForm", pForm);
        model.put("subnaviActive", PageState.MATERIAL.name());
      } else {
        ret = "redirect:/panel";
      }
    }
    return ret;
  }

  /**
   * TODO SAVE FUNKTION!!!!!
   * 
   * @param pForm
   * @param bindingResult
   * @param model
   * @return
   */
  @RequestMapping(value = { "", "/{pid}" }, method = RequestMethod.POST)
  public String saveProject(@ModelAttribute("ProjectForm") ProjectForm pForm, BindingResult bindingResult,
      ModelMap model, RedirectAttributes redirectAttributes) {
    log.trace("Entering saveProject");
    validator.validate(pForm, bindingResult, ProjectDTO.ProjectVal.class);
    validator.validate(pForm, bindingResult);
    List<ContributorDTO> cContri = projectService.validateContributors(pForm, bindingResult);
    if (bindingResult.hasErrors()) {
      if (log.isInfoEnabled()) {
        log.info("bindingResult has Errors " + bindingResult.getAllErrors().toString());
      }
      model.put("errorMSG",
          messageSource.getMessage("error.project.validation", null, LocaleContextHolder.getLocale()));
      return "project";
    }
    pForm.setContributors(cContri);
    DataWizErrorCodes state = projectService.saveOrUpdateProject(pForm);
    log.debug("Leaving saveProject with result: [code: {}]", () -> state.name());
    if (!state.equals(DataWizErrorCodes.OK)) {
      model.put("errorMSG",
          messageSource.getMessage("project.save.error." + state.name(), null, LocaleContextHolder.getLocale()));
      return "project";
    } else {
      redirectAttributes.addFlashAttribute("successMSG",
          messageSource.getMessage("project.save.error." + state.name(), null, LocaleContextHolder.getLocale()));
      return "redirect:/project/" + pForm.getProject().getId();
    }
  }

  /**
   * Adds a new Contributor Object to the contributor list
   * 
   * @param pForm
   * @return
   */
  @RequestMapping(value = { "", "/{pid}" }, params = { "addContributor" }, method = RequestMethod.POST)
  public String addContributor(@ModelAttribute("ProjectForm") ProjectForm pForm, ModelMap model) {
    log.trace("execute addContributor");
    if (pForm.getContributors() == null) {
      pForm.setContributors(new ArrayList<ContributorDTO>());
    }
    pForm.getContributors().add(0, (ContributorDTO) applicationContext.getBean("ContributorDTO"));
    return "project";
  }

  /**
   * 
   * @param pForm
   * @param model
   * @return
   */
  @RequestMapping(value = { "", "/{pid}" }, params = { "deleteContributor" }, method = RequestMethod.POST)
  public String deleteContributor(@ModelAttribute("ProjectForm") ProjectForm pForm, ModelMap model) {
    log.trace("execute deleteContributor");
    if (pForm.getContributors() == null) {
      pForm.setContributors(new ArrayList<ContributorDTO>());
    }
    ContributorDTO selected = pForm.getContributors().get(pForm.getDelPos());
    pForm.getContributors().remove(pForm.getDelPos());
    try {
      contributorDAO.deleteContributor(selected);
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return "project";
  }

  /**
   * Multidata Upload
   * 
   * @param request
   * @param pForm
   * @return
   */
  @RequestMapping(value = { "/{pid}/upload", "/{pid}/study/{studyId}/upload" }, method = RequestMethod.POST)
  public @ResponseBody ResponseEntity<String> uploadFile(MultipartHttpServletRequest request,
      @ModelAttribute("ProjectForm") ProjectForm pForm, @PathVariable Optional<Long> pid,
      @PathVariable Optional<Long> studyId) {
    log.trace("Entering uploadFile for Project [id: {}]", () -> pid);
    if (!pid.isPresent()) {
      log.warn("ProjectForm is empty or missing values");
      // TOTO ERROR STRINGS!!!!
      return new ResponseEntity<String>("{\"error\" : \""
          + messageSource.getMessage("project.not.available", null, LocaleContextHolder.getLocale()) + "\"}",
          HttpStatus.INTERNAL_SERVER_ERROR);
    }
    UserDTO user = UserUtil.getCurrentUser();
    if (user == null) {
      log.warn("Auth User Object == null - redirect to login");
      return new ResponseEntity<String>("{\"error\" : \""
          + messageSource.getMessage("project.not.available", null, LocaleContextHolder.getLocale()) + "\"}",
          HttpStatus.INTERNAL_SERVER_ERROR);
    }
    // TODO LESESCHREIBRECHTE!!!!!!
    FileDTO file = null;
    try {
      Iterator<String> itr = request.getFileNames();
      while (itr.hasNext()) {
        String filename = itr.next();
        log.debug("Saving file [name: {}]", () -> filename);
        final MultipartFile mpf = request.getFile(filename);
        if (mpf != null) {
          file = fileUtil.buildFileDTO(pid.get(), studyId.isPresent() ? studyId.get() : 0, 0, 0, user.getId(), mpf);
          // String filePath = fileUtil.saveFile(file);
          MinioResult res = minioUtil.putFile(file);
          if (res.equals(MinioResult.OK)) {
            fileDAO.saveFile(file);
          } else if (res.equals(MinioResult.CONNECTION_ERROR)) {
            log.error("ERROR: No Connection to Minio Server - please check Settings or Server");
            return new ResponseEntity<String>("{\"error\" : \""
                + messageSource.getMessage("minio.connection.exception.upload", null, LocaleContextHolder.getLocale())
                + "\"}", HttpStatus.INTERNAL_SERVER_ERROR);
          } else {
            log.error("ERROR: During Saving File - MinioResult:", () -> res.name());
            return new ResponseEntity<String>("{\"error\" : \""
                + messageSource.getMessage("minio.connection.exception.upload", null, LocaleContextHolder.getLocale())
                + "\"}", HttpStatus.INTERNAL_SERVER_ERROR);
          }
        }
      }
    } catch (Exception e) {
      if (file != null && minioUtil.getFile(file).equals(MinioResult.OK)) {
        minioUtil.deleteFile(file);
      }

      log.warn("Exception during file upload: ", () -> e);
      return new ResponseEntity<String>("{\"error\" : \""
          + messageSource.getMessage("dbs.sql.exception", null, LocaleContextHolder.getLocale()) + "\"}",
          HttpStatus.CONFLICT);
    }

    log.trace("Method uploadFile successfully completed");
    return new ResponseEntity<String>("", HttpStatus.OK);
  }

  /**
   * 
   * @param pForm
   * @param model
   * @param redirectAttributes
   * @return
   */
  @RequestMapping(value = { "/{pid}/multisaved", "/{pid}/study/{studyId}/multisaved" })
  public String multiSaved(@ModelAttribute("ProjectForm") ProjectForm pForm, @PathVariable Optional<Long> pid,
      @PathVariable Optional<Long> studyId, RedirectAttributes redirectAttributes, ModelMap model) {
    if (log.isDebugEnabled()) {
      log.debug("execute multiSaved()");
    }
    redirectAttributes.addFlashAttribute("saveState", SavedState.SUCCESS.toString());
    redirectAttributes.addFlashAttribute("saveStateMsg",
        messageSource.getMessage("material.upload.successful", null, LocaleContextHolder.getLocale()));
    if (studyId.isPresent()) {
      return "redirect:/project/" + pid.get() + "/study/" + studyId.get() + "/material";
    } else {
      redirectAttributes.addFlashAttribute("jQueryMap", "material");
      return "redirect:/project/" + pid.get() + "/material";
    }
  }

  /**
   * 
   * @param docId
   * @param response
   * @param redirectAttributes
   * @return
   */
  @RequestMapping(value = { "/{pid}/download/{docId}",
      "/{pid}/study/{studyId}/download/{docId}" }, method = RequestMethod.GET)
  public @ResponseBody ResponseEntity<String> downloadDocument(@PathVariable long pid, @PathVariable long docId,
      HttpServletResponse response, RedirectAttributes redirectAttributes) {
    if (log.isDebugEnabled()) {
      log.debug("execute downloadDocument id=" + docId);
    }
    UserDTO user = UserUtil.getCurrentUser();
    ResponseEntity<String> resp = new ResponseEntity<String>("{}", HttpStatus.OK);
    if (user == null) {
      log.warn("Auth User Object == null - redirect to login");
      resp = new ResponseEntity<String>("{}", HttpStatus.UNAUTHORIZED);
    }
    FileDTO file = null;
    try {
      file = fileDAO.findById(docId);
      // fileUtil.setFileBytes(file);
      if (minioUtil.getFile(file).equals(MinioResult.OK)) {
        response.setContentType(file.getContentType());
        response.setHeader("Content-Disposition", "attachment; filename=\"" + file.getFileName() + "\"");
        response.setContentLength(file.getContent().length);
        FileCopyUtils.copy(file.getContent(), response.getOutputStream());
      } else {
        resp = new ResponseEntity<String>("Fehler und so", HttpStatus.NOT_FOUND);
      }
    } catch (Exception e) {
      resp = new ResponseEntity<String>("{}", HttpStatus.CONFLICT);
      // TODO
      e.printStackTrace();
    }
    return resp;
  }

  /**
   * 
   * @param pid
   * @param docId
   * @param response
   * @param redirectAttributes
   * @return
   */
  @RequestMapping(value = { "/{pid}/delDoc/{docId}",
      "/{pid}/study/{studyId}/delDoc/{docId}" }, method = RequestMethod.GET)
  public String deleteDocument(@PathVariable long pid, @PathVariable long docId, HttpServletResponse response,
      @PathVariable Optional<Long> studyId, RedirectAttributes redirectAttributes) {
    log.trace("Entering deleteDocument [id: {}]", () -> docId);
    UserDTO user = UserUtil.getCurrentUser();
    if (user == null) {
      log.warn("Auth User Object == null - redirect to login");
      return "redirect:/login";
    }
    SavedState state = SavedState.ERROR;
    String msg = "material.delete.successful";
    try {
      if (minioUtil.deleteFile(fileDAO.findById(docId)).equals(MinioResult.OK)) {
        fileDAO.deleteFile(docId);
        state = SavedState.SUCCESS;
      } else {
        msg = "material.delete.minio.error";
      }
    } catch (Exception e) {
      msg = "material.delete.db.error";
      log.error("WARN: deleteDocument [id: {}] not successful because of DB Error - Message: {}", () -> docId,
          () -> e.getMessage());
    }
    redirectAttributes.addFlashAttribute("saveState", state.name());
    redirectAttributes.addFlashAttribute("saveStateMsg",
        messageSource.getMessage(msg, null, LocaleContextHolder.getLocale()));
    redirectAttributes.addFlashAttribute("jQueryMap", "material");
    if (studyId.isPresent())
      return "redirect:/project/" + pid + "/study/" + studyId.get() + "/material";
    return "redirect:/project/" + pid + "/material";
  }

  /**
   * 
   * @param pid
   * @param imgId
   * @param response
   */
  @RequestMapping(value = { "/{pid}/img/{imgId}", "/{pid}/study/{studyId}/img/{imgId}" }, method = RequestMethod.GET)
  private void setThumbnailImage(@PathVariable long pid, @PathVariable long imgId, HttpServletResponse response) {
    final int thumbHeight = 98;
    final int maxWidth = 160;
    try {
      projectService.scaleAndSetThumbnail(imgId, response, thumbHeight, maxWidth);
    } catch (Exception e) {
      log.warn("Warn: setThumbnailImage throws an exception: ", () -> e);
    }
  }

}
