package de.zpid.datawiz.dto;

import java.io.Serializable;
import java.util.Date;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;

public class ProjectDTO implements Serializable {

  private static final long serialVersionUID = -7148120591732205800L;

  private int id;
  @NotNull
  @NotBlank
  private String title;
  private String description;
  private Date created;
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

  public Date getCreated() {
    return created;
  }

  public void setCreated(Date created) {
    this.created = created;
  }

  public UserRoleDTO getProjectRole() {
    return projectRole;
  }

  public void setProjectRole(UserRoleDTO projectRole) {
    this.projectRole = projectRole;
  }

  @Override
  public String toString() {
    return "ProjectDTO [id=" + id + ", title=" + title + ", created=" + created + ", projectRole=" + projectRole + "]";
  }

}
