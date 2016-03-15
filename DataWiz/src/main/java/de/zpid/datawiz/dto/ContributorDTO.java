package de.zpid.datawiz.dto;

import java.io.Serializable;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import de.zpid.datawiz.util.RegexUtil;

public class ContributorDTO implements Serializable {

  private static final long serialVersionUID = -5898704446715573949L;

  private long id;
  private long projectId;
  private long studyId;
  private int sort;
  @Pattern(regexp = RegexUtil.alphabeticWithBlanksAndHypens + RegexUtil.size0to50)
  private String title;
  @Pattern(regexp = RegexUtil.alphabeticWithBlanksAndHypens + RegexUtil.size0to250)
  private String firstName;
  @Pattern(regexp = RegexUtil.alphabeticWithBlanksAndHypens + RegexUtil.size0to250)
  private String lastName;
  @Size(min = 0, max = 250)
  private String institution;
  @Size(min = 0, max = 250)
  private String department;
  @Pattern(regexp = RegexUtil.regexORCID)
  private String orcid;
  private Boolean primaryContributor;

  public static long getSerialversionuid() {
    return serialVersionUID;
  }

  public long getId() {
    return id;
  }

  public long getProjectId() {
    return projectId;
  }

  public long getStudyId() {
    return studyId;
  }

  public int getSort() {
    return sort;
  }

  public String getTitle() {
    return title;
  }

  public String getFirstName() {
    return firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public String getInstitution() {
    return institution;
  }

  public String getDepartment() {
    return department;
  }

  public String getOrcid() {
    return orcid;
  }

  public Boolean getPrimaryContributor() {
    return primaryContributor;
  }

  public void setId(long id) {
    this.id = id;

  }

  public void setProjectId(long projectId) {
    this.projectId = projectId;

  }

  public void setStudyId(long studyId) {
    this.studyId = studyId;

  }

  public void setSort(int sort) {
    this.sort = sort;

  }

  public void setTitle(String title) {
    this.title = title;

  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;

  }

  public void setLastName(String lastName) {
    this.lastName = lastName;

  }

  public void setInstitution(String institution) {
    this.institution = institution;

  }

  public void setDepartment(String department) {
    this.department = department;

  }

  public void setOrcid(String orcid) {
    this.orcid = orcid;

  }

  public void setPrimaryContributor(Boolean primaryContributor) {
    this.primaryContributor = primaryContributor;

  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((department == null) ? 0 : department.hashCode());
    result = prime * result + ((firstName == null) ? 0 : firstName.hashCode());
    result = prime * result + (int) (id ^ (id >>> 32));
    result = prime * result + ((institution == null) ? 0 : institution.hashCode());
    result = prime * result + ((lastName == null) ? 0 : lastName.hashCode());
    result = prime * result + ((orcid == null) ? 0 : orcid.hashCode());
    result = prime * result + ((primaryContributor == null) ? 0 : primaryContributor.hashCode());
    result = prime * result + (int) (projectId ^ (projectId >>> 32));
    result = prime * result + (int) (sort ^ (sort >>> 32));
    result = prime * result + (int) (studyId ^ (studyId >>> 32));
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
    ContributorDTO other = (ContributorDTO) obj;
    if (department == null) {
      if (other.department != null)
        return false;
    } else if (!department.equals(other.department))
      return false;
    if (firstName == null) {
      if (other.firstName != null)
        return false;
    } else if (!firstName.equals(other.firstName))
      return false;
    if (id != other.id)
      return false;
    if (institution == null) {
      if (other.institution != null)
        return false;
    } else if (!institution.equals(other.institution))
      return false;
    if (lastName == null) {
      if (other.lastName != null)
        return false;
    } else if (!lastName.equals(other.lastName))
      return false;
    if (orcid == null) {
      if (other.orcid != null)
        return false;
    } else if (!orcid.equals(other.orcid))
      return false;
    if (primaryContributor == null) {
      if (other.primaryContributor != null)
        return false;
    } else if (!primaryContributor.equals(other.primaryContributor))
      return false;
    if (projectId != other.projectId)
      return false;
    if (sort != other.sort)
      return false;
    if (studyId != other.studyId)
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
    return "ContributorDTO [id=" + id + ", projectId=" + projectId + ", studyId=" + studyId + ", sort=" + sort
        + ", title=" + title + ", firstName=" + firstName + ", lastName=" + lastName + ", institution=" + institution
        + ", department=" + department + ", orcid=" + orcid + ", primaryContributor=" + primaryContributor + "]";
  }

}
