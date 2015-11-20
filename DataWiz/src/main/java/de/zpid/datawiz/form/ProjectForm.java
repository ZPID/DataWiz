package de.zpid.datawiz.form;

import java.util.List;

import de.zpid.datawiz.dto.ProjectDTO;

public class ProjectForm {

  List<ProjectDTO> projects;

  public List<ProjectDTO> getProjects() {
    return projects;
  }

  public void setProjects(List<ProjectDTO> project) {
    this.projects = project;
  }

}
