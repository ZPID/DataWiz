package de.zpid.datawiz.form;

import java.io.Serializable;
import java.util.List;

import org.springframework.security.core.userdetails.User;

import de.zpid.datawiz.dto.StudyDTO;
import de.zpid.datawiz.dto.ProjectDTO;

public class ProjectForm implements Serializable {

  private static final long serialVersionUID = 868777577427023558L;
  private List<ProjectDTO> projects;
  private List<StudyDTO> data;
  private List<User> users;

  public List<ProjectDTO> getProjects() {
    return projects;
  }

  public void setProjects(List<ProjectDTO> project) {
    this.projects = project;
  }

  public List<StudyDTO> getData() {
    return data;
  }

  public void setData(List<StudyDTO> data) {
    this.data = data;
  }

  public List<User> getUsers() {
    return users;
  }

  public void setUsers(List<User> users) {
    this.users = users;
  }

}
