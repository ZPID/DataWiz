package de.zpid.datawiz.dto;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import de.zpid.datawiz.enumeration.InterventionTypes;

public class StudyDTO implements Serializable {

  private static final long serialVersionUID = -7300213401850684971L;
  private long id;
  private long projectId;

  public interface StGeneralVal {
  }

  /** Study01 */
  @NotNull(groups = StGeneralVal.class)
  @Size(min = 0, max = 250, groups = StGeneralVal.class)
  private String title;
  /** Study02 */
  @Size(min = 0, max = 50, groups = StGeneralVal.class)
  private String internalID;
  /** Study03 */
  @Size(min = 0, max = 250, groups = StGeneralVal.class)
  private String transTitle;
  /** Study04 */
  private List<ContributorDTO> contributor;
  /** Study05 */
  @Size(min = 0, max = 2000, groups = StGeneralVal.class)
  private String sAbstract;
  /** Study06 */
  @Size(min = 0, max = 2000, groups = StGeneralVal.class)
  private String sAbstractTrans;
  /** Study07 */
  @Size(min = 0, max = 20, groups = StGeneralVal.class)
  private String completeSel;
  /** Study08 */
  @Size(min = 0, max = 500, groups = StGeneralVal.class)
  private String excerpt;
  /** Study09 */
  @Size(min = 0, max = 20, groups = StGeneralVal.class)
  private String prevWork;
  /** Study10 */
  @Size(min = 0, max = 500, groups = StGeneralVal.class)
  private String prevWorkStr;
  /** Study11 */
  @Valid
  private List<ListTypesDTO> software;
  /** Study12 */
  @Valid
  private List<ListTypesDTO> pubOnData;
  /** Study13 */
  @Valid
  private List<ListTypesDTO> conflInterests;

  public interface StDesignVal {
  }

  /** Study14/15 */
  @Valid
  private List<ObjectivesDTO> objectives;
  /** Study16 */
  @Valid
  private List<ListTypesDTO> relTheorys;
  /** Study17 -> DMP89 */
  private String repMeasures;
  /** Study18 */
  @Valid
  private List<ListTypesDTO> measOccName;
  /** Study19 */
  @Size(min = 0, max = 2000, groups = StDesignVal.class)
  private String timeDim;
  /** Study20 survey data */
  private boolean surveyIntervention;
  /** Study20 experimental data */
  private boolean experimentalIntervention;
  /** Study20 test data */
  private boolean testIntervention;
  /** Study21 */
  private InterventionTypes interTypeExp;
  /** Study22 */
  private InterventionTypes interTypeDes;
  /** Study23 */
  private InterventionTypes interTypeLab;
  /** Study24 */
  private InterventionTypes randomization;
  /** Study25 */
  @Valid
  private List<ListTypesDTO> interArms;
  /** Study26 */
  private List<Boolean> interTimeTable;
  /** Study27 */
  private InterventionTypes surveyType;
  /** Study28/ Study29 */
  @Valid
  private List<ConstructDTO> constructs;
  /** Study30 - Study40 */
  @Valid
  private List<InstrumentDTO> instruments;
  /** Study41 */
  @Size(min = 0, max = 2000, groups = StDesignVal.class)
  private String description;

  public interface StCharacteristicsVal {
  }
  
  
  
  

  private long lastUserId;
  private LocalDateTime timestamp;

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public long getProjectId() {
    return projectId;
  }

  public void setProjectId(long projectId) {
    this.projectId = projectId;
  }

  public long getLastUserId() {
    return lastUserId;
  }

  public void setLastUserId(long lastUserId) {
    this.lastUserId = lastUserId;
  }

  public LocalDateTime getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(LocalDateTime timestamp) {
    this.timestamp = timestamp;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

}
