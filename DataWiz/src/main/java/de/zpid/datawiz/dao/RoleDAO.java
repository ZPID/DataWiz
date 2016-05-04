package de.zpid.datawiz.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import de.zpid.datawiz.dto.UserRoleDTO;

@Service
@Scope("singleton")
public class RoleDAO extends SuperDAO {

  public RoleDAO() {
    super();
    if (log.isInfoEnabled())
      log.info("Loading RoleDAO as Singleton and Service");
  }

  public int setRole(final UserRoleDTO role) throws SQLException {
    log.trace("Entering setRole role: {}", () -> role);
    final int ret = this.jdbcTemplate.update(
        "INSERT INTO dw_user_roles (user_id, role_id, project_id, study_id) VALUES (?,?,?,?)", role.getUserId(),
        role.getRoleId(), (role.getProjectId() > 0) ? role.getProjectId() : null,
        (role.getStudyId() > 0) ? role.getStudyId() : null);
    log.debug("Transaction for setRole returned [{}]", () -> ret);
    return ret;
  }

  public int deleteRole(final UserRoleDTO role) throws SQLException {
    log.trace("Entering deleteRole role: {}", () -> role);
    List<Object> oList = new LinkedList<Object>();
    oList.add(role.getRoleId());
    oList.add(role.getUserId());
    if (role.getProjectId() > 0)
      oList.add(role.getProjectId());
    if (role.getStudyId() > 0)
      oList.add(role.getStudyId());
    final int ret = this.jdbcTemplate.update(
        "DELETE FROM dw_user_roles WHERE dw_user_roles.role_id = ? AND dw_user_roles.user_id = ? "
            + ((role.getProjectId() > 0) ? " AND dw_user_roles.project_id = ? "
                : " AND dw_user_roles.project_id IS NULL")
            + ((role.getStudyId() > 0) ? " AND dw_user_roles.study_id = ?" : " AND dw_user_roles.study_id IS NULL"),
        oList.toArray());
    log.debug("Transaction for deleteRole returned [{}]", () -> ret);
    return ret;
  }

  public List<UserRoleDTO> findRolesByUserID(final long id) throws SQLException {
    log.trace("Entering findRolesByUserID id: {}", () -> id);
    String sql = "SELECT * FROM dw_user_roles " + " JOIN dw_roles ON dw_user_roles.role_id = dw_roles.id "
        + "WHERE user_id  = ?";
    final List<UserRoleDTO> ret = this.jdbcTemplate.query(sql, new Object[] { id }, new RowMapper<UserRoleDTO>() {
      public UserRoleDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
        return setRole(rs);
      }
    });
    log.debug("Transaction for findRolesByUserID returned [lenght: {}]", () -> ret.size());
    return ret;
  }

  public List<String> findAllProjectRoles() throws SQLException {
    log.trace("Entering findAllProjectRoles");
    String sql = "SELECT type FROM dw_roles WHERE type != 'USER' AND type != 'ADMIN' AND type != 'REL_ROLE'";
    final List<String> ret = this.jdbcTemplate.query(sql, new Object[] {}, new RowMapper<String>() {
      public String mapRow(ResultSet rs, int rowNum) throws SQLException {
        return rs.getString("type");
      }
    });
    log.debug("Transaction for findAllProjectRoles returned [lenght: {}]", () -> ret.size());
    return ret;
  }

  public List<UserRoleDTO> findRolesByUserIDAndProjectID(final long uid, final long pid) throws SQLException {
    log.trace("execute findRolesByUserIDAndProjectID for [userid: {} projectid: {}]", () -> uid, () -> pid);
    String sql = "SELECT * FROM dw_user_roles " + " JOIN dw_roles ON dw_user_roles.role_id = dw_roles.id "
        + "WHERE user_id  = ? AND project_id = ?";
    final List<UserRoleDTO> ret = this.jdbcTemplate.query(sql, new Object[] { uid, pid }, new RowMapper<UserRoleDTO>() {
      public UserRoleDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
        return setRole(rs);
      }
    });
    log.debug("Transaction for findRolesByUserIDAndProjectID returned [lenght: {}]", () -> ret.size());
    return ret;
  }

  private UserRoleDTO setRole(final ResultSet rs) throws SQLException {
    final UserRoleDTO role = (UserRoleDTO) context.getBean("UserRoleDTO");
    role.setRoleId(rs.getLong("id"));
    role.setStudyId(rs.getLong("study_id"));
    role.setProjectId(rs.getLong("project_id"));
    role.setUserId(rs.getLong("user_id"));
    role.setType(rs.getString("type"));
    return role;
  }
}
