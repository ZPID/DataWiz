package de.zpid.datawiz.controller;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.SmartValidator;
import org.springframework.web.bind.annotation.ModelAttribute;

import de.zpid.datawiz.dao.ContributorDAO;
import de.zpid.datawiz.dao.DmpDAO;
import de.zpid.datawiz.dao.FileDAO;
import de.zpid.datawiz.dao.FormTypesDAO;
import de.zpid.datawiz.dao.ProjectDAO;
import de.zpid.datawiz.dao.RoleDAO;
import de.zpid.datawiz.dao.StudyDAO;
import de.zpid.datawiz.dao.StudyListTypesDAO;
import de.zpid.datawiz.dao.StudyObjectivesDAO;
import de.zpid.datawiz.dao.UserDAO;
import de.zpid.datawiz.dto.UserDTO;
import de.zpid.datawiz.form.ProjectForm;
import de.zpid.datawiz.form.StudyForm;
import de.zpid.datawiz.util.ProjectUtil;

public class SuperController {

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
  protected ContributorDAO contributorDAO;
  @Autowired
  protected FormTypesDAO formTypeDAO;
  @Autowired
  protected DmpDAO dmpDAO;
  @Autowired
  protected StudyListTypesDAO studyListTypesDAO;
  @Autowired
  protected StudyObjectivesDAO studyObjectivesDAO;
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

  protected ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("spring.xml");

  protected Logger log = LogManager.getLogger(getClass());

  @ModelAttribute("ProjectForm")
  protected ProjectForm createProjectForm() {
    return (ProjectForm) context.getBean("ProjectForm");
  }

  @ModelAttribute("StudyForm")
  protected StudyForm createStudyForm() {
    return (StudyForm) context.getBean("StudyForm");
  }

  @ModelAttribute("UserDTO")
  protected UserDTO createUserDTO() {
    return (UserDTO) context.getBean("UserDTO");
  }

}
