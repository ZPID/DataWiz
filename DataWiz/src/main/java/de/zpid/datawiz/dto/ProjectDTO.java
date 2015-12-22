package de.zpid.datawiz.dto;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;

public class ProjectDTO implements Serializable {

  private static final long serialVersionUID = -7148120591732205800L;

  private int id;
  @NotNull
  @NotBlank
  private String title;
  /**
   * Not the database ID - this is used for documentation and is not unique!
   */
  private String projectIdent;
  private String funding;
  private String grantNumber;
  private String thesaurusType;
  private String copyright;
  private String description;
  private LocalDateTime created;
  private UserRoleDTO projectRole;

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public LocalDateTime getCreated() {
    return created;
  }

  public void setCreated(LocalDateTime created) {
    this.created = created;
  }

  public UserRoleDTO getProjectRole() {
    return projectRole;
  }

  public void setProjectRole(UserRoleDTO projectRole) {
    this.projectRole = projectRole;
  }

  public String getProjectIdent() {
    return projectIdent;
  }

  public void setProjectIdent(String projectIdent) {
    this.projectIdent = projectIdent;
  }

  public String getFunding() {
    return funding;
  }

  public void setFunding(String funding) {
    this.funding = funding;
  }

  public String getGrantNumber() {
    return grantNumber;
  }

  public void setGrantNumber(String grantNumber) {
    this.grantNumber = grantNumber;
  }

  public String getThesaurusType() {
    return thesaurusType;
  }

  public void setThesaurusType(String thesaurusType) {
    this.thesaurusType = thesaurusType;
  }

  public String getCopyright() {
    return copyright;
  }

  public void setCopyright(String copyright) {
    this.copyright = copyright;
  }

  @Override
  public String toString() {
    return "ProjectDTO [id=" + id + ", title=" + title + ", projectIdent=" + projectIdent + ", funding=" + funding
        + ", grantNumber=" + grantNumber + ", thesaurusType=" + thesaurusType + ", copyright=" + copyright
        + ", created=" + created + ", projectRole=" + projectRole + "]";
  }

}
