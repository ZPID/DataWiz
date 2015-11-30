package de.zpid.datawiz.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;

import de.zpid.datawiz.dto.UserDTO;
import de.zpid.datawiz.dto.UserRoleDTO;
import de.zpid.datawiz.util.AccountState;

public class UserDAO {

  private static final Logger log = Logger.getLogger(UserDAO.class);
  private ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("spring.xml");
  private JdbcTemplate jdbcTemplate;

  public UserDAO() {
  }

  public UserDAO(DataSource dataSource) {
    this.jdbcTemplate = new JdbcTemplate(dataSource);
  }

  public UserDTO findById(int id) {
    return new UserDTO();
  }

  public UserDTO findByMail(String email, boolean pwd) throws SQLException, DataAccessException {
    if (log.isDebugEnabled())
      log.debug("execute findByMail for email: " + email);
    UserDTO user = this.jdbcTemplate.query("SELECT * FROM dw_user WHERE email= ?", new Object[] { email },
        new ResultSetExtractor<UserDTO>() {
          @Override
          public UserDTO extractData(ResultSet rs) throws SQLException, DataAccessException {
            if (rs.next()) {
              UserDTO contact = (UserDTO) context.getBean("UserDTO");
              contact.setId(rs.getInt("id"));
              contact.setFirstName(rs.getString("first_name"));
              contact.setLastName(rs.getString("last_name"));
              if (pwd)
                contact.setPassword(rs.getString("password"));
              contact.setEmail(rs.getString("email"));
              contact.setAccountState(rs.getString("account_state"));
              contact.setActivationCode(rs.getString("activationcode"));
              return contact;
            }
            return null;
          }
        });
    if (user != null && user.getId() > 0) {
      user.setGlobalRoles(getRolesByUserID(user.getId()));
    }
    if (log.isDebugEnabled())
      log.debug("leaving findByMail user: " + user);
    return user;
  }

  public List<UserRoleDTO> getRolesByUserID(int id) throws SQLException {
    if (log.isDebugEnabled())
      log.debug("execute getRolesByUserID for userid: " + id);
    String sql = "SELECT * FROM dw_user_roles " + " JOIN dw_roles ON dw_user_roles.role_id = dw_roles.id "
        + "WHERE user_id  = ?";
    return this.jdbcTemplate.query(sql, new Object[] { id }, new RowMapper<UserRoleDTO>() {
      public UserRoleDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
        UserRoleDTO role = (UserRoleDTO) context.getBean("UserRoleDTO");
        role.setRoleId(rs.getInt("id"));
        role.setProjectId(rs.getInt("project_id"));
        role.setUserId(rs.getInt("user_id"));
        role.setType(rs.getString("type"));
        return role;
      }
    });
  }

  public void saveOrUpdate(UserDTO user, boolean changePWD) {
    if (log.isDebugEnabled())
      log.debug("execute saveOrUpdate user: " + user);
    if (user.getId() > 0) {
      if (changePWD) {
        this.jdbcTemplate.update(
            "UPDATE dw_user SET first_name = ?, last_name = ?, password = ?, email = ?, account_state = ? WHERE id = ?",
            user.getFirstName(), user.getLastName(), user.getPassword(), user.getEmail(), user.getAccountState(),
            user.getId());
      } else {
        this.jdbcTemplate.update(
            "UPDATE dw_user SET first_name = ?, last_name = ?, email = ?, account_state = ? WHERE id = ?",
            user.getFirstName(), user.getLastName(), user.getEmail(), user.getAccountState(), user.getId());
      }
    } else {
      this.jdbcTemplate.update(
          "INSERT INTO dw_user  (first_name, last_name, password, email, account_state, activationcode) VALUES (?,?,?,?,?,?)",
          user.getFirstName(), user.getLastName(), user.getPassword(), user.getEmail(), AccountState.LOCKED.name(),
          UUID.randomUUID().toString());
    }
  }

  public void activateUserAccount(UserDTO user) {
    if (log.isDebugEnabled())
      log.debug("execute activateUserAccount user: " + user);
    this.jdbcTemplate.update("UPDATE dw_user SET account_state = ?, activationcode = ?  WHERE id = ?",
        AccountState.ACTIVE.name(), "", user.getId());
  }

  public void setRole(int userid, int projectid, int roleid) {
    if (log.isDebugEnabled())
      log.debug("execute activateUserAccount userid: " + userid);
    this.jdbcTemplate.update("INSERT INTO dw_user_roles (user_id, role_id, project_id) VALUES (?,?,?)", userid, roleid,
        (projectid > 0) ? projectid : null);
  }
}
