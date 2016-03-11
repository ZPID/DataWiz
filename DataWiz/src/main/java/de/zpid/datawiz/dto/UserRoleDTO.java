package de.zpid.datawiz.dto;

import java.io.Serializable;
import java.math.BigInteger;

public class UserRoleDTO implements Serializable {

  private static final long serialVersionUID = -1945304373696201185L;
  private BigInteger roleId;
  private BigInteger userId;
  private BigInteger projectId;
  private BigInteger studyId;
  private String type;

  public BigInteger getRoleId() {
    return roleId;
  }

  public BigInteger getUserId() {
    return userId;
  }

  public BigInteger getProjectId() {
    return projectId;
  }

  public BigInteger getStudyId() {
    return studyId;
  }

  public String getType() {
    return type;
  }

  public void setRoleId(BigInteger roleId) {
    this.roleId = roleId;

  }

  public void setUserId(BigInteger userId) {
    this.userId = userId;

  }

  public void setProjectId(BigInteger projectId) {
    this.projectId = projectId;

  }

  public void setStudyId(BigInteger studyId) {
    this.studyId = studyId;

  }

  public void setType(String type) {
    this.type = type;

  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((projectId == null) ? 0 : projectId.hashCode());
    result = prime * result + ((roleId == null) ? 0 : roleId.hashCode());
    result = prime * result + ((studyId == null) ? 0 : studyId.hashCode());
    result = prime * result + ((type == null) ? 0 : type.hashCode());
    result = prime * result + ((userId == null) ? 0 : userId.hashCode());
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
    UserRoleDTO other = (UserRoleDTO) obj;
    if (projectId == null) {
      if (other.projectId != null)
        return false;
    } else if (!projectId.equals(other.projectId))
      return false;
    if (roleId == null) {
      if (other.roleId != null)
        return false;
    } else if (!roleId.equals(other.roleId))
      return false;
    if (studyId == null) {
      if (other.studyId != null)
        return false;
    } else if (!studyId.equals(other.studyId))
      return false;
    if (type == null) {
      if (other.type != null)
        return false;
    } else if (!type.equals(other.type))
      return false;
    if (userId == null) {
      if (other.userId != null)
        return false;
    } else if (!userId.equals(other.userId))
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "UserRoleDTO [roleId=" + roleId + ", userId=" + userId + ", projectId=" + projectId + ", studyId=" + studyId
        + ", type=" + type + "]";
  }

}
