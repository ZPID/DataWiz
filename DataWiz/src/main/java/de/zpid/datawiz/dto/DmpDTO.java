package de.zpid.datawiz.dto;

import java.io.Serializable;
import java.util.List;

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

  private int id;

  // ***************** Administrative Data *****************

  /** DMP02 */
  private String projectAims;

  /** DMP04 */
  private String duration;

  /** DMP05 */
  private String organizations;

  /** DMP07 */
  private String planAims;

  // ***************** Research Data *****************

  /** DMP09 : 0 yes, existing data are used/ 1 no data were found/ 2 no search was carried out. */
  private int existingData;

  /** DMP10 */
  private String existingDataRelevance;

  /** DMP11 */
  private String existingDataIntegration;

  // ***************** Types of data *****************

  /** DMP12 META096 Study Metadata */
  private List<DmpDataTypeDTO> dataTypes;

  /** DMP13 */
  private String dataReproducibility;

  /** DMP14 */
  private List<DmpCollectionModeDTO> collectionModes;

  /** Subitem DMP14, JARS - META094 Study Metadata */
  private String instruments;

  /** Subitem DMP14, PsychData - META097 Study Metadata */
  private String measOccasions;

  /**
   * TODO KLÄREN !!!!!!
   */
  /** DMP16 */
  private String qualityManagement;

  /** Subitem DMP16, JARS - META131 Study Metadata */
  private String reliabilityTraining;

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getProjectAims() {
    return projectAims;
  }

  public void setProjectAims(String projectAims) {
    this.projectAims = projectAims;
  }

  public String getDuration() {
    return duration;
  }

  public void setDuration(String duration) {
    this.duration = duration;
  }

  public String getOrganizations() {
    return organizations;
  }

  public void setOrganizations(String organizations) {
    this.organizations = organizations;
  }

  public String getPlanAims() {
    return planAims;
  }

  public void setPlanAims(String planAims) {
    this.planAims = planAims;
  }

  public int getExistingData() {
    return existingData;
  }

  public void setExistingData(int existingData) {
    this.existingData = existingData;
  }

  public String getExistingDataRelevance() {
    return existingDataRelevance;
  }

  public void setExistingDataRelevance(String existingDataRelevance) {
    this.existingDataRelevance = existingDataRelevance;
  }

  public String getExistingDataIntegration() {
    return existingDataIntegration;
  }

  public void setExistingDataIntegration(String existingDataIntegration) {
    this.existingDataIntegration = existingDataIntegration;
  }

  public List<DmpDataTypeDTO> getDataTypes() {
    return dataTypes;
  }

  public void setDataTypes(List<DmpDataTypeDTO> dataTypes) {
    this.dataTypes = dataTypes;
  }

  public String getDataReproducibility() {
    return dataReproducibility;
  }

  public void setDataReproducibility(String dataReproducibility) {
    this.dataReproducibility = dataReproducibility;
  }

  public List<DmpCollectionModeDTO> getCollectionModes() {
    return collectionModes;
  }

  public void setCollectionModes(List<DmpCollectionModeDTO> collectionModes) {
    this.collectionModes = collectionModes;
  }

  public String getInstruments() {
    return instruments;
  }

  public void setInstruments(String instruments) {
    this.instruments = instruments;
  }

  public String getMeasOccasions() {
    return measOccasions;
  }

  public void setMeasOccasions(String measOccasions) {
    this.measOccasions = measOccasions;
  }

  public String getQualityManagement() {
    return qualityManagement;
  }

  public void setQualityManagement(String qualityManagement) {
    this.qualityManagement = qualityManagement;
  }

  public String getReliabilityTraining() {
    return reliabilityTraining;
  }

  public void setReliabilityTraining(String reliabilityTraining) {
    this.reliabilityTraining = reliabilityTraining;
  }

}
