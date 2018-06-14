package de.zpid.datawiz.service;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Service;

import de.zpid.datawiz.dao.AdminDAO;
import de.zpid.datawiz.dao.ProjectDAO;
import de.zpid.datawiz.dao.StudyDAO;
import de.zpid.datawiz.dao.UserDAO;
import de.zpid.datawiz.dto.UserDTO;

/**
 * Administration Service Class <br />
 * <br />
 * This file is part of Datawiz.<br />
 *
 * <b>Copyright 2018, Leibniz Institute for Psychology Information (ZPID), <a href="http://zpid.de" title="http://zpid.de">http://zpid.de</a>.</b><br />
 * <br />
 * <a rel="license" href="http://creativecommons.org/licenses/by-nc-sa/4.0/"><img alt="Creative Commons License" style= "border-width:0" src=
 * "https://i.creativecommons.org/l/by-nc-sa/4.0/80x15.png" /></a><br />
 * <span xmlns:dct="http://purl.org/dc/terms/" property="dct:title">Datawiz</span> by
 * <a xmlns:cc="http://creativecommons.org/ns#" href="zpid.de" property="cc:attributionName" rel="cc:attributionURL"> Leibniz Institute for Psychology
 * Information (ZPID)</a> is licensed under a <a rel="license" href="http://creativecommons.org/licenses/by-nc-sa/4.0/">Creative Commons
 * Attribution-NonCommercial-ShareAlike 4.0 International License</a>.
 *
 * @author Ronny Boelter
 * @version 1.0
 */
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
                case " user":
                    lst = userDAO.findAll();
                    break;
                case "project":
                    if (id > 0)
                        lst = projectDAO.findAllByUserID(userDAO.findById(id));
                    else
                        lst = projectDAO.findAll();
                    break;
                case " study":
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
     * @return The count as {@link int}
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


    public void setAndUpdateUser(final long id, final String title, final String firstName, final String lastName, final String email, final String secEmail,
                                 final String password, final String accountState) throws Exception {
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
