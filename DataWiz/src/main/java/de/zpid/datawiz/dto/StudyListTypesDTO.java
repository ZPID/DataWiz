package de.zpid.datawiz.dto;

import de.zpid.datawiz.enumeration.DWFieldTypes;

import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Objects;

/**
 * StudyListTypes Data Transfer Object
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
public class StudyListTypesDTO implements Serializable {

    private static final long serialVersionUID = -3098463141488593526L;

    private long id;
    private long studyid;

    @Size(max = 1000, groups = {StudyDTO.StGeneralVal.class, StudyDTO.StDesignVal.class,
            StudyDTO.StSampleVal.class})
    private String text;
    private DWFieldTypes type;
    private int sort;
    private boolean timetable;
    private String objectivetype;

    public StudyListTypesDTO() {
        super();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getStudyid() {
        return studyid;
    }

    public void setStudyid(long studyid) {
        this.studyid = studyid;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public DWFieldTypes getType() {
        return type;
    }

    public void setType(DWFieldTypes type) {
        this.type = type;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public boolean isTimetable() {
        return timetable;
    }

    public void setTimetable(boolean timetable) {
        this.timetable = timetable;
    }

    public String getObjectivetype() {
        return objectivetype;
    }

    public void setObjectivetype(String objectivetype) {
        this.objectivetype = objectivetype;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StudyListTypesDTO that = (StudyListTypesDTO) o;
        return id == that.id &&
                studyid == that.studyid &&
                sort == that.sort &&
                timetable == that.timetable &&
                Objects.equals(text, that.text) &&
                type == that.type &&
                Objects.equals(objectivetype, that.objectivetype);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, studyid, text, type, sort, timetable, objectivetype);
    }

    @Override
    public String toString() {
        return "StudyListTypesDTO{" +
                "id=" + id +
                ", studyid=" + studyid +
                ", text='" + text + '\'' +
                ", type=" + type +
                ", sort=" + sort +
                ", timetable=" + timetable +
                ", objectivetype='" + objectivetype + '\'' +
                '}';
    }
}
