package de.zpid.datawiz.form;

import java.io.Serializable;
import java.util.List;

import javax.validation.Valid;

import org.springframework.web.multipart.MultipartFile;

import de.zpid.datawiz.dto.ContributorDTO;
import de.zpid.datawiz.dto.FormTypesDTO;
import de.zpid.datawiz.dto.ProjectDTO;
import de.zpid.datawiz.dto.RecordDTO;
import de.zpid.datawiz.dto.StudyDTO;

public class StudyForm implements Serializable {

  private static final long serialVersionUID = 7871841325048805095L;

  @Valid
  private StudyDTO study;
  private ProjectDTO project;
  private List<ContributorDTO> projectContributors;
  private List<FormTypesDTO> collectionModes;
  private List<FormTypesDTO> sourFormat;
  private List<RecordDTO> records;
  private int hiddenVar;
  private int delPos;
  private int scrollPosition;
  private String fileType;

  private String selectedFileType;
  private MultipartFile spssFile;
  private MultipartFile codeBookFile;
  private MultipartFile csvFile;
  private char csvSeperator;
  private char csvQuoteChar;
  private char csvDecChar;
  private String newChangeLog;

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

  public int getHiddenVar() {
    return hiddenVar;
  }

  public void setHiddenVar(int hiddenVar) {
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

  public int getDelPos() {
    return delPos;
  }

  public void setDelPos(int delPos) {
    this.delPos = delPos;
  }

  public int getScrollPosition() {
    return scrollPosition;
  }

  public void setScrollPosition(int scrollPosition) {
    this.scrollPosition = scrollPosition;
  }

  public List<RecordDTO> getRecords() {
    return records;
  }

  public void setRecords(List<RecordDTO> records) {
    this.records = records;
  }

  public String getFileType() {
    return fileType;
  }

  public void setFileType(String fileType) {
    this.fileType = fileType;
  }

  public String getNewChangeLog() {
    return newChangeLog;
  }

  public void setNewChangeLog(String newChangeLog) {
    this.newChangeLog = newChangeLog;
  }

  public MultipartFile getSpssFile() {
    return spssFile;
  }

  public void setSpssFile(MultipartFile spssFile) {
    this.spssFile = spssFile;
  }

  public MultipartFile getCodeBookFile() {
    return codeBookFile;
  }

  public void setCodeBookFile(MultipartFile codeBookFile) {
    this.codeBookFile = codeBookFile;
  }

  public MultipartFile getCsvFile() {
    return csvFile;
  }

  public void setCsvFile(MultipartFile csvFile) {
    this.csvFile = csvFile;
  }

  public String getSelectedFileType() {
    return selectedFileType;
  }

  public void setSelectedFileType(String selectedFileType) {
    this.selectedFileType = selectedFileType;
  }

  public char getCsvSeperator() {
    return csvSeperator;
  }

  public void setCsvSeperator(char csvSeperator) {
    this.csvSeperator = csvSeperator;
  }

  public char getCsvQuoteChar() {
    return csvQuoteChar;
  }

  public void setCsvQuoteChar(char csvQuoteChar) {
    this.csvQuoteChar = csvQuoteChar;
  }

  public char getCsvDecChar() {
    return csvDecChar;
  }

  public void setCsvDecChar(char csvDecChar) {
    this.csvDecChar = csvDecChar;
  }

}
