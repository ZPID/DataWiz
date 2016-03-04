package de.zpid.datawiz.controller;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
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

import de.zpid.datawiz.dao.ContributorDAO;
import de.zpid.datawiz.dao.FileDAO;
import de.zpid.datawiz.dao.FormTypesDAO;
import de.zpid.datawiz.dao.ProjectDAO;
import de.zpid.datawiz.dao.RoleDAO;
import de.zpid.datawiz.dao.StudyDAO;
import de.zpid.datawiz.dao.TagDAO;
import de.zpid.datawiz.dto.ContributorDTO;
import de.zpid.datawiz.dto.FileDTO;
import de.zpid.datawiz.dto.ProjectDTO;
import de.zpid.datawiz.dto.UserDTO;
import de.zpid.datawiz.enumeration.DelType;
import de.zpid.datawiz.enumeration.Roles;
import de.zpid.datawiz.enumeration.SavedState;
import de.zpid.datawiz.exceptions.DataWizException;
import de.zpid.datawiz.exceptions.DataWizSecurityException;
import de.zpid.datawiz.form.ProjectForm;
import de.zpid.datawiz.util.BreadCrumpUtil;
import de.zpid.datawiz.util.FileUtil;
import de.zpid.datawiz.util.UserUtil;

@Controller
@RequestMapping(value = "/project")
@SessionAttributes({ "ProjectForm", "subnaviActive" })
public class ProjectController {

  @Autowired
  private StudyDAO studyDAO;
  @Autowired
  private ProjectDAO projectDAO;
  @Autowired
  private TagDAO tagDAO;
  @Autowired
  private FileDAO fileDAO;
  @Autowired
  private ContributorDAO contributorDAO;
  @Autowired
  private RoleDAO roleDAO;
  @Autowired
  private MessageSource messageSource;
  @Autowired
  private SmartValidator validator;

  private static final Logger log = Logger.getLogger(ProjectController.class);
  private ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("spring.xml");

  /**
   * 
   * @return
   */
  @ModelAttribute("ProjectForm")
  public ProjectForm createProjectForm() {
    return (ProjectForm) context.getBean("ProjectForm");

  }

  /**
   * 
   * @param model
   * @return
   */
  @RequestMapping(method = RequestMethod.GET)
  public String createProject(ModelMap model) {
    if (log.isDebugEnabled()) {
      log.debug("execute createProject - GET");
    }
    model.put("breadcrumpList", BreadCrumpUtil.generateBC("project"));
    model.put("subnaviActive", "PROJECT");
    model.put("ProjectForm", createProjectForm());
    return "project";
  }

