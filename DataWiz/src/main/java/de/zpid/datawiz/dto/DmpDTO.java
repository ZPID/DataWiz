package de.zpid.datawiz.dto;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.List;
import java.util.Objects;

import javax.validation.constraints.Size;

import org.springframework.util.CollectionUtils;

import de.zpid.datawiz.util.ListUtil;

// TODO: Auto-generated Javadoc
/**
 * Data-management-plan data transfer object: Includes all necessary information for the data-management-plan. Some DMP
 * information are saved in the Project information, such as name of the project Please read the metadata excel sheet
 *
 * @author Ronny Boelter
 * @version 1.0
 * @since January 2016
 */
public class DmpDTO implements Serializable {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 1989300324143602401L;

  /** The DMP ID - Is similar to the Project ID because only 1 DMP for a project. */
  private BigInteger id;

  // ***************** Administrative Data *****************
  /** checks if the fields of the Administrative Data has changed, this is used for particular saving */
  private boolean adminChanged = false;

  /**
   * group interface for validation - see
   * {@link http://stackoverflow.com/questions/19190592/manually-call-spring-annotation-validation}
   */
  public interface AdminVal {
  }

  /** DMP02. */
  @Size(min = 0, max = 2000, groups = AdminVal.class)
  private String projectAims;

  /** DMP03. */
  @Size(min = 0, max = 250, groups = AdminVal.class)
  private String projectSponsors;

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
  /** checks if the fields of the Research Data has changed, this is used for particular saving */
  private boolean researchChanged = false;

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
  @Size(min = 0, max = 1000)
  private String qualitityOther;

  /** DMP17. */
  @Size(min = 0, max = 1000, groups = ResearchVal.class)
  private String fileFormat;

  /** DMP18 - Why must the data be completely or partially stored?. */
  /** DMP19 - Antwortoption DMP18 */
  private boolean workingCopy;

  /** DMP19 - Antwortoption DMP18. */
  @Size(min = 0, max = 250, groups = ResearchVal.class)
  private String workingCopyTxt;

  /** DMP20 - Antwortoption DMP18. */
  private boolean goodScientific;

  /** DMP20 - Antwortoption DMP18. */
  @Size(min = 0, max = 250, groups = ResearchVal.class)
  private String goodScientificTxt;

  /** DMP21 - Antwortoption DMP18. */
  private boolean subsequentUse;

  /** DMP21 - Antwortoption DMP18. */
  @Size(min = 0, max = 250, groups = ResearchVal.class)
  private String subsequentUseTxt;

  /** DMP22 - Antwortoption DMP18. */
  private boolean requirements;

  /** DMP22 - Antwortoption DMP18. */
  @Size(min = 0, max = 250, groups = ResearchVal.class)
  private String requirementsTxt;

  /** DMP23 - Antwortoption DMP18. */
  private boolean documentation;

  /** DMP23 - Antwortoption DMP18. */
  @Size(min = 0, max = 250, groups = ResearchVal.class)
  private String documentationTxt;

  /** DMP86 - Does data selection take place?. */
  private boolean dataSelection;

  /** the next inputs are only important if dataselection == true. */
  /** DMP24 - DMP86 ja */
  @Size(min = 0, max = 500, groups = ResearchVal.class)
  private String selectionTime;

  /** DMP25 - DMP86 ja. */
  @Size(min = 0, max = 500, groups = ResearchVal.class)
  private String selectionResp;

  /** DMP26 - DMP86 ja. */
  @Size(min = 0, max = 500, groups = ResearchVal.class)
  private String selectionSoftware;

  /** DMP27 - DMP86 ja. */
  @Size(min = 0, max = 1000, groups = ResearchVal.class)
  private String selectionCriteria;

  /** DMP28. */
  @Size(min = 0, max = 500, groups = ResearchVal.class)
  private String storageDuration;

  /** DMP29. */
  @Size(min = 0, max = 500, groups = ResearchVal.class)
  private String deleteProcedure;

  // ***************** MetaData Data *****************
  /** checks if the fields of the MetaData Data has changed, this is used for particular saving */
  private boolean metaChanged = false;

  /** DMP31. */
  private List<Integer> selectedMetaPurposes;

  /** DMP32. */
  @Size(min = 0, max = 1000)
  private String metaDescription;

  /** DMP33. */
  @Size(min = 0, max = 1000)
  private String metaFramework;

  /** DMP34. */
  @Size(min = 0, max = 1000)
  private String metaGeneration;

  /** DMP35. */
  @Size(min = 0, max = 1000)
  private String metaMonitor;

  /** DMP36. */
  @Size(min = 0, max = 1000)
  private String metaFormat;

  // ***************** Data Sharing *****************
  /** checks if the fields of the Data Sharing has changed, this is used for particular saving */
  private boolean sharingChanged = false;

  /** DMP39. */
  private boolean releaseObligation;

  /** DMP40. */
  @Size(min = 0, max = 1000)
  private String expectedGroups;

  /** DMP42. */
  private boolean searchableData;

  /** DMP44. */
  @Size(min = 0, max = 1000)
  private String expectedUsage;

  /** DMP43 (select). */
  private String publStrategy;

  /** DMP38 - if data access on demand by author. */
  @Size(min = 0, max = 500)
  private String accessReasonAuthor;

  /** DMP38 - if data are not accessible (select). */
  private String noAccessReason;

  /** DMP38 - if data are not accessible - reason == other. */
  @Size(min = 0, max = 500)
  private String noAccessReasonOther;

  /** DMP98. */
  @Size(min = 0, max = 1000)
  private String depositName;

  // next fields are shown if principleRetain == repository!<
  /** DMP45. */
  @Size(min = 0, max = 1000)
  private String transferTime;

  /** DMP46. */
  @Size(min = 0, max = 1000)
  private String sensitiveData;

  /** DMP47. */
  @Size(min = 0, max = 1000)
  private String initialUsage;

  /** DMP48. */
  @Size(min = 0, max = 500)
  private String usageRestriction;

