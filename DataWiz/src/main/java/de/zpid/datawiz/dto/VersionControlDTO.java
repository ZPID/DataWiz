package de.zpid.datawiz.dto;

import java.io.Serializable;
import java.sql.Date;

public class VersionControlDTO implements Serializable {

  private static final long serialVersionUID = -1148580770170794383L;
  private int id;
  private int projectId;
  private int userId;
  private Date timestamp;
  private String description;

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public int getProjectId() {
    return projectId;
  }

  public void setProjectId(int projectId) {
    this.projectId = projectId;
  }

  public int getUserId() {
    return userId;
  }

  public void setUserId(int userId) {
    this.userId = userId;
  }

  public Date getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(Date timestamp) {
    this.timestamp = timestamp;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  @Override
  public String toString() {
    return "VersionControlDTO [id=" + id + ", projectId=" + projectId + ", userId=" + userId + ", timestamp="
        + timestamp + ", description=" + description + "]";
  }

}
