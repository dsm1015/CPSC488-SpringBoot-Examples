package edu.sru.cpsc.webshopping.controller;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.RestController;

import edu.sru.cpsc.webshopping.domain.user.User;
import jakarta.servlet.ServletContext;

@RestController
public class EmailController {

  @Value("${server.port}")
  private String port;

  @Autowired
  private ServletContext servletContext;

  @Bean
  public JavaMailSender getJavaMailSender() {
    JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
    mailSender.setHost("smtp.gmail.com");
    mailSender.setPort(587);

    mailSender.setUsername("worldofwidgetsinc@gmail.com");
    mailSender.setPassword("zxfeppgfuibrisay");

    Properties props = mailSender.getJavaMailProperties();
    props.put("mail.transport.protocol", "smtp");
    props.put("mail.smtp.auth", "true");
    props.put("mail.smtp.starttls.enable", "true");
    props.put("mail.debug", "true");
    props.put("mail.smtp.ssl.trust", "smtp.gmail.com");

    return mailSender;
  }

  @Async
  public void sendTwoFactorAuthEmail(User user, String code) {
    SimpleMailMessage message = new SimpleMailMessage();
    message.setTo(user.getEmail());
    message.setSubject("<no-reply> Two Factor Authentication");
    message.setText(
        "Your two factor authentication code is: "
            + code
            + "\n"
            + "If you have received this email in error please contact us immediately.");
    this.getJavaMailSender().send(message);
  }

  public void verificationEmail(User recipient, String code) {
    User user = recipient;
    user.setEmailVerification(code);
    SimpleMailMessage message = new SimpleMailMessage();
    message.setTo(user.getEmail());
    message.setSubject("<no-reply> Welcome!");

    String verificationLink = String.format("%s/emailverification", servletContext.getContextPath());
    try {
      message.setText(
          String.format(
              "Hello %s please use the following link http://%s:%s%s to verify your account with code:\n%s",
              user.getUsername(), InetAddress.getLocalHost().getHostAddress(), port, verificationLink, code));
    } catch (UnknownHostException e) {
      throw new RuntimeException(e);
    }
    this.getJavaMailSender().send(message);
  }

  public void usernameRecovery(User user) {
    SimpleMailMessage message = new SimpleMailMessage();
    message.setTo(user.getEmail());
    message.setSubject("<no-reply> Username Recovery");
    message.setText(
        "Your username is: "
            + user.getUsername()
            + "\n"
            + "If you have received this email in error please contact us immediately.");
    this.getJavaMailSender().send(message);
  }

}
