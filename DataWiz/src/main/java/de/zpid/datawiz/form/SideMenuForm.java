package de.zpid.datawiz.form;

import de.zpid.datawiz.dto.SideMenuDTO;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

/**
 * Used to exchange side menu data between controller and view
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
public class SideMenuForm implements Serializable {


    private static final long serialVersionUID = 549623417016554186L;
    private String linkProject;
    private String linkDmp;
    private String linkStudies;
    private String linkProMat;
    private String linkContri;
    private String linkExport;
    private String linkStudy;
    private String linkRecords;
    private String linkStudMat;
    private String linkRecord;
    private String linkCodebook;
    private String linkMatrix;

    private List<SideMenuDTO> items;

    public String getLinkProject() {
        return linkProject;
    }

    public void setLinkProject(String linkProject) {
        this.linkProject = linkProject;
    }

    public String getLinkDmp() {
        return linkDmp;
    }

    public void setLinkDmp(String linkDmp) {
        this.linkDmp = linkDmp;
    }

    public String getLinkStudies() {
        return linkStudies;
    }

    public void setLinkStudies(String linkStudies) {
        this.linkStudies = linkStudies;
    }

    public String getLinkProMat() {
        return linkProMat;
    }

    public void setLinkProMat(String linkProMat) {
        this.linkProMat = linkProMat;
    }

    public String getLinkContri() {
        return linkContri;
    }

    public void setLinkContri(String linkContri) {
        this.linkContri = linkContri;
    }

    public String getLinkExport() {
        return linkExport;
    }

    public void setLinkExport(String linkExport) {
        this.linkExport = linkExport;
    }

    public String getLinkStudy() {
        return linkStudy;
    }

    public void setLinkStudy(String linkStudy) {
        this.linkStudy = linkStudy;
    }

    public String getLinkRecords() {
        return linkRecords;
    }

    public void setLinkRecords(String linkRecords) {
        this.linkRecords = linkRecords;
    }

    public String getLinkStudMat() {
        return linkStudMat;
    }

    public void setLinkStudMat(String linkStudMat) {
        this.linkStudMat = linkStudMat;
    }

    public String getLinkRecord() {
        return linkRecord;
    }

    public void setLinkRecord(String linkRecord) {
        this.linkRecord = linkRecord;
    }

    public String getLinkCodebook() {
        return linkCodebook;
    }

    public void setLinkCodebook(String linkCodebook) {
        this.linkCodebook = linkCodebook;
    }

    public String getLinkMatrix() {
        return linkMatrix;
    }

    public void setLinkMatrix(String linkMatrix) {
        this.linkMatrix = linkMatrix;
    }

    public List<SideMenuDTO> getItems() {
        return items;
    }

    public void setItems(List<SideMenuDTO> items) {
        this.items = items;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SideMenuForm that = (SideMenuForm) o;
        return Objects.equals(linkProject, that.linkProject) &&
                Objects.equals(linkDmp, that.linkDmp) &&
                Objects.equals(linkStudies, that.linkStudies) &&
                Objects.equals(linkProMat, that.linkProMat) &&
                Objects.equals(linkContri, that.linkContri) &&
                Objects.equals(linkExport, that.linkExport) &&
                Objects.equals(linkStudy, that.linkStudy) &&
                Objects.equals(linkRecords, that.linkRecords) &&
                Objects.equals(linkStudMat, that.linkStudMat) &&
                Objects.equals(linkRecord, that.linkRecord) &&
                Objects.equals(linkCodebook, that.linkCodebook) &&
                Objects.equals(linkMatrix, that.linkMatrix) &&
                Objects.equals(items, that.items);
    }

    @Override
    public int hashCode() {

        return Objects.hash(linkProject, linkDmp, linkStudies, linkProMat, linkContri, linkExport, linkStudy, linkRecords, linkStudMat, linkRecord, linkCodebook, linkMatrix, items);
    }

    @Override
    public String toString() {
        return "SideMenuForm{" +
                "linkProject='" + linkProject + '\'' +
                ", linkDmp='" + linkDmp + '\'' +
                ", linkStudies='" + linkStudies + '\'' +
                ", linkProMat='" + linkProMat + '\'' +
                ", linkContri='" + linkContri + '\'' +
                ", linkExport='" + linkExport + '\'' +
                ", linkStudy='" + linkStudy + '\'' +
                ", linkRecords='" + linkRecords + '\'' +
                ", linkStudMat='" + linkStudMat + '\'' +
                ", linkRecord='" + linkRecord + '\'' +
                ", linkCodebook='" + linkCodebook + '\'' +
                ", linkMatrix='" + linkMatrix + '\'' +
                ", items=" + items +
                '}';
    }
}
