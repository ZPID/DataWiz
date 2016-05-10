package de.zpid.datawiz.dto;

import java.io.Serializable;

import de.zpid.datawiz.enumeration.ObjectiveTypes;

public class StudyObjectivesDTO implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = 6800112774213761668L;
  private long id;
  private long studyId;
  private String objective;
  private ObjectiveTypes type;

  public StudyObjectivesDTO() {
    super();
  }

  public StudyObjectivesDTO(long id, long studyId, String objective, ObjectiveTypes type) {
    super();
    this.id = id;
    this.studyId = studyId;
    this.objective = objective;
    this.type = type;
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public long getStudyId() {
    return studyId;
  }

  public void setStudyId(long studyId) {
    this.studyId = studyId;
  }

  public String getObjective() {
    return objective;
  }

  public void setObjective(String objective) {
    this.objective = objective;
  }

  public ObjectiveTypes getType() {
    return type;
  }

  public void setType(ObjectiveTypes type) {
    this.type = type;
  }

  public static long getSerialversionuid() {
    return serialVersionUID;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + (int) (id ^ (id >>> 32));
    result = prime * result + ((objective == null) ? 0 : objective.hashCode());
    result = prime * result + (int) (studyId ^ (studyId >>> 32));
    result = prime * result + ((type == null) ? 0 : type.hashCode());
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
    StudyObjectivesDTO other = (StudyObjectivesDTO) obj;
    if (id != other.id)
      return false;
    if (objective == null) {
      if (other.objective != null)
        return false;
    } else if (!objective.equals(other.objective))
      return false;
    if (studyId != other.studyId)
      return false;
    if (type != other.type)
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "ObjectivesDTO [id=" + id + ", studyId=" + studyId + ", objective=" + objective + ", type=" + type + "]";
  }

}
