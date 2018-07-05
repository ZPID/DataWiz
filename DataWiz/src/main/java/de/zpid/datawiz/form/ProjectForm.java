package de.zpid.datawiz.form;

import de.zpid.datawiz.dto.*;

import javax.validation.Valid;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;


/**
 * Used to exchange project data between controller and view
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
public class ProjectForm implements Serializable {

    private static final long serialVersionUID = 868777577427023558L;
    @Valid
    private ProjectDTO project;
    @Valid
    private List<UserDTO> sharedUser;
    @Valid
    private List<StudyDTO> studies;
    @Valid
    private List<ContributorDTO> contributors;
    @Valid
    private ContributorDTO primaryContributor;
    @Valid
    private DmpDTO dmp;

    private List<String> tags;
    private List<FileDTO> files;

    // DMP DATA
    private List<FormTypesDTO> dataTypes;
    private List<FormTypesDTO> collectionModes;
    private List<FormTypesDTO> metaPurposes;

    // ACCESS DATA
    private List<String> roleList;
    private List<String> pendingMails;
    private UserRoleDTO newRole;
    private String delMail;

    // ViewHelper
    private int delPos;
    private String pagePosi;

    public ProjectDTO getProject() {
        return project;
    }

    public void setProject(ProjectDTO project) {
        this.project = project;
    }

    public List<UserDTO> getSharedUser() {
        return sharedUser;
    }

    public void setSharedUser(List<UserDTO> list) {
        this.sharedUser = list;
    }

    public List<StudyDTO> getStudies() {
        return studies;
    }

    public void setStudies(List<StudyDTO> studies) {
        this.studies = studies;

    }

    public List<ContributorDTO> getContributors() {
        return contributors;
    }

    public void setContributors(List<ContributorDTO> contributors) {
        this.contributors = contributors;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public ContributorDTO getPrimaryContributor() {
        return primaryContributor;
    }

    public void setPrimaryContributor(ContributorDTO primaryContributor) {
        this.primaryContributor = primaryContributor;
    }

    public int getDelPos() {
        return delPos;
    }

    public void setDelPos(int delPos) {
        this.delPos = delPos;
    }

    public List<FileDTO> getFiles() {
        return files;
    }

    public void setFiles(List<FileDTO> files) {
        this.files = files;
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }

    public DmpDTO getDmp() {
        return dmp;
    }

    public void setDmp(DmpDTO dmp) {
        this.dmp = dmp;
    }

    public List<FormTypesDTO> getDataTypes() {
        return dataTypes;
    }

    public void setDataTypes(List<FormTypesDTO> dataTypes) {
        this.dataTypes = dataTypes;
    }

    public List<FormTypesDTO> getCollectionModes() {
        return collectionModes;
    }

    public void setCollectionModes(List<FormTypesDTO> collectionModes) {
        this.collectionModes = collectionModes;
    }

    public List<FormTypesDTO> getMetaPurposes() {
        return metaPurposes;
    }

    public void setMetaPurposes(List<FormTypesDTO> metaPurposes) {
        this.metaPurposes = metaPurposes;
    }

    public String getPagePosi() {
        return pagePosi;
    }

    public void setPagePosi(String pagePosi) {
        this.pagePosi = pagePosi;
    }

    public List<String> getRoleList() {
        return roleList;
    }

    public void setRoleList(List<String> roleList) {
        this.roleList = roleList;

    }

    public UserRoleDTO getNewRole() {
        return newRole;
    }

    public void setNewRole(UserRoleDTO newRole) {
        this.newRole = newRole;

    }

    public String getDelMail() {
        return delMail;
    }

    public void setDelMail(String delMail) {
        this.delMail = delMail;

    }

    public List<String> getPendingMails() {
        return pendingMails;
    }

    public void setPendingMails(List<String> pendingMails) {
        this.pendingMails = pendingMails;

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProjectForm that = (ProjectForm) o;
        return delPos == that.delPos &&
                Objects.equals(project, that.project) &&
                Objects.equals(sharedUser, that.sharedUser) &&
                Objects.equals(studies, that.studies) &&
                Objects.equals(contributors, that.contributors) &&
                Objects.equals(primaryContributor, that.primaryContributor) &&
                Objects.equals(dmp, that.dmp) &&
                Objects.equals(tags, that.tags) &&
                Objects.equals(files, that.files) &&
                Objects.equals(dataTypes, that.dataTypes) &&
                Objects.equals(collectionModes, that.collectionModes) &&
                Objects.equals(metaPurposes, that.metaPurposes) &&
                Objects.equals(roleList, that.roleList) &&
                Objects.equals(pendingMails, that.pendingMails) &&
                Objects.equals(newRole, that.newRole) &&
                Objects.equals(delMail, that.delMail) &&
                Objects.equals(pagePosi, that.pagePosi);
    }

    @Override
    public int hashCode() {

        return Objects.hash(project, sharedUser, studies, contributors, primaryContributor, dmp, tags, files, dataTypes, collectionModes, metaPurposes, roleList, pendingMails, newRole, delMail, delPos, pagePosi);
    }

    @Override
    public String toString() {
        return "ProjectForm{" +
                "project=" + project +
                ", sharedUser=" + sharedUser +
                ", studies=" + studies +
                ", contributors=" + contributors +
                ", primaryContributor=" + primaryContributor +
                ", dmp=" + dmp +
                ", tags=" + tags +
                ", files=" + files +
                ", dataTypes=" + dataTypes +
                ", collectionModes=" + collectionModes +
                ", metaPurposes=" + metaPurposes +
                ", roleList=" + roleList +
                ", pendingMails=" + pendingMails +
                ", newRole=" + newRole +
                ", delMail='" + delMail + '\'' +
                ", delPos=" + delPos +
                ", pagePosi='" + pagePosi + '\'' +
                '}';
    }
}
