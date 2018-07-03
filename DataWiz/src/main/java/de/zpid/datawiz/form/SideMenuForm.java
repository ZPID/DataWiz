package de.zpid.datawiz.form;

import de.zpid.datawiz.dto.SideMenuDTO;

import java.io.Serializable;
import java.util.List;

public class SideMenuForm implements Serializable {


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

}
