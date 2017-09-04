package de.zpid.datawiz.controller;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import de.zpid.datawiz.dto.DmpDTO;
import de.zpid.datawiz.dto.ProjectDTO;
import de.zpid.datawiz.dto.UserDTO;
import de.zpid.datawiz.enumeration.DWFieldTypes;
import de.zpid.datawiz.enumeration.DmpCategory;
import de.zpid.datawiz.enumeration.PageState;
import de.zpid.datawiz.enumeration.SavedState;
import de.zpid.datawiz.form.ProjectForm;
import de.zpid.datawiz.service.ExceptionService;
import de.zpid.datawiz.util.BreadCrumpUtil;
import de.zpid.datawiz.util.UserUtil;

@Controller
@RequestMapping(value = "/dmp")
@SessionAttributes({ "ProjectForm", "subnaviActive" })
public class DMPController extends SuperController {

	private static Logger log = LogManager.getLogger(DMPController.class);

	@Autowired
	private ExceptionService exceptionService;

	public DMPController() {
		super();
		if (log.isEnabled(Level.INFO))
			log.info("Loading DMPController for mapping /dmp");
	}

	/**
	 * 
	 * @return
	 */
	@ModelAttribute("ProjectForm")
	public ProjectForm createProjectForm() {
		return (ProjectForm) applicationContext.getBean("ProjectForm");
	}

