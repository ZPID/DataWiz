package de.zpid.datawiz.dto;

import java.io.Serializable;

import javax.validation.constraints.Size;

import de.zpid.datawiz.enumeration.DWFieldTypes;

public class ListTypesDTO implements Serializable {

  private static final long serialVersionUID = -3098463141488593526L;
  private long id;

  @Size(min = 0, max = 1000, groups = { StudyDTO.StGeneralVal.class, StudyDTO.StDesignVal.class })
  private String text;
  private DWFieldTypes type;

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
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

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + (int) (id ^ (id >>> 32));
    result = prime * result + ((text == null) ? 0 : text.hashCode());
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
    ListTypesDTO other = (ListTypesDTO) obj;
    if (id != other.id)
      return false;
    if (text == null) {
      if (other.text != null)
        return false;
    } else if (!text.equals(other.text))
      return false;
    if (type != other.type)
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "ListTypesDTO [id=" + id + ", text=" + text + ", type=" + type + "]";
  }

}
