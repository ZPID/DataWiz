package de.zpid.datawiz.dto;

import de.zpid.datawiz.util.ListUtil;

import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;

/**
 * DMP Data Transfer Object
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
public class DmpDTO implements Serializable {

    /**
     * The Constant serialVersionUID.
     */
    private static final long serialVersionUID = 1989300324143602401L;

    /**
     * The DMP ID - Is similar to the Project ID because only 1 DMP for a project.
     */
    private long id;

    // ***************** Administrative Data *****************
    public interface AdminVal {
    }

    /**
     * DMP04.
     */
    @Size(max = 250, groups = AdminVal.class)
    private String duration;

    /**
     * DMP05.
     */
    @Size(max = 250, groups = AdminVal.class)
    private String organizations;

    /**
     * DMP07.
     */
    @Size(max = 2000, groups = AdminVal.class)
    private String planAims;

    // ***************** Research Data *****************
    public interface ResearchVal {
    }

    /**
     * DMP09 : 0 yes, existing data are used/ 1 no data were found/ 2 no search was carried out.
     */
    private String existingData;

    /**
     * DMP97.
     */
    @Size(max = 1000, groups = ResearchVal.class)
    private String dataCitation;

    /**
     * DMP10.
     */
    @Size(max = 1000, groups = ResearchVal.class)
    private String existingDataRelevance;

    /**
     * DMP11.
     */
    @Size(max = 1000, groups = ResearchVal.class)
    private String existingDataIntegration;

    /**
     * DMP12 META096 Study Metadata.
     */
    private List<Integer> usedDataTypes;

    /**
     * DMP12 -> selected == other.
     */
    @Size(max = 1000, groups = ResearchVal.class)
    private String otherDataTypes;

    /**
     * DMP13.
     */
    @Size(max = 1000, groups = ResearchVal.class)
    private String dataReproducibility;

    /**
     * DMP14. How will the data be collected or generated?
     * DMP14 PsychData - META096
     */
    private List<Integer> usedCollectionModes;

    /**
     * DMP87 other Collection Modes with Invest. present
     */
    @Size(max = 1000, groups = ResearchVal.class)
    private String otherCMIP;

    /**
     * DMP87 other Collection Modes with Invest. not present
     */
    @Size(max = 1000, groups = ResearchVal.class)
    private String otherCMINP;

    /**
     * DMP89 Subitem DMP14, PsychData - META097 Study Metadata.
     */
    @Size(max = 1000, groups = ResearchVal.class)
    private String measOccasions;

    /**
     * Subitem DMP16.
     * Subitem DMP90, JARS - META131 Study Metadata.
     */
    @Size(max = 1000, groups = ResearchVal.class)
    private String reliabilityTraining;

    /**
     * Subitem DMP91, JARS - META132 Study Metadata.
     */
    @Size(max = 1000, groups = ResearchVal.class)
    private String multipleMeasurements;

    /**
     * Subitem DMP92, Psychdata - META233 Study Metadata.
     */
    @Size(max = 1000, groups = ResearchVal.class)
    private String qualitityOther;

    /**
     * DMP17.
     */
    @Size(max = 1000, groups = ResearchVal.class)
    private String fileFormat;

    /**
     * DMP18 - Why must the data be completely or partially stored?.
     * DMP19 - Antwortoption DMP18
     */
    private boolean workingCopy;

    /**
     * DMP20 - Antwortoption DMP18.
     */
    private boolean goodScientific;

    /**
     * DMP21 - Antwortoption DMP18.
     */
    private boolean subsequentUse;

    /**
     * DMP22 - Antwortoption DMP18.
     */
    private boolean requirements;

    /**
     * DMP23 - Antwortoption DMP18.
     */
    private boolean documentation;

    /**
     * DMP86 - Does data selection take place?.
     */
    private boolean dataSelection;

    /**
     * the next inputs are only important if dataselection == true.
     * DMP24 - DMP86 ja
     */
    @Size(max = 500, groups = ResearchVal.class)
    private String selectionTime;

    /**
     * DMP25 - DMP86 ja.
     */
    @Size(max = 500, groups = ResearchVal.class)
    private String selectionResp;

    /**
     * DMP28.
     */
    @Size(max = 500, groups = ResearchVal.class)
    private String storageDuration;

    /**
     * DMP29.
     */
    @Size(max = 1000, groups = ResearchVal.class)
    private String deleteProcedure;

    // ***************** MetaData Data *****************

    public interface MetaVal {
    }

    /**
     * DMP31.
     */
    private List<Integer> selectedMetaPurposes;

    /**
     * DMP32.
     */
    @Size(max = 1000, groups = MetaVal.class)
    private String metaDescription;

    /**
     * DMP33.
     */
    @Size(max = 1000, groups = MetaVal.class)
    private String metaFramework;

    /**
     * DMP34.
     */
    @Size(max = 1000, groups = MetaVal.class)
    private String metaGeneration;

    /**
     * DMP35.
     */
    @Size(max = 1000, groups = MetaVal.class)
    private String metaMonitor;

    /**
     * DMP36.
     */
    @Size(max = 1000, groups = MetaVal.class)
    private String metaFormat;

    // ***************** Data Sharing *****************

    public interface SharingVal {
    }

    /**
     * DMP39.
     */
    private boolean releaseObligation;

    /**
     * DMP42.
     */
    @Size(max = 1000, groups = SharingVal.class)
    private String searchableData;

    /**
     * DMP44.
     */
    @Size(max = 1000, groups = SharingVal.class)
    private String expectedUsage;

    /**
     * DMP43 (select).
     */
    private String publStrategy;

    /**
     * DMP38 - if data access on demand by author.
     */
    @Size(max = 500, groups = SharingVal.class)
    private String accessReasonAuthor;

    /**
     * DMP38 - if data are not accessible (select).
     */
    private String noAccessReason;

    /**
     * DMP38 - if data are not accessible - reason == other.
     */
    @Size(max = 500, groups = SharingVal.class)
    private String noAccessReasonOther;

    /**
     * DMP98.
     */
    @Size(max = 1000, groups = SharingVal.class)
    private String depositName;

    // next fields are shown if principleRetain == repository!<
    /**
     * DMP45.
     */
    @Size(max = 1000, groups = SharingVal.class)
    private String transferTime;

    /**
     * DMP46.
     */
    @Size(max = 1000, groups = SharingVal.class)
    private String sensitiveData;

    /**
     * DMP47.
     */
    @Size(max = 1000, groups = SharingVal.class)
    private String initialUsage;

    /**
     * DMP48.
     */
    @Size(max = 500, groups = SharingVal.class)
    private String usageRestriction;

    /**
     * DMP49.
     */
    private boolean accessCosts;

    /**
     * DMP51.
     */
    private boolean clarifiedRights;

    /**
     * DMP52.
     */
    private boolean acquisitionAgreement;

    /**
     * DMP53 (select).
     */
    private String usedPID;

    /**
     * DMP53 (select)-> other selected.
     */
    @Size(max = 500, groups = SharingVal.class)
    private String usedPIDTxt;

    // ***************** Storage and infrastructure *****************

    public interface StorageVal {
    }

    /**
     * DMP54.
     */
    @Size(max = 1000, groups = StorageVal.class)
    private String storageResponsible;

    /**
     * DMP55.
     */
    @Size(max = 1000, groups = StorageVal.class)
    private String namingCon;

    /**
     * DMP56.
     */
    @Size(max = 1000, groups = StorageVal.class)
    private String storagePlaces;

    /**
     * DMP57.
     */
    @Size(max = 1000, groups = StorageVal.class)
    private String storageBackups;

    /**
     * DMP58.
     */
    @Size(max = 1000, groups = StorageVal.class)
    private String storageTransfer;

    /**
     * DMP59.
     */
    @Size(max = 1000, groups = StorageVal.class)
    private String storageExpectedSize;

    /**
     * DMP60.
     */
    private boolean storageRequirements;

    /**
     * DMP60 -> if "no" selected.
     */
    @Size(max = 1000, groups = StorageVal.class)
    private String storageRequirementsTxt;

    /**
     * DMP61.
     */
    private boolean storageSuccession;

    /**
     * DMP61 -> if "yes" selected.
     */
    @Size(max = 1000, groups = StorageVal.class)
    private String storageSuccessionTxt;

    // ***************** Organization, management and policies *****************

    public interface OrganizationVal {
    }

    /**
     * DMP62.
     */
    private String frameworkNationality;

    /**
     * DMP62 -> if international specific requirements selected.
     */
    @Size(max = 1000, groups = OrganizationVal.class)
    private String frameworkNationalityTxt;

    /**
     * DMP63.
     */
    @Size(max = 500, groups = OrganizationVal.class)
    private String responsibleUnit;

    /**
     * DMP64.
     */
    @Size(max = 1000, groups = OrganizationVal.class)
    private String involvedInstitutions;

    /**
     * DMP65.
     */
    private boolean involvedInformed;

    /**
     * DMP93 -> if 65 == 1.
     */
    private boolean contributionsDefined;

    /**
     * DMP93 if -> 93 == "1".
     */
    @Size(max = 1000, groups = OrganizationVal.class)
    private String contributionsDefinedTxt;

    /**
     * DMP94 -> if 65 == 1.
     */
    private boolean givenConsent;

    /**
     * DMP66.
     */
    private boolean managementWorkflow;

    /**
     * DMP66 if -> 66 == "1".
     */
    @Size(max = 1000, groups = OrganizationVal.class)
    private String managementWorkflowTxt;

    /**
     * DMP67.
     */
    private boolean staffDescription;

    /**
     * DMP67 if -> 67 == "1".
     */
    @Size(max = 1000, groups = OrganizationVal.class)
    private String staffDescriptionTxt;

    /**
     * DMP68.
     */
    @Size(max = 2000, groups = OrganizationVal.class)
    private String funderRequirements;

    /**
     * DMP72.
     */
    @Size(max = 1000, groups = OrganizationVal.class)
    private String planningAdherence;

    // ***************** Ethical and legal aspects *****************

    public interface EthicalVal {
    }

    /**
     * DMP73.
     */
    private boolean dataProtection;

    /**
     * DMP74. -> if DMP73 == true
     */
    @Size(max = 2000, groups = EthicalVal.class)
    private String protectionRequirements;

    /**
     * DMP75. -> if DMP73 == true
     */
    private boolean consentObtained;

    /**
     * DMP75. -> if DMP75 == false
     */
    @Size(max = 2000, groups = EthicalVal.class)
    private String consentObtainedTxt;

    /**
     * DMP95. -> if DMP75 == true
     */
    private boolean sharingConsidered;

    /**
     * DMP76.
     */
    private boolean irbApproval;

    /**
     * DMP76. -> if DMP76 == false
     */
    @Size(max = 1000, groups = EthicalVal.class)
    private String irbApprovalTxt;

    /**
     * DMP78.
     */
    private boolean sensitiveDataIncluded;

    /**
     * DMP96. -> if DMP78 == true
     */
    @Size(max = 1000, groups = EthicalVal.class)
    private String sensitiveDataIncludedTxt;

    /**
     * DMP79.
     */
    private boolean externalCopyright;

    /**
     * DMP79. -> if DMP79 == true
     */
    @Size(max = 1000, groups = EthicalVal.class)
    private String externalCopyrightTxt;

    /**
     * DMP80.
     */
    private boolean internalCopyright;

    /**
     * DMP80. -> if DMP80 == true
     */
    @Size(max = 1000, groups = EthicalVal.class)
    private String internalCopyrightTxt;

    // ***************** Costs *****************

    public interface CostsVal {
    }

    /**
     * DMP83.
     */
    private String specificCosts;

    /**
     * DMP83. -> if DMP83 == true
     */
    @Size(max = 1000, groups = CostsVal.class)
    private String specificCostsTxt;

    /**
     * DMP85. -> if DMP83 == true
     */
    @Size(max = 1000, groups = CostsVal.class)
    private String bearCost;

    /**
     * Getter for {@link #id}.
     *
     * @return id
     */
    public long getId() {
        return id;
    }

    /**
     * Getter for {@link #duration}.
     *
     * @return duration
     */
    public String getDuration() {
        return duration;
    }

    /**
     * Getter for {@link #organizations}.
     *
     * @return organizations
     */
    public String getOrganizations() {
        return organizations;
    }

    /**
     * Getter for {@link #planAims}.
     *
     * @return planAims
     */
    public String getPlanAims() {
        return planAims;
    }

    /**
     * Getter for {@link #existingData}.
     *
     * @return existingData
     */
    public String getExistingData() {
        return existingData;
    }

    /**
     * Getter for {@link #dataCitation}.
     *
     * @return dataCitation
     */
    public String getDataCitation() {
        return dataCitation;
    }

    /**
     * Getter for {@link #existingDataRelevance}.
     *
     * @return existingDataRelevance
     */
    public String getExistingDataRelevance() {
        return existingDataRelevance;
    }

    /**
     * Getter for {@link #existingDataIntegration}.
     *
     * @return existingDataIntegration
     */
    public String getExistingDataIntegration() {
        return existingDataIntegration;
    }

    /**
     * Getter for {@link #usedDataTypes}.
     *
     * @return usedDataTypes
     */
    public List<Integer> getUsedDataTypes() {
        return usedDataTypes;
    }

    /**
     * Getter for {@link #otherDataTypes}.
     *
     * @return otherDataTypes
     */
    public String getOtherDataTypes() {
        return otherDataTypes;
    }

    /**
     * Getter for {@link #dataReproducibility}.
     *
     * @return dataReproducibility
     */
    public String getDataReproducibility() {
        return dataReproducibility;
    }

    /**
     * Getter for {@link #usedCollectionModes}.
     *
     * @return usedCollectionModes
     */
    public List<Integer> getUsedCollectionModes() {
        return usedCollectionModes;
    }

    /**
     * Getter for {@link #otherCMIP}.
     *
     * @return otherCMIP
     */
    public String getOtherCMIP() {
        return otherCMIP;
    }

    /**
     * Getter for {@link #otherCMINP}.
     *
     * @return otherCMINP
     */
    public String getOtherCMINP() {
        return otherCMINP;
    }

    /**
     * Getter for {@link #measOccasions}.
     *
     * @return measOccasions
     */
    public String getMeasOccasions() {
        return measOccasions;
    }

    /**
     * Getter for {@link #reliabilityTraining}.
     *
     * @return reliabilityTraining
     */
    public String getReliabilityTraining() {
        return reliabilityTraining;
    }

    /**
     * Getter for {@link #multipleMeasurements}.
     *
     * @return multipleMeasurements
     */
    public String getMultipleMeasurements() {
        return multipleMeasurements;
    }

    /**
     * Getter for {@link #qualitityOther}.
     *
     * @return qualitityOther
     */
    public String getQualitityOther() {
        return qualitityOther;
    }

    /**
     * Getter for {@link #fileFormat}.
     *
     * @return fileFormat
     */
    public String getFileFormat() {
        return fileFormat;
    }

    /**
     * Checks if is {@link #workingCopy}.
     *
     * @return true, if is working copy
     */
    public boolean isWorkingCopy() {
        return workingCopy;
    }

    /**
     * Checks if is {@link #goodScientific}.
     *
     * @return true, if is good scientific
     */
    public boolean isGoodScientific() {
        return goodScientific;
    }

    /**
     * Checks if is {@link #subsequentUse}.
     *
     * @return true, if is subsequent use
     */
    public boolean isSubsequentUse() {
        return subsequentUse;
    }

    /**
     * Checks if is {@link #requirements}.
     *
     * @return true, if is requirements
     */
    public boolean isRequirements() {
        return requirements;
    }

    /**
     * Checks if is {@link #documentation}.
     *
     * @return true, if is documentation
     */
    public boolean isDocumentation() {
        return documentation;
    }

    /**
     * Checks if is {@link #dataSelection}.
     *
     * @return true, if is data selection
     */
    public boolean isDataSelection() {
        return dataSelection;
    }

    /**
     * Getter for {@link #selectionTime}.
     *
     * @return selectionTime
     */
    public String getSelectionTime() {
        return selectionTime;
    }

    /**
     * Getter for {@link #selectionResp}.
     *
     * @return selectionResp
     */
    public String getSelectionResp() {
        return selectionResp;
    }

    /**
     * Getter for {@link #storageDuration}.
     *
     * @return storageDuration
     */
    public String getStorageDuration() {
        return storageDuration;
    }

    /**
     * Getter for {@link #deleteProcedure}.
     *
     * @return deleteProcedure
     */
    public String getDeleteProcedure() {
        return deleteProcedure;
    }

    /**
     * Getter for {@link #selectedMetaPurposes}.
     *
     * @return selectedMetaPurposes
     */
    public List<Integer> getSelectedMetaPurposes() {
        return selectedMetaPurposes;
    }

    /**
     * Getter for {@link #metaDescription}.
     *
     * @return metaDescription
     */
    public String getMetaDescription() {
        return metaDescription;
    }

    /**
     * Getter for {@link #metaFramework}.
     *
     * @return metaFramework
     */
    public String getMetaFramework() {
        return metaFramework;
    }

    /**
     * Getter for {@link #metaGeneration}.
     *
     * @return metaGeneration
     */
    public String getMetaGeneration() {
        return metaGeneration;
    }

    /**
     * Getter for {@link #metaMonitor}.
     *
     * @return metaMonitor
     */
    public String getMetaMonitor() {
        return metaMonitor;
    }

    /**
     * Getter for {@link #metaFormat}.
     *
     * @return metaFormat
     */
    public String getMetaFormat() {
        return metaFormat;
    }

    /**
     * Checks if is {@link #releaseObligation}.
     *
     * @return true, if is release obligation
     */
    public boolean isReleaseObligation() {
        return releaseObligation;
    }

    /**
     * Checks if is {@link #searchableData}.
     *
     * @return true, if is searchable data
     */
    public String getSearchableData() {
        return searchableData;
    }

    /**
     * Getter for {@link #expectedUsage}.
     *
     * @return expectedUsage
     */
    public String getExpectedUsage() {
        return expectedUsage;
    }

    /**
     * Getter for {@link #publStrategy}.
     *
     * @return publStrategy
     */
    public String getPublStrategy() {
        return publStrategy;
    }

    /**
     * Getter for {@link #accessReasonAuthor}.
     *
     * @return accessReasonAuthor
     */
    public String getAccessReasonAuthor() {
        return accessReasonAuthor;
    }

    /**
     * Getter for {@link #noAccessReason}.
     *
     * @return noAccessReason
     */
    public String getNoAccessReason() {
        return noAccessReason;
    }

    /**
     * Getter for {@link #noAccessReasonOther}.
     *
     * @return noAccessReasonOther
     */
    public String getNoAccessReasonOther() {
        return noAccessReasonOther;
    }

    /**
     * Getter for {@link #depositName}.
     *
     * @return depositName
     */
    public String getDepositName() {
        return depositName;
    }

    /**
     * Getter for {@link #transferTime}.
     *
     * @return transferTime
     */
    public String getTransferTime() {
        return transferTime;
    }

    /**
     * Getter for {@link #sensitiveData}.
     *
     * @return sensitiveData
     */
    public String getSensitiveData() {
        return sensitiveData;
    }

    /**
     * Getter for {@link #initialUsage}.
     *
     * @return initialUsage
     */
    public String getInitialUsage() {
        return initialUsage;
    }

    /**
     * Getter for {@link #usageRestriction}.
     *
     * @return usageRestriction
     */
    public String getUsageRestriction() {
        return usageRestriction;
    }

    /**
     * Checks if is {@link #accessCosts}.
     *
     * @return true, if is access costs
     */
    public boolean isAccessCosts() {
        return accessCosts;
    }

    /**
     * Checks if is {@link #clarifiedRights}.
     *
     * @return true, if is clarified rights
     */
    public boolean isClarifiedRights() {
        return clarifiedRights;
    }

    /**
     * Checks if is {@link #acquisitionAgreement}.
     *
     * @return true, if is acquisition agreement
     */
    public boolean isAcquisitionAgreement() {
        return acquisitionAgreement;
    }

    /**
     * Getter for {@link #usedPID}.
     *
     * @return usedPID
     */
    public String getUsedPID() {
        return usedPID;
    }

    /**
     * Getter for {@link #usedPIDTxt}.
     *
     * @return usedPIDTxt
     */
    public String getUsedPIDTxt() {
        return usedPIDTxt;
    }

    /**
     * Getter for {@link #storageResponsible}.
     *
     * @return storageResponsible
     */
    public String getStorageResponsible() {
        return storageResponsible;
    }

    /**
     * Getter for {@link #namingCon}.
     *
     * @return storageTechnologies
     */
    public String getNamingCon() {
        return namingCon;
    }

    /**
     * Getter for {@link #storagePlaces}.
     *
     * @return storagePlaces
     */
    public String getStoragePlaces() {
        return storagePlaces;
    }

    /**
     * Getter for {@link #storageBackups}.
     *
     * @return storageBackups
     */
    public String getStorageBackups() {
        return storageBackups;
    }

    /**
     * Getter for {@link #storageTransfer}.
     *
     * @return storageTransfer
     */
    public String getStorageTransfer() {
        return storageTransfer;
    }

    /**
     * Getter for {@link #storageExpectedSize}.
     *
     * @return storageExpectedSize
     */
    public String getStorageExpectedSize() {
        return storageExpectedSize;
    }

    /**
     * Checks if is {@link #storageRequirements}.
     *
     * @return true, if is storage requirements
     */
    public boolean isStorageRequirements() {
        return storageRequirements;
    }

    /**
     * Getter for {@link #storageRequirementsTxt}.
     *
     * @return storageRequirementsTxt
     */
    public String getStorageRequirementsTxt() {
        return storageRequirementsTxt;
    }

    /**
     * Checks if is {@link #storageSuccession}.
     *
     * @return true, if is storage succession
     */
    public boolean isStorageSuccession() {
        return storageSuccession;
    }

    /**
     * Getter for {@link #storageSuccessionTxt}.
     *
     * @return storageSuccessionTxt
     */
    public String getStorageSuccessionTxt() {
        return storageSuccessionTxt;
    }

    /**
     * Getter for {@link #frameworkNationality}.
     *
     * @return frameworkNationality
     */
    public String getFrameworkNationality() {
        return frameworkNationality;
    }

    /**
     * Getter for {@link #frameworkNationalityTxt}.
     *
     * @return frameworkNationalityTxt
     */
    public String getFrameworkNationalityTxt() {
        return frameworkNationalityTxt;
    }

    /**
     * Getter for {@link #responsibleUnit}.
     *
     * @return responsibleUnit
     */
    public String getResponsibleUnit() {
        return responsibleUnit;
    }

    /**
     * Getter for {@link #involvedInstitutions}.
     *
     * @return involvedInstitutions
     */
    public String getInvolvedInstitutions() {
        return involvedInstitutions;
    }

    /**
     * Checks if is {@link #involvedInformed}.
     *
     * @return true, if is involved informed
     */
    public boolean isInvolvedInformed() {
        return involvedInformed;
    }

    /**
     * Checks if is {@link #contributionsDefined}.
     *
     * @return true, if is contributions defined
     */
    public boolean isContributionsDefined() {
        return contributionsDefined;
    }

    /**
     * Getter for {@link #contributionsDefinedTxt}.
     *
     * @return contributionsDefinedTxt
     */
    public String getContributionsDefinedTxt() {
        return contributionsDefinedTxt;
    }

    /**
     * Checks if is {@link #givenConsent}.
     *
     * @return true, if is given consent
     */
    public boolean isGivenConsent() {
        return givenConsent;
    }

    /**
     * Checks if is {@link #managementWorkflow}.
     *
     * @return true, if is management workflow
     */
    public boolean isManagementWorkflow() {
        return managementWorkflow;
    }

    /**
     * Getter for {@link #managementWorkflowTxt}.
     *
     * @return managementWorkflowTxt
     */
    public String getManagementWorkflowTxt() {
        return managementWorkflowTxt;
    }

    /**
     * Checks if is {@link #staffDescription}.
     *
     * @return true, if is staff description
     */
    public boolean isStaffDescription() {
        return staffDescription;
    }

    /**
     * Getter for {@link #staffDescriptionTxt}.
     *
     * @return staffDescriptionTxt
     */
    public String getStaffDescriptionTxt() {
        return staffDescriptionTxt;
    }

    /**
     * Getter for {@link #funderRequirements}.
     *
     * @return funderRequirements
     */
    public String getFunderRequirements() {
        return funderRequirements;
    }

    /**
     * Getter for {@link #planningAdherence}.
     *
     * @return planningAdherence
     */
    public String getPlanningAdherence() {
        return planningAdherence;
    }

    /**
     * Checks if is {@link #dataProtection}.
     *
     * @return true, if is data protection
     */
    public boolean isDataProtection() {
        return dataProtection;
    }

    /**
     * Getter for {@link #protectionRequirements}.
     *
     * @return protectionRequirements
     */
    public String getProtectionRequirements() {
        return protectionRequirements;
    }

    /**
     * Checks if is {@link #consentObtained}.
     *
     * @return true, if is consent obtained
     */
    public boolean isConsentObtained() {
        return consentObtained;
    }

    /**
     * Getter for {@link #consentObtainedTxt}.
     *
     * @return consentObtainedTxt
     */
    public String getConsentObtainedTxt() {
        return consentObtainedTxt;
    }

    /**
     * Checks if is {@link #sharingConsidered}.
     *
     * @return true, if is sharing considered
     */
    public boolean isSharingConsidered() {
        return sharingConsidered;
    }

    /**
     * Checks if is {@link #irbApproval}.
     *
     * @return true, if is irb approval
     */
    public boolean isIrbApproval() {
        return irbApproval;
    }

    /**
     * Getter for {@link #irbApprovalTxt}.
     *
     * @return irbApprovalTxt
     */
    public String getIrbApprovalTxt() {
        return irbApprovalTxt;
    }

    /**
     * Checks if is {@link #sensitiveDataIncluded}.
     *
     * @return true, if is sensitive data included
     */
    public boolean isSensitiveDataIncluded() {
        return sensitiveDataIncluded;
    }

    /**
     * Getter for {@link #sensitiveDataIncludedTxt}.
     *
     * @return sensitiveDataIncludedTxt
     */
    public String getSensitiveDataIncludedTxt() {
        return sensitiveDataIncludedTxt;
    }

    /**
     * Checks if is {@link #externalCopyright}.
     *
     * @return true, if is external copyright
     */
    public boolean isExternalCopyright() {
        return externalCopyright;
    }

    /**
     * Getter for {@link #externalCopyrightTxt}.
     *
     * @return externalCopyrightTxt
     */
    public String getExternalCopyrightTxt() {
        return externalCopyrightTxt;
    }

    /**
     * Checks if is {@link #internalCopyright}.
     *
     * @return true, if is internal copyright
     */
    public boolean isInternalCopyright() {
        return internalCopyright;
    }

    /**
     * Getter for {@link #internalCopyrightTxt}.
     *
     * @return internalCopyrightTxt
     */
    public String getInternalCopyrightTxt() {
        return internalCopyrightTxt;
    }

    /**
     * Getter for {@link #specificCosts}.
     *
     * @return specificCosts
     */
    public String getSpecificCosts() {
        return specificCosts;
    }

    /**
     * Getter for {@link #specificCostsTxt}.
     *
     * @return specificCostsTxt
     */
    public String getSpecificCostsTxt() {
        return specificCostsTxt;
    }

    /**
     * Getter for {@link #bearCost}.
     *
     * @return bearCost
     */
    public String getBearCost() {
        return bearCost;
    }

    /**
     * Setter for {@link #id}.
     *
     * @param id -> this.id
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * Setter for {@link #duration}.
     *
     * @param duration -> this.duration
     */
    public void setDuration(String duration) {
        if (!Objects.equals(this.duration, duration)) {
            this.duration = duration;
        }
    }

    /**
     * Setter for {@link #organizations}.
     *
     * @param organizations -> this.organizations
     */
    public void setOrganizations(String organizations) {
        if (!Objects.equals(this.organizations, organizations)) {
            this.organizations = organizations;
        }
    }

    /**
     * Setter for {@link #planAims}.
     *
     * @param planAims -> this.planAims
     */
    public void setPlanAims(String planAims) {
        if (!Objects.equals(this.planAims, planAims)) {
            this.planAims = planAims;
        }
    }

    /**
     * Setter for {@link #existingData}.
     *
     * @param existingData -> this.existingData
     */
    public void setExistingData(String existingData) {
        if (!Objects.equals(this.existingData, existingData)) {
            this.existingData = existingData;

        }
    }

    /**
     * Setter for {@link #dataCitation}.
     *
     * @param dataCitation -> this.dataCitation
     */
    public void setDataCitation(String dataCitation) {
        if (!Objects.equals(this.dataCitation, dataCitation)) {
            this.dataCitation = dataCitation;

        }
    }

    /**
     * Setter for {@link #existingDataRelevance}.
     *
     * @param existingDataRelevance -> this.existingDataRelevance
     */
    public void setExistingDataRelevance(String existingDataRelevance) {
        if (!Objects.equals(this.existingDataRelevance, existingDataRelevance)) {
            this.existingDataRelevance = existingDataRelevance;

        }

    }

    /**
     * Setter for {@link #existingDataIntegration}.
     *
     * @param existingDataIntegration -> this.existingDataIntegration
     */
    public void setExistingDataIntegration(String existingDataIntegration) {
        if (!Objects.equals(this.existingDataIntegration, existingDataIntegration)) {
            this.existingDataIntegration = existingDataIntegration;

        }
    }

    /**
     * Setter for {@link #usedDataTypes}.
     *
     * @param usedDataTypes -> this.usedDataTypes
     */
    public void setUsedDataTypes(List<Integer> usedDataTypes) {
        if ((this.usedDataTypes != null && this.usedDataTypes.size() > 0) || (usedDataTypes != null && usedDataTypes.size() > 0))
            if (!ListUtil.equalsWithoutOrder(this.usedDataTypes, usedDataTypes)) {
                this.usedDataTypes = usedDataTypes;

            }
    }

    /**
     * Setter for {@link #otherDataTypes}.
     *
     * @param otherDataTypes -> this.otherDataTypes
     */
    public void setOtherDataTypes(String otherDataTypes) {
        if (!Objects.equals(this.otherDataTypes, otherDataTypes)) {
            this.otherDataTypes = otherDataTypes;

        }

    }

    /**
     * Setter for {@link #dataReproducibility}.
     *
     * @param dataReproducibility -> this.dataReproducibility
     */
    public void setDataReproducibility(String dataReproducibility) {
        if (!Objects.equals(this.dataReproducibility, dataReproducibility)) {
            this.dataReproducibility = dataReproducibility;

        }

    }

    /**
     * Setter for {@link #usedCollectionModes}.
     *
     * @param usedCollectionModes -> this.usedCollectionModes
     */
    public void setUsedCollectionModes(List<Integer> usedCollectionModes) {
        if ((this.usedCollectionModes != null && this.usedCollectionModes.size() > 0) || (usedCollectionModes != null && usedCollectionModes.size() > 0))
            if (!ListUtil.equalsWithoutOrder(this.usedCollectionModes, usedCollectionModes)) {
                this.usedCollectionModes = usedCollectionModes;

            }
    }

    /**
     * Setter for {@link #otherCMIP}.
     *
     * @param otherCMIP -> this.otherCMIP
     */
    public void setOtherCMIP(String otherCMIP) {
        if (!Objects.equals(this.otherCMIP, otherCMIP)) {
            this.otherCMIP = otherCMIP;

        }

    }

    /**
     * Setter for {@link #otherCMINP}.
     *
     * @param otherCMINP -> this.otherCMINP
     */
    public void setOtherCMINP(String otherCMINP) {
        if (!Objects.equals(this.otherCMINP, otherCMINP)) {
            this.otherCMINP = otherCMINP;

        }

    }

    /**
     * Setter for {@link #measOccasions}.
     *
     * @param measOccasions -> this.measOccasions
     */
    public void setMeasOccasions(String measOccasions) {
        if (!Objects.equals(this.measOccasions, measOccasions)) {
            this.measOccasions = measOccasions;

        }

    }

    /**
     * Setter for {@link #reliabilityTraining}.
     *
     * @param reliabilityTraining -> this.reliabilityTraining
     */
    public void setReliabilityTraining(String reliabilityTraining) {
        if (!Objects.equals(this.reliabilityTraining, reliabilityTraining)) {
            this.reliabilityTraining = reliabilityTraining;

        }

    }

    /**
     * Setter for {@link #multipleMeasurements}.
     *
     * @param multipleMeasurements -> this.multipleMeasurements
     */
    public void setMultipleMeasurements(String multipleMeasurements) {
        if (!Objects.equals(this.multipleMeasurements, multipleMeasurements)) {
            this.multipleMeasurements = multipleMeasurements;

        }

    }

    /**
     * Setter for {@link #qualitityOther}.
     *
     * @param qualitityOther -> this.qualitityOther
     */
    public void setQualitityOther(String qualitityOther) {
        if (!Objects.equals(this.qualitityOther, qualitityOther)) {
            this.qualitityOther = qualitityOther;

        }

    }

    /**
     * Setter for {@link #fileFormat}.
     *
     * @param fileFormat -> this.fileFormat
     */
    public void setFileFormat(String fileFormat) {
        if (!Objects.equals(this.fileFormat, fileFormat)) {
            this.fileFormat = fileFormat;

        }

    }

    /**
     * Setter for {@link #workingCopy}.
     *
     * @param workingCopy -> this.workingCopy
     */
    public void setWorkingCopy(boolean workingCopy) {
        if (!Objects.equals(this.workingCopy, workingCopy)) {
            this.workingCopy = workingCopy;

        }

    }

    /**
     * Setter for {@link #goodScientific}.
     *
     * @param goodScientific -> this.goodScientific
     */
    public void setGoodScientific(boolean goodScientific) {
        if (!Objects.equals(this.goodScientific, goodScientific)) {
            this.goodScientific = goodScientific;

        }

    }

    /**
     * Setter for {@link #subsequentUse}.
     *
     * @param subsequentUse -> this.subsequentUse
     */
    public void setSubsequentUse(boolean subsequentUse) {
        if (!Objects.equals(this.subsequentUse, subsequentUse)) {
            this.subsequentUse = subsequentUse;

        }

    }

    /**
     * Setter for {@link #requirements}.
     *
     * @param requirements -> this.requirements
     */
    public void setRequirements(boolean requirements) {
        if (!Objects.equals(this.requirements, requirements)) {
            this.requirements = requirements;

        }

    }

    /**
     * Setter for {@link #documentation}.
     *
     * @param documentation -> this.documentation
     */
    public void setDocumentation(boolean documentation) {
        if (!Objects.equals(this.documentation, documentation)) {
            this.documentation = documentation;

        }

    }

    /**
     * Setter for {@link #dataSelection}.
     *
     * @param dataSelection -> this.dataSelection
     */
    public void setDataSelection(boolean dataSelection) {
        if (!Objects.equals(this.dataSelection, dataSelection)) {
            this.dataSelection = dataSelection;

        }

    }

    /**
     * Setter for {@link #selectionTime}.
     *
     * @param selectionTime -> this.selectionTime
     */
    public void setSelectionTime(String selectionTime) {
        if (!Objects.equals(this.selectionTime, selectionTime)) {
            this.selectionTime = selectionTime;

        }

    }

    /**
     * Setter for {@link #selectionResp}.
     *
     * @param selectionResp -> this.selectionResp
     */
    public void setSelectionResp(String selectionResp) {
        if (!Objects.equals(this.selectionResp, selectionResp)) {
            this.selectionResp = selectionResp;

        }

    }

    /**
     * Setter for {@link #storageDuration}.
     *
     * @param storageDuration -> this.storageDuration
     */
    public void setStorageDuration(String storageDuration) {
        if (!Objects.equals(this.storageDuration, storageDuration)) {
            this.storageDuration = storageDuration;

        }

    }

    /**
     * Setter for {@link #deleteProcedure}.
     *
     * @param deleteProcedure -> this.deleteProcedure
     */
    public void setDeleteProcedure(String deleteProcedure) {
        if (!Objects.equals(this.deleteProcedure, deleteProcedure)) {
            this.deleteProcedure = deleteProcedure;

        }

    }

    /**
     * Setter for {@link #selectedMetaPurposes}.
     *
     * @param selectedMetaPurposes -> this.selectedMetaPurposes
     */
    public void setSelectedMetaPurposes(List<Integer> selectedMetaPurposes) {
        if ((this.selectedMetaPurposes != null && this.selectedMetaPurposes.size() > 0) || (selectedMetaPurposes != null && selectedMetaPurposes.size() > 0))
            if (!ListUtil.equalsWithoutOrder(this.selectedMetaPurposes, selectedMetaPurposes)) {
                this.selectedMetaPurposes = selectedMetaPurposes;

            }
    }

    /**
     * Setter for {@link #metaDescription}.
     *
     * @param metaDescription -> this.metaDescription
     */
    public void setMetaDescription(String metaDescription) {
        if (!Objects.equals(this.metaDescription, metaDescription)) {
            this.metaDescription = metaDescription;

        }
    }

    /**
     * Setter for {@link #metaFramework}.
     *
     * @param metaFramework -> this.metaFramework
     */
    public void setMetaFramework(String metaFramework) {
        if (!Objects.equals(this.metaFramework, metaFramework)) {
            this.metaFramework = metaFramework;

        }
    }

    /**
     * Setter for {@link #metaGeneration}.
     *
     * @param metaGeneration -> this.metaGeneration
     */
    public void setMetaGeneration(String metaGeneration) {
        if (!Objects.equals(this.metaGeneration, metaGeneration)) {
            this.metaGeneration = metaGeneration;

        }
    }

    /**
     * Setter for {@link #metaMonitor}.
     *
     * @param metaMonitor -> this.metaMonitor
     */
    public void setMetaMonitor(String metaMonitor) {
        if (!Objects.equals(this.metaMonitor, metaMonitor)) {
            this.metaMonitor = metaMonitor;

        }

    }

    /**
     * Setter for {@link #metaFormat}.
     *
     * @param metaFormat -> this.metaFormat
     */
    public void setMetaFormat(String metaFormat) {
        if (!Objects.equals(this.metaFormat, metaFormat)) {
            this.metaFormat = metaFormat;

        }
    }

    /**
     * Setter for {@link #releaseObligation}.
     *
     * @param releaseObligation -> this.releaseObligation
     */
    public void setReleaseObligation(boolean releaseObligation) {
        if (!Objects.equals(this.releaseObligation, releaseObligation)) {
            this.releaseObligation = releaseObligation;

        }
    }

    /**
     * Setter for {@link #searchableData}.
     *
     * @param searchableData -> this.searchableData
     */
    public void setSearchableData(String searchableData) {
        if (!Objects.equals(this.searchableData, searchableData)) {
            this.searchableData = searchableData;

        }
    }

    /**
     * Setter for {@link #expectedUsage}.
     *
     * @param expectedUsage -> this.expectedUsage
     */
    public void setExpectedUsage(String expectedUsage) {
        if (!Objects.equals(this.expectedUsage, expectedUsage)) {
            this.expectedUsage = expectedUsage;

        }
    }

    /**
     * Setter for {@link #publStrategy}.
     *
     * @param publStrategy -> this.publStrategy
     */
    public void setPublStrategy(String publStrategy) {
        if (!Objects.equals(this.publStrategy, publStrategy)) {
            this.publStrategy = publStrategy;

        }
    }

    /**
     * Setter for {@link #accessReasonAuthor}.
     *
     * @param accessReasonAuthor -> this.accessReasonAuthor
     */
    public void setAccessReasonAuthor(String accessReasonAuthor) {
        if (!Objects.equals(this.accessReasonAuthor, accessReasonAuthor)) {
            this.accessReasonAuthor = accessReasonAuthor;

        }
    }

    /**
     * Setter for {@link #noAccessReason}.
     *
     * @param noAccessReason -> this.noAccessReason
     */
    public void setNoAccessReason(String noAccessReason) {
        if (!Objects.equals(this.noAccessReason, noAccessReason)) {
            this.noAccessReason = noAccessReason;

        }
    }

    /**
     * Setter for {@link #noAccessReasonOther}.
     *
     * @param noAccessReasonOther -> this.noAccessReasonOther
     */
    public void setNoAccessReasonOther(String noAccessReasonOther) {
        if (!Objects.equals(this.noAccessReasonOther, noAccessReasonOther)) {
            this.noAccessReasonOther = noAccessReasonOther;

        }

    }

    /**
     * Setter for {@link #depositName}.
     *
     * @param depositName -> this.depositName
     */
    public void setDepositName(String depositName) {
        if (!Objects.equals(this.depositName, depositName)) {
            this.depositName = depositName;

        }
    }

    /**
     * Setter for {@link #transferTime}.
     *
     * @param transferTime -> this.transferTime
     */
    public void setTransferTime(String transferTime) {
        if (!Objects.equals(this.transferTime, transferTime)) {
            this.transferTime = transferTime;

        }
    }

    /**
     * Setter for {@link #sensitiveData}.
     *
     * @param sensitiveData -> this.sensitiveData
     */
    public void setSensitiveData(String sensitiveData) {
        if (!Objects.equals(this.sensitiveData, sensitiveData)) {
            this.sensitiveData = sensitiveData;

        }
    }

    /**
     * Setter for {@link #initialUsage}.
     *
     * @param initialUsage -> this.initialUsage
     */
    public void setInitialUsage(String initialUsage) {
        if (!Objects.equals(this.initialUsage, initialUsage)) {
            this.initialUsage = initialUsage;

        }
    }

    /**
     * Setter for {@link #usageRestriction}.
     *
     * @param usageRestriction -> this.usageRestriction
     */
    public void setUsageRestriction(String usageRestriction) {
        if (!Objects.equals(this.usageRestriction, usageRestriction)) {
            this.usageRestriction = usageRestriction;

        }
    }

    /**
     * Setter for {@link #accessCosts}.
     *
     * @param accessCosts -> this.accessCosts
     */
    public void setAccessCosts(boolean accessCosts) {
        if (!Objects.equals(this.accessCosts, accessCosts)) {
            this.accessCosts = accessCosts;

        }
    }

    /**
     * Setter for {@link #clarifiedRights}.
     *
     * @param clarifiedRights -> this.clarifiedRights
     */
    public void setClarifiedRights(boolean clarifiedRights) {
        if (!Objects.equals(this.clarifiedRights, clarifiedRights)) {
            this.clarifiedRights = clarifiedRights;

        }
    }

    /**
     * Setter for {@link #acquisitionAgreement}.
     *
     * @param acquisitionAgreement -> this.acquisitionAgreement
     */
    public void setAcquisitionAgreement(boolean acquisitionAgreement) {
        if (!Objects.equals(this.acquisitionAgreement, acquisitionAgreement)) {
            this.acquisitionAgreement = acquisitionAgreement;

        }
    }

    /**
     * Setter for {@link #usedPID}.
     *
     * @param usedPID -> this.usedPID
     */
    public void setUsedPID(String usedPID) {
        if (!Objects.equals(this.usedPID, usedPID)) {
            this.usedPID = usedPID;

        }
    }

    /**
     * Setter for {@link #usedPIDTxt}.
     *
     * @param usedPIDTxt -> this.usedPIDTxt
     */
    public void setUsedPIDTxt(String usedPIDTxt) {
        if (!Objects.equals(this.usedPIDTxt, usedPIDTxt)) {
            this.usedPIDTxt = usedPIDTxt;

        }
    }

    /**
     * Setter for {@link #storageResponsible}.
     *
     * @param storageResponsible -> this.storageResponsible
     */
    public void setStorageResponsible(String storageResponsible) {
        if (!Objects.equals(this.storageResponsible, storageResponsible)) {
            this.storageResponsible = storageResponsible;

        }
    }

    /**
     * Setter for {@link #namingCon}.
     *
     * @param namingCon -> this.storageTechnologies
     */
    public void setNamingCon(String namingCon) {
        if (!Objects.equals(this.namingCon, namingCon)) {
            this.namingCon = namingCon;

        }

    }

    /**
     * Setter for {@link #storagePlaces}.
     *
     * @param storagePlaces -> this.storagePlaces
     */
    public void setStoragePlaces(String storagePlaces) {
        if (!Objects.equals(this.storagePlaces, storagePlaces)) {
            this.storagePlaces = storagePlaces;

        }
    }

    /**
     * Setter for {@link #storageBackups}.
     *
     * @param storageBackups -> this.storageBackups
     */
    public void setStorageBackups(String storageBackups) {
        if (!Objects.equals(this.storageBackups, storageBackups)) {
            this.storageBackups = storageBackups;

        }
    }

    /**
     * Setter for {@link #storageTransfer}.
     *
     * @param storageTransfer -> this.storageTransfer
     */
    public void setStorageTransfer(String storageTransfer) {
        if (!Objects.equals(this.storageTransfer, storageTransfer)) {
            this.storageTransfer = storageTransfer;

        }
    }

    /**
     * Setter for {@link #storageExpectedSize}.
     *
     * @param storageExpectedSize -> this.storageExpectedSize
     */
    public void setStorageExpectedSize(String storageExpectedSize) {
        if (!Objects.equals(this.storageExpectedSize, storageExpectedSize)) {
            this.storageExpectedSize = storageExpectedSize;

        }
    }

    /**
     * Setter for {@link #storageRequirements}.
     *
     * @param storageRequirements -> this.storageRequirements
     */
    public void setStorageRequirements(boolean storageRequirements) {
        if (!Objects.equals(this.storageRequirements, storageRequirements)) {
            this.storageRequirements = storageRequirements;

        }
    }

    /**
     * Setter for {@link #storageRequirementsTxt}.
     *
     * @param storageRequirementsTxt -> this.storageRequirementsTxt
     */
    public void setStorageRequirementsTxt(String storageRequirementsTxt) {
        if (!Objects.equals(this.storageRequirementsTxt, storageRequirementsTxt)) {
            this.storageRequirementsTxt = storageRequirementsTxt;

        }
    }

    /**
     * Setter for {@link #storageSuccession}.
     *
     * @param storageSuccession -> this.storageSuccession
     */
    public void setStorageSuccession(boolean storageSuccession) {
        if (!Objects.equals(this.storageSuccession, storageSuccession)) {
            this.storageSuccession = storageSuccession;

        }
    }

    /**
     * Setter for {@link #storageSuccessionTxt}.
     *
     * @param storageSuccessionTxt -> this.storageSuccessionTxt
     */
    public void setStorageSuccessionTxt(String storageSuccessionTxt) {
        if (!Objects.equals(this.storageSuccessionTxt, storageSuccessionTxt)) {
            this.storageSuccessionTxt = storageSuccessionTxt;

        }
    }

    /**
     * Setter for {@link #frameworkNationality}.
     *
     * @param frameworkNationality -> this.frameworkNationality
     */
    public void setFrameworkNationality(String frameworkNationality) {
        if (!Objects.equals(this.frameworkNationality, frameworkNationality)) {
            this.frameworkNationality = frameworkNationality;

        }
    }

    /**
     * Setter for {@link #frameworkNationalityTxt}.
     *
     * @param frameworkNationalityTxt -> this.frameworkNationalityTxt
     */
    public void setFrameworkNationalityTxt(String frameworkNationalityTxt) {
        if (!Objects.equals(this.frameworkNationalityTxt, frameworkNationalityTxt)) {
            this.frameworkNationalityTxt = frameworkNationalityTxt;

        }
    }

    /**
     * Setter for {@link #responsibleUnit}.
     *
     * @param responsibleUnit -> this.responsibleUnit
     */
    public void setResponsibleUnit(String responsibleUnit) {
        if (!Objects.equals(this.responsibleUnit, responsibleUnit)) {
            this.responsibleUnit = responsibleUnit;

        }
    }

    /**
     * Setter for {@link #involvedInstitutions}.
     *
     * @param involvedInstitutions -> this.involvedInstitutions
     */
    public void setInvolvedInstitutions(String involvedInstitutions) {
        if (!Objects.equals(this.involvedInstitutions, involvedInstitutions)) {
            this.involvedInstitutions = involvedInstitutions;

        }
    }

    /**
     * Setter for {@link #involvedInformed}.
     *
     * @param involvedInformed -> this.involvedInformed
     */
    public void setInvolvedInformed(boolean involvedInformed) {
        if (!Objects.equals(this.involvedInformed, involvedInformed)) {
            this.involvedInformed = involvedInformed;

        }
    }

    /**
     * Setter for {@link #contributionsDefined}.
     *
     * @param contributionsDefined -> this.contributionsDefined
     */
    public void setContributionsDefined(boolean contributionsDefined) {
        if (!Objects.equals(this.contributionsDefined, contributionsDefined)) {
            this.contributionsDefined = contributionsDefined;

        }
    }

    /**
     * Setter for {@link #contributionsDefinedTxt}.
     *
     * @param contributionsDefinedTxt -> this.contributionsDefinedTxt
     */
    public void setContributionsDefinedTxt(String contributionsDefinedTxt) {
        if (!Objects.equals(this.contributionsDefinedTxt, contributionsDefinedTxt)) {
            this.contributionsDefinedTxt = contributionsDefinedTxt;

        }
    }

    /**
     * Setter for {@link #givenConsent}.
     *
     * @param givenConsent -> this.givenConsent
     */
    public void setGivenConsent(boolean givenConsent) {
        if (!Objects.equals(this.givenConsent, givenConsent)) {
            this.givenConsent = givenConsent;

        }
    }

    /**
     * Setter for {@link #managementWorkflow}.
     *
     * @param managementWorkflow -> this.managementWorkflow
     */
    public void setManagementWorkflow(boolean managementWorkflow) {
        if (!Objects.equals(this.managementWorkflow, managementWorkflow)) {
            this.managementWorkflow = managementWorkflow;

        }
    }

    /**
     * Setter for {@link #managementWorkflowTxt}.
     *
     * @param managementWorkflowTxt -> this.managementWorkflowTxt
     */
    public void setManagementWorkflowTxt(String managementWorkflowTxt) {
        if (!Objects.equals(this.managementWorkflowTxt, managementWorkflowTxt)) {
            this.managementWorkflowTxt = managementWorkflowTxt;

        }
    }

    /**
     * Setter for {@link #staffDescription}.
     *
     * @param staffDescription -> this.staffDescription
     */
    public void setStaffDescription(boolean staffDescription) {
        if (!Objects.equals(this.staffDescription, staffDescription)) {
            this.staffDescription = staffDescription;

        }
    }

    /**
     * Setter for {@link #staffDescriptionTxt}.
     *
     * @param staffDescriptionTxt -> this.staffDescriptionTxt
     */
    public void setStaffDescriptionTxt(String staffDescriptionTxt) {
        if (!Objects.equals(this.staffDescriptionTxt, staffDescriptionTxt)) {
            this.staffDescriptionTxt = staffDescriptionTxt;

        }
    }

    /**
     * Setter for {@link #funderRequirements}.
     *
     * @param funderRequirements -> this.funderRequirements
     */
    public void setFunderRequirements(String funderRequirements) {
        if (!Objects.equals(this.funderRequirements, funderRequirements)) {
            this.funderRequirements = funderRequirements;

        }
    }

    /**
     * Setter for {@link #planningAdherence}.
     *
     * @param planningAdherence -> this.planningAdherence
     */
    public void setPlanningAdherence(String planningAdherence) {
        if (!Objects.equals(this.planningAdherence, planningAdherence)) {
            this.planningAdherence = planningAdherence;

        }
    }

    /**
     * Setter for {@link #dataProtection}.
     *
     * @param dataProtection -> this.dataProtection
     */
    public void setDataProtection(boolean dataProtection) {
        if (!Objects.equals(this.dataProtection, dataProtection)) {
            this.dataProtection = dataProtection;

        }
    }

    /**
     * Setter for {@link #protectionRequirements}.
     *
     * @param protectionRequirements -> this.protectionRequirements
     */
    public void setProtectionRequirements(String protectionRequirements) {
        if (!Objects.equals(this.protectionRequirements, protectionRequirements)) {
            this.protectionRequirements = protectionRequirements;

        }
    }

    /**
     * Setter for {@link #consentObtained}.
     *
     * @param consentObtained -> this.consentObtained
     */
    public void setConsentObtained(boolean consentObtained) {
        if (!Objects.equals(this.consentObtained, consentObtained)) {
            this.consentObtained = consentObtained;

        }
    }

    /**
     * Setter for {@link #consentObtainedTxt}.
     *
     * @param consentObtainedTxt -> this.consentObtainedTxt
     */
    public void setConsentObtainedTxt(String consentObtainedTxt) {
        if (!Objects.equals(this.consentObtainedTxt, consentObtainedTxt)) {
            this.consentObtainedTxt = consentObtainedTxt;

        }
    }

    /**
     * Setter for {@link #sharingConsidered}.
     *
     * @param sharingConsidered -> this.sharingConsidered
     */
    public void setSharingConsidered(boolean sharingConsidered) {
        if (!Objects.equals(this.sharingConsidered, sharingConsidered)) {
            this.sharingConsidered = sharingConsidered;

        }
    }

    /**
     * Setter for {@link #irbApproval}.
     *
     * @param irbApproval -> this.irbApproval
     */
    public void setIrbApproval(boolean irbApproval) {
        if (!Objects.equals(this.irbApproval, irbApproval)) {
            this.irbApproval = irbApproval;

        }
    }

    /**
     * Setter for {@link #irbApprovalTxt}.
     *
     * @param irbApprovalTxt -> this.irbApprovalTxt
     */
    public void setIrbApprovalTxt(String irbApprovalTxt) {
        if (!Objects.equals(this.irbApprovalTxt, irbApprovalTxt)) {
            this.irbApprovalTxt = irbApprovalTxt;

        }
    }

    /**
     * Setter for {@link #sensitiveDataIncluded}.
     *
     * @param sensitiveDataIncluded -> this.sensitiveDataIncluded
     */
    public void setSensitiveDataIncluded(boolean sensitiveDataIncluded) {
        if (!Objects.equals(this.sensitiveDataIncluded, sensitiveDataIncluded)) {
            this.sensitiveDataIncluded = sensitiveDataIncluded;

        }
    }

    /**
     * Setter for {@link #sensitiveDataIncludedTxt}.
     *
     * @param sensitiveDataIncludedTxt -> this.sensitiveDataIncludedTxt
     */
    public void setSensitiveDataIncludedTxt(String sensitiveDataIncludedTxt) {
        if (!Objects.equals(this.sensitiveDataIncludedTxt, sensitiveDataIncludedTxt)) {
            this.sensitiveDataIncludedTxt = sensitiveDataIncludedTxt;

        }
    }

    /**
     * Setter for {@link #externalCopyright}.
     *
     * @param externalCopyright -> this.externalCopyright
     */
    public void setExternalCopyright(boolean externalCopyright) {
        if (!Objects.equals(this.externalCopyright, externalCopyright)) {
            this.externalCopyright = externalCopyright;

        }
    }

    /**
     * Setter for {@link #externalCopyrightTxt}.
     *
     * @param externalCopyrightTxt -> this.externalCopyrightTxt
     */
    public void setExternalCopyrightTxt(String externalCopyrightTxt) {
        if (!Objects.equals(this.externalCopyrightTxt, externalCopyrightTxt)) {
            this.externalCopyrightTxt = externalCopyrightTxt;

        }
    }

    /**
     * Setter for {@link #internalCopyright}.
     *
     * @param internalCopyright -> this.internalCopyright
     */
    public void setInternalCopyright(boolean internalCopyright) {
        if (!Objects.equals(this.internalCopyright, internalCopyright)) {
            this.internalCopyright = internalCopyright;

        }
    }

    /**
     * Setter for {@link #internalCopyrightTxt}.
     *
     * @param internalCopyrightTxt -> this.internalCopyrightTxt
     */
    public void setInternalCopyrightTxt(String internalCopyrightTxt) {
        if (!Objects.equals(this.internalCopyrightTxt, internalCopyrightTxt)) {
            this.internalCopyrightTxt = internalCopyrightTxt;

        }
    }

    /**
     * Setter for {@link #specificCosts}.
     *
     * @param specificCosts -> this.specificCosts
     */
    public void setSpecificCosts(String specificCosts) {
        if (!Objects.equals(this.specificCosts, specificCosts)) {
            this.specificCosts = specificCosts;

        }
    }

    /**
     * Setter for {@link #specificCostsTxt}.
     *
     * @param specificCostsTxt -> this.specificCostsTxt
     */
    public void setSpecificCostsTxt(String specificCostsTxt) {
        if (!Objects.equals(this.specificCostsTxt, specificCostsTxt)) {
            this.specificCostsTxt = specificCostsTxt;

        }
    }

    /**
     * Setter for {@link #bearCost}.
     *
     * @param bearCost -> this.bearCost
     */
    public void setBearCost(String bearCost) {
        if (!Objects.equals(this.bearCost, bearCost)) {
            this.bearCost = bearCost;

        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DmpDTO dmpDTO = (DmpDTO) o;
        return id == dmpDTO.id &&
                workingCopy == dmpDTO.workingCopy &&
                goodScientific == dmpDTO.goodScientific &&
                subsequentUse == dmpDTO.subsequentUse &&
                requirements == dmpDTO.requirements &&
                documentation == dmpDTO.documentation &&
                dataSelection == dmpDTO.dataSelection &&
                releaseObligation == dmpDTO.releaseObligation &&
                accessCosts == dmpDTO.accessCosts &&
                clarifiedRights == dmpDTO.clarifiedRights &&
                acquisitionAgreement == dmpDTO.acquisitionAgreement &&
                storageRequirements == dmpDTO.storageRequirements &&
                storageSuccession == dmpDTO.storageSuccession &&
                involvedInformed == dmpDTO.involvedInformed &&
                contributionsDefined == dmpDTO.contributionsDefined &&
                givenConsent == dmpDTO.givenConsent &&
                managementWorkflow == dmpDTO.managementWorkflow &&
                staffDescription == dmpDTO.staffDescription &&
                dataProtection == dmpDTO.dataProtection &&
                consentObtained == dmpDTO.consentObtained &&
                sharingConsidered == dmpDTO.sharingConsidered &&
                irbApproval == dmpDTO.irbApproval &&
                sensitiveDataIncluded == dmpDTO.sensitiveDataIncluded &&
                externalCopyright == dmpDTO.externalCopyright &&
                internalCopyright == dmpDTO.internalCopyright &&
                Objects.equals(duration, dmpDTO.duration) &&
                Objects.equals(organizations, dmpDTO.organizations) &&
                Objects.equals(planAims, dmpDTO.planAims) &&
                Objects.equals(existingData, dmpDTO.existingData) &&
                Objects.equals(dataCitation, dmpDTO.dataCitation) &&
                Objects.equals(existingDataRelevance, dmpDTO.existingDataRelevance) &&
                Objects.equals(existingDataIntegration, dmpDTO.existingDataIntegration) &&
                Objects.equals(usedDataTypes, dmpDTO.usedDataTypes) &&
                Objects.equals(otherDataTypes, dmpDTO.otherDataTypes) &&
                Objects.equals(dataReproducibility, dmpDTO.dataReproducibility) &&
                Objects.equals(usedCollectionModes, dmpDTO.usedCollectionModes) &&
                Objects.equals(otherCMIP, dmpDTO.otherCMIP) &&
                Objects.equals(otherCMINP, dmpDTO.otherCMINP) &&
                Objects.equals(measOccasions, dmpDTO.measOccasions) &&
                Objects.equals(reliabilityTraining, dmpDTO.reliabilityTraining) &&
                Objects.equals(multipleMeasurements, dmpDTO.multipleMeasurements) &&
                Objects.equals(qualitityOther, dmpDTO.qualitityOther) &&
                Objects.equals(fileFormat, dmpDTO.fileFormat) &&
                Objects.equals(selectionTime, dmpDTO.selectionTime) &&
                Objects.equals(selectionResp, dmpDTO.selectionResp) &&
                Objects.equals(storageDuration, dmpDTO.storageDuration) &&
                Objects.equals(deleteProcedure, dmpDTO.deleteProcedure) &&
                Objects.equals(selectedMetaPurposes, dmpDTO.selectedMetaPurposes) &&
                Objects.equals(metaDescription, dmpDTO.metaDescription) &&
                Objects.equals(metaFramework, dmpDTO.metaFramework) &&
                Objects.equals(metaGeneration, dmpDTO.metaGeneration) &&
                Objects.equals(metaMonitor, dmpDTO.metaMonitor) &&
                Objects.equals(metaFormat, dmpDTO.metaFormat) &&
                Objects.equals(searchableData, dmpDTO.searchableData) &&
                Objects.equals(expectedUsage, dmpDTO.expectedUsage) &&
                Objects.equals(publStrategy, dmpDTO.publStrategy) &&
                Objects.equals(accessReasonAuthor, dmpDTO.accessReasonAuthor) &&
                Objects.equals(noAccessReason, dmpDTO.noAccessReason) &&
                Objects.equals(noAccessReasonOther, dmpDTO.noAccessReasonOther) &&
                Objects.equals(depositName, dmpDTO.depositName) &&
                Objects.equals(transferTime, dmpDTO.transferTime) &&
                Objects.equals(sensitiveData, dmpDTO.sensitiveData) &&
                Objects.equals(initialUsage, dmpDTO.initialUsage) &&
                Objects.equals(usageRestriction, dmpDTO.usageRestriction) &&
                Objects.equals(usedPID, dmpDTO.usedPID) &&
                Objects.equals(usedPIDTxt, dmpDTO.usedPIDTxt) &&
                Objects.equals(storageResponsible, dmpDTO.storageResponsible) &&
                Objects.equals(namingCon, dmpDTO.namingCon) &&
                Objects.equals(storagePlaces, dmpDTO.storagePlaces) &&
                Objects.equals(storageBackups, dmpDTO.storageBackups) &&
                Objects.equals(storageTransfer, dmpDTO.storageTransfer) &&
                Objects.equals(storageExpectedSize, dmpDTO.storageExpectedSize) &&
                Objects.equals(storageRequirementsTxt, dmpDTO.storageRequirementsTxt) &&
                Objects.equals(storageSuccessionTxt, dmpDTO.storageSuccessionTxt) &&
                Objects.equals(frameworkNationality, dmpDTO.frameworkNationality) &&
                Objects.equals(frameworkNationalityTxt, dmpDTO.frameworkNationalityTxt) &&
                Objects.equals(responsibleUnit, dmpDTO.responsibleUnit) &&
                Objects.equals(involvedInstitutions, dmpDTO.involvedInstitutions) &&
                Objects.equals(contributionsDefinedTxt, dmpDTO.contributionsDefinedTxt) &&
                Objects.equals(managementWorkflowTxt, dmpDTO.managementWorkflowTxt) &&
                Objects.equals(staffDescriptionTxt, dmpDTO.staffDescriptionTxt) &&
                Objects.equals(funderRequirements, dmpDTO.funderRequirements) &&
                Objects.equals(planningAdherence, dmpDTO.planningAdherence) &&
                Objects.equals(protectionRequirements, dmpDTO.protectionRequirements) &&
                Objects.equals(consentObtainedTxt, dmpDTO.consentObtainedTxt) &&
                Objects.equals(irbApprovalTxt, dmpDTO.irbApprovalTxt) &&
                Objects.equals(sensitiveDataIncludedTxt, dmpDTO.sensitiveDataIncludedTxt) &&
                Objects.equals(externalCopyrightTxt, dmpDTO.externalCopyrightTxt) &&
                Objects.equals(internalCopyrightTxt, dmpDTO.internalCopyrightTxt) &&
                Objects.equals(specificCosts, dmpDTO.specificCosts) &&
                Objects.equals(specificCostsTxt, dmpDTO.specificCostsTxt) &&
                Objects.equals(bearCost, dmpDTO.bearCost);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, duration, organizations, planAims, existingData, dataCitation, existingDataRelevance, existingDataIntegration,
                usedDataTypes, otherDataTypes, dataReproducibility, usedCollectionModes, otherCMIP, otherCMINP, measOccasions, reliabilityTraining,
                multipleMeasurements, qualitityOther, fileFormat, workingCopy, goodScientific, subsequentUse, requirements, documentation, dataSelection,
                selectionTime, selectionResp, storageDuration, deleteProcedure, selectedMetaPurposes, metaDescription, metaFramework, metaGeneration,
                metaMonitor, metaFormat, releaseObligation, searchableData, expectedUsage, publStrategy, accessReasonAuthor, noAccessReason, noAccessReasonOther,
                depositName, transferTime, sensitiveData, initialUsage, usageRestriction, accessCosts, clarifiedRights, acquisitionAgreement, usedPID, usedPIDTxt,
                storageResponsible, namingCon, storagePlaces, storageBackups, storageTransfer, storageExpectedSize, storageRequirements, storageRequirementsTxt,
                storageSuccession, storageSuccessionTxt, frameworkNationality, frameworkNationalityTxt, responsibleUnit, involvedInstitutions, involvedInformed,
                contributionsDefined, contributionsDefinedTxt, givenConsent, managementWorkflow, managementWorkflowTxt, staffDescription, staffDescriptionTxt,
                funderRequirements, planningAdherence, dataProtection, protectionRequirements, consentObtained, consentObtainedTxt, sharingConsidered, irbApproval,
                irbApprovalTxt, sensitiveDataIncluded, sensitiveDataIncludedTxt, externalCopyright, externalCopyrightTxt, internalCopyright, internalCopyrightTxt,
                specificCosts, specificCostsTxt, bearCost);
    }

    @Override
    public String toString() {
        return "DmpDTO{" +
                "id=" + id +
                ", duration='" + duration + '\'' +
                ", organizations='" + organizations + '\'' +
                ", planAims='" + planAims + '\'' +
                ", existingData='" + existingData + '\'' +
                ", dataCitation='" + dataCitation + '\'' +
                ", existingDataRelevance='" + existingDataRelevance + '\'' +
                ", existingDataIntegration='" + existingDataIntegration + '\'' +
                ", usedDataTypes=" + usedDataTypes +
                ", otherDataTypes='" + otherDataTypes + '\'' +
                ", dataReproducibility='" + dataReproducibility + '\'' +
                ", usedCollectionModes=" + usedCollectionModes +
                ", otherCMIP='" + otherCMIP + '\'' +
                ", otherCMINP='" + otherCMINP + '\'' +
                ", measOccasions='" + measOccasions + '\'' +
                ", reliabilityTraining='" + reliabilityTraining + '\'' +
                ", multipleMeasurements='" + multipleMeasurements + '\'' +
                ", qualitityOther='" + qualitityOther + '\'' +
                ", fileFormat='" + fileFormat + '\'' +
                ", workingCopy=" + workingCopy +
                ", goodScientific=" + goodScientific +
                ", subsequentUse=" + subsequentUse +
                ", requirements=" + requirements +
                ", documentation=" + documentation +
                ", dataSelection=" + dataSelection +
                ", selectionTime='" + selectionTime + '\'' +
                ", selectionResp='" + selectionResp + '\'' +
                ", storageDuration='" + storageDuration + '\'' +
                ", deleteProcedure='" + deleteProcedure + '\'' +
                ", selectedMetaPurposes=" + selectedMetaPurposes +
                ", metaDescription='" + metaDescription + '\'' +
                ", metaFramework='" + metaFramework + '\'' +
                ", metaGeneration='" + metaGeneration + '\'' +
                ", metaMonitor='" + metaMonitor + '\'' +
                ", metaFormat='" + metaFormat + '\'' +
                ", releaseObligation=" + releaseObligation +
                ", searchableData='" + searchableData + '\'' +
                ", expectedUsage='" + expectedUsage + '\'' +
                ", publStrategy='" + publStrategy + '\'' +
                ", accessReasonAuthor='" + accessReasonAuthor + '\'' +
                ", noAccessReason='" + noAccessReason + '\'' +
                ", noAccessReasonOther='" + noAccessReasonOther + '\'' +
                ", depositName='" + depositName + '\'' +
                ", transferTime='" + transferTime + '\'' +
                ", sensitiveData='" + sensitiveData + '\'' +
                ", initialUsage='" + initialUsage + '\'' +
                ", usageRestriction='" + usageRestriction + '\'' +
                ", accessCosts=" + accessCosts +
                ", clarifiedRights=" + clarifiedRights +
                ", acquisitionAgreement=" + acquisitionAgreement +
                ", usedPID='" + usedPID + '\'' +
                ", usedPIDTxt='" + usedPIDTxt + '\'' +
                ", storageResponsible='" + storageResponsible + '\'' +
                ", namingCon='" + namingCon + '\'' +
                ", storagePlaces='" + storagePlaces + '\'' +
                ", storageBackups='" + storageBackups + '\'' +
                ", storageTransfer='" + storageTransfer + '\'' +
                ", storageExpectedSize='" + storageExpectedSize + '\'' +
                ", storageRequirements=" + storageRequirements +
                ", storageRequirementsTxt='" + storageRequirementsTxt + '\'' +
                ", storageSuccession=" + storageSuccession +
                ", storageSuccessionTxt='" + storageSuccessionTxt + '\'' +
                ", frameworkNationality='" + frameworkNationality + '\'' +
                ", frameworkNationalityTxt='" + frameworkNationalityTxt + '\'' +
                ", responsibleUnit='" + responsibleUnit + '\'' +
                ", involvedInstitutions='" + involvedInstitutions + '\'' +
                ", involvedInformed=" + involvedInformed +
                ", contributionsDefined=" + contributionsDefined +
                ", contributionsDefinedTxt='" + contributionsDefinedTxt + '\'' +
                ", givenConsent=" + givenConsent +
                ", managementWorkflow=" + managementWorkflow +
                ", managementWorkflowTxt='" + managementWorkflowTxt + '\'' +
                ", staffDescription=" + staffDescription +
                ", staffDescriptionTxt='" + staffDescriptionTxt + '\'' +
                ", funderRequirements='" + funderRequirements + '\'' +
                ", planningAdherence='" + planningAdherence + '\'' +
                ", dataProtection=" + dataProtection +
                ", protectionRequirements='" + protectionRequirements + '\'' +
                ", consentObtained=" + consentObtained +
                ", consentObtainedTxt='" + consentObtainedTxt + '\'' +
                ", sharingConsidered=" + sharingConsidered +
                ", irbApproval=" + irbApproval +
                ", irbApprovalTxt='" + irbApprovalTxt + '\'' +
                ", sensitiveDataIncluded=" + sensitiveDataIncluded +
                ", sensitiveDataIncludedTxt='" + sensitiveDataIncludedTxt + '\'' +
                ", externalCopyright=" + externalCopyright +
                ", externalCopyrightTxt='" + externalCopyrightTxt + '\'' +
                ", internalCopyright=" + internalCopyright +
                ", internalCopyrightTxt='" + internalCopyrightTxt + '\'' +
                ", specificCosts='" + specificCosts + '\'' +
                ", specificCostsTxt='" + specificCostsTxt + '\'' +
                ", bearCost='" + bearCost + '\'' +
                '}';
    }
}
