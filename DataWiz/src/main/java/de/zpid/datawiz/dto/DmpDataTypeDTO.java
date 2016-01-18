package de.zpid.datawiz.dto;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

// TODO: Auto-generated Javadoc
/**
 * Implementation of the Data Types which are declared as DMP12 in the excel meta data sheet.
 * <P>
 * For the administration of these fields it was important to save them to the database.
 * 
 * @author Ronny Boelter
 * @version 1.0
 */
public class DmpDataTypeDTO implements Serializable {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 4523755361477035462L;

  /** data type id. */
  @NotNull
  private int id;

  /** The project id - saved in the. */
  private int projectId;

  /**  German name of the data type. */
  @NotNull
  private String nameDE;

  /**  English name of the data type. */
  @NotNull
  private String nameEN;

  /** If "other" is chosen - possibility to enter these types */
  private String otherDataTypes;

  /**
   * Instantiates a new dmpDataTypeDTO.
   */
  public DmpDataTypeDTO() {
    super();
  }

  /**
   * Instantiates a new dmpDataTypeDTO.
   *
   * @param id DataTypeID
   * @param projectId ProjectId is used in relation table 
   * @param nameDE German name of the type of data 
   * @param nameEN English name of the type of data 
   * @param otherDataTypes Saved in Relation table if user has chosen "other" option
   */
  public DmpDataTypeDTO(int id, int projectId, String nameDE, String nameEN, String otherDataTypes) {
    super();
    this.id = id;
    this.projectId = projectId;
    this.nameDE = nameDE;
    this.nameEN = nameEN;
    this.otherDataTypes = otherDataTypes;
  }

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
   * Getter for {@link #projectId}.
   *
   * @return projectId
   */
  public int getProjectId() {
    return projectId;
  }

  /**
   * Setter for {@link #projectId}.
   *
   * @param projectId
   *          -> this.projectId
   */
  public void setProjectId(int projectId) {
    this.projectId = projectId;
  }

  /**
   * Getter for {@link #nameDE}.
   *
   * @return nameDE
   */
  public String getNameDE() {
    return nameDE;
  }

  /**
   * Setter for {@link #nameDE}.
   *
   * @param nameDE
   *          -> this.nameDE
   */
  public void setNameDE(String nameDE) {
    this.nameDE = nameDE;
  }

  /**
   * Getter for {@link #nameEN}.
   *
   * @return nameEN
   */
  public String getNameEN() {
    return nameEN;
  }

  /**
   * Setter for {@link #nameEN}.
   *
   * @param nameEN
   *          -> this.nameEN
   */
  public void setNameEN(String nameEN) {
    this.nameEN = nameEN;
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
   * @param otherDataTypes -> this.otherDataTypes
   */
  public void setOtherDataTypes(String otherDataTypes) {
    this.otherDataTypes = otherDataTypes;
  }

  /* (non-Javadoc)
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + id;
    result = prime * result + ((nameDE == null) ? 0 : nameDE.hashCode());
    result = prime * result + ((nameEN == null) ? 0 : nameEN.hashCode());
    result = prime * result + ((otherDataTypes == null) ? 0 : otherDataTypes.hashCode());
    result = prime * result + projectId;
    return result;
  }

  /* (non-Javadoc)
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
    DmpDataTypeDTO other = (DmpDataTypeDTO) obj;
    if (id != other.id)
      return false;
    if (nameDE == null) {
      if (other.nameDE != null)
        return false;
    } else if (!nameDE.equals(other.nameDE))
      return false;
    if (nameEN == null) {
      if (other.nameEN != null)
        return false;
    } else if (!nameEN.equals(other.nameEN))
      return false;
    if (otherDataTypes == null) {
      if (other.otherDataTypes != null)
        return false;
    } else if (!otherDataTypes.equals(other.otherDataTypes))
      return false;
    if (projectId != other.projectId)
      return false;
    return true;
  }

  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "DmpDataTypeDTO [id=" + id + ", projectId=" + projectId + ", nameDE=" + nameDE + ", nameEN=" + nameEN
        + ", otherDataTypes=" + otherDataTypes + "]";
  }

}
