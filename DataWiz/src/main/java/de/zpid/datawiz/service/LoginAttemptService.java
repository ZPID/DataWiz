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

@Service
public class LoginAttemptService implements ApplicationListener {

    private static Logger log = LogManager.getLogger(LoginAttemptService.class);
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

    private void loginSucceeded(String key) {
        attemptsCache.invalidate(key);
    }

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

    boolean isBlocked(String key) {
        try {
            return attemptsCache.get(key) >= MAX_ATTEMPT;
        } catch (ExecutionException e) {
            return false;
        }
    }
}
