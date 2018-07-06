package de.zpid.datawiz.form;

import de.zpid.datawiz.dto.*;
import de.zpid.spss.dto.SPSSVarDTO;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.Set;


/**
 * Used to exchange study data between controller and view
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
    private String scrollPosition;
    private String fileType;
    private String jQueryMap;

    private String selectedFileType;
    private MultipartFile spssFile;
    private MultipartFile codeBookFile;
    private MultipartFile csvFile;
    private char csvSeperator;
    private char csvQuoteChar;
    private char csvDecChar;
    private String newChangeLog;
    private boolean headerRow;
    private RecordDTO record;
    private RecordDTO previousRecordVersion;
    private FileDTO file;
    private List<String[]> importMatrix;
    private Boolean parsingError;
    private Set<String> warnings;
    private List<String> errors;
    private List<RecordCompareDTO> compList;
    private List<SPSSVarDTO> viewVars;
    private List<SPSSVarDTO> delVars;

    private int pageLoadMin;
    private int pageLoadMax;
    private boolean ignoreValidationErrors;

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

    public String getScrollPosition() {
        return scrollPosition;
    }

    public void setScrollPosition(String scrollPosition) {
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

    public boolean isHeaderRow() {
        return headerRow;
    }

    public void setHeaderRow(boolean headerRow) {
        this.headerRow = headerRow;
    }

    public RecordDTO getRecord() {
        return record;
    }

    public void setRecord(RecordDTO record) {
        this.record = record;
    }

    public List<String[]> getImportMatrix() {
        return importMatrix;
    }

    public void setImportMatrix(List<String[]> importMatrix) {
        this.importMatrix = importMatrix;
    }

    public FileDTO getFile() {
        return file;
    }

    public void setFile(FileDTO file) {
        this.file = file;
    }

    public Boolean getParsingError() {
        return parsingError;
    }

    public void setParsingError(Boolean parsingError) {
        this.parsingError = parsingError;
    }

    public Set<String> getWarnings() {
        return warnings;
    }

    public void setWarnings(Set<String> warnings) {
        this.warnings = warnings;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

    public RecordDTO getPreviousRecordVersion() {
        return previousRecordVersion;
    }

    public void setPreviousRecordVersion(RecordDTO previousRecordVersion) {
        this.previousRecordVersion = previousRecordVersion;
    }

    public List<RecordCompareDTO> getCompList() {
        return compList;
    }

    public void setCompList(List<RecordCompareDTO> compList) {
        this.compList = compList;
    }

    public List<SPSSVarDTO> getViewVars() {
        return viewVars;
    }

    public void setViewVars(List<SPSSVarDTO> viewVars) {
        this.viewVars = viewVars;
    }

    public List<SPSSVarDTO> getDelVars() {
        return delVars;
    }

    public void setDelVars(List<SPSSVarDTO> delVars) {
        this.delVars = delVars;
    }

    public String getjQueryMap() {
        return jQueryMap;
    }

    public void setjQueryMap(String jQueryMap) {
        this.jQueryMap = jQueryMap;
    }

    public int getPageLoadMin() {
        return pageLoadMin;
    }

    public void setPageLoadMin(int pageLoadMin) {
        this.pageLoadMin = pageLoadMin;
    }

    public int getPageLoadMax() {
        return pageLoadMax;
    }

    public void setPageLoadMax(int pageLoadMax) {
        this.pageLoadMax = pageLoadMax;
    }

    public boolean isIgnoreValidationErrors() {
        return ignoreValidationErrors;
    }

    public void setIgnoreValidationErrors(boolean ignoreValidationErrors) {
        this.ignoreValidationErrors = ignoreValidationErrors;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StudyForm studyForm = (StudyForm) o;
        return hiddenVar == studyForm.hiddenVar &&
                delPos == studyForm.delPos &&
                csvSeperator == studyForm.csvSeperator &&
                csvQuoteChar == studyForm.csvQuoteChar &&
                csvDecChar == studyForm.csvDecChar &&
                headerRow == studyForm.headerRow &&
                pageLoadMin == studyForm.pageLoadMin &&
                pageLoadMax == studyForm.pageLoadMax &&
                ignoreValidationErrors == studyForm.ignoreValidationErrors &&
                Objects.equals(study, studyForm.study) &&
                Objects.equals(project, studyForm.project) &&
                Objects.equals(projectContributors, studyForm.projectContributors) &&
                Objects.equals(collectionModes, studyForm.collectionModes) &&
                Objects.equals(sourFormat, studyForm.sourFormat) &&
                Objects.equals(records, studyForm.records) &&
                Objects.equals(scrollPosition, studyForm.scrollPosition) &&
                Objects.equals(fileType, studyForm.fileType) &&
                Objects.equals(jQueryMap, studyForm.jQueryMap) &&
                Objects.equals(selectedFileType, studyForm.selectedFileType) &&
                Objects.equals(spssFile, studyForm.spssFile) &&
                Objects.equals(codeBookFile, studyForm.codeBookFile) &&
                Objects.equals(csvFile, studyForm.csvFile) &&
                Objects.equals(newChangeLog, studyForm.newChangeLog) &&
                Objects.equals(record, studyForm.record) &&
                Objects.equals(previousRecordVersion, studyForm.previousRecordVersion) &&
                Objects.equals(file, studyForm.file) &&
                Objects.equals(importMatrix, studyForm.importMatrix) &&
                Objects.equals(parsingError, studyForm.parsingError) &&
                Objects.equals(warnings, studyForm.warnings) &&
                Objects.equals(errors, studyForm.errors) &&
                Objects.equals(compList, studyForm.compList) &&
                Objects.equals(viewVars, studyForm.viewVars) &&
                Objects.equals(delVars, studyForm.delVars);
    }

    @Override
    public int hashCode() {

        return Objects.hash(study, project, projectContributors, collectionModes, sourFormat, records, hiddenVar, delPos, scrollPosition, fileType, jQueryMap, selectedFileType, spssFile, codeBookFile, csvFile, csvSeperator, csvQuoteChar, csvDecChar, newChangeLog, headerRow, record, previousRecordVersion, file, importMatrix, parsingError, warnings, errors, compList, viewVars, delVars, pageLoadMin, pageLoadMax, ignoreValidationErrors);
    }


    @Override
    public String toString() {
        return "StudyForm{" +
                "study=" + study +
                ", project=" + project +
                ", projectContributors=" + projectContributors +
                ", collectionModes=" + collectionModes +
                ", sourFormat=" + sourFormat +
                ", records=" + records +
                ", hiddenVar=" + hiddenVar +
                ", delPos=" + delPos +
                ", scrollPosition='" + scrollPosition + '\'' +
                ", fileType='" + fileType + '\'' +
                ", jQueryMap='" + jQueryMap + '\'' +
                ", selectedFileType='" + selectedFileType + '\'' +
                ", spssFile=" + spssFile +
                ", codeBookFile=" + codeBookFile +
                ", csvFile=" + csvFile +
                ", csvSeperator=" + csvSeperator +
                ", csvQuoteChar=" + csvQuoteChar +
                ", csvDecChar=" + csvDecChar +
                ", newChangeLog='" + newChangeLog + '\'' +
                ", headerRow=" + headerRow +
                ", record=" + record +
                ", previousRecordVersion=" + previousRecordVersion +
                ", file=" + file +
                ", importMatrix=" + importMatrix +
                ", parsingError=" + parsingError +
                ", warnings=" + warnings +
                ", errors=" + errors +
                ", compList=" + compList +
                ", viewVars=" + viewVars +
                ", delVars=" + delVars +
                ", pageLoadMin=" + pageLoadMin +
                ", pageLoadMax=" + pageLoadMax +
                ", ignoreValidationErrors=" + ignoreValidationErrors +
                '}';
    }
}
