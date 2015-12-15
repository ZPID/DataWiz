package de.zpid.datawiz.controller;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;

import de.zpid.datawiz.dao.ContributorDAO;
import de.zpid.datawiz.dao.ProjectDAO;
import de.zpid.datawiz.dao.StudyDAO;
import de.zpid.datawiz.dao.UserDAO;
import de.zpid.datawiz.dto.ContributorDTO;
import de.zpid.datawiz.dto.ProjectDTO;
import de.zpid.datawiz.dto.UserDTO;
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
  private StudyDAO studyDAO;
  @Autowired
  private ContributorDAO contributorDAO;
  @Autowired
  private UserDAO userDAO;
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
      ModelMap model) {
    if (log.isDebugEnabled()) {
      log.debug("execute saveProject()");
    }
    if (bindingResult.hasErrors()) {
      if (log.isInfoEnabled()) {
        log.info("bindingResult has Errors " + bindingResult.getAllErrors().toString());
      }
      return "project";
    }
    pForm.setSaveState(SavedState.ERROR);
    pForm.setSaveMsg("erfolgreich!!!");
    return "redirect:/project/" + pForm.getProject().getId();
  }

  /**
   * Adds a new Contributor Object to the contributor list
   * 
   * @param pForm
   * @return
   */
  @RequestMapping(value = { "", "/{pid}" }, params = { "addContributor" }, method = RequestMethod.POST)
  public String addContributor(@ModelAttribute("ProjectForm") ProjectForm pForm) {
    if (log.isDebugEnabled()) {
      log.debug("execute addContributor");
    }
    if (pForm.getContributors() == null) {
      pForm.setContributors(new ArrayList<ContributorDTO>());
    }
    pForm.getContributors().add(0, (ContributorDTO) context.getBean("ContributorDTO"));
    // pForm.getContributors().add(new ContributorDTO());
    return "project";
  }

  @RequestMapping(value = { "", "/{pid}" }, params = { "deleteContributor" }, method = RequestMethod.POST)
  public String deleteContributor(@ModelAttribute("ProjectForm") ProjectForm pForm) {
    if (log.isDebugEnabled()) {
      log.debug("execute addContributor");
    }
    if (pForm.getContributors() == null) {
      pForm.setContributors(new ArrayList<ContributorDTO>());
    }
    pForm.getContributors().add(0, (ContributorDTO) context.getBean("ContributorDTO"));
    // pForm.getContributors().add(new ContributorDTO());
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
  public String editProject(@PathVariable String pid, @ModelAttribute("ProjectForm") ProjectForm pForm,
      ModelMap model) {
    if (log.isDebugEnabled()) {
      log.debug("execute editProject for projectID=" + pid);
    }
    UserDTO user = UserUtil.getCurrentUser();
    if (user == null) {
      log.warn("Auth User Object == null - redirect to login");
      return "redirect:/login";
    }
    // catch redirect saveState before overwriting pForm!!
    String state = null;
    String statemsg = null;
    if (pForm != null && pForm.getSaveState() instanceof SavedState) {
      state = pForm.getSaveState().toString();
      statemsg = pForm.getSaveMsg();
    }
    // create new pform!
    pForm = createProjectForm();
    ProjectDTO pdto = null;
    try {
      pdto = projectDAO.findByIdWithRole(pid, String.valueOf(user.getId()));
      if (pdto == null || pdto.getId() <= 0 || pdto.getProjectRole() == null
          || pdto.getProjectRole().getUserId() <= 0) {
        log.warn("Project or project_role is empty for user=" + user.getEmail() + " and project=" + pid);
        // TODO Unerlaubter zugriff -> ausstieg!!!!
      } else {
        if (!user.hasRole(Roles.ADMIN) || (user.getId() != pdto.getProjectRole().getUserId())) {
          log.warn("User with email: " + user.getEmail() + " tries to get access to project:" + pdto.getId()
              + " but has no ");
        }
      }
      pForm.setProject(pdto);
      pForm.setContributors(contributorDAO.getByProject(pdto, false, false));
      pForm.setPrimaryContributor(contributorDAO.findPrimaryContributorByProject(pdto));
      List<String> tags = new ArrayList<>();
      tags.add("loltroll");
      tags.add("qwert");
      pForm.setTags(tags);

    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    if (state != null && !state.equals("")) {
      model.put("saveStateMsg", statemsg);
      model.put("saveState", state);
    }
    model.put("ProjectForm", pForm);
    return "project";
  }

}
