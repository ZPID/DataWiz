package de.zpid.datawiz.service;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import de.zpid.datawiz.dao.UserDAO;
import de.zpid.datawiz.util.CustomUserDetails;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;
import java.time.LocalDateTime;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;


/**
 * Service class for login attempt security.
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
 * @version 1.1.0
 **/
@Service
public class LoginAttemptService implements ApplicationListener {

    private static Logger log = LogManager.getLogger(LoginAttemptService.class);
    @SuppressWarnings("FieldCanBeLocal")
    private final int MAX_ATTEMPT = 5;
    private LoadingCache<String, Integer> attemptsCache;
    private UserDAO userDAO;


    @Autowired
    public LoginAttemptService(final UserDAO userDAO) {
        super();
        this.userDAO = userDAO;
        attemptsCache = CacheBuilder.newBuilder().
                expireAfterWrite(15, TimeUnit.MINUTES).build(new CacheLoader<>() {
            public Integer load(@Nonnull String key) {
                return 0;
            }
        });
    }

    /**
     * This function is called by different Application Events and is used to count the login attempts and to block users for a specific time if MAX_ATTEMPT is exceeded.
     *
     * @param event {@link ApplicationEvent}
     */
    @Override
    public void onApplicationEvent(@Nonnull ApplicationEvent event) {
        if (event instanceof AuthenticationSuccessEvent) {
            Object principal = ((AuthenticationSuccessEvent) event).getAuthentication().getPrincipal();
            String username = ((CustomUserDetails) principal).getUsername();
            log.debug("Successful login for user [{}]", () -> username);
            long uid = ((CustomUserDetails) principal).getUser().getId();
            loginSucceeded(username);
            userDAO.updateLastLogin(uid, LocalDateTime.now());
        } else if (event instanceof AuthenticationFailureBadCredentialsEvent) {
            Object principal = ((AuthenticationFailureBadCredentialsEvent) event).getAuthentication().getPrincipal();
            log.debug("Failed login for user [{}]", principal::toString);
            loginFailed(principal.toString());
        }
    }

    /**
     * Deletes a user from the "login attempt" cache.
     *
     * @param key {@link String} username (primary mail)
     */
    private void loginSucceeded(String key) {
        attemptsCache.invalidate(key);
    }

    /**
     * Adds a user to the "login attempt" cache, or increases the number of attempt is the username is already in the cache.
     *
     * @param key {@link String} username (primary mail)
     */
    private void loginFailed(String key) {
        int attempts;
        try {
            attempts = attemptsCache.get(key);
        } catch (ExecutionException e) {
            attempts = 0;
        }
        attempts++;
        attemptsCache.put(key, attempts);
    }

    /**
     * Checks if the number of current attempt is greater than MAX_ATTEMPT
     *
     * @param key {@link String} username (primary mail)
     * @return true is current attempt is greater than MAX_ATTEMPT, otherwise false
     */
    boolean isBlocked(String key) {
        try {
            return attemptsCache.get(key) >= MAX_ATTEMPT;
        } catch (ExecutionException e) {
            return false;
        }
    }
}
