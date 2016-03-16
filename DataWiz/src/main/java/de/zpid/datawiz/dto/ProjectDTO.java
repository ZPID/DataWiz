package de.zpid.datawiz.dto;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;

public class ProjectDTO implements Serializable {

  private static final long serialVersionUID = -7148120591732205800L;

  private long id;
  private long ownerId;

  public interface ProjectVal {
  }

  @NotNull(groups = ProjectVal.class)
  @NotBlank(groups = ProjectVal.class)
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

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public long getOwnerId() {
    return ownerId;
  }

  public void setOwnerId(long ownerId) {
    this.ownerId = ownerId;

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
    return "ProjectDTO [id=" + id + ", ownerId=" + ownerId + ", title=" + title + ", projectIdent=" + projectIdent
        + ", funding=" + funding + ", grantNumber=" + grantNumber + ", thesaurusType=" + thesaurusType + ", copyright="
        + copyright + ", description=" + description + ", created=" + created + "]";
  }

}
