package de.zpid.datawiz.form;

import java.io.Serializable;
import java.util.List;

import de.zpid.datawiz.dto.ExportStudyDTO;
import de.zpid.datawiz.dto.FileDTO;

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

	@Override
	public String toString() {
		return "ExportProjectForm [projectId=" + projectId + ", projectTitle=" + projectTitle + ", exportFullProject=" + exportFullProject
		    + ", exportMetaData=" + exportMetaData + ", exportDMP=" + exportDMP + ", exportProjectMaterial=" + exportProjectMaterial + ", studies="
		    + studies + "]";
	}

}
