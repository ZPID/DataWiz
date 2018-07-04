package de.zpid.datawiz.dao;

import de.zpid.datawiz.dto.UserDTO;
import de.zpid.datawiz.enumeration.AccountState;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


/**
 * DAO Class for study list types
 * <p>
 * This file is part of the DataWiz distribution (https://github.com/ZPID/DataWiz).
 * Copyright (c) 2018 <a href="https://leibniz-psychology.org/">Leibniz Institute for Psychology Information (ZPID)</a>.
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3.
 * <p>
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <a href="http://www.gnu.org/licenses/">http://www.gnu.org/licenses/</a>.
 *
 * @author Ronny Boelter
 * @version 1.0
 **/
@Repository
@Scope("singleton")
public class UserDAO {

    private final ClassPathXmlApplicationContext applicationContext;
    private final JdbcTemplate jdbcTemplate;
    private final RoleDAO roleDAO;
    private static final Logger log = LogManager.getLogger(UserDAO.class);

    @Autowired
    public UserDAO(ClassPathXmlApplicationContext applicationContext, JdbcTemplate jdbcTemplate, RoleDAO roleDAO) {
        super();
        log.info("Loading UserDAO as @Scope(\"singleton\") and @Repository");
        this.applicationContext = applicationContext;
        this.jdbcTemplate = jdbcTemplate;
        this.roleDAO = roleDAO;
    }


    /**
     * This function selects the complete User Information for the User with the passed ID. If no user was found, null is returned.
     *
     * @param id The ID of an DataWiz User
     * @return A User TO Object with all Information of this User excluding the password, or NULL if no User for the passed ID.
     */
    public UserDTO findById(final long id) {
        log.trace("Entering findById [id: {}]", () -> id);
        final UserDTO user = this.jdbcTemplate.query("SELECT * FROM dw_user WHERE id= ?", new Object[]{id}, rs -> {
            if (rs.next()) {
                return setUserDTO(false, rs);
            }
            return null;
        });
        // set Userroles
        if (user != null && user.getId() > 0) {
            user.setGlobalRoles(roleDAO.findRolesByUserID(user.getId()));
        }
        log.debug("Transaction for findByID returned user: [email: {}]", () -> user != null ? user.getEmail() : null);
        return user;
    }

    /**
     * This function selects the complete User Information for the User with the passed mail. If no user was found, null is returned.
     *
     * @param email The primary mail address is defined as unique in the DB, therefore it is possible to search a User by mail.
     * @param pwd   A boolean value: select true if the password is needed for further execution, otherwise select false
     * @return A UserDTO object with all available user data if the user exists, otherwise null
     */
    public UserDTO findByMail(final String email, final boolean pwd) {
        log.trace("Entering findByMail [mail: {}], exptractPWD[{}]", () -> email, () -> pwd);
        UserDTO user = this.jdbcTemplate.query("SELECT * FROM dw_user WHERE email= ?", new Object[]{email}, rs -> {
            if (rs.next()) {
                return setUserDTO(pwd, rs);
            }
            return null;
        });
        // set Userroles
        if (user != null && user.getId() > 0) {
            user.setGlobalRoles(roleDAO.findRolesByUserID(user.getId()));
        }
        log.debug("Transaction for findByMail returned user: [id: {}]", () -> user != null ? user.getId() : null);
        return user;
    }

    /**
     * Return only the encoded password of a DataWiz User
     *
     * @param id The id of an User
     * @return The encoded password
     */
    public String findPasswordbyId(final long id) {
        log.debug("execute findPasswordbyId [id: {}]", () -> id);
        final String pwd = this.jdbcTemplate.query("SELECT dw_user.password FROM dw_user WHERE id= ?", new Object[]{id}, rs -> {
            if (rs.next()) {
                return rs.getString("password");
            }
            return null;
        });
        log.debug("Transaction for findPasswordbyId returned: {}", () -> (pwd != null && !pwd.isEmpty()) ? "password found" : "no password found");
        return pwd;
    }

    /**
     * Returns a list of all project contributors, which are selected by the passed project id
     *
     * @param pid The projectId
     * @return List of Projectcontributors
     */
    public List<UserDTO> findGroupedByProject(final long pid) {
        log.trace("Entering findGroupedByProject [projectID: {}], exptractPWD[{}]", () -> pid);
        String sql = "SELECT dw_user.* FROM dw_user LEFT JOIN dw_user_roles ON dw_user.id = dw_user_roles.user_id "
                + "LEFT JOIN dw_roles ON dw_roles.id = dw_user_roles.role_id " + "WHERE dw_user_roles.project_id = ? GROUP BY dw_user.id";
        List<UserDTO> ret = this.jdbcTemplate.query(sql, new Object[]{pid}, (rs, rowNum) -> setUserDTO(false, rs));
        log.debug("Transaction for findGroupedByProject returned [size: {}]", ret::size);
        return ret;
    }

