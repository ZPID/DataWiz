package de.zpid.datawiz.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.List;

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
import de.zpid.datawiz.dto.UserRoleDTO;

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
      log.debug("execute getAllByUserID for user: " + user.getEmail());
    String sql = "SELECT dw_user_roles.*, dw_project.*, dw_roles.type FROM dw_user_roles "
        + "LEFT JOIN dw_project ON dw_user_roles.project_id = dw_project.id "
        + "LEFT JOIN dw_roles ON dw_roles.id = dw_user_roles.role_id "
        + "WHERE dw_user_roles.user_id = ? AND dw_user_roles.project_id > ?";
    return jdbcTemplate.query(sql, new Object[] { user.getId(), 0 }, new RowMapper<ProjectDTO>() {
      public ProjectDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
        ProjectDTO project = (ProjectDTO) context.getBean("ProjectDTO");
        UserRoleDTO role = (UserRoleDTO) context.getBean("UserRoleDTO");
        role.setRoleId(rs.getInt("role_id"));
        role.setProjectId(rs.getInt("id"));
        role.setUserId(user.getId());
        role.setType(rs.getString("type"));
        project.setProjectRole(role);
        project.setId(rs.getInt("id"));
        project.setTitle(rs.getString("name"));
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
   * @param pid
   * @param uid
   * @return
   * @throws SQLException
   * @throws DataAccessException
   */
  public ProjectDTO findByIdWithRole(String pid, String uid) throws Exception {
    if (log.isDebugEnabled())
      log.debug("execute findByIdWithRole for projectID: " + pid + " UserID=" + uid);
    ProjectDTO project = jdbcTemplate.query(
        "SELECT dw_user_roles.*, dw_project.*, dw_roles.type FROM dw_project"
            + " LEFT JOIN dw_user_roles ON dw_project.id = dw_user_roles.project_id"
            + " LEFT JOIN dw_roles ON dw_roles.id = dw_user_roles.role_id"
            + " WHERE  dw_project.id = ? AND  dw_user_roles.user_id = ?",
        new Object[] { pid, uid }, new ResultSetExtractor<ProjectDTO>() {
          @Override
          public ProjectDTO extractData(ResultSet rs) throws SQLException, DataAccessException {
            if (rs.next()) {
              ProjectDTO project = (ProjectDTO) context.getBean("ProjectDTO");
              UserRoleDTO role = (UserRoleDTO) context.getBean("UserRoleDTO");
              role.setRoleId(rs.getInt("role_id"));
              role.setProjectId(rs.getInt("id"));
              role.setUserId(rs.getInt("user_id"));
              role.setType(rs.getString("type"));
              project.setProjectRole(role);
              project.setId(rs.getInt("id"));
              project.setTitle(rs.getString("name"));
              project.setDescription(rs.getString("description"));
              project.setCreated(rs.getTimestamp("created").toLocalDateTime());
              return project;
            }
            return null;
          }
        });
    if (log.isDebugEnabled())
      log.debug("leaving findByID project: " + project);
    return project;
  }

  public int updateProject(ProjectDTO project) throws Exception {
    if (log.isDebugEnabled())
      log.debug("execute updateProject: " + project);
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
}
