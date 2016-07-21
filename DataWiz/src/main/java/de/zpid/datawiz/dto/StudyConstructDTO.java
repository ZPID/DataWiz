package de.zpid.datawiz.dto;

import java.io.Serializable;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import de.zpid.datawiz.dto.StudyDTO.StDesignVal;

// TODO: Auto-generated Javadoc
/**
 * The Class ConstructDTO.
 */
public class StudyConstructDTO implements Serializable {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -7126615950848855816L;

  /** The id. */
  private long id;

  private long StudyId;

  /** The name of the construct. */
  @Size(min = 0, max = 500, groups = StudyDTO.StDesignVal.class)
  private String name;

  /** The type for the select box. */
  @Pattern(regexp = "(^$|INDEPENDENT|DEPENDENT|CONTROL|OTHER)", groups = StDesignVal.class)
  private String type;

  /** The other -> if other is selected as type. */
  @Size(min = 0, max = 500, groups = StudyDTO.StDesignVal.class)
  private String other;

  public StudyConstructDTO() {
    super();
  }

  public StudyConstructDTO(long id, long studyId, String name, String type, String other) {
    super();
    this.id = id;
    StudyId = studyId;
    this.name = name;
    this.type = type;
    this.other = other;
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public long getStudyId() {
    return StudyId;
  }

  public void setStudyId(long studyId) {
    StudyId = studyId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getOther() {
    return other;
  }

  public void setOther(String other) {
    this.other = other;
  }

  public static long getSerialversionuid() {
    return serialVersionUID;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + (int) (StudyId ^ (StudyId >>> 32));
    result = prime * result + (int) (id ^ (id >>> 32));
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    result = prime * result + ((other == null) ? 0 : other.hashCode());
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
    StudyConstructDTO other = (StudyConstructDTO) obj;
    if (StudyId != other.StudyId)
      return false;
    if (id != other.id)
      return false;
    if (name == null) {
      if (other.name != null)
        return false;
    } else if (!name.equals(other.name))
      return false;
    if (this.other == null) {
      if (other.other != null)
        return false;
    } else if (!this.other.equals(other.other))
      return false;
    if (type != other.type)
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "StudyConstructDTO [id=" + id + ", StudyId=" + StudyId + ", name=" + name + ", type=" + type + ", other="
        + other + "]";
  }

}
