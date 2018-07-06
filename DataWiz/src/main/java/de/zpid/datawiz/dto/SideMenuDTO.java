package de.zpid.datawiz.dto;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

/**
 * SideMenu Data Transfer Object
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
public class SideMenuDTO implements Serializable {

    private static final long serialVersionUID = -4412745685049356069L;
    private long id;
    private String title;
    private List<SideMenuDTO> sublist;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<SideMenuDTO> getSublist() {
        return sublist;
    }

    public void setSublist(List<SideMenuDTO> sublist) {
        this.sublist = sublist;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SideMenuDTO that = (SideMenuDTO) o;
        return id == that.id &&
                Objects.equals(title, that.title) &&
                Objects.equals(sublist, that.sublist);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, title, sublist);
    }

    @Override
    public String toString() {
        return "SideMenuDTO{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", sublist=" + sublist +
                '}';
    }
}
