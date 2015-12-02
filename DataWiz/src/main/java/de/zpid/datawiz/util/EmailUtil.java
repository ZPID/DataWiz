package de.zpid.datawiz.util;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class EmailUtil {

  public static void main(String[] args) {
    System.out.println("test");
    try {
      sendSSLMail("robo0001@stud.hs-kl.de", "lol", "");
    } catch (MessagingException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  public static void sendSSLMail(String recipient, String subject, String text)
      throws AddressException, MessagingException {
    Message message = new MimeMessage(getConnection());
    message.setFrom(new InternetAddress("datawiz@zpid.de"));
    message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient));
    message.setSubject(subject);
    message.setContent(text, "text/html; charset=utf-8");
    Transport.send(message);
  }

  private static Session getConnection() {
    Properties props = new Properties();
    props.put("mail.smtp.host", "mail.zpid.de");
    props.put("mail.smtp.starttls.enable", "true");
    props.put("mail.smtp.auth", "true");
    props.put("mail.smtp.port", "587");
    Session session = Session.getDefaultInstance(props, new javax.mail.Authenticator() {
      protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication("boelter@zpid.de", "Dp9chatr;");
      }
    });
    return session;
  }
}
