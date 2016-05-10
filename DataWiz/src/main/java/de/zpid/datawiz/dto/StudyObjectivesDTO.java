package de.zpid.datawiz.dto;

import java.io.Serializable;

import de.zpid.datawiz.enumeration.ObjectiveTypes;

public class ObjectivesDTO implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = 6800112774213761668L;
  private long id;
  private String objective;
  private ObjectiveTypes type;

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
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

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + (int) (id ^ (id >>> 32));
    result = prime * result + ((objective == null) ? 0 : objective.hashCode());
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
    ObjectivesDTO other = (ObjectivesDTO) obj;
    if (id != other.id)
      return false;
    if (objective == null) {
      if (other.objective != null)
        return false;
    } else if (!objective.equals(other.objective))
      return false;
    if (type != other.type)
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "ObjectivesDTO [id=" + id + ", objective=" + objective + ", type=" + type + "]";
  }

}
