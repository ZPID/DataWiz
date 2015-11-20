package de.zpid.datawiz.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import de.zpid.datawiz.dto.VersionControlDTO;

public class VersionControlDAO {

  private static final Logger log = Logger.getLogger(VersionControlDAO.class);
  private ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("spring.xml");
  private JdbcTemplate jdbcTemplate;

  public VersionControlDAO() {
    super();
  }

  public VersionControlDAO(DataSource dataSource) {
    super();
    this.jdbcTemplate = new JdbcTemplate(dataSource);
  }

  public List<VersionControlDTO> getAllByProjectID(int projectId) throws SQLException {
    if (log.isDebugEnabled())
      log.debug("execute getAllByUserID for project: " + projectId);
    String sql = "SELECT * FROM dw_version_control WHERE project_id  = ?";
    return jdbcTemplate.query(sql, new Object[] { projectId }, new RowMapper<VersionControlDTO>() {
      public VersionControlDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
        VersionControlDTO version = (VersionControlDTO) context.getBean("VersionControlDTO");
        version.setId(rs.getInt("id"));
        version.setProjectId(rs.getInt("project_id"));
        version.setUserId(rs.getInt("user_id"));
        version.setDescription(rs.getString("description"));
        version.setTimestamp(rs.getDate("timestamp"));
        return version;
      }
    });
  }

  public List<VersionControlDTO> getAllByUserID(int userId) throws SQLException {
    if (log.isDebugEnabled())
      log.debug("execute getAllByUserID for user: " + userId);
    String sql = "SELECT * FROM dw_version_control WHERE user_id  = ?";
    return jdbcTemplate.query(sql, new Object[] { userId }, new RowMapper<VersionControlDTO>() {
      public VersionControlDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
        VersionControlDTO version = (VersionControlDTO) context.getBean("VersionControlDTO");
        version.setId(rs.getInt("id"));
        version.setProjectId(rs.getInt("project_id"));
        version.setUserId(rs.getInt("user_id"));
        version.setDescription(rs.getString("description"));
        version.setTimestamp(rs.getDate("timestamp"));
        return version;
      }
    });
  }

}
