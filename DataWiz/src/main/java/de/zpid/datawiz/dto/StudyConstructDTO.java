package de.zpid.datawiz.dto;

import de.zpid.datawiz.dto.StudyDTO.StDesignVal;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Objects;

/**
 * StudyConstruct Data Transfer Object
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
public class StudyConstructDTO implements Serializable {

    /**
     * The Constant serialVersionUID.
     */
    private static final long serialVersionUID = -7126615950848855816L;

    /**
     * The id.
     */
    private long id;

    private long StudyId;

    /**
     * The name of the construct.
     */
    @Size(min = 0, max = 500, groups = StudyDTO.StDesignVal.class)
    private String name;

    /**
     * The type for the select box.
     */
    @Pattern(regexp = "(^$|INDEPENDENT|DEPENDENT|CONTROL|OTHER)", groups = StDesignVal.class)
    private String type;

    /**
     * The other -> if other is selected as type.
     */
    @Size(min = 0, max = 500, groups = StudyDTO.StDesignVal.class)
    private String other;

    public StudyConstructDTO() {
        super();
    }

    public StudyConstructDTO(long id, long studyId, String name, String type, String other) {
        super();
        this.id = id;
        StudyId = studyId;
        this.name = name;
        this.type = type;
        this.other = other;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getStudyId() {
        return StudyId;
    }

    public void setStudyId(long studyId) {
        StudyId = studyId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getOther() {
        return other;
    }

    public void setOther(String other) {
        this.other = other;
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StudyConstructDTO that = (StudyConstructDTO) o;
        return id == that.id &&
                StudyId == that.StudyId &&
                Objects.equals(name, that.name) &&
                Objects.equals(type, that.type) &&
                Objects.equals(other, that.other);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, StudyId, name, type, other);
    }

    @Override
    public String toString() {
        return "StudyConstructDTO{" +
                "id=" + id +
                ", StudyId=" + StudyId +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", other='" + other + '\'' +
                '}';
    }
}
