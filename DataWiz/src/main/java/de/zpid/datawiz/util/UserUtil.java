package de.zpid.datawiz.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.context.SecurityContextHolder;

import de.zpid.datawiz.dto.UserDTO;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

/**
 * This class provides static functions for user management.<br />
 * <br />
 * This file is part of Datawiz.<br />
 * 
 * <b>Copyright 2017, Leibniz Institute for Psychology Information (ZPID), <a href="http://zpid.de" title="http://zpid.de">http://zpid.de</a>.</b><br />
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
 * 
 */
@Component
public class UserUtil {

	private static Logger log = LogManager.getLogger(UserUtil.class);

	/**
	 * Retrieves the current user object from the session and returns it. This function is called by almost all controller functions to ensure that a user is
	 * logged in and has the necessary rights to view or edit the data.
	 * 
	 * @return UserDTO of the currently logged in user
	 */
	public static UserDTO getCurrentUser() {
		UserDTO user = null;
		try {
			if (SecurityContextHolder.getContext().getAuthentication() != null && SecurityContextHolder.getContext().getAuthentication().getPrincipal() != null
			    && SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof CustomUserDetails)
				user = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
		} catch (Exception e) {
			log.warn("Error getting current authendicated User - Message: " + e.getMessage());
			user = null;
		}
		return user;
	}

	/**
	 * This function saves a new UserDTO object into the session. This is necessary if either the user data has changed or new rights to projects or studies have
	 * been given to the user. The call of this function takes place in the panel and therefore a new login is not necessary.
	 * 
	 * @param user
	 *          UserDTO of the currently logged in user
	 * @return true, if setUser was successful, otherwise false
	 */
	public static boolean setCurrentUser(UserDTO user) {
		boolean set = false;
		if (user != null) {
			try {
				if (SecurityContextHolder.getContext().getAuthentication() != null && SecurityContextHolder.getContext().getAuthentication().getPrincipal() != null
				    && SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof CustomUserDetails) {
					((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).setUser(user);
					set = true;
				}
			} catch (Exception e) {
				log.warn("Error setting current authendicated User - Message: " + e.getMessage());
			}
		}
		return set;
	}
}
