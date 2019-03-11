package de.zpid.datawiz.service;

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

import java.math.BigInteger;
import java.util.List;
import java.util.Locale;

/**
 * Service class for the DMP controller to separate the web logic from the business logic.
 * <p>
 * This file is part of the DataWiz distribution (https://github.com/ZPID/DataWiz).
 * Copyright (c) 2018 <a href="https://leibniz-psychology.org/">Leibniz Institute for Psychology Information (ZPID)</a>.
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3.
 * <p>
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <a href="http://www.gnu.org/licenses/">http://www.gnu.org/licenses/</a>.
 *
 * @author Ronny Boelter
 * @version 1.0
 **/
@Service
public class DMPService {

    private static final Logger log = LogManager.getLogger(DMPService.class);

    private final DmpDAO dmpDAO;
    private final SmartValidator validator;
    private final FormTypesDAO formTypeDAO;
    private final MessageSource messageSource;
    private final ODFUtil odfUtil;
    private final ProjectService projectService;
    private final ProjectDAO projectDAO;
    private final ContributorDAO contributorDAO;

    @Autowired
    public DMPService(DmpDAO dmpDAO, SmartValidator validator, FormTypesDAO formTypeDAO, MessageSource messageSource,
                      ODFUtil odfUtil, ProjectService projectService, ProjectDAO projectDAO, ContributorDAO contributorDAO) {
        this.dmpDAO = dmpDAO;
        this.validator = validator;
        this.formTypeDAO = formTypeDAO;
        this.messageSource = messageSource;
        this.odfUtil = odfUtil;
        this.projectService = projectService;
        this.projectDAO = projectDAO;
        this.contributorDAO = contributorDAO;
    }

    /**
     * Saves a new DMP when a project is created and sets the new identifier to the dmp object included in ProjectForm
     *
     * @param pForm {@link ProjectForm} contains Project and DMP data
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
     * This functions saves the form data of the DMP for each specific part. Because of the fact, that the DMP-form is very huge, it was decided to split them
     * for a better handling.
     *
     * @param pForm {@link ProjectForm} contains DMP data
     * @param bRes  {@link BindingResult}
     * @param cat   {@link DmpCategory} the part that has to be saved
     * @param cls   {@link Class} required for validation
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
     * Updates the different form type fields of the DMP
     *
     * @param pForm {@link ProjectForm} contains DMP data
     * @param cat   {@link DmpCategory} the part that has to be updated
     */
    private void updateFormTypes(final ProjectForm pForm, final DmpCategory cat) {
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
     * Prepares the DMP data for ODT export
     *
     * @param pid    {@link Long} Project/DMP identifier
     * @param type   {@link String} Export type selected by the user
     * @param locale {@link Locale} EN or DE
     * @return export document as byte[]
     * @throws Exception DBS or ODF Exceptions
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
                content = odfUtil.createAllDoc(pForm, locale);
                break;
        }
        return content;
    }

}
