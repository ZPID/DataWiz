package de.zpid.datawiz.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import de.zpid.datawiz.dto.ProjectDTO;
import de.zpid.datawiz.dto.UserDTO;

/**
 * This file is part of Datawiz
 * 
 * <b>Copyright 2018, Leibniz Institute for Psychology Information (ZPID), <a href="http://zpid.de" title="http://zpid.de">http://zpid.de</a>.</b><br />
 * <br />
 * <a rel="license" href="http://creativecommons.org/licenses/by-nc-sa/4.0/"><img alt="Creative Commons License" style= "border-width:0" src=
 * "https://i.creativecommons.org/l/by-nc-sa/4.0/80x15.png" /></a><br />
 * <span xmlns:dct="http://purl.org/dc/terms/" property="dct:title">Datawiz</span> by
 * <a xmlns:cc="http://creativecommons.org/ns#" href="zpid.de" property="cc:attributionName" rel="cc:attributionURL"> Leibniz Institute for Psychology
 * Information (ZPID)</a> is licensed under a <a rel="license" href="http://creativecommons.org/licenses/by-nc-sa/4.0/">Creative Commons
 * Attribution-NonCommercial-ShareAlike 4.0 International License</a>. <br />
 * <br />
 * 
 * @author Ronny Boelter
 * @version 1.0
 *
 */
@Repository
@Scope("singleton")
public class ProjectDAO {

	@Autowired
	protected ClassPathXmlApplicationContext applicationContext;
	@Autowired
	protected JdbcTemplate jdbcTemplate;

	private static Logger log = LogManager.getLogger(ProjectDAO.class);

	/**
	 * Instantiates a new project DAO.
	 */
	public ProjectDAO() {
		super();
		log.info("Loading ProjectDAO as Singleton and Service");
	}