    /**
     * Returns a list of all contributors(administration query).
     *
     * @return List of contributors
     */
    public List<UserDTO> findAll() {
        log.trace("Entering findAll (ADMIN only)");
        String sql = "SELECT * FROM dw_user ";
        List<UserDTO> ret = this.jdbcTemplate.query(sql, new Object[]{}, (rs, rowNum) -> setUserDTO(false, rs));
        log.debug("Transaction for findGroupedByProject returned [size: {}]", ret::size);
        return ret;
    }

    @SuppressWarnings("unused")
    public int deleteUser(final UserDTO user) {
        log.trace("Entering deleteUser for user [email: {}]", user::getEmail);
        String query = "DELETE FROM dw_user WHERE " + (user.getEmail() != null && !user.getEmail().isEmpty() ? "email = ?" : "id = ?");
        final int ret = this.jdbcTemplate.update(query, (user.getEmail() != null && !user.getEmail().isEmpty() ? user.getEmail() : user.getId()));
        log.debug("Transaction for deleteUser returned: {}", ret);
        return ret;
    }

    /**
     * This function decides by evaluation of the ID in the passed UserDTO if the User Data has to be saved or updated.
     *
     * @param user      The UserDTO Object
     * @param changePWD True if the password has to be changed, otherwise false.
     */
    public void saveOrUpdate(final UserDTO user, final boolean changePWD) {
        log.trace("Entering saveOrUpdate for user [email: {}; id {}] and changePWD [{}]", user::getEmail, () -> user.getId() > 0 ? user.getId() : null,
                () -> changePWD);
        int ret;
        if (user.getId() > 0) {
            final List<Object> params = new ArrayList<>();
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
            ret = this.jdbcTemplate.update(
                    "UPDATE dw_user SET email = ?, email2= ?, " + (changePWD ? "password = ?, " : "")
                            + "title = ?, first_name = ?, last_name = ?, phone = ?, fax = ?, comments = ?, institution = ?, department = ?,  homepage = ?, "
                            + "street = ?, zip = ?, city = ?, state = ?, country = ?, orcid_id = ?, account_state = ?, activationcode = ? " + "WHERE id = ?",
                    params.toArray());
        } else {
            ret = this.jdbcTemplate.update("INSERT INTO dw_user  (first_name, last_name, password, email, account_state, activationcode) VALUES (?,?,?,?,?,?)",
                    user.getFirstName(), user.getLastName(), user.getPassword(), user.getEmail(), AccountState.LOCKED.name(),
                    (user.getActivationCode() != null && !user.getActivationCode().isEmpty()) ? user.getActivationCode() : UUID.randomUUID().toString());
        }
        log.debug("Transaction for saveOrUpdate returned: {}", ret);
    }

    public void updatePassword(final UserDTO user) {
        log.trace("Entering updatePassword for user [email: {}]", user::getEmail);
        int ret = this.jdbcTemplate.update("UPDATE dw_user SET password = ? WHERE email = ?", user.getPassword(), user.getEmail());
        log.debug("Transaction for updatePassword returned: {}", ret);
    }

    /**
     * Activates a registered user Account
     *
     * @param user UserDTO
     */
    public void activateUserAccount(final UserDTO user) {
        log.trace("Entering activateUserAccount for user [email: {}]", user::getEmail);
        final int ret = this.jdbcTemplate.update("UPDATE dw_user SET account_state = ?, activationcode = ?  WHERE id = ?", AccountState.ACTIVE.name(), null,
                user.getId());
        log.debug("Transaction for activateUserAccount returned: {}", ret);
    }

    /**
     * Private function to save the database transaction result into a UserDTO object an to avoid redundancy
     *
     * @param pwd True if the password should be saved into the DTO Object, otherwise false!
     * @param rs  Result of the database transaction as ResultSet
     * @return a full set of user data
     */
    private UserDTO setUserDTO(final boolean pwd, final ResultSet rs) throws SQLException {
        final UserDTO contact = (UserDTO) applicationContext.getBean("UserDTO");
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
