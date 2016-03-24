package de.zpid.datawiz.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import de.zpid.datawiz.dto.UserRoleDTO;

public class RoleDAO {

  private static final Logger log = Logger.getLogger(RoleDAO.class);
  private ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("spring.xml");
  private JdbcTemplate jdbcTemplate;

  public RoleDAO() {
    super();
  }

  public RoleDAO(DataSource dataSource) {
    super();
    this.jdbcTemplate = new JdbcTemplate(dataSource);
  }

  public int setRole(UserRoleDTO role) throws Exception {
    if (log.isDebugEnabled())
      log.debug("execute setRole role: " + role);
    return this.jdbcTemplate.update(
        "INSERT INTO dw_user_roles (user_id, role_id, project_id, study_id) VALUES (?,?,?,?)", role.getUserId(),
        role.getRoleId(), (role.getProjectId() > 0) ? role.getProjectId() : null,
        (role.getStudyId() > 0) ? role.getStudyId() : null);
  }

  public int deleteRole(UserRoleDTO role) throws Exception {
    List<Object> oList = new LinkedList<Object>();
    oList.add(role.getRoleId());
    oList.add(role.getUserId());
    if (role.getProjectId() > 0)
      oList.add(role.getProjectId());
    if (role.getStudyId() > 0)
      oList.add(role.getStudyId());
    if (log.isDebugEnabled())
      log.debug("execute deleteRole userid: " + role);
    return this.jdbcTemplate
        .update(
            "DELETE FROM dw_user_roles WHERE dw_user_roles.role_id = ? AND dw_user_roles.user_id = ? "
                + ((role.getProjectId() > 0) ? " AND dw_user_roles.project_id = ? "
                    : " AND dw_user_roles.project_id IS NULL")
                + ((role.getStudyId() > 0) ? " AND dw_user_roles.study_id = ?" : " AND dw_user_roles.study_id IS NULL"),
            oList.toArray());
  }

  public List<UserRoleDTO> findRolesByUserID(long id) throws Exception {
    if (log.isDebugEnabled())
      log.debug("execute getRolesByUserID for userid: " + id);
    String sql = "SELECT * FROM dw_user_roles " + " JOIN dw_roles ON dw_user_roles.role_id = dw_roles.id "
        + "WHERE user_id  = ?";
    return this.jdbcTemplate.query(sql, new Object[] { id }, new RowMapper<UserRoleDTO>() {
      public UserRoleDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
        return setRole(rs);
      }
    });
  }

  public List<String> findAllProjectRoles() throws Exception {
    if (log.isDebugEnabled())
      log.debug("execute getAllProjectRoles");
    String sql = "SELECT type FROM dw_roles WHERE type != 'USER' AND type != 'ADMIN' AND type != 'REL_ROLE'";
    return this.jdbcTemplate.query(sql, new Object[] {}, new RowMapper<String>() {
      public String mapRow(ResultSet rs, int rowNum) throws SQLException {
        return rs.getString("type");
      }
    });
  }

  public List<UserRoleDTO> findRolesByUserIDAndProjectID(long uid, long pid) throws Exception {
    if (log.isDebugEnabled())
      log.debug("execute getRolesByUserID for [userid: " + uid + " projectid: " + pid + "]");
    String sql = "SELECT * FROM dw_user_roles " + " JOIN dw_roles ON dw_user_roles.role_id = dw_roles.id "
        + "WHERE user_id  = ? AND project_id = ?";
    return this.jdbcTemplate.query(sql, new Object[] { uid, pid }, new RowMapper<UserRoleDTO>() {
      public UserRoleDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
        return setRole(rs);
      }
    });
  }

  private UserRoleDTO setRole(ResultSet rs) throws SQLException {
    UserRoleDTO role = (UserRoleDTO) context.getBean("UserRoleDTO");
    role.setRoleId(rs.getLong("id"));
    role.setStudyId(rs.getLong("study_id"));
    role.setProjectId(rs.getLong("project_id"));
    role.setUserId(rs.getLong("user_id"));
    role.setType(rs.getString("type"));
    return role;
  }

}
