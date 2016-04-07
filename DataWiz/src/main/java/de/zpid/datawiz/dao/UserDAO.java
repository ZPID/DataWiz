package de.zpid.datawiz.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import de.zpid.datawiz.dto.ProjectDTO;
import de.zpid.datawiz.dto.UserDTO;
import de.zpid.datawiz.enumeration.AccountState;

@Service
@Scope("singleton")
public class UserDAO extends SuperDAO {

  public UserDAO() {
    super();
    if (log.isInfoEnabled())
      log.info("Loading UserDAO as Singleton and Service");
  }

  @Autowired
  private RoleDAO roleDAO;

  public UserDTO findById(long l) throws Exception {
    if (log.isDebugEnabled())
      log.debug("execute findById [id: " + l + "]");
    UserDTO user = this.jdbcTemplate.query("SELECT * FROM dw_user WHERE id= ?", new Object[] { l },
        new ResultSetExtractor<UserDTO>() {
          @Override
          public UserDTO extractData(ResultSet rs) throws SQLException, DataAccessException {
            if (rs.next()) {
              UserDTO contact = (UserDTO) context.getBean("UserDTO");
              contact.setId(rs.getInt("id"));
              contact.setFirstName(rs.getString("first_name"));
              contact.setLastName(rs.getString("last_name"));
              contact.setEmail(rs.getString("email"));
              contact.setAccountState(rs.getString("account_state"));
              contact.setActivationCode(rs.getString("activationcode"));
              return contact;
            }
            return null;
          }
        });
    if (user != null && user.getId() > 0) {
      user.setGlobalRoles(roleDAO.findRolesByUserID(user.getId()));
    }
    if (log.isDebugEnabled())
      log.debug("leaving findById user: " + user);
    return user;
  }

  public UserDTO findByMail(String email, boolean pwd) throws Exception {
    if (log.isDebugEnabled())
      log.debug("execute findByMail [email: " + email + "]");
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
      user.setGlobalRoles(roleDAO.findRolesByUserID(user.getId()));
    }
    if (log.isDebugEnabled()) {
      if (user != null)
        log.debug("leaving findByMail user [id: " + user.getId() + " email: " + user.getEmail() + "]");
      else
        log.debug("leaving findByMail - user not found - return NULL");
    }
    return user;
  }

  public List<UserDTO> findGroupedByProject(ProjectDTO project) throws Exception {
    if (log.isDebugEnabled())
      log.debug(
          "execute findGroupedByProject for project [id: " + project.getId() + " name: " + project.getTitle() + "]");
    String sql = "SELECT dw_user.* FROM dw_user " + "LEFT JOIN dw_user_roles ON dw_user.id = dw_user_roles.user_id "
        + "LEFT JOIN dw_roles ON dw_roles.id = dw_user_roles.role_id "
        + "WHERE dw_user_roles.project_id = ? GROUP BY dw_user_roles.user_id";
    return this.jdbcTemplate.query(sql, new Object[] { project.getId() }, new RowMapper<UserDTO>() {
      public UserDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
        UserDTO contact = (UserDTO) context.getBean("UserDTO");
        contact.setId(rs.getInt("id"));
        contact.setTitle(rs.getString("title"));
        contact.setFirstName(rs.getString("first_name"));
        contact.setLastName(rs.getString("last_name"));
        contact.setEmail(rs.getString("email"));
        contact.setAccountState(rs.getString("account_state"));
        contact.setActivationCode(rs.getString("activationcode"));
        return contact;
      }
    });
  }

  public int saveOrUpdate(UserDTO user, boolean changePWD) {
    if (log.isDebugEnabled())
      log.debug("execute saveOrUpdate user [id: " + user.getId() + "]");
    if (user.getId() > 0) {
      if (changePWD) {
        return this.jdbcTemplate.update(
            "UPDATE dw_user SET first_name = ?, last_name = ?, password = ?, email = ?, account_state = ? WHERE id = ?",
            user.getFirstName(), user.getLastName(), user.getPassword(), user.getEmail(), user.getAccountState(),
            user.getId());
      } else {
        return this.jdbcTemplate.update(
            "UPDATE dw_user SET first_name = ?, last_name = ?, email = ?, account_state = ? WHERE id = ?",
            user.getFirstName(), user.getLastName(), user.getEmail(), user.getAccountState(), user.getId());
      }
    } else {
      return this.jdbcTemplate.update(
          "INSERT INTO dw_user  (first_name, last_name, password, email, account_state, activationcode) VALUES (?,?,?,?,?,?)",
          user.getFirstName(), user.getLastName(), user.getPassword(), user.getEmail(), AccountState.LOCKED.name(),
          (user.getActivationCode() != null && !user.getActivationCode().isEmpty()) ? user.getActivationCode()
              : UUID.randomUUID().toString());
    }
  }

  public void activateUserAccount(UserDTO user) {
    if (log.isDebugEnabled())
      log.debug("execute activateUserAccount [id: " + user.getId() + "]");
    this.jdbcTemplate.update("UPDATE dw_user SET account_state = ?, activationcode = ?  WHERE id = ?",
        AccountState.ACTIVE.name(), null, user.getId());
  }
}
