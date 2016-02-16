package de.zpid.datawiz.controller;

import java.math.BigInteger;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.SmartValidator;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import de.zpid.datawiz.dao.ContributorDAO;
import de.zpid.datawiz.dao.DmpDAO;
import de.zpid.datawiz.dao.DmpRelTypeDAO;
import de.zpid.datawiz.dao.ProjectDAO;
import de.zpid.datawiz.dto.DmpDTO;
import de.zpid.datawiz.dto.UserDTO;
import de.zpid.datawiz.exceptions.DataWizException;
import de.zpid.datawiz.exceptions.DataWizSecurityException;
import de.zpid.datawiz.form.ProjectForm;
import de.zpid.datawiz.util.UserUtil;

@Controller
@RequestMapping(value = "/dmp")
@SessionAttributes({ "ProjectForm", "subnaviActive" })
public class DMPController {

  @Autowired
  private ProjectDAO projectDAO;
  @Autowired
  private ContributorDAO contributorDAO;
  @Autowired
  private DmpRelTypeDAO dmpRelTypeDAO;
  @Autowired
  private DmpDAO dmpDAO;
  @Autowired
  private MessageSource messageSource;
  @Autowired
  SmartValidator validator;

  private static final Logger log = Logger.getLogger(DMPController.class);
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
  public String createDMP(ModelMap model) {
    if (log.isDebugEnabled()) {
      log.debug("execute createDMP - GET");
    }
    model.put("subnaviActive", "DMP");
    model.put("ProjectForm", createProjectForm());
    return "dmp";
  }

  /**
   * 
   * @param pid
   * @param pForm
   * @param model
   * @return
   */
  @RequestMapping(value = "/{pid}", method = RequestMethod.GET)
  public String editDMP(@PathVariable String pid, @ModelAttribute("ProjectForm") ProjectForm pForm, ModelMap model,
      RedirectAttributes redirectAttributes) {
    if (log.isDebugEnabled()) {
      log.debug("execute editDMP for projectID=" + pid);
    }
    UserDTO user = UserUtil.getCurrentUser();
    if (user == null) {
      log.warn("Auth User Object == null - redirect to login");
      return "redirect:/login";
    }
    // create new pform!
    DmpDTO dmp;
    try {
      pForm = ProjectController.getProjectForm(pForm, pid, user, this.projectDAO, this.contributorDAO, null, null, null,
          this.dmpRelTypeDAO, "DMP");
      dmp = dmpDAO.getByID(pForm.getProject());
    } catch (Exception e) {
      log.warn("Exception: " + e.getMessage());
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
    if (dmp == null || dmp.getId() == null || dmp.getId().compareTo(BigInteger.ZERO) <= 0) {
      dmp = (DmpDTO) context.getBean("DmpDTO");
    }
    pForm.setDmp(dmp);
    model.put("subnaviActive", "DMP");
    model.put("ProjectForm", pForm);
    return "dmp";
  }

  @RequestMapping(value = "/{pid}", method = RequestMethod.POST)
  public String saveDMP(@PathVariable String pid, @ModelAttribute("ProjectForm") ProjectForm pForm, ModelMap model,
      RedirectAttributes redirectAttributes, BindingResult bRes) {
    if (log.isDebugEnabled()) {
      log.debug("execute saveDMP - POST");
    }
    int changed = 0;
    // check is AdminData Values have changed
    if (pForm.getDmp().isAdminChanged()) {
      if (log.isDebugEnabled()) {
        log.debug("save Administration Data");
      }
      // validate AdminData
      validator.validate(pForm, bRes, DmpDTO.AdminVal.class);
      if (bRes.hasErrors()) {
        if (log.isInfoEnabled()) {
          log.info("Administatrion Data has Errors  - return to form");
        }
        return "dmp";
      }
      changed = dmpDAO.updateAdminData(pForm.getDmp());
      if (changed <= 0) {
        // TODO not saved!!!!
      } else {
        pForm.getDmp().setAdminChanged(false);
      }
    }

    System.out.println(changed);
    return "redirect:/dmp/" + pForm.getDmp().getId();
  }
}
