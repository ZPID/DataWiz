package de.zpid.datawiz.service;

import java.math.BigInteger;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.SmartValidator;

import de.zpid.datawiz.dao.ContributorDAO;
import de.zpid.datawiz.dao.DmpDAO;
import de.zpid.datawiz.dao.FormTypesDAO;
import de.zpid.datawiz.dao.ProjectDAO;
import de.zpid.datawiz.enumeration.DWFieldTypes;
import de.zpid.datawiz.enumeration.DmpCategory;
import de.zpid.datawiz.form.ProjectForm;
import de.zpid.datawiz.util.ODFUtil;

@Service
public class DMPService {

	private static Logger log = LogManager.getLogger(DMPService.class);

	@Autowired
	private DmpDAO dmpDAO;
	@Autowired
	private SmartValidator validator;
	@Autowired
	private FormTypesDAO formTypeDAO;
	@Autowired
	private MessageSource messageSource;
	@Autowired
	private ODFUtil odfUtil;
	@Autowired
	private ProjectService projectService;
	@Autowired
	private ProjectDAO projectDAO;
	@Autowired
	private ContributorDAO contributorDAO;

	/**
	 * 
	 * @param pForm
	 * @param hasErrors
	 * @return
	 */
	public boolean saveNewDMPSetID(final ProjectForm pForm, boolean hasErrors) {
		log.trace("Entering saveNewDMPSetID for Project [pid: {}] with Error [{}]", () -> pForm.getProject().getId(), () -> hasErrors);
		boolean err = hasErrors;
		if (!hasErrors && pForm.getDmp() != null && (pForm.getDmp().getId() <= 0)) {
			try {
				int chk = dmpDAO.insertNewDMP(BigInteger.valueOf(pForm.getProject().getId()));
				if (chk <= 0)
					err = true;
				else
					pForm.getDmp().setId(pForm.getProject().getId());
			} catch (Exception e) {
				err = true;
				log.fatal("DBS Error - Saving new DMP was not successful Exception: ", () -> e);
			}
		}
		log.trace("Leaving saveNewDMPSetID for Project [pid: {}] with Error [{}]", pForm.getProject().getId(), err);
		return err;
	}

	/**
	 * 
	 * @param pForm
	 * @param bRes
	 * @param cat
	 * @param cls
	 * @return
	 */
	public boolean saveDMPDataPart(ProjectForm pForm, BindingResult bRes, DmpCategory cat, Class<?> cls) {
		BeanPropertyBindingResult bResTmp = new BeanPropertyBindingResult(pForm, bRes.getObjectName());
		int changed = 0;
		log.trace("Entering saveDMPDataPart " + cat);
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
	 * 
	 * @param pForm
	 * @param changed
	 * @param cat
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

	/**
	 * TODO
	 * 
	 * @param pid
	 * @param type
	 * @param locale
	 * @return
	 * @throws Exception
	 */
	public byte[] createDMPExport(final Optional<Long> pid, final Optional<String> type, final Locale locale) throws Exception {
		ProjectForm pForm = projectService.createProjectForm();
		pForm.setProject(projectDAO.findById(pid.get()));
		pForm.setDmp(dmpDAO.findByID(pForm.getProject()));
		pForm.getDmp().setUsedDataTypes(formTypeDAO.findSelectedFormTypesByIdAndType(pid.get(), DWFieldTypes.DATATYPE, false));
		pForm.getDmp().setUsedCollectionModes(formTypeDAO.findSelectedFormTypesByIdAndType(pid.get(), DWFieldTypes.COLLECTIONMODE, false));
		pForm.getDmp().setSelectedMetaPurposes(formTypeDAO.findSelectedFormTypesByIdAndType(pid.get(), DWFieldTypes.METAPORPOSE, false));
		pForm.setPrimaryContributor(contributorDAO.findPrimaryContributorByProject(pForm.getProject()));
		pForm.setContributors(contributorDAO.findByProject(pForm.getProject(), false, false));
		byte[] content = null;
		switch (type.get()) {
		case "BMBF":
			content = odfUtil.createBMBFDoc(pForm, locale);
			break;
		case "DFG":
			content = odfUtil.createDFGDoc(pForm, locale);
			break;
		case "H2020":
			content = odfUtil.createH2020Doc(pForm, locale);
			break;
/*		case "PreReg":
			content = odfUtil.createPreRegistrationDoc(pForm, locale);
			break;
		case "PsychData":
			content = odfUtil.createPsychdataDoc(pForm, locale);
			break;*/
		default:
			break;
		}
		return content;
	}

}
