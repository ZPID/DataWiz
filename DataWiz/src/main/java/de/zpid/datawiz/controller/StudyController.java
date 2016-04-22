package de.zpid.datawiz.controller;

import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;

import de.zpid.datawiz.dto.UserDTO;
import de.zpid.datawiz.form.StudyForm;
import de.zpid.datawiz.util.BreadCrumpUtil;
import de.zpid.datawiz.util.UserUtil;

@Controller
@RequestMapping(value = { "/study", "/project/{pid}/study" })
@SessionAttributes({ "StudyForm", "subnaviActive" })
public class StudyController extends SuperController {

  public StudyController() {
    super();
    log.info("Loading StudyController for mapping /project");
  }

  @ModelAttribute("StudyForm")
  public StudyForm createStudyForm() {
    return (StudyForm) context.getBean("StudyForm");
  }

  @RequestMapping(value = { "", "/{studyId}", }, method = RequestMethod.GET)
  public String handleRequest(@PathVariable final Optional<Long> pid, @PathVariable final Optional<Long> studyId,
      ModelMap model) {
    if (studyId.isPresent()) {
      log.trace("Entering handleRequest(edit) for study [id: {}]", () -> studyId.get());
    } else {
      log.trace("Entering handleRequest(create) study");
    }
    final UserDTO user = UserUtil.getCurrentUser();
    if (user == null) {
      log.warn("Auth User Object == null - redirect to login");
      return "redirect:/login";
    }
    StudyForm sForm = createStudyForm();
    try {
      if (pid.isPresent()) {
        if (checkProjectRoles(user, pid.get(), false, true) == null) {

        }
        sForm.setProject(projectDAO.findById(pid.get()));
      } else {
        // TODO ausstieg - pid fehlt!!!
      }
      if (studyId.isPresent()) {
        sForm.setStudy(studyDAO.findById(studyId.get()));
      }
    } catch (Exception e) {
      log.warn(e);
    }
    model
        .put("breadcrumpList",
            BreadCrumpUtil.generateBC("study",
                new String[] { sForm.getProject().getTitle(),
                    (sForm.getStudy() != null && sForm.getStudy().getTitle() != null
                        && !sForm.getStudy().getTitle().isEmpty() ? sForm.getStudy().getTitle() : "empty") },
                pid.get()));
    model.put("StudyForm", sForm);
    model.put("studySubMenu", true);
    model.put("subnaviActive", "STUDY");
    return "study";
  }
}
