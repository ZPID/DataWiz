package de.zpid.datawiz.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import de.zpid.datawiz.dto.ProjectDTO;
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

  public List<ProjectDTO> getAllByUserID(int userId) throws SQLException {
    if (log.isDebugEnabled())
      log.debug("execute getAllByUserID for user: " + userId);
    String sql = "SELECT dw_user_roles.*, dw_project.*, dw_roles.type FROM dw_user_roles "
        + "LEFT JOIN dw_project ON dw_user_roles.project_id = dw_project.id "
        + "LEFT JOIN dw_roles ON dw_roles.id = dw_user_roles.role_id "
        + "WHERE dw_user_roles.user_id = ? AND dw_user_roles.project_id > ?";
    return jdbcTemplate.query(sql, new Object[] { userId, 0 }, new RowMapper<ProjectDTO>() {
      public ProjectDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
        ProjectDTO project = (ProjectDTO) context.getBean("ProjectDTO");
        UserRoleDTO role = (UserRoleDTO) context.getBean("UserRoleDTO");
        role.setRoleId(rs.getInt("role_id"));
        role.setProjectId(rs.getInt("id"));
        role.setUserId(userId);
        role.setType(rs.getString("type"));
        project.setProjectRole(role);
        project.setId(rs.getInt("id"));
        project.setName(rs.getString("name"));
        project.setDescription(rs.getString("description"));
        project.setCreated(rs.getDate("created"));
        return project;
      }
    });
  }

}
