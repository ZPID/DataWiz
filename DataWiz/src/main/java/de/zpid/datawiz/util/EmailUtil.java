package de.zpid.datawiz.util;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.core.env.Environment;

public class EmailUtil {

  private Environment env;

  public EmailUtil(Environment env) {
    super();
    this.env = env;
  }

  public void sendSSLMail(String recipient, String subject, String text) throws Exception {
    Properties props = new Properties();
    props.put("mail.smtp.starttls.enable", env.getRequiredProperty("mail.smtp.starttls.enable"));
    props.put("mail.smtp.auth", env.getRequiredProperty("mail.smtp.auth"));
    props.put("mail.smtp.port", env.getRequiredProperty("mail.smtp.port"));
    Session session = Session.getInstance(props);
    MimeMessage message = new MimeMessage(session);
    message.setFrom(new InternetAddress(env.getRequiredProperty("mail.set.from")));
    message.addRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient));
    message.setSubject(subject);
    message.setContent(text, env.getRequiredProperty("mail.set.content"));
    Transport transport = session.getTransport("smtp");
    transport.connect(env.getRequiredProperty("mail.smtp.host"), env.getRequiredProperty("mail.smtp.username"),
        env.getRequiredProperty("mail.smtp.password"));
    transport.sendMessage(message, message.getAllRecipients());
    transport.close();
  }
}
