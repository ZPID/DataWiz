package de.zpid.datawiz.service;

import java.util.List;
import java.util.Optional;

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

@Service
public class AdminService {

	private static Logger log = LogManager.getLogger(AdminService.class);

	@Autowired
	private UserDAO userDAO;
	@Autowired
	private AdminDAO adminDAO;
	@Autowired
	private ProjectDAO projectDAO;
	@Autowired
	private StudyDAO studyDAO;

	public List<?> getList(final String type, final Optional<Integer> id) {
		log.trace("Entering getList with [type: {}; id: {}]", () -> type, () -> id);
		List<?> lst = null;
		try {
			if (type.equals("user"))
				lst = userDAO.findAll();
			else if (type.equals("project"))
				if (id != null && id.isPresent())
					lst = projectDAO.findAllByUserID(userDAO.findById(id.get()));
				else
					lst = projectDAO.findAll();
			else if (type.equals("study"))
				lst = studyDAO.findAll();
		} catch (Exception e) {
			log.warn("SQL:", () -> e);
		}
		log.trace("Leaving getList with result: {}", lst == null ? "null" : lst.size());
		return lst;
	}

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

	@Autowired
	private ClassPathXmlApplicationContext applicationContext;

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
