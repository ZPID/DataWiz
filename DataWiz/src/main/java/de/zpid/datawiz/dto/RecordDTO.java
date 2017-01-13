package de.zpid.datawiz.dto;

import java.io.Serializable;
import java.time.LocalDateTime;

import de.zpid.spss.dto.SPSSFileDTO;
import de.zpid.spss.util.SPSSVarTypes;

public class RecordDTO extends SPSSFileDTO implements Serializable {

  private static final long serialVersionUID = 1689784689090294373L;
  // DB Table dw_record
  private long id;
  private long studyId;
  private String recordName;
  private LocalDateTime created;
  private String createdBy;
  private String description;
  private String fileName;
  // DB Table dw_record_metadata
  private long versionId;
  private String changeLog;
  private LocalDateTime changed;
  private String changedBy;
  private boolean masterRecord;

  public static SPSSVarTypes simplifyVarTypes(SPSSVarTypes spssType) {
    if (spssType == null || spssType.equals(SPSSVarTypes.SPSS_UNKNOWN)) {
      return SPSSVarTypes.SPSS_UNKNOWN;
    } else if (spssType.equals(SPSSVarTypes.SPSS_FMT_COMMA) || spssType.equals(SPSSVarTypes.SPSS_FMT_DOT)
        || spssType.equals(SPSSVarTypes.SPSS_FMT_PCT) || spssType.equals(SPSSVarTypes.SPSS_FMT_F)
        || spssType.equals(SPSSVarTypes.SPSS_FMT_N) || spssType.equals(SPSSVarTypes.SPSS_FMT_E)
        || spssType.equals(SPSSVarTypes.SPSS_FMT_DOLLAR) || spssType.equals(SPSSVarTypes.SPSS_FMT_IB)
        || spssType.equals(SPSSVarTypes.SPSS_FMT_PIBHEX) || spssType.equals(SPSSVarTypes.SPSS_FMT_P)
        || spssType.equals(SPSSVarTypes.SPSS_FMT_PIB) || spssType.equals(SPSSVarTypes.SPSS_FMT_PK)
        || spssType.equals(SPSSVarTypes.SPSS_FMT_RB) || spssType.equals(SPSSVarTypes.SPSS_FMT_RBHEX)
        || spssType.equals(SPSSVarTypes.SPSS_FMT_Z) || spssType.equals(SPSSVarTypes.SPSS_FMT_CCA)
        || spssType.equals(SPSSVarTypes.SPSS_FMT_CCB) || spssType.equals(SPSSVarTypes.SPSS_FMT_CCC)
        || spssType.equals(SPSSVarTypes.SPSS_FMT_CCD) || spssType.equals(SPSSVarTypes.SPSS_FMT_CCE)) {
      return SPSSVarTypes.SPSS_FMT_F;
    } else if (spssType.equals(SPSSVarTypes.SPSS_FMT_DATE) || spssType.equals(SPSSVarTypes.SPSS_FMT_TIME)
        || spssType.equals(SPSSVarTypes.SPSS_FMT_DATE_TIME) || spssType.equals(SPSSVarTypes.SPSS_FMT_ADATE)
        || spssType.equals(SPSSVarTypes.SPSS_FMT_JDATE) || spssType.equals(SPSSVarTypes.SPSS_FMT_DTIME)
        || spssType.equals(SPSSVarTypes.SPSS_FMT_WKDAY) || spssType.equals(SPSSVarTypes.SPSS_FMT_MONTH)
        || spssType.equals(SPSSVarTypes.SPSS_FMT_MOYR) || spssType.equals(SPSSVarTypes.SPSS_FMT_QYR)
        || spssType.equals(SPSSVarTypes.SPSS_FMT_WKYR) || spssType.equals(SPSSVarTypes.SPSS_FMT_EDATE)
        || spssType.equals(SPSSVarTypes.SPSS_FMT_SDATE) || spssType.equals(SPSSVarTypes.SPSS_FMT_MTIME)
        || spssType.equals(SPSSVarTypes.SPSS_FMT_YMDHMS)) {
      return SPSSVarTypes.SPSS_FMT_DATE;
    } else {
      return SPSSVarTypes.SPSS_FMT_A;
    }
  }

  public RecordDTO() {
    super();
  }

  public RecordDTO(String filepath) {
    super(filepath);
  }

