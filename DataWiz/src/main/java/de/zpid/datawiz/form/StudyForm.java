package de.zpid.datawiz.form;

import java.io.Serializable;
import java.util.List;

import javax.validation.Valid;

import de.zpid.datawiz.dto.ContributorDTO;
import de.zpid.datawiz.dto.FormTypesDTO;
import de.zpid.datawiz.dto.ProjectDTO;
import de.zpid.datawiz.dto.StudyDTO;

public class StudyForm implements Serializable {

  private static final long serialVersionUID = 7871841325048805095L;

  @Valid
  private StudyDTO study;

  private ProjectDTO project;

  private List<ContributorDTO> projectContributors;

  private List<FormTypesDTO> collectionModes;
  private List<FormTypesDTO> sourFormat;

  private String hiddenVar;

  public StudyDTO getStudy() {
    return study;
  }

  public void setStudy(StudyDTO study) {
    this.study = study;
  }

  public ProjectDTO getProject() {
    return project;
  }

  public void setProject(ProjectDTO project) {
    this.project = project;
  }

  public List<ContributorDTO> getProjectContributors() {
    return projectContributors;
  }

  public void setProjectContributors(List<ContributorDTO> projectContributors) {
    this.projectContributors = projectContributors;
  }

  public String getHiddenVar() {
    return hiddenVar;
  }

  public void setHiddenVar(String hiddenVar) {
    this.hiddenVar = hiddenVar;
  }

  public List<FormTypesDTO> getCollectionModes() {
    return collectionModes;
  }

  public void setCollectionModes(List<FormTypesDTO> collectionModes) {
    this.collectionModes = collectionModes;
  }

  public List<FormTypesDTO> getSourFormat() {
    return sourFormat;
  }

  public void setSourFormat(List<FormTypesDTO> sourFormat) {
    this.sourFormat = sourFormat;
  }

}
