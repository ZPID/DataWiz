package de.zpid.datawiz.form;

import java.io.Serializable;
import java.util.List;

import javax.validation.Valid;

import de.zpid.datawiz.dto.ContributorDTO;
import de.zpid.datawiz.dto.DmpDTO;
import de.zpid.datawiz.dto.FormTypesDTO;
import de.zpid.datawiz.dto.FileDTO;
import de.zpid.datawiz.dto.ProjectDTO;
import de.zpid.datawiz.dto.StudyDTO;
import de.zpid.datawiz.dto.UserDTO;

public class ProjectForm implements Serializable {

  private static final long serialVersionUID = 868777577427023558L;
  @Valid
  private ProjectDTO project;
  @Valid
  private List<UserDTO> sharedUser;
  @Valid
  private List<List<StudyDTO>> studies;
  @Valid
  private List<ContributorDTO> contributors;
  @Valid
  private ContributorDTO primaryContributor;
  @Valid
  private DmpDTO dmp;

  private List<String> tags;
  private List<FileDTO> files;

  /** DMP DATA */
  private List<FormTypesDTO> dataTypes;
  private List<FormTypesDTO> collectionModes;
  private List<FormTypesDTO> metaPurposes;

  // ViewHelper
  private int delPos;
  private String pagePosi;

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

  public List<List<StudyDTO>> getStudies() {
    return studies;
  }

  public void setStudies(List<List<StudyDTO>> studies) {
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

  public int getDelPos() {
    return delPos;
  }

  public void setDelPos(int delPos) {
    this.delPos = delPos;
  }

  public List<FileDTO> getFiles() {
    return files;
  }

  public void setFiles(List<FileDTO> files) {
    this.files = files;
  }

  public static long getSerialversionuid() {
    return serialVersionUID;
  }

  public DmpDTO getDmp() {
    return dmp;
  }

  public void setDmp(DmpDTO dmp) {
    this.dmp = dmp;
  }

  public List<FormTypesDTO> getDataTypes() {
    return dataTypes;
  }

  public void setDataTypes(List<FormTypesDTO> dataTypes) {
    this.dataTypes = dataTypes;
  }

  public List<FormTypesDTO> getCollectionModes() {
    return collectionModes;
  }

  public void setCollectionModes(List<FormTypesDTO> collectionModes) {
    this.collectionModes = collectionModes;
  }

  public List<FormTypesDTO> getMetaPurposes() {
    return metaPurposes;
  }

  public void setMetaPurposes(List<FormTypesDTO> metaPurposes) {
    this.metaPurposes = metaPurposes;
  }

  public String getPagePosi() {
    return pagePosi;
  }

  public void setPagePosi(String pagePosi) {
    this.pagePosi = pagePosi;
  }

}
