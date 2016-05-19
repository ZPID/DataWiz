package de.zpid.datawiz.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import de.zpid.datawiz.dto.UserDTO;
import de.zpid.datawiz.enumeration.AccountState;

@Repository
@Scope("singleton")
public class UserDAO extends SuperDAO {

  public UserDAO() {
    super();
    if (log.isInfoEnabled())
      log.info("Loading UserDAO as Singleton and Service");
  }

  @Autowired
  private RoleDAO roleDAO;

  /**
   * 
   * This function selects the complete User Information for the User with the passed ID. If no user was found, null is
   * returned.
   * 
   * @param id
   *          The ID of an DataWiz User
   * @return A User TO Object with all Information of this User excluding the password, or NULL if no User for the
   *         passed ID.
   * @throws SQLException
   */
  public UserDTO findById(final long id) throws SQLException {
    log.trace("Entering findById [id: {}]", () -> id);
    final UserDTO user = this.jdbcTemplate.query("SELECT * FROM dw_user WHERE id= ?", new Object[] { id },
        new ResultSetExtractor<UserDTO>() {
          @Override
          public UserDTO extractData(ResultSet rs) throws SQLException, DataAccessException {
            if (rs.next()) {
              return setUserDTO(false, rs);
            }
            return null;
          }
        });
    // set Userroles
    if (user != null && user.getId() > 0) {
      user.setGlobalRoles(roleDAO.findRolesByUserID(user.getId()));
    }
    log.debug("Transaction for findByID returned user: [email: {}]", () -> user != null ? user.getEmail() : null);
    return user;
  }

  /**
   * This function selects the complete User Information for the User with the passed mail. If no user was found, null
   * is returned.
   * 
   * @param email
   *          The primary mail address is defined as unique in the DB, therefore it is possible to search a User by
   *          mail.
   * @param pwd
   *          A boolean value: select true if the password is needed for further execution, otherwise select false
   * @return A UserDTO object with all available user data if the user exists, otherwise null
   * @throws SQLException
   */
  public UserDTO findByMail(final String email, final boolean pwd) throws SQLException {
    log.trace("Entering findByMail [mail: {}], exptractPWD[{}]", () -> email, () -> pwd);
    UserDTO user = this.jdbcTemplate.query("SELECT * FROM dw_user WHERE email= ?", new Object[] { email },
        new ResultSetExtractor<UserDTO>() {
          @Override
          public UserDTO extractData(ResultSet rs) throws SQLException, DataAccessException {
            if (rs.next()) {
              return setUserDTO(pwd, rs);
            }
            return null;
          }

        });
    // set Userroles
    if (user != null && user.getId() > 0) {
      user.setGlobalRoles(roleDAO.findRolesByUserID(user.getId()));
    }
    log.debug("Transaction for findByMail returned user: [id: {}]", () -> user != null ? user.getId() : null);
    return user;
  }

  public int deleteUser(final UserDTO user) {
    log.trace("Entering deleteUser for user [email: {}]", () -> user.getEmail());
    String query = "DELETE FROM dw_user WHERE "
        + (user.getEmail() != null && !user.getEmail().isEmpty() ? "email = ?" : "id = ?");
    final int ret = this.jdbcTemplate.update(query,
        (user.getEmail() != null && !user.getEmail().isEmpty() ? user.getEmail() : user.getId()));
    log.debug("Transaction for deleteUser returned: {}", ret);
    return ret;
  }