  public RecordDTO(SPSSFileDTO spssFile) {
    super(spssFile.isU8(), spssFile.gethFile(), spssFile.getFilepath(), spssFile.getPassword(),
        spssFile.getNumberOfVariables(), spssFile.getNumberOfFileAttributes(), spssFile.getNumberOfCases(),
        spssFile.getEstimatedNofCases(), spssFile.getCaseSize(), spssFile.getCaseWeightVar(), spssFile.getCompression(),
        spssFile.getDateNumOfElements(), spssFile.getDateInfo(), spssFile.getFileCodePage(), spssFile.getFileEncoding(),
        spssFile.getFileIdString(), spssFile.getInterfaceEncoding(), spssFile.getMultRespDefsEx(),
        spssFile.getVariables(), spssFile.getAttributes(), spssFile.getDataMatrix(), spssFile.getDataMatrixJson(),
        spssFile.getErrors());
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public long getStudyId() {
    return studyId;
  }

  public void setStudyId(long studyId) {
    this.studyId = studyId;
  }

  public String getRecordName() {
    return recordName;
  }

  public void setRecordName(String recordName) {
    this.recordName = recordName;
  }

  public LocalDateTime getCreated() {
    return created;
  }

  public void setCreated(LocalDateTime created) {
    this.created = created;
  }

  public String getCreatedBy() {
    return createdBy;
  }

  public void setCreatedBy(String createdBy) {
    this.createdBy = createdBy;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getFileName() {
    return fileName;
  }

  public void setFileName(String fileName) {
    this.fileName = fileName;
  }

  public long getVersionId() {
    return versionId;
  }

  public void setVersionId(long versionId) {
    this.versionId = versionId;
  }

  public String getChangeLog() {
    return changeLog;
  }

  public void setChangeLog(String changeLog) {
    this.changeLog = changeLog;
  }

  public LocalDateTime getChanged() {
    return changed;
  }

  public void setChanged(LocalDateTime changed) {
    this.changed = changed;
  }

  public String getChangedBy() {
    return changedBy;
  }

  public void setChangedBy(String changedBy) {
    this.changedBy = changedBy;
  }

  public boolean isMasterRecord() {
    return masterRecord;
  }

  public void setMasterRecord(boolean masterRecord) {
    this.masterRecord = masterRecord;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + ((changeLog == null) ? 0 : changeLog.hashCode());
    result = prime * result + ((changed == null) ? 0 : changed.hashCode());
    result = prime * result + ((changedBy == null) ? 0 : changedBy.hashCode());
    result = prime * result + ((created == null) ? 0 : created.hashCode());
    result = prime * result + ((createdBy == null) ? 0 : createdBy.hashCode());
    result = prime * result + ((description == null) ? 0 : description.hashCode());
    result = prime * result + ((fileName == null) ? 0 : fileName.hashCode());
    result = prime * result + (int) (id ^ (id >>> 32));
    result = prime * result + (masterRecord ? 1231 : 1237);
    result = prime * result + ((recordName == null) ? 0 : recordName.hashCode());
    result = prime * result + (int) (studyId ^ (studyId >>> 32));
    result = prime * result + (int) (versionId ^ (versionId >>> 32));
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (!super.equals(obj))
      return false;
    if (getClass() != obj.getClass())
      return false;
    RecordDTO other = (RecordDTO) obj;
    if (changeLog == null) {
      if (other.changeLog != null)
        return false;
    } else if (!changeLog.equals(other.changeLog))
      return false;
    if (changed == null) {
      if (other.changed != null)
        return false;
    } else if (!changed.equals(other.changed))
      return false;
    if (changedBy == null) {
      if (other.changedBy != null)
        return false;
    } else if (!changedBy.equals(other.changedBy))
      return false;
    if (created == null) {
      if (other.created != null)
        return false;
    } else if (!created.equals(other.created))
      return false;
    if (createdBy == null) {
      if (other.createdBy != null)
        return false;
    } else if (!createdBy.equals(other.createdBy))
      return false;
    if (description == null) {
      if (other.description != null)
        return false;
    } else if (!description.equals(other.description))
      return false;
    if (fileName == null) {
      if (other.fileName != null)
        return false;
    } else if (!fileName.equals(other.fileName))
      return false;
    if (id != other.id)
      return false;
    if (masterRecord != other.masterRecord)
      return false;
    if (recordName == null) {
      if (other.recordName != null)
        return false;
    } else if (!recordName.equals(other.recordName))
      return false;
    if (studyId != other.studyId)
      return false;
    if (versionId != other.versionId)
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "RecordDTO [id=" + id + ", studyId=" + studyId + ", recordName=" + recordName + ", created=" + created
        + ", createdBy=" + createdBy + ", description=" + description + ", fileName=" + fileName + ", versionId="
        + versionId + ", changeLog=" + changeLog + ", changed=" + changed + ", changedBy=" + changedBy
        + ", masterRecord=" + masterRecord + "]";
  }

}
