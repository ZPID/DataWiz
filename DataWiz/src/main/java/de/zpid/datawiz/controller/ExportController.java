package de.zpid.datawiz.controller;

import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import de.zpid.datawiz.dto.UserDTO;
import de.zpid.datawiz.enumeration.PageState;
import de.zpid.datawiz.exceptions.DataWizException;
import de.zpid.datawiz.exceptions.DataWizSecurityException;
import de.zpid.datawiz.form.ProjectForm;
import de.zpid.datawiz.util.BreadCrumpUtil;
import de.zpid.datawiz.util.UserUtil;

@Controller
@RequestMapping(value = "/export")
@SessionAttributes({ "breadcrumpList" })
public class ExportController extends SuperController {

  private static Logger log = LogManager.getLogger(ExportController.class);

  @RequestMapping(value = { "", "/{projectId}" }, method = RequestMethod.GET)
  public String showExportPage(@PathVariable final Optional<Long> projectId, final ModelMap model,
      final RedirectAttributes reAtt) {
    log.trace("Entering showExportPage for project [id: {}]", () -> projectId.isPresent() ? projectId.get() : "null");
    if (!projectId.isPresent()) {
      reAtt.addFlashAttribute("errorMSG",
          messageSource.getMessage("roles.error.empty.form", null, LocaleContextHolder.getLocale()));
      return "redirect:/panel";
    }
    final UserDTO user = UserUtil.getCurrentUser();
    if (user == null) {
      log.warn("Auth User Object == null - redirect to login");
      return "redirect:/login";
    }
    final ProjectForm pForm = createProjectForm();
    String pName = "";
    try {
      user.setGlobalRoles(roleDAO.findRolesByUserID(user.getId()));
      projectService.getProjectForm(pForm, projectId.get(), user, PageState.EXPORT,
          projectService.checkProjectRoles(user, projectId.get(), 0, false, false));
      if (pForm.getProject() != null && pForm.getProject().getId() > 0) {
        if (pForm.getProject().getTitle() != null && !pForm.getProject().getTitle().trim().isEmpty()) {
          pName = pForm.getProject().getTitle();
        }
      }
    } catch (Exception e) {
      String redirectMessage;
      if (e instanceof DataWizException) {
        redirectMessage = "project.not.available";
        log.warn("WARN: No Project available for user [email: {}] and project [id: {}]", () -> user.getEmail(),
            () -> projectId.get());
      } else if (e instanceof DataWizSecurityException) {
        redirectMessage = "project.access.denied";
        log.warn("WARN: user [email: {}] tried to get access to project [id: {}] without having a role",
            () -> user.getEmail(), () -> projectId.get());
      } else {
        redirectMessage = "dbs.sql.exception";
        log.error("ERROR: Database error during database transaction, showExportPage aborted - Exception:", e);
      }
      reAtt.addFlashAttribute("errorMSG",
          messageSource.getMessage(redirectMessage, null, LocaleContextHolder.getLocale()));
      return "redirect:/panel";
    }
    model.put("breadcrumpList", BreadCrumpUtil.generateBC(PageState.PROJECT, new String[] { pName }, null, messageSource));
    model.put("subnaviActive", PageState.EXPORT.name());
    model.put("ProjectForm", pForm);
    log.trace("Method showExportPage successfully completed");
    return "export";
  }

}
