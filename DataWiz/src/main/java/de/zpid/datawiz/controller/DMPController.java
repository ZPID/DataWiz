package de.zpid.datawiz.controller;

import java.math.BigInteger;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.SmartValidator;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import de.zpid.datawiz.dao.ContributorDAO;
import de.zpid.datawiz.dao.DmpDAO;
import de.zpid.datawiz.dao.FormTypesDAO;
import de.zpid.datawiz.dao.ProjectDAO;
import de.zpid.datawiz.dto.DmpDTO;
import de.zpid.datawiz.dto.UserDTO;
import de.zpid.datawiz.exceptions.DataWizException;
import de.zpid.datawiz.exceptions.DataWizSecurityException;
import de.zpid.datawiz.form.ProjectForm;
import de.zpid.datawiz.util.DelType;
import de.zpid.datawiz.util.DmpCategory;
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
  private FormTypesDAO formTypeDAO;
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
    DmpDTO dmp;
    try {
      pForm = ProjectController.getProjectForm(pForm, pid, user, this.projectDAO, this.contributorDAO, null, null, null,
          this.formTypeDAO, "DMP");
      dmp = dmpDAO.getByID(pForm.getProject());
      if (dmp != null && dmp.getId().compareTo(BigInteger.ZERO) > 0) {
        dmp.setUsedDataTypes(dmpDAO.getDMPUsedDataTypes(dmp.getId(), DelType.datatype));
        dmp.setUsedCollectionModes(dmpDAO.getDMPUsedDataTypes(dmp.getId(), DelType.collectionmode));
        dmp.setSelectedMetaPurposes(dmpDAO.getDMPUsedDataTypes(dmp.getId(), DelType.metaporpose));
        dmp.setAdminChanged(false);
        dmp.setResearchChanged(false);
        dmp.setMetaChanged(false);
        dmp.setSharingChanged(false);
        dmp.setStorageChanged(false);
        dmp.setOrganizationChanged(false);
        dmp.setEthicalChanged(false);
        dmp.setCostsChanged(false);
      }
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
    Boolean hasErrors = false;
    Boolean unChanged = true;
    // TODO testen ob nur berechtiger nutzer speichert + semaphore falls ein nutzer schon daran arbeitet!!!
    // TODO Projektdaten speichern bzw. neues anlegen wenn noch nicht vorhanden!!!
    if (pForm.getDmp().isAdminChanged()) {
      hasErrors = saveDMPDataPart(pForm, bRes, DmpCategory.admin, DmpDTO.AdminVal.class);
      unChanged = false;
    }
    if (pForm.getDmp().isResearchChanged()) {
      hasErrors = (saveDMPDataPart(pForm, bRes, DmpCategory.research, DmpDTO.ResearchVal.class) || hasErrors) ? true
          : false;
      unChanged = false;
    }
    if (pForm.getDmp().isMetaChanged()) {
      hasErrors = (saveDMPDataPart(pForm, bRes, DmpCategory.meta, DmpDTO.MetaVal.class) || hasErrors) ? true : false;
      unChanged = false;
    }
    if (pForm.getDmp().isSharingChanged()) {
      hasErrors = (saveDMPDataPart(pForm, bRes, DmpCategory.sharing, DmpDTO.SharingVal.class) || hasErrors) ? true
          : false;
      unChanged = false;
    }
    if (pForm.getDmp().isStorageChanged()) {
      hasErrors = (saveDMPDataPart(pForm, bRes, DmpCategory.storage, DmpDTO.StorageVal.class) || hasErrors) ? true
          : false;
      unChanged = false;
    }
    if (pForm.getDmp().isOrganizationChanged()) {
      hasErrors = (saveDMPDataPart(pForm, bRes, DmpCategory.organization, DmpDTO.OrganizationVal.class) || hasErrors)
          ? true : false;
      unChanged = false;
    }
    if (pForm.getDmp().isEthicalChanged()) {
      hasErrors = (saveDMPDataPart(pForm, bRes, DmpCategory.ethical, DmpDTO.EthicalVal.class) || hasErrors) ? true
          : false;
      unChanged = false;
    }
    if (pForm.getDmp().isCostsChanged()) {
      hasErrors = (saveDMPDataPart(pForm, bRes, DmpCategory.costs, DmpDTO.CostsVal.class) || hasErrors) ? true : false;
      unChanged = false;
    }
    if (hasErrors || unChanged) {
      return "dmp";
    }
    return "redirect:/dmp/" + pForm.getDmp().getId();
  }

  private boolean saveDMPDataPart(ProjectForm pForm, BindingResult bRes, DmpCategory cat, Class<?> cls) {
    BeanPropertyBindingResult bResTmp = new BeanPropertyBindingResult(pForm, bRes.getObjectName());
    int changed = 0;
    if (log.isDebugEnabled()) {
      log.debug("saving DMP Data for cat= " + cat);
    }
    validator.validate(pForm, bResTmp, cls);
    if (!bResTmp.hasErrors()) {
      try {
        switch (cat) {
        case admin:
          changed = dmpDAO.updateAdminData(pForm.getDmp());
          if (changed > 0)
            pForm.getDmp().setAdminChanged(false);
          break;
        case research:
          changed = (dmpDAO.updateResearchData(pForm.getDmp()));
          if (changed > 0)
            pForm.getDmp().setResearchChanged(false);
          break;
        case meta:
          changed = (dmpDAO.updateMetaData(pForm.getDmp()));
          if (changed > 0)
            pForm.getDmp().setMetaChanged(false);
          break;
        case sharing:
          changed = (dmpDAO.updateSharingData(pForm.getDmp()));
          if (changed > 0)
            pForm.getDmp().setSharingChanged(false);
          break;
        case storage:
          changed = (dmpDAO.updateStorageData(pForm.getDmp()));
          if (changed > 0)
            pForm.getDmp().setStorageChanged(false);
          break;
        case organization:
          changed = (dmpDAO.updateOrganizationData(pForm.getDmp()));
          if (changed > 0)
            pForm.getDmp().setOrganizationChanged(false);
          break;
        case ethical:
          changed = (dmpDAO.updateEthicalData(pForm.getDmp()));
          if (changed > 0)
            pForm.getDmp().setEthicalChanged(false);
          break;
        case costs:
          changed = (dmpDAO.updateCostsData(pForm.getDmp()));
          if (changed > 0)
            pForm.getDmp().setCostsChanged(false);
          break;
        default:
          break;
        }
      } catch (Exception e) {
        // TODO vernünftiger ausstieg bei dbs fehler
        log.warn("DMP not saved! DBS Error: " + e.getMessage());
        return true;
      }
    } else {
      for (ObjectError error : bResTmp.getAllErrors()) {
        bRes.addError(error);
      }
      if (log.isInfoEnabled()) {
        log.info("Validation Errors for cat: " + cat.toString());
        bRes.reject("globalErrors", messageSource.getMessage("dmp.edit." + cat.toString() + ".globalerror.valid", null,
            LocaleContextHolder.getLocale()));
      }
      return true;
    }
    if (changed <= 0) {
      bRes.reject("globalErrors", messageSource.getMessage("dmp.edit." + cat.toString() + ".globalerror.save", null,
          LocaleContextHolder.getLocale()));
      return true;
    }
    return false;
  }
}
