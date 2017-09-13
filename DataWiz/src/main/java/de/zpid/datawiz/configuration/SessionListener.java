package de.zpid.datawiz.configuration;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SessionListener implements HttpSessionListener {

	private static Logger log = LogManager.getLogger(SessionListener.class);

	private int timeout;

	public SessionListener(String timeout) {
		super();
		try {
			this.timeout = Integer.parseInt(timeout.trim());
			log.info("Application session is set to {} seconds", () -> this.timeout);
		} catch (Exception e) {
			this.timeout = 30 * 60;
			log.warn("Application session is set to default setting of {} seconds", () -> this.timeout);
		}
	}

	@Override
	public void sessionCreated(HttpSessionEvent event) {
		log.debug("new Session is created at {} with id {}", () -> event.getSession().getCreationTime(), () -> event.getSession().getId());
		event.getSession().setMaxInactiveInterval(this.timeout);
	}

	@Override
	public void sessionDestroyed(HttpSessionEvent event) {
		log.debug("Session created at {} was destroyed", () -> event.getSession().getCreationTime());
	}
}
