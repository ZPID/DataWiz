package de.zpid.datawiz.dao;

import de.zpid.datawiz.dto.UserRoleDTO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

/**
 * DAO Class for User Roles
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
@Repository
@Scope("singleton")
public class RoleDAO {

    private final ClassPathXmlApplicationContext applicationContext;
    private final JdbcTemplate jdbcTemplate;

    private static final Logger log = LogManager.getLogger(RoleDAO.class);

    /**
     * Instantiates a new RoleDAO.
     */
    @Autowired
    public RoleDAO(JdbcTemplate jdbcTemplate, ClassPathXmlApplicationContext applicationContext) {
        super();
        log.info("Loading RoleDAO as @Scope(\"singleton\") and @Repository");
        this.jdbcTemplate = jdbcTemplate;
        this.applicationContext = applicationContext;
    }

    /**
     * Saves a new user-project/study-role relation. Does not save a new role!
     *
     * @param role {@link UserRoleDTO} contains user, project/study and role identifiers
     * @return 1 if changes occurred, 0 if not
     */
    public int saveRole(final UserRoleDTO role) {
        log.trace("Entering setRole role: {}", () -> role);
        final int ret = this.jdbcTemplate.update(
                "INSERT INTO dw_user_roles (user_id, role_id, project_id, study_id) VALUES (?,?,?,?)", role.getUserId(),
                role.getRoleId(), (role.getProjectId() > 0) ? role.getProjectId() : null,
                (role.getStudyId() > 0) ? role.getStudyId() : null);
        log.debug("Transaction for setRole returned [{}]", () -> ret);
        return ret;
    }

    /**
     * Deletes a new user-project/study-role relation. Does not delete a role!
     *
     * @param role {@link UserRoleDTO} contains user, project/study and role identifiers
     */
    public void deleteRole(final UserRoleDTO role) {
        log.trace("Entering deleteRole role: {}", () -> role);
        List<Object> oList = new LinkedList<>();
        oList.add(role.getRoleId());
        oList.add(role.getUserId());
        if (role.getProjectId() > 0)
            oList.add(role.getProjectId());
        if (role.getStudyId() > 0)
            oList.add(role.getStudyId());
        final int ret = this.jdbcTemplate.update(
                "DELETE FROM dw_user_roles WHERE dw_user_roles.role_id = ? AND dw_user_roles.user_id = ? "
                        + ((role.getProjectId() > 0) ? " AND dw_user_roles.project_id = ? "
                        : " AND dw_user_roles.project_id IS NULL")
                        + ((role.getStudyId() > 0) ? " AND dw_user_roles.study_id = ?" : " AND dw_user_roles.study_id IS NULL"),
                oList.toArray());
        log.debug("Transaction for deleteRole returned [{}]", () -> ret);
    }

    /**
     * Finds all roles which belong to a user
     *
     * @param id long User identifier
     * @return {@link List} of {@link UserRoleDTO}
     */
    public List<UserRoleDTO> findRolesByUserID(final long id) {
        log.trace("Entering findRolesByUserID id: {}", () -> id);
        String sql = "SELECT * FROM dw_user_roles " + " JOIN dw_roles ON dw_user_roles.role_id = dw_roles.id "
                + "WHERE user_id  = ?";
        final List<UserRoleDTO> ret = this.jdbcTemplate.query(sql, new Object[]{id}, (rs, rowNum) -> setRole(rs));
        log.debug("Transaction for findRolesByUserID returned [length: {}]", ret::size);
        return ret;
    }

    /**
     * Finds all role types except of 'ADMIN', 'USER' or 'REL_ROLE'
     *
     * @return {@link List} of Role Types {@link String}
     */
    public List<String> findAllProjectRoles() {
        log.trace("Entering findAllProjectRoles");
        String sql = "SELECT type FROM dw_roles WHERE type != 'USER' AND type != 'ADMIN' AND type != 'REL_ROLE'";
        final List<String> ret = this.jdbcTemplate.query(sql, new Object[]{}, (rs, rowNum) -> rs.getString("type"));
        log.debug("Transaction for findAllProjectRoles returned [length: {}]", ret::size);
        return ret;
    }

    /**
     * Finds all roles which belong to a user and a specific project
     *
     * @param uid long User identifier
     * @param pid long Project identifier
     * @return {@link List} of {@link UserRoleDTO}
     */
    public List<UserRoleDTO> findRolesByUserIDAndProjectID(final long uid, final long pid) {
        log.trace("execute findRolesByUserIDAndProjectID for [userId: {} projectId: {}]", () -> uid, () -> pid);
        String sql = "SELECT * FROM dw_user_roles " + " JOIN dw_roles ON dw_user_roles.role_id = dw_roles.id "
                + "WHERE user_id  = ? AND project_id = ?";
        final List<UserRoleDTO> ret = this.jdbcTemplate.query(sql, new Object[]{uid, pid}, (rs, rowNum) -> setRole(rs));
        log.debug("Transaction for findRolesByUserIDAndProjectID returned [length: {}]", ret::size);
        return ret;
    }

    /**
     * Builds the RoleDTO from the ResultSet
     *
     * @param rs {@link ResultSet}
     * @return {@link UserRoleDTO}
     * @throws SQLException DBS Exceptions
     */
    private UserRoleDTO setRole(final ResultSet rs) throws SQLException {
        final UserRoleDTO role = (UserRoleDTO) applicationContext.getBean("UserRoleDTO");
        role.setRoleId(rs.getLong("id"));
        role.setStudyId(rs.getLong("study_id"));
        role.setProjectId(rs.getLong("project_id"));
        role.setUserId(rs.getLong("user_id"));
        role.setType(rs.getString("type"));
        return role;
    }
}
