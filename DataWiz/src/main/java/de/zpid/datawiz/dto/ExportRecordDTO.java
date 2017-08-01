package de.zpid.datawiz.dto;

import java.io.Serializable;
import java.util.List;

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
	public String toString() {
		return "ExportRecordDTO [recordId=" + recordId + ", versionId=" + versionId + ", recordTitle=" + recordTitle + ", exportFullRecord="
		    + exportFullRecord + ", exportMetaData=" + exportMetaData + ", exportCodebook=" + exportCodebook + ", exportMatrix=" + exportMatrix
		    + ", warnings=" + warnings + "]";
	}

}
