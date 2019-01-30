package de.zpid.datawiz.util;

import de.zpid.datawiz.enumeration.DataWizErrorCodes;
import de.zpid.datawiz.util.ClientInfo;
import de.zpid.datawiz.util.GoogleResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.util.regex.Pattern;

@Component
public class CaptchaUtil {

    private final HttpServletRequest request;
    private final Environment environment;
    private final ClientInfo clientInfo;
    private final Pattern RESPONSE_PATTERN = Pattern.compile("[A-Za-z0-9_-]+");

    @Autowired
    public CaptchaUtil(HttpServletRequest request, Environment environment, ClientInfo clientInfo) {
        this.request = request;
        this.environment = environment;
        this.clientInfo = clientInfo;
    }

    public DataWizErrorCodes processResponse(String response) {
        if (!(StringUtils.hasLength(response) && RESPONSE_PATTERN.matcher(response).matches())) {
            return DataWizErrorCodes.CAPTCHA_EMPTY;
        } else {
            URI verifyUri = URI.create(String.format("https://www.google.com/recaptcha/api/siteverify?secret=%s&response=%s&remoteip=%s",
                    environment.getProperty("google.recaptcha.key.secret"), response, clientInfo.getClientInfo(request).get("IPAddress")));
            GoogleResponse googleResponse = new RestTemplate().getForObject(verifyUri, GoogleResponse.class);
            if (googleResponse == null || !googleResponse.isSuccess()) {
                return DataWizErrorCodes.CAPTCHA_FAILURE;
            }
            return DataWizErrorCodes.CAPTCHA_OK;
        }
    }
}
