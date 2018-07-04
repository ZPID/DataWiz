package de.zpid.datawiz.dao;

import de.zpid.datawiz.dto.ProjectDTO;
import de.zpid.datawiz.dto.UserDTO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * DAO Class for Project
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
public class ProjectDAO {

    private final ClassPathXmlApplicationContext applicationContext;
    private final JdbcTemplate jdbcTemplate;

    private static final Logger log = LogManager.getLogger(ProjectDAO.class);

    /**
     * Instantiates a new ProjectDAO.
     */
    @Autowired
    public ProjectDAO(ClassPathXmlApplicationContext applicationContext, JdbcTemplate jdbcTemplate) {
        super();
        log.info("Loading ProjectDAO as @Scope(\"singleton\") and @Repository");
        this.applicationContext = applicationContext;
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * This function returns a list of all matching ProjectDTO entities from table dw_project for which the passed user object is linked by one of the different
     * roles.
     *
     * @param user {@link UserDTO} contains user identifier
     * @return {@link List} of {@link ProjectDTO}
     */
    public List<ProjectDTO> findAllByUserID(final UserDTO user) {
        log.trace("Entering findAllByUserID for user [id: {}; email: {}]", user::getId, user::getEmail);
        String sql = "SELECT dw_project.* FROM dw_user_roles " + "LEFT JOIN dw_project ON dw_user_roles.project_id = dw_project.id "
                + "LEFT JOIN dw_roles ON dw_user_roles.role_id = dw_roles.id " + "WHERE dw_user_roles.user_id = ? AND dw_user_roles.project_id > 0 "
                + "GROUP BY dw_project.id";
        List<ProjectDTO> ret = jdbcTemplate.query(sql, new Object[]{user.getId()}, (resultSet, rowNum) -> setProjectDTO(resultSet));
        log.debug("Transaction \"findAllByUserID\" terminates with result: [length: {}]", ret::size);
        return ret;
    }

    /**
     * This function returns a List of all ProjectDTO entities from the table dw_project
     *
     * @return {@link List} of {@link ProjectDTO}
     */
    public List<ProjectDTO> findAll() {
        log.trace("Entering findAll");
        String sql = "SELECT * FROM dw_project ORDER BY dw_project.owner_id";
        List<ProjectDTO> ret = jdbcTemplate.query(sql, new Object[]{}, (resultSet, rowNum) -> setProjectDTO(resultSet));
        log.debug("Transaction \"findAll\" terminates with result: [length: {}]", ret::size);
        return ret;
    }

    /**
     * This function returns a List of all matching ProjectDTO entities from the table dw_project, for which the passed user object has the role "PROJECT_ADMIN"
     *
     * @param userID User identifier as long
     * @return {@link List} of {@link ProjectDTO}
     */
    public List<ProjectDTO> findAllByAdminRole(final long userID) {
        log.trace("Entering findAllByAdminRole for user [id: {}]", () -> userID);
        String sql = "SELECT dw_project.* FROM dw_user_roles LEFT JOIN dw_project ON dw_user_roles.project_id = dw_project.id "
                + "LEFT JOIN dw_roles ON dw_user_roles.role_id = dw_roles.id WHERE dw_user_roles.user_id = ? "
                + "AND dw_user_roles.project_id > 0 AND dw_roles.type = 'PROJECT_ADMIN' GROUP BY dw_project.id";
        List<ProjectDTO> ret = jdbcTemplate.query(sql, new Object[]{userID}, (resultSet, rowNum) -> setProjectDTO(resultSet));
        log.debug("Transaction \"findAllByAdminRole\" terminates with result: [length: {}]", ret::size);
        return ret;
    }

    /**
     * This function returns a ProjectDTO entity from the table dw_project, depending on the passed identifier. If no project was found, null is returned.
     *
     * @param projectId Project identifier as long
     * @return {@link ProjectDTO} on success - otherwise null
     */
    public ProjectDTO findById(final long projectId) {
        log.trace("Entering findById for project [id: {}]", () -> projectId);
        ProjectDTO project;
        try {
            project = jdbcTemplate.queryForObject("SELECT dw_project.* FROM dw_project WHERE  dw_project.id = ?", new Object[]{projectId}, (resultSet, rowNum) -> setProjectDTO(resultSet));
        } catch (EmptyResultDataAccessException e) {
            project = null;
        }
        log.debug("Transaction \"findById\" terminates with result: [Title: {}]", (project != null) ? project.getTitle() : "null");
        return project;
    }

    /**
     * This function returns a single value from the table dw_project_invite, depending on the passed parameters.
     *
     * @param email     {@link String} Mail of the invited user
     * @param projectId long Project identifier
     * @param val       {@link String} Value to be searched and returned
     * @return {@link String} with the searched value on success, otherwise null
     */
    public String findValFromInviteData(final String email, final long projectId, final String val) {
        log.trace("Entering getValFromInviteData for [email: {}], [pid: {}] and [val: {}]", () -> email, () -> projectId, () -> val);
        final String sql = "SELECT " + val + " from dw_project_invite WHERE user_email = ? AND project_id = ?";
        String ret;
        try {
            ret = jdbcTemplate.queryForObject(sql, new Object[]{email, projectId}, String.class);
        } catch (DataAccessException e) {
            ret = null;
        }
        log.debug("Transaction \"findValFromInviteData\" terminates with result: [value:{}; result: {}]", val, ret);
        return ret;
    }

    /**
     * This function returns a single value from the table dw_project_invite, depending on the passed parameters.
     *
     * @param email    {@link String}  Mail of the invited user
     * @param linkhash {@link String} Invitation linkhash value
     * @param val      {@link String} Value to be searched and returned
     * @return {@link String} with the searched value on success, otherwise '0'
     */
    public String findValFromInviteData(final String email, final String linkhash, final String val) {
        log.trace("Entering getValFromInviteData for [email: {}], [linkhash : {}] and [val: {}]", () -> email, () -> linkhash, () -> val);
        final String sql = "SELECT " + val + " from dw_project_invite WHERE user_email = ? AND linkhash = ?";
        String ret;
        try {
            ret = jdbcTemplate.queryForObject(sql, new Object[]{email, linkhash}, String.class);
        } catch (DataAccessException e) {
            ret = "0";
        }
        log.debug("Transaction \"getValFromInviteData\" terminates with result: [value:{}; resul: {}]", val, ret);
        return ret;
    }

    /**
     * This function returns a List of all invitations for a project from the table dw_project, depending on the passed project identifier.
     *
     * @param pid long Project identifier
     * @return List of Invitations (mails as {@link String})
     */
    public List<String> findPendingInvitesByProjectID(final long pid) {
        log.trace("Entering findPendingInvitesByProjectID for project [id: {}]", () -> pid);
        final String sql = "SELECT user_email from dw_project_invite WHERE project_id = ?";
        List<String> ret = jdbcTemplate.query(sql, new Object[]{pid}, (resultSet, rowNum) -> resultSet.getString("user_email"));
        log.debug("Transaction \"findPendingInvitesByProjectID\" terminates with result: [lenght: {}]", ret::size);
        return ret;
    }

    /**
     * This function saves a new project entity into the table dw_project.
     *
     * @param project {@link ProjectDTO} Contains all project attributes
     * @return 1 if changes have happened, otherwise -1
     */
    public int insertProject(final ProjectDTO project) {
        log.trace("Entering insertProject [title: {}]", project::getTitle);
        KeyHolder holder = new GeneratedKeyHolder();
        final String stmt = "INSERT INTO dw_project (owner_id, created, last_user_id, last_edit, title, project_ident, funding, "
                + "grant_number, description) VALUES (?,?,?,?,?,?,?,?,?)";
        this.jdbcTemplate.update((Connection connection) -> {
            PreparedStatement ps = connection.prepareStatement(stmt, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, project.getOwnerId());
            ps.setString(2, LocalDateTime.now().toString());
            ps.setLong(3, project.getLastUserId());
            ps.setString(4, LocalDateTime.now().toString());
            ps.setString(5, project.getTitle());
            ps.setString(6, project.getProjectIdent());
            ps.setString(7, project.getFunding());
            ps.setString(8, project.getGrantNumber());
            ps.setString(9, project.getDescription());
            return ps;
        }, holder);
        final int key = (holder.getKey() != null && holder.getKey().intValue() > 0) ? holder.getKey().intValue() : -1;
        log.debug("Transaction \"insertProject\" terminates with result: [key: {}]", () -> key);
        return key;
    }

    /**
     * This function saves a new project - invitation relation entity into the table dw_project_invite.
     *
     * @param projectId long Project identifier
     * @param userMail  {@link String} Mail of the invited user
     * @param adminMail {@link String} Project Administrator Email
     */
    public void insertInviteEntity(final long projectId, final String userMail, final String adminMail) {
        log.trace("Entering insertInviteEntity for project [id: {}], User [id: {}] by Admin [email: {}]", () -> projectId, () -> userMail, () -> adminMail);
        int chk = this.jdbcTemplate.update("INSERT INTO dw_project_invite (user_email, invited_by, project_id, linkhash, date) VALUES (?,?,?,?, ?)", userMail,
                adminMail, projectId, UUID.randomUUID().toString(), LocalDateTime.now().toString());
        log.debug("Transaction \"insertInviteEntity\" terminates with result: [success: {}]", () -> chk);
    }

    /**
     * This function updates the mail address of a project - invitation relation entity from the table dw_project_invite.
     *
     * @param projectId    long Project identifier
     * @param userMailOld  {@link String} old mail of the invited user
     * @param userEmailNew {@link String} new mail of the invited user
     */
    public void updateInvitationEntity(long projectId, final String userMailOld, final String userEmailNew) {
        log.trace("Entering updateInvitationEntity for project [id: {}] and User [old email: {}; new email: {}]", () -> projectId, () -> userMailOld,
                () -> userEmailNew);
        int chk = this.jdbcTemplate.update("UPDATE dw_project_invite SET user_email = ? WHERE user_email = ? AND project_id = ?", userEmailNew, userMailOld,
                projectId);
        log.debug("Transaction \"updateInvitationEntity\" terminates with result: [result: {}]", () -> chk);
    }

    /**
     * This function updates a project entity from the table dw_project.
     *
     * @param project {@link ProjectDTO} Contains all project attributes
     */
    public void updateProject(final ProjectDTO project) {
        log.trace("Entering updateProject for project [id: {}]", project::getId);
        int chk = this.jdbcTemplate.update(
                "UPDATE dw_project SET last_user_id = ?, last_edit = ?, title = ?, " + "project_ident = ?, funding = ?, grant_number = ?, description = ? WHERE id = ?",
                project.getLastUserId(), LocalDateTime.now().toString(), project.getTitle(), project.getProjectIdent(), project.getFunding(), project.getGrantNumber(),
                project.getDescription(), project.getId());
        log.debug("Transaction \"updateProject\" terminates with result:  [result: {}]", () -> chk);
    }

    /**
     * This function deletes a project entity from the table dw_project.
     *
     * @param id long Project identifier
     */
    public void deleteProject(final long id) {
        log.trace("Entering deleteProject for project [id: {}]", () -> id);
        int chk = this.jdbcTemplate.update("DELETE FROM dw_project WHERE id = ? ", id);
        log.debug("Transaction \"deleteProject\" terminates with result:  [result: {}]", () -> chk);
    }

    /**
     * This function deletes a project - invitation relation entity from the table dw_project_invite.
     *
     * @param projectId long Project identifier
     * @param userMail  {@link String} Mail of the invited user
     */
    public void deleteInvitationEntity(final long projectId, final String userMail) {
        log.trace("Entering deleteInvitationEntity for project [id: {}] and User [id: {}]", () -> projectId, () -> userMail);
        int chk = this.jdbcTemplate.update("DELETE FROM dw_project_invite WHERE user_email = ? AND project_id = ?", userMail, projectId);
        log.debug("Transaction \"deleteInvitationEntity\" terminates with result:  [result: {}]", () -> chk);
    }

    /**
     * This function transfers the values from the ResultSet to a ProjectDTO
     *
     * @param rs {@link ResultSet}
     * @return {@link ProjectDTO}
     * @throws SQLException DBS Exceptions
     */
    private ProjectDTO setProjectDTO(ResultSet rs) throws SQLException {
        ProjectDTO project = (ProjectDTO) applicationContext.getBean("ProjectDTO");
        project.setId(rs.getInt("id"));
        project.setOwnerId(rs.getLong("owner_id"));
        project.setCreated(rs.getTimestamp("created").toLocalDateTime());
        project.setLastUserId(rs.getLong("last_user_id"));
        project.setLastEdit(rs.getTimestamp("last_edit").toLocalDateTime());
        project.setTitle(rs.getString("title"));
        project.setProjectIdent(rs.getString("project_ident"));
        project.setFunding(rs.getString("funding"));
        project.setGrantNumber(rs.getString("grant_number"));
        project.setDescription(rs.getString("description"));
        return project;
    }

}
