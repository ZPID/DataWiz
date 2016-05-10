package de.zpid.datawiz.dto;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import de.zpid.datawiz.enumeration.DWFieldTypes;

/**
 * Implementation of the MetaPorpose which are declared as DMP131in the excel meta data sheet.
 * 
 * @author Ronny Boelter
 * @version 1.0
 */
public class FormTypesDTO implements Serializable {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 4523755361477035462L;

  /** data type id. */
  @NotNull
  private int id;

  /** The project id - saved in the. */
  private int dmpId;

  /** German name of the MetaPorpose */
  @NotNull
  private String nameDE;

  /** English name of the MetaPorpose. */
  @NotNull
  private String nameEN;

  /** If "other" is chosen - possibility to enter these types. */
  private String otherTypes;

  /** Delete these items is only possible if they are not used, but they can be set active or inactive. */
  private boolean active;

  /** sort the MetaPorpose. */
  private int sort;

  /** true if the investor is present. */
  private boolean investPresent;

  private DWFieldTypes type;

  public FormTypesDTO() {
    super();
  }

  public FormTypesDTO(int id, int dmpId, String nameDE, String nameEN, String otherTypes, boolean active, int sort,
      boolean investPresent, DWFieldTypes type) {
    super();
    this.id = id;
    this.dmpId = dmpId;
    this.nameDE = nameDE;
    this.nameEN = nameEN;
    this.otherTypes = otherTypes;
    this.active = active;
    this.sort = sort;
    this.investPresent = investPresent;
    this.type = type;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public int getDmpId() {
    return dmpId;
  }

  public void setDmpId(int dmpId) {
    this.dmpId = dmpId;
  }

  public String getNameDE() {
    return nameDE;
  }

  public void setNameDE(String nameDE) {
    this.nameDE = nameDE;
  }

  public String getNameEN() {
    return nameEN;
  }

  public void setNameEN(String nameEN) {
    this.nameEN = nameEN;
  }

  public String getOtherTypes() {
    return otherTypes;
  }

  public void setOtherTypes(String otherTypes) {
    this.otherTypes = otherTypes;
  }

  public boolean isActive() {
    return active;
  }

  public void setActive(boolean active) {
    this.active = active;
  }

  public int getSort() {
    return sort;
  }

  public void setSort(int sort) {
    this.sort = sort;
  }

  public boolean isInvestPresent() {
    return investPresent;
  }

  public void setInvestPresent(boolean investPresent) {
    this.investPresent = investPresent;
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
    result = prime * result + (active ? 1231 : 1237);
    result = prime * result + dmpId;
    result = prime * result + id;
    result = prime * result + (investPresent ? 1231 : 1237);
    result = prime * result + ((nameDE == null) ? 0 : nameDE.hashCode());
    result = prime * result + ((nameEN == null) ? 0 : nameEN.hashCode());
    result = prime * result + ((otherTypes == null) ? 0 : otherTypes.hashCode());
    result = prime * result + sort;
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
    FormTypesDTO other = (FormTypesDTO) obj;
    if (active != other.active)
      return false;
    if (dmpId != other.dmpId)
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
    if (otherTypes == null) {
      if (other.otherTypes != null)
        return false;
    } else if (!otherTypes.equals(other.otherTypes))
      return false;
    if (sort != other.sort)
      return false;
    if (type != other.type)
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "FormTypesDTO [id=" + id + ", dmpId=" + dmpId + ", nameDE=" + nameDE + ", nameEN=" + nameEN
        + ", otherTypes=" + otherTypes + ", active=" + active + ", sort=" + sort + ", investPresent=" + investPresent
        + ", type=" + type + "]";
  }

}
