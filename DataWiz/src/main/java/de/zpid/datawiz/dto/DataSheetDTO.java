package de.zpid.datawiz.dto;

import java.io.Serializable;

public class DataSheetDTO implements Serializable {

  private static final long serialVersionUID = -7300213401850684971L;
  private int id;
  private String name;
  private String descrption;

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescrption() {
    return descrption;
  }

  public void setDescrption(String descrption) {
    this.descrption = descrption;
  }

  @Override
  public String toString() {
    return "DataSheetDTO [id=" + id + ", name=" + name + ", descrption=" + descrption + "]";
  }

}
