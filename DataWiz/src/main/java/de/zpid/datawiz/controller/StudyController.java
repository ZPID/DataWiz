package de.zpid.datawiz.controller;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;

import de.zpid.datawiz.dto.ContributorDTO;
import de.zpid.datawiz.dto.ProjectDTO;
import de.zpid.datawiz.dto.StudyDTO;
import de.zpid.datawiz.dto.UserDTO;
import de.zpid.datawiz.enumeration.DWFieldTypes;
import de.zpid.datawiz.enumeration.PageState;
import de.zpid.datawiz.form.StudyForm;
import de.zpid.datawiz.util.BreadCrumpUtil;
import de.zpid.datawiz.util.UserUtil;

@Controller
@RequestMapping(value = { "/study", "/project/{pid}/study" })
@SessionAttributes({ "StudyForm", "subnaviActive" })
public class StudyController extends SuperController {

  public StudyController() {
    super();
    log.info("Loading StudyController for mapping /study");
  }

  @RequestMapping(value = { "", "/{studyId}", }, method = RequestMethod.GET)
  public String showStudyPage(@PathVariable final Optional<Long> pid, @PathVariable final Optional<Long> studyId,
      ModelMap model) {
    if (studyId.isPresent()) {
      log.trace("Entering showStudyPage(edit) for study [id: {}]", () -> studyId.get());
    } else {
      log.trace("Entering showStudyPage(create) study");
    }
    if (!pid.isPresent()) {
      // TODO ausstieg - pid fehlt!!!
    }
    final UserDTO user = UserUtil.getCurrentUser();
    if (user == null) {
      log.warn("Auth User Object == null - redirect to login");
      return "redirect:/login";
    }
    StudyForm sForm = createStudyForm();
    try {
      if (pUtil.checkProjectRoles(user, pid.get(), false, true) == null) {
        // TODO
      }
      ProjectDTO project = projectDAO.findById(pid.get());
      if (project == null) {
        // TODO loggin
        return "redirect:/panel";
      }
      sForm.setProject(project);
      List<ContributorDTO> pContri = contributorDAO.findByProject(project, false, true);
      if (studyId.isPresent()) {
        final StudyDTO study = studyDAO.findById(studyId.get());
        if (study != null && study.getId() > 0) {
          study.setContributors(contributorDAO.findByStudy(studyId.get()));
          study.setSoftware(studyListTypesDAO.getAllByStudyAndType(studyId.get(), DWFieldTypes.SOFTWARE));
          study.setPubOnData(studyListTypesDAO.getAllByStudyAndType(studyId.get(), DWFieldTypes.PUBONDATA));
          study.setConflInterests(studyListTypesDAO.getAllByStudyAndType(studyId.get(), DWFieldTypes.CONFLINTEREST));
        }
        sForm.setStudy(study);
        cleanContributorList(pContri, study.getContributors());
      }
      sForm.setProjectContributors(pContri);
    } catch (Exception e) {
      // TODO
      log.warn(e);
    }
    model
        .put("breadcrumpList",
            BreadCrumpUtil.generateBC(PageState.STUDY,
                new String[] { sForm.getProject().getTitle(),
                    (sForm.getStudy() != null && sForm.getStudy().getTitle() != null
                        && !sForm.getStudy().getTitle().isEmpty() ? sForm.getStudy().getTitle() : "empty") },
                pid.get()));
    model.put("StudyForm", sForm);
    model.put("studySubMenu", true);
    model.put("subnaviActive", PageState.STUDY.name());
    log.trace("Method showStudyPage successfully completed");
    return "study";
  }

  /**
   * @param pContri
   * @param study
   */
  private void cleanContributorList(final List<ContributorDTO> pContri, final List<ContributorDTO> sContri) {
    if (pContri != null && sContri != null)
      for (Iterator<ContributorDTO> it = pContri.iterator(); it.hasNext();) {
        ContributorDTO ccontri = it.next();
        for (ContributorDTO scontri : sContri)
          if (scontri.getId() == ccontri.getId())
            it.remove();
      }
  }
}
