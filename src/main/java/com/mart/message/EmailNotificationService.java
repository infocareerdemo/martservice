package com.mart.message;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import jakarta.mail.Message;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMessage.RecipientType;

@Service
public class EmailNotificationService {
	
	@Autowired
	Environment env;

	public void sendOtpToMail(String emailId, Long otp) throws Exception {

		String toMailAddress = emailId;

		String mailSubject = env.getProperty("otpSubject");
		String mailContent = env.getProperty("otpContent");

		String modifiedContent = mailContent.replace("[otp]", String.valueOf(otp));

		sendMail(toMailAddress, mailSubject, modifiedContent);
	}
	
	@Async("threadPoolTaskExecutor")
	public String sendMail(String toMailAddress, String subject, String content) throws Exception {

		Message message = new MimeMessage(getSession());

		String sendMail = env.getProperty("sendMail");
		if (sendMail == null || !sendMail.equalsIgnoreCase("yes")) {
			System.out.println("MAIL IGNORED");
			return "SENT";
		} else {
			message.addRecipient(RecipientType.TO, new InternetAddress(toMailAddress));
			String fromAddress = "";
			fromAddress = env.getProperty("fromAddress");
			message.addFrom(new InternetAddress[] { new InternetAddress(fromAddress) });
			message.setSubject(subject);
			message.setContent(content, "text/html");
			Transport.send(message);

			System.out.println("SENT");

			return "SENT";
		}
	}

	private Session getSession() throws Exception {

		String hostName = "";
		String portNumber = "";

		hostName = env.getProperty("spring.mail.host");
		portNumber = env.getProperty("spring.mail.port");

		System.out.println("----> Host Name " + hostName);
		System.out.println("----> Port Number " + portNumber);

		Authenticator authenticator = new Authenticator();

		Properties properties = new Properties();
		properties.setProperty("mail.smtp.submitter", authenticator.getPasswordAuthentication().getUserName());
		properties.setProperty("mail.smtp.auth", "true");
		properties.setProperty("mail.smtp.host", hostName);
		properties.setProperty("mail.smtp.port", portNumber);
		properties.setProperty("mail.smtp.ssl.protocols", "TLSv1.2");
		properties.setProperty("mail.smtp.starttls.enable", "true");

		return Session.getInstance(properties, authenticator);
	}

	private class Authenticator extends jakarta.mail.Authenticator {
		private PasswordAuthentication authentication;

		public Authenticator() throws Exception {
			String fromEmail = env.getProperty("spring.mail.username");
			String fromEmailPwd = env.getProperty("spring.mail.password");
			authentication = new PasswordAuthentication(fromEmail, fromEmailPwd);
		}

		protected PasswordAuthentication getPasswordAuthentication() {
			return authentication;
		}
	}

}
