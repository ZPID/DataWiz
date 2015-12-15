package de.zpid.datawiz.form;

import java.io.Serializable;
import java.util.List;

import javax.validation.Valid;

import de.zpid.datawiz.dto.ContributorDTO;
import de.zpid.datawiz.dto.ProjectDTO;
import de.zpid.datawiz.dto.StudyDTO;
import de.zpid.datawiz.dto.UserDTO;
import de.zpid.datawiz.util.SavedState;

public class ProjectForm implements Serializable {

  private static final long serialVersionUID = 868777577427023558L;
  @Valid
  private ProjectDTO project;
  @Valid
  private List<UserDTO> sharedUser;
  @Valid
  private List<StudyDTO> studies;
  @Valid
  private List<ContributorDTO> contributors;
  @Valid
  private ContributorDTO primaryContributor;

  private List<String> tags;

  // ViewHelper
  private SavedState saveState;
  private String saveMsg;

  public ProjectDTO getProject() {
    return project;
  }

  public void setProject(ProjectDTO project) {
    this.project = project;
  }

  public List<UserDTO> getSharedUser() {
    return sharedUser;
  }

  public void setSharedUser(List<UserDTO> list) {
    this.sharedUser = list;
  }

  public List<StudyDTO> getStudies() {
    return studies;
  }

  public void setStudies(List<StudyDTO> studies) {
    this.studies = studies;
  }

  public List<ContributorDTO> getContributors() {
    return contributors;
  }

  public void setContributors(List<ContributorDTO> contributors) {
    this.contributors = contributors;
  }

  public List<String> getTags() {
    return tags;
  }

  public void setTags(List<String> tags) {
    this.tags = tags;
  }

  public ContributorDTO getPrimaryContributor() {
    return primaryContributor;
  }

  public void setPrimaryContributor(ContributorDTO primaryContributor) {
    this.primaryContributor = primaryContributor;
  }

  public SavedState getSaveState() {
    return saveState;
  }

  public void setSaveState(SavedState saveState) {
    this.saveState = saveState;
  }

  public String getSaveMsg() {
    return saveMsg;
  }

  public void setSaveMsg(String saveMsg) {
    this.saveMsg = saveMsg;
  }
}
