package de.zpid.datawiz.configuration;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Customized Session Listener
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
public class SessionListener implements HttpSessionListener {

    private static Logger log = LogManager.getLogger(SessionListener.class);

    private int timeout;

    SessionListener(String timeout) {
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
