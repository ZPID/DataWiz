package de.zpid.datawiz.dto;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

import javax.validation.constraints.Size;

import de.zpid.datawiz.util.ListUtil;

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
  private long id;

  // ***************** Administrative Data *****************
  /** checks if the fields of the Administrative Data has changed, this is used for particular saving. */
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
  /** checks if the fields of the Research Data has changed, this is used for particular saving. */
  private boolean researchChanged = false;

  /**
   * group interface for validation - see
   * {@link http://stackoverflow.com/questions/19190592/manually-call-spring-annotation-validation}
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
  /** checks if the fields of the MetaData Data has changed, this is used for particular saving. */
  private boolean metaChanged = false;

  /**
   * group interface for validation - see
   * {@link http://stackoverflow.com/questions/19190592/manually-call-spring-annotation-validation}
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
  /** checks if the fields of the Data Sharing has changed, this is used for particular saving. */
  private boolean sharingChanged = false;

  /**
   * group interface for validation - see
   * {@link http://stackoverflow.com/questions/19190592/manually-call-spring-annotation-validation}
   */
  public interface SharingVal {
  }

  /** DMP39. */
  private boolean releaseObligation;

  /** DMP40. */
  @Size(min = 0, max = 1000, groups = SharingVal.class)
  private String expectedGroups;

  /** DMP42. */
  private boolean searchableData;

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

  /** DMP49 - if accessCost == true. */
  @Size(min = 0, max = 500, groups = SharingVal.class)
  private String accessCostsTxt;

  /** DMP50. */
  @Size(min = 0, max = 2000, groups = SharingVal.class)
  private String accessTermsImplementation;

  /** DMP51. */
  private boolean clarifiedRights;

  /** DMP51 - if clarifiedRights == true. */
  @Size(min = 0, max = 500, groups = SharingVal.class)
  private String clarifiedRightsTxt;

  /** DMP52. */
  private boolean acquisitionAgreement;

  /** DMP53 (select). */
  private String usedPID;

  /** DMP53 (select)-> other selected. */
  @Size(min = 0, max = 500, groups = SharingVal.class)
  private String usedPIDTxt;

  // ***************** Storage and infrastructure *****************
  /** checks if the fields of the Storage has changed, this is used for particular saving. */
  private boolean storageChanged = false;

  /**
   * group interface for validation - see
   * {@link http://stackoverflow.com/questions/19190592/manually-call-spring-annotation-validation}
   */
  public interface StorageVal {
  }

  /** DMP54. */
  @Size(min = 0, max = 1000, groups = StorageVal.class)
  private String storageResponsible;

  /** DMP55. */
  @Size(min = 0, max = 1000, groups = StorageVal.class)
  private String storageTechnologies;

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
  /** checks if the fields of the Organization has changed, this is used for particular saving. */
  private boolean organizationChanged = false;

  /**
   * group interface for validation - see
   * {@link http://stackoverflow.com/questions/19190592/manually-call-spring-annotation-validation}
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

  /** DMP69. */
  @Size(min = 0, max = 2000, groups = OrganizationVal.class)
  private String providerRequirements;

  /** DMP70 -> if DMP43 archive or repo. */
  @Size(min = 0, max = 1000, groups = OrganizationVal.class)
  private String repoPolicies;

  /** DMP71 -> if DMP43 archive or repo. */
  @Size(min = 0, max = 1000, groups = OrganizationVal.class)
  private String repoPoliciesResponsible;

  /** DMP72. */
  @Size(min = 0, max = 1000, groups = OrganizationVal.class)
  private String planningAdherence;

  // ***************** Ethical and legal aspects *****************
  /** checks if the fields of the Ethical has changed, this is used for particular saving. */
  private boolean ethicalChanged = false;

  /**
   * group interface for validation - see
   * {@link http://stackoverflow.com/questions/19190592/manually-call-spring-annotation-validation}
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
  /** checks if the fields of the Costs has changed, this is used for particular saving. */
  private boolean costsChanged = false;

  /**
   * group interface for validation - see
   * {@link http://stackoverflow.com/questions/19190592/manually-call-spring-annotation-validation}
   */
  public interface CostsVal {
  }

  /** DMP83. */
  private String specificCosts;

  /** DMP83. -> if DMP83 == true */
  @Size(min = 0, max = 1000, groups = CostsVal.class)
  private String specificCostsTxt;

  /** DMP84. -> if DMP83 == true */
  @Size(min = 0, max = 1000, groups = CostsVal.class)
  private String ariseCosts;

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
   * Checks if is {@link #adminChanged}.
   *
   * @return true, if is admin changed
   */
  public boolean isAdminChanged() {
    return adminChanged;
  }

  /**
   * Getter for {@link #projectAims}.
   *
   * @return projectAims
   */
  public String getProjectAims() {
    return projectAims;
  }

  /**
   * Getter for {@link #projectSponsors}.
   *
   * @return projectSponsors
   */
  public String getProjectSponsors() {
    return projectSponsors;
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
   * Checks if is {@link #researchChanged}.
   *
   * @return true, if is research changed
   */
  public boolean isResearchChanged() {
    return researchChanged;
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
   * Getter for {@link #workingCopyTxt}.
   *
   * @return workingCopyTxt
   */
  public String getWorkingCopyTxt() {
    return workingCopyTxt;
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
   * Getter for {@link #goodScientificTxt}.
   *
   * @return goodScientificTxt
   */
  public String getGoodScientificTxt() {
    return goodScientificTxt;
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
   * Getter for {@link #subsequentUseTxt}.
   *
   * @return subsequentUseTxt
   */
  public String getSubsequentUseTxt() {
    return subsequentUseTxt;
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
   * Getter for {@link #requirementsTxt}.
   *
   * @return requirementsTxt
   */
  public String getRequirementsTxt() {
    return requirementsTxt;
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
   * Getter for {@link #documentationTxt}.
   *
   * @return documentationTxt
   */
  public String getDocumentationTxt() {
    return documentationTxt;
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
   * Getter for {@link #selectionSoftware}.
   *
   * @return selectionSoftware
   */
  public String getSelectionSoftware() {
    return selectionSoftware;
  }

  /**
   * Getter for {@link #selectionCriteria}.
   *
   * @return selectionCriteria
   */
  public String getSelectionCriteria() {
    return selectionCriteria;
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
   * Checks if is {@link #metaChanged}.
   *
   * @return true, if is meta changed
   */
  public boolean isMetaChanged() {
    return metaChanged;
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
   * Checks if is {@link #sharingChanged}.
   *
   * @return true, if is sharing changed
   */
  public boolean isSharingChanged() {
    return sharingChanged;
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
   * Getter for {@link #expectedGroups}.
   *
   * @return expectedGroups
   */
  public String getExpectedGroups() {
    return expectedGroups;
  }

  /**
   * Checks if is {@link #searchableData}.
   *
   * @return true, if is searchable data
   */
  public boolean isSearchableData() {
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
   * Getter for {@link #accessCostsTxt}.
   *
   * @return accessCostsTxt
   */
  public String getAccessCostsTxt() {
    return accessCostsTxt;
  }

  /**
   * Getter for {@link #accessTermsImplementation}.
   *
   * @return accessTermsImplementation
   */
  public String getAccessTermsImplementation() {
    return accessTermsImplementation;
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
   * Getter for {@link #clarifiedRightsTxt}.
   *
   * @return clarifiedRightsTxt
   */
  public String getClarifiedRightsTxt() {
    return clarifiedRightsTxt;
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
   * Checks if is {@link #storageChanged}.
   *
   * @return true, if is storage changed
   */
  public boolean isStorageChanged() {
    return storageChanged;
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
  public String getStorageTechnologies() {
    return storageTechnologies;
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
   * Checks if is {@link #organizationChanged}.
   *
   * @return true, if is organization changed
   */
  public boolean isOrganizationChanged() {
    return organizationChanged;
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
   * Getter for {@link #providerRequirements}.
   *
   * @return providerRequirements
   */
  public String getProviderRequirements() {
    return providerRequirements;
  }

  /**
   * Getter for {@link #repoPolicies}.
   *
   * @return repoPolicies
   */
  public String getRepoPolicies() {
    return repoPolicies;
  }

  /**
   * Getter for {@link #repoPoliciesResponsible}.
   *
   * @return repoPoliciesResponsible
   */
  public String getRepoPoliciesResponsible() {
    return repoPoliciesResponsible;
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
   * Checks if is {@link #ethicalChanged}.
   *
   * @return true, if is ethical changed
   */
  public boolean isEthicalChanged() {
    return ethicalChanged;
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
   * Checks if is {@link #costsChanged}.
   *
   * @return true, if is costs changed
   */
  public boolean isCostsChanged() {
    return costsChanged;
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
   * Getter for {@link #ariseCosts}.
   *
   * @return ariseCosts
   */
  public String getAriseCosts() {
    return ariseCosts;
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
   * Setter for {@link #adminChanged}.
   *
   * @param adminChanged
   *          -> this.adminChanged
   */
  public void setAdminChanged(boolean adminChanged) {
    this.adminChanged = adminChanged;
  }

  /**
   * Setter for {@link #projectAims}.
   *
   * @param projectAims
   *          -> this.projectAims
   */
  public void setProjectAims(String projectAims) {
    if (!Objects.equals(this.projectAims, projectAims)) {
      this.projectAims = projectAims;
      this.adminChanged = true;
    }
  }

  /**
   * Setter for {@link #projectSponsors}.
   *
   * @param projectSponsors
   *          -> this.projectSponsors
   */
  public void setProjectSponsors(String projectSponsors) {
    if (!Objects.equals(this.projectSponsors, projectSponsors)) {
      this.projectSponsors = projectSponsors;
      this.adminChanged = true;
    }
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
      this.adminChanged = true;
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
      this.adminChanged = true;
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
      this.adminChanged = true;
    }
  }

  /**
   * Setter for {@link #researchChanged}.
   *
   * @param researchChanged
   *          -> this.researchChanged
   */
  public void setResearchChanged(boolean researchChanged) {
    this.researchChanged = researchChanged;
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
      this.researchChanged = true;
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
      this.researchChanged = true;
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
      this.researchChanged = true;
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
      this.researchChanged = true;
    }
  }

  /**
   * Setter for {@link #usedDataTypes}.
   *
   * @param usedDataTypes
   *          -> this.usedDataTypes
   */
  public void setUsedDataTypes(List<Integer> usedDataTypes) {
    if ((this.usedDataTypes != null && this.usedDataTypes.size() > 0)
        || (usedDataTypes != null && usedDataTypes.size() > 0))
      if (!ListUtil.equalsWithoutOrder(this.usedDataTypes, usedDataTypes)) {
        this.usedDataTypes = usedDataTypes;
        this.researchChanged = true;
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
      this.researchChanged = true;
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
      this.researchChanged = true;
    }

  }

  /**
   * Setter for {@link #usedCollectionModes}.
   *
   * @param usedCollectionModes
   *          -> this.usedCollectionModes
   */
  public void setUsedCollectionModes(List<Integer> usedCollectionModes) {
    if ((this.usedCollectionModes != null && this.usedCollectionModes.size() > 0)
        || (usedCollectionModes != null && usedCollectionModes.size() > 0))
      if (!ListUtil.equalsWithoutOrder(this.usedCollectionModes, usedCollectionModes)) {
        this.usedCollectionModes = usedCollectionModes;
        this.researchChanged = true;
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
      this.researchChanged = true;
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
      this.researchChanged = true;
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
      this.researchChanged = true;
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
      this.researchChanged = true;
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
      this.researchChanged = true;
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
      this.researchChanged = true;
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
      this.researchChanged = true;
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
      this.researchChanged = true;
    }

  }

  /**
   * Setter for {@link #workingCopyTxt}.
   *
   * @param workingCopyTxt
   *          -> this.workingCopyTxt
   */
  public void setWorkingCopyTxt(String workingCopyTxt) {
    if (!Objects.equals(this.workingCopyTxt, workingCopyTxt)) {
      this.workingCopyTxt = workingCopyTxt;
      this.researchChanged = true;
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
      this.researchChanged = true;
    }

  }

  /**
   * Setter for {@link #goodScientificTxt}.
   *
   * @param goodScientificTxt
   *          -> this.goodScientificTxt
   */
  public void setGoodScientificTxt(String goodScientificTxt) {
    if (!Objects.equals(this.goodScientificTxt, goodScientificTxt)) {
      this.goodScientificTxt = goodScientificTxt;
      this.researchChanged = true;
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
      this.researchChanged = true;
    }

  }

  /**
   * Setter for {@link #subsequentUseTxt}.
   *
   * @param subsequentUseTxt
   *          -> this.subsequentUseTxt
   */
  public void setSubsequentUseTxt(String subsequentUseTxt) {
    if (!Objects.equals(this.subsequentUseTxt, subsequentUseTxt)) {
      this.subsequentUseTxt = subsequentUseTxt;
      this.researchChanged = true;
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
      this.researchChanged = true;
    }

  }

  /**
   * Setter for {@link #requirementsTxt}.
   *
   * @param requirementsTxt
   *          -> this.requirementsTxt
   */
  public void setRequirementsTxt(String requirementsTxt) {
    if (!Objects.equals(this.requirementsTxt, requirementsTxt)) {
      this.requirementsTxt = requirementsTxt;
      this.researchChanged = true;
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
      this.researchChanged = true;
    }

  }

  /**
   * Setter for {@link #documentationTxt}.
   *
   * @param documentationTxt
   *          -> this.documentationTxt
   */
  public void setDocumentationTxt(String documentationTxt) {
    if (!Objects.equals(this.documentationTxt, documentationTxt)) {
      this.documentationTxt = documentationTxt;
      this.researchChanged = true;
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
      this.researchChanged = true;
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
      this.researchChanged = true;
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
      this.researchChanged = true;
    }

  }

  /**
   * Setter for {@link #selectionSoftware}.
   *
   * @param selectionSoftware
   *          -> this.selectionSoftware
   */
  public void setSelectionSoftware(String selectionSoftware) {
    if (!Objects.equals(this.selectionSoftware, selectionSoftware)) {
      this.selectionSoftware = selectionSoftware;
      this.researchChanged = true;
    }

  }

  /**
   * Setter for {@link #selectionCriteria}.
   *
   * @param selectionCriteria
   *          -> this.selectionCriteria
   */
  public void setSelectionCriteria(String selectionCriteria) {
    if (!Objects.equals(this.selectionCriteria, selectionCriteria)) {
      this.selectionCriteria = selectionCriteria;
      this.researchChanged = true;
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
      this.researchChanged = true;
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
      this.researchChanged = true;
    }

  }

  /**
   * Setter for {@link #metaChanged}.
   *
   * @param metaChanged
   *          -> this.metaChanged
   */
  public void setMetaChanged(boolean metaChanged) {
    this.metaChanged = metaChanged;
  }

  /**
   * Setter for {@link #selectedMetaPurposes}.
   *
   * @param selectedMetaPurposes
   *          -> this.selectedMetaPurposes
   */
  public void setSelectedMetaPurposes(List<Integer> selectedMetaPurposes) {
    if ((this.selectedMetaPurposes != null && this.selectedMetaPurposes.size() > 0)
        || (selectedMetaPurposes != null && selectedMetaPurposes.size() > 0))
      if (!ListUtil.equalsWithoutOrder(this.selectedMetaPurposes, selectedMetaPurposes)) {
        this.selectedMetaPurposes = selectedMetaPurposes;
        this.metaChanged = true;
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
      this.metaChanged = true;
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
      this.metaChanged = true;
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
      this.metaChanged = true;
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
      this.metaChanged = true;
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
      this.metaChanged = true;
    }

  }

  /**
   * Setter for {@link #sharingChanged}.
   *
   * @param sharingChanged
   *          -> this.sharingChanged
   */
  public void setSharingChanged(boolean sharingChanged) {
    this.sharingChanged = sharingChanged;
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
      this.sharingChanged = true;
    }

  }

  /**
   * Setter for {@link #expectedGroups}.
   *
   * @param expectedGroups
   *          -> this.expectedGroups
   */
  public void setExpectedGroups(String expectedGroups) {
    if (!Objects.equals(this.expectedGroups, expectedGroups)) {
      this.expectedGroups = expectedGroups;
      this.sharingChanged = true;
    }

  }

  /**
   * Setter for {@link #searchableData}.
   *
   * @param searchableData
   *          -> this.searchableData
   */
  public void setSearchableData(boolean searchableData) {
    if (!Objects.equals(this.searchableData, searchableData)) {
      this.searchableData = searchableData;
      this.sharingChanged = true;
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
      this.sharingChanged = true;
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
      this.sharingChanged = true;
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
      this.sharingChanged = true;
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
      this.sharingChanged = true;
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
      this.sharingChanged = true;
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
      this.sharingChanged = true;
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
      this.sharingChanged = true;
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
      this.sharingChanged = true;
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
      this.sharingChanged = true;
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
      this.sharingChanged = true;
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
      this.sharingChanged = true;
    }

  }

  /**
   * Setter for {@link #accessCostsTxt}.
   *
   * @param accessCostsTxt
   *          -> this.accessCostsTxt
   */
  public void setAccessCostsTxt(String accessCostsTxt) {
    if (!Objects.equals(this.accessCostsTxt, accessCostsTxt)) {
      this.accessCostsTxt = accessCostsTxt;
      this.sharingChanged = true;
    }

  }

  /**
   * Setter for {@link #accessTermsImplementation}.
   *
   * @param accessTermsImplementation
   *          -> this.accessTermsImplementation
   */
  public void setAccessTermsImplementation(String accessTermsImplementation) {
    if (!Objects.equals(this.accessTermsImplementation, accessTermsImplementation)) {
      this.accessTermsImplementation = accessTermsImplementation;
      this.sharingChanged = true;
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
      this.sharingChanged = true;
    }

  }

  /**
   * Setter for {@link #clarifiedRightsTxt}.
   *
   * @param clarifiedRightsTxt
   *          -> this.clarifiedRightsTxt
   */
  public void setClarifiedRightsTxt(String clarifiedRightsTxt) {
    if (!Objects.equals(this.clarifiedRightsTxt, clarifiedRightsTxt)) {
      this.clarifiedRightsTxt = clarifiedRightsTxt;
      this.sharingChanged = true;
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
      this.sharingChanged = true;
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
      this.sharingChanged = true;
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
      this.sharingChanged = true;
    }

  }

  /**
   * Setter for {@link #storageChanged}.
   *
   * @param storageChanged
   *          -> this.storageChanged
   */
  public void setStorageChanged(boolean storageChanged) {
    this.storageChanged = storageChanged;
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
      this.storageChanged = true;
    }

  }

  /**
   * Setter for {@link #storageTechnologies}.
   *
   * @param storageTechnologies
   *          -> this.storageTechnologies
   */
  public void setStorageTechnologies(String storageTechnologies) {
    if (!Objects.equals(this.storageTechnologies, storageTechnologies)) {
      this.storageTechnologies = storageTechnologies;
      this.storageChanged = true;
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
      this.storageChanged = true;
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
      this.storageChanged = true;
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
      this.storageChanged = true;
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
      this.storageChanged = true;
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
      this.storageChanged = true;
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
      this.storageChanged = true;
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
      this.storageChanged = true;
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
      this.storageChanged = true;
    }

  }

  /**
   * Setter for {@link #organizationChanged}.
   *
   * @param organizationChanged
   *          -> this.organizationChanged
   */
  public void setOrganizationChanged(boolean organizationChanged) {
    this.organizationChanged = organizationChanged;
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
      this.organizationChanged = true;
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
      this.organizationChanged = true;
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
      this.organizationChanged = true;
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
      this.organizationChanged = true;
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
      this.organizationChanged = true;
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
      this.organizationChanged = true;
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
      this.organizationChanged = true;
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
      this.organizationChanged = true;
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
      this.organizationChanged = true;
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
      this.organizationChanged = true;
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
      this.organizationChanged = true;
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
      this.organizationChanged = true;
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
      this.organizationChanged = true;
    }

  }

  /**
   * Setter for {@link #providerRequirements}.
   *
   * @param providerRequirements
   *          -> this.providerRequirements
   */
  public void setProviderRequirements(String providerRequirements) {
    if (!Objects.equals(this.providerRequirements, providerRequirements)) {
      this.providerRequirements = providerRequirements;
      this.organizationChanged = true;
    }

  }

  /**
   * Setter for {@link #repoPolicies}.
   *
   * @param repoPolicies
   *          -> this.repoPolicies
   */
  public void setRepoPolicies(String repoPolicies) {
    if (!Objects.equals(this.repoPolicies, repoPolicies)) {
      this.repoPolicies = repoPolicies;
      this.organizationChanged = true;
    }

  }

  /**
   * Setter for {@link #repoPoliciesResponsible}.
   *
   * @param repoPoliciesResponsible
   *          -> this.repoPoliciesResponsible
   */
  public void setRepoPoliciesResponsible(String repoPoliciesResponsible) {
    if (!Objects.equals(this.repoPoliciesResponsible, repoPoliciesResponsible)) {
      this.repoPoliciesResponsible = repoPoliciesResponsible;
      this.organizationChanged = true;
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
      this.organizationChanged = true;
    }

  }

  /**
   * Setter for {@link #ethicalChanged}.
   *
   * @param ethicalChanged
   *          -> this.ethicalChanged
   */
  public void setEthicalChanged(boolean ethicalChanged) {
    this.ethicalChanged = ethicalChanged;
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
      this.ethicalChanged = true;
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
      this.ethicalChanged = true;
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
      this.ethicalChanged = true;
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
      this.ethicalChanged = true;
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
      this.ethicalChanged = true;
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
      this.ethicalChanged = true;
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
      this.ethicalChanged = true;
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
      this.ethicalChanged = true;
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
      this.ethicalChanged = true;
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
      this.ethicalChanged = true;
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
      this.ethicalChanged = true;
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
      this.ethicalChanged = true;
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
      this.ethicalChanged = true;
    }

  }

  /**
   * Setter for {@link #costsChanged}.
   *
   * @param costsChanged
   *          -> this.costsChanged
   */
  public void setCostsChanged(boolean costsChanged) {
    this.costsChanged = costsChanged;
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
      this.costsChanged = true;
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
      this.costsChanged = true;
    }

  }

  /**
   * Setter for {@link #ariseCosts}.
   *
   * @param ariseCosts
   *          -> this.ariseCosts
   */
  public void setAriseCosts(String ariseCosts) {
    if (!Objects.equals(this.ariseCosts, ariseCosts)) {
      this.ariseCosts = ariseCosts;
      this.costsChanged = true;
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
      this.costsChanged = true;
    }

  }

  @Override
  public String toString() {
    return "DmpDTO [id=" + id + ", adminChanged=" + adminChanged + ", projectAims=" + projectAims + ", projectSponsors="
        + projectSponsors + ", duration=" + duration + ", organizations=" + organizations + ", planAims=" + planAims
        + ", researchChanged=" + researchChanged + ", existingData=" + existingData + ", dataCitation=" + dataCitation
        + ", existingDataRelevance=" + existingDataRelevance + ", existingDataIntegration=" + existingDataIntegration
        + ", usedDataTypes=" + usedDataTypes + ", otherDataTypes=" + otherDataTypes + ", dataReproducibility="
        + dataReproducibility + ", usedCollectionModes=" + usedCollectionModes + ", otherCMIP=" + otherCMIP
        + ", otherCMINP=" + otherCMINP + ", measOccasions=" + measOccasions + ", reliabilityTraining="
        + reliabilityTraining + ", multipleMeasurements=" + multipleMeasurements + ", qualitityOther=" + qualitityOther
        + ", fileFormat=" + fileFormat + ", workingCopy=" + workingCopy + ", workingCopyTxt=" + workingCopyTxt
        + ", goodScientific=" + goodScientific + ", goodScientificTxt=" + goodScientificTxt + ", subsequentUse="
        + subsequentUse + ", subsequentUseTxt=" + subsequentUseTxt + ", requirements=" + requirements
        + ", requirementsTxt=" + requirementsTxt + ", documentation=" + documentation + ", documentationTxt="
        + documentationTxt + ", dataSelection=" + dataSelection + ", selectionTime=" + selectionTime
        + ", selectionResp=" + selectionResp + ", selectionSoftware=" + selectionSoftware + ", selectionCriteria="
        + selectionCriteria + ", storageDuration=" + storageDuration + ", deleteProcedure=" + deleteProcedure
        + ", metaChanged=" + metaChanged + ", selectedMetaPurposes=" + selectedMetaPurposes + ", metaDescription="
        + metaDescription + ", metaFramework=" + metaFramework + ", metaGeneration=" + metaGeneration + ", metaMonitor="
        + metaMonitor + ", metaFormat=" + metaFormat + ", sharingChanged=" + sharingChanged + ", releaseObligation="
        + releaseObligation + ", expectedGroups=" + expectedGroups + ", searchableData=" + searchableData
        + ", expectedUsage=" + expectedUsage + ", publStrategy=" + publStrategy + ", accessReasonAuthor="
        + accessReasonAuthor + ", noAccessReason=" + noAccessReason + ", noAccessReasonOther=" + noAccessReasonOther
        + ", depositName=" + depositName + ", transferTime=" + transferTime + ", sensitiveData=" + sensitiveData
        + ", initialUsage=" + initialUsage + ", usageRestriction=" + usageRestriction + ", accessCosts=" + accessCosts
        + ", accessCostsTxt=" + accessCostsTxt + ", accessTermsImplementation=" + accessTermsImplementation
        + ", clarifiedRights=" + clarifiedRights + ", clarifiedRightsTxt=" + clarifiedRightsTxt
        + ", acquisitionAgreement=" + acquisitionAgreement + ", usedPID=" + usedPID + ", usedPIDTxt=" + usedPIDTxt
        + ", storageChanged=" + storageChanged + ", storageResponsible=" + storageResponsible + ", storageTechnologies="
        + storageTechnologies + ", storagePlaces=" + storagePlaces + ", storageBackups=" + storageBackups
        + ", storageTransfer=" + storageTransfer + ", storageExpectedSize=" + storageExpectedSize
        + ", storageRequirements=" + storageRequirements + ", storageRequirementsTxt=" + storageRequirementsTxt
        + ", storageSuccession=" + storageSuccession + ", storageSuccessionTxt=" + storageSuccessionTxt
        + ", organizationChanged=" + organizationChanged + ", frameworkNationality=" + frameworkNationality
        + ", frameworkNationalityTxt=" + frameworkNationalityTxt + ", responsibleUnit=" + responsibleUnit
        + ", involvedInstitutions=" + involvedInstitutions + ", involvedInformed=" + involvedInformed
        + ", contributionsDefined=" + contributionsDefined + ", contributionsDefinedTxt=" + contributionsDefinedTxt
        + ", givenConsent=" + givenConsent + ", managementWorkflow=" + managementWorkflow + ", managementWorkflowTxt="
        + managementWorkflowTxt + ", staffDescription=" + staffDescription + ", staffDescriptionTxt="
        + staffDescriptionTxt + ", funderRequirements=" + funderRequirements + ", providerRequirements="
        + providerRequirements + ", repoPolicies=" + repoPolicies + ", repoPoliciesResponsible="
        + repoPoliciesResponsible + ", planningAdherence=" + planningAdherence + ", ethicalChanged=" + ethicalChanged
        + ", dataProtection=" + dataProtection + ", protectionRequirements=" + protectionRequirements
        + ", consentObtained=" + consentObtained + ", consentObtainedTxt=" + consentObtainedTxt + ", sharingConsidered="
        + sharingConsidered + ", irbApproval=" + irbApproval + ", irbApprovalTxt=" + irbApprovalTxt
        + ", sensitiveDataIncluded=" + sensitiveDataIncluded + ", sensitiveDataIncludedTxt=" + sensitiveDataIncludedTxt
        + ", externalCopyright=" + externalCopyright + ", externalCopyrightTxt=" + externalCopyrightTxt
        + ", internalCopyright=" + internalCopyright + ", internalCopyrightTxt=" + internalCopyrightTxt
        + ", costsChanged=" + costsChanged + ", specificCosts=" + specificCosts + ", specificCostsTxt="
        + specificCostsTxt + ", ariseCosts=" + ariseCosts + ", bearCost=" + bearCost + "]";
  }

}
