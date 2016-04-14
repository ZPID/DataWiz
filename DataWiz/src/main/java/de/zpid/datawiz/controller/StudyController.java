package de.zpid.datawiz.controller;

import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;

import de.zpid.datawiz.form.StudyForm;
import de.zpid.datawiz.util.BreadCrumpUtil;

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
  public String handleRequest(@PathVariable final Long pid, @PathVariable final Optional<Long> studyId,
      ModelMap model) {
    if (studyId.isPresent()) {
      log.trace("Entering handleRequest(edit) for study [id: {}]", () -> studyId.get());
    } else {
      log.trace("Entering handleRequest(create) study");
    }
    StudyForm sForm = createStudyForm();
    try {
      sForm.setProject(projectDAO.findById(pid));
      if (studyId.isPresent()) {
        sForm.setStudy(studyDAO.findById(studyId.get()));
        model.put("breadcrumpList", BreadCrumpUtil.generateBC("study", new String[] { "" + pid, "sdfsfd" }, pid));
      }
    } catch (Exception e) {
      log.warn(e);
    }
    model.put("breadcrumpList", BreadCrumpUtil.generateBC("study",
        new String[] { sForm.getProject().getTitle(), sForm.getStudy().getTitle() }, pid));
    model.put("StudyForm", sForm);
    return "study";
  }
}
