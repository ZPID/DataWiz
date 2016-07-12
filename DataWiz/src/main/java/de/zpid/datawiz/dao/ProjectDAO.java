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
import org.springframework.context.annotation.Scope;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import de.zpid.datawiz.dto.ProjectDTO;
import de.zpid.datawiz.dto.UserDTO;

@Repository
@Scope("singleton")
public class ProjectDAO extends SuperDAO {

  private static Logger log = LogManager.getLogger(ProjectDAO.class);

  public ProjectDAO() {
    super();
    log.info("Loading ProjectDAO as Singleton and Service");
  }

  public List<ProjectDTO> findAllByUserID(final UserDTO user) throws Exception {
    log.trace("execute findAllByUserID for user [email: {}]", () -> user.getEmail());
    String sql = "SELECT dw_user_roles.*, dw_project.*, dw_roles.type FROM dw_user_roles "
        + "LEFT JOIN dw_project ON dw_user_roles.project_id = dw_project.id "
        + "LEFT JOIN dw_roles ON dw_roles.id = dw_user_roles.role_id "
        + "WHERE dw_user_roles.user_id = ? AND dw_user_roles.project_id > 0 GROUP BY dw_project.id";
    List<ProjectDTO> ret = jdbcTemplate.query(sql, new Object[] { user.getId() }, new RowMapper<ProjectDTO>() {
      public ProjectDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
        ProjectDTO project = (ProjectDTO) applicationContext.getBean("ProjectDTO");
        project.setId(rs.getInt("id"));
        project.setTitle(rs.getString("name"));
        project.setOwnerId(rs.getLong("owner_id"));
        project.setDescription(rs.getString("description"));
        project.setCreated(rs.getTimestamp("created").toLocalDateTime());
        return project;
      }
    });
    log.debug("Transaction for findAllByUserID returned [lenght: {}]", () -> ret != null ? ret.size() : "null");
    return ret;
  }

  /**
   * Returns the project and the UserRole in one turn. For that, UserID and ProjectID is important, because projects and
   * users have an mxn relationship!
   * 
   * @param projectId
   * @param userId
   * @return
   * @throws SQLException
   * @throws DataAccessException
   */
  public ProjectDTO findById(final long projectId) throws Exception {
    log.trace("execute findById for project [id: " + projectId + "]");
    ProjectDTO project = jdbcTemplate.query(
        // "SELECT dw_user_roles.*, dw_project.*, dw_roles.type FROM dw_project"
        // + " LEFT JOIN dw_user_roles ON dw_project.id = dw_user_roles.project_id"
        // + " LEFT JOIN dw_roles ON dw_roles.id = dw_user_roles.role_id"
        // + " WHERE dw_project.id = ? AND dw_user_roles.user_id = ?",
        "SELECT dw_project.* FROM dw_project WHERE  dw_project.id = ?", new Object[] { projectId },
        new ResultSetExtractor<ProjectDTO>() {
          @Override
          public ProjectDTO extractData(ResultSet rs) throws SQLException, DataAccessException {
            if (rs.next()) {
              ProjectDTO project = (ProjectDTO) applicationContext.getBean("ProjectDTO");
              project.setId(rs.getInt("id"));
              project.setTitle(rs.getString("name"));
              project.setOwnerId(rs.getLong("owner_id"));
              project.setDescription(rs.getString("description"));
              project.setCreated(rs.getTimestamp("created").toLocalDateTime());
              return project;
            }
            return null;
          }
        });
    log.debug("Transaction for findById returned [id: {}]", () -> ((project != null) ? project.getTitle() : "null"));
    return project;
  }

  public int updateProject(final ProjectDTO project) throws Exception {
    log.trace("execute updateProject for project [id: {}]", () -> project.getId());
    int chk = this.jdbcTemplate.update("UPDATE dw_project SET name = ?, description = ? WHERE id = ?",
        project.getTitle(), project.getDescription(), project.getId());
    log.debug("Transaction for updateProject returned [success: {}]", () -> chk);
    return chk;
  }

  public int insertProject(final ProjectDTO project) throws Exception {
    log.trace("execute insertProject [title: {}]", () -> project.getTitle());
    KeyHolder holder = new GeneratedKeyHolder();
    final String stmt = "INSERT INTO dw_project (name, description, created, owner_id) VALUES (?,?,?,?)";
    this.jdbcTemplate.update(new PreparedStatementCreator() {
      @Override
      public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(stmt, Statement.RETURN_GENERATED_KEYS);
        ps.setString(1, project.getTitle());
        ps.setString(2, project.getDescription());
        ps.setString(3, LocalDateTime.now().toString());
        ps.setLong(4, project.getOwnerId());
        return ps;
      }
    }, holder);
    final int key = (holder.getKey().intValue() > 0) ? holder.getKey().intValue() : -1;
    log.debug("Transaction for insertProject returned [key: {}]", () -> key);
    return key;
  }

  public int insertInviteEntree(final long projectId, final String userMail, final String adminMail) {
    log.trace("execute instertUsertoProject for project [id: {}], User [id: {}] by Admin [email: {}]", () -> projectId,
        () -> userMail, () -> adminMail);
    int chk = this.jdbcTemplate.update(
        "INSERT INTO dw_project_invite (user_email, invited_by, project_id, linkhash, date) VALUES (?,?,?,?, ?)",
        userMail, adminMail, projectId, UUID.randomUUID().toString(), LocalDateTime.now().toString());
    log.debug("Transaction for insertInviteEntree returned [success: {}]", () -> chk);
    return chk;
  }

  public String findValFromInviteData(final String email, final long projectId, final String val) throws Exception {
    log.trace("execute getValFromInviteData for project [id: {}], user [id: {}] and [val: {}]", () -> projectId,
        () -> email, () -> val);
    final String sql = "SELECT " + val + " from dw_project_invite WHERE user_email = ? AND project_id = ?";
    String ret = jdbcTemplate.queryForObject(sql, new Object[] { email, projectId }, String.class);
    log.debug("Transaction for getValFromInviteData returned [{}: {}]", () -> val, () -> ret);
    return ret;
  }

  public String findValFromInviteData(final String email, final String linkhash, final String val) throws Exception {
    log.trace("execute getValFromInviteData for project [id: {}], [linkhash : {}] and [val: {}]", () -> linkhash,
        () -> email, () -> val);
    final String sql = "SELECT " + val + " from dw_project_invite WHERE user_email = ? AND linkhash = ?";
    String ret = jdbcTemplate.queryForObject(sql, new Object[] { email, linkhash }, String.class);
    log.debug("Transaction for getValFromInviteData returned [{}: {}]", () -> val, () -> ret);
    return ret;
  }

  public List<String> findPendingInvitesByProjectID(final long pid) throws Exception {
    log.trace("execute findPendingInvitesByProjectID for project [id: {}]", () -> pid);
    final String sql = "SELECT user_email from dw_project_invite WHERE project_id = ?";
    List<String> ret = jdbcTemplate.query(sql, new Object[] { pid }, new RowMapper<String>() {
      public String mapRow(ResultSet rs, int rowNum) throws SQLException {
        return rs.getString("user_email");
      }
    });
    log.debug("Transaction for findPendingInvitesByProjectID returned [lenght: {}]",
        () -> ret != null ? ret.size() : "null");
    return ret;
  }

  public int deleteInvitationEntree(final long projectId, final String userMail) {
    log.trace("execute deleteInvitationEntree for project [id: {}] and User [id: {}]", () -> projectId, () -> userMail);
    int chk = this.jdbcTemplate.update("DELETE FROM dw_project_invite WHERE user_email = ? AND project_id = ?",
        userMail, projectId);
    log.debug("Transaction for deleteInvitationEntree returned [success: {}]", () -> chk);
    return chk;
  }

  public int updateInvitationEntree(long projectId, String userMailOld, String userEmailNew) {
    log.trace("execute updateInvitationEntree for project [id: {}] and User [old email: {}; new email: {}]",
        () -> projectId, () -> userMailOld, () -> userEmailNew);
    int chk = this.jdbcTemplate.update(
        "UPDATE dw_project_invite SET user_email = ? WHERE user_email = ? AND project_id = ?", userEmailNew,
        userMailOld, projectId);
    log.debug("Transaction for updateInvitationEntree returned [success: {}]", () -> chk);
    return chk;
  }
}
