package de.zpid.datawiz.dto;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

/**
 * Implementation of the CollectionMode which is declared as DMP14(Subitem DMP14, PsychData) in the excel meta data
 * sheet.
 * <P>
 * For the administration of these fields it was important to save them to the database.
 * 
 * @author Ronny Boelter
 * @version 1.0
 */
public class CollectionModeDTO implements Serializable {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 2692731844761020139L;

  /** data type id. */
  @NotNull
  private int id;

  /** The project id - saved in the. */
  private int projectId;

  /** German name of the data type. */
  @NotNull
  private String nameDE;

  /** English name of the data type. */
  @NotNull
  private String nameEN;

  /** If "other" is chosen - possibility to enter these types. */
  private String otherCollectionMode;

  /** Delete these items is only possible if they are not used, but they can be set active or inactive. */
  private boolean active;

  /**  true if the investor is present. */
  private boolean investPresent;

  /** sort the datatypes. */
  private int sort;

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
   * @param id -> this.id
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
   * @param projectId -> this.projectId
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
   * @param nameDE -> this.nameDE
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
   * @param nameEN -> this.nameEN
   */
  public void setNameEN(String nameEN) {
    this.nameEN = nameEN;
  }

  /**
   * Getter for {@link #otherCollectionMode}.
   *
   * @return otherCollectionMode
   */
  public String getOtherCollectionMode() {
    return otherCollectionMode;
  }

  /**
   * Setter for {@link #otherCollectionMode}.
   *
   * @param otherCollectionMode -> this.otherCollectionMode
   */
  public void setOtherCollectionMode(String otherCollectionMode) {
    this.otherCollectionMode = otherCollectionMode;
  }

  /**
   * Checks if is {@link #active}.
   *
   * @return true, if is active
   */
  public boolean isActive() {
    return active;
  }

  /**
   * Setter for {@link #active}.
   *
   * @param active -> this.active
   */
  public void setActive(boolean active) {
    this.active = active;
  }

  /**
   * Checks if is {@link #investPresent}.
   *
   * @return true, if is invest present
   */
  public boolean isInvestPresent() {
    return investPresent;
  }

  /**
   * Setter for {@link #investPresent}.
   *
   * @param investPresent -> this.investPresent
   */
  public void setInvestPresent(boolean investPresent) {
    this.investPresent = investPresent;
  }

  /**
   * Getter for {@link #sort}.
   *
   * @return sort
   */
  public int getSort() {
    return sort;
  }

  /**
   * Setter for {@link #sort}.
   *
   * @param sort -> this.sort
   */
  public void setSort(int sort) {
    this.sort = sort;
  }

  /**
   * Instantiates a new collectionModeDTO.
   *
   * @param id the id
   * @param projectId the project id
   * @param nameDE the name de
   * @param nameEN the name en
   * @param otherCollectionMode the other collection mode
   * @param active the active
   * @param investPresent the invest present
   * @param sort the sort
   */
  public CollectionModeDTO(int id, int projectId, String nameDE, String nameEN, String otherCollectionMode,
      boolean active, boolean investPresent, int sort) {
    super();
    this.id = id;
    this.projectId = projectId;
    this.nameDE = nameDE;
    this.nameEN = nameEN;
    this.otherCollectionMode = otherCollectionMode;
    this.active = active;
    this.investPresent = investPresent;
    this.sort = sort;
  }

  /**
   * Instantiates a new collectionModeDTO.
   */
  public CollectionModeDTO() {
    super();
  }

  /* (non-Javadoc)
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + (active ? 1231 : 1237);
    result = prime * result + id;
    result = prime * result + (investPresent ? 1231 : 1237);
    result = prime * result + ((nameDE == null) ? 0 : nameDE.hashCode());
    result = prime * result + ((nameEN == null) ? 0 : nameEN.hashCode());
    result = prime * result + ((otherCollectionMode == null) ? 0 : otherCollectionMode.hashCode());
    result = prime * result + projectId;
    result = prime * result + sort;
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
    CollectionModeDTO other = (CollectionModeDTO) obj;
    if (active != other.active)
      return false;
    if (id != other.id)
      return false;
    if (investPresent != other.investPresent)
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
    if (otherCollectionMode == null) {
      if (other.otherCollectionMode != null)
        return false;
    } else if (!otherCollectionMode.equals(other.otherCollectionMode))
      return false;
    if (projectId != other.projectId)
      return false;
    if (sort != other.sort)
      return false;
    return true;
  }

  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "CollectionModeDTO [id=" + id + ", projectId=" + projectId + ", nameDE=" + nameDE + ", nameEN=" + nameEN
        + ", otherCollectionMode=" + otherCollectionMode + ", active=" + active + ", investPresent=" + investPresent
        + ", sort=" + sort + "]";
  }

}