	/**
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping(method = RequestMethod.GET)
	public String createDMP(ModelMap model) {
		if (log.isEnabled(Level.DEBUG)) {
			log.debug("execute createDMP RequestMethod.GET");
		}
		model.put("subnaviActive", "DMP");
		ProjectForm pForm = createProjectForm();
		try {
			pForm.setDataTypes(formTypeDAO.findAllByType(true, DWFieldTypes.DATATYPE));
			pForm.setCollectionModes(formTypeDAO.findAllByType(true, DWFieldTypes.COLLECTIONMODE));
			pForm.setMetaPurposes(formTypeDAO.findAllByType(true, DWFieldTypes.METAPORPOSE));
		} catch (Exception e) {
			if (log.isEnabled(Level.ERROR))
				log.error("ERROR: Database error during database transaction, deleteInvite aborted - Exception:", e);
			model.put("errorMSG", messageSource.getMessage("dbs.sql.exception", null, LocaleContextHolder.getLocale()));
			return "redirect:/panel";
		}
		model.put("breadcrumpList", BreadCrumpUtil.generateBC(PageState.PROJECT,
		    new String[] { messageSource.getMessage("breadcrumb.new.project", null, LocaleContextHolder.getLocale()) }, null, messageSource));
		model.put("subnaviActive", PageState.DMP.name());
		model.put("ProjectForm", pForm);
		if (log.isEnabled(Level.DEBUG)) {
			log.debug("Method createDMP successfully completed");
		}
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
	public String editDMP(@PathVariable Optional<Long> pid, @ModelAttribute("ProjectForm") ProjectForm pForm, ModelMap model,
	    RedirectAttributes redirectAttributes) {
		if (log.isEnabled(Level.DEBUG)) {
			log.debug("execute editDMP for projectID=" + pid);
		}
		UserDTO user = UserUtil.getCurrentUser();
		if (user == null) {
			log.warn("Auth User Object == null - redirect to login");
			return "redirect:/login";
		}
		String pName = "";
		try {
			projectService.getProjectForm(pForm, pid.get(), user, PageState.DMP, projectService.checkProjectRoles(user, pid.get(), 0, false, false));
			if (pForm != null && pForm.getProject() != null && pForm.getProject().getTitle() != null && !pForm.getProject().getTitle().trim().isEmpty()) {
				pName = pForm.getProject().getTitle();
			}
		} catch (Exception e) {
			return exceptionService.setErrorMessagesAndRedirects(pid, null, null, model, redirectAttributes, e, "dmpController.editDMP");
		}
		model.put("breadcrumpList", BreadCrumpUtil.generateBC(PageState.PROJECT, new String[] { pName }, null, messageSource));
		model.put("subnaviActive", PageState.DMP.name());
		model.put("ProjectForm", pForm);
		return "dmp";
	}

	@RequestMapping(value = { "", "/{pid}" }, method = RequestMethod.POST)
	public String saveDMP(@ModelAttribute("ProjectForm") ProjectForm pForm, ModelMap model, RedirectAttributes redirectAttributes, BindingResult bRes,
	    @PathVariable final Optional<Long> pid) {
		if (log.isEnabled(Level.DEBUG)) {
			log.debug("execute saveDMP - POST");
		}
		UserDTO user = UserUtil.getCurrentUser();
		Boolean hasErrors = false;
		if (!pid.isPresent() || projectService.checkProjectRoles(user, pid.get(), 0, true, false) == null) {
			bRes.reject("globalErrors", messageSource.getMessage("project.save.globalerror.not.successful", null, LocaleContextHolder.getLocale()));
			hasErrors = true;
		}
		Boolean unChanged = true;
		validator.validate(pForm, bRes, ProjectDTO.ProjectVal.class);
		if (bRes.hasErrors() || pForm.getProject() == null || pForm.getProject().getTitle().isEmpty()) {
			if (log.isEnabled(Level.INFO)) {
				log.info("bindingResult has Errors = ProjectName not given!");
			}
			hasErrors = true;
		}
		// if (hasErrors || !projectService.saveOrUpdateProject(pForm).equals(DataWizErrorCodes.OK)) {
		// bRes.reject("globalErrors",
		// messageSource.getMessage("project.save.globalerror.not.successful", null, LocaleContextHolder.getLocale()));
		// hasErrors = true;
		// }
		if (!hasErrors && pForm.getDmp() != null && (pForm.getDmp().getId() <= 0)) {
			try {
				int chk = dmpDAO.insertNewDMP(BigInteger.valueOf(pForm.getProject().getId()));
				if (chk <= 0)
					hasErrors = true;
				else
					pForm.getDmp().setId(pForm.getProject().getId());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		// TODO testen ob nur berechtiger nutzer speichert + semaphore falls ein nutzer schon daran arbeitet!!!
		// TODO Projektdaten speichern bzw. neues anlegen wenn noch nicht vorhanden!!!
		if (!hasErrors) {
			if (pForm.getDmp().isAdminChanged()) {
				hasErrors = saveDMPDataPart(pForm, bRes, DmpCategory.ADMIN, DmpDTO.AdminVal.class);
				unChanged = false;
			}
			if (pForm.getDmp().isResearchChanged()) {
				hasErrors = (saveDMPDataPart(pForm, bRes, DmpCategory.RESEARCH, DmpDTO.ResearchVal.class) || hasErrors) ? true : false;
				unChanged = false;
			}
			if (pForm.getDmp().isMetaChanged()) {
				hasErrors = (saveDMPDataPart(pForm, bRes, DmpCategory.META, DmpDTO.MetaVal.class) || hasErrors) ? true : false;
				unChanged = false;
			}
			if (pForm.getDmp().isSharingChanged()) {
				hasErrors = (saveDMPDataPart(pForm, bRes, DmpCategory.SHARING, DmpDTO.SharingVal.class) || hasErrors) ? true : false;
				unChanged = false;
			}
			if (pForm.getDmp().isStorageChanged()) {
				hasErrors = (saveDMPDataPart(pForm, bRes, DmpCategory.STORAGE, DmpDTO.StorageVal.class) || hasErrors) ? true : false;
				unChanged = false;
			}
			if (pForm.getDmp().isOrganizationChanged()) {
				hasErrors = (saveDMPDataPart(pForm, bRes, DmpCategory.ORGANIZATION, DmpDTO.OrganizationVal.class) || hasErrors) ? true : false;
				unChanged = false;
			}
			if (pForm.getDmp().isEthicalChanged()) {
				hasErrors = (saveDMPDataPart(pForm, bRes, DmpCategory.ETHICAL, DmpDTO.EthicalVal.class) || hasErrors) ? true : false;
				unChanged = false;
			}
			if (pForm.getDmp().isCostsChanged()) {
				hasErrors = (saveDMPDataPart(pForm, bRes, DmpCategory.COSTS, DmpDTO.CostsVal.class) || hasErrors) ? true : false;
				unChanged = false;
			}
		}
		if (hasErrors || unChanged) {
			// TODO
			return "dmp";
		}
		redirectAttributes.addFlashAttribute("saveState", SavedState.SUCCESS);
		redirectAttributes.addFlashAttribute("saveStateMsg", "erfolgreich!!!");
		return "redirect:/dmp/" + pForm.getDmp().getId();
	}

	private boolean saveDMPDataPart(ProjectForm pForm, BindingResult bRes, DmpCategory cat, Class<?> cls) {
		BeanPropertyBindingResult bResTmp = new BeanPropertyBindingResult(pForm, bRes.getObjectName());
		int changed = 0;
		if (log.isEnabled(Level.DEBUG)) {
			log.debug("saving DMP Data for cat= " + cat);
		}
		validator.validate(pForm, bResTmp, cls);
		if (!bResTmp.hasErrors()) {
			try {
				switch (cat) {
				case ADMIN:
					changed = dmpDAO.updateAdminData(pForm.getDmp());
					if (changed > 0)
						pForm.getDmp().setAdminChanged(false);
					break;
				case RESEARCH:
					changed = dmpDAO.updateResearchData(pForm.getDmp());
					updateFormTypes(pForm, changed, cat);
					if (changed > 0)
						pForm.getDmp().setResearchChanged(false);
					break;
				case META:
					changed = dmpDAO.updateMetaData(pForm.getDmp());
					updateFormTypes(pForm, changed, cat);
					if (changed > 0)
						pForm.getDmp().setMetaChanged(false);
					break;
				case SHARING:
					changed = (dmpDAO.updateSharingData(pForm.getDmp()));
					if (changed > 0)
						pForm.getDmp().setSharingChanged(false);
					break;
				case STORAGE:
					changed = (dmpDAO.updateStorageData(pForm.getDmp()));
					if (changed > 0)
						pForm.getDmp().setStorageChanged(false);
					break;
				case ORGANIZATION:
					changed = (dmpDAO.updateOrganizationData(pForm.getDmp()));
					if (changed > 0)
						pForm.getDmp().setOrganizationChanged(false);
					break;
				case ETHICAL:
					changed = (dmpDAO.updateEthicalData(pForm.getDmp()));
					if (changed > 0)
						pForm.getDmp().setEthicalChanged(false);
					break;
				case COSTS:
					changed = (dmpDAO.updateCostsData(pForm.getDmp()));
					if (changed > 0)
						pForm.getDmp().setCostsChanged(false);
					break;
				default:
					break;
				}
			} catch (Exception e) {
				if (log.isEnabled(Level.ERROR))
					log.error("ERROR: Database error during database transaction, deleteInvite aborted - Exception:", e);
				return true;
			}
		} else {
			for (ObjectError error : bResTmp.getAllErrors()) {
				bRes.addError(error);
			}
			if (log.isEnabled(Level.INFO)) {
				log.info("Validation Errors for cat: " + cat.toString());
				bRes.reject("globalErrors",
				    messageSource.getMessage("dmp.edit." + cat.name().toLowerCase() + ".globalerror.valid", null, LocaleContextHolder.getLocale()));
			}
			return true;
		}
		if (changed <= 0) {
			bRes.reject("globalErrors",
			    messageSource.getMessage("dmp.edit." + cat.name().toLowerCase() + ".globalerror.save", null, LocaleContextHolder.getLocale()));
			return true;
		}
		return false;
	}

	/**
	 * @param pForm
	 * @param changed
	 * @throws Exception
	 */
	private void updateFormTypes(ProjectForm pForm, int changed, DmpCategory cat) throws Exception {
		if (changed > 0) {
			if (cat.equals(DmpCategory.RESEARCH)) {
				List<Integer> datatypes = formTypeDAO.findSelectedFormTypesByIdAndType(pForm.getDmp().getId(), DWFieldTypes.DATATYPE, false);
				if (datatypes != null && datatypes.size() > 0) {
					formTypeDAO.deleteSelectedFormType(pForm.getDmp().getId(), datatypes, false);
				}
				if (pForm.getDmp().getUsedDataTypes() != null && pForm.getDmp().getUsedDataTypes().size() > 0) {
					formTypeDAO.insertSelectedFormType(pForm.getDmp().getId(), pForm.getDmp().getUsedDataTypes(), false);
				}
				List<Integer> collectionModes = formTypeDAO.findSelectedFormTypesByIdAndType(pForm.getDmp().getId(), DWFieldTypes.COLLECTIONMODE, false);
				if (collectionModes != null && collectionModes.size() > 0) {
					formTypeDAO.deleteSelectedFormType(pForm.getDmp().getId(), collectionModes, false);
				}
				if (pForm.getDmp().getUsedCollectionModes() != null && pForm.getDmp().getUsedCollectionModes().size() > 0) {
					formTypeDAO.insertSelectedFormType(pForm.getDmp().getId(), pForm.getDmp().getUsedCollectionModes(), false);
				}
			} else if (cat.equals(DmpCategory.META)) {
				List<Integer> metaporpose = formTypeDAO.findSelectedFormTypesByIdAndType(pForm.getDmp().getId(), DWFieldTypes.METAPORPOSE, false);
				if (metaporpose != null && metaporpose.size() > 0) {
					formTypeDAO.deleteSelectedFormType(pForm.getDmp().getId(), metaporpose, false);
				}
				if (pForm.getDmp().getSelectedMetaPurposes() != null && pForm.getDmp().getSelectedMetaPurposes().size() > 0) {
					formTypeDAO.insertSelectedFormType(pForm.getDmp().getId(), pForm.getDmp().getSelectedMetaPurposes(), false);
				}
			}
		}
	}

	@RequestMapping(value = { "/checkConnection" })
	public ResponseEntity<Object> checkConnection() {
		log.trace("checkConnection");
		return new ResponseEntity<Object>(HttpStatus.OK);
	}
}