  /** DMP49. */
  private boolean accessCosts;

  /** DMP49 - if accessCost == true. */
  @Size(min = 0, max = 500)
  private String accessCostsTxt;

  /** DMP50. */
  @Size(min = 0, max = 2000)
  private String accessTermsImplementation;

  /** DMP51. */
  private boolean clarifiedRights;

  /** DMP51 - if clarifiedRights == true. */
  @Size(min = 0, max = 500)
  private String clarifiedRightsTxt;

  /** DMP52. */
  private boolean acquisitionAgreement;

  /** DMP53 (select). */
  private String usedPID;

  /** DMP53 (select)-> other selected. */
  @Size(min = 0, max = 500)
  private String usedPIDTxt;

  // ***************** Storage and infrastructure *****************
  /** checks if the fields of the Storage has changed, this is used for particular saving */
  private boolean storageChanged = false;

  /** DMP54. */
  @Size(min = 0, max = 1000)
  private String storageResponsible;

  /** DMP55. */
  @Size(min = 0, max = 1000)
  private String storageTechnologies;

  /** DMP56. */
  @Size(min = 0, max = 1000)
  private String storagePlaces;

  /** DMP57. */
  @Size(min = 0, max = 1000)
  private String storageBackups;

  /** DMP58. */
  @Size(min = 0, max = 1000)
  private String storageTransfer;

  /** DMP59. */
  @Size(min = 0, max = 1000)
  private String storageExpectedSize;

  /** DMP60. */
  private boolean storageRequirements;

  /** DMP60 -> if "no" selected. */
  @Size(min = 0, max = 1000)
  private String storageRequirementsTxt;

  /** DMP61. */
  private boolean storageSuccession;

  /** DMP61 -> if "yes" selected. */
  @Size(min = 0, max = 1000)
  private String storageSuccessionTxt;

  // ***************** Organization, management and policies *****************
  /** checks if the fields of the Organization has changed, this is used for particular saving */
  private boolean organizationChanged = false;
  /** DMP62. */
  private String frameworkNationality;

  /** DMP62 -> if international specific requirements selected. */
  @Size(min = 0, max = 1000)
  private String frameworkNationalityTxt;

  /** DMP63. */
  @Size(min = 0, max = 500)
  private String responsibleUnit;

  /** DMP64. */
  @Size(min = 0, max = 1000)
  private String involvedInstitutions;

  /** DMP65. */
  private boolean involvedInformed;

  /** DMP93 -> if 65 == 1. */
  private boolean contributionsDefined;

  /** DMP93 if -> 93 == "1". */
  @Size(min = 0, max = 1000)
  private String contributionsDefinedTxt;

  /** DMP94 -> if 65 == 1. */
  private boolean givenConsent;

  /** DMP66. */
  private boolean managementWorkflow;

  /** DMP66 if -> 66 == "1". */
  @Size(min = 0, max = 1000)
  private String managementWorkflowTxt;

  /** DMP67. */
  private boolean staffDescription;

  /** DMP67 if -> 67 == "1". */
  @Size(min = 0, max = 1000)
  private String staffDescriptionTxt;

  /** DMP68. */
  @Size(min = 0, max = 2000)
  private String funderRequirements;

  /** DMP69. */
  @Size(min = 0, max = 2000)
  private String providerRequirements;

  /** DMP70 -> if DMP43 archive or repo. */
  @Size(min = 0, max = 1000)
  private String repoPolicies;

  /** DMP71 -> if DMP43 archive or repo. */
  @Size(min = 0, max = 1000)
  private String repoPoliciesResponsible;

  /** DMP72. */
  @Size(min = 0, max = 1000)
  private String planningAdherence;

  // ***************** Ethical and legal aspects *****************
  /** checks if the fields of the Ethical has changed, this is used for particular saving */
  private boolean ethicalChanged = false;

  /** DMP73. */
  private boolean dataProtection;

  /** DMP74. -> if DMP73 == true */
  @Size(min = 0, max = 2000)
  private String protectionRequirements;

  /** DMP75. -> if DMP73 == true */
  private boolean consentObtained;

  /** DMP75. -> if DMP75 == false */
  @Size(min = 0, max = 2000)
  private String consentObtainedTxt;

  /** DMP95. -> if DMP75 == true */
  private boolean sharingConsidered;

  /** DMP76. */
  private boolean irbApproval;

  /** DMP76. -> if DMP76 == false */
  @Size(min = 0, max = 1000)
  private String irbApprovalTxt;

  /** DMP78. */
  private boolean sensitiveDataIncluded;

  /** DMP96. -> if DMP78 == true */
  @Size(min = 0, max = 1000)
  private String sensitiveDataIncludedTxt;

  /** DMP79. */
  private boolean externalCopyright;

  /** DMP79. -> if DMP79 == true */
  @Size(min = 0, max = 1000)
  private String externalCopyrightTxt;

  /** DMP80. */
  private boolean internalCopyright;

  /** DMP80. -> if DMP80 == true */
  @Size(min = 0, max = 1000)
  private String internalCopyrightTxt;

  // ***************** Costs *****************
  /** checks if the fields of the Costs has changed, this is used for particular saving */
  private boolean costsChanged = false;

  /** DMP83. */
  private String specificCosts;

  /** DMP83. -> if DMP83 == true */
  @Size(min = 0, max = 1000)
  private String specificCostsTxt;

  /** DMP84. -> if DMP83 == true */
  @Size(min = 0, max = 1000)
  private String ariseCosts;

  /** DMP85. -> if DMP83 == true */
  @Size(min = 0, max = 1000)
  private String bearCost;

  public BigInteger getId() {
    return id;
  }

  public boolean isAdminChanged() {
    return adminChanged;
  }

  public String getProjectAims() {
    return projectAims;
  }

  public String getProjectSponsors() {
    return projectSponsors;
  }

  public String getDuration() {
    return duration;
  }

  public String getOrganizations() {
    return organizations;
  }

