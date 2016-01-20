package de.zpid.datawiz.dto;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

// TODO: Auto-generated Javadoc
/**
 * Implementation of the CollectionMode which are declared as DMP14(Subitem DMP14, PsychData) in the excel meta data
 * sheet.
 * <P>
 * For the administration of these fields it was important to save them to the database.
 * 
 * @author Ronny Boelter
 * @version 1.0
 */
public class CollectionModeDTO implements Serializable {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 4523755361477035462L;

  /** data type id. */
  @NotNull
  private int id;

  /** The project id - saved in the. */
  private int projectId;

  /** German name of the CollectionMode. */
  @NotNull
  private String nameDE;

  /** English name of the CollectionMode. */
  @NotNull
  private String nameEN;

  /** If "other" is chosen - possibility to enter these types */
  private String otherModes;

  /**
   * Instantiates a new CollectionModeDTO.
   */
  public CollectionModeDTO() {
    super();
  }

  /**
   * Instantiates a new CollectionModeDTO.
   *
   * @param id
   *          CollectionModeID
   * @param projectId
   *          ProjectId is used in relation table
   * @param nameDE
   *          German name of the type of data
   * @param nameEN
   *          English name of the type of data
   * @param otherModes
   *          Saved in Relation table if user has chosen "other" option
   */
  public CollectionModeDTO(int id, int projectId, String nameDE, String nameEN, String otherModes) {
    super();
    this.id = id;
    this.projectId = projectId;
    this.nameDE = nameDE;
    this.nameEN = nameEN;
    this.otherModes = otherModes;
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
   * Getter for {@link #otherModes}.
   *
   * @return otherModes
   */
  public String getOtherCollectionMode() {
    return otherModes;
  }

  /**
   * Setter for {@link #otherModes}.
   *
   * @param otherModes
   *          -> this.otherModes
   */
  public void setOtherCollectionMode(String otherModes) {
    this.otherModes = otherModes;
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
    result = prime * result + id;
    result = prime * result + ((nameDE == null) ? 0 : nameDE.hashCode());
    result = prime * result + ((nameEN == null) ? 0 : nameEN.hashCode());
    result = prime * result + ((otherModes == null) ? 0 : otherModes.hashCode());
    result = prime * result + projectId;
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
    CollectionModeDTO other = (CollectionModeDTO) obj;
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
    if (otherModes == null) {
      if (other.otherModes != null)
        return false;
    } else if (!otherModes.equals(other.otherModes))
      return false;
    if (projectId != other.projectId)
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
    return "DataTypeDTO [id=" + id + ", projectId=" + projectId + ", nameDE=" + nameDE + ", nameEN=" + nameEN
        + ", otherCollectionMode=" + otherModes + "]";
  }

}
