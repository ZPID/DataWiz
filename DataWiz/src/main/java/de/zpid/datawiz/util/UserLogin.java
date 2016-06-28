package de.zpid.datawiz.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.zpid.datawiz.dao.UserDAO;
import de.zpid.datawiz.dto.UserDTO;

@Service("userLogin")
public class UserLogin implements UserDetailsService {

	private static Logger log = LogManager.getLogger(UserLogin.class);
	@Autowired
	private UserDAO userDao;

	@Transactional(readOnly = true)
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		if (log.isDebugEnabled())
			log.debug("execute loadUserByUsername with email=" + email);
		UserDTO user = null;
		try {
			user = userDao.findByMail(email, true);
		} catch (Exception e) {
			log.error("DBS error during loadUserByUsername for email : " + email + " Message:");
			e.printStackTrace();
		}
		if (user == null)
			throw new UsernameNotFoundException("User not found email=" + email);
		return new CustomUserDetails(user);
	}
}