  public String getPlanAims() {
    return planAims;
  }

  public boolean isResearchChanged() {
    return researchChanged;
  }

  public String getExistingData() {
    return existingData;
  }

  public String getDataCitation() {
    return dataCitation;
  }

  public String getExistingDataRelevance() {
    return existingDataRelevance;
  }

  public String getExistingDataIntegration() {
    return existingDataIntegration;
  }

  public List<Integer> getUsedDataTypes() {
    return usedDataTypes;
  }

  public String getOtherDataTypes() {
    return otherDataTypes;
  }

  public String getDataReproducibility() {
    return dataReproducibility;
  }

  public List<Integer> getUsedCollectionModes() {
    return usedCollectionModes;
  }

  public String getOtherCMIP() {
    return otherCMIP;
  }

  public String getOtherCMINP() {
    return otherCMINP;
  }

  public String getMeasOccasions() {
    return measOccasions;
  }

  public String getReliabilityTraining() {
    return reliabilityTraining;
  }

  public String getMultipleMeasurements() {
    return multipleMeasurements;
  }

  public String getQualitityOther() {
    return qualitityOther;
  }

  public String getFileFormat() {
    return fileFormat;
  }

  public boolean isWorkingCopy() {
    return workingCopy;
  }

  public String getWorkingCopyTxt() {
    return workingCopyTxt;
  }

  public boolean isGoodScientific() {
    return goodScientific;
  }

  public String getGoodScientificTxt() {
    return goodScientificTxt;
  }

  public boolean isSubsequentUse() {
    return subsequentUse;
  }

  public String getSubsequentUseTxt() {
    return subsequentUseTxt;
  }

  public boolean isRequirements() {
    return requirements;
  }

  public String getRequirementsTxt() {
    return requirementsTxt;
  }

  public boolean isDocumentation() {
    return documentation;
  }

  public String getDocumentationTxt() {
    return documentationTxt;
  }

  public boolean isDataSelection() {
    return dataSelection;
  }

  public String getSelectionTime() {
    return selectionTime;
  }

  public String getSelectionResp() {
    return selectionResp;
  }

  public String getSelectionSoftware() {
    return selectionSoftware;
  }

  public String getSelectionCriteria() {
    return selectionCriteria;
  }

  public String getStorageDuration() {
    return storageDuration;
  }

  public String getDeleteProcedure() {
    return deleteProcedure;
  }

  public boolean isMetaChanged() {
    return metaChanged;
  }

  public List<Integer> getSelectedMetaPurposes() {
    return selectedMetaPurposes;
  }

  public String getMetaDescription() {
    return metaDescription;
  }

  public String getMetaFramework() {
    return metaFramework;
  }

  public String getMetaGeneration() {
    return metaGeneration;
  }

  public String getMetaMonitor() {
    return metaMonitor;
  }

  public String getMetaFormat() {
    return metaFormat;
  }

  public boolean isSharingChanged() {
    return sharingChanged;
  }

  public boolean isReleaseObligation() {
    return releaseObligation;
  }

  public String getExpectedGroups() {
    return expectedGroups;
  }

  public boolean isSearchableData() {
    return searchableData;
  }

  public String getExpectedUsage() {
    return expectedUsage;
  }

  public String getPublStrategy() {
    return publStrategy;
  }

  public String getAccessReasonAuthor() {
    return accessReasonAuthor;
  }

  public String getNoAccessReason() {
    return noAccessReason;
  }

  public String getNoAccessReasonOther() {
    return noAccessReasonOther;
  }

  public String getDepositName() {
    return depositName;
  }

  public String getTransferTime() {
    return transferTime;
  }

  public String getSensitiveData() {
    return sensitiveData;
  }

  public String getInitialUsage() {
    return initialUsage;
  }

  public String getUsageRestriction() {
    return usageRestriction;
  }

  public boolean isAccessCosts() {
    return accessCosts;
  }

  public String getAccessCostsTxt() {
    return accessCostsTxt;
  }

  public String getAccessTermsImplementation() {
    return accessTermsImplementation;
  }

  public boolean isClarifiedRights() {
    return clarifiedRights;
  }

  public String getClarifiedRightsTxt() {
    return clarifiedRightsTxt;
  }

  public boolean isAcquisitionAgreement() {
    return acquisitionAgreement;
  }

  public String getUsedPID() {
    return usedPID;
  }

  public String getUsedPIDTxt() {
    return usedPIDTxt;
  }

  public boolean isStorageChanged() {
    return storageChanged;
  }

  public String getStorageResponsible() {
    return storageResponsible;
  }

  public String getStorageTechnologies() {
    return storageTechnologies;
  }

  public String getStoragePlaces() {
    return storagePlaces;
  }

  public String getStorageBackups() {
    return storageBackups;
  }

  public String getStorageTransfer() {
    return storageTransfer;
  }

  public String getStorageExpectedSize() {
    return storageExpectedSize;
  }

  public boolean isStorageRequirements() {
    return storageRequirements;
  }

  public String getStorageRequirementsTxt() {
    return storageRequirementsTxt;
  }

  public boolean isStorageSuccession() {
    return storageSuccession;
  }

  public String getStorageSuccessionTxt() {
    return storageSuccessionTxt;
  }

  public boolean isOrganizationChanged() {
    return organizationChanged;
  }

  public String getFrameworkNationality() {
    return frameworkNationality;
  }

  public String getFrameworkNationalityTxt() {
    return frameworkNationalityTxt;
  }

  public String getResponsibleUnit() {
    return responsibleUnit;
  }

  public String getInvolvedInstitutions() {
    return involvedInstitutions;
  }

  public boolean isInvolvedInformed() {
    return involvedInformed;
  }

  public boolean isContributionsDefined() {
    return contributionsDefined;
  }

  public String getContributionsDefinedTxt() {
    return contributionsDefinedTxt;
  }

  public boolean isGivenConsent() {
    return givenConsent;
  }

  public boolean isManagementWorkflow() {
    return managementWorkflow;
  }

