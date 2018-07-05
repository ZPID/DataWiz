package de.zpid.datawiz.dto;

import java.io.Serializable;

/**
 * UserRole Data Transfer Object
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
public class UserRoleDTO implements Serializable {

    private static final long serialVersionUID = -1945304373696201185L;
    private long roleId;
    private long userId;
    private long projectId;
    private long studyId;
    private String type;

    public long getRoleId() {
        return roleId;
    }

    public long getUserId() {
        return userId;
    }

    public long getProjectId() {
        return projectId;
    }

    public long getStudyId() {
        return studyId;
    }

    public String getType() {
        return type;
    }

    public void setRoleId(long roleId) {
        this.roleId = roleId;

    }

    public void setUserId(long userId) {
        this.userId = userId;

    }

    public void setProjectId(long projectId) {
        this.projectId = projectId;

    }

    public void setStudyId(long studyId) {
        this.studyId = studyId;

    }

    public void setType(String type) {
        this.type = type;

    }

    public UserRoleDTO() {
        super();
    }

    public UserRoleDTO(long roleId, long userId, long projectId, long studyId, String type) {
        super();
        this.roleId = roleId;
        this.userId = userId;
        this.projectId = projectId;
        this.studyId = studyId;
        this.type = type;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (projectId ^ (projectId >>> 32));
        result = prime * result + (int) (roleId ^ (roleId >>> 32));
        result = prime * result + (int) (studyId ^ (studyId >>> 32));
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        result = prime * result + (int) (userId ^ (userId >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        UserRoleDTO other = (UserRoleDTO) obj;
        if (projectId != other.projectId)
            return false;
        if (roleId != other.roleId)
            return false;
        if (studyId != other.studyId)
            return false;
        if (type == null) {
            if (other.type != null)
                return false;
        } else if (!type.equals(other.type))
            return false;
        return userId == other.userId;
    }

    @Override
    public String toString() {
        return "UserRoleDTO [roleId=" + roleId + ", userId=" + userId + ", projectId=" + projectId + ", studyId=" + studyId
                + ", type=" + type + "]";
    }

}
