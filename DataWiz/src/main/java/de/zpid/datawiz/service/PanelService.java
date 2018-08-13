package de.zpid.datawiz.service;


import de.zpid.datawiz.dao.*;
import de.zpid.datawiz.dto.ProjectDTO;
import de.zpid.datawiz.dto.StudyDTO;
import de.zpid.datawiz.dto.UserDTO;
import de.zpid.datawiz.dto.UserRoleDTO;
import de.zpid.datawiz.enumeration.DataWizErrorCodes;
import de.zpid.datawiz.enumeration.Roles;
import de.zpid.datawiz.exceptions.DataWizSystemException;
import de.zpid.datawiz.form.ProjectForm;
import de.zpid.datawiz.util.UserUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;


/**
 * Service class for the Panel controller to separate the web logic from the business logic.
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
public class PanelService {

    private static Logger log = LogManager.getLogger(PanelService.class);
    private ClassPathXmlApplicationContext applicationContext;
    private ProjectDAO projectDAO;
    private RoleDAO roleDAO;
    private UserDAO userDAO;
    private ContributorDAO contributorDAO;
    private StudyDAO studyDAO;

    @Autowired
    public PanelService(ClassPathXmlApplicationContext applicationContext,
                        ProjectDAO projectDAO, RoleDAO roleDAO, UserDAO userDAO, ContributorDAO contributorDAO,
                        StudyDAO studyDAO) {
        super();
        log.info("Loading PanelService");
        this.applicationContext = applicationContext;
        this.projectDAO = projectDAO;
        this.roleDAO = roleDAO;
        this.userDAO = userDAO;
        this.contributorDAO = contributorDAO;
        this.studyDAO = studyDAO;
    }

    /**
     * Creates the project form.
     *
     * @return {@link ProjectForm}
     */
    @ModelAttribute("ProjectForm")
    public ProjectForm createProjectForm() {
        return (ProjectForm) applicationContext.getBean("ProjectForm");
    }

    /**
     * This function tries to refresh the credentials of the current logged-on user. This is necessary to pretend
     * log-off/log-on behaviour if, for example, the user was invited to a new project.
     *
     * @return The current logged-on user
     * @throws DataWizSystemException Is thrown if no user is logged-on
     */
    public UserDTO refreshAndGetUserDTO() throws DataWizSystemException {
        UserDTO user;
        if (UserUtil.setCurrentUser(userDAO.findByMail(UserUtil.getCurrentUser().getEmail(), true))) {
            user = UserUtil.getCurrentUser();
        } else {
            throw new DataWizSystemException("User not found in Session", DataWizErrorCodes.USER_NOT_IN_SESSION);
        }
        return user;
    }

    /**
     * Load and return the projects of a passed user.
     *
     * @param user   {@link UserDTO} The Information of the logged-on user.
     * @param parChk {@link AtomicBoolean} Will be set to true if an error occurs during parallel loop
     * @return List of projects
     */
    public List<ProjectForm> getProjects(final UserDTO user, final AtomicBoolean parChk) {
        List<ProjectForm> cpform = new ArrayList<>();
        List<ProjectDTO> cpdto = projectDAO.findAllByUserID(user);
        if (cpdto != null) {
            for (ProjectDTO pdto : cpdto) {
                ProjectForm pform = createProjectForm();
                pform.setProject(pdto);
                if (user.hasRole(Roles.ADMIN) || user.hasRole(Roles.PROJECT_ADMIN, pdto.getId(), false)
                        || user.hasRole(Roles.PROJECT_READER, pdto.getId(), false) || user.hasRole(Roles.PROJECT_WRITER, pdto.getId(), false)) {
                    pform.setStudies(studyDAO.findAllStudiesByProjectId(pdto));
                } else if (user.hasRole(Roles.DS_READER, pdto.getId(), false) || user.hasRole(Roles.DS_WRITER, pdto.getId(), false)) {
                    pform.setStudies(getStudyDTOS(user.getId(), pdto.getId(), parChk));
                }
                setStudyContributors(pform.getStudies(), parChk);
                pform.setContributors(contributorDAO.findByProject(pdto, false, true));
                pform.setSharedUser(getUserDTOS(pdto.getId(), parChk));
                cpform.add(pform);
            }
        }
        return cpform;
    }

    /**
     * Load and return the studies of the given project using a parallel loop.
     *
     * @param userId User Identifier
     * @param pid    Project Identifier
     * @param parChk {@link AtomicBoolean} Will be set to true if an error occurs during parallel loop
     * @return List of Studies
     */
    private List<StudyDTO> getStudyDTOS(final long userId, final long pid, final AtomicBoolean parChk) {
        List<UserRoleDTO> userRoles = roleDAO.findRolesByUserIDAndProjectID(userId, pid);
        List<StudyDTO> cStud = new ArrayList<>();
        if (userRoles != null)
            userRoles.parallelStream().forEach(role -> {
                Roles uRole = Roles.valueOf(role.getType());
                if (role.getStudyId() > 0 && (uRole.equals(Roles.DS_READER) || uRole.equals(Roles.DS_WRITER))) {
                    try {
                        cStud.add(studyDAO.findById(role.getStudyId(), role.getProjectId(), true, false));
                    } catch (Exception e) {
                        log.warn("Parallel loop Error while getting studies from DBS", () -> e);
                        parChk.set(true);
                    }
                }
            });
        return cStud;
    }

    /**
     * Load the contributors of the given studies using a parallel loop.
     *
     * @param studies List of Studies
     * @param parChk  {@link AtomicBoolean} Will be set to true if an error occurs during parallel loop
     */
    private void setStudyContributors(final List<StudyDTO> studies, final AtomicBoolean parChk) {
        if (studies != null)
            studies.parallelStream().forEach(stud -> {
                try {
                    stud.setContributors(contributorDAO.findByStudy(stud.getId()));
                } catch (Exception e) {
                    log.warn("Parallel loop Error while getting contributors from DBS", () -> e);
                    parChk.set(true);
                }
            });
    }

    /**
     * Load and return the collaborators of the given project using a parallel loop.
     *
     * @param pid    Project Identifier
     * @param parChk {@link AtomicBoolean} Will be set to true if an error occurs during parallel loop
     * @return List of Users
     */
    private List<UserDTO> getUserDTOS(final long pid, final AtomicBoolean parChk) {
        List<UserDTO> sharedUser = userDAO.findGroupedByProject(pid);
        if (sharedUser != null)
            sharedUser.parallelStream().forEach(shared -> {
                try {
                    shared.setGlobalRoles(roleDAO.findRolesByUserIDAndProjectID(shared.getId(), pid));
                } catch (Exception e) {
                    log.warn("Parallel loop Error while getting roles from DBS", () -> e);
                    parChk.set(true);
                }
            });
        return sharedUser;
    }


}