  public String getManagementWorkflowTxt() {
    return managementWorkflowTxt;
  }

  public boolean isStaffDescription() {
    return staffDescription;
  }

  public String getStaffDescriptionTxt() {
    return staffDescriptionTxt;
  }

  public String getFunderRequirements() {
    return funderRequirements;
  }

  public String getProviderRequirements() {
    return providerRequirements;
  }

  public String getRepoPolicies() {
    return repoPolicies;
  }

  public String getRepoPoliciesResponsible() {
    return repoPoliciesResponsible;
  }

  public String getPlanningAdherence() {
    return planningAdherence;
  }

  public boolean isEthicalChanged() {
    return ethicalChanged;
  }

  public boolean isDataProtection() {
    return dataProtection;
  }

  public String getProtectionRequirements() {
    return protectionRequirements;
  }

  public boolean isConsentObtained() {
    return consentObtained;
  }

  public String getConsentObtainedTxt() {
    return consentObtainedTxt;
  }

  public boolean isSharingConsidered() {
    return sharingConsidered;
  }

  public boolean isIrbApproval() {
    return irbApproval;
  }

  public String getIrbApprovalTxt() {
    return irbApprovalTxt;
  }

  public boolean isSensitiveDataIncluded() {
    return sensitiveDataIncluded;
  }

  public String getSensitiveDataIncludedTxt() {
    return sensitiveDataIncludedTxt;
  }

  public boolean isExternalCopyright() {
    return externalCopyright;
  }

  public String getExternalCopyrightTxt() {
    return externalCopyrightTxt;
  }

  public boolean isInternalCopyright() {
    return internalCopyright;
  }

  public String getInternalCopyrightTxt() {
    return internalCopyrightTxt;
  }

  public boolean isCostsChanged() {
    return costsChanged;
  }

  public String getSpecificCosts() {
    return specificCosts;
  }

  public String getSpecificCostsTxt() {
    return specificCostsTxt;
  }

  public String getAriseCosts() {
    return ariseCosts;
  }

  public String getBearCost() {
    return bearCost;
  }

  public void setId(BigInteger id) {
    this.id = id;
  }

  public void setAdminChanged(boolean adminChanged) {
    this.adminChanged = adminChanged;
  }

  public void setProjectAims(String projectAims) {
    if (!Objects.equals(this.projectAims, projectAims)) {
      this.projectAims = projectAims;
      this.adminChanged = true;
    }
  }

  public void setProjectSponsors(String projectSponsors) {
    if (!Objects.equals(this.projectSponsors, projectSponsors)) {
      this.projectSponsors = projectSponsors;
      this.adminChanged = true;
    }
  }

  public void setDuration(String duration) {
    if (!Objects.equals(this.duration, duration)) {
      this.duration = duration;
      this.adminChanged = true;
    }
  }

  public void setOrganizations(String organizations) {
    if (!Objects.equals(this.organizations, organizations)) {
      this.organizations = organizations;
      this.adminChanged = true;
    }
  }

  public void setPlanAims(String planAims) {
    if (!Objects.equals(this.planAims, planAims)) {
      this.planAims = planAims;
      this.adminChanged = true;
    }
  }

  public void setResearchChanged(boolean researchChanged) {
    this.researchChanged = researchChanged;
  }

  public void setExistingData(String existingData) {
    if (!Objects.equals(this.existingData, existingData)) {
      this.existingData = existingData;
      this.researchChanged = true;
    }
  }

  public void setDataCitation(String dataCitation) {
    if (!Objects.equals(this.dataCitation, dataCitation)) {
      this.dataCitation = dataCitation;
      this.researchChanged = true;
    }
  }

  public void setExistingDataRelevance(String existingDataRelevance) {
    if (!Objects.equals(this.existingDataRelevance, existingDataRelevance)) {
      this.existingDataRelevance = existingDataRelevance;
      this.researchChanged = true;
    }

  }

  public void setExistingDataIntegration(String existingDataIntegration) {
    if (!Objects.equals(this.existingDataIntegration, existingDataIntegration)) {
      this.existingDataIntegration = existingDataIntegration;
      this.researchChanged = true;
    }
  }

  public void setUsedDataTypes(List<Integer> usedDataTypes) {
    if ((this.usedDataTypes != null && this.usedDataTypes.size() > 0)
        || (usedDataTypes != null && usedDataTypes.size() > 0))
      if (!ListUtil.equalsWithoutOrder(this.usedDataTypes, usedDataTypes)) {
        this.usedDataTypes = usedDataTypes;
        this.researchChanged = true;
      }
  }

  public void setOtherDataTypes(String otherDataTypes) {
    if (!Objects.equals(this.otherDataTypes, otherDataTypes)) {
      this.otherDataTypes = otherDataTypes;
      this.researchChanged = true;
    }

  }

  public void setDataReproducibility(String dataReproducibility) {
    if (!Objects.equals(this.dataReproducibility, dataReproducibility)) {
      this.dataReproducibility = dataReproducibility;
      this.researchChanged = true;
    }

  }

  public void setUsedCollectionModes(List<Integer> usedCollectionModes) {
    if (!Objects.equals(this.usedCollectionModes, usedCollectionModes)) {
      this.usedCollectionModes = usedCollectionModes;
      this.researchChanged = true;
    }

  }

  public void setOtherCMIP(String otherCMIP) {
    if (!Objects.equals(this.otherCMIP, otherCMIP)) {
      this.otherCMIP = otherCMIP;
      this.researchChanged = true;
    }

  }

  public void setOtherCMINP(String otherCMINP) {
    if (!Objects.equals(this.otherCMINP, otherCMINP)) {
      this.otherCMINP = otherCMINP;
      this.researchChanged = true;
    }

  }

  public void setMeasOccasions(String measOccasions) {
    if (!Objects.equals(this.measOccasions, measOccasions)) {
      this.measOccasions = measOccasions;
      this.researchChanged = true;
    }

  }

