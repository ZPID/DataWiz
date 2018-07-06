package de.zpid.datawiz.dto;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;


/**
 * ExportRecord Data Transfer Object
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
 **/
public class ExportRecordDTO implements Serializable {

    private static final long serialVersionUID = 455686522505589747L;
    private long recordId;
    private long versionId;
    private String recordTitle;
    private boolean exportFullRecord;
    private boolean exportMetaData;
    private boolean exportCodebook;
    private boolean exportMatrix;
    private List<String> warnings;

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

    public String getRecordTitle() {
        return recordTitle;
    }

    public void setRecordTitle(String recordTitle) {
        this.recordTitle = recordTitle;
    }

    public boolean isExportFullRecord() {
        return exportFullRecord;
    }

    public void setExportFullRecord(boolean exportFullRecord) {
        this.exportFullRecord = exportFullRecord;
    }

    public boolean isExportMetaData() {
        return exportMetaData;
    }

    public void setExportMetaData(boolean exportMetaData) {
        this.exportMetaData = exportMetaData;
    }

    public boolean isExportCodebook() {
        return exportCodebook;
    }

    public void setExportCodebook(boolean exportCodebook) {
        this.exportCodebook = exportCodebook;
    }

    public boolean isExportMatrix() {
        return exportMatrix;
    }

    public void setExportMatrix(boolean exportMatrix) {
        this.exportMatrix = exportMatrix;
    }

    public List<String> getWarnings() {
        return warnings;
    }

    public void setWarnings(List<String> warnings) {
        this.warnings = warnings;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExportRecordDTO that = (ExportRecordDTO) o;
        return recordId == that.recordId &&
                versionId == that.versionId &&
                exportFullRecord == that.exportFullRecord &&
                exportMetaData == that.exportMetaData &&
                exportCodebook == that.exportCodebook &&
                exportMatrix == that.exportMatrix &&
                Objects.equals(recordTitle, that.recordTitle) &&
                Objects.equals(warnings, that.warnings);
    }

    @Override
    public int hashCode() {

        return Objects.hash(recordId, versionId, recordTitle, exportFullRecord, exportMetaData, exportCodebook, exportMatrix, warnings);
    }

    @Override
    public String toString() {
        return "ExportRecordDTO{" +
                "recordId=" + recordId +
                ", versionId=" + versionId +
                ", recordTitle='" + recordTitle + '\'' +
                ", exportFullRecord=" + exportFullRecord +
                ", exportMetaData=" + exportMetaData +
                ", exportCodebook=" + exportCodebook +
                ", exportMatrix=" + exportMatrix +
                ", warnings=" + warnings +
                '}';
    }
}
