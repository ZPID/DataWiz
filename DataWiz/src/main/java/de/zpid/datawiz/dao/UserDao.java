package de.zpid.datawiz.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;

import de.zpid.datawiz.controller.LoginController;
import de.zpid.datawiz.dto.UserDTO;
import de.zpid.datawiz.dto.UserRoleDTO;

public class UserDao {

  private static final Logger log = Logger.getLogger(LoginController.class);
  private ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("spring.xml");
  private JdbcTemplate jdbcTemplate;

  public UserDao() {
  }

  public UserDao(DataSource dataSource) {
    jdbcTemplate = new JdbcTemplate(dataSource);
  }

  public UserDTO findById(int id) {

    return new UserDTO();
  }

  public UserDTO findByMail(String email) throws SQLException, DataAccessException{
    if (log.isDebugEnabled())
      log.debug("execute findByMail for email: " + email);
    UserDTO user = jdbcTemplate.query("SELECT * FROM datawiz_users WHERE email= ?", new Object[] { email },
        new ResultSetExtractor<UserDTO>() {
          @Override
          public UserDTO extractData(ResultSet rs) throws SQLException, DataAccessException {
            if (rs.next()) {
              UserDTO contact = (UserDTO) context.getBean("UserDTO");
              contact.setId(rs.getInt("id"));
              contact.setFirstName(rs.getString("first_name"));
              contact.setLastName(rs.getString("last_name"));
              contact.setPassword(rs.getString("password"));
              contact.setEmail(rs.getString("email"));
              contact.setState(rs.getString("state"));
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

  public void saveOrUpdate(UserDTO user) {

  }


  public List<UserRoleDTO> getRolesByUserID(int id) throws SQLException{
    String sql = "SELECT * FROM datawiz_users_userroles "
        + " JOIN datawiz_userroles ON datawiz_users_userroles.datawiz_userroles_id = datawiz_userroles.id "
        + "WHERE datawiz_user_id  = ?";
    return jdbcTemplate.query(sql, new Object[] { id }, new RowMapper<UserRoleDTO>() {
      public UserRoleDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
        UserRoleDTO role = (UserRoleDTO) context.getBean("UserRoleDTO");
        role.setRoleId(rs.getInt("id"));
        role.setProjectId(rs.getInt("datawiz_project_id"));
        role.setUserId(rs.getInt("datawiz_user_id"));
        role.setType(rs.getString("type"));
        return role;
      }
    });
  }
}
