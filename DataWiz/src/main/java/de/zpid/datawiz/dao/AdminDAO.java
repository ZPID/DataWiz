package de.zpid.datawiz.dao;

import de.zpid.datawiz.dto.UserDTO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;


/**
 * DAO Class for Admin
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
public class AdminDAO {

    private final JdbcTemplate jdbcTemplate;
    private final PasswordEncoder passwordEncoder;

    private static final Logger log = LogManager.getLogger(AdminDAO.class);

    @Autowired
    public AdminDAO(JdbcTemplate jdbcTemplate, PasswordEncoder passwordEncoder) {
        super();
        log.info("Loading AdminDAO as @Scope(\"singleton\") and @Repository");
        this.jdbcTemplate = jdbcTemplate;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Returns the count of all entities of a dbs table, selected by the passed name
     *
     * @param name {@link String} Name of the table
     * @return Num of all entities
     */
    public int findCountByTableName(final String name) {
        log.trace("Entering findCountByTableName [name: {}", () -> name);
        Integer count = jdbcTemplate.queryForObject("SELECT count(*) FROM " + name, Integer.class);
        log.debug("Leaving getTagsByProjectID [result: {}]", () -> count);
        return count != null ? count : 0;
    }


    /**
     * Updates user data in dw_user.
     *
     * @param user {@link UserDTO} Contains the data of a user
     */
    public void updateUserAccount(final UserDTO user) {
        log.trace("Entering updateUserAccount for user [id {}]", user::getId);
        final List<Object> params = new ArrayList<>();
        params.add(user.getTitle());
        params.add(user.getFirstName());
        params.add(user.getLastName());
        params.add(user.getEmail());
        params.add(user.getSecEmail());
        if (user.getPassword() != null && !user.getPassword().trim().isEmpty())
            params.add(passwordEncoder.encode(user.getPassword()));
        params.add(user.getAccount_state());
        params.add(user.getId());
        int ret = this.jdbcTemplate.update(
                "UPDATE dw_user SET title = ?, first_name = ?, last_name = ?, email = ?, email2= ?,"
                        + ((user.getPassword() != null && !user.getPassword().trim().isEmpty()) ? " password = ?," : "") + " account_state = ? WHERE id = ?",
                params.toArray());
        log.debug("Transaction for updateUserAccount returned: {}", () -> ret);
    }
}
