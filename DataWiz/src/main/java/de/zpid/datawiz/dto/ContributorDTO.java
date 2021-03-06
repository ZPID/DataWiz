package de.zpid.datawiz.dto;

import de.zpid.datawiz.util.RegexUtil;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Objects;


/**
 * Contributor Data Transfer Object
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
public class ContributorDTO implements Serializable {

    private static final long serialVersionUID = -5898704446715573949L;

    private long id;
    private long projectId;
    private long studyId;
    private int sort;
    @Pattern(regexp = RegexUtil.alphabeticWithBlanksAndHypens + RegexUtil.size0to50)
    private String title;
    @Pattern(regexp = RegexUtil.alphabeticWithBlanksAndHypens + RegexUtil.size0to250)
    private String firstName;
    @Pattern(regexp = RegexUtil.alphabeticWithBlanksAndHypens + RegexUtil.size0to250)
    private String lastName;
    @Size(max = 250)
    private String institution;
    @Size(max = 250)
    private String department;
    @Pattern(regexp = RegexUtil.regexORCID)
    private String orcid;
    private Boolean primaryContributor;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getProjectId() {
        return projectId;
    }

    public void setProjectId(long projectId) {
        this.projectId = projectId;
    }

    public long getStudyId() {
        return studyId;
    }

    public void setStudyId(long studyId) {
        this.studyId = studyId;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getInstitution() {
        return institution;
    }

    public void setInstitution(String institution) {
        this.institution = institution;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getOrcid() {
        return orcid;
    }

    public void setOrcid(String orcid) {
        this.orcid = orcid;
    }

    public Boolean getPrimaryContributor() {
        return primaryContributor;
    }

    public void setPrimaryContributor(Boolean primaryContributor) {
        this.primaryContributor = primaryContributor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ContributorDTO that = (ContributorDTO) o;
        return id == that.id &&
                projectId == that.projectId &&
                studyId == that.studyId &&
                sort == that.sort &&
                Objects.equals(title, that.title) &&
                Objects.equals(firstName, that.firstName) &&
                Objects.equals(lastName, that.lastName) &&
                Objects.equals(institution, that.institution) &&
                Objects.equals(department, that.department) &&
                Objects.equals(orcid, that.orcid) &&
                Objects.equals(primaryContributor, that.primaryContributor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, projectId, studyId, sort, title, firstName, lastName, institution, department, orcid, primaryContributor);
    }

    @Override
    public String toString() {
        return "ContributorDTO{" +
                "id=" + id +
                ", projectId=" + projectId +
                ", studyId=" + studyId +
                ", sort=" + sort +
                ", title='" + title + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", institution='" + institution + '\'' +
                ", department='" + department + '\'' +
                ", orcid='" + orcid + '\'' +
                ", primaryContributor=" + primaryContributor +
                '}';
    }
}
