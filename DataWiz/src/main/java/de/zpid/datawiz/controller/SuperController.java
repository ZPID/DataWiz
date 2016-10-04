package de.zpid.datawiz.controller;

import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.SmartValidator;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import de.zpid.datawiz.dao.ContributorDAO;
import de.zpid.datawiz.dao.DmpDAO;
import de.zpid.datawiz.dao.FileDAO;
import de.zpid.datawiz.dao.FormTypesDAO;
import de.zpid.datawiz.dao.ProjectDAO;
import de.zpid.datawiz.dao.RecordDAO;
import de.zpid.datawiz.dao.RoleDAO;
import de.zpid.datawiz.dao.StudyConstructDAO;
import de.zpid.datawiz.dao.StudyDAO;
import de.zpid.datawiz.dao.StudyInstrumentDAO;
import de.zpid.datawiz.dao.StudyListTypesDAO;
import de.zpid.datawiz.dao.UserDAO;
import de.zpid.datawiz.dto.UserDTO;
import de.zpid.datawiz.form.ProjectForm;
import de.zpid.datawiz.form.StudyForm;
import de.zpid.datawiz.util.FileUtil;
import de.zpid.datawiz.util.MinioUtil;
import de.zpid.datawiz.util.ProjectUtil;

public class SuperController {

  private static Logger log = LogManager.getLogger(SuperController.class);

  @Autowired
  protected MessageSource messageSource;
  @Autowired
  protected UserDAO userDAO;
  @Autowired
  protected ProjectDAO projectDAO;
  @Autowired
  protected RoleDAO roleDAO;
  @Autowired
  protected StudyDAO studyDAO;
  @Autowired
  protected FileDAO fileDAO;
  @Autowired
  protected RecordDAO recordDAO;
  @Autowired
  protected ContributorDAO contributorDAO;
  @Autowired
  protected FormTypesDAO formTypeDAO;
  @Autowired
  protected DmpDAO dmpDAO;
  @Autowired
  protected StudyListTypesDAO studyListTypesDAO;
  @Autowired
  protected StudyConstructDAO studyConstructDAO;
  @Autowired
  protected StudyInstrumentDAO studyInstrumentDAO;
  @Autowired
  protected SmartValidator validator;
  @Autowired
  protected HttpServletRequest request;
  @Autowired
  protected Environment env;
  @Autowired
  protected PasswordEncoder passwordEncoder;
  @Autowired
  protected ProjectUtil pUtil;
  @Autowired
  protected FileUtil fileUtil;
  @Autowired
  protected MinioUtil minioUtil;
  @Autowired
  protected ClassPathXmlApplicationContext applicationContext;

  @ModelAttribute("ProjectForm")
  protected ProjectForm createProjectForm() {
    return (ProjectForm) applicationContext.getBean("ProjectForm");
  }

  @ModelAttribute("StudyForm")
  protected StudyForm createStudyForm() {
    return (StudyForm) applicationContext.getBean("StudyForm");
  }

  @ModelAttribute("UserDTO")
  protected UserDTO createUserDTO() {
    return (UserDTO) applicationContext.getBean("UserDTO");
  }

  /**
   * 
   * @param pid
   * @param studyId
   * @param redirectAttributes
   * @param onlyWrite
   * @return
   */
  protected String checkStudyAccess(final Optional<Long> pid, final Optional<Long> studyId,
      final RedirectAttributes redirectAttributes, final boolean onlyWrite, final UserDTO user) {
    if (user == null) {
      log.warn("Auth User Object == null - redirect to login");
      return "redirect:/login";
    }
    if (!pid.isPresent() || pUtil.checkProjectRoles(user, pid.get(), studyId.isPresent() ? studyId.get() : -1,
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
