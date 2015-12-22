package de.zpid.datawiz.controller;

import java.util.ArrayList;

import javax.validation.Valid;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import de.zpid.datawiz.dao.ContributorDAO;
import de.zpid.datawiz.dao.ProjectDAO;
import de.zpid.datawiz.dao.TagDAO;
import de.zpid.datawiz.dto.ContributorDTO;
import de.zpid.datawiz.dto.ProjectDTO;
import de.zpid.datawiz.dto.UserDTO;
import de.zpid.datawiz.exceptions.DataWizException;
import de.zpid.datawiz.exceptions.DataWizSecurityException;
import de.zpid.datawiz.form.ProjectForm;
import de.zpid.datawiz.util.Roles;
import de.zpid.datawiz.util.SavedState;
import de.zpid.datawiz.util.UserUtil;

@Controller
@RequestMapping(value = "/project")
@SessionAttributes({ "ProjectForm" })
public class ProjectController {

  @Autowired
  private ProjectDAO projectDAO;
  @Autowired
  private TagDAO tagDAO;
  @Autowired
  private ContributorDAO contributorDAO;
  @Autowired
  private MessageSource messageSource;

  private static final Logger log = Logger.getLogger(ProjectController.class);
  private ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("spring.xml");

  @ModelAttribute("ProjectForm")
  public ProjectForm createProjectForm() {
    return (ProjectForm) context.getBean("ProjectForm");
  }

  @RequestMapping(method = RequestMethod.GET)
  public String createProject(ModelMap model) {
    if (log.isDebugEnabled()) {
      log.debug("execute createProject - GET");
    }
    model.put("ProjectForm", createProjectForm());
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
  @RequestMapping(value = "/{pid}", method = RequestMethod.POST)
  public String saveProject(@Valid @ModelAttribute("ProjectForm") ProjectForm pForm, BindingResult bindingResult,
      ModelMap model, RedirectAttributes redirectAttributes) {
    if (log.isDebugEnabled()) {
      log.debug("execute saveProject()");
    }
    if (bindingResult.hasErrors()) {
      if (log.isInfoEnabled()) {
        log.info("bindingResult has Errors " + bindingResult.getAllErrors().toString());
      }
      return "project";
    }
    redirectAttributes.addFlashAttribute("saveState", SavedState.ERROR.toString());
    redirectAttributes.addFlashAttribute("saveStateMsg", "erfolgreich!!!");
    return "redirect:/project/" + pForm.getProject().getId();
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
      pForm = getProjectData(pid, user);
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
    model.put("ProjectForm", pForm);
    return "project";
  }

  /**
   * 
   * @param pid
   * @param user
   * @return
   * @throws Exception
   */
  public ProjectForm getProjectData(String pid, UserDTO user) throws Exception {
    if (log.isDebugEnabled()) {
      log.debug("execute getProjectData");
    }
    if (pid != null && !pid.isEmpty() && user != null) {
      ProjectDTO pdto = projectDAO.findByIdWithRole(pid, String.valueOf(user.getId()));
      if (pdto == null || pdto.getId() <= 0 || pdto.getProjectRole() == null
          || pdto.getProjectRole().getUserId() <= 0) {
        throw new DataWizException(
            "Project or project_role is empty for user=" + user.getEmail() + " and project=" + pid);
      }
      if (!user.hasRole(Roles.ADMIN) || (user.getId() != pdto.getProjectRole().getUserId())) {
        throw new DataWizSecurityException("SECURITY: User with email: " + user.getEmail()
            + " tries to get access to project:" + pdto.getId() + " without having the permissions to read");
      }
      ProjectForm pForm = createProjectForm();
      pForm.setProject(pdto);
      pForm.setContributors(contributorDAO.getByProject(pdto, false, false));
      pForm.setPrimaryContributor(contributorDAO.findPrimaryContributorByProject(pdto));
      pForm.setTags(new ArrayList<String>(tagDAO.getTagsByProjectID(pdto).values()));
      return pForm;
    } else {
      log.warn("ProjectID or UserDTO is empty - NULL returned!");
      return null;
    }
  }
}
