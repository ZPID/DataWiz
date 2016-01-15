package de.zpid.datawiz.dto;

import java.io.Serializable;

public class DmpDTO implements Serializable {

  private static final long serialVersionUID = 1989300324143602401L;
  private String projectAims;
  private String duration;
  private String organizations;
  private String planAims;

  public String getProjectAims() {
    return projectAims;
  }

  public void setProjectAims(String projectAims) {
    this.projectAims = projectAims;
  }

  public String getDuration() {
    return duration;
  }

  public void setDuration(String duration) {
    this.duration = duration;
  }

  public String getOrganizations() {
    return organizations;
  }

  public void setOrganizations(String organizations) {
    this.organizations = organizations;
  }

  public String getPlanAims() {
    return planAims;
  }

  public void setPlanAims(String planAims) {
    this.planAims = planAims;
  }

}