  public void setReliabilityTraining(String reliabilityTraining) {
    if (!Objects.equals(this.reliabilityTraining, reliabilityTraining)) {
      this.reliabilityTraining = reliabilityTraining;
      this.researchChanged = true;
    }

  }

  public void setMultipleMeasurements(String multipleMeasurements) {
    if (!Objects.equals(this.multipleMeasurements, multipleMeasurements)) {
      this.multipleMeasurements = multipleMeasurements;
      this.researchChanged = true;
    }

  }

  public void setQualitityOther(String qualitityOther) {
    if (!Objects.equals(this.qualitityOther, qualitityOther)) {
      this.qualitityOther = qualitityOther;
      this.researchChanged = true;
    }

  }

  public void setFileFormat(String fileFormat) {
    if (!Objects.equals(this.fileFormat, fileFormat)) {
      this.fileFormat = fileFormat;
      this.researchChanged = true;
    }

  }

  public void setWorkingCopy(boolean workingCopy) {
    if (!Objects.equals(this.workingCopy, workingCopy)) {
      this.workingCopy = workingCopy;
      this.researchChanged = true;
    }

  }

  public void setWorkingCopyTxt(String workingCopyTxt) {
    if (!Objects.equals(this.workingCopyTxt, workingCopyTxt)) {
      this.workingCopyTxt = workingCopyTxt;
      this.researchChanged = true;
    }

  }

  public void setGoodScientific(boolean goodScientific) {
    if (!Objects.equals(this.goodScientific, goodScientific)) {
      this.goodScientific = goodScientific;
      this.researchChanged = true;
    }

  }

  public void setGoodScientificTxt(String goodScientificTxt) {
    if (!Objects.equals(this.goodScientificTxt, goodScientificTxt)) {
      this.goodScientificTxt = goodScientificTxt;
      this.researchChanged = true;
    }

  }

  public void setSubsequentUse(boolean subsequentUse) {
    if (!Objects.equals(this.subsequentUse, subsequentUse)) {
      this.subsequentUse = subsequentUse;
      this.researchChanged = true;
    }

  }

  public void setSubsequentUseTxt(String subsequentUseTxt) {
    if (!Objects.equals(this.subsequentUseTxt, subsequentUseTxt)) {
      this.subsequentUseTxt = subsequentUseTxt;
      this.researchChanged = true;
    }

  }

  public void setRequirements(boolean requirements) {
    if (!Objects.equals(this.requirements, requirements)) {
      this.requirements = requirements;
      this.researchChanged = true;
    }

  }

  public void setRequirementsTxt(String requirementsTxt) {
    if (!Objects.equals(this.requirementsTxt, requirementsTxt)) {
      this.requirementsTxt = requirementsTxt;
      this.researchChanged = true;
    }

  }

  public void setDocumentation(boolean documentation) {
    if (!Objects.equals(this.documentation, documentation)) {
      this.documentation = documentation;
      this.researchChanged = true;
    }

  }

  public void setDocumentationTxt(String documentationTxt) {
    if (!Objects.equals(this.documentationTxt, documentationTxt)) {
      this.documentationTxt = documentationTxt;
      this.researchChanged = true;
    }

  }

  public void setDataSelection(boolean dataSelection) {
    if (!Objects.equals(this.dataSelection, dataSelection)) {
      this.dataSelection = dataSelection;
      this.researchChanged = true;
    }

  }

  public void setSelectionTime(String selectionTime) {
    if (!Objects.equals(this.selectionTime, selectionTime)) {
      this.selectionTime = selectionTime;
      this.researchChanged = true;
    }

  }

  public void setSelectionResp(String selectionResp) {
    if (!Objects.equals(this.selectionResp, selectionResp)) {
      this.selectionResp = selectionResp;
      this.researchChanged = true;
    }

  }

  public void setSelectionSoftware(String selectionSoftware) {
    if (!Objects.equals(this.selectionSoftware, selectionSoftware)) {
      this.selectionSoftware = selectionSoftware;
      this.researchChanged = true;
    }

  }

  public void setSelectionCriteria(String selectionCriteria) {
    if (!Objects.equals(this.selectionCriteria, selectionCriteria)) {
      this.selectionCriteria = selectionCriteria;
      this.researchChanged = true;
    }

  }

  public void setStorageDuration(String storageDuration) {
    if (!Objects.equals(this.storageDuration, storageDuration)) {
      this.storageDuration = storageDuration;
      this.researchChanged = true;
    }

  }

  public void setDeleteProcedure(String deleteProcedure) {
    if (!Objects.equals(this.deleteProcedure, deleteProcedure)) {
      this.deleteProcedure = deleteProcedure;
      this.researchChanged = true;
    }

  }

  public void setMetaChanged(boolean metaChanged) {
    this.metaChanged = metaChanged;
  }

  public void setSelectedMetaPurposes(List<Integer> selectedMetaPurposes) {
    if (this.selectedMetaPurposes != selectedMetaPurposes) {
      this.selectedMetaPurposes = selectedMetaPurposes;
      this.metaChanged = true;
    }
  }

  public void setMetaDescription(String metaDescription) {
    if (!(this.metaDescription == null && metaDescription == "") && this.metaDescription != metaDescription) {
      this.metaDescription = metaDescription;
      this.metaChanged = true;
    }
  }

  public void setMetaFramework(String metaFramework) {
    if (!(this.metaFramework == null && metaFramework == "") && this.metaFramework != metaFramework) {
      this.metaFramework = metaFramework;
      this.metaChanged = true;
    }
  }

  public void setMetaGeneration(String metaGeneration) {
    if (!(this.metaGeneration == null && metaGeneration == "") && this.metaGeneration != metaGeneration) {
      this.metaGeneration = metaGeneration;
      this.metaChanged = true;
    }
  }

  public void setMetaMonitor(String metaMonitor) {
    if (!(this.metaMonitor == null && metaMonitor == "") && this.metaMonitor != metaMonitor) {
      this.metaMonitor = metaMonitor;
      this.metaChanged = true;
    }
  }

