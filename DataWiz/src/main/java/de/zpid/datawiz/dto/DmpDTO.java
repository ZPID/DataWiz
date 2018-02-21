package de.zpid.datawiz.dto;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

import javax.validation.constraints.Size;

import de.zpid.datawiz.util.ListUtil;

/**
 * Data-management-plan data transfer object: Includes all necessary information for the data-management-plan. Some DMP information are saved in the Project
 * information, such as name of the project Please read the metadata excel sheet
 *
 * @author Ronny Boelter
 * @version 1.0
 * @since January 2016
 */
public class DmpDTO implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1989300324143602401L;

	/** The DMP ID - Is similar to the Project ID because only 1 DMP for a project. */
	private long id;

	// ***************** Administrative Data *****************
	/**
	 * group interface for validation - see {@link http://stackoverflow.com/questions/19190592/manually-call-spring-annotation-validation}
	 */
	public interface AdminVal {
	}

	/** DMP04. */
	@Size(min = 0, max = 250, groups = AdminVal.class)
	private String duration;

	/** DMP05. */
	@Size(min = 0, max = 250, groups = AdminVal.class)
	private String organizations;

	/** DMP07. */
	@Size(min = 0, max = 2000, groups = AdminVal.class)
	private String planAims;

	// ***************** Research Data *****************
	/**
	 * group interface for validation - see {@link http://stackoverflow.com/questions/19190592/manually-call-spring-annotation-validation}
	 */
	public interface ResearchVal {
	}

	/** DMP09 : 0 yes, existing data are used/ 1 no data were found/ 2 no search was carried out. */
	private String existingData;

	/** DMP97. */
	@Size(min = 0, max = 1000, groups = ResearchVal.class)
	private String dataCitation;

	/** DMP10. */
	@Size(min = 0, max = 1000, groups = ResearchVal.class)
	private String existingDataRelevance;

	/** DMP11. */
	@Size(min = 0, max = 1000, groups = ResearchVal.class)
	private String existingDataIntegration;

	/** DMP12 META096 Study Metadata. */
	private List<Integer> usedDataTypes;

	/** DMP12 -> selected == other. */
	@Size(min = 0, max = 1000, groups = ResearchVal.class)
	private String otherDataTypes;

	/** DMP13. */
	@Size(min = 0, max = 1000, groups = ResearchVal.class)
	private String dataReproducibility;

	/** DMP14. How will the data be collected or generated? */
	/** DMP14 PsychData - META096 */
	private List<Integer> usedCollectionModes;

	/** DMP87 other Collection Modes with Invest. present */
	@Size(min = 0, max = 1000, groups = ResearchVal.class)
	private String otherCMIP;

	/** DMP87 other Collection Modes with Invest. not present */
	@Size(min = 0, max = 1000, groups = ResearchVal.class)
	private String otherCMINP;

	/** DMP89 Subitem DMP14, PsychData - META097 Study Metadata. */
	@Size(min = 0, max = 1000, groups = ResearchVal.class)
	private String measOccasions;

	/** Subitem DMP16. */
	/** Subitem DMP90, JARS - META131 Study Metadata. */
	@Size(min = 0, max = 1000, groups = ResearchVal.class)
	private String reliabilityTraining;

	/** Subitem DMP91, JARS - META132 Study Metadata. */
	@Size(min = 0, max = 1000, groups = ResearchVal.class)
	private String multipleMeasurements;

	/** Subitem DMP92, Psychdata - META233 Study Metadata. */
	@Size(min = 0, max = 1000, groups = ResearchVal.class)
	private String qualitityOther;

	/** DMP17. */
	@Size(min = 0, max = 1000, groups = ResearchVal.class)
	private String fileFormat;

	/** DMP18 - Why must the data be completely or partially stored?. */
	/** DMP19 - Antwortoption DMP18 */
	private boolean workingCopy;

	/** DMP20 - Antwortoption DMP18. */
	private boolean goodScientific;

	/** DMP21 - Antwortoption DMP18. */
	private boolean subsequentUse;

	/** DMP22 - Antwortoption DMP18. */
	private boolean requirements;

	/** DMP23 - Antwortoption DMP18. */
	private boolean documentation;

	/** DMP86 - Does data selection take place?. */
	private boolean dataSelection;

	/** the next inputs are only important if dataselection == true. */
	/** DMP24 - DMP86 ja */
	@Size(min = 0, max = 500, groups = ResearchVal.class)
	private String selectionTime;

	/** DMP25 - DMP86 ja. */
	@Size(min = 0, max = 500, groups = ResearchVal.class)
	private String selectionResp;

	/** DMP28. */
	@Size(min = 0, max = 500, groups = ResearchVal.class)
	private String storageDuration;

	/** DMP29. */
	@Size(min = 0, max = 1000, groups = ResearchVal.class)
	private String deleteProcedure;

	// ***************** MetaData Data *****************
	/**
	 * group interface for validation - see {@link http://stackoverflow.com/questions/19190592/manually-call-spring-annotation-validation}
	 */
	public interface MetaVal {
	}

	/** DMP31. */
	private List<Integer> selectedMetaPurposes;

	/** DMP32. */
	@Size(min = 0, max = 1000, groups = MetaVal.class)
	private String metaDescription;

	/** DMP33. */
	@Size(min = 0, max = 1000, groups = MetaVal.class)
	private String metaFramework;

	/** DMP34. */
	@Size(min = 0, max = 1000, groups = MetaVal.class)
	private String metaGeneration;

	/** DMP35. */
	@Size(min = 0, max = 1000, groups = MetaVal.class)
	private String metaMonitor;

	/** DMP36. */
	@Size(min = 0, max = 1000, groups = MetaVal.class)
	private String metaFormat;

	// ***************** Data Sharing *****************
	/**
	 * group interface for validation - see {@link http://stackoverflow.com/questions/19190592/manually-call-spring-annotation-validation}
	 */
	public interface SharingVal {
	}

	/** DMP39. */
	private boolean releaseObligation;

	/** DMP42. */
	@Size(min = 0, max = 1000, groups = SharingVal.class)
	private String searchableData;

	/** DMP44. */
	@Size(min = 0, max = 1000, groups = SharingVal.class)
	private String expectedUsage;

	/** DMP43 (select). */
	private String publStrategy;

	/** DMP38 - if data access on demand by author. */
	@Size(min = 0, max = 500, groups = SharingVal.class)
	private String accessReasonAuthor;

	/** DMP38 - if data are not accessible (select). */
	private String noAccessReason;

	/** DMP38 - if data are not accessible - reason == other. */
	@Size(min = 0, max = 500, groups = SharingVal.class)
	private String noAccessReasonOther;

	/** DMP98. */
	@Size(min = 0, max = 1000, groups = SharingVal.class)
	private String depositName;

	// next fields are shown if principleRetain == repository!<
	/** DMP45. */
	@Size(min = 0, max = 1000, groups = SharingVal.class)
	private String transferTime;

	/** DMP46. */
	@Size(min = 0, max = 1000, groups = SharingVal.class)
	private String sensitiveData;

	/** DMP47. */
	@Size(min = 0, max = 1000, groups = SharingVal.class)
	private String initialUsage;

	/** DMP48. */
	@Size(min = 0, max = 500, groups = SharingVal.class)
	private String usageRestriction;

	/** DMP49. */
	private boolean accessCosts;

	/** DMP51. */
	private boolean clarifiedRights;

	/** DMP52. */
	private boolean acquisitionAgreement;

	/** DMP53 (select). */
	private String usedPID;

	/** DMP53 (select)-> other selected. */
	@Size(min = 0, max = 500, groups = SharingVal.class)
	private String usedPIDTxt;

	// ***************** Storage and infrastructure *****************
	/**
	 * group interface for validation - see {@link http://stackoverflow.com/questions/19190592/manually-call-spring-annotation-validation}
	 */
	public interface StorageVal {
	}

	/** DMP54. */
	@Size(min = 0, max = 1000, groups = StorageVal.class)
	private String storageResponsible;

	/** DMP55. */
	@Size(min = 0, max = 1000, groups = StorageVal.class)
	private String namingCon;

	/** DMP56. */
	@Size(min = 0, max = 1000, groups = StorageVal.class)
	private String storagePlaces;

	/** DMP57. */
	@Size(min = 0, max = 1000, groups = StorageVal.class)
	private String storageBackups;

	/** DMP58. */
	@Size(min = 0, max = 1000, groups = StorageVal.class)
	private String storageTransfer;

	/** DMP59. */
	@Size(min = 0, max = 1000, groups = StorageVal.class)
	private String storageExpectedSize;

	/** DMP60. */
	private boolean storageRequirements;

	/** DMP60 -> if "no" selected. */
	@Size(min = 0, max = 1000, groups = StorageVal.class)
	private String storageRequirementsTxt;

	/** DMP61. */
	private boolean storageSuccession;

	/** DMP61 -> if "yes" selected. */
	@Size(min = 0, max = 1000, groups = StorageVal.class)
	private String storageSuccessionTxt;

	// ***************** Organization, management and policies *****************

	/**
	 * group interface for validation - see {@link http://stackoverflow.com/questions/19190592/manually-call-spring-annotation-validation}
	 */
	public interface OrganizationVal {
	}

	/** DMP62. */
	private String frameworkNationality;

	/** DMP62 -> if international specific requirements selected. */
	@Size(min = 0, max = 1000, groups = OrganizationVal.class)
	private String frameworkNationalityTxt;

	/** DMP63. */
	@Size(min = 0, max = 500, groups = OrganizationVal.class)
	private String responsibleUnit;

	/** DMP64. */
	@Size(min = 0, max = 1000, groups = OrganizationVal.class)
	private String involvedInstitutions;

	/** DMP65. */
	private boolean involvedInformed;

	/** DMP93 -> if 65 == 1. */
	private boolean contributionsDefined;

	/** DMP93 if -> 93 == "1". */
	@Size(min = 0, max = 1000, groups = OrganizationVal.class)
	private String contributionsDefinedTxt;

	/** DMP94 -> if 65 == 1. */
	private boolean givenConsent;

	/** DMP66. */
	private boolean managementWorkflow;

	/** DMP66 if -> 66 == "1". */
	@Size(min = 0, max = 1000, groups = OrganizationVal.class)
	private String managementWorkflowTxt;

	/** DMP67. */
	private boolean staffDescription;

	/** DMP67 if -> 67 == "1". */
	@Size(min = 0, max = 1000, groups = OrganizationVal.class)
	private String staffDescriptionTxt;

	/** DMP68. */
	@Size(min = 0, max = 2000, groups = OrganizationVal.class)
	private String funderRequirements;

	/** DMP72. */
	@Size(min = 0, max = 1000, groups = OrganizationVal.class)
	private String planningAdherence;

	// ***************** Ethical and legal aspects *****************
	/**
	 * group interface for validation - see {@link http://stackoverflow.com/questions/19190592/manually-call-spring-annotation-validation}
	 */
	public interface EthicalVal {
	}

	/** DMP73. */
	private boolean dataProtection;

	/** DMP74. -> if DMP73 == true */
	@Size(min = 0, max = 2000, groups = EthicalVal.class)
	private String protectionRequirements;

	/** DMP75. -> if DMP73 == true */
	private boolean consentObtained;

	/** DMP75. -> if DMP75 == false */
	@Size(min = 0, max = 2000, groups = EthicalVal.class)
	private String consentObtainedTxt;

	/** DMP95. -> if DMP75 == true */
	private boolean sharingConsidered;

	/** DMP76. */
	private boolean irbApproval;

	/** DMP76. -> if DMP76 == false */
	@Size(min = 0, max = 1000, groups = EthicalVal.class)
	private String irbApprovalTxt;

	/** DMP78. */
	private boolean sensitiveDataIncluded;

	/** DMP96. -> if DMP78 == true */
	@Size(min = 0, max = 1000, groups = EthicalVal.class)
	private String sensitiveDataIncludedTxt;

	/** DMP79. */
	private boolean externalCopyright;

	/** DMP79. -> if DMP79 == true */
	@Size(min = 0, max = 1000, groups = EthicalVal.class)
	private String externalCopyrightTxt;

	/** DMP80. */
	private boolean internalCopyright;

	/** DMP80. -> if DMP80 == true */
	@Size(min = 0, max = 1000, groups = EthicalVal.class)
	private String internalCopyrightTxt;

	// ***************** Costs *****************
	/**
	 * group interface for validation - see {@link http://stackoverflow.com/questions/19190592/manually-call-spring-annotation-validation}
	 */
	public interface CostsVal {
	}

	/** DMP83. */
	private String specificCosts;

	/** DMP83. -> if DMP83 == true */
	@Size(min = 0, max = 1000, groups = CostsVal.class)
	private String specificCostsTxt;

	/** DMP85. -> if DMP83 == true */
	@Size(min = 0, max = 1000, groups = CostsVal.class)
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
	 * Getter for {@link #storageTechnologies}.
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
	 * @param id
	 *          -> this.id
	 */
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * Setter for {@link #duration}.
	 *
	 * @param duration
	 *          -> this.duration
	 */
	public void setDuration(String duration) {
		if (!Objects.equals(this.duration, duration)) {
			this.duration = duration;
		}
	}

	/**
	 * Setter for {@link #organizations}.
	 *
	 * @param organizations
	 *          -> this.organizations
	 */
	public void setOrganizations(String organizations) {
		if (!Objects.equals(this.organizations, organizations)) {
			this.organizations = organizations;
		}
	}

	/**
	 * Setter for {@link #planAims}.
	 *
	 * @param planAims
	 *          -> this.planAims
	 */
	public void setPlanAims(String planAims) {
		if (!Objects.equals(this.planAims, planAims)) {
			this.planAims = planAims;
		}
	}

	/**
	 * Setter for {@link #existingData}.
	 *
	 * @param existingData
	 *          -> this.existingData
	 */
	public void setExistingData(String existingData) {
		if (!Objects.equals(this.existingData, existingData)) {
			this.existingData = existingData;

		}
	}

	/**
	 * Setter for {@link #dataCitation}.
	 *
	 * @param dataCitation
	 *          -> this.dataCitation
	 */
	public void setDataCitation(String dataCitation) {
		if (!Objects.equals(this.dataCitation, dataCitation)) {
			this.dataCitation = dataCitation;

		}
	}

	/**
	 * Setter for {@link #existingDataRelevance}.
	 *
	 * @param existingDataRelevance
	 *          -> this.existingDataRelevance
	 */
	public void setExistingDataRelevance(String existingDataRelevance) {
		if (!Objects.equals(this.existingDataRelevance, existingDataRelevance)) {
			this.existingDataRelevance = existingDataRelevance;

		}

	}

	/**
	 * Setter for {@link #existingDataIntegration}.
	 *
	 * @param existingDataIntegration
	 *          -> this.existingDataIntegration
	 */
	public void setExistingDataIntegration(String existingDataIntegration) {
		if (!Objects.equals(this.existingDataIntegration, existingDataIntegration)) {
			this.existingDataIntegration = existingDataIntegration;

		}
	}

	/**
	 * Setter for {@link #usedDataTypes}.
	 *
	 * @param usedDataTypes
	 *          -> this.usedDataTypes
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
	 * @param otherDataTypes
	 *          -> this.otherDataTypes
	 */
	public void setOtherDataTypes(String otherDataTypes) {
		if (!Objects.equals(this.otherDataTypes, otherDataTypes)) {
			this.otherDataTypes = otherDataTypes;

		}

	}

	/**
	 * Setter for {@link #dataReproducibility}.
	 *
	 * @param dataReproducibility
	 *          -> this.dataReproducibility
	 */
	public void setDataReproducibility(String dataReproducibility) {
		if (!Objects.equals(this.dataReproducibility, dataReproducibility)) {
			this.dataReproducibility = dataReproducibility;

		}

	}

	/**
	 * Setter for {@link #usedCollectionModes}.
	 *
	 * @param usedCollectionModes
	 *          -> this.usedCollectionModes
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
	 * @param otherCMIP
	 *          -> this.otherCMIP
	 */
	public void setOtherCMIP(String otherCMIP) {
		if (!Objects.equals(this.otherCMIP, otherCMIP)) {
			this.otherCMIP = otherCMIP;

		}

	}

	/**
	 * Setter for {@link #otherCMINP}.
	 *
	 * @param otherCMINP
	 *          -> this.otherCMINP
	 */
	public void setOtherCMINP(String otherCMINP) {
		if (!Objects.equals(this.otherCMINP, otherCMINP)) {
			this.otherCMINP = otherCMINP;

		}

	}

	/**
	 * Setter for {@link #measOccasions}.
	 *
	 * @param measOccasions
	 *          -> this.measOccasions
	 */
	public void setMeasOccasions(String measOccasions) {
		if (!Objects.equals(this.measOccasions, measOccasions)) {
			this.measOccasions = measOccasions;

		}

	}

	/**
	 * Setter for {@link #reliabilityTraining}.
	 *
	 * @param reliabilityTraining
	 *          -> this.reliabilityTraining
	 */
	public void setReliabilityTraining(String reliabilityTraining) {
		if (!Objects.equals(this.reliabilityTraining, reliabilityTraining)) {
			this.reliabilityTraining = reliabilityTraining;

		}

	}

	/**
	 * Setter for {@link #multipleMeasurements}.
	 *
	 * @param multipleMeasurements
	 *          -> this.multipleMeasurements
	 */
	public void setMultipleMeasurements(String multipleMeasurements) {
		if (!Objects.equals(this.multipleMeasurements, multipleMeasurements)) {
			this.multipleMeasurements = multipleMeasurements;

		}

	}

	/**
	 * Setter for {@link #qualitityOther}.
	 *
	 * @param qualitityOther
	 *          -> this.qualitityOther
	 */
	public void setQualitityOther(String qualitityOther) {
		if (!Objects.equals(this.qualitityOther, qualitityOther)) {
			this.qualitityOther = qualitityOther;

		}

	}

	/**
	 * Setter for {@link #fileFormat}.
	 *
	 * @param fileFormat
	 *          -> this.fileFormat
	 */
	public void setFileFormat(String fileFormat) {
		if (!Objects.equals(this.fileFormat, fileFormat)) {
			this.fileFormat = fileFormat;

		}

	}

	/**
	 * Setter for {@link #workingCopy}.
	 *
	 * @param workingCopy
	 *          -> this.workingCopy
	 */
	public void setWorkingCopy(boolean workingCopy) {
		if (!Objects.equals(this.workingCopy, workingCopy)) {
			this.workingCopy = workingCopy;

		}

	}

	/**
	 * Setter for {@link #goodScientific}.
	 *
	 * @param goodScientific
	 *          -> this.goodScientific
	 */
	public void setGoodScientific(boolean goodScientific) {
		if (!Objects.equals(this.goodScientific, goodScientific)) {
			this.goodScientific = goodScientific;

		}

	}

	/**
	 * Setter for {@link #subsequentUse}.
	 *
	 * @param subsequentUse
	 *          -> this.subsequentUse
	 */
	public void setSubsequentUse(boolean subsequentUse) {
		if (!Objects.equals(this.subsequentUse, subsequentUse)) {
			this.subsequentUse = subsequentUse;

		}

	}

	/**
	 * Setter for {@link #requirements}.
	 *
	 * @param requirements
	 *          -> this.requirements
	 */
	public void setRequirements(boolean requirements) {
		if (!Objects.equals(this.requirements, requirements)) {
			this.requirements = requirements;

		}

	}

	/**
	 * Setter for {@link #documentation}.
	 *
	 * @param documentation
	 *          -> this.documentation
	 */
	public void setDocumentation(boolean documentation) {
		if (!Objects.equals(this.documentation, documentation)) {
			this.documentation = documentation;

		}

	}

	/**
	 * Setter for {@link #dataSelection}.
	 *
	 * @param dataSelection
	 *          -> this.dataSelection
	 */
	public void setDataSelection(boolean dataSelection) {
		if (!Objects.equals(this.dataSelection, dataSelection)) {
			this.dataSelection = dataSelection;

		}

	}

	/**
	 * Setter for {@link #selectionTime}.
	 *
	 * @param selectionTime
	 *          -> this.selectionTime
	 */
	public void setSelectionTime(String selectionTime) {
		if (!Objects.equals(this.selectionTime, selectionTime)) {
			this.selectionTime = selectionTime;

		}

	}

	/**
	 * Setter for {@link #selectionResp}.
	 *
	 * @param selectionResp
	 *          -> this.selectionResp
	 */
	public void setSelectionResp(String selectionResp) {
		if (!Objects.equals(this.selectionResp, selectionResp)) {
			this.selectionResp = selectionResp;

		}

	}

	/**
	 * Setter for {@link #storageDuration}.
	 *
	 * @param storageDuration
	 *          -> this.storageDuration
	 */
	public void setStorageDuration(String storageDuration) {
		if (!Objects.equals(this.storageDuration, storageDuration)) {
			this.storageDuration = storageDuration;

		}

	}

	/**
	 * Setter for {@link #deleteProcedure}.
	 *
	 * @param deleteProcedure
	 *          -> this.deleteProcedure
	 */
	public void setDeleteProcedure(String deleteProcedure) {
		if (!Objects.equals(this.deleteProcedure, deleteProcedure)) {
			this.deleteProcedure = deleteProcedure;

		}

	}

	/**
	 * Setter for {@link #selectedMetaPurposes}.
	 *
	 * @param selectedMetaPurposes
	 *          -> this.selectedMetaPurposes
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
	 * @param metaDescription
	 *          -> this.metaDescription
	 */
	public void setMetaDescription(String metaDescription) {
		if (!Objects.equals(this.metaDescription, metaDescription)) {
			this.metaDescription = metaDescription;

		}
	}

	/**
	 * Setter for {@link #metaFramework}.
	 *
	 * @param metaFramework
	 *          -> this.metaFramework
	 */
	public void setMetaFramework(String metaFramework) {
		if (!Objects.equals(this.metaFramework, metaFramework)) {
			this.metaFramework = metaFramework;

		}
	}

	/**
	 * Setter for {@link #metaGeneration}.
	 *
	 * @param metaGeneration
	 *          -> this.metaGeneration
	 */
	public void setMetaGeneration(String metaGeneration) {
		if (!Objects.equals(this.metaGeneration, metaGeneration)) {
			this.metaGeneration = metaGeneration;

		}
	}

	/**
	 * Setter for {@link #metaMonitor}.
	 *
	 * @param metaMonitor
	 *          -> this.metaMonitor
	 */
	public void setMetaMonitor(String metaMonitor) {
		if (!Objects.equals(this.metaMonitor, metaMonitor)) {
			this.metaMonitor = metaMonitor;

		}

	}

	/**
	 * Setter for {@link #metaFormat}.
	 *
	 * @param metaFormat
	 *          -> this.metaFormat
	 */
	public void setMetaFormat(String metaFormat) {
		if (!Objects.equals(this.metaFormat, metaFormat)) {
			this.metaFormat = metaFormat;

		}
	}

	/**
	 * Setter for {@link #releaseObligation}.
	 *
	 * @param releaseObligation
	 *          -> this.releaseObligation
	 */
	public void setReleaseObligation(boolean releaseObligation) {
		if (!Objects.equals(this.releaseObligation, releaseObligation)) {
			this.releaseObligation = releaseObligation;

		}
	}

	/**
	 * Setter for {@link #searchableData}.
	 *
	 * @param searchableData
	 *          -> this.searchableData
	 */
	public void setSearchableData(String searchableData) {
		if (!Objects.equals(this.searchableData, searchableData)) {
			this.searchableData = searchableData;

		}
	}

	/**
	 * Setter for {@link #expectedUsage}.
	 *
	 * @param expectedUsage
	 *          -> this.expectedUsage
	 */
	public void setExpectedUsage(String expectedUsage) {
		if (!Objects.equals(this.expectedUsage, expectedUsage)) {
			this.expectedUsage = expectedUsage;

		}
	}

	/**
	 * Setter for {@link #publStrategy}.
	 *
	 * @param publStrategy
	 *          -> this.publStrategy
	 */
	public void setPublStrategy(String publStrategy) {
		if (!Objects.equals(this.publStrategy, publStrategy)) {
			this.publStrategy = publStrategy;

		}
	}

	/**
	 * Setter for {@link #accessReasonAuthor}.
	 *
	 * @param accessReasonAuthor
	 *          -> this.accessReasonAuthor
	 */
	public void setAccessReasonAuthor(String accessReasonAuthor) {
		if (!Objects.equals(this.accessReasonAuthor, accessReasonAuthor)) {
			this.accessReasonAuthor = accessReasonAuthor;

		}
	}

	/**
	 * Setter for {@link #noAccessReason}.
	 *
	 * @param noAccessReason
	 *          -> this.noAccessReason
	 */
	public void setNoAccessReason(String noAccessReason) {
		if (!Objects.equals(this.noAccessReason, noAccessReason)) {
			this.noAccessReason = noAccessReason;

		}
	}

	/**
	 * Setter for {@link #noAccessReasonOther}.
	 *
	 * @param noAccessReasonOther
	 *          -> this.noAccessReasonOther
	 */
	public void setNoAccessReasonOther(String noAccessReasonOther) {
		if (!Objects.equals(this.noAccessReasonOther, noAccessReasonOther)) {
			this.noAccessReasonOther = noAccessReasonOther;

		}

	}

	/**
	 * Setter for {@link #depositName}.
	 *
	 * @param depositName
	 *          -> this.depositName
	 */
	public void setDepositName(String depositName) {
		if (!Objects.equals(this.depositName, depositName)) {
			this.depositName = depositName;

		}
	}

	/**
	 * Setter for {@link #transferTime}.
	 *
	 * @param transferTime
	 *          -> this.transferTime
	 */
	public void setTransferTime(String transferTime) {
		if (!Objects.equals(this.transferTime, transferTime)) {
			this.transferTime = transferTime;

		}
	}

	/**
	 * Setter for {@link #sensitiveData}.
	 *
	 * @param sensitiveData
	 *          -> this.sensitiveData
	 */
	public void setSensitiveData(String sensitiveData) {
		if (!Objects.equals(this.sensitiveData, sensitiveData)) {
			this.sensitiveData = sensitiveData;

		}
	}

	/**
	 * Setter for {@link #initialUsage}.
	 *
	 * @param initialUsage
	 *          -> this.initialUsage
	 */
	public void setInitialUsage(String initialUsage) {
		if (!Objects.equals(this.initialUsage, initialUsage)) {
			this.initialUsage = initialUsage;

		}
	}

	/**
	 * Setter for {@link #usageRestriction}.
	 *
	 * @param usageRestriction
	 *          -> this.usageRestriction
	 */
	public void setUsageRestriction(String usageRestriction) {
		if (!Objects.equals(this.usageRestriction, usageRestriction)) {
			this.usageRestriction = usageRestriction;

		}
	}

	/**
	 * Setter for {@link #accessCosts}.
	 *
	 * @param accessCosts
	 *          -> this.accessCosts
	 */
	public void setAccessCosts(boolean accessCosts) {
		if (!Objects.equals(this.accessCosts, accessCosts)) {
			this.accessCosts = accessCosts;

		}
	}

	/**
	 * Setter for {@link #clarifiedRights}.
	 *
	 * @param clarifiedRights
	 *          -> this.clarifiedRights
	 */
	public void setClarifiedRights(boolean clarifiedRights) {
		if (!Objects.equals(this.clarifiedRights, clarifiedRights)) {
			this.clarifiedRights = clarifiedRights;

		}
	}

	/**
	 * Setter for {@link #acquisitionAgreement}.
	 *
	 * @param acquisitionAgreement
	 *          -> this.acquisitionAgreement
	 */
	public void setAcquisitionAgreement(boolean acquisitionAgreement) {
		if (!Objects.equals(this.acquisitionAgreement, acquisitionAgreement)) {
			this.acquisitionAgreement = acquisitionAgreement;

		}
	}

	/**
	 * Setter for {@link #usedPID}.
	 *
	 * @param usedPID
	 *          -> this.usedPID
	 */
	public void setUsedPID(String usedPID) {
		if (!Objects.equals(this.usedPID, usedPID)) {
			this.usedPID = usedPID;

		}
	}

	/**
	 * Setter for {@link #usedPIDTxt}.
	 *
	 * @param usedPIDTxt
	 *          -> this.usedPIDTxt
	 */
	public void setUsedPIDTxt(String usedPIDTxt) {
		if (!Objects.equals(this.usedPIDTxt, usedPIDTxt)) {
			this.usedPIDTxt = usedPIDTxt;

		}
	}

	/**
	 * Setter for {@link #storageResponsible}.
	 *
	 * @param storageResponsible
	 *          -> this.storageResponsible
	 */
	public void setStorageResponsible(String storageResponsible) {
		if (!Objects.equals(this.storageResponsible, storageResponsible)) {
			this.storageResponsible = storageResponsible;

		}
	}

	/**
	 * Setter for {@link #storageTechnologies}.
	 *
	 * @param storageTechnologies
	 *          -> this.storageTechnologies
	 */
	public void setNamingCon(String namingCon) {
		if (!Objects.equals(this.namingCon, namingCon)) {
			this.namingCon = namingCon;

		}

	}

	/**
	 * Setter for {@link #storagePlaces}.
	 *
	 * @param storagePlaces
	 *          -> this.storagePlaces
	 */
	public void setStoragePlaces(String storagePlaces) {
		if (!Objects.equals(this.storagePlaces, storagePlaces)) {
			this.storagePlaces = storagePlaces;

		}
	}

	/**
	 * Setter for {@link #storageBackups}.
	 *
	 * @param storageBackups
	 *          -> this.storageBackups
	 */
	public void setStorageBackups(String storageBackups) {
		if (!Objects.equals(this.storageBackups, storageBackups)) {
			this.storageBackups = storageBackups;

		}
	}

	/**
	 * Setter for {@link #storageTransfer}.
	 *
	 * @param storageTransfer
	 *          -> this.storageTransfer
	 */
	public void setStorageTransfer(String storageTransfer) {
		if (!Objects.equals(this.storageTransfer, storageTransfer)) {
			this.storageTransfer = storageTransfer;

		}
	}

	/**
	 * Setter for {@link #storageExpectedSize}.
	 *
	 * @param storageExpectedSize
	 *          -> this.storageExpectedSize
	 */
	public void setStorageExpectedSize(String storageExpectedSize) {
		if (!Objects.equals(this.storageExpectedSize, storageExpectedSize)) {
			this.storageExpectedSize = storageExpectedSize;

		}
	}

	/**
	 * Setter for {@link #storageRequirements}.
	 *
	 * @param storageRequirements
	 *          -> this.storageRequirements
	 */
	public void setStorageRequirements(boolean storageRequirements) {
		if (!Objects.equals(this.storageRequirements, storageRequirements)) {
			this.storageRequirements = storageRequirements;

		}
	}

	/**
	 * Setter for {@link #storageRequirementsTxt}.
	 *
	 * @param storageRequirementsTxt
	 *          -> this.storageRequirementsTxt
	 */
	public void setStorageRequirementsTxt(String storageRequirementsTxt) {
		if (!Objects.equals(this.storageRequirementsTxt, storageRequirementsTxt)) {
			this.storageRequirementsTxt = storageRequirementsTxt;

		}
	}

	/**
	 * Setter for {@link #storageSuccession}.
	 *
	 * @param storageSuccession
	 *          -> this.storageSuccession
	 */
	public void setStorageSuccession(boolean storageSuccession) {
		if (!Objects.equals(this.storageSuccession, storageSuccession)) {
			this.storageSuccession = storageSuccession;

		}
	}

	/**
	 * Setter for {@link #storageSuccessionTxt}.
	 *
	 * @param storageSuccessionTxt
	 *          -> this.storageSuccessionTxt
	 */
	public void setStorageSuccessionTxt(String storageSuccessionTxt) {
		if (!Objects.equals(this.storageSuccessionTxt, storageSuccessionTxt)) {
			this.storageSuccessionTxt = storageSuccessionTxt;

		}
	}

	/**
	 * Setter for {@link #frameworkNationality}.
	 *
	 * @param frameworkNationality
	 *          -> this.frameworkNationality
	 */
	public void setFrameworkNationality(String frameworkNationality) {
		if (!Objects.equals(this.frameworkNationality, frameworkNationality)) {
			this.frameworkNationality = frameworkNationality;

		}
	}

	/**
	 * Setter for {@link #frameworkNationalityTxt}.
	 *
	 * @param frameworkNationalityTxt
	 *          -> this.frameworkNationalityTxt
	 */
	public void setFrameworkNationalityTxt(String frameworkNationalityTxt) {
		if (!Objects.equals(this.frameworkNationalityTxt, frameworkNationalityTxt)) {
			this.frameworkNationalityTxt = frameworkNationalityTxt;

		}
	}

	/**
	 * Setter for {@link #responsibleUnit}.
	 *
	 * @param responsibleUnit
	 *          -> this.responsibleUnit
	 */
	public void setResponsibleUnit(String responsibleUnit) {
		if (!Objects.equals(this.responsibleUnit, responsibleUnit)) {
			this.responsibleUnit = responsibleUnit;

		}
	}

	/**
	 * Setter for {@link #involvedInstitutions}.
	 *
	 * @param involvedInstitutions
	 *          -> this.involvedInstitutions
	 */
	public void setInvolvedInstitutions(String involvedInstitutions) {
		if (!Objects.equals(this.involvedInstitutions, involvedInstitutions)) {
			this.involvedInstitutions = involvedInstitutions;

		}
	}

	/**
	 * Setter for {@link #involvedInformed}.
	 *
	 * @param involvedInformed
	 *          -> this.involvedInformed
	 */
	public void setInvolvedInformed(boolean involvedInformed) {
		if (!Objects.equals(this.involvedInformed, involvedInformed)) {
			this.involvedInformed = involvedInformed;

		}
	}

	/**
	 * Setter for {@link #contributionsDefined}.
	 *
	 * @param contributionsDefined
	 *          -> this.contributionsDefined
	 */
	public void setContributionsDefined(boolean contributionsDefined) {
		if (!Objects.equals(this.contributionsDefined, contributionsDefined)) {
			this.contributionsDefined = contributionsDefined;

		}
	}

	/**
	 * Setter for {@link #contributionsDefinedTxt}.
	 *
	 * @param contributionsDefinedTxt
	 *          -> this.contributionsDefinedTxt
	 */
	public void setContributionsDefinedTxt(String contributionsDefinedTxt) {
		if (!Objects.equals(this.contributionsDefinedTxt, contributionsDefinedTxt)) {
			this.contributionsDefinedTxt = contributionsDefinedTxt;

		}
	}

	/**
	 * Setter for {@link #givenConsent}.
	 *
	 * @param givenConsent
	 *          -> this.givenConsent
	 */
	public void setGivenConsent(boolean givenConsent) {
		if (!Objects.equals(this.givenConsent, givenConsent)) {
			this.givenConsent = givenConsent;

		}
	}

	/**
	 * Setter for {@link #managementWorkflow}.
	 *
	 * @param managementWorkflow
	 *          -> this.managementWorkflow
	 */
	public void setManagementWorkflow(boolean managementWorkflow) {
		if (!Objects.equals(this.managementWorkflow, managementWorkflow)) {
			this.managementWorkflow = managementWorkflow;

		}
	}

	/**
	 * Setter for {@link #managementWorkflowTxt}.
	 *
	 * @param managementWorkflowTxt
	 *          -> this.managementWorkflowTxt
	 */
	public void setManagementWorkflowTxt(String managementWorkflowTxt) {
		if (!Objects.equals(this.managementWorkflowTxt, managementWorkflowTxt)) {
			this.managementWorkflowTxt = managementWorkflowTxt;

		}
	}

	/**
	 * Setter for {@link #staffDescription}.
	 *
	 * @param staffDescription
	 *          -> this.staffDescription
	 */
	public void setStaffDescription(boolean staffDescription) {
		if (!Objects.equals(this.staffDescription, staffDescription)) {
			this.staffDescription = staffDescription;

		}
	}

	/**
	 * Setter for {@link #staffDescriptionTxt}.
	 *
	 * @param staffDescriptionTxt
	 *          -> this.staffDescriptionTxt
	 */
	public void setStaffDescriptionTxt(String staffDescriptionTxt) {
		if (!Objects.equals(this.staffDescriptionTxt, staffDescriptionTxt)) {
			this.staffDescriptionTxt = staffDescriptionTxt;

		}
	}

	/**
	 * Setter for {@link #funderRequirements}.
	 *
	 * @param funderRequirements
	 *          -> this.funderRequirements
	 */
	public void setFunderRequirements(String funderRequirements) {
		if (!Objects.equals(this.funderRequirements, funderRequirements)) {
			this.funderRequirements = funderRequirements;

		}
	}

	/**
	 * Setter for {@link #planningAdherence}.
	 *
	 * @param planningAdherence
	 *          -> this.planningAdherence
	 */
	public void setPlanningAdherence(String planningAdherence) {
		if (!Objects.equals(this.planningAdherence, planningAdherence)) {
			this.planningAdherence = planningAdherence;

		}
	}

	/**
	 * Setter for {@link #dataProtection}.
	 *
	 * @param dataProtection
	 *          -> this.dataProtection
	 */
	public void setDataProtection(boolean dataProtection) {
		if (!Objects.equals(this.dataProtection, dataProtection)) {
			this.dataProtection = dataProtection;

		}
	}

	/**
	 * Setter for {@link #protectionRequirements}.
	 *
	 * @param protectionRequirements
	 *          -> this.protectionRequirements
	 */
	public void setProtectionRequirements(String protectionRequirements) {
		if (!Objects.equals(this.protectionRequirements, protectionRequirements)) {
			this.protectionRequirements = protectionRequirements;

		}
	}

	/**
	 * Setter for {@link #consentObtained}.
	 *
	 * @param consentObtained
	 *          -> this.consentObtained
	 */
	public void setConsentObtained(boolean consentObtained) {
		if (!Objects.equals(this.consentObtained, consentObtained)) {
			this.consentObtained = consentObtained;

		}
	}

	/**
	 * Setter for {@link #consentObtainedTxt}.
	 *
	 * @param consentObtainedTxt
	 *          -> this.consentObtainedTxt
	 */
	public void setConsentObtainedTxt(String consentObtainedTxt) {
		if (!Objects.equals(this.consentObtainedTxt, consentObtainedTxt)) {
			this.consentObtainedTxt = consentObtainedTxt;

		}
	}

	/**
	 * Setter for {@link #sharingConsidered}.
	 *
	 * @param sharingConsidered
	 *          -> this.sharingConsidered
	 */
	public void setSharingConsidered(boolean sharingConsidered) {
		if (!Objects.equals(this.sharingConsidered, sharingConsidered)) {
			this.sharingConsidered = sharingConsidered;

		}
	}

	/**
	 * Setter for {@link #irbApproval}.
	 *
	 * @param irbApproval
	 *          -> this.irbApproval
	 */
	public void setIrbApproval(boolean irbApproval) {
		if (!Objects.equals(this.irbApproval, irbApproval)) {
			this.irbApproval = irbApproval;

		}
	}

	/**
	 * Setter for {@link #irbApprovalTxt}.
	 *
	 * @param irbApprovalTxt
	 *          -> this.irbApprovalTxt
	 */
	public void setIrbApprovalTxt(String irbApprovalTxt) {
		if (!Objects.equals(this.irbApprovalTxt, irbApprovalTxt)) {
			this.irbApprovalTxt = irbApprovalTxt;

		}
	}

	/**
	 * Setter for {@link #sensitiveDataIncluded}.
	 *
	 * @param sensitiveDataIncluded
	 *          -> this.sensitiveDataIncluded
	 */
	public void setSensitiveDataIncluded(boolean sensitiveDataIncluded) {
		if (!Objects.equals(this.sensitiveDataIncluded, sensitiveDataIncluded)) {
			this.sensitiveDataIncluded = sensitiveDataIncluded;

		}
	}

	/**
	 * Setter for {@link #sensitiveDataIncludedTxt}.
	 *
	 * @param sensitiveDataIncludedTxt
	 *          -> this.sensitiveDataIncludedTxt
	 */
	public void setSensitiveDataIncludedTxt(String sensitiveDataIncludedTxt) {
		if (!Objects.equals(this.sensitiveDataIncludedTxt, sensitiveDataIncludedTxt)) {
			this.sensitiveDataIncludedTxt = sensitiveDataIncludedTxt;

		}
	}

	/**
	 * Setter for {@link #externalCopyright}.
	 *
	 * @param externalCopyright
	 *          -> this.externalCopyright
	 */
	public void setExternalCopyright(boolean externalCopyright) {
		if (!Objects.equals(this.externalCopyright, externalCopyright)) {
			this.externalCopyright = externalCopyright;

		}
	}

	/**
	 * Setter for {@link #externalCopyrightTxt}.
	 *
	 * @param externalCopyrightTxt
	 *          -> this.externalCopyrightTxt
	 */
	public void setExternalCopyrightTxt(String externalCopyrightTxt) {
		if (!Objects.equals(this.externalCopyrightTxt, externalCopyrightTxt)) {
			this.externalCopyrightTxt = externalCopyrightTxt;

		}
	}

	/**
	 * Setter for {@link #internalCopyright}.
	 *
	 * @param internalCopyright
	 *          -> this.internalCopyright
	 */
	public void setInternalCopyright(boolean internalCopyright) {
		if (!Objects.equals(this.internalCopyright, internalCopyright)) {
			this.internalCopyright = internalCopyright;

		}
	}

	/**
	 * Setter for {@link #internalCopyrightTxt}.
	 *
	 * @param internalCopyrightTxt
	 *          -> this.internalCopyrightTxt
	 */
	public void setInternalCopyrightTxt(String internalCopyrightTxt) {
		if (!Objects.equals(this.internalCopyrightTxt, internalCopyrightTxt)) {
			this.internalCopyrightTxt = internalCopyrightTxt;

		}
	}

	/**
	 * Setter for {@link #specificCosts}.
	 *
	 * @param specificCosts
	 *          -> this.specificCosts
	 */
	public void setSpecificCosts(String specificCosts) {
		if (!Objects.equals(this.specificCosts, specificCosts)) {
			this.specificCosts = specificCosts;

		}
	}

	/**
	 * Setter for {@link #specificCostsTxt}.
	 *
	 * @param specificCostsTxt
	 *          -> this.specificCostsTxt
	 */
	public void setSpecificCostsTxt(String specificCostsTxt) {
		if (!Objects.equals(this.specificCostsTxt, specificCostsTxt)) {
			this.specificCostsTxt = specificCostsTxt;

		}
	}

	/**
	 * Setter for {@link #bearCost}.
	 *
	 * @param bearCost
	 *          -> this.bearCost
	 */
	public void setBearCost(String bearCost) {
		if (!Objects.equals(this.bearCost, bearCost)) {
			this.bearCost = bearCost;

		}
	}

	@Override
	public String toString() {
		return "DmpDTO [id=" + id + ", duration=" + duration + ", organizations=" + organizations + ", planAims=" + planAims + ", existingData=" + existingData
		    + ", dataCitation=" + dataCitation + ", existingDataRelevance=" + existingDataRelevance + ", existingDataIntegration=" + existingDataIntegration
		    + ", usedDataTypes=" + usedDataTypes + ", otherDataTypes=" + otherDataTypes + ", dataReproducibility=" + dataReproducibility + ", usedCollectionModes="
		    + usedCollectionModes + ", otherCMIP=" + otherCMIP + ", otherCMINP=" + otherCMINP + ", measOccasions=" + measOccasions + ", reliabilityTraining="
		    + reliabilityTraining + ", multipleMeasurements=" + multipleMeasurements + ", qualitityOther=" + qualitityOther + ", fileFormat=" + fileFormat
		    + ", workingCopy=" + workingCopy + ", goodScientific=" + goodScientific + ", subsequentUse=" + subsequentUse + ", requirements=" + requirements
		    + ", documentation=" + documentation + ", dataSelection=" + dataSelection + ", selectionTime=" + selectionTime + ", selectionResp=" + selectionResp
		    + ", storageDuration=" + storageDuration + ", deleteProcedure=" + deleteProcedure + ", selectedMetaPurposes=" + selectedMetaPurposes
		    + ", metaDescription=" + metaDescription + ", metaFramework=" + metaFramework + ", metaGeneration=" + metaGeneration + ", metaMonitor=" + metaMonitor
		    + ", metaFormat=" + metaFormat + ", releaseObligation=" + releaseObligation + ", searchableData=" + searchableData + ", expectedUsage=" + expectedUsage
		    + ", publStrategy=" + publStrategy + ", accessReasonAuthor=" + accessReasonAuthor + ", noAccessReason=" + noAccessReason + ", noAccessReasonOther="
		    + noAccessReasonOther + ", depositName=" + depositName + ", transferTime=" + transferTime + ", sensitiveData=" + sensitiveData + ", initialUsage="
		    + initialUsage + ", usageRestriction=" + usageRestriction + ", accessCosts=" + accessCosts + ", clarifiedRights=" + clarifiedRights
		    + ", acquisitionAgreement=" + acquisitionAgreement + ", usedPID=" + usedPID + ", usedPIDTxt=" + usedPIDTxt + ", storageResponsible="
		    + storageResponsible + ", namingCon=" + namingCon + ", storagePlaces=" + storagePlaces + ", storageBackups=" + storageBackups + ", storageTransfer="
		    + storageTransfer + ", storageExpectedSize=" + storageExpectedSize + ", storageRequirements=" + storageRequirements + ", storageRequirementsTxt="
		    + storageRequirementsTxt + ", storageSuccession=" + storageSuccession + ", storageSuccessionTxt=" + storageSuccessionTxt + ", frameworkNationality="
		    + frameworkNationality + ", frameworkNationalityTxt=" + frameworkNationalityTxt + ", responsibleUnit=" + responsibleUnit + ", involvedInstitutions="
		    + involvedInstitutions + ", involvedInformed=" + involvedInformed + ", contributionsDefined=" + contributionsDefined + ", contributionsDefinedTxt="
		    + contributionsDefinedTxt + ", givenConsent=" + givenConsent + ", managementWorkflow=" + managementWorkflow + ", managementWorkflowTxt="
		    + managementWorkflowTxt + ", staffDescription=" + staffDescription + ", staffDescriptionTxt=" + staffDescriptionTxt + ", funderRequirements="
		    + funderRequirements + ", planningAdherence=" + planningAdherence + ", dataProtection=" + dataProtection + ", protectionRequirements="
		    + protectionRequirements + ", consentObtained=" + consentObtained + ", consentObtainedTxt=" + consentObtainedTxt + ", sharingConsidered="
		    + sharingConsidered + ", irbApproval=" + irbApproval + ", irbApprovalTxt=" + irbApprovalTxt + ", sensitiveDataIncluded=" + sensitiveDataIncluded
		    + ", sensitiveDataIncludedTxt=" + sensitiveDataIncludedTxt + ", externalCopyright=" + externalCopyright + ", externalCopyrightTxt="
		    + externalCopyrightTxt + ", internalCopyright=" + internalCopyright + ", internalCopyrightTxt=" + internalCopyrightTxt + ", specificCosts="
		    + specificCosts + ", specificCostsTxt=" + specificCostsTxt + ", bearCost=" + bearCost + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (accessCosts ? 1231 : 1237);
		result = prime * result + ((accessReasonAuthor == null) ? 0 : accessReasonAuthor.hashCode());
		result = prime * result + (acquisitionAgreement ? 1231 : 1237);
		result = prime * result + ((bearCost == null) ? 0 : bearCost.hashCode());
		result = prime * result + (clarifiedRights ? 1231 : 1237);
		result = prime * result + (consentObtained ? 1231 : 1237);
		result = prime * result + ((consentObtainedTxt == null) ? 0 : consentObtainedTxt.hashCode());
		result = prime * result + (contributionsDefined ? 1231 : 1237);
		result = prime * result + ((contributionsDefinedTxt == null) ? 0 : contributionsDefinedTxt.hashCode());
		result = prime * result + ((dataCitation == null) ? 0 : dataCitation.hashCode());
		result = prime * result + (dataProtection ? 1231 : 1237);
		result = prime * result + ((dataReproducibility == null) ? 0 : dataReproducibility.hashCode());
		result = prime * result + (dataSelection ? 1231 : 1237);
		result = prime * result + ((deleteProcedure == null) ? 0 : deleteProcedure.hashCode());
		result = prime * result + ((depositName == null) ? 0 : depositName.hashCode());
		result = prime * result + (documentation ? 1231 : 1237);
		result = prime * result + ((duration == null) ? 0 : duration.hashCode());
		result = prime * result + ((existingData == null) ? 0 : existingData.hashCode());
		result = prime * result + ((existingDataIntegration == null) ? 0 : existingDataIntegration.hashCode());
		result = prime * result + ((existingDataRelevance == null) ? 0 : existingDataRelevance.hashCode());
		result = prime * result + ((expectedUsage == null) ? 0 : expectedUsage.hashCode());
		result = prime * result + (externalCopyright ? 1231 : 1237);
		result = prime * result + ((externalCopyrightTxt == null) ? 0 : externalCopyrightTxt.hashCode());
		result = prime * result + ((fileFormat == null) ? 0 : fileFormat.hashCode());
		result = prime * result + ((frameworkNationality == null) ? 0 : frameworkNationality.hashCode());
		result = prime * result + ((frameworkNationalityTxt == null) ? 0 : frameworkNationalityTxt.hashCode());
		result = prime * result + ((funderRequirements == null) ? 0 : funderRequirements.hashCode());
		result = prime * result + (givenConsent ? 1231 : 1237);
		result = prime * result + (goodScientific ? 1231 : 1237);
		result = prime * result + (int) (id ^ (id >>> 32));
		result = prime * result + ((initialUsage == null) ? 0 : initialUsage.hashCode());
		result = prime * result + (internalCopyright ? 1231 : 1237);
		result = prime * result + ((internalCopyrightTxt == null) ? 0 : internalCopyrightTxt.hashCode());
		result = prime * result + (involvedInformed ? 1231 : 1237);
		result = prime * result + ((involvedInstitutions == null) ? 0 : involvedInstitutions.hashCode());
		result = prime * result + (irbApproval ? 1231 : 1237);
		result = prime * result + ((irbApprovalTxt == null) ? 0 : irbApprovalTxt.hashCode());
		result = prime * result + (managementWorkflow ? 1231 : 1237);
		result = prime * result + ((managementWorkflowTxt == null) ? 0 : managementWorkflowTxt.hashCode());
		result = prime * result + ((measOccasions == null) ? 0 : measOccasions.hashCode());
		result = prime * result + ((metaDescription == null) ? 0 : metaDescription.hashCode());
		result = prime * result + ((metaFormat == null) ? 0 : metaFormat.hashCode());
		result = prime * result + ((metaFramework == null) ? 0 : metaFramework.hashCode());
		result = prime * result + ((metaGeneration == null) ? 0 : metaGeneration.hashCode());
		result = prime * result + ((metaMonitor == null) ? 0 : metaMonitor.hashCode());
		result = prime * result + ((multipleMeasurements == null) ? 0 : multipleMeasurements.hashCode());
		result = prime * result + ((namingCon == null) ? 0 : namingCon.hashCode());
		result = prime * result + ((noAccessReason == null) ? 0 : noAccessReason.hashCode());
		result = prime * result + ((noAccessReasonOther == null) ? 0 : noAccessReasonOther.hashCode());
		result = prime * result + ((organizations == null) ? 0 : organizations.hashCode());
		result = prime * result + ((otherCMINP == null) ? 0 : otherCMINP.hashCode());
		result = prime * result + ((otherCMIP == null) ? 0 : otherCMIP.hashCode());
		result = prime * result + ((otherDataTypes == null) ? 0 : otherDataTypes.hashCode());
		result = prime * result + ((planAims == null) ? 0 : planAims.hashCode());
		result = prime * result + ((planningAdherence == null) ? 0 : planningAdherence.hashCode());
		result = prime * result + ((protectionRequirements == null) ? 0 : protectionRequirements.hashCode());
		result = prime * result + ((publStrategy == null) ? 0 : publStrategy.hashCode());
		result = prime * result + ((qualitityOther == null) ? 0 : qualitityOther.hashCode());
		result = prime * result + (releaseObligation ? 1231 : 1237);
		result = prime * result + ((reliabilityTraining == null) ? 0 : reliabilityTraining.hashCode());
		result = prime * result + (requirements ? 1231 : 1237);
		result = prime * result + ((responsibleUnit == null) ? 0 : responsibleUnit.hashCode());
		result = prime * result + ((searchableData == null) ? 0 : searchableData.hashCode());
		result = prime * result + ((selectedMetaPurposes == null) ? 0 : selectedMetaPurposes.hashCode());
		result = prime * result + ((selectionResp == null) ? 0 : selectionResp.hashCode());
		result = prime * result + ((selectionTime == null) ? 0 : selectionTime.hashCode());
		result = prime * result + ((sensitiveData == null) ? 0 : sensitiveData.hashCode());
		result = prime * result + (sensitiveDataIncluded ? 1231 : 1237);
		result = prime * result + ((sensitiveDataIncludedTxt == null) ? 0 : sensitiveDataIncludedTxt.hashCode());
		result = prime * result + (sharingConsidered ? 1231 : 1237);
		result = prime * result + ((specificCosts == null) ? 0 : specificCosts.hashCode());
		result = prime * result + ((specificCostsTxt == null) ? 0 : specificCostsTxt.hashCode());
		result = prime * result + (staffDescription ? 1231 : 1237);
		result = prime * result + ((staffDescriptionTxt == null) ? 0 : staffDescriptionTxt.hashCode());
		result = prime * result + ((storageBackups == null) ? 0 : storageBackups.hashCode());
		result = prime * result + ((storageDuration == null) ? 0 : storageDuration.hashCode());
		result = prime * result + ((storageExpectedSize == null) ? 0 : storageExpectedSize.hashCode());
		result = prime * result + ((storagePlaces == null) ? 0 : storagePlaces.hashCode());
		result = prime * result + (storageRequirements ? 1231 : 1237);
		result = prime * result + ((storageRequirementsTxt == null) ? 0 : storageRequirementsTxt.hashCode());
		result = prime * result + ((storageResponsible == null) ? 0 : storageResponsible.hashCode());
		result = prime * result + (storageSuccession ? 1231 : 1237);
		result = prime * result + ((storageSuccessionTxt == null) ? 0 : storageSuccessionTxt.hashCode());
		result = prime * result + ((storageTransfer == null) ? 0 : storageTransfer.hashCode());
		result = prime * result + (subsequentUse ? 1231 : 1237);
		result = prime * result + ((transferTime == null) ? 0 : transferTime.hashCode());
		result = prime * result + ((usageRestriction == null) ? 0 : usageRestriction.hashCode());
		result = prime * result + ((usedCollectionModes == null) ? 0 : usedCollectionModes.hashCode());
		result = prime * result + ((usedDataTypes == null) ? 0 : usedDataTypes.hashCode());
		result = prime * result + ((usedPID == null) ? 0 : usedPID.hashCode());
		result = prime * result + ((usedPIDTxt == null) ? 0 : usedPIDTxt.hashCode());
		result = prime * result + (workingCopy ? 1231 : 1237);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DmpDTO other = (DmpDTO) obj;
		if (accessCosts != other.accessCosts)
			return false;
		if (accessReasonAuthor == null) {
			if (other.accessReasonAuthor != null)
				return false;
		} else if (!accessReasonAuthor.equals(other.accessReasonAuthor))
			return false;
		if (acquisitionAgreement != other.acquisitionAgreement)
			return false;
		if (bearCost == null) {
			if (other.bearCost != null)
				return false;
		} else if (!bearCost.equals(other.bearCost))
			return false;
		if (clarifiedRights != other.clarifiedRights)
			return false;
		if (consentObtained != other.consentObtained)
			return false;
		if (consentObtainedTxt == null) {
			if (other.consentObtainedTxt != null)
				return false;
		} else if (!consentObtainedTxt.equals(other.consentObtainedTxt))
			return false;
		if (contributionsDefined != other.contributionsDefined)
			return false;
		if (contributionsDefinedTxt == null) {
			if (other.contributionsDefinedTxt != null)
				return false;
		} else if (!contributionsDefinedTxt.equals(other.contributionsDefinedTxt))
			return false;
		if (dataCitation == null) {
			if (other.dataCitation != null)
				return false;
		} else if (!dataCitation.equals(other.dataCitation))
			return false;
		if (dataProtection != other.dataProtection)
			return false;
		if (dataReproducibility == null) {
			if (other.dataReproducibility != null)
				return false;
		} else if (!dataReproducibility.equals(other.dataReproducibility))
			return false;
		if (dataSelection != other.dataSelection)
			return false;
		if (deleteProcedure == null) {
			if (other.deleteProcedure != null)
				return false;
		} else if (!deleteProcedure.equals(other.deleteProcedure))
			return false;
		if (depositName == null) {
			if (other.depositName != null)
				return false;
		} else if (!depositName.equals(other.depositName))
			return false;
		if (documentation != other.documentation)
			return false;
		if (duration == null) {
			if (other.duration != null)
				return false;
		} else if (!duration.equals(other.duration))
			return false;
		if (existingData == null) {
			if (other.existingData != null)
				return false;
		} else if (!existingData.equals(other.existingData))
			return false;
		if (existingDataIntegration == null) {
			if (other.existingDataIntegration != null)
				return false;
		} else if (!existingDataIntegration.equals(other.existingDataIntegration))
			return false;
		if (existingDataRelevance == null) {
			if (other.existingDataRelevance != null)
				return false;
		} else if (!existingDataRelevance.equals(other.existingDataRelevance))
			return false;
		if (expectedUsage == null) {
			if (other.expectedUsage != null)
				return false;
		} else if (!expectedUsage.equals(other.expectedUsage))
			return false;
		if (externalCopyright != other.externalCopyright)
			return false;
		if (externalCopyrightTxt == null) {
			if (other.externalCopyrightTxt != null)
				return false;
		} else if (!externalCopyrightTxt.equals(other.externalCopyrightTxt))
			return false;
		if (fileFormat == null) {
			if (other.fileFormat != null)
				return false;
		} else if (!fileFormat.equals(other.fileFormat))
			return false;
		if (frameworkNationality == null) {
			if (other.frameworkNationality != null)
				return false;
		} else if (!frameworkNationality.equals(other.frameworkNationality))
			return false;
		if (frameworkNationalityTxt == null) {
			if (other.frameworkNationalityTxt != null)
				return false;
		} else if (!frameworkNationalityTxt.equals(other.frameworkNationalityTxt))
			return false;
		if (funderRequirements == null) {
			if (other.funderRequirements != null)
				return false;
		} else if (!funderRequirements.equals(other.funderRequirements))
			return false;
		if (givenConsent != other.givenConsent)
			return false;
		if (goodScientific != other.goodScientific)
			return false;
		if (id != other.id)
			return false;
		if (initialUsage == null) {
			if (other.initialUsage != null)
				return false;
		} else if (!initialUsage.equals(other.initialUsage))
			return false;
		if (internalCopyright != other.internalCopyright)
			return false;
		if (internalCopyrightTxt == null) {
			if (other.internalCopyrightTxt != null)
				return false;
		} else if (!internalCopyrightTxt.equals(other.internalCopyrightTxt))
			return false;
		if (involvedInformed != other.involvedInformed)
			return false;
		if (involvedInstitutions == null) {
			if (other.involvedInstitutions != null)
				return false;
		} else if (!involvedInstitutions.equals(other.involvedInstitutions))
			return false;
		if (irbApproval != other.irbApproval)
			return false;
		if (irbApprovalTxt == null) {
			if (other.irbApprovalTxt != null)
				return false;
		} else if (!irbApprovalTxt.equals(other.irbApprovalTxt))
			return false;
		if (managementWorkflow != other.managementWorkflow)
			return false;
		if (managementWorkflowTxt == null) {
			if (other.managementWorkflowTxt != null)
				return false;
		} else if (!managementWorkflowTxt.equals(other.managementWorkflowTxt))
			return false;
		if (measOccasions == null) {
			if (other.measOccasions != null)
				return false;
		} else if (!measOccasions.equals(other.measOccasions))
			return false;
		if (metaDescription == null) {
			if (other.metaDescription != null)
				return false;
		} else if (!metaDescription.equals(other.metaDescription))
			return false;
		if (metaFormat == null) {
			if (other.metaFormat != null)
				return false;
		} else if (!metaFormat.equals(other.metaFormat))
			return false;
		if (metaFramework == null) {
			if (other.metaFramework != null)
				return false;
		} else if (!metaFramework.equals(other.metaFramework))
			return false;
		if (metaGeneration == null) {
			if (other.metaGeneration != null)
				return false;
		} else if (!metaGeneration.equals(other.metaGeneration))
			return false;
		if (metaMonitor == null) {
			if (other.metaMonitor != null)
				return false;
		} else if (!metaMonitor.equals(other.metaMonitor))
			return false;
		if (multipleMeasurements == null) {
			if (other.multipleMeasurements != null)
				return false;
		} else if (!multipleMeasurements.equals(other.multipleMeasurements))
			return false;
		if (namingCon == null) {
			if (other.namingCon != null)
				return false;
		} else if (!namingCon.equals(other.namingCon))
			return false;
		if (noAccessReason == null) {
			if (other.noAccessReason != null)
				return false;
		} else if (!noAccessReason.equals(other.noAccessReason))
			return false;
		if (noAccessReasonOther == null) {
			if (other.noAccessReasonOther != null)
				return false;
		} else if (!noAccessReasonOther.equals(other.noAccessReasonOther))
			return false;
		if (organizations == null) {
			if (other.organizations != null)
				return false;
		} else if (!organizations.equals(other.organizations))
			return false;
		if (otherCMINP == null) {
			if (other.otherCMINP != null)
				return false;
		} else if (!otherCMINP.equals(other.otherCMINP))
			return false;
		if (otherCMIP == null) {
			if (other.otherCMIP != null)
				return false;
		} else if (!otherCMIP.equals(other.otherCMIP))
			return false;
		if (otherDataTypes == null) {
			if (other.otherDataTypes != null)
				return false;
		} else if (!otherDataTypes.equals(other.otherDataTypes))
			return false;
		if (planAims == null) {
			if (other.planAims != null)
				return false;
		} else if (!planAims.equals(other.planAims))
			return false;
		if (planningAdherence == null) {
			if (other.planningAdherence != null)
				return false;
		} else if (!planningAdherence.equals(other.planningAdherence))
			return false;
		if (protectionRequirements == null) {
			if (other.protectionRequirements != null)
				return false;
		} else if (!protectionRequirements.equals(other.protectionRequirements))
			return false;
		if (publStrategy == null) {
			if (other.publStrategy != null)
				return false;
		} else if (!publStrategy.equals(other.publStrategy))
			return false;
		if (qualitityOther == null) {
			if (other.qualitityOther != null)
				return false;
		} else if (!qualitityOther.equals(other.qualitityOther))
			return false;
		if (releaseObligation != other.releaseObligation)
			return false;
		if (reliabilityTraining == null) {
			if (other.reliabilityTraining != null)
				return false;
		} else if (!reliabilityTraining.equals(other.reliabilityTraining))
			return false;
		if (requirements != other.requirements)
			return false;
		if (responsibleUnit == null) {
			if (other.responsibleUnit != null)
				return false;
		} else if (!responsibleUnit.equals(other.responsibleUnit))
			return false;
		if (searchableData == null) {
			if (other.searchableData != null)
				return false;
		} else if (!searchableData.equals(other.searchableData))
			return false;
		if (selectedMetaPurposes == null) {
			if (other.selectedMetaPurposes != null)
				return false;
		} else if (!selectedMetaPurposes.equals(other.selectedMetaPurposes))
			return false;
		if (selectionResp == null) {
			if (other.selectionResp != null)
				return false;
		} else if (!selectionResp.equals(other.selectionResp))
			return false;
		if (selectionTime == null) {
			if (other.selectionTime != null)
				return false;
		} else if (!selectionTime.equals(other.selectionTime))
			return false;
		if (sensitiveData == null) {
			if (other.sensitiveData != null)
				return false;
		} else if (!sensitiveData.equals(other.sensitiveData))
			return false;
		if (sensitiveDataIncluded != other.sensitiveDataIncluded)
			return false;
		if (sensitiveDataIncludedTxt == null) {
			if (other.sensitiveDataIncludedTxt != null)
				return false;
		} else if (!sensitiveDataIncludedTxt.equals(other.sensitiveDataIncludedTxt))
			return false;
		if (sharingConsidered != other.sharingConsidered)
			return false;
		if (specificCosts == null) {
			if (other.specificCosts != null)
				return false;
		} else if (!specificCosts.equals(other.specificCosts))
			return false;
		if (specificCostsTxt == null) {
			if (other.specificCostsTxt != null)
				return false;
		} else if (!specificCostsTxt.equals(other.specificCostsTxt))
			return false;
		if (staffDescription != other.staffDescription)
			return false;
		if (staffDescriptionTxt == null) {
			if (other.staffDescriptionTxt != null)
				return false;
		} else if (!staffDescriptionTxt.equals(other.staffDescriptionTxt))
			return false;
		if (storageBackups == null) {
			if (other.storageBackups != null)
				return false;
		} else if (!storageBackups.equals(other.storageBackups))
			return false;
		if (storageDuration == null) {
			if (other.storageDuration != null)
				return false;
		} else if (!storageDuration.equals(other.storageDuration))
			return false;
		if (storageExpectedSize == null) {
			if (other.storageExpectedSize != null)
				return false;
		} else if (!storageExpectedSize.equals(other.storageExpectedSize))
			return false;
		if (storagePlaces == null) {
			if (other.storagePlaces != null)
				return false;
		} else if (!storagePlaces.equals(other.storagePlaces))
			return false;
		if (storageRequirements != other.storageRequirements)
			return false;
		if (storageRequirementsTxt == null) {
			if (other.storageRequirementsTxt != null)
				return false;
		} else if (!storageRequirementsTxt.equals(other.storageRequirementsTxt))
			return false;
		if (storageResponsible == null) {
			if (other.storageResponsible != null)
				return false;
		} else if (!storageResponsible.equals(other.storageResponsible))
			return false;
		if (storageSuccession != other.storageSuccession)
			return false;
		if (storageSuccessionTxt == null) {
			if (other.storageSuccessionTxt != null)
				return false;
		} else if (!storageSuccessionTxt.equals(other.storageSuccessionTxt))
			return false;
		if (storageTransfer == null) {
			if (other.storageTransfer != null)
				return false;
		} else if (!storageTransfer.equals(other.storageTransfer))
			return false;
		if (subsequentUse != other.subsequentUse)
			return false;
		if (transferTime == null) {
			if (other.transferTime != null)
				return false;
		} else if (!transferTime.equals(other.transferTime))
			return false;
		if (usageRestriction == null) {
			if (other.usageRestriction != null)
				return false;
		} else if (!usageRestriction.equals(other.usageRestriction))
			return false;
		if (usedCollectionModes == null) {
			if (other.usedCollectionModes != null)
				return false;
		} else if (!usedCollectionModes.equals(other.usedCollectionModes))
			return false;
		if (usedDataTypes == null) {
			if (other.usedDataTypes != null)
				return false;
		} else if (!usedDataTypes.equals(other.usedDataTypes))
			return false;
		if (usedPID == null) {
			if (other.usedPID != null)
				return false;
		} else if (!usedPID.equals(other.usedPID))
			return false;
		if (usedPIDTxt == null) {
			if (other.usedPIDTxt != null)
				return false;
		} else if (!usedPIDTxt.equals(other.usedPIDTxt))
			return false;
		if (workingCopy != other.workingCopy)
			return false;
		return true;
	}

}