  /**
   * Returns a list of all project contributors, which are selected by the passed project id
   * 
   * @param pid
   *          The projectId
   * @return List of Projectcontributors
   * @throws SQLException
   */
  public List<UserDTO> findGroupedByProject(final long pid) throws SQLException {
    log.trace("Entering findGroupedByProject [projectID: {}], exptractPWD[{}]", () -> pid);
    String sql = "SELECT dw_user.* FROM dw_user " + "LEFT JOIN dw_user_roles ON dw_user.id = dw_user_roles.user_id "
        + "LEFT JOIN dw_roles ON dw_roles.id = dw_user_roles.role_id "
        + "WHERE dw_user_roles.project_id = ? GROUP BY dw_user_roles.user_id";
    List<UserDTO> ret = this.jdbcTemplate.query(sql, new Object[] { pid }, new RowMapper<UserDTO>() {
      public UserDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
        return setUserDTO(false, rs);
      }
    });
    log.debug("Transaction for findGroupedByProject returned [size: {}]", () -> ret.size());
    return ret;
  }

  /**
   * This function decides by evaluation of the ID in the passed UserDTO if the User Data has to be saved or updated.
   * 
   * @param user
   *          The UserDTO Object
   * @param changePWD
   *          True if the password has to be changed, otherwise false.
   * @return returns "0" if no changes where saved, and ">1" if data where saved!
   * @throws SQLException
   */
  public int saveOrUpdate(final UserDTO user, final boolean changePWD) throws SQLException {
    log.trace("Entering saveOrUpdate for user [email: {}; id {}] and changePWD [{}]", () -> user.getEmail(),
        () -> user.getId() > 0 ? user.getId() : null, () -> changePWD);
    int ret = 0;
    if (user.getId() > 0) {
      final List<Object> params = new ArrayList<Object>();
      params.add(user.getEmail());
      params.add(user.getSecEmail());
      if (changePWD)
        params.add(user.getPassword());
      params.add(user.getTitle());
      params.add(user.getFirstName());
      params.add(user.getLastName());
      params.add(user.getPhone());
      params.add(user.getFax());
      params.add(user.getComments());
      params.add(user.getInstitution());
      params.add(user.getDepartment());
      params.add(user.getHomepage());
      params.add(user.getStreet());
      params.add(user.getZip());
      params.add(user.getCity());
      params.add(user.getState());
      params.add(user.getCountry());
      params.add(user.getOrcid());
      params.add(user.getAccount_state());
      params.add(user.getActivationCode());
      params.add(user.getId());
      ret = this.jdbcTemplate.update("UPDATE dw_user SET email = ?, email2= ?, " + (changePWD ? "password = ?, " : "")
          + "title = ?, first_name = ?, last_name = ?, phone = ?, fax = ?, comments = ?, institution = ?, department = ?,  homepage = ?, "
          + "street = ?, zip = ?, city = ?, state = ?, country = ?, orcid_id = ?, account_state = ?, activationcode = ? "
          + "WHERE id = ?", params.toArray());
    } else {
      ret = this.jdbcTemplate.update(
          "INSERT INTO dw_user  (first_name, last_name, password, email, account_state, activationcode) VALUES (?,?,?,?,?,?)",
          user.getFirstName(), user.getLastName(), user.getPassword(), user.getEmail(), AccountState.LOCKED.name(),
          (user.getActivationCode() != null && !user.getActivationCode().isEmpty()) ? user.getActivationCode()
              : UUID.randomUUID().toString());
    }
    log.debug("Transaction for saveOrUpdate returned: {}", ret);
    return ret;
  }

  /**
   * Return only the encoded password of a DataWiz User
   * 
   * @param id
   *          The id of an User
   * @return The encoded password
   * @throws SQLException
   */
  public String findPasswordbyId(final long id) throws SQLException {
    log.debug("execute findPasswordbyId [id: {}]", () -> id);
    final String pwd = this.jdbcTemplate.query("SELECT dw_user.password FROM dw_user WHERE id= ?", new Object[] { id },
        new ResultSetExtractor<String>() {
          @Override
          public String extractData(ResultSet rs) throws SQLException, DataAccessException {
            if (rs.next()) {
              return rs.getString("password");
            }
            return null;
          }
        });
    log.debug("Transaction for findPasswordbyId returned: {}",
        () -> (pwd != null && !pwd.isEmpty()) ? "password found" : "no password found");
    return pwd;
  }

  /**
   * Activates a registered user Account
   * 
   * @param user
   *          UserDTO
   * @return "0" if no changes where saved, ">0" if data where saved
   */
  public int activateUserAccount(final UserDTO user) {
    log.trace("Entering activateUserAccount for user [email: {}]", () -> user.getEmail());
    final int ret = this.jdbcTemplate.update("UPDATE dw_user SET account_state = ?, activationcode = ?  WHERE id = ?",
        AccountState.ACTIVE.name(), null, user.getId());
    log.debug("Transaction for activateUserAccount returned: {}", ret);
    return ret;
  }

  /**
   * Private function to save the database transaction result into a UserDTO object an to avoid redundancy
   * 
   * @param pwd
   *          True if the password should be saved into the DTO Object, otherwise false!
   * @param rs
   *          Result of the database transaction as ResultSet
   * @return a full set of user data
   * @throws SQLException
   */
  private UserDTO setUserDTO(final boolean pwd, final ResultSet rs) throws SQLException {
    final UserDTO contact = (UserDTO) context.getBean("UserDTO");
    contact.setId(rs.getLong("id"));
    contact.setEmail(rs.getString("email"));
    contact.setSecEmail(rs.getString("email2"));
    contact.setPassword(pwd ? rs.getString("password") : "");
    contact.setTitle(rs.getString("title"));
    contact.setFirstName(rs.getString("first_name"));
    contact.setLastName(rs.getString("last_name"));
    contact.setPhone(rs.getString("phone"));
    contact.setFax(rs.getString("fax"));
    contact.setComments(rs.getString("comments"));
    contact.setInstitution(rs.getString("institution"));
    contact.setDepartment(rs.getString("department"));
    contact.setHomepage(rs.getString("homepage"));
    contact.setStreet(rs.getString("street"));
    contact.setZip(rs.getString("zip"));
    contact.setCity(rs.getString("city"));
    contact.setState(rs.getString("state"));
    contact.setCountry(rs.getString("country"));
    contact.setOrcid(rs.getString("orcid_id"));
    contact.setAccountState(rs.getString("account_state"));
    contact.setActivationCode(rs.getString("activationcode"));
    return contact;
  }
}
