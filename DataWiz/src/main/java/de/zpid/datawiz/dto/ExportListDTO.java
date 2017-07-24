package de.zpid.datawiz.dto;

import java.io.Serializable;

import de.zpid.datawiz.enumeration.ExportStates;

public class ExportListDTO implements Serializable {

  private static final long serialVersionUID = 4423068973345927927L;
  private ExportStates state;
  private long projectId;
  private long studyId;
  private long recordId;
  private long versionId;
  private boolean export;

  public ExportStates getState() {
    return state;
  }

  public void setState(ExportStates state) {
    this.state = state;
  }

  public long getProjectId() {
    return projectId;
  }

  public void setProjectId(long projectId) {
    this.projectId = projectId;
  }

  public long getStudyId() {
    return studyId;
  }

  public void setStudyId(long studyId) {
    this.studyId = studyId;
  }

  public long getRecordId() {
    return recordId;
  }

  public void setRecordId(long recordId) {
    this.recordId = recordId;
  }

  public long getVersionId() {
    return versionId;
  }

  public void setVersionId(long versionId) {
    this.versionId = versionId;
  }

  public boolean isExport() {
    return export;
  }

  public void setExport(boolean export) {
    this.export = export;
  }

  @Override
  public String toString() {
    return "ExportListDTO [state=" + state + ", projectId=" + projectId + ", studyId=" + studyId + ", recordId="
        + recordId + ", versionId=" + versionId + ", export=" + export + "]";
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + (export ? 1231 : 1237);
    result = prime * result + (int) (projectId ^ (projectId >>> 32));
    result = prime * result + (int) (recordId ^ (recordId >>> 32));
    result = prime * result + ((state == null) ? 0 : state.hashCode());
    result = prime * result + (int) (studyId ^ (studyId >>> 32));
    result = prime * result + (int) (versionId ^ (versionId >>> 32));
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
    ExportListDTO other = (ExportListDTO) obj;
    if (export != other.export)
      return false;
    if (projectId != other.projectId)
      return false;
    if (recordId != other.recordId)
      return false;
    if (state != other.state)
      return false;
    if (studyId != other.studyId)
      return false;
    if (versionId != other.versionId)
      return false;
    return true;
  }

}
