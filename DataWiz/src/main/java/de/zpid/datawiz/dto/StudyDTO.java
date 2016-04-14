package de.zpid.datawiz.dto;

import java.io.Serializable;
import java.util.Date;

public class StudyDTO implements Serializable {

  private static final long serialVersionUID = -7300213401850684971L;
  private long id;
  private long projectId;
  private long lastUserId;
  private boolean master;
  private Date timestamp;
  private String title;

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public long getProjectId() {
    return projectId;
  }

  public void setProjectId(long projectId) {
    this.projectId = projectId;
  }

  public long getLastUserId() {
    return lastUserId;
  }

  public void setLastUserId(long lastUserId) {
    this.lastUserId = lastUserId;
  }

  public boolean isMaster() {
    return master;
  }

  public void setMaster(boolean master) {
    this.master = master;
  }

  public Date getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(Date timestamp) {
    this.timestamp = timestamp;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + (int) (id ^ (id >>> 32));
    result = prime * result + (int) (lastUserId ^ (lastUserId >>> 32));
    result = prime * result + (master ? 1231 : 1237);
    result = prime * result + (int) (projectId ^ (projectId >>> 32));
    result = prime * result + ((timestamp == null) ? 0 : timestamp.hashCode());
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
    StudyDTO other = (StudyDTO) obj;
    if (id != other.id)
      return false;
    if (lastUserId != other.lastUserId)
      return false;
    if (master != other.master)
      return false;
    if (projectId != other.projectId)
      return false;
    if (timestamp == null) {
      if (other.timestamp != null)
        return false;
    } else if (!timestamp.equals(other.timestamp))
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
    return "StudyDTO [id=" + id + ", projectId=" + projectId + ", lastUserId=" + lastUserId + ", master=" + master
        + ", timestamp=" + timestamp + ", title=" + title + "]";
  }

}
