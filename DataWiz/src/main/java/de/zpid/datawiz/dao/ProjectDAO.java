package de.zpid.datawiz.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import de.zpid.datawiz.dto.ProjectDTO;
import de.zpid.datawiz.dto.UserDTO;

public class ProjectDAO {

  private static final Logger log = Logger.getLogger(ProjectDAO.class);
  private ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("spring.xml");
  private JdbcTemplate jdbcTemplate;

  public ProjectDAO() {
    super();
  }

  public ProjectDAO(DataSource dataSource) {
    super();
    this.jdbcTemplate = new JdbcTemplate(dataSource);
  }

  public List<ProjectDTO> getAllByUserID(UserDTO user) throws Exception {
    if (log.isDebugEnabled())
      log.debug("execute getAllByUserID for user [email: " + user.getEmail() + "]");
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
      log.debug("leaving findByID project: " + project.getId());
    return project;
  }

  public int updateProject(ProjectDTO project) throws Exception {
    if (log.isDebugEnabled())
      log.debug("execute updateProject for project [id: " + project.getId() + "]");
    return this.jdbcTemplate.update("UPDATE dw_project SET name = ?, description = ? WHERE id = ?", project.getTitle(),
        project.getDescription(), project.getId());
  }

  public int saveProject(ProjectDTO project) throws Exception {
    if (log.isDebugEnabled())
      log.debug("execute saveProject: " + project);
    KeyHolder holder = new GeneratedKeyHolder();
    final String stmt = "INSERT INTO dw_project (name, description, created) VALUES (?,?,?)";
    this.jdbcTemplate.update(new PreparedStatementCreator() {
      @Override
      public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(stmt, Statement.RETURN_GENERATED_KEYS);
        ps.setString(1, project.getTitle());
        ps.setString(2, project.getDescription());
        ps.setString(3, LocalDateTime.now().toString());
        return ps;
      }
    }, holder);
    return (holder.getKey().intValue() > 0) ? holder.getKey().intValue() : -1;
  }

  public int addUsertoProject(long projectId, String userMail) {
    if (log.isDebugEnabled())
      log.debug("execute addUsertoProject for project [id: " + projectId + "] and User [id: " + userMail + "]");
    return this.jdbcTemplate.update(
        "INSERT INTO dw_project_invite (user_email, project_id, linkhash, date) VALUES (?,?,?,?)", userMail, projectId,
        UUID.randomUUID().toString(), LocalDateTime.now().toString());
  }

  public String getInviteHash(String email, long projectId) throws Exception {
    if (log.isDebugEnabled())
      log.debug("execute getInviteHash for user [email: " + email + "]");
    String sql = "SELECT linkhash from dw_project_invite WHERE user_email = ? AND project_id = ?";
    return jdbcTemplate.query(sql, new Object[] { email, projectId }, new ResultSetExtractor<String>() {
      @Override
      public String extractData(ResultSet rs) throws SQLException, DataAccessException {
        if (rs.next()) {
          return rs.getString("linkhash");
        }
        return null;
      }
    });
  }

  public int deleteInvitationEntree(long projectId, String userMail) {
    if (log.isDebugEnabled())
      log.debug("execute deleteInvitationEntree for project [id: " + projectId + "] and User [id: " + userMail + "]");
    return this.jdbcTemplate.update("DELETE FROM dw_project_invite WHERE user_email = ? AND project_id = ?", userMail,
        projectId);
  }
}
