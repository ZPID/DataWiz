package de.zpid.datawiz.form;

import de.zpid.datawiz.dto.ExportStudyDTO;
import de.zpid.datawiz.dto.FileDTO;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;


/**
 * Used to exchange project export data between controller and view
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
public class ExportProjectForm implements Serializable {

    private static final long serialVersionUID = 4423068973345927927L;
    private long projectId;
    private String projectTitle;
    private boolean exportFullProject;
    private boolean exportMetaData;
    private boolean exportDMP;
    private boolean exportProjectMaterial;
    private List<ExportStudyDTO> studies;
    private List<FileDTO> material;
    private List<String> warnings;
    private List<String> notices;

    public long getProjectId() {
        return projectId;
    }

    public void setProjectId(long projectId) {
        this.projectId = projectId;
    }

    public String getProjectTitle() {
        return projectTitle;
    }

    public void setProjectTitle(String projectTitle) {
        this.projectTitle = projectTitle;
    }

    public boolean isExportFullProject() {
        return exportFullProject;
    }

    public void setExportFullProject(boolean exportFullProject) {
        this.exportFullProject = exportFullProject;
    }

    public boolean isExportMetaData() {
        return exportMetaData;
    }

    public void setExportMetaData(boolean exportMetaData) {
        this.exportMetaData = exportMetaData;
    }

    public boolean isExportDMP() {
        return exportDMP;
    }

    public void setExportDMP(boolean exportDMP) {
        this.exportDMP = exportDMP;
    }

    public boolean isExportProjectMaterial() {
        return exportProjectMaterial;
    }

    public void setExportProjectMaterial(boolean exportProjectMaterial) {
        this.exportProjectMaterial = exportProjectMaterial;
    }

    public List<ExportStudyDTO> getStudies() {
        return studies;
    }

    public void setStudies(List<ExportStudyDTO> studies) {
        this.studies = studies;
    }

    public List<FileDTO> getMaterial() {
        return material;
    }

    public void setMaterial(List<FileDTO> material) {
        this.material = material;
    }

    public List<String> getWarnings() {
        return warnings;
    }

    public void setWarnings(List<String> warnings) {
        this.warnings = warnings;
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
        ExportProjectForm that = (ExportProjectForm) o;
        return projectId == that.projectId &&
                exportFullProject == that.exportFullProject &&
                exportMetaData == that.exportMetaData &&
                exportDMP == that.exportDMP &&
                exportProjectMaterial == that.exportProjectMaterial &&
                Objects.equals(projectTitle, that.projectTitle) &&
                Objects.equals(studies, that.studies) &&
                Objects.equals(material, that.material) &&
                Objects.equals(warnings, that.warnings) &&
                Objects.equals(notices, that.notices);
    }

    @Override
    public int hashCode() {

        return Objects.hash(projectId, projectTitle, exportFullProject, exportMetaData, exportDMP, exportProjectMaterial, studies, material, warnings, notices);
    }

    @Override
    public String toString() {
        return "ExportProjectForm{" +
                "projectId=" + projectId +
                ", projectTitle='" + projectTitle + '\'' +
                ", exportFullProject=" + exportFullProject +
                ", exportMetaData=" + exportMetaData +
                ", exportDMP=" + exportDMP +
                ", exportProjectMaterial=" + exportProjectMaterial +
                ", studies=" + studies +
                ", material=" + material +
                ", warnings=" + warnings +
                ", notices=" + notices +
                '}';
    }
}
