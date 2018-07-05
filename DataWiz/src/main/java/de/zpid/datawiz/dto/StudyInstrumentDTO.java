package de.zpid.datawiz.dto;

import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Objects;

/**
 * StudyInstrument Data Transfer Object
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
public class StudyInstrumentDTO implements Serializable {

    private static final long serialVersionUID = -2468624912740410851L;

    private long id;
    private long studyId;
    /**
     * Study30
     */
    @Size(max = 500, groups = StudyDTO.StDesignVal.class)
    private String title;
    /**
     * Study31
     */
    @Size(max = 1000, groups = StudyDTO.StDesignVal.class)
    private String author;
    /**
     * Study32
     */
    @Size(max = 1000, groups = StudyDTO.StDesignVal.class)
    private String citation;
    /**
     * Study33
     */
    @Size(max = 2000, groups = StudyDTO.StDesignVal.class)
    private String summary;
    /**
     * Study34
     */
    @Size(max = 2000, groups = StudyDTO.StDesignVal.class)
    private String theoHint;
    /**
     * Study35
     */
    @Size(max = 2000, groups = StudyDTO.StDesignVal.class)
    private String structure;
    /**
     * Study36
     */
    @Size(max = 2000, groups = StudyDTO.StDesignVal.class)
    private String construction;
    /**
     * Study37
     */
    @Size(max = 2000, groups = StudyDTO.StDesignVal.class)
    private String objectivity;
    /**
     * Study38
     */
    @Size(max = 2000, groups = StudyDTO.StDesignVal.class)
    private String reliability;
    /**
     * Study39
     */
    @Size(max = 2000, groups = StudyDTO.StDesignVal.class)
    private String validity;
    /**
     * Study40
     */
    @Size(max = 2000, groups = StudyDTO.StDesignVal.class)
    private String norm;

    public StudyInstrumentDTO(long id, String title, String author, String citation, String summary, String theoHint,
                              String structure, String construction, String objectivity, String reliability, String validity, String norm) {
        super();
        this.id = id;
        this.title = title;
        this.author = author;
        this.citation = citation;
        this.summary = summary;
        this.theoHint = theoHint;
        this.structure = structure;
        this.construction = construction;
        this.objectivity = objectivity;
        this.reliability = reliability;
        this.validity = validity;
        this.norm = norm;
    }

    public StudyInstrumentDTO() {
        super();
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getCitation() {
        return citation;
    }

    public void setCitation(String citation) {
        this.citation = citation;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getTheoHint() {
        return theoHint;
    }

    public void setTheoHint(String theoHint) {
        this.theoHint = theoHint;
    }

    public String getStructure() {
        return structure;
    }

    public void setStructure(String structure) {
        this.structure = structure;
    }

    public String getConstruction() {
        return construction;
    }

    public void setConstruction(String construction) {
        this.construction = construction;
    }

    public String getObjectivity() {
        return objectivity;
    }

    public void setObjectivity(String objectivity) {
        this.objectivity = objectivity;
    }

    public String getReliability() {
        return reliability;
    }

    public void setReliability(String reliability) {
        this.reliability = reliability;
    }

    public String getValidity() {
        return validity;
    }

    public void setValidity(String validity) {
        this.validity = validity;
    }

    public String getNorm() {
        return norm;
    }

    public void setNorm(String norm) {
        this.norm = norm;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StudyInstrumentDTO that = (StudyInstrumentDTO) o;
        return id == that.id &&
                studyId == that.studyId &&
                Objects.equals(title, that.title) &&
                Objects.equals(author, that.author) &&
                Objects.equals(citation, that.citation) &&
                Objects.equals(summary, that.summary) &&
                Objects.equals(theoHint, that.theoHint) &&
                Objects.equals(structure, that.structure) &&
                Objects.equals(construction, that.construction) &&
                Objects.equals(objectivity, that.objectivity) &&
                Objects.equals(reliability, that.reliability) &&
                Objects.equals(validity, that.validity) &&
                Objects.equals(norm, that.norm);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, studyId, title, author, citation, summary, theoHint, structure, construction, objectivity, reliability, validity, norm);
    }

    @Override
    public String toString() {
        return "StudyInstrumentDTO{" +
                "id=" + id +
                ", studyId=" + studyId +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", citation='" + citation + '\'' +
                ", summary='" + summary + '\'' +
                ", theoHint='" + theoHint + '\'' +
                ", structure='" + structure + '\'' +
                ", construction='" + construction + '\'' +
                ", objectivity='" + objectivity + '\'' +
                ", reliability='" + reliability + '\'' +
                ", validity='" + validity + '\'' +
                ", norm='" + norm + '\'' +
                '}';
    }
}
