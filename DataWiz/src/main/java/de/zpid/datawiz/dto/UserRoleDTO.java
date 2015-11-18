package de.zpid.datawiz.dto;

import java.io.Serializable;

public class UserRoleDTO implements Serializable {

  private static final long serialVersionUID = -1945304373696201185L;
  private int roleId;
  private int userId;
  private int projectId;
  private String type;

  public int getRoleId() {
    return roleId;
  }

  public void setRoleId(int roleId) {
    this.roleId = roleId;
  }

  public int getUserId() {
    return userId;
  }

  public void setUserId(int userId) {
    this.userId = userId;
  }

  public int getProjectId() {
    return projectId;
  }

  public void setProjectId(int projectId) {
    this.projectId = projectId;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + projectId;
    result = prime * result + roleId;
    result = prime * result + ((type == null) ? 0 : type.hashCode());
    result = prime * result + userId;
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
    if (projectId != other.projectId)
      return false;
    if (roleId != other.roleId)
      return false;
    if (type == null) {
      if (other.type != null)
        return false;
    } else if (!type.equals(other.type))
      return false;
    if (userId != other.userId)
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "UserRoleDTO [roleId=" + roleId + ", userId=" + userId + ", projectId=" + projectId + ", type=" + type + "]";
  }

}
