package de.zpid.datawiz.controller;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import de.zpid.datawiz.dao.ContributorDAO;
import de.zpid.datawiz.dao.ProjectDAO;
import de.zpid.datawiz.dao.RoleDAO;
import de.zpid.datawiz.dao.StudyDAO;
import de.zpid.datawiz.dao.UserDAO;
import de.zpid.datawiz.dto.ProjectDTO;
import de.zpid.datawiz.dto.StudyDTO;
import de.zpid.datawiz.dto.UserDTO;
import de.zpid.datawiz.dto.UserRoleDTO;
import de.zpid.datawiz.enumeration.DataWizErrorCodes;
import de.zpid.datawiz.enumeration.PageState;
import de.zpid.datawiz.enumeration.Roles;
import de.zpid.datawiz.exceptions.DataWizSystemException;
import de.zpid.datawiz.form.ProjectForm;
import de.zpid.datawiz.util.BreadCrumbUtil;
import de.zpid.datawiz.util.UserUtil;

/**
 * Controller for mapping "/panel" <br />
 * <br />
 * This file is part of Datawiz.<br />
 * 
 * <b>Copyright 2016, Leibniz Institute for Psychology Information (ZPID),
 * <a href="http://zpid.de" title="http://zpid.de">http://zpid.de</a>.</b><br />
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
 *
 *          TODO Missing service layer: to separate the DBS logic from the web logic! And Error Handling!
 *
 */

@Controller
@RequestMapping(value = "/panel")
public class PanelController {

	@Autowired
	private MessageSource messageSource;
	@Autowired
	private ClassPathXmlApplicationContext applicationContext;
	@Autowired
	private Environment env;
	@Autowired
	private ProjectDAO projectDAO;
	@Autowired
	private RoleDAO roleDAO;
	@Autowired
	private UserDAO userDAO;
	@Autowired
	private ContributorDAO contributorDAO;
	@Autowired
	private StudyDAO studyDAO;

	private static Logger log = LogManager.getLogger(PanelController.class);

	/**
	 * Instantiates a new panel controller.
	 */
	public PanelController() {
		super();
		if (log.isInfoEnabled())
			log.info("Loading PanelController for mapping /panel");
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
	 * This function handles the calls to /panel. Depending on the access rights for the user who has called the panel, it loads only the content which
	 * the user has the appropriate rights for.
	 * 
	 * @param model
	 *          {@link ModelMap}
	 * @return
	 */
	@RequestMapping(method = RequestMethod.GET)
	public String dashboardPage(ModelMap model) {
		if (log.isTraceEnabled()) {
			log.trace("execute dashboardPage()");
		}

		UserDTO user = null;
		List<ProjectForm> cpform = new ArrayList<ProjectForm>();
		try {
			if (UserUtil.setCurrentUser(userDAO.findByMail(UserUtil.getCurrentUser().getEmail(), true))) {
				user = UserUtil.getCurrentUser();
			} else {
				throw new DataWizSystemException("User not found in Session", DataWizErrorCodes.NO_DATA_ERROR);
			}
			/*List<ProjectDTO> cpdto = null;
			if (user.hasRole(Roles.ADMIN)) {
				cpdto = projectDAO.findAll();
			} else {
				cpdto = projectDAO.findAllByUserID(user);
			}*/
			List<ProjectDTO> cpdto = projectDAO.findAllByUserID(user);
			if (cpdto != null) {
				for (ProjectDTO pdto : cpdto) {
					ProjectForm pform = createProjectForm();
					pform.setProject(pdto);
					if (user.hasRole(Roles.ADMIN) || user.hasRole(Roles.PROJECT_ADMIN, pdto.getId(), false)
					    || user.hasRole(Roles.PROJECT_READER, pdto.getId(), false) || user.hasRole(Roles.PROJECT_WRITER, pdto.getId(), false)) {
						pform.setStudies(studyDAO.findAllStudiesByProjectId(pdto));
					} else if (user.hasRole(Roles.DS_READER, pdto.getId(), false) || user.hasRole(Roles.DS_WRITER, pdto.getId(), false)) {
						List<UserRoleDTO> userRoles = roleDAO.findRolesByUserIDAndProjectID(user.getId(), pdto.getId());
						List<StudyDTO> cStud = new ArrayList<StudyDTO>();
						userRoles.parallelStream().forEach(role -> {
							Roles uRole = Roles.valueOf(role.getType());
							if (role.getStudyId() > 0 && (uRole.equals(Roles.DS_READER) || uRole.equals(Roles.DS_WRITER))) {
								try {
									cStud.add(studyDAO.findById(role.getStudyId(), role.getProjectId(), true, false));
								} catch (Exception e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						});
						pform.setStudies(cStud);
					}
					List<Boolean> par = new ArrayList<>();
					if (pform.getStudies() != null)
						pform.getStudies().parallelStream().forEach(stud -> {
							try {
								stud.setContributors(contributorDAO.findByStudy(stud.getId()));
							} catch (Exception e) {
								// TODO Auto-generated catch block
								par.add(false);
								e.printStackTrace();
							}
						});
					pform.setContributors(contributorDAO.findByProject(pdto, false, true));
					List<UserDTO> sharedUser = userDAO.findGroupedByProject(pdto.getId());
					if (sharedUser != null)
						sharedUser.parallelStream().forEach(shared -> {
							try {
								shared.setGlobalRoles(roleDAO.findRolesByUserIDAndProjectID(shared.getId(), pdto.getId()));
							} catch (SQLException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						});
					pform.setSharedUser(sharedUser);
					cpform.add(pform);
				}
			}
		} catch (Exception e) {
			log.fatal("DBS error during setting Users Dashboardpage for user : {} Message: {}", user.getEmail(), e.getMessage());
			model.put("errormsg", messageSource.getMessage("dbs.sql.exception", new Object[] { env.getRequiredProperty("organisation.admin.email"), e },
			    LocaleContextHolder.getLocale()));
			return "error";
		}
		model.put("breadcrumpList", BreadCrumbUtil.generateBC(PageState.PANEL, null, null, messageSource));
		model.put("CProjectForm", cpform);
		return "panel";
	}
}