  public void setMetaFormat(String metaFormat) {
    if (!(this.metaFormat == null && metaFormat == "") && this.metaFormat != metaFormat) {
      this.metaFormat = metaFormat;
      this.metaChanged = true;
    }
  }

  public void setSharingChanged(boolean sharingChanged) {
    this.sharingChanged = sharingChanged;
  }

  public void setReleaseObligation(boolean releaseObligation) {
    if (this.releaseObligation != releaseObligation) {
      this.releaseObligation = releaseObligation;
      this.sharingChanged = true;
    }
  }

  public void setExpectedGroups(String expectedGroups) {
    if (!(this.expectedGroups == null && expectedGroups == "") && this.expectedGroups != expectedGroups) {
      this.expectedGroups = expectedGroups;
      this.sharingChanged = true;
    }
  }

  public void setSearchableData(boolean searchableData) {
    if (this.searchableData != searchableData) {
      this.searchableData = searchableData;
      this.sharingChanged = true;
    }
  }

  public void setExpectedUsage(String expectedUsage) {
    if (this.expectedUsage != expectedUsage) {
      this.expectedUsage = expectedUsage;
      this.sharingChanged = true;
    }
  }

  public void setPublStrategy(String publStrategy) {
    if (this.publStrategy != publStrategy) {
      this.publStrategy = publStrategy;
      this.sharingChanged = true;
    }
  }

  public void setAccessReasonAuthor(String accessReasonAuthor) {
    if (!(this.accessReasonAuthor == null && accessReasonAuthor == "")
        && this.accessReasonAuthor != accessReasonAuthor) {
      this.accessReasonAuthor = accessReasonAuthor;
      this.sharingChanged = true;
    }
  }

  public void setNoAccessReason(String noAccessReason) {
    if (!(this.noAccessReason == null && noAccessReason == "") && this.noAccessReason != noAccessReason) {
      this.noAccessReason = noAccessReason;
      this.sharingChanged = true;
    }
  }

  public void setNoAccessReasonOther(String noAccessReasonOther) {
    if (!(this.noAccessReasonOther == null && noAccessReasonOther == "")
        && this.noAccessReasonOther != noAccessReasonOther) {
      this.noAccessReasonOther = noAccessReasonOther;
      this.sharingChanged = true;
    }
  }

  public void setDepositName(String depositName) {
    if (!(this.depositName == null && depositName == "") && this.depositName != depositName) {
      this.depositName = depositName;
      this.sharingChanged = true;
    }
  }

  public void setTransferTime(String transferTime) {
    if (!(this.transferTime == null && transferTime == "") && this.transferTime != transferTime) {
      this.transferTime = transferTime;
      this.sharingChanged = true;
    }
  }

  public void setSensitiveData(String sensitiveData) {
    if (!(this.sensitiveData == null && sensitiveData == "") && this.sensitiveData != sensitiveData) {
      this.sensitiveData = sensitiveData;
      this.sharingChanged = true;
    }
  }

  public void setInitialUsage(String initialUsage) {
    if (!(this.initialUsage == null && initialUsage == "") && this.initialUsage != initialUsage) {
      this.initialUsage = initialUsage;
      this.sharingChanged = true;
    }
  }

  public void setUsageRestriction(String usageRestriction) {
    if (!(this.usageRestriction == null && usageRestriction == "") && this.usageRestriction != usageRestriction) {
      this.usageRestriction = usageRestriction;
      this.sharingChanged = true;
    }
  }

  public void setAccessCosts(boolean accessCosts) {
    if (this.accessCosts != accessCosts) {
      this.accessCosts = accessCosts;
      this.sharingChanged = true;
    }
  }

  public void setAccessCostsTxt(String accessCostsTxt) {
    if (this.accessCostsTxt != accessCostsTxt) {
      this.accessCostsTxt = accessCostsTxt;
      this.sharingChanged = true;
    }
  }

  public void setAccessTermsImplementation(String accessTermsImplementation) {
    if (!(this.accessTermsImplementation == null && accessTermsImplementation == "")
        && this.accessTermsImplementation != accessTermsImplementation) {
      this.accessTermsImplementation = accessTermsImplementation;
      this.sharingChanged = true;
    }
  }

  public void setClarifiedRights(boolean clarifiedRights) {
    if (this.clarifiedRights != clarifiedRights) {
      this.clarifiedRights = clarifiedRights;
      this.sharingChanged = true;
    }
  }

  public void setClarifiedRightsTxt(String clarifiedRightsTxt) {
    if (!(this.clarifiedRightsTxt == null && clarifiedRightsTxt == "")
        && this.clarifiedRightsTxt != clarifiedRightsTxt) {
      this.clarifiedRightsTxt = clarifiedRightsTxt;
      this.sharingChanged = true;
    }
  }

  public void setAcquisitionAgreement(boolean acquisitionAgreement) {
    if (this.acquisitionAgreement != acquisitionAgreement) {
      this.acquisitionAgreement = acquisitionAgreement;
      this.sharingChanged = true;
    }
  }

  public void setUsedPID(String usedPID) {
    if (!(this.usedPID == null && usedPID == "") && this.usedPID != usedPID) {
      this.usedPID = usedPID;
      this.sharingChanged = true;
    }
  }

  public void setUsedPIDTxt(String usedPIDTxt) {
    if (!(this.usedPIDTxt == null && usedPIDTxt == "") && this.usedPIDTxt != usedPIDTxt) {
      this.usedPIDTxt = usedPIDTxt;
      this.sharingChanged = true;
    }
  }

  public void setStorageChanged(boolean storageChanged) {
    this.storageChanged = storageChanged;
  }

  public void setStorageResponsible(String storageResponsible) {
    if (!(this.storageResponsible == null && storageResponsible == "")
        && this.storageResponsible != storageResponsible) {
      this.storageResponsible = storageResponsible;
      this.storageChanged = true;
    }
  }

