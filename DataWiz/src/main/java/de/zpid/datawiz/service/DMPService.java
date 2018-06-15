package de.zpid.datawiz.service;

import java.math.BigInteger;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

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
import de.zpid.datawiz.enumeration.DataWizErrorCodes;
import de.zpid.datawiz.enumeration.DmpCategory;
import de.zpid.datawiz.exceptions.DataWizSystemException;
import de.zpid.datawiz.form.ProjectForm;
import de.zpid.datawiz.util.ODFUtil;

/**
 * Service Class for the DMP <br />
 * <br />
 * This file is part of Datawiz.<br />
 *
 * <b>Copyright 2018, Leibniz Institute for Psychology Information (ZPID), <a href="http://zpid.de" title="http://zpid.de">http://zpid.de</a>.</b><br />
 * <br />
 * <a rel="license" href="http://creativecommons.org/licenses/by-nc-sa/4.0/"><img alt="Creative Commons License" style= "border-width:0" src=
 * "https://i.creativecommons.org/l/by-nc-sa/4.0/80x15.png" /></a><br />
 * <span xmlns:dct="http://purl.org/dc/terms/" property="dct:title">Datawiz</span> by
 * <a xmlns:cc="http://creativecommons.org/ns#" href="zpid.de" property="cc:attributionName" rel="cc:attributionURL"> Leibniz Institute for Psychology
 * Information (ZPID)</a> is licensed under a <a rel="license" href="http://creativecommons.org/licenses/by-nc-sa/4.0/">Creative Commons
 * Attribution-NonCommercial-ShareAlike 4.0 International License</a>.
 *
 * @author Ronny Boelter
 * @version 1.0
 */
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
     * This function saves a new DMP into the DMP table if the DMP identifier is not set.
     *
     * @param pForm     ProjectForm
     * @param hasErrors boolean
     * @return Returns an error if saving was not sucessful
     * @throws DataWizSystemException
     */
    public void saveNewDMPSetID(final ProjectForm pForm) throws DataWizSystemException {
        log.trace("Entering saveNewDMPSetID for Project [pid: {}]", () -> pForm.getProject().getId());
        if (pForm.getDmp() != null && pForm.getDmp().getId() <= 0) {
            try {
                dmpDAO.insertNewDMP(BigInteger.valueOf(pForm.getProject().getId()));
                pForm.getDmp().setId(pForm.getProject().getId());
            } catch (Exception e) {
                log.fatal("DBS Error - Saving new DMP was not successful Exception: ", () -> e);
                throw new DataWizSystemException(messageSource.getMessage("logging.database.error", new Object[]{e.getMessage()}, Locale.ENGLISH),
                        DataWizErrorCodes.DATABASE_ERROR);
            }
        }
        log.trace("Leaving saveNewDMPSetID for Project [pid: {}]", pForm.getProject().getId());
    }

    /**
     * This functions saves the form data of the DMP for each specific subpage. Because of the fact, that the DMP-form is very huge, it was decidet to split them
     * for a better overview.
     *
     * @param pForm ProjectForm
     * @param bRes  BindingResult
     * @param cat   DmpCategory
     * @param cls   Class
     * @return Returns an error if saving was not sucessful
     * @throws DataWizSystemException
     */
    public void saveDMPDataPart(final ProjectForm pForm, final BindingResult bRes, final DmpCategory cat, final Class<?> cls) throws DataWizSystemException {
        BeanPropertyBindingResult bResTmp = new BeanPropertyBindingResult(pForm, bRes.getObjectName());
        log.trace("Entering saveDMPDataPart " + cat);
        validator.validate(pForm, bResTmp, cls);
        if (!bResTmp.hasErrors()) {
            try {
                switch (cat) {
                    case ADMIN:
                        dmpDAO.updateAdminData(pForm.getDmp());
                        break;
                    case RESEARCH:
                        dmpDAO.updateResearchData(pForm.getDmp());
                        updateFormTypes(pForm, cat);
                        break;
                    case META:
                        dmpDAO.updateMetaData(pForm.getDmp());
                        updateFormTypes(pForm, cat);
                        break;
                    case SHARING:
                        dmpDAO.updateSharingData(pForm.getDmp());
                        break;
                    case STORAGE:
                        dmpDAO.updateStorageData(pForm.getDmp());
                        break;
                    case ORGANIZATION:
                        dmpDAO.updateOrganizationData(pForm.getDmp());
                        break;
                    case ETHICAL:
                        dmpDAO.updateEthicalData(pForm.getDmp());
                        break;
                    case COSTS:
                        dmpDAO.updateCostsData(pForm.getDmp());
                        break;
                }
            } catch (Exception e) {
                log.fatal("ERROR: Database error during database transaction, saveDMPDataPart aborted - Exception:", () -> e);
                throw new DataWizSystemException(messageSource.getMessage("logging.database.error", new Object[]{e.getMessage()}, Locale.ENGLISH),
                        DataWizErrorCodes.DATABASE_ERROR);
            }
        } else {
            for (ObjectError error : bResTmp.getAllErrors()) {
                bRes.addError(error);
            }
            bRes.reject("globalErrors",
                    messageSource.getMessage("dmp.edit." + cat.name().toLowerCase() + ".globalerror.valid", null, LocaleContextHolder.getLocale()));
        }
    }

    /**
     * @param pForm
     * @param cat
     * @throws Exception
     */
    private void updateFormTypes(final ProjectForm pForm, final DmpCategory cat) throws Exception {
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

    /**
     * TODO
     *
     * @param pid
     * @param type
     * @param locale
     * @return
     * @throws Exception
     */
    public byte[] createDMPExport(final Long pid, final String type, final Locale locale) throws Exception {
        ProjectForm pForm = projectService.createProjectForm();
        pForm.setProject(projectDAO.findById(pid));
        pForm.setDmp(dmpDAO.findByID(pForm.getProject()));
        pForm.getDmp().setUsedDataTypes(formTypeDAO.findSelectedFormTypesByIdAndType(pid, DWFieldTypes.DATATYPE, false));
        pForm.getDmp().setUsedCollectionModes(formTypeDAO.findSelectedFormTypesByIdAndType(pid, DWFieldTypes.COLLECTIONMODE, false));
        pForm.getDmp().setSelectedMetaPurposes(formTypeDAO.findSelectedFormTypesByIdAndType(pid, DWFieldTypes.METAPORPOSE, false));
        pForm.setPrimaryContributor(contributorDAO.findPrimaryContributorByProject(pForm.getProject()));
        pForm.setContributors(contributorDAO.findByProject(pForm.getProject(), false, false));
        byte[] content = null;
        switch (type) {
            case "BMBF":
                content = odfUtil.createBMBFDoc(pForm, locale);
                break;
            case "DFG":
                content = odfUtil.createDFGDoc(pForm, locale);
                break;
            case "H2020":
                content = odfUtil.createH2020Doc(pForm, locale);
                break;
            default:
                break;
        }
        return content;
    }

}
