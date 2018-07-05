package de.zpid.datawiz.dto;

import de.zpid.datawiz.enumeration.DWFieldTypes;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Objects;

/**
 * FormTypes Data Transfer Object
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
 * <p>
 * Implementation of the MetaPorpose which are declared as DMP131in the excel meta data sheet.
 */
public class FormTypesDTO implements Serializable {

    /**
     * The Constant serialVersionUID.
     */
    private static final long serialVersionUID = 4523755361477035462L;

    /**
     * data type id.
     */
    @NotNull
    private int id;

    /**
     * The project id - saved in the.
     */
    private int dmpId;

    /**
     * German name of the MetaPorpose
     */
    @NotNull
    private String nameDE;

    /**
     * English name of the MetaPorpose.
     */
    @NotNull
    private String nameEN;

    /**
     * If "other" is chosen - possibility to enter these types.
     */
    private String otherTypes;

    /**
     * Delete these items is only possible if they are not used, but they can be set active or inactive.
     */
    private boolean active;

    /**
     * sort the MetaPorpose.
     */
    private int sort;

    /**
     * true if the investor is present.
     */
    private boolean investPresent;

    private DWFieldTypes type;

    public FormTypesDTO() {
        super();
    }

    public FormTypesDTO(int id, int dmpId, String nameDE, String nameEN, String otherTypes, boolean active, int sort,
                        boolean investPresent, DWFieldTypes type) {
        super();
        this.id = id;
        this.dmpId = dmpId;
        this.nameDE = nameDE;
        this.nameEN = nameEN;
        this.otherTypes = otherTypes;
        this.active = active;
        this.sort = sort;
        this.investPresent = investPresent;
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDmpId() {
        return dmpId;
    }

    public void setDmpId(int dmpId) {
        this.dmpId = dmpId;
    }

    public String getNameDE() {
        return nameDE;
    }

    public void setNameDE(String nameDE) {
        this.nameDE = nameDE;
    }

    public String getNameEN() {
        return nameEN;
    }

    public void setNameEN(String nameEN) {
        this.nameEN = nameEN;
    }

    public String getOtherTypes() {
        return otherTypes;
    }

    public void setOtherTypes(String otherTypes) {
        this.otherTypes = otherTypes;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public boolean isInvestPresent() {
        return investPresent;
    }

    public void setInvestPresent(boolean investPresent) {
        this.investPresent = investPresent;
    }

    public DWFieldTypes getType() {
        return type;
    }

    public void setType(DWFieldTypes type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FormTypesDTO that = (FormTypesDTO) o;
        return id == that.id &&
                dmpId == that.dmpId &&
                active == that.active &&
                sort == that.sort &&
                investPresent == that.investPresent &&
                Objects.equals(nameDE, that.nameDE) &&
                Objects.equals(nameEN, that.nameEN) &&
                Objects.equals(otherTypes, that.otherTypes) &&
                type == that.type;
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, dmpId, nameDE, nameEN, otherTypes, active, sort, investPresent, type);
    }

    @Override
    public String toString() {
        return "FormTypesDTO{" +
                "id=" + id +
                ", dmpId=" + dmpId +
                ", nameDE='" + nameDE + '\'' +
                ", nameEN='" + nameEN + '\'' +
                ", otherTypes='" + otherTypes + '\'' +
                ", active=" + active +
                ", sort=" + sort +
                ", investPresent=" + investPresent +
                ", type=" + type +
                '}';
    }
}
