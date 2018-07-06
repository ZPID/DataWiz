package de.zpid.datawiz.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Project Data Transfer Object
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
public class ProjectDTO implements Serializable {

    private static final long serialVersionUID = -7148120591732205800L;

    private long id;
    private long ownerId;

    public interface ProjectVal {
    }

    @NotNull(groups = ProjectVal.class)
    @NotBlank(groups = ProjectVal.class)
    private String title;
    /**
     * Not the database ID - this is used for documentation and is not unique!
     */
    @Size(min = 0, max = 250)
    private String projectIdent;

    @Size(min = 0, max = 1000)
    private String funding;

    @Size(min = 0, max = 250)
    private String grantNumber;

    @Size(min = 0, max = 5000)
    private String description;

    private LocalDateTime created;
    private long lastUserId;
    private LocalDateTime lastEdit;
    private String thesaurusType;

    private String copyright;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(long ownerId) {
        this.ownerId = ownerId;

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

    public String getProjectIdent() {
        return projectIdent;
    }

    public void setProjectIdent(String projectIdent) {
        this.projectIdent = projectIdent;
    }

    public String getFunding() {
        return funding;
    }

    public void setFunding(String funding) {
        this.funding = funding;
    }

    public String getGrantNumber() {
        return grantNumber;
    }

    public void setGrantNumber(String grantNumber) {
        this.grantNumber = grantNumber;
    }

    public String getThesaurusType() {
        return thesaurusType;
    }

    public void setThesaurusType(String thesaurusType) {
        this.thesaurusType = thesaurusType;
    }

    public String getCopyright() {
        return copyright;
    }

    public void setCopyright(String copyright) {
        this.copyright = copyright;
    }

    public long getLastUserId() {
        return lastUserId;
    }

    public void setLastUserId(long lastUserId) {
        this.lastUserId = lastUserId;
    }

    public LocalDateTime getLastEdit() {
        return lastEdit;
    }

    public void setLastEdit(LocalDateTime lastEdit) {
        this.lastEdit = lastEdit;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProjectDTO that = (ProjectDTO) o;
        return id == that.id &&
                ownerId == that.ownerId &&
                lastUserId == that.lastUserId &&
                Objects.equals(title, that.title) &&
                Objects.equals(projectIdent, that.projectIdent) &&
                Objects.equals(funding, that.funding) &&
                Objects.equals(grantNumber, that.grantNumber) &&
                Objects.equals(description, that.description) &&
                Objects.equals(created, that.created) &&
                Objects.equals(lastEdit, that.lastEdit) &&
                Objects.equals(thesaurusType, that.thesaurusType) &&
                Objects.equals(copyright, that.copyright);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, ownerId, title, projectIdent, funding, grantNumber, description, created, lastUserId, lastEdit, thesaurusType, copyright);
    }

    @Override
    public String toString() {
        return "ProjectDTO{" +
                "id=" + id +
                ", ownerId=" + ownerId +
                ", title='" + title + '\'' +
                ", projectIdent='" + projectIdent + '\'' +
                ", funding='" + funding + '\'' +
                ", grantNumber='" + grantNumber + '\'' +
                ", description='" + description + '\'' +
                ", created=" + created +
                ", lastUserId=" + lastUserId +
                ", lastEdit=" + lastEdit +
                ", thesaurusType='" + thesaurusType + '\'' +
                ", copyright='" + copyright + '\'' +
                '}';
    }
}
