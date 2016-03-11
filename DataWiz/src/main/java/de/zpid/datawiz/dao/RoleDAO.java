package de.zpid.datawiz.dao;

import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.SQLException;
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

  public void setRole(int userid, int projectid, int roleid) {
    if (log.isDebugEnabled())
      log.debug("execute activateUserAccount userid: " + userid);
    this.jdbcTemplate.update("INSERT INTO dw_user_roles (user_id, role_id, project_id) VALUES (?,?,?)", userid, roleid,
        (projectid > 0) ? projectid : null);
  }

  public List<UserRoleDTO> getRolesByUserID(int id) throws Exception {
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

  public List<UserRoleDTO> getRolesByUserIDAndProjectID(int uid, int pid) throws Exception {
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
    role.setRoleId(new BigInteger(rs.getBigDecimal("id").toString()));
    role.setStudyId(
        rs.getBigDecimal("study_id") != null ? new BigInteger(rs.getBigDecimal("study_id").toString()) : null);
    role.setProjectId(
        rs.getBigDecimal("project_id") != null ? new BigInteger(rs.getBigDecimal("project_id").toString()) : null);
    role.setUserId(new BigInteger(rs.getBigDecimal("user_id").toString()));
    role.setType(rs.getString("type"));
    return role;
  }

}
