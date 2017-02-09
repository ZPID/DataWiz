package de.zpid.datawiz.controller;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Optional;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.FileCopyUtils;
import org.springframework.validation.BindingResult;
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
import de.zpid.datawiz.enumeration.MinioResult;
import de.zpid.datawiz.enumeration.PageState;
import de.zpid.datawiz.enumeration.Roles;
import de.zpid.datawiz.enumeration.SavedState;
import de.zpid.datawiz.exceptions.DataWizException;
import de.zpid.datawiz.exceptions.DataWizSecurityException;
import de.zpid.datawiz.form.ProjectForm;
import de.zpid.datawiz.util.BreadCrumpUtil;
import de.zpid.datawiz.util.UserUtil;

@Controller
@RequestMapping(value = "/project")
@SessionAttributes({ "ProjectForm", "subnaviActive" })
public class ProjectController extends SuperController {

  private static Logger log = LogManager.getLogger(ProjectController.class);

  public ProjectController() {
    super();
    log.info("Loading ProjectController for mapping /project");
  }

  /**
   * 
   * @param pid
   * @param pForm
   * @param model
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

    // create new pform!
    ProjectForm pForm = createProjectForm();
    // TODO
    String name = messageSource.getMessage("breadcrumb.new.project", null, LocaleContextHolder.getLocale());
    if (pid.isPresent()) {
      try {
        Roles role = pUtil.checkProjectRoles(user, pid.get(), 0, false, true);
        if ((!role.equals(Roles.ADMIN) && !role.equals(Roles.PROJECT_ADMIN) && !role.equals(Roles.PROJECT_READER)
            && !role.equals(Roles.PROJECT_WRITER)) && (role.equals(Roles.DS_READER) || role.equals(Roles.DS_WRITER))) {
          redirectAttributes.addFlashAttribute("hideMenu", true);
          return "redirect:/project/" + pid.get() + "/studies";
        }
        pUtil.getProjectForm(pForm, pid.get(), user, PageState.PROJECT, role);
        name = pForm.getProject().getTitle();
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
    }
    model.put("breadcrumpList",
        BreadCrumpUtil.generateBC(PageState.PROJECT, new String[] { name }, null, messageSource));
    model.put("subnaviActive", PageState.PROJECT.name());
    model.put("ProjectForm", pForm);
    return "project";
  }

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
      Roles role = pUtil.checkProjectRoles(user, pid.get(), 0, false, true);
      if ((!role.equals(Roles.ADMIN) && !role.equals(Roles.PROJECT_ADMIN) && !role.equals(Roles.PROJECT_READER)
          && !role.equals(Roles.PROJECT_WRITER)) && (role.equals(Roles.DS_READER) || role.equals(Roles.DS_WRITER))) {
        model.put("hideMenu", true);
      }
      pUtil.getProjectForm(pForm, pid.get(), user, PageState.STUDIES, role);
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

  @RequestMapping(value = { "/{pid}/material" }, method = RequestMethod.GET)
  public String showMaterialPage(@PathVariable Optional<Long> pid, ModelMap model,
      RedirectAttributes redirectAttributes) {
    log.debug("execute showMaterialPage for projectID= {}", () -> pid.isPresent() ? pid.get() : "null");
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
      Roles role = pUtil.checkProjectRoles(user, pid.get(), 0, false, true);
      pUtil.getProjectForm(pForm, pid.get(), user, PageState.MATERIAL, role);
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
    model.put("subnaviActive", PageState.MATERIAL.name());
    model.put("ProjectForm", pForm);
    return "material";
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
    if (log.isDebugEnabled()) {
      log.debug("execute saveProject()");
    }
    validator.validate(pForm, bindingResult, ProjectDTO.ProjectVal.class);
    if (bindingResult.hasErrors()) {
      if (log.isInfoEnabled()) {
        log.info("bindingResult has Errors " + bindingResult.getAllErrors().toString());
      }
      return "project";
    }
    if (!pUtil.saveOrUpdateProject(pForm)) {
      // TODO vern�nftige Fehlerausgabe
      model.put("saveState", SavedState.ERROR.toString());
      model.put("saveStateMsg", "fehler!!!!");
      return "project";
    } else {
      redirectAttributes.addFlashAttribute("saveState", SavedState.SUCCESS);
      redirectAttributes.addFlashAttribute("saveStateMsg", "erfolgreich!!!");
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
    if (log.isDebugEnabled()) {
      log.debug("execute addContributor");
    }
    if (pForm.getContributors() == null) {
      pForm.setContributors(new ArrayList<ContributorDTO>());
    }
    pForm.getContributors().add(0, (ContributorDTO) applicationContext.getBean("ContributorDTO"));
    // pForm.getContributors().add(new ContributorDTO());
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
    if (log.isDebugEnabled()) {
      log.debug("execute deleteContributor");
    }
    if (pForm.getContributors() == null) {
      pForm.setContributors(new ArrayList<ContributorDTO>());
    }
    ContributorDTO selected = pForm.getContributors().get(pForm.getDelPos());
    pForm.getContributors().remove(pForm.getDelPos());
    // TODO DELETE FUNCTION
    return "project";
  }

  /**
   * Multidata Upload
   * 
   * @param request
   * @param pForm
   * @return
   */
  @RequestMapping(value = { "/{pid}/upload" }, method = RequestMethod.POST)
  public @ResponseBody ResponseEntity<String> uploadFile(MultipartHttpServletRequest request,
      @ModelAttribute("ProjectForm") ProjectForm pForm, @PathVariable long pid) {
    log.trace("Entering uploadFile for Project [id: {}]", () -> pid);
    if (pForm == null || pForm.getProject() == null || pForm.getProject().getId() <= 0) {
      log.warn("ProjectForm is empty or missing values");
      return new ResponseEntity<String>("{}", HttpStatus.INTERNAL_SERVER_ERROR);
    }
    UserDTO user = UserUtil.getCurrentUser();
    if (user == null) {
      log.warn("Auth User Object == null - redirect to login");
      return new ResponseEntity<String>("{}", HttpStatus.INTERNAL_SERVER_ERROR);
    }
    try {
      Iterator<String> itr = request.getFileNames();
      while (itr.hasNext()) {
        String filename = itr.next();
        log.debug("Saving file [name: {}]", () -> filename);
        final MultipartFile mpf = request.getFile(filename);
        if (mpf != null) {
          FileDTO file = fileUtil.buildFileDTO(pid, 0, 0, 0, user.getId(), mpf);
          // String filePath = fileUtil.saveFile(file);
          MinioResult res = minioUtil.putFile(file);
          if (res.equals(MinioResult.OK)) {
            fileDAO.saveFile(file);
          } else if (res.equals(MinioResult.CONNECTION_ERROR)) {
            log.error("ERROR: No Connection to Minio Server - please check Settings or Server");
            return new ResponseEntity<String>("{\"error\" : \""
                + messageSource.getMessage("minio.connection.exception", null, LocaleContextHolder.getLocale()) + "\"}",
                HttpStatus.INTERNAL_SERVER_ERROR);
          } else {
            log.error("ERROR: During Saving File - MinioResult:", () -> res.name());
            return new ResponseEntity<String>("{\"error\" : \"12\"}", HttpStatus.INTERNAL_SERVER_ERROR);
          }
        }
      }
    } catch (Exception e) {
      // TODO delete file
      log.warn("Exception during file upload: ", () -> e);
      return new ResponseEntity<String>("{}", HttpStatus.INTERNAL_SERVER_ERROR);
    }
    log.trace("Method uploadFile successfully completed");
    return new ResponseEntity<String>("{}", HttpStatus.OK);
  }

