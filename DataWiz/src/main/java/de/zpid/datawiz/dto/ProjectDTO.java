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
  private long lastUserId;
  private LocalDateTime lastEdit;

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

  public long getLastUserId() {
    return lastUserId;
  }

  public void setLastUserId(long lastUserId) {
    this.lastUserId = lastUserId;
  }

  public LocalDateTime getLastEdit() {
    return lastEdit;
  }

  public void setLastEdit(LocalDateTime lastEdit) {
    this.lastEdit = lastEdit;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((copyright == null) ? 0 : copyright.hashCode());
    result = prime * result + ((created == null) ? 0 : created.hashCode());
    result = prime * result + ((description == null) ? 0 : description.hashCode());
    result = prime * result + ((funding == null) ? 0 : funding.hashCode());
    result = prime * result + ((grantNumber == null) ? 0 : grantNumber.hashCode());
    result = prime * result + (int) (id ^ (id >>> 32));
    result = prime * result + ((lastEdit == null) ? 0 : lastEdit.hashCode());
    result = prime * result + (int) (lastUserId ^ (lastUserId >>> 32));
    result = prime * result + (int) (ownerId ^ (ownerId >>> 32));
    result = prime * result + ((projectIdent == null) ? 0 : projectIdent.hashCode());
    result = prime * result + ((thesaurusType == null) ? 0 : thesaurusType.hashCode());
    result = prime * result + ((title == null) ? 0 : title.hashCode());
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
    ProjectDTO other = (ProjectDTO) obj;
    if (copyright == null) {
      if (other.copyright != null)
        return false;
    } else if (!copyright.equals(other.copyright))
      return false;
    if (created == null) {
      if (other.created != null)
        return false;
    } else if (!created.equals(other.created))
      return false;
    if (description == null) {
      if (other.description != null)
        return false;
    } else if (!description.equals(other.description))
      return false;
    if (funding == null) {
      if (other.funding != null)
        return false;
    } else if (!funding.equals(other.funding))
      return false;
    if (grantNumber == null) {
      if (other.grantNumber != null)
        return false;
    } else if (!grantNumber.equals(other.grantNumber))
      return false;
    if (id != other.id)
      return false;
    if (lastEdit == null) {
      if (other.lastEdit != null)
        return false;
    } else if (!lastEdit.equals(other.lastEdit))
      return false;
    if (lastUserId != other.lastUserId)
      return false;
    if (ownerId != other.ownerId)
      return false;
    if (projectIdent == null) {
      if (other.projectIdent != null)
        return false;
    } else if (!projectIdent.equals(other.projectIdent))
      return false;
    if (thesaurusType == null) {
      if (other.thesaurusType != null)
        return false;
    } else if (!thesaurusType.equals(other.thesaurusType))
      return false;
    if (title == null) {
      if (other.title != null)
        return false;
    } else if (!title.equals(other.title))
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "ProjectDTO [id=" + id + ", ownerId=" + ownerId + ", title=" + title + ", projectIdent=" + projectIdent
        + ", funding=" + funding + ", grantNumber=" + grantNumber + ", thesaurusType=" + thesaurusType + ", copyright="
        + copyright + ", description=" + description + ", created=" + created + ", lastUserId=" + lastUserId
        + ", lastEdit=" + lastEdit + "]";
  }

}