	/**
	 * This function returns a list of all matching ProjectDTO entities from table dw_project for which the passed user object is linked by one of the different
	 * roles.
	 * 
	 * @param user
	 *          User
	 * @return List of Projects
	 * @throws Exception
	 */
	public List<ProjectDTO> findAllByUserID(final UserDTO user) throws Exception {
		log.trace("Entering findAllByUserID for user [id: {}; email: {}]", () -> user.getId(), () -> user.getEmail());
		String sql = "SELECT dw_project.* FROM dw_user_roles " + "LEFT JOIN dw_project ON dw_user_roles.project_id = dw_project.id "
		    + "LEFT JOIN dw_roles ON dw_user_roles.role_id = dw_roles.id " + "WHERE dw_user_roles.user_id = ? AND dw_user_roles.project_id > 0 "
		    + "GROUP BY dw_project.id";
		List<ProjectDTO> ret = jdbcTemplate.query(sql, new Object[] { user.getId() }, new RowMapper<ProjectDTO>() {
			public ProjectDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
				return setProjectDTO(rs);
			}
		});
		log.debug("Transaction \"findAllByUserID\" terminates with result: [lenght: {}]", () -> ret != null ? ret.size() : "null");
		return ret;
	}

	/**
	 * This function returns a List of all ProjectDTO entities from the table dw_project
	 * 
	 * @return List of ProjectDTO
	 * @throws Exception
	 */
	public List<ProjectDTO> findAll() throws Exception {
		log.trace("Entering findAll");
		String sql = "SELECT * FROM dw_project ORDER BY dw_project.owner_id";
		List<ProjectDTO> ret = jdbcTemplate.query(sql, new Object[] {}, new RowMapper<ProjectDTO>() {
			public ProjectDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
				return setProjectDTO(rs);
			}
		});
		log.debug("Transaction \"findAll\" terminates with result: [lenght: {}]", () -> ret != null ? ret.size() : "null");
		return ret;
	}

	/**
	 * This function returns a List of all matching ProjectDTO entities from the table dw_project, for which the passed user object has the role "PROJECT_ADMIN"
	 * 
	 * @param user
	 *          User
	 * @return List of Projects
	 * @throws Exception
	 */
	public List<ProjectDTO> findAllByAdminRole(final long userID) throws Exception {
		log.trace("Entering findAllByAdminRole for user [id: {}]", () -> userID);
		String sql = "SELECT dw_project.* FROM dw_user_roles LEFT JOIN dw_project ON dw_user_roles.project_id = dw_project.id "
		    + "LEFT JOIN dw_roles ON dw_user_roles.role_id = dw_roles.id WHERE dw_user_roles.user_id = ? "
		    + "AND dw_user_roles.project_id > 0 AND dw_roles.type = \"PROJECT_ADMIN\" GROUP BY dw_project.id";
		List<ProjectDTO> ret = jdbcTemplate.query(sql, new Object[] { userID }, new RowMapper<ProjectDTO>() {
			public ProjectDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
				return setProjectDTO(rs);
			}
		});
		log.debug("Transaction \"findAllByAdminRole\" terminates with result: [lenght: {}]", () -> ret != null ? ret.size() : "null");
		return ret;
	}

	/**
	 * This function returns a ProjectDTO entity from the table dw_project, depending on the passed identifier. If no project was found, null is returned.
	 * 
	 * @param projectId
	 *          Project identifier
	 * @return Project, if found - otherwise null
	 * @throws Exception
	 */
	public ProjectDTO findById(final long projectId) throws Exception {
		log.trace("Entering findById for project [id: {}]", () -> projectId);
		ProjectDTO project = jdbcTemplate.query("SELECT dw_project.* FROM dw_project WHERE  dw_project.id = ?", new Object[] { projectId },
		    new ResultSetExtractor<ProjectDTO>() {
			    @Override
			    public ProjectDTO extractData(ResultSet rs) throws SQLException, DataAccessException {
				    if (rs.next()) {
					    return setProjectDTO(rs);
				    }
				    return null;
			    }
		    });
		log.debug("Transaction \"findById\" terminates with result: [Title: {}]", () -> ((project != null) ? project.getTitle() : "null"));
		return project;
	}

	/**
	 * This function returns a single value from the table dw_project_invite, depending on the passed parameters.
	 * 
	 * @param email
	 *          Mail of the invited user
	 * @param projectId
	 *          Project identifier
	 * @param val
	 *          Value to be searched and returned
	 * @return String with the searched value
	 * @throws Exception
	 */
	public String findValFromInviteData(final String email, final long projectId, final String val) throws Exception {
		log.trace("Entering getValFromInviteData for [email: {}], [pid: {}] and [val: {}]", () -> email, () -> projectId, () -> val);
		final String sql = "SELECT " + val + " from dw_project_invite WHERE user_email = ? AND project_id = ?";
		String ret = jdbcTemplate.queryForObject(sql, new Object[] { email, projectId }, String.class);
		log.debug("Transaction \"findValFromInviteData\" terminates with result: [value:{}; resul: {}]", () -> val, () -> ret);
		return ret;
	}

	/**
	 * This function returns a single value from the table dw_project_invite, depending on the passed parameters.
	 * 
	 * @param email
	 *          Mail of the invited user
	 * @param linkhash
	 *          Invitation linkhash value
	 * @param val
	 *          Value to be searched and returned
	 * @return String with the searched value
	 * @throws Exception
	 */
	public String findValFromInviteData(final String email, final String linkhash, final String val) throws Exception {
		log.trace("Entering getValFromInviteData for [email: {}], [linkhash : {}] and [val: {}]", () -> email, () -> linkhash, () -> val);
		final String sql = "SELECT " + val + " from dw_project_invite WHERE user_email = ? AND linkhash = ?";
		String ret;
		try {
			ret = jdbcTemplate.queryForObject(sql, new Object[] { email, linkhash }, String.class);
		} catch (Exception e) {
			ret = "0";
		}
		log.debug("Transaction \"getValFromInviteData\" terminates with result: [value:{}; resul: {}]", val, ret);
		return ret;
	}

	/**
	 * This function returns a List of all invitations for a project from the table dw_project, depending on the passed project identifier.
	 * 
	 * @param pid
	 *          Project identifier
	 * @return List of Invitations (mails as string)
	 * @throws Exception
	 */
	public List<String> findPendingInvitesByProjectID(final long pid) throws Exception {
		log.trace("Entering findPendingInvitesByProjectID for project [id: {}]", () -> pid);
		final String sql = "SELECT user_email from dw_project_invite WHERE project_id = ?";
		List<String> ret = jdbcTemplate.query(sql, new Object[] { pid }, new RowMapper<String>() {
			public String mapRow(ResultSet rs, int rowNum) throws SQLException {
				return rs.getString("user_email");
			}
		});
		log.debug("Transaction \"findPendingInvitesByProjectID\" terminates with result: [lenght: {}]", () -> ret != null ? ret.size() : "null");
		return ret;
	}

	/**
	 * This function saves a new project entity into the table dw_project.
	 * 
	 * @param project
	 *          Contains all project attributes
	 * @return 1 if changes have happened, otherwise 0
	 * @throws Exception
	 */
	public int insertProject(final ProjectDTO project) throws Exception {
		log.trace("Entering insertProject [title: {}]", () -> project.getTitle());
		KeyHolder holder = new GeneratedKeyHolder();
		final String stmt = "INSERT INTO dw_project (owner_id, created, last_user_id, last_edit, title, project_ident, funding, "
		    + "grant_number, description) VALUES (?,?,?,?,?,?,?,?,?)";
		this.jdbcTemplate.update(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
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
			}
		}, holder);
		final int key = (holder.getKey().intValue() > 0) ? holder.getKey().intValue() : -1;
		log.debug("Transaction \"insertProject\" terminates with result: [key: {}]", () -> key);
		return key;
	}

	/**
	 * This function saves a new project - invitation relation entity into the table dw_project_invite.
	 * 
	 * @param projectId
	 *          Project identifier
	 * @param userMail
	 *          Mail of the invited user
	 * @param adminMail
	 *          Project Administrator Email
	 * @return 1 if changes have happened, otherwise 0
	 */
	public int insertInviteEntity(final long projectId, final String userMail, final String adminMail) {
		log.trace("Entering insertInviteEntity for project [id: {}], User [id: {}] by Admin [email: {}]", () -> projectId, () -> userMail, () -> adminMail);
		int chk = this.jdbcTemplate.update("INSERT INTO dw_project_invite (user_email, invited_by, project_id, linkhash, date) VALUES (?,?,?,?, ?)", userMail,
		    adminMail, projectId, UUID.randomUUID().toString(), LocalDateTime.now().toString());
		log.debug("Transaction \"insertInviteEntity\" terminates with result: [success: {}]", () -> chk);
		return chk;
	}

	/**
	 * This function updates the mail address of a project - invitation relation entity from the table dw_project_invite.
	 * 
	 * @param projectId
	 *          Project identifier
	 * @param userMailOld
	 *          old mail of the invited user
	 * @param userEmailNew
	 *          new mail of the invited user
	 * @return 1 if changes have happened, otherwise 0
	 */
	public int updateInvitationEntity(long projectId, String userMailOld, String userEmailNew) {
		log.trace("Entering updateInvitationEntity for project [id: {}] and User [old email: {}; new email: {}]", () -> projectId, () -> userMailOld,
		    () -> userEmailNew);
		int chk = this.jdbcTemplate.update("UPDATE dw_project_invite SET user_email = ? WHERE user_email = ? AND project_id = ?", userEmailNew, userMailOld,
		    projectId);
		log.debug("Transaction \"updateInvitationEntity\" terminates with result: [result: {}]", () -> chk);
		return chk;
	}

	/**
	 * This function updates a project entity from the table dw_project.
	 * 
	 * @param project
	 *          Contains all project attributes
	 * @return 1 if changes have happened, otherwise 0
	 * @throws Exception
	 */
	public int updateProject(final ProjectDTO project) throws Exception {
		log.trace("Entering updateProject for project [id: {}]", () -> project.getId());
		int chk = this.jdbcTemplate.update(
		    "UPDATE dw_project SET last_user_id = ?, last_edit = ?, title = ?, " + "project_ident = ?, funding = ?, grant_number = ?, description = ? WHERE id = ?",
		    project.getLastUserId(), LocalDateTime.now().toString(), project.getTitle(), project.getProjectIdent(), project.getFunding(), project.getGrantNumber(),
		    project.getDescription(), project.getId());
		log.debug("Transaction \"updateProject\" terminates with result:  [result: {}]", () -> chk);
		return chk;
	}

	/**
	 * This function deletes a project entity from the table dw_project.
	 * 
	 * @param id
	 *          Project identifier
	 * @return 1 if changes have happened, otherwise 0
	 * @throws Exception
	 */
	public int deleteProject(final long id) throws Exception {
		log.trace("Entering deleteProject for project [id: {}]", () -> id);
		int chk = this.jdbcTemplate.update("DELETE FROM dw_project WHERE id = ? ", id);
		log.debug("Transaction \"deleteProject\" terminates with result:  [result: {}]", () -> chk);
		return chk;
	}

	/**
	 * This function deletes a project - invitation relation entity from the table dw_project_invite.
	 * 
	 * @param projectId
	 *          Project identifier
	 * @param userMail
	 *          Mail of the invited user
	 * @return 1 if changes have happened, otherwise 0
	 */
	public int deleteInvitationEntity(final long projectId, final String userMail) {
		log.trace("Entering deleteInvitationEntity for project [id: {}] and User [id: {}]", () -> projectId, () -> userMail);
		int chk = this.jdbcTemplate.update("DELETE FROM dw_project_invite WHERE user_email = ? AND project_id = ?", userMail, projectId);
		log.debug("Transaction \"deleteInvitationEntity\" terminates with result:  [result: {}]", () -> chk);
		return chk;
	}

	/**
	 * This function transfers the values from the ResultSet to a ProjectDTO
	 * 
	 * @param rs
	 *          ResultSet
	 * @return Project object
	 * @throws SQLException
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
