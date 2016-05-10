package de.zpid.datawiz.dto;

import java.io.Serializable;

import javax.validation.constraints.Size;

import de.zpid.datawiz.enumeration.ConstructTypes;

// TODO: Auto-generated Javadoc
/**
 * The Class ConstructDTO.
 */
public class ConstructDTO implements Serializable {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -7126615950848855816L;

  /** The id. */
  private long id;

  /** The name of the construct. */
  @Size(min = 0, max = 500, groups = StudyDTO.StDesignVal.class)
  private String name;

  /** The type for the select box. */
  private ConstructTypes type;

  /** The other -> if other is selected as type. */
  @Size(min = 0, max = 500, groups = StudyDTO.StDesignVal.class)
  private String other;

  /**
   * Gets the id.
   *
   * @return the id
   */
  public long getId() {
    return id;
  }

  /**
   * Sets the id.
   *
   * @param id
   *          the new id
   */
  public void setId(long id) {
    this.id = id;
  }

  /**
   * Gets the name.
   *
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * Sets the name.
   *
   * @param name
   *          the new name
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Gets the type.
   *
   * @return the type
   */
  public ConstructTypes getType() {
    return type;
  }

  /**
   * Sets the type.
   *
   * @param type
   *          the new type
   */
  public void setType(ConstructTypes type) {
    this.type = type;
  }

  /**
   * Gets the other.
   *
   * @return the other
   */
  public String getOther() {
    return other;
  }

  /**
   * Sets the other.
   *
   * @param other
   *          the new other
   */
  public void setOther(String other) {
    this.other = other;
  }

  /**
   * Instantiates a new construct dto.
   *
   * @param id
   *          the id
   * @param name
   *          the name
   * @param type
   *          the type
   * @param other
   *          the other
   */
  public ConstructDTO(long id, String name, ConstructTypes type, String other) {
    super();
    this.id = id;
    this.name = name;
    this.type = type;
    this.other = other;
  }

  /**
   * Instantiates a new construct dto.
   */
  public ConstructDTO() {
    super();
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + (int) (id ^ (id >>> 32));
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    result = prime * result + ((other == null) ? 0 : other.hashCode());
    result = prime * result + ((type == null) ? 0 : type.hashCode());
    return result;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    ConstructDTO other = (ConstructDTO) obj;
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

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "ConstructDTO [id=" + id + ", name=" + name + ", type=" + type + ", other=" + other + "]";
  }

}
