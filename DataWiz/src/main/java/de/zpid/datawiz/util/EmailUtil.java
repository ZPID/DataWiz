package de.zpid.datawiz.util;

import com.google.gson.Gson;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import javax.mail.internet.MimeMessage;
import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Component
public class EmailUtil {

    private static Logger log = LogManager.getLogger(EmailUtil.class);
    private final Environment env;
    private final JavaMailSender emailSender;
    private final Configuration freemarkerConfig;

    @Autowired
    public EmailUtil(Environment env, JavaMailSender emailSender, Configuration freemarkerConfig) {
        super();
        this.env = env;
        this.emailSender = emailSender;
        this.freemarkerConfig = freemarkerConfig;
    }

    public void sendSSLMail(String recipient, String subject, String text) throws Exception {
        log.debug("Entering sendSSLMail for recipient[{}] with subject[{}]", () -> recipient, () -> subject);
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, StandardCharsets.UTF_8.name());
        Template t = freemarkerConfig.getTemplate("mail_register_template.ftl");
        Map<String, Object> model = new HashMap<>();
        if (!recipient.trim().toLowerCase().equals(env.getRequiredProperty("organisation.admin.email").trim().toLowerCase())) {
            model.put("salutation", "Dear Sir or Madam,");
            model.put("adoption", "Best wishes, <br/>The DataWiz Team");
        } else {
            model.put("salutation", "");
            model.put("adoption", "");
        }
        model.put("text", text);
        helper.setText(FreeMarkerTemplateUtils.processTemplateIntoString(t, model), true);
        helper.setTo(recipient);
        helper.setSubject(subject);
        helper.setFrom(env.getRequiredProperty("mail.set.from"));
        emailSender.send(message);
        log.debug("Leaving sendSSLMail for recipient[{}] with subject[{}]", () -> recipient, () -> subject);
    }


    public boolean isFakeMail(final String email) {
        log.debug("Entering isFakeMail for email[{}]", () -> email);
        String domain;
        try {
            if (email == null || !email.contains("@")) {
                return false;
            } else {
                String[] split = email.split("@");
                if (split.length <= 1)
                    return false;
                domain = split[1];
            }
            StringBuilder sb = new StringBuilder();
            HttpsURLConnection conn = (HttpsURLConnection) new URL(env.getRequiredProperty("trashmail.blacklist.api") + domain).openConnection();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                reader.lines().forEach(sb::append);
            }
            BlackList bl = new Gson().fromJson(sb.toString(), BlackList.class);
            if (bl != null && bl.status != null && bl.status.equals("blacklisted"))
                return true;
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    private class BlackList {
        private String status;
        private String domain;
        private long added;
        private long lastchecked;

        @Override
        public String toString() {
            return "BlackList [status=" + status + ", domain=" + domain + ", added=" + added + ", lastchecked=" + lastchecked + "]";
        }

    }
}
