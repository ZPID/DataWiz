package de.zpid.datawiz.dto;

import java.io.Serializable;

import javax.validation.constraints.Size;

import de.zpid.datawiz.enumeration.DWFieldTypes;

public class StudyListTypesDTO implements Serializable {

  private static final long serialVersionUID = -3098463141488593526L;

  private long id;
  private long studyid;

  @Size(min = 0, max = 1000, groups = { StudyDTO.StGeneralVal.class, StudyDTO.StDesignVal.class,
      StudyDTO.StCharacteristicsVal.class })
  private String text;
  private DWFieldTypes type;
  private int sort;
  private boolean timetable;
  private String objectivetype;

  public StudyListTypesDTO() {
    super();
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public long getStudyid() {
    return studyid;
  }

  public void setStudyid(long studyid) {
    this.studyid = studyid;
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  public DWFieldTypes getType() {
    return type;
  }

  public void setType(DWFieldTypes type) {
    this.type = type;
  }

  public int getSort() {
    return sort;
  }

  public void setSort(int sort) {
    this.sort = sort;
  }

  public boolean isTimetable() {
    return timetable;
  }

  public void setTimetable(boolean timetable) {
    this.timetable = timetable;
  }

  public String getObjectivetype() {
    return objectivetype;
  }

  public void setObjectivetype(String objectivetype) {
    this.objectivetype = objectivetype;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + (int) (id ^ (id >>> 32));
    result = prime * result + ((objectivetype == null) ? 0 : objectivetype.hashCode());
    result = prime * result + sort;
    result = prime * result + (int) (studyid ^ (studyid >>> 32));
    result = prime * result + ((text == null) ? 0 : text.hashCode());
    result = prime * result + (timetable ? 1231 : 1237);
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
    StudyListTypesDTO other = (StudyListTypesDTO) obj;
    if (id != other.id)
      return false;
    if (objectivetype == null) {
      if (other.objectivetype != null)
        return false;
    } else if (!objectivetype.equals(other.objectivetype))
      return false;
    if (sort != other.sort)
      return false;
    if (studyid != other.studyid)
      return false;
    if (text == null) {
      if (other.text != null)
        return false;
    } else if (!text.equals(other.text))
      return false;
    if (timetable != other.timetable)
      return false;
    if (type != other.type)
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "StudyListTypesDTO [id=" + id + ", studyid=" + studyid + ", text=" + text + ", type=" + type + ", sort="
        + sort + ", timetable=" + timetable + ", objectivetype=" + objectivetype + "]";
  }

}
