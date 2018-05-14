package de.zpid.datawiz.dao;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

import de.zpid.datawiz.dto.UserDTO;

@Repository
@Scope("singleton")
public class AdminDAO {

	@Autowired
	private JdbcTemplate jdbcTemplate;
	@Autowired
	private PasswordEncoder passwordEncoder;

	private static Logger log = LogManager.getLogger(AdminDAO.class);

	public AdminDAO() {
		super();
		if (log.isInfoEnabled())
			log.info("Loading TagDAO as Singleton and Service");
	}

	public int findCountByTableName(final String name) throws Exception {
		log.trace("Entering findCountByTableName [name: {}", () -> name);
		String sql = "SELECT count(*) FROM " + name;
		int count = jdbcTemplate.queryForObject(sql, new Object[] {}, Integer.class);
		log.debug("Leaving getTagsByProjectID [result: {}]", () -> count);
		return count;
	}

	public int updateUserAccount(final UserDTO user) throws Exception {
		log.trace("Entering updateUserAccount for user [id {}]", () -> user.getId());
		final List<Object> params = new ArrayList<Object>();
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
		return ret;
	}
}
