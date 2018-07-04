package de.zpid.datawiz.dao;

import de.zpid.datawiz.dto.DmpDTO;
import de.zpid.datawiz.dto.ProjectDTO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.sql.ResultSet;

/**
 * DAO Class for DMP
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
@Repository
@Scope("singleton")
public class DmpDAO {

    private final ClassPathXmlApplicationContext applicationContext;
    private final JdbcTemplate jdbcTemplate;
    private static final Logger log = LogManager.getLogger(DmpDAO.class);

    /**
     * Instantiates a new DmpDAO.
     */
    @Autowired
    public DmpDAO(ClassPathXmlApplicationContext applicationContext, JdbcTemplate jdbcTemplate) {
        super();
        log.info("Loading DmpDAO as @Scope(\"singleton\") and @Repository");
        this.applicationContext = applicationContext;
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * This function returns a {@link DmpDTO} which belongs to the passed project.
     *
     * @param project {@link ProjectDTO} contains project identifier
     * @return {@link DmpDTO} on success, otherwise null
     */
    public DmpDTO findByID(final ProjectDTO project) {
        log.trace("Entering findByID for project [id: {}]", project::getId);
        String sql = "SELECT * FROM dw_dmp where id = ?";
        DmpDTO res = jdbcTemplate.query(sql, new Object[]{project.getId()}, (ResultSet rs) -> {
            if (rs.next()) {
                DmpDTO dmp = (DmpDTO) applicationContext.getBean("DmpDTO");
                dmp.setId(rs.getLong("id"));
                // ***************** Administrative Data *****************
                dmp.setDuration(rs.getString("duration"));
                dmp.setOrganizations(rs.getString("organizations"));
                dmp.setPlanAims(rs.getString("planAims"));
                // ***************** Research Data *****************
                dmp.setExistingData(rs.getString("existingData"));
                dmp.setDataCitation(rs.getString("dataCitation"));
                dmp.setExistingDataRelevance(rs.getString("existingDataRelevance"));
                dmp.setExistingDataIntegration(rs.getString("existingDataIntegration"));
                dmp.setOtherDataTypes(rs.getString("otherDataTypes"));
                dmp.setDataReproducibility(rs.getString("dataReproducibility"));
                dmp.setOtherCMIP(rs.getString("otherCMIP"));
                dmp.setOtherCMINP(rs.getString("otherCMINP"));
                dmp.setMeasOccasions(rs.getString("measOccasions"));
                dmp.setReliabilityTraining(rs.getString("reliabilityTraining"));
                dmp.setMultipleMeasurements(rs.getString("multipleMeasurements"));
                dmp.setQualitityOther(rs.getString("qualitityOther"));
                dmp.setFileFormat(rs.getString("fileFormat"));
                dmp.setWorkingCopy(rs.getBoolean("workingCopy"));
                dmp.setGoodScientific(rs.getBoolean("goodScientific"));
                dmp.setSubsequentUse(rs.getBoolean("subsequentUse"));
                dmp.setRequirements(rs.getBoolean("requirements"));
                dmp.setDocumentation(rs.getBoolean("documentation"));
                dmp.setDataSelection(rs.getBoolean("dataSelection"));
                dmp.setSelectionTime(rs.getString("selectionTime"));
                dmp.setSelectionResp(rs.getString("selectionResp"));
                dmp.setStorageDuration(rs.getString("storageDuration"));
                dmp.setDeleteProcedure(rs.getString("deleteProcedure"));
                // ***************** MetaData Data *****************
                dmp.setMetaDescription(rs.getString("metaDescription"));
                dmp.setMetaFramework(rs.getString("metaFramework"));
                dmp.setMetaGeneration(rs.getString("metaGeneration"));
                dmp.setMetaMonitor(rs.getString("metaMonitor"));
                dmp.setMetaFormat(rs.getString("metaFormat"));
                // ***************** Data Sharing *****************
                dmp.setReleaseObligation(rs.getBoolean("releaseObligation"));
                dmp.setSearchableData(rs.getString("searchableData"));
                dmp.setExpectedUsage(rs.getString("expectedUsage"));
                dmp.setPublStrategy(rs.getString("publStrategy"));
                dmp.setAccessReasonAuthor(rs.getString("accessReasonAuthor"));
                dmp.setNoAccessReason(rs.getString("noAccessReason"));
                dmp.setNoAccessReasonOther(rs.getString("noAccessReasonOther"));
                dmp.setDepositName(rs.getString("depositName"));
                dmp.setTransferTime(rs.getString("transferTime"));
                dmp.setSensitiveData(rs.getString("sensitiveData"));
                dmp.setInitialUsage(rs.getString("initialUsage"));
                dmp.setUsageRestriction(rs.getString("usageRestriction"));
                dmp.setAccessCosts(rs.getBoolean("accessCosts"));
                dmp.setClarifiedRights(rs.getBoolean("clarifiedRights"));
                dmp.setAcquisitionAgreement(rs.getBoolean("acquisitionAgreement"));
                dmp.setUsedPID(rs.getString("usedPID"));
                dmp.setUsedPIDTxt(rs.getString("usedPIDTxt"));
                // ***************** Storage and infrastructure *****************
                dmp.setStorageResponsible(rs.getString("storageResponsible"));
                dmp.setNamingCon(rs.getString("namingCon"));
                dmp.setStoragePlaces(rs.getString("storagePlaces"));
                dmp.setStorageBackups(rs.getString("storageBackups"));
                dmp.setStorageTransfer(rs.getString("storageTransfer"));
                dmp.setStorageExpectedSize(rs.getString("storageExpectedSize"));
                dmp.setStorageRequirements(rs.getBoolean("storageRequirements"));
                dmp.setStorageRequirementsTxt(rs.getString("storageRequirementsTxt"));
                dmp.setStorageSuccession(rs.getBoolean("storageSuccession"));
                dmp.setStorageSuccessionTxt(rs.getString("storageSuccessionTxt"));
                // ***************** Organization, management and policies *****************
                dmp.setFrameworkNationality(rs.getString("frameworkNationality"));
                dmp.setFrameworkNationalityTxt(rs.getString("frameworkNationalityTxt"));
                dmp.setResponsibleUnit(rs.getString("responsibleUnit"));
                dmp.setInvolvedInstitutions(rs.getString("involvedInstitutions"));
                dmp.setInvolvedInformed(rs.getBoolean("involvedInformed"));
                dmp.setContributionsDefined(rs.getBoolean("contributionsDefined"));
                dmp.setContributionsDefinedTxt(rs.getString("contributionsDefinedTxt"));
                dmp.setGivenConsent(rs.getBoolean("givenConsent"));
                dmp.setManagementWorkflow(rs.getBoolean("managementWorkflow"));
                dmp.setManagementWorkflowTxt(rs.getString("managementWorkflowTxt"));
                dmp.setStaffDescription(rs.getBoolean("staffDescription"));
                dmp.setStaffDescriptionTxt(rs.getString("staffDescriptionTxt"));
                dmp.setFunderRequirements(rs.getString("funderRequirements"));
                dmp.setPlanningAdherence(rs.getString("planningAdherence"));
                // ***************** Ethical and legal aspects *****************
                dmp.setDataProtection(rs.getBoolean("dataProtection"));
                dmp.setProtectionRequirements(rs.getString("protectionRequirements"));
                dmp.setConsentObtained(rs.getBoolean("consentObtained"));
                dmp.setConsentObtainedTxt(rs.getString("consentObtainedTxt"));
                dmp.setSharingConsidered(rs.getBoolean("sharingConsidered"));
                dmp.setIrbApproval(rs.getBoolean("irbApproval"));
                dmp.setIrbApprovalTxt(rs.getString("irbApprovalTxt"));
                dmp.setSensitiveDataIncluded(rs.getBoolean("sensitiveDataIncluded"));
                dmp.setSensitiveDataIncludedTxt(rs.getString("sensitiveDataIncludedTxt"));
                dmp.setExternalCopyright(rs.getBoolean("externalCopyright"));
                dmp.setExternalCopyrightTxt(rs.getString("externalCopyrightTxt"));
                dmp.setInternalCopyright(rs.getBoolean("internalCopyright"));
                dmp.setInternalCopyrightTxt(rs.getString("internalCopyrightTxt"));
                // ***************** Costs *****************
                dmp.setSpecificCosts(rs.getString("specificCosts"));
                dmp.setSpecificCostsTxt(rs.getString("specificCostsTxt"));
                dmp.setBearCost(rs.getString("bearCost"));
                rs.close();
                return dmp;
            }
            return null;
        });
        log.debug("Transaction \"findByID\" terminates with result: [id: {}]", () -> ((res != null) ? res.getId() : "null"));
        return res;
    }

    /**
     * This function updates the admin data.
     *
     * @param dmp {@link DmpDTO} containing DMP data
     */
    public void updateAdminData(final DmpDTO dmp) {
        log.trace("Entering updateAdminData for project [id: {}]", dmp::getId);
        int ret = this.jdbcTemplate.update("UPDATE dw_dmp SET duration = ?, organizations = ?, planAims = ? WHERE id = ?", dmp.getDuration(),
                dmp.getOrganizations(), dmp.getPlanAims(), dmp.getId());
        log.debug("Transaction \"updateAdminData\" terminates with result: [result: {}]", () -> ret);
    }

    /**
     * This function updates the research data.
     *
     * @param dmp {@link DmpDTO} containing DMP data
     */
    public void updateResearchData(final DmpDTO dmp) {
        log.trace("Entering updateResearchData for project [id: {}]", dmp::getId);
        int ret = this.jdbcTemplate.update(
                "UPDATE dw_dmp SET existingData = ?, dataCitation = ?, existingDataRelevance = ?, existingDataIntegration = ?,"
                        + " otherDataTypes = ?, dataReproducibility = ?, otherCMIP = ?, otherCMINP = ?, measOccasions = ?,"
                        + " reliabilityTraining = ?, multipleMeasurements = ?, qualitityOther = ?, fileFormat = ?,"
                        + " workingCopy = ?, goodScientific = ?, subsequentUse = ?, requirements = ?,"
                        + " documentation = ?, dataSelection = ?, selectionTime = ?, selectionResp = ?, storageDuration = ?, deleteProcedure = ? WHERE id = ?",
                dmp.getExistingData(), dmp.getDataCitation(), dmp.getExistingDataRelevance(), dmp.getExistingDataIntegration(), dmp.getOtherDataTypes(),
                dmp.getDataReproducibility(), dmp.getOtherCMIP(), dmp.getOtherCMINP(), dmp.getMeasOccasions(), dmp.getReliabilityTraining(),
                dmp.getMultipleMeasurements(), dmp.getQualitityOther(), dmp.getFileFormat(), dmp.isWorkingCopy(), dmp.isGoodScientific(), dmp.isSubsequentUse(),
                dmp.isRequirements(), dmp.isDocumentation(), dmp.isDataSelection(), dmp.getSelectionTime(), dmp.getSelectionResp(), dmp.getStorageDuration(),
                dmp.getDeleteProcedure(), dmp.getId());
        log.debug("Transaction \"updateResearchData\" terminates with result: [result: {}]", () -> ret);
    }

    /**
     * This function updates the meta data.
     *
     * @param dmp {@link DmpDTO} containing DMP data
     */
    public void updateMetaData(final DmpDTO dmp) {
        log.trace("Entering updateMetaData for project [id: {}]", dmp::getId);
        int ret = this.jdbcTemplate.update(
                "UPDATE dw_dmp SET metaDescription = ?, metaFramework = ?, metaGeneration = ?, metaMonitor = ?, metaFormat = ? WHERE id = ?", dmp.getMetaDescription(),
                dmp.getMetaFramework(), dmp.getMetaGeneration(), dmp.getMetaMonitor(), dmp.getMetaFormat(), dmp.getId());
        log.debug("Transaction \"updateMetaData\" terminates with result: [result: {}]", () -> ret);
    }

    /**
     * This function updates the sharing data.
     *
     * @param dmp {@link DmpDTO} containing DMP data
     */
    public void updateSharingData(final DmpDTO dmp) {
        log.trace("Entering updateSharingData for project [id: {}]", dmp::getId);
        int ret = this.jdbcTemplate.update(
                "UPDATE dw_dmp SET releaseObligation = ?, searchableData = ?, expectedUsage = ?, "
                        + "publStrategy = ?, accessReasonAuthor = ?, noAccessReason = ?, noAccessReasonOther = ?, "
                        + "depositName = ?, transferTime = ?, sensitiveData= ?, initialUsage = ?, usageRestriction = ?, accessCosts = ?,"
                        + "clarifiedRights = ?, acquisitionAgreement = ?, usedPID = ?, " + "usedPIDTxt = ? WHERE id = ?",
                dmp.isReleaseObligation(), dmp.getSearchableData(), dmp.getExpectedUsage(), dmp.getPublStrategy(), dmp.getAccessReasonAuthor(), dmp.getNoAccessReason(),
                dmp.getNoAccessReasonOther(), dmp.getDepositName(), dmp.getTransferTime(), dmp.getSensitiveData(), dmp.getInitialUsage(), dmp.getUsageRestriction(),
                dmp.isAccessCosts(), dmp.isClarifiedRights(), dmp.isAcquisitionAgreement(), dmp.getUsedPID(), dmp.getUsedPIDTxt(), dmp.getId());
        log.debug("Transaction \"updateSharingData\" terminates with result: [result: {}]", () -> ret);
    }

    /**
     * This function updates the storage data.
     *
     * @param dmp {@link DmpDTO} containing DMP data
     */
    public void updateStorageData(final DmpDTO dmp) {
        log.trace("Entering updateStorageData for project [id: {}]", dmp::getId);
        int ret = this.jdbcTemplate.update(
                "UPDATE dw_dmp SET storageResponsible = ?, namingCon = ?, storagePlaces = ?, storageBackups = ?, "
                        + "storageTransfer = ?, storageExpectedSize = ?, storageRequirements = ?, storageRequirementsTxt = ?, "
                        + "storageSuccession = ?, storageSuccessionTxt = ? WHERE id = ?",
                dmp.getStorageResponsible(), dmp.getNamingCon(), dmp.getStoragePlaces(), dmp.getStorageBackups(), dmp.getStorageTransfer(),
                dmp.getStorageExpectedSize(), dmp.isStorageRequirements(), dmp.getStorageRequirementsTxt(), dmp.isStorageSuccession(), dmp.getStorageSuccessionTxt(),
                dmp.getId());
        log.debug("Transaction \"updateStorageData\" terminates with result: [result: {}]", () -> ret);
    }

    /**
     * This function updates the organization data.
     *
     * @param dmp {@link DmpDTO} containing DMP data
     */
    public void updateOrganizationData(final DmpDTO dmp) {
        log.trace("Entering updateOrganizationData for project [id: {}]", dmp::getId);
        int ret = this.jdbcTemplate.update(
                "UPDATE dw_dmp SET frameworkNationality = ?, frameworkNationalityTxt = ?, responsibleUnit = ?, "
                        + "involvedInstitutions = ?, involvedInformed = ?, contributionsDefined = ?, "
                        + "contributionsDefinedTxt = ?, givenConsent = ?, managementWorkflow = ?, "
                        + "managementWorkflowTxt = ?, staffDescription = ?, staffDescriptionTxt = ?, funderRequirements = ?, planningAdherence = ? WHERE id = ?",
                dmp.getFrameworkNationality(), dmp.getFrameworkNationalityTxt(), dmp.getResponsibleUnit(), dmp.getInvolvedInstitutions(), dmp.isInvolvedInformed(),
                dmp.isContributionsDefined(), dmp.getContributionsDefinedTxt(), dmp.isGivenConsent(), dmp.isManagementWorkflow(), dmp.getManagementWorkflowTxt(),
                dmp.isStaffDescription(), dmp.getStaffDescriptionTxt(), dmp.getFunderRequirements(), dmp.getPlanningAdherence(), dmp.getId());
        log.debug("Transaction \"updateOrganizationData\" terminates with result: [result: {}]", () -> ret);
    }

    /**
     * This function updates the ethical data.
     *
     * @param dmp {@link DmpDTO} containing DMP data
     */
    public void updateEthicalData(final DmpDTO dmp) {
        log.trace("Entering updateEthicalData for project [id: {}]", dmp::getId);
        int ret = this.jdbcTemplate.update("UPDATE dw_dmp SET dataProtection = ?, protectionRequirements = ?, consentObtained = ?, consentObtainedTxt = ?, "
                        + "sharingConsidered = ?, irbApproval = ?, irbApprovalTxt = ?, sensitiveDataIncluded = ?, "
                        + "sensitiveDataIncludedTxt = ?, externalCopyright = ?, externalCopyrightTxt = ?, " + "internalCopyright = ?, internalCopyrightTxt = ? WHERE id = ?",
                dmp.isDataProtection(), dmp.getProtectionRequirements(), dmp.isConsentObtained(), dmp.getConsentObtainedTxt(), dmp.isSharingConsidered(),
                dmp.isIrbApproval(), dmp.getIrbApprovalTxt(), dmp.isSensitiveDataIncluded(), dmp.getSensitiveDataIncludedTxt(), dmp.isExternalCopyright(),
                dmp.getExternalCopyrightTxt(), dmp.isInternalCopyright(), dmp.getInternalCopyrightTxt(), dmp.getId());
        log.debug("Transaction \"updateEthicalData\" terminates with result: [result: {}]", () -> ret);
    }

    /**
     * This function updates the costs data.
     *
     * @param dmp {@link DmpDTO} containing DMP data
     */
    public void updateCostsData(final DmpDTO dmp) {
        log.trace("Entering updateCostsData for project [id: {}]", dmp::getId);
        int ret = this.jdbcTemplate.update("UPDATE dw_dmp SET specificCosts = ?, specificCostsTxt = ?, bearCost= ? WHERE id = ?", dmp.getSpecificCosts(),
                dmp.getSpecificCostsTxt(), dmp.getBearCost(), dmp.getId());
        log.debug("Transaction \"updateCostsData\" terminates with result: [result: {}]", () -> ret);
    }

    /**
     * This function inserts a new DMP into the dw_dmp table.
     *
     * @param dmpid DMP/Project Identifier as {@link BigInteger}
     */
    public void insertNewDMP(final BigInteger dmpid) {
        log.trace("Entering updateCostsData for project [id: {}]", () -> dmpid);
        int ret = this.jdbcTemplate.update("INSERT INTO dw_dmp (id) VALUES(?)", dmpid);
        log.debug("Transaction \"updateCostsData\" terminates with result: [result: {}]", () -> ret);
    }
}
