package de.zpid.datawiz.dto;

import de.zpid.spss.dto.SPSSFileDTO;
import de.zpid.spss.util.SPSSVarTypes;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;


/**
 * Record Data Transfer Object
 * <p>
 * This file is part of the DataWiz distribution (https://github.com/ZPID/DataWiz).
 * Copyright (c) 2018 <a href="https://leibniz-psychology.org/">Leibniz Institute for Psychology Information (ZPID)</a>.
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3.
 * <p>
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <a href="http://www.gnu.org/licenses/">http://www.gnu.org/licenses/</a>.
 *
 * @author Ronny Boelter
 * @version 1.0
 */
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
    private String originalName;
    private String minioName;

    public static SPSSVarTypes simplifyVarTypes(SPSSVarTypes spssType) {
        if (spssType == null || spssType.equals(SPSSVarTypes.SPSS_UNKNOWN)) {
            return SPSSVarTypes.SPSS_UNKNOWN;
        } else if (spssType.equals(SPSSVarTypes.SPSS_FMT_COMMA) || spssType.equals(SPSSVarTypes.SPSS_FMT_DOT)
                || spssType.equals(SPSSVarTypes.SPSS_FMT_PCT) || spssType.equals(SPSSVarTypes.SPSS_FMT_F) || spssType.equals(SPSSVarTypes.SPSS_FMT_N)
                || spssType.equals(SPSSVarTypes.SPSS_FMT_E) || spssType.equals(SPSSVarTypes.SPSS_FMT_DOLLAR) || spssType.equals(SPSSVarTypes.SPSS_FMT_IB)
                || spssType.equals(SPSSVarTypes.SPSS_FMT_PIBHEX) || spssType.equals(SPSSVarTypes.SPSS_FMT_P) || spssType.equals(SPSSVarTypes.SPSS_FMT_PIB)
                || spssType.equals(SPSSVarTypes.SPSS_FMT_PK) || spssType.equals(SPSSVarTypes.SPSS_FMT_RB) || spssType.equals(SPSSVarTypes.SPSS_FMT_RBHEX)
                || spssType.equals(SPSSVarTypes.SPSS_FMT_Z) || spssType.equals(SPSSVarTypes.SPSS_FMT_CCA) || spssType.equals(SPSSVarTypes.SPSS_FMT_CCB)
                || spssType.equals(SPSSVarTypes.SPSS_FMT_CCC) || spssType.equals(SPSSVarTypes.SPSS_FMT_CCD) || spssType.equals(SPSSVarTypes.SPSS_FMT_CCE)) {
            return SPSSVarTypes.SPSS_FMT_F;
        } else if (spssType.equals(SPSSVarTypes.SPSS_FMT_DATE) || spssType.equals(SPSSVarTypes.SPSS_FMT_TIME)
                || spssType.equals(SPSSVarTypes.SPSS_FMT_DATE_TIME) || spssType.equals(SPSSVarTypes.SPSS_FMT_ADATE)
                || spssType.equals(SPSSVarTypes.SPSS_FMT_JDATE) || spssType.equals(SPSSVarTypes.SPSS_FMT_DTIME)
                || spssType.equals(SPSSVarTypes.SPSS_FMT_WKDAY) || spssType.equals(SPSSVarTypes.SPSS_FMT_MONTH) || spssType.equals(SPSSVarTypes.SPSS_FMT_MOYR)
                || spssType.equals(SPSSVarTypes.SPSS_FMT_QYR) || spssType.equals(SPSSVarTypes.SPSS_FMT_WKYR) || spssType.equals(SPSSVarTypes.SPSS_FMT_EDATE)
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
        super(spssFile.isU8(), spssFile.gethFile(), spssFile.getFilepath(), spssFile.getPassword(), spssFile.getNumberOfVariables(),
                spssFile.getNumberOfFileAttributes(), spssFile.getNumberOfCases(), spssFile.getEstimatedNofCases(), spssFile.getCaseSize(),
                spssFile.getCaseWeightVar(), spssFile.getCompression(), spssFile.getDateNumOfElements(), spssFile.getDateInfo(), spssFile.getFileCodePage(),
                spssFile.getFileEncoding(), spssFile.getFileIdString(), spssFile.getInterfaceEncoding(), spssFile.getMultRespDefsEx(), spssFile.getVarSets(),
                spssFile.getVariables(), spssFile.getAttributes(), spssFile.getDataMatrix(), spssFile.getDataMatrixJson(), spssFile.getErrors());
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

    public String getOriginalName() {
        return originalName;
    }

    public void setOriginalName(String originalName) {
        this.originalName = originalName;
    }

    public String getMinioName() {
        return minioName;
    }

    public void setMinioName(String minioName) {
        this.minioName = minioName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        RecordDTO recordDTO = (RecordDTO) o;
        return id == recordDTO.id &&
                studyId == recordDTO.studyId &&
                versionId == recordDTO.versionId &&
                masterRecord == recordDTO.masterRecord &&
                Objects.equals(recordName, recordDTO.recordName) &&
                Objects.equals(created, recordDTO.created) &&
                Objects.equals(createdBy, recordDTO.createdBy) &&
                Objects.equals(description, recordDTO.description) &&
                Objects.equals(fileName, recordDTO.fileName) &&
                Objects.equals(changeLog, recordDTO.changeLog) &&
                Objects.equals(changed, recordDTO.changed) &&
                Objects.equals(changedBy, recordDTO.changedBy) &&
                Objects.equals(originalName, recordDTO.originalName) &&
                Objects.equals(minioName, recordDTO.minioName);
    }

    @Override
    public int hashCode() {

        return Objects.hash(super.hashCode(), id, studyId, recordName, created, createdBy, description, fileName, versionId, changeLog, changed, changedBy, masterRecord, originalName, minioName);
    }

    @Override
    public String toString() {
        return "RecordDTO{" +
                "id=" + id +
                ", studyId=" + studyId +
                ", recordName='" + recordName + '\'' +
                ", created=" + created +
                ", createdBy='" + createdBy + '\'' +
                ", description='" + description + '\'' +
                ", fileName='" + fileName + '\'' +
                ", versionId=" + versionId +
                ", changeLog='" + changeLog + '\'' +
                ", changed=" + changed +
                ", changedBy='" + changedBy + '\'' +
                ", masterRecord=" + masterRecord +
                ", originalName='" + originalName + '\'' +
                ", minioName='" + minioName + '\'' +
                '}';
    }
}
