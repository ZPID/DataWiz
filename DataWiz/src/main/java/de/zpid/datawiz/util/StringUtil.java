package de.zpid.datawiz.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.zpid.datawiz.dto.UserDTO;

@Component
public class StringUtil {

	private final int FILENAMESIZE = 50;

	private static Logger log = LogManager.getLogger(StringUtil.class);

	/**
	 * This function tries to set the name of an admin if the needed fiels are set, otherwise it return the email of the admin
	 * 
	 * @param user
	 * @return
	 */
	public String createUserNameString(final UserDTO user) {
		if (user != null) {
			log.trace("Entering createUserNameString for user [id: {}]", () -> user.getEmail());
			if (user.getLastName() != null && !user.getLastName().isEmpty() && user.getFirstName() != null && !user.getFirstName().isEmpty())
				return (user.getTitle() != null && !user.getTitle().isEmpty() ? user.getTitle() + " " : "") + user.getFirstName() + " " + user.getLastName() + "("
				    + user.getEmail() + ")";
			else
				return user.getEmail();
		}
		return null;
	}

	public String formatFilename(String s) {
		if (s != null) {
			if (s.length() > FILENAMESIZE) {
				s = s.substring(0, FILENAMESIZE - 1);
			}
			s = s.trim();
			return s.replace("\u00fc", "ue").replace("\u00f6", "oe").replace("\u00e4", "ae").replace("\u00df", "ss")
			    .replaceAll("\u00dc(?=[a-z\u00e4\u00f6\u00fc\u00df ])", "Ue").replaceAll("\u00d6(?=[a-z\u00e4\u00f6\u00fc\u00df ])", "Oe")
			    .replaceAll("\u00c4(?=[a-z\u00e4\u00f6\u00fc\u00df ])", "Ae").replace("\u00dc", "UE").replace("\u00d6", "OE").replace("\u00c4", "AE")
			    .replaceAll("[^a-zA-Z0-9]", " ");
		}
		return null;
	}

	/**
	 * This function removes all line-break and tabulator commands from the passed String.
	 * 
	 * @param s
	 *          String, which has to be cleaned
	 * @return Cleaned String
	 */
	public String removeLineBreaksAndTabsString(String s) {
		return s.replaceAll("(\\r|\\n|\\t)", " ").replaceAll("\"", "'");
	}

}