  public void setStorageTechnologies(String storageTechnologies) {
    if (!(this.storageTechnologies == null && storageTechnologies == "")
        && this.storageTechnologies != storageTechnologies) {
      this.storageTechnologies = storageTechnologies;
      this.storageChanged = true;
    }
  }

  public void setStoragePlaces(String storagePlaces) {
    if (!(this.storagePlaces == null && storagePlaces == "") && this.storagePlaces != storagePlaces) {
      this.storagePlaces = storagePlaces;
      this.storageChanged = true;
    }
  }

  public void setStorageBackups(String storageBackups) {
    if (!(this.storageBackups == null && storageBackups == "") && this.storageBackups != storageBackups) {
      this.storageBackups = storageBackups;
      this.storageChanged = true;
    }
  }

  public void setStorageTransfer(String storageTransfer) {
    if (!(this.storageTransfer == null && storageTransfer == "") && this.storageTransfer != storageTransfer) {
      this.storageTransfer = storageTransfer;
      this.storageChanged = true;
    }
  }

  public void setStorageExpectedSize(String storageExpectedSize) {
    if (!(this.storageExpectedSize == null && storageExpectedSize == "")
        && this.storageExpectedSize != storageExpectedSize) {
      this.storageExpectedSize = storageExpectedSize;
      this.storageChanged = true;
    }
  }

  public void setStorageRequirements(boolean storageRequirements) {
    if (this.storageRequirements != storageRequirements) {
      this.storageRequirements = storageRequirements;
      this.storageChanged = true;
    }
  }

  public void setStorageRequirementsTxt(String storageRequirementsTxt) {
    if (!(this.storageRequirementsTxt == null && storageRequirementsTxt == "")
        && this.storageRequirementsTxt != storageRequirementsTxt) {
      this.storageRequirementsTxt = storageRequirementsTxt;
      this.storageChanged = true;
    }
  }

  public void setStorageSuccession(boolean storageSuccession) {
    if (this.storageSuccession != storageSuccession) {
      this.storageSuccession = storageSuccession;
      this.storageChanged = true;
    }
  }

  public void setStorageSuccessionTxt(String storageSuccessionTxt) {
    if (!(this.storageSuccessionTxt == null && storageSuccessionTxt == "")
        && this.storageSuccessionTxt != storageSuccessionTxt) {
      this.storageSuccessionTxt = storageSuccessionTxt;
      this.storageChanged = true;
    }
  }

  public void setOrganizationChanged(boolean organizationChanged) {
    this.organizationChanged = organizationChanged;
  }

  public void setFrameworkNationality(String frameworkNationality) {
    if (!(this.frameworkNationality == null && frameworkNationality == "")
        && this.frameworkNationality != frameworkNationality) {
      this.frameworkNationality = frameworkNationality;
      this.organizationChanged = true;
    }
  }

  public void setFrameworkNationalityTxt(String frameworkNationalityTxt) {
    if (!(this.frameworkNationalityTxt == null && frameworkNationalityTxt == "")
        && this.frameworkNationalityTxt != frameworkNationalityTxt) {
      this.frameworkNationalityTxt = frameworkNationalityTxt;
      this.organizationChanged = true;
    }
  }

  public void setResponsibleUnit(String responsibleUnit) {
    if (!(this.responsibleUnit == null && responsibleUnit == "") && this.responsibleUnit != responsibleUnit) {
      this.responsibleUnit = responsibleUnit;
      this.organizationChanged = true;
    }
  }

  public void setInvolvedInstitutions(String involvedInstitutions) {
    if (!(this.involvedInstitutions == null && involvedInstitutions == "")
        && this.involvedInstitutions != involvedInstitutions) {
      this.involvedInstitutions = involvedInstitutions;
      this.organizationChanged = true;
    }
  }

  public void setInvolvedInformed(boolean involvedInformed) {
    if (this.involvedInformed != involvedInformed) {
      this.involvedInformed = involvedInformed;
      this.organizationChanged = true;
    }
  }

  public void setContributionsDefined(boolean contributionsDefined) {
    if (this.contributionsDefined != contributionsDefined) {
      this.contributionsDefined = contributionsDefined;
      this.organizationChanged = true;
    }
  }

  public void setContributionsDefinedTxt(String contributionsDefinedTxt) {
    if (!(this.contributionsDefinedTxt == null && contributionsDefinedTxt == "")
        && this.contributionsDefinedTxt != contributionsDefinedTxt) {
      this.contributionsDefinedTxt = contributionsDefinedTxt;
      this.organizationChanged = true;
    }
  }

  public void setGivenConsent(boolean givenConsent) {
    if (this.givenConsent != givenConsent) {
      this.givenConsent = givenConsent;
      this.organizationChanged = true;
    }
  }

  public void setManagementWorkflow(boolean managementWorkflow) {
    if (this.managementWorkflow != managementWorkflow) {
      this.managementWorkflow = managementWorkflow;
      this.organizationChanged = true;
    }
  }

  public void setManagementWorkflowTxt(String managementWorkflowTxt) {
    if (!(this.managementWorkflowTxt == null && managementWorkflowTxt == "")
        && this.managementWorkflowTxt != managementWorkflowTxt) {
      this.managementWorkflowTxt = managementWorkflowTxt;
      this.organizationChanged = true;
    }
  }

  public void setStaffDescription(boolean staffDescription) {
    if (this.staffDescription != staffDescription) {
      this.staffDescription = staffDescription;
      this.organizationChanged = true;
    }
  }

  public void setStaffDescriptionTxt(String staffDescriptionTxt) {
    if (!(this.staffDescriptionTxt == null && staffDescriptionTxt == "")
        && this.staffDescriptionTxt != staffDescriptionTxt) {
      this.staffDescriptionTxt = staffDescriptionTxt;
      this.organizationChanged = true;
    }
  }

  public void setFunderRequirements(String funderRequirements) {
    if (!(this.funderRequirements == null && funderRequirements == "")
        && this.funderRequirements != funderRequirements) {
      this.funderRequirements = funderRequirements;
      this.organizationChanged = true;
    }
  }