  /**
   * 
   * @param pid
   * @param pForm
   * @param model
   * @return
   */
  @RequestMapping(value = "/{pid}", method = RequestMethod.GET)
  public String editProject(@PathVariable String pid, @ModelAttribute("ProjectForm") ProjectForm pForm, ModelMap model,
      RedirectAttributes redirectAttributes) {
    if (log.isDebugEnabled()) {
      log.debug("execute editProject for projectID=" + pid);
    }
    UserDTO user = UserUtil.getCurrentUser();
    if (user == null) {
      log.warn("Auth User Object == null - redirect to login");
      return "redirect:/login";
    }
    // create new pform!
    try {
      pForm = getProjectForm(pForm, pid, user, this.projectDAO, this.contributorDAO, this.fileDAO, this.tagDAO,
          this.studyDAO, null, "PROJECT");
    } catch (Exception e) {
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
    model.put("breadcrumpList", BreadCrumpUtil.generateBC("project"));
    model.put("subnaviActive", "PROJECT");
    model.put("ProjectForm", pForm);
    return "project";
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
    if (saveOrUpdateProject(pForm, this.projectDAO, this.roleDAO)) {
      // TODO vernünftige Fehlerausgabe
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
    pForm.getContributors().add(0, (ContributorDTO) context.getBean("ContributorDTO"));
    // pForm.getContributors().add(new ContributorDTO());
    model.put("jQueryMap", "contri");
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
    model.put("jQueryMap", "contri");
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
      @ModelAttribute("ProjectForm") ProjectForm pForm) {
    if (log.isDebugEnabled()) {
      log.debug("execute uploadFile");
    }
    if (pForm == null || pForm.getProject() == null || pForm.getProject().getId() <= 0) {
      log.warn("");
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
        FileDTO file = (FileDTO) context.getBean("FileDTO");
        MultipartFile mpf = request.getFile(itr.next());
        if (mpf != null) {
          file.setMultipartFile(mpf);
          file.setMd5checksum(FileUtil.getFileChecksum(MessageDigest.getInstance("MD5"), file.getContent()));
          file.setSha1Checksum(FileUtil.getFileChecksum(MessageDigest.getInstance("SHA-1"), file.getContent()));
          // file.setSha256Checksum(FileUtil.getFileChecksum(MessageDigest.getInstance("SHA-256"),
          file.setProjectId(pForm.getProject().getId());
          file.setUserId(user.getId());
          file.setUploadDate(LocalDateTime.now());
          fileDAO.saveFile(file);
        }
      }
    } catch (Exception e) {
      log.warn("Exception during file upload: " + e);
      return new ResponseEntity<String>("{\"test\": \"test\"}", HttpStatus.INTERNAL_SERVER_ERROR);
    }
    return new ResponseEntity<String>("{}", HttpStatus.OK);
  }

  /**
   * 
   * @param pForm
   * @param model
   * @param redirectAttributes
   * @return
   */
  @RequestMapping(value = "/multisaved")
  public String multiSaved(@ModelAttribute("ProjectForm") ProjectForm pForm, ModelMap model,
      RedirectAttributes redirectAttributes) {
    if (log.isDebugEnabled()) {
      log.debug("execute multiSaved()");
    }
    redirectAttributes.addFlashAttribute("saveState", SavedState.SUCCESS.toString());
    redirectAttributes.addFlashAttribute("saveStateMsg", "Upload passt!");
    redirectAttributes.addFlashAttribute("jQueryMap", "material");
    return "redirect:/project/" + pForm.getProject().getId();
  }

  /**
   * 
   * @param docId
   * @param response
   * @param redirectAttributes
   * @return
   */
  @RequestMapping(value = { "/{pid}/download/{docId}" }, method = RequestMethod.GET)
  public String downloadDocument(@PathVariable int pid, @PathVariable int docId, HttpServletResponse response,
      RedirectAttributes redirectAttributes) {
    if (log.isDebugEnabled()) {
      log.debug("execute downloadDocument id=" + docId);
    }
    UserDTO user = UserUtil.getCurrentUser();
    if (user == null) {
      log.warn("Auth User Object == null - redirect to login");
      return "redirect:/login";
    }
    FileDTO file = null;
    try {
      file = fileDAO.findById(docId);
      response.setContentType(file.getContentType());
      response.setContentLength(file.getContent().length);
      response.setHeader("Content-Disposition", "attachment; filename=\"" + file.getFileName() + "\"");
      FileCopyUtils.copy(file.getContent(), response.getOutputStream());
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    redirectAttributes.addFlashAttribute("saveState", SavedState.ERROR.toString());
    redirectAttributes.addFlashAttribute("saveStateMsg", "Downloaderror");
    redirectAttributes.addFlashAttribute("jQueryMap", "material");
    return "redirect:/project/" + pid;
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
  public String deleteDocument(@PathVariable int pid, @PathVariable int docId, HttpServletResponse response,
      RedirectAttributes redirectAttributes) {
    if (log.isDebugEnabled()) {
      log.debug("execute deleteDocument id=" + docId);
    }
    UserDTO user = UserUtil.getCurrentUser();
    if (user == null) {
      log.warn("Auth User Object == null - redirect to login");
      return "redirect:/login";
    }
    try {
      fileDAO.deleteFile(docId);
    } catch (Exception e) {
      // TODO: handle exception
    }
    redirectAttributes.addFlashAttribute("saveState", SavedState.ERROR.toString());
    redirectAttributes.addFlashAttribute("saveStateMsg", "DELTETE");
    redirectAttributes.addFlashAttribute("jQueryMap", "material");
    return "redirect:/project/" + pid;
  }

  /**
   * 
   * @param pid
   * @param imgId
   * @param response
   */
  @RequestMapping(value = { "/{pid}/img/{imgId}" }, method = RequestMethod.GET)
  private void setThumbnailImage(@PathVariable int pid, @PathVariable int imgId, HttpServletResponse response) {
    FileDTO file;
    final int thumbHeight = 98;
    final int maxWidth = 160;
    try {
      file = fileDAO.findById(imgId);
      if (file.getContentType().substring(0, 5).equalsIgnoreCase("image") && file.getContent() != null) {
        OutputStream sos = response.getOutputStream();
        InputStream in = new ByteArrayInputStream(file.getContent());
        BufferedImage bImage = ImageIO.read(in);
        int scale = bImage.getHeight() / thumbHeight;
        BufferedImage bf = FileUtil.scaleImage(bImage,
            (bImage.getWidth() / scale > maxWidth) ? maxWidth : bImage.getWidth() / scale, thumbHeight);
        response.setContentType(file.getContentType());
        ImageIO.write(bf, "jpg", sos);
        sos.flush();
        sos.close();
      }
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  public static boolean saveOrUpdateProject(ProjectForm pForm, ProjectDAO projectDAO, RoleDAO roleDAO) {
    boolean error = false;
    try {
      UserDTO user = UserUtil.getCurrentUser();
      if (pForm != null && pForm.getProject() != null && user != null) {
        if (pForm.getProject().getId() <= 0) {
          int chk = projectDAO.saveProject(pForm.getProject());
          if (chk > 0) {
            roleDAO.setRole(user.getId(), chk, Roles.PROJECT_ADMIN.toInt());
            pForm.getProject().setId(chk);
          } else {
            error = true;
          }
        } else {
          projectDAO.updateProject(pForm.getProject());
        }
      } else {
        error = true;
      }
      UserUtil.getCurrentUser().setGlobalRoles(roleDAO.getRolesByUserID(user.getId()));
    } catch (Exception e) {
      log.error("Project saving not sucessful error:" + e.getMessage());
      error = true;
    }
    return error;
  }

  /**
   * 
   * @param pid
   * @param user
   * @return
   * @throws Exception
   */
  public static ProjectForm getProjectForm(ProjectForm pForm, String pid, UserDTO user, ProjectDAO projectDAO,
      ContributorDAO contributorDAO, FileDAO fileDAO, TagDAO tagDAO, StudyDAO studyDAO, FormTypesDAO dmpRelTypeDAO,
      String call) throws Exception {
    if (log.isDebugEnabled()) {
      log.debug("execute getProjectData");
    }
    // 1st - security access check!
    if (pid != null && !pid.isEmpty() && user != null) {
      if (!user.hasRole(Roles.ADMIN) && !user.hasProjectRole(Roles.PROJECT_READER, pid)
          && !user.hasProjectRole(Roles.PROJECT_ADMIN, pid) && !user.hasProjectRole(Roles.PROJECT_WRITER, pid)) {
        throw new DataWizSecurityException("SECURITY: User with email: " + user.getEmail()
            + " tries to get access to project:" + pid + " without having the permissions to read");
      }
      ProjectDTO pdto = projectDAO.findByIdWithRole(pid, String.valueOf(user.getId()));
      if (pdto == null || pdto.getId() <= 0 || pdto.getProjectRole() == null
          || pdto.getProjectRole().getUserId() <= 0) {
        throw new DataWizException(
            "Project or project_role is empty for user=" + user.getEmail() + " and project=" + pid);
      }
      // 2nd - security access check!
      if (user.getId() != pdto.getProjectRole().getUserId()) {
        throw new DataWizSecurityException("SECURITY: User with email: " + user.getEmail()
            + " tries to get access to project:" + pdto.getId() + " without having permissions to read");
      }
      pForm.setProject(pdto);      
      if (call == null || call.isEmpty() || call.equals("PROJECT")) {
        pForm.setFiles(fileDAO.getProjectFiles(pdto));
        pForm.setTags(new ArrayList<String>(tagDAO.getTagsByProjectID(pdto).values()));
        pForm.setStudies(studyDAO.getAllStudiesByProjectId(pdto));
        pForm.setContributors(contributorDAO.getByProject(pdto, false, false));
        pForm.setPrimaryContributor(contributorDAO.findPrimaryContributorByProject(pdto));
      } else if (call.equals("DMP")) {
        pForm.setDataTypes(dmpRelTypeDAO.getAllByType(true, DelType.datatype));
        pForm.setCollectionModes(dmpRelTypeDAO.getAllByType(true, DelType.collectionmode));
        pForm.setMetaPurposes(dmpRelTypeDAO.getAllByType(true, DelType.metaporpose));
        pForm.setPrimaryContributor(contributorDAO.findPrimaryContributorByProject(pdto));
      } else if (call.equals("ACCESS")) {
      }
      return pForm;
    } else {
      log.warn("ProjectID or UserDTO is empty - NULL returned!");
      return null;
    }
  }

}
