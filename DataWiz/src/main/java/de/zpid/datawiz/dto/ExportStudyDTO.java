package de.zpid.datawiz.dto;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

/**
 * ExportStudy Data Transfer Object
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
public class ExportStudyDTO implements Serializable {

    private static final long serialVersionUID = 4195314952192465761L;
    private long studyId;
    private String studyTitle;
    private boolean exportFullStudy;
    private boolean exportMetaData;
    private boolean exportStudyMaterial;
    private List<ExportRecordDTO> records;
    private List<FileDTO> material;
    private List<String> warnings;
    private List<String> notices;

    public long getStudyId() {
        return studyId;
    }

    public void setStudyId(long studyId) {
        this.studyId = studyId;
    }

    public String getStudyTitle() {
        return studyTitle;
    }

    public void setStudyTitle(String studyTitle) {
        this.studyTitle = studyTitle;
    }

    public boolean isExportFullStudy() {
        return exportFullStudy;
    }

    public void setExportFullStudy(boolean exportFullStudy) {
        this.exportFullStudy = exportFullStudy;
    }

    public boolean isExportMetaData() {
        return exportMetaData;
    }

    public void setExportMetaData(boolean exportMetaData) {
        this.exportMetaData = exportMetaData;
    }

    public boolean isExportStudyMaterial() {
        return exportStudyMaterial;
    }

    public void setExportStudyMaterial(boolean exportStudyMaterial) {
        this.exportStudyMaterial = exportStudyMaterial;
    }

    public List<ExportRecordDTO> getRecords() {
        return records;
    }

    public void setRecords(List<ExportRecordDTO> records) {
        this.records = records;
    }

    public List<String> getWarnings() {
        return warnings;
    }

    public void setWarnings(List<String> warnings) {
        this.warnings = warnings;
    }

    public List<FileDTO> getMaterial() {
        return material;
    }

    public void setMaterial(List<FileDTO> material) {
        this.material = material;
    }

    public List<String> getNotices() {
        return notices;
    }

    public void setNotices(List<String> notices) {
        this.notices = notices;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExportStudyDTO that = (ExportStudyDTO) o;
        return studyId == that.studyId &&
                exportFullStudy == that.exportFullStudy &&
                exportMetaData == that.exportMetaData &&
                exportStudyMaterial == that.exportStudyMaterial &&
                Objects.equals(studyTitle, that.studyTitle) &&
                Objects.equals(records, that.records) &&
                Objects.equals(material, that.material) &&
                Objects.equals(warnings, that.warnings) &&
                Objects.equals(notices, that.notices);
    }

    @Override
    public int hashCode() {
        return Objects.hash(studyId, studyTitle, exportFullStudy, exportMetaData, exportStudyMaterial, records, material, warnings, notices);
    }

    @Override
    public String toString() {
        return "ExportStudyDTO{" +
                "studyId=" + studyId +
                ", studyTitle='" + studyTitle + '\'' +
                ", exportFullStudy=" + exportFullStudy +
                ", exportMetaData=" + exportMetaData +
                ", exportStudyMaterial=" + exportStudyMaterial +
                ", records=" + records +
                ", material=" + material +
                ", warnings=" + warnings +
                ", notices=" + notices +
                '}';
    }
}
