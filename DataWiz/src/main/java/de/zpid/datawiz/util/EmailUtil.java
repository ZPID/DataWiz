package de.zpid.datawiz.util;

import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Properties;

@Component
public class EmailUtil {


    private Environment env;

    @Autowired
    public EmailUtil(Environment env) {
        super();
        this.env = env;
    }

    public void sendSSLMail(String recipient, String subject, String text) throws Exception {
        Properties props = new Properties();
        props.put("mail.smtp.starttls.enable", env.getRequiredProperty("mail.smtp.starttls.enable"));
        props.put("mail.smtp.auth", env.getRequiredProperty("mail.smtp.auth"));
        props.put("mail.smtp.host", env.getRequiredProperty("mail.smtp.host"));
        props.put("mail.smtp.port", Integer.parseInt(env.getRequiredProperty("mail.smtp.port")));
        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(env.getRequiredProperty("mail.smtp.username"), env.getRequiredProperty("mail.smtp.password"));
                    }
                });
        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress(env.getRequiredProperty("mail.set.from")));
        message.addRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient));
        message.setSubject(subject);
        message.setContent(text, env.getRequiredProperty("mail.set.content"));
        Transport t = session.getTransport();
        t.send(message);
        t.close();
    }

    public boolean isFakeMail(final String email) {
        String domain = null;
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
