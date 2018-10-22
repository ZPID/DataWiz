package de.zpid.datawiz.service;

import de.zpid.datawiz.dao.AdminDAO;
import de.zpid.datawiz.dao.ProjectDAO;
import de.zpid.datawiz.dao.StudyDAO;
import de.zpid.datawiz.dao.UserDAO;
import de.zpid.datawiz.dto.ProjectDTO;
import de.zpid.datawiz.dto.UserDTO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service class for the admin controller to separate the web logic from the business logic.
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
@Service
public class AdminService {

    private static Logger log = LogManager.getLogger(AdminService.class);

    private UserDAO userDAO;
    private AdminDAO adminDAO;
    private ProjectDAO projectDAO;
    private StudyDAO studyDAO;
    private ClassPathXmlApplicationContext applicationContext;

    @Autowired
    public AdminService(final UserDAO userDAO, final AdminDAO adminDAO, final ProjectDAO projectDAO, final StudyDAO studyDAO,
                        final ClassPathXmlApplicationContext applicationContext) {
        super();
        log.info("Loading AdminService");
        this.userDAO = userDAO;
        this.adminDAO = adminDAO;
        this.projectDAO = projectDAO;
        this.studyDAO = studyDAO;
        this.applicationContext = applicationContext;
    }

    /**
     * This function returns a list of projects, studies or users. The type on this list depends on the passed type parameter.
     * Supported types are "user", "project" or "study", other types will be ignored.
     *
     * @param type Type of the list which has to be looked for in the database and returned as {@link String}
     * @param id   This identifier is only used for project lists at the moment, because the function can return a list of all projects for
     *             a specific user. {@link Long}
     * @return Returns a list of users, projects or studies, depending on the type param
     */
    public List<?> getList(final String type, final long id) {
        log.trace("Entering getList with [type: {}; id: {}]", () -> type, () -> id);
        List<?> lst = null;
        try {
            switch (type) {
                case "user":
                    lst = userDAO.findAll();
                    break;
                case "project":
                    if (id > 0)
                        lst = projectDAO.findAllByUserID(userDAO.findById(id));
                    else
                        lst = projectDAO.findAll();
                    break;
                case "study":
                    lst = studyDAO.findAll();
                    break;
            }
        } catch (Exception e) {
            log.warn("SQL:", () -> e);
        }
        log.trace("Leaving getList with result: {}", lst == null ? "null" : lst.size());
        return lst;
    }

    /**
     * This functions returns the total count of all entities in a database table, depending on the passed name param
     *
     * @param name DB table identifier as {@link String}
     * @return The count as int
     */
    public int countValuesByTableName(final String name) {
        log.trace("Entering countValuesByTableName with [name: {}]", () -> name);
        int count = -1;
        try {
            count = adminDAO.findCountByTableName(name);
        } catch (Exception e) {
            log.warn("SQL:", () -> e);
        }
        log.trace("Leaving countValuesByTableName with result: {}", count);
        return count;
    }

    public int countProjectsByUser(final long id) {
        List<ProjectDTO> projects = projectDAO.findAllByUserID(userDAO.findById(id));
        return projects == null ? 0 : projects.size();
    }


    /**
     * Updates user details
     *
     * @param id           long User identifier
     * @param title        {@link String}
     * @param firstName    {@link String}
     * @param lastName     {@link String}
     * @param email        {@link String}
     * @param secEmail     {@link String}
     * @param password     {@link String}
     * @param accountState {@link String}
     */
    public void setAndUpdateUser(final long id, final String title, final String firstName, final String lastName, final String email, final String secEmail,
                                 final String password, final String accountState) {
        UserDTO user = (UserDTO) applicationContext.getBean("UserDTO");
        user.setId(id);
        user.setTitle(title);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setSecEmail(secEmail);
        user.setPassword(password);
        user.setAccount_state(accountState);
        adminDAO.updateUserAccount(user);
    }

}
