package de.zpid.datawiz.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.context.annotation.Scope;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;

import de.zpid.datawiz.dto.ProjectDTO;
import de.zpid.datawiz.dto.UserDTO;

@Service
@Scope("singleton")
public class ProjectDAO extends SuperDAO {

  public ProjectDAO() {
    super();
    log.info("Loading ProjectDAO as Singleton and Service");
  }

  public List<ProjectDTO> findAllByUserID(UserDTO user) throws Exception {
    if (log.isDebugEnabled())
      log.debug("execute findAllByUserID for user [email: " + user.getEmail() + "]");
    String sql = "SELECT dw_user_roles.*, dw_project.*, dw_roles.type FROM dw_user_roles "
        + "LEFT JOIN dw_project ON dw_user_roles.project_id = dw_project.id "
        + "LEFT JOIN dw_roles ON dw_roles.id = dw_user_roles.role_id "
        + "WHERE dw_user_roles.user_id = ? AND dw_user_roles.project_id > 0 GROUP BY dw_project.id";
    return jdbcTemplate.query(sql, new Object[] { user.getId() }, new RowMapper<ProjectDTO>() {
      public ProjectDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
        ProjectDTO project = (ProjectDTO) context.getBean("ProjectDTO");
        project.setId(rs.getInt("id"));
        project.setTitle(rs.getString("name"));
        project.setOwnerId(rs.getLong("owner_id"));
        project.setDescription(rs.getString("description"));
        project.setCreated(rs.getTimestamp("created").toLocalDateTime());
        return project;
      }
    });
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
  public ProjectDTO findById(long projectId) throws Exception {
    if (log.isDebugEnabled())
      log.debug("execute findByIdWithRole for project [id: " + projectId + "]");
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
              ProjectDTO project = (ProjectDTO) context.getBean("ProjectDTO");
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
    if (log.isDebugEnabled())
      log.debug("leaving findByID project: " + ((project != null) ? project.getId() : "null"));
    return project;
  }

  public int updateProject(ProjectDTO project) throws Exception {
    if (log.isDebugEnabled())
      log.debug("execute updateProject for project [id: " + project.getId() + "]");
    return this.jdbcTemplate.update("UPDATE dw_project SET name = ?, description = ? WHERE id = ?", project.getTitle(),
        project.getDescription(), project.getId());
  }

  public int insertProject(ProjectDTO project) throws Exception {
    if (log.isDebugEnabled())
      log.debug("execute saveProject: " + project);
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
    return (holder.getKey().intValue() > 0) ? holder.getKey().intValue() : -1;
  }

  public int insertInviteEntree(long projectId, String userMail, String adminMail) {
    if (log.isDebugEnabled())
      log.debug("execute instertUsertoProject for project [id: " + projectId + "], User [id: " + userMail
          + "] by Admin [email: " + adminMail + "]");
    return this.jdbcTemplate.update(
        "INSERT INTO dw_project_invite (user_email, invited_by, project_id, linkhash, date) VALUES (?,?,?,?, ?)",
        userMail, adminMail, projectId, UUID.randomUUID().toString(), LocalDateTime.now().toString());
  }

  public String getValFromInviteData(String email, long projectId, String val) throws Exception {
    if (log.isDebugEnabled())
      log.debug("execute getInviteHash for user [email: " + email + "]");
    String sql = "SELECT " + val + " from dw_project_invite WHERE user_email = ? AND project_id = ?";
    return jdbcTemplate.query(sql, new Object[] { email, projectId }, new ResultSetExtractor<String>() {
      @Override
      public String extractData(ResultSet rs) throws SQLException, DataAccessException {
        if (rs.next()) {
          return rs.getString(val);
        }
        return null;
      }
    });
  }

  public List<String> findPendingInvitesByProjectID(long pid) throws Exception {
    if (log.isDebugEnabled())
      log.debug("execute findPendingInvitesByProjectID for project [id: " + pid + "]");
    String sql = "SELECT user_email from dw_project_invite WHERE project_id = ?";
    return jdbcTemplate.query(sql, new Object[] { pid }, new RowMapper<String>() {
      public String mapRow(ResultSet rs, int rowNum) throws SQLException {
        return rs.getString("user_email");
      }
    });
  }

  public int deleteInvitationEntree(long projectId, String userMail) {
    if (log.isDebugEnabled())
      log.debug("execute deleteInvitationEntree for project [id: " + projectId + "] and User [id: " + userMail + "]");
    return this.jdbcTemplate.update("DELETE FROM dw_project_invite WHERE user_email = ? AND project_id = ?", userMail,
        projectId);
  }

  public int updateInvitationEntree(long projectId, String userMailOld, String userEmailNew) {
    if (log.isDebugEnabled())
      log.debug("execute updateInvitationEntree for project [id: " + projectId + "] and User [old email: " + userMailOld
          + "new email: " + userEmailNew + "]");
    return this.jdbcTemplate.update(
        "UPDATE dw_project_invite SET user_email = ? WHERE user_email = ? AND project_id = ?", userEmailNew,
        userMailOld, projectId);
  }
}