  public void setProviderRequirements(String providerRequirements) {
    if (!(this.providerRequirements == null && providerRequirements == "")
        && this.providerRequirements != providerRequirements) {
      this.providerRequirements = providerRequirements;
      this.organizationChanged = true;
    }
  }

  public void setRepoPolicies(String repoPolicies) {
    if (!(this.repoPolicies == null && repoPolicies == "") && this.repoPolicies != repoPolicies) {
      this.repoPolicies = repoPolicies;
      this.organizationChanged = true;
    }
  }

  public void setRepoPoliciesResponsible(String repoPoliciesResponsible) {
    if (!(this.repoPoliciesResponsible == null && repoPoliciesResponsible == "")
        && this.repoPoliciesResponsible != repoPoliciesResponsible) {
      this.repoPoliciesResponsible = repoPoliciesResponsible;
      this.organizationChanged = true;
    }
  }

  public void setPlanningAdherence(String planningAdherence) {
    if (!(this.planningAdherence == null && planningAdherence == "") && this.planningAdherence != planningAdherence) {
      this.planningAdherence = planningAdherence;
      this.organizationChanged = true;
    }
  }

  public void setEthicalChanged(boolean ethicalChanged) {
    this.ethicalChanged = ethicalChanged;
  }

  public void setDataProtection(boolean dataProtection) {
    if (this.dataProtection != dataProtection) {
      this.dataProtection = dataProtection;
      this.ethicalChanged = true;
    }
  }

  public void setProtectionRequirements(String protectionRequirements) {
    if (!(this.protectionRequirements == null && protectionRequirements == "")
        && this.protectionRequirements != protectionRequirements) {
      this.protectionRequirements = protectionRequirements;
      this.ethicalChanged = true;
    }
  }

  public void setConsentObtained(boolean consentObtained) {
    if (this.consentObtained != consentObtained) {
      this.consentObtained = consentObtained;
      this.ethicalChanged = true;
    }
  }

  public void setConsentObtainedTxt(String consentObtainedTxt) {
    if (!(this.consentObtainedTxt == null && consentObtainedTxt == "")
        && this.consentObtainedTxt != consentObtainedTxt) {
      this.consentObtainedTxt = consentObtainedTxt;
      this.ethicalChanged = true;
    }
  }

  public void setSharingConsidered(boolean sharingConsidered) {
    if (this.sharingConsidered != sharingConsidered) {
      this.sharingConsidered = sharingConsidered;
      this.ethicalChanged = true;
    }
  }

  public void setIrbApproval(boolean irbApproval) {
    if (this.irbApproval != irbApproval) {
      this.irbApproval = irbApproval;
      this.ethicalChanged = true;
    }
  }

  public void setIrbApprovalTxt(String irbApprovalTxt) {
    if (!(this.irbApprovalTxt == null && irbApprovalTxt == "") && this.irbApprovalTxt != irbApprovalTxt) {
      this.irbApprovalTxt = irbApprovalTxt;
      this.ethicalChanged = true;
    }
  }

  public void setSensitiveDataIncluded(boolean sensitiveDataIncluded) {
    if (this.sensitiveDataIncluded != sensitiveDataIncluded) {
      this.sensitiveDataIncluded = sensitiveDataIncluded;
      this.ethicalChanged = true;
    }
  }

  public void setSensitiveDataIncludedTxt(String sensitiveDataIncludedTxt) {
    if (!(this.sensitiveDataIncludedTxt == null && sensitiveDataIncludedTxt == "")
        && this.sensitiveDataIncludedTxt != sensitiveDataIncludedTxt) {
      this.sensitiveDataIncludedTxt = sensitiveDataIncludedTxt;
      this.ethicalChanged = true;
    }
  }

  public void setExternalCopyright(boolean externalCopyright) {
    if (this.externalCopyright != externalCopyright) {
      this.externalCopyright = externalCopyright;
      this.ethicalChanged = true;
    }
  }

  public void setExternalCopyrightTxt(String externalCopyrightTxt) {
    if (!(this.externalCopyrightTxt == null && externalCopyrightTxt == "")
        && this.externalCopyrightTxt != externalCopyrightTxt) {
      this.externalCopyrightTxt = externalCopyrightTxt;
      this.ethicalChanged = true;
    }
  }

  public void setInternalCopyright(boolean internalCopyright) {
    if (this.internalCopyright != internalCopyright) {
      this.internalCopyright = internalCopyright;
      this.ethicalChanged = true;
    }
  }

  public void setInternalCopyrightTxt(String internalCopyrightTxt) {
    if (!(this.internalCopyrightTxt == null && internalCopyrightTxt == "")
        && this.internalCopyrightTxt != internalCopyrightTxt) {
      this.internalCopyrightTxt = internalCopyrightTxt;
      this.ethicalChanged = true;
    }
  }

  public void setCostsChanged(boolean costsChanged) {
    this.costsChanged = costsChanged;
  }

  public void setSpecificCosts(String specificCosts) {
    if (!(this.specificCosts == null && specificCosts == "") && this.specificCosts != specificCosts) {
      this.specificCosts = specificCosts;
      this.costsChanged = true;
    }
  }

  public void setSpecificCostsTxt(String specificCostsTxt) {
    if (!(this.specificCostsTxt == null && specificCostsTxt == "") && this.specificCostsTxt != specificCostsTxt) {
      this.specificCostsTxt = specificCostsTxt;
      this.costsChanged = true;
    }
  }

  public void setAriseCosts(String ariseCosts) {
    if (!(this.ariseCosts == null && ariseCosts == "") && this.ariseCosts != ariseCosts) {
      this.ariseCosts = ariseCosts;
      this.costsChanged = true;
    }
  }

  public void setBearCost(String bearCost) {
    if (!(this.bearCost == null && bearCost == "") && this.bearCost != bearCost) {
      this.bearCost = bearCost;
      this.costsChanged = true;
    }
  }

}
