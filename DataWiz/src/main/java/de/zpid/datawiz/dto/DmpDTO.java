package de.zpid.datawiz.dto;

import java.io.Serializable;
import java.util.List;

import javax.validation.constraints.Size;

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
  private int id;

  // ***************** Administrative Data *****************
  /** DMP02. */
  @Size(min = 0, max = 2000)
  private String projectAims;

  /** DMP04. */
  @Size(min = 0, max = 250)
  private String duration;

  /** DMP05. */
  @Size(min = 0, max = 250)
  private String organizations;

  /** DMP07. */
  @Size(min = 0, max = 2000)
  private String planAims;

  // ***************** Research Data *****************
  /** DMP09 : 0 yes, existing data are used/ 1 no data were found/ 2 no search was carried out. */
  private String existingData;

  /** DMP97. */
  @Size(min = 0, max = 1000)
  private String dataCitation;

  /** DMP10. */
  @Size(min = 0, max = 1000)
  private String existingDataRelevance;

  /** DMP11. */
  @Size(min = 0, max = 1000)
  private String existingDataIntegration;

  /** DMP12 META096 Study Metadata. */
  private List<Integer> usedDataTypes;

  /** DMP12 -> selected == other. */
  @Size(min = 0, max = 1000)
  private String otherDataTypes;

  /** DMP13. */
  @Size(min = 0, max = 1000)
  private String dataReproducibility;

  /** DMP14. How will the data be collected or generated? */
  /** DMP14 - Subitem DMP14, PsychData - META096 */
  private List<Integer> usedCollectionModes;

  /** other Collection Modes with Invest. present */
  @Size(min = 0, max = 1000)
  private String otherCMIP;

  /** other Collection Modes with Invest. not present */
  @Size(min = 0, max = 1000)
  private String otherCMINP;

  /** Subitem DMP14, JARS - META094 Study Metadata. */
  @Size(min = 0, max = 250)
  private String instruments;

  /** Subitem DMP14, PsychData - META097 Study Metadata. */
  @Size(min = 0, max = 1000)
  private String measOccasions;

  /** Subitem DMP16. */
  /** Subitem DMP90, JARS - META131 Study Metadata. */
  @Size(min = 0, max = 1000)
  private String reliabilityTraining;

  /** Subitem DMP91, JARS - META132 Study Metadata. */
  @Size(min = 0, max = 1000)
  private String multipleMeasurements;

  /** Subitem DMP92, Psychdata - META233 Study Metadata. */
  @Size(min = 0, max = 1000)
  private String qualitityOther;

  /** DMP17. */
  @Size(min = 0, max = 1000)
  private String fileFormat;

  /** DMP18 - Why must the data be completely or partially stored?. */
  /** DMP19 - Antwortoption DMP18 */
  private boolean workingCopy;

  /** DMP19 - Antwortoption DMP18. */
  @Size(min = 0, max = 250)
  private String workingCopyTxt;

  /** DMP20 - Antwortoption DMP18. */
  private boolean goodScientific;

  /** DMP20 - Antwortoption DMP18. */
  @Size(min = 0, max = 250)
  private String goodScientificTxt;

  /** DMP21 - Antwortoption DMP18. */
  private boolean subsequentUse;

  /** DMP21 - Antwortoption DMP18. */
  @Size(min = 0, max = 250)
  private String subsequentUseTxt;

  /** DMP22 - Antwortoption DMP18. */
  private boolean requirements;

  /** DMP22 - Antwortoption DMP18. */
  @Size(min = 0, max = 250)
  private String requirementsTxt;

  /** DMP23 - Antwortoption DMP18. */
  private boolean documentation;

  /** DMP23 - Antwortoption DMP18. */
  @Size(min = 0, max = 250)
  private String documentationTxt;

  /** DMP86 - Does data selection take place?. */
  private boolean dataSelection;

  /** the next inputs are only important if dataselection == true. */
  /** DMP24 - DMP86 ja */
  @Size(min = 0, max = 500)
  private String selectionTime;

  /** DMP25 - DMP86 ja. */
  @Size(min = 0, max = 500)
  private String selectionResp;

  /** DMP26 - DMP86 ja. */
  @Size(min = 0, max = 500)
  private String selectionSoftware;

  /** DMP27 - DMP86 ja. */
  @Size(min = 0, max = 1000)
  private String selectionCriteria;

  /** DMP28. */
  @Size(min = 0, max = 500)
  private String storageDuration;

  /** DMP29. */
  @Size(min = 0, max = 500)
  private String deleteProcedure;

  // ***************** MetaData Data *****************

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

  /**
   * Getter for {@link #id}.
   *
   * @return id
   */
  public int getId() {
    return id;
  }

  /**
   * Setter for {@link #id}.
   *
   * @param id
   *          -> this.id
   */
  public void setId(int id) {
    this.id = id;
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
   * Setter for {@link #projectAims}.
   *
   * @param projectAims
   *          -> this.projectAims
   */
  public void setProjectAims(String projectAims) {
    this.projectAims = projectAims;
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
   * Setter for {@link #duration}.
   *
   * @param duration
   *          -> this.duration
   */
  public void setDuration(String duration) {
    this.duration = duration;
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
   * Setter for {@link #organizations}.
   *
   * @param organizations
   *          -> this.organizations
   */
  public void setOrganizations(String organizations) {
    this.organizations = organizations;
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
   * Setter for {@link #planAims}.
   *
   * @param planAims
   *          -> this.planAims
   */
  public void setPlanAims(String planAims) {
    this.planAims = planAims;
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
   * Setter for {@link #existingData}.
   *
   * @param existingData
   *          -> this.existingData
   */
  public void setExistingData(String existingData) {
    this.existingData = existingData;
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
   * Setter for {@link #existingDataRelevance}.
   *
   * @param existingDataRelevance
   *          -> this.existingDataRelevance
   */
  public void setExistingDataRelevance(String existingDataRelevance) {
    this.existingDataRelevance = existingDataRelevance;
  }

  public String getDataCitation() {
    return dataCitation;
  }

  public void setDataCitation(String dataCitation) {
    this.dataCitation = dataCitation;
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
   * Setter for {@link #existingDataIntegration}.
   *
   * @param existingDataIntegration
   *          -> this.existingDataIntegration
   */
  public void setExistingDataIntegration(String existingDataIntegration) {
    this.existingDataIntegration = existingDataIntegration;
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
   * Setter for {@link #usedDataTypes}.
   *
   * @param usedDataTypes
   *          -> this.usedDataTypes
   */
  public void setUsedDataTypes(List<Integer> usedDataTypes) {
    this.usedDataTypes = usedDataTypes;
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
   * Setter for {@link #dataReproducibility}.
   *
   * @param dataReproducibility
   *          -> this.dataReproducibility
   */
  public void setDataReproducibility(String dataReproducibility) {
    this.dataReproducibility = dataReproducibility;
  }

  /**
   * Getter for {@link #collectionModes}.
   *
   * @return collectionModes
   */
  public List<Integer> getUsedCollectionModes() {
    return usedCollectionModes;
  }

  /**
   * Setter for {@link #collectionModes}.
   *
   * @param usedCollectionModes
   *          -> this.usedCollectionModes
   */
  public void setUsedCollectionModes(List<Integer> usedCollectionModes) {
    this.usedCollectionModes = usedCollectionModes;
  }

  /**
   * Getter for {@link #instruments}.
   *
   * @return instruments
   */
  public String getInstruments() {
    return instruments;
  }

  /**
   * Setter for {@link #instruments}.
   *
   * @param instruments
   *          -> this.instruments
   */
  public void setInstruments(String instruments) {
    this.instruments = instruments;
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
   * Setter for {@link #measOccasions}.
   *
   * @param measOccasions
   *          -> this.measOccasions
   */
  public void setMeasOccasions(String measOccasions) {
    this.measOccasions = measOccasions;
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
   * Setter for {@link #reliabilityTraining}.
   *
   * @param reliabilityTraining
   *          -> this.reliabilityTraining
   */
  public void setReliabilityTraining(String reliabilityTraining) {
    this.reliabilityTraining = reliabilityTraining;
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
   * Setter for {@link #multipleMeasurements}.
   *
   * @param multipleMeasurements
   *          -> this.multipleMeasurements
   */
  public void setMultipleMeasurements(String multipleMeasurements) {
    this.multipleMeasurements = multipleMeasurements;
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
   * Setter for {@link #qualitityOther}.
   *
   * @param qualitityOther
   *          -> this.qualitityOther
   */
  public void setQualitityOther(String qualitityOther) {
    this.qualitityOther = qualitityOther;
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
   * Setter for {@link #fileFormat}.
   *
   * @param fileFormat
   *          -> this.fileFormat
   */
  public void setFileFormat(String fileFormat) {
    this.fileFormat = fileFormat;
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
   * Setter for {@link #workingCopy}.
   *
   * @param workingCopy
   *          -> this.workingCopy
   */
  public void setWorkingCopy(boolean workingCopy) {
    this.workingCopy = workingCopy;
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
   * Setter for {@link #workingCopyTxt}.
   *
   * @param workingCopyTxt
   *          -> this.workingCopyTxt
   */
  public void setWorkingCopyTxt(String workingCopyTxt) {
    this.workingCopyTxt = workingCopyTxt;
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
   * Setter for {@link #goodScientific}.
   *
   * @param goodScientific
   *          -> this.goodScientific
   */
  public void setGoodScientific(boolean goodScientific) {
    this.goodScientific = goodScientific;
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
   * Setter for {@link #goodScientificTxt}.
   *
   * @param goodScientificTxt
   *          -> this.goodScientificTxt
   */
  public void setGoodScientificTxt(String goodScientificTxt) {
    this.goodScientificTxt = goodScientificTxt;
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
   * Setter for {@link #subsequentUse}.
   *
   * @param subsequentUse
   *          -> this.subsequentUse
   */
  public void setSubsequentUse(boolean subsequentUse) {
    this.subsequentUse = subsequentUse;
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
   * Setter for {@link #subsequentUseTxt}.
   *
   * @param subsequentUseTxt
   *          -> this.subsequentUseTxt
   */
  public void setSubsequentUseTxt(String subsequentUseTxt) {
    this.subsequentUseTxt = subsequentUseTxt;
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
   * Setter for {@link #requirements}.
   *
   * @param requirements
   *          -> this.requirements
   */
  public void setRequirements(boolean requirements) {
    this.requirements = requirements;
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
   * Setter for {@link #requirementsTxt}.
   *
   * @param requirementsTxt
   *          -> this.requirementsTxt
   */
  public void setRequirementsTxt(String requirementsTxt) {
    this.requirementsTxt = requirementsTxt;
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
   * Setter for {@link #documentation}.
   *
   * @param documentation
   *          -> this.documentation
   */
  public void setDocumentation(boolean documentation) {
    this.documentation = documentation;
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
   * Setter for {@link #documentationTxt}.
   *
   * @param documentationTxt
   *          -> this.documentationTxt
   */
  public void setDocumentationTxt(String documentationTxt) {
    this.documentationTxt = documentationTxt;
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
   * Setter for {@link #dataSelection}.
   *
   * @param dataSelection
   *          -> this.dataSelection
   */
  public void setDataSelection(boolean dataSelection) {
    this.dataSelection = dataSelection;
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
   * Setter for {@link #selectionTime}.
   *
   * @param selectionTime
   *          -> this.selectionTime
   */
  public void setSelectionTime(String selectionTime) {
    this.selectionTime = selectionTime;
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
   * Setter for {@link #selectionResp}.
   *
   * @param selectionResp
   *          -> this.selectionResp
   */
  public void setSelectionResp(String selectionResp) {
    this.selectionResp = selectionResp;
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
   * Setter for {@link #selectionSoftware}.
   *
   * @param selectionSoftware
   *          -> this.selectionSoftware
   */
  public void setSelectionSoftware(String selectionSoftware) {
    this.selectionSoftware = selectionSoftware;
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
   * Setter for {@link #selectionCriteria}.
   *
   * @param selectionCriteria
   *          -> this.selectionCriteria
   */
  public void setSelectionCriteria(String selectionCriteria) {
    this.selectionCriteria = selectionCriteria;
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
   * Setter for {@link #storageDuration}.
   *
   * @param storageDuration
   *          -> this.storageDuration
   */
  public void setStorageDuration(String storageDuration) {
    this.storageDuration = storageDuration;
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
   * Setter for {@link #deleteProcedure}.
   *
   * @param deleteProcedure
   *          -> this.deleteProcedure
   */
  public void setDeleteProcedure(String deleteProcedure) {
    this.deleteProcedure = deleteProcedure;
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
   * Setter for {@link #otherDataTypes}.
   *
   * @param otherDataTypes
   *          -> this.otherDataTypes
   */
  public void setOtherDataTypes(String otherDataTypes) {
    this.otherDataTypes = otherDataTypes;
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
   * Setter for {@link #otherCMIP}.
   *
   * @param otherCMIP
   *          -> this.otherCMIP
   */
  public void setOtherCMIP(String otherCMIP) {
    this.otherCMIP = otherCMIP;
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
   * Setter for {@link #otherCMINP}.
   *
   * @param otherCMINP
   *          -> this.otherCMINP
   */
  public void setOtherCMINP(String otherCMINP) {
    this.otherCMINP = otherCMINP;
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
   * Setter for {@link #releaseObligation}.
   *
   * @param releaseObligation
   *          -> this.releaseObligation
   */
  public void setReleaseObligation(boolean releaseObligation) {
    this.releaseObligation = releaseObligation;
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
   * Setter for {@link #expectedGroups}.
   *
   * @param expectedGroups
   *          -> this.expectedGroups
   */
  public void setExpectedGroups(String expectedGroups) {
    this.expectedGroups = expectedGroups;
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
   * Setter for {@link #searchableData}.
   *
   * @param searchableData
   *          -> this.searchableData
   */
  public void setSearchableData(boolean searchableData) {
    this.searchableData = searchableData;
  }

  /**
   * Checks if is {@link #expectedUsage}.
   *
   * @return true, if is expected usage
   */
  public String getExpectedUsage() {
    return expectedUsage;
  }

  /**
   * Setter for {@link #expectedUsage}.
   *
   * @param expectedUsage
   *          -> this.expectedUsage
   */
  public void setExpectedUsage(String expectedUsage) {
    this.expectedUsage = expectedUsage;
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
   * Setter for {@link #accessReasonAuthor}.
   *
   * @param accessReasonAuthor
   *          -> this.accessReasonAuthor
   */
  public void setAccessReasonAuthor(String accessReasonAuthor) {
    this.accessReasonAuthor = accessReasonAuthor;
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
   * Setter for {@link #noAccessReason}.
   *
   * @param noAccessReason
   *          -> this.noAccessReason
   */
  public void setNoAccessReason(String noAccessReason) {
    this.noAccessReason = noAccessReason;
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
   * Setter for {@link #noAccessReasonOther}.
   *
   * @param noAccessReasonOther
   *          -> this.noAccessReasonOther
   */
  public void setNoAccessReasonOther(String noAccessReasonOther) {
    this.noAccessReasonOther = noAccessReasonOther;
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
   * Setter for {@link #transferTime}.
   *
   * @param transferTime
   *          -> this.transferTime
   */
  public void setTransferTime(String transferTime) {
    this.transferTime = transferTime;
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
   * Setter for {@link #sensitiveData}.
   *
   * @param sensitiveData
   *          -> this.sensitiveData
   */
  public void setSensitiveData(String sensitiveData) {
    this.sensitiveData = sensitiveData;
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
   * Setter for {@link #initialUsage}.
   *
   * @param initialUsage
   *          -> this.initialUsage
   */
  public void setInitialUsage(String initialUsage) {
    this.initialUsage = initialUsage;
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
   * Setter for {@link #usageRestriction}.
   *
   * @param usageRestriction
   *          -> this.usageRestriction
   */
  public void setUsageRestriction(String usageRestriction) {
    this.usageRestriction = usageRestriction;
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
   * Setter for {@link #accessCosts}.
   *
   * @param accessCosts
   *          -> this.accessCosts
   */
  public void setAccessCosts(boolean accessCosts) {
    this.accessCosts = accessCosts;
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
   * Setter for {@link #accessCostsTxt}.
   *
   * @param accessCostsTxt
   *          -> this.accessCostsTxt
   */
  public void setAccessCostsTxt(String accessCostsTxt) {
    this.accessCostsTxt = accessCostsTxt;
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
   * Setter for {@link #accessTermsImplementation}.
   *
   * @param accessTermsImplementation
   *          -> this.accessTermsImplementation
   */
  public void setAccessTermsImplementation(String accessTermsImplementation) {
    this.accessTermsImplementation = accessTermsImplementation;
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
   * Setter for {@link #clarifiedRights}.
   *
   * @param clarifiedRights
   *          -> this.clarifiedRights
   */
  public void setClarifiedRights(boolean clarifiedRights) {
    this.clarifiedRights = clarifiedRights;
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
   * Setter for {@link #clarifiedRightsTxt}.
   *
   * @param clarifiedRightsTxt
   *          -> this.clarifiedRightsTxt
   */
  public void setClarifiedRightsTxt(String clarifiedRightsTxt) {
    this.clarifiedRightsTxt = clarifiedRightsTxt;
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
   * Setter for {@link #acquisitionAgreement}.
   *
   * @param acquisitionAgreement
   *          -> this.acquisitionAgreement
   */
  public void setAcquisitionAgreement(boolean acquisitionAgreement) {
    this.acquisitionAgreement = acquisitionAgreement;
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
   * Setter for {@link #usedPID}.
   *
   * @param usedPID
   *          -> this.usedPID
   */
  public void setUsedPID(String usedPID) {
    this.usedPID = usedPID;
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
   * Setter for {@link #publStrategy}.
   *
   * @param publStrategy
   *          -> this.publStrategy
   */
  public void setPublStrategy(String publStrategy) {
    this.publStrategy = publStrategy;
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
   * Setter for {@link #usedPIDTxt}.
   *
   * @param usedPIDTxt
   *          -> this.usedPIDTxt
   */
  public void setUsedPIDTxt(String usedPIDTxt) {
    this.usedPIDTxt = usedPIDTxt;
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
   * Setter for {@link #selectedMetaPurposes}.
   *
   * @param selectedMetaPurposes
   *          -> this.selectedMetaPurposes
   */
  public void setSelectedMetaPurposes(List<Integer> selectedMetaPurposes) {
    this.selectedMetaPurposes = selectedMetaPurposes;
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
   * Setter for {@link #metaDescription}.
   *
   * @param metaDescription
   *          -> this.metaDescription
   */
  public void setMetaDescription(String metaDescription) {
    this.metaDescription = metaDescription;
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
   * Setter for {@link #metaFramework}.
   *
   * @param metaFramework
   *          -> this.metaFramework
   */
  public void setMetaFramework(String metaFramework) {
    this.metaFramework = metaFramework;
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
   * Setter for {@link #metaGeneration}.
   *
   * @param metaGeneration
   *          -> this.metaGeneration
   */
  public void setMetaGeneration(String metaGeneration) {
    this.metaGeneration = metaGeneration;
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
   * Setter for {@link #metaMonitor}.
   *
   * @param metaMonitor
   *          -> this.metaMonitor
   */
  public void setMetaMonitor(String metaMonitor) {
    this.metaMonitor = metaMonitor;
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
   * Setter for {@link #metaFormat}.
   *
   * @param metaFormat
   *          -> this.metaFormat
   */
  public void setMetaFormat(String metaFormat) {
    this.metaFormat = metaFormat;
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
   * Setter for {@link #storageResponsible}.
   *
   * @param storageResponsible
   *          -> this.storageResponsible
   */
  public void setStorageResponsible(String storageResponsible) {
    this.storageResponsible = storageResponsible;
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
   * Setter for {@link #storageTechnologies}.
   *
   * @param storageTechnologies
   *          -> this.storageTechnologies
   */
  public void setStorageTechnologies(String storageTechnologies) {
    this.storageTechnologies = storageTechnologies;
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
   * Setter for {@link #storagePlaces}.
   *
   * @param storagePlaces
   *          -> this.storagePlaces
   */
  public void setStoragePlaces(String storagePlaces) {
    this.storagePlaces = storagePlaces;
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
   * Setter for {@link #storageBackups}.
   *
   * @param storageBackups
   *          -> this.storageBackups
   */
  public void setStorageBackups(String storageBackups) {
    this.storageBackups = storageBackups;
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
   * Setter for {@link #storageTransfer}.
   *
   * @param storageTransfer
   *          -> this.storageTransfer
   */
  public void setStorageTransfer(String storageTransfer) {
    this.storageTransfer = storageTransfer;
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
   * Setter for {@link #storageExpectedSize}.
   *
   * @param storageExpectedSize
   *          -> this.storageExpectedSize
   */
  public void setStorageExpectedSize(String storageExpectedSize) {
    this.storageExpectedSize = storageExpectedSize;
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
   * Setter for {@link #storageRequirements}.
   *
   * @param storageRequirements
   *          -> this.storageRequirements
   */
  public void setStorageRequirements(boolean storageRequirements) {
    this.storageRequirements = storageRequirements;
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
   * Setter for {@link #storageRequirementsTxt}.
   *
   * @param storageRequirementsTxt
   *          -> this.storageRequirementsTxt
   */
  public void setStorageRequirementsTxt(String storageRequirementsTxt) {
    this.storageRequirementsTxt = storageRequirementsTxt;
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
   * Setter for {@link #storageSuccession}.
   *
   * @param storageSuccession
   *          -> this.storageSuccession
   */
  public void setStorageSuccession(boolean storageSuccession) {
    this.storageSuccession = storageSuccession;
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
   * Setter for {@link #storageSuccessionTxt}.
   *
   * @param storageSuccessionTxt
   *          -> this.storageSuccessionTxt
   */
  public void setStorageSuccessionTxt(String storageSuccessionTxt) {
    this.storageSuccessionTxt = storageSuccessionTxt;
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
   * Setter for {@link #frameworkNationality}.
   *
   * @param frameworkNationality
   *          -> this.frameworkNationality
   */
  public void setFrameworkNationality(String frameworkNationality) {
    this.frameworkNationality = frameworkNationality;
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
   * Setter for {@link #frameworkNationalityTxt}.
   *
   * @param frameworkNationalityTxt
   *          -> this.frameworkNationalityTxt
   */
  public void setFrameworkNationalityTxt(String frameworkNationalityTxt) {
    this.frameworkNationalityTxt = frameworkNationalityTxt;
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
   * Setter for {@link #responsibleUnit}.
   *
   * @param responsibleUnit
   *          -> this.responsibleUnit
   */
  public void setResponsibleUnit(String responsibleUnit) {
    this.responsibleUnit = responsibleUnit;
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
   * Setter for {@link #involvedInstitutions}.
   *
   * @param involvedInstitutions
   *          -> this.involvedInstitutions
   */
  public void setInvolvedInstitutions(String involvedInstitutions) {
    this.involvedInstitutions = involvedInstitutions;
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
   * Setter for {@link #involvedInformed}.
   *
   * @param involvedInformed
   *          -> this.involvedInformed
   */
  public void setInvolvedInformed(boolean involvedInformed) {
    this.involvedInformed = involvedInformed;
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
   * Setter for {@link #contributionsDefined}.
   *
   * @param contributionsDefined
   *          -> this.contributionsDefined
   */
  public void setContributionsDefined(boolean contributionsDefined) {
    this.contributionsDefined = contributionsDefined;
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
   * Setter for {@link #contributionsDefinedTxt}.
   *
   * @param contributionsDefinedTxt
   *          -> this.contributionsDefinedTxt
   */
  public void setContributionsDefinedTxt(String contributionsDefinedTxt) {
    this.contributionsDefinedTxt = contributionsDefinedTxt;
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
   * Setter for {@link #givenConsent}.
   *
   * @param givenConsent
   *          -> this.givenConsent
   */
  public void setGivenConsent(boolean givenConsent) {
    this.givenConsent = givenConsent;
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
   * Setter for {@link #managementWorkflow}.
   *
   * @param managementWorkflow
   *          -> this.managementWorkflow
   */
  public void setManagementWorkflow(boolean managementWorkflow) {
    this.managementWorkflow = managementWorkflow;
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
   * Setter for {@link #managementWorkflowTxt}.
   *
   * @param managementWorkflowTxt
   *          -> this.managementWorkflowTxt
   */
  public void setManagementWorkflowTxt(String managementWorkflowTxt) {
    this.managementWorkflowTxt = managementWorkflowTxt;
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
   * Setter for {@link #staffDescription}.
   *
   * @param staffDescription
   *          -> this.staffDescription
   */
  public void setStaffDescription(boolean staffDescription) {
    this.staffDescription = staffDescription;
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
   * Setter for {@link #staffDescriptionTxt}.
   *
   * @param staffDescriptionTxt
   *          -> this.staffDescriptionTxt
   */
  public void setStaffDescriptionTxt(String staffDescriptionTxt) {
    this.staffDescriptionTxt = staffDescriptionTxt;
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
   * Setter for {@link #funderRequirements}.
   *
   * @param funderRequirements
   *          -> this.funderRequirements
   */
  public void setFunderRequirements(String funderRequirements) {
    this.funderRequirements = funderRequirements;
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
   * Setter for {@link #providerRequirements}.
   *
   * @param providerRequirements
   *          -> this.providerRequirements
   */
  public void setProviderRequirements(String providerRequirements) {
    this.providerRequirements = providerRequirements;
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
   * Setter for {@link #repoPolicies}.
   *
   * @param repoPolicies
   *          -> this.repoPolicies
   */
  public void setRepoPolicies(String repoPolicies) {
    this.repoPolicies = repoPolicies;
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
   * Setter for {@link #repoPoliciesResponsible}.
   *
   * @param repoPoliciesResponsible
   *          -> this.repoPoliciesResponsible
   */
  public void setRepoPoliciesResponsible(String repoPoliciesResponsible) {
    this.repoPoliciesResponsible = repoPoliciesResponsible;
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
   * Setter for {@link #planningAdherence}.
   *
   * @param planningAdherence
   *          -> this.planningAdherence
   */
  public void setPlanningAdherence(String planningAdherence) {
    this.planningAdherence = planningAdherence;
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
   * Setter for {@link #dataProtection}.
   *
   * @param dataProtection
   *          -> this.dataProtection
   */
  public void setDataProtection(boolean dataProtection) {
    this.dataProtection = dataProtection;
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
   * Setter for {@link #protectionRequirements}.
   *
   * @param protectionRequirements
   *          -> this.protectionRequirements
   */
  public void setProtectionRequirements(String protectionRequirements) {
    this.protectionRequirements = protectionRequirements;
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
   * Setter for {@link #consentObtained}.
   *
   * @param consentObtained
   *          -> this.consentObtained
   */
  public void setConsentObtained(boolean consentObtained) {
    this.consentObtained = consentObtained;
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
   * Setter for {@link #consentObtainedTxt}.
   *
   * @param consentObtainedTxt
   *          -> this.consentObtainedTxt
   */
  public void setConsentObtainedTxt(String consentObtainedTxt) {
    this.consentObtainedTxt = consentObtainedTxt;
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
   * Setter for {@link #sharingConsidered}.
   *
   * @param sharingConsidered
   *          -> this.sharingConsidered
   */
  public void setSharingConsidered(boolean sharingConsidered) {
    this.sharingConsidered = sharingConsidered;
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
   * Setter for {@link #irbApproval}.
   *
   * @param irbApproval
   *          -> this.irbApproval
   */
  public void setIrbApproval(boolean irbApproval) {
    this.irbApproval = irbApproval;
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
   * Setter for {@link #irbApprovalTxt}.
   *
   * @param irbApprovalTxt
   *          -> this.irbApprovalTxt
   */
  public void setIrbApprovalTxt(String irbApprovalTxt) {
    this.irbApprovalTxt = irbApprovalTxt;
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
   * Setter for {@link #sensitiveDataIncluded}.
   *
   * @param sensitiveDataIncluded
   *          -> this.sensitiveDataIncluded
   */
  public void setSensitiveDataIncluded(boolean sensitiveDataIncluded) {
    this.sensitiveDataIncluded = sensitiveDataIncluded;
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
   * Setter for {@link #sensitiveDataIncludedTxt}.
   *
   * @param sensitiveDataIncludedTxt
   *          -> this.sensitiveDataIncludedTxt
   */
  public void setSensitiveDataIncludedTxt(String sensitiveDataIncludedTxt) {
    this.sensitiveDataIncludedTxt = sensitiveDataIncludedTxt;
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
   * Setter for {@link #externalCopyright}.
   *
   * @param externalCopyright
   *          -> this.externalCopyright
   */
  public void setExternalCopyright(boolean externalCopyright) {
    this.externalCopyright = externalCopyright;
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
   * Setter for {@link #externalCopyrightTxt}.
   *
   * @param externalCopyrightTxt
   *          -> this.externalCopyrightTxt
   */
  public void setExternalCopyrightTxt(String externalCopyrightTxt) {
    this.externalCopyrightTxt = externalCopyrightTxt;
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
   * Setter for {@link #internalCopyright}.
   *
   * @param internalCopyright
   *          -> this.internalCopyright
   */
  public void setInternalCopyright(boolean internalCopyright) {
    this.internalCopyright = internalCopyright;
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
   * Setter for {@link #internalCopyrightTxt}.
   *
   * @param internalCopyrightTxt
   *          -> this.internalCopyrightTxt
   */
  public void setInternalCopyrightTxt(String internalCopyrightTxt) {
    this.internalCopyrightTxt = internalCopyrightTxt;
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
   * Setter for {@link #specificCosts}.
   *
   * @param specificCosts
   *          -> this.specificCosts
   */
  public void setSpecificCosts(String specificCosts) {
    this.specificCosts = specificCosts;
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
   * Setter for {@link #specificCostsTxt}.
   *
   * @param specificCostsTxt
   *          -> this.specificCostsTxt
   */
  public void setSpecificCostsTxt(String specificCostsTxt) {
    this.specificCostsTxt = specificCostsTxt;
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
   * Setter for {@link #ariseCosts}.
   *
   * @param ariseCosts
   *          -> this.ariseCosts
   */
  public void setAriseCosts(String ariseCosts) {
    this.ariseCosts = ariseCosts;
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
   * Setter for {@link #bearCost}.
   *
   * @param bearCost
   *          -> this.bearCost
   */
  public void setBearCost(String bearCost) {
    this.bearCost = bearCost;
  }

}
