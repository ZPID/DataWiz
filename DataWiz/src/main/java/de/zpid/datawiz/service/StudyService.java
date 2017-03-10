package de.zpid.datawiz.service;

import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import de.zpid.datawiz.dto.UserDTO;

@Service
public class StudyService {

  private static Logger log = LogManager.getLogger(StudyService.class);

  @Autowired
  private ProjectService projectService;
  @Autowired
  private MessageSource messageSource;

  public String checkStudyAccess(final Optional<Long> pid, final Optional<Long> studyId,
      final RedirectAttributes redirectAttributes, final boolean onlyWrite, final UserDTO user) {
    if (user == null) {
      log.warn("Auth User Object == null - redirect to login");
      return "redirect:/login";
    }
    if (!pid.isPresent() || projectService.checkProjectRoles(user, pid.get(), studyId.isPresent() ? studyId.get() : -1,
        onlyWrite, true) == null) {
      log.warn(
          "WARN: access denied because of: " + (!pid.isPresent() ? "missing project identifier"
              : "user [id: {}] has no rights to read/write study [id: {}]"),
          () -> user.getId(), () -> studyId.isPresent() ? studyId.get() : 0);
      redirectAttributes.addFlashAttribute("errorMSG",
          messageSource.getMessage("project.not.available", null, LocaleContextHolder.getLocale()));
      return !pid.isPresent() ? "redirect:/panel" : "redirect:/project/" + pid.get();
    }
    return null;
  }

}
