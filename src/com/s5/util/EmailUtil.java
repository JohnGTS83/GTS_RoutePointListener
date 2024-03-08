package com.s5.util;

import java.util.Properties;

import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import org.apache.commons.lang3.StringUtils;

public class EmailUtil {
	
	public static boolean sendEmail(String subject,String bodyText,String toEmail) {
		boolean flag = true;
		if(StringUtils.isNotEmpty(toEmail)) {
			try{
				Properties props = new Properties();
				props.put("mail.transport.protocol", "smtp");
		    	props.put("mail.smtp.port", "587"); 
		    	props.put("mail.smtp.starttls.enable", "true");
		    	props.put("mail.smtp.auth", "true");
		    	props.put("mail.smtp.ssl.protocols", "TLSv1.2");
		    	
				String mailToBox = StringUtils.trimToEmpty(toEmail);
				String mailFromBox = StringUtils.trimToEmpty("Genuine Tracking<support@genuinetrackingsolutions.com>");

				Session session = Session.getDefaultInstance(props);
				session.setDebug(false);
				Message message = new MimeMessage(session);
				message.setFrom(new InternetAddress(mailFromBox));
				message.setRecipients(Message.RecipientType.TO,InternetAddress.parse(mailToBox));
				Multipart multipart = new MimeMultipart();
				BodyPart messageBodyPart = new MimeBodyPart();
				messageBodyPart.setContent(bodyText, "text/html");  
		        multipart.addBodyPart(messageBodyPart);
				message.setSubject(subject);
				message.setContent(multipart, "text/html" );			
				Transport transport = session.getTransport();
				try {
			        transport.connect("email-smtp.us-east-1.amazonaws.com" , "AKIAIX5LBTQRZ2TFW2XQ","BOtK3vM98DltCcGxrnnOhJyKr3PXqFPJyN5SNUwHQYk6");
			        transport.sendMessage(message, message.getAllRecipients());
			    } catch (MessagingException mex) {
			    	mex.printStackTrace();
			    	flag = false;
			    } finally {
			    	transport.close();
			    }
			} catch(Exception e) {
				flag = false;
				e.printStackTrace();
			}
		}
		return flag;
	}
}