  /**
   * 
   * @param pForm
   * @param model
   * @param redirectAttributes
   * @return
   */
  @RequestMapping(value = { "/{pid}/multisaved" })
  public String multiSaved(@ModelAttribute("ProjectForm") ProjectForm pForm, ModelMap model,
      RedirectAttributes redirectAttributes) {
    if (log.isDebugEnabled()) {
      log.debug("execute multiSaved()");
    }
    redirectAttributes.addFlashAttribute("saveState", SavedState.SUCCESS.toString());
    redirectAttributes.addFlashAttribute("saveStateMsg", "Upload passt!");
    redirectAttributes.addFlashAttribute("jQueryMap", "material");
    return "redirect:/project/" + pForm.getProject().getId() + "/material";
  }

  /**
   * 
   * @param docId
   * @param response
   * @param redirectAttributes
   * @return
   */
  @RequestMapping(value = { "/{pid}/download/{docId}" }, method = RequestMethod.GET)
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
  @RequestMapping(value = { "/{pid}/delDoc/{docId}" }, method = RequestMethod.GET)
  public String deleteDocument(@PathVariable long pid, @PathVariable long docId, HttpServletResponse response,
      RedirectAttributes redirectAttributes) {
    log.trace("Entering deleteDocument [id: {}]", () -> docId);
    UserDTO user = UserUtil.getCurrentUser();
    if (user == null) {
      log.warn("Auth User Object == null - redirect to login");
      return "redirect:/login";
    }
    SavedState state = SavedState.ERROR;
    String msg = "Nicht gelöscht";
    try {
      if (minioUtil.deleteFile(fileDAO.findById(docId)).equals(MinioResult.OK)) {
        fileDAO.deleteFile(docId);
        state = SavedState.SUCCESS;
        msg = "Erfolgreich gelöscht";
        log.trace("Method deleteDocument [id: {}] successfully completed", () -> docId);
      }
    } catch (Exception e) {
      log.warn("WARN: deleteDocument [id: {}] not successful because of DB Error - Message: {}", () -> docId, () -> e);
    }
    redirectAttributes.addFlashAttribute("saveState", state.name());
    redirectAttributes.addFlashAttribute("saveStateMsg", msg);
    redirectAttributes.addFlashAttribute("jQueryMap", "material");
    return "redirect:/project/" + pid + "/material";
  }

  /**
   * 
   * @param pid
   * @param imgId
   * @param response
   */
  @RequestMapping(value = { "/{pid}/img/{imgId}" }, method = RequestMethod.GET)
  private void setThumbnailImage(@PathVariable long pid, @PathVariable long imgId, HttpServletResponse response) {
    FileDTO file;
    final int thumbHeight = 98;
    final int maxWidth = 160;
    try {
      file = fileDAO.findById(imgId);
      // fileUtil.setFileBytes(file);
      if (minioUtil.getFile(file).equals(MinioResult.OK)) {
        if (file.getContentType().toLowerCase().contains("image") && file.getContent() != null
            && !file.getContentType().toLowerCase().contains("icon")) {
          OutputStream sos = response.getOutputStream();
          BufferedImage bImage = ImageIO.read(new ByteArrayInputStream(file.getContent()));
          int scale = bImage.getHeight() / thumbHeight;
          BufferedImage bf = fileUtil.scaleImage(bImage,
              (bImage.getWidth() / scale > maxWidth) ? maxWidth : bImage.getWidth() / scale, thumbHeight);
          response.setContentType(file.getContentType());
          ImageIO.write(bf, "jpg", sos);
          sos.flush();
          sos.close();
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
