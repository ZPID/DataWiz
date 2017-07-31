package de.zpid.datawiz.dto;

import java.io.Serializable;
import java.util.List;

public class ExportStudyDTO implements Serializable {

	private static final long serialVersionUID = 4195314952192465761L;
	private long studyId;
	private String studyTitle;
	private boolean exportFullStudy;
	private boolean exportMetaData;
	private boolean exportStudyMaterial;
	private List<ExportRecordDTO> records;
	private List<String> warnings;

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

	@Override
	public String toString() {
		return "ExportStudyDTO [studyId=" + studyId + ", studyTitle=" + studyTitle + ", exportFullStudy=" + exportFullStudy + ", exportMetaData="
		    + exportMetaData + ", exportStudyMaterial=" + exportStudyMaterial + ", records=" + records + ", warnings=" + warnings + "]";
	}

}
