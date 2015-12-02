package de.zpid.datawiz.form;

import java.io.Serializable;
import java.util.List;

import org.springframework.security.core.userdetails.User;

import de.zpid.datawiz.dto.ProjectDTO;
import de.zpid.datawiz.dto.StudyDTO;

public class ProjectForm implements Serializable {

  private static final long serialVersionUID = 868777577427023558L;
  private ProjectDTO project;
  private List<User> sharedUser;
  private List<StudyDTO> studies;

  public ProjectDTO getProject() {
    return project;
  }

  public void setProject(ProjectDTO project) {
    this.project = project;
  }

  public List<User> getSharedUser() {
    return sharedUser;
  }

  public void setSharedUser(List<User> sharedUser) {
    this.sharedUser = sharedUser;
  }

  public List<StudyDTO> getStudies() {
    return studies;
  }

  public void setStudies(List<StudyDTO> studies) {
    this.studies = studies;
  }

}
