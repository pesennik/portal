package com.github.pesennik.util;

import com.github.pesennik.Context;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.util.Properties;

public class MailClient {

    private static final Logger log = LoggerFactory.getLogger(MailClient.class);

    public static final String SUPPORT = "support@pesennik.online";

    /**
     * Configuration for Amazon Simple Mail Service.
     */
    private static final String SES_USER = Context.getProdConfig().getProperty("ses.user");
    private static final String SES_PASSWORD = Context.getProdConfig().getProperty("ses.password");
    private static final String SES_HOST = Context.getProdConfig().getProperty("ses.host", "email-smtp.us-east-1.amazonaws.com");
    private static final int SES_PORT = Integer.parseInt(Context.getProdConfig().getProperty("ses.port", "587"));

    public static void sendMail(@NotNull String toEmail, @NotNull String subject, @NotNull String body) throws IOException, MessagingException {
        if (!Context.isProduction()) {
            log.info("EMAIL to: " + toEmail + "" +
                    "\nsubj: " + subject
                    + "\n-----"
                    + body
                    + "\n-----"
            );
            return;
        }
        sendBySes(SUPPORT, toEmail, subject, body);
    }

    private static synchronized void sendBySes(@NotNull String from, @NotNull String to, @NotNull String subject, @NotNull String body) throws MessagingException {
        if (!Context.isProduction()) {
            return;
        }
        log.debug("sendMail[SES]: " + to + " subject:" + subject);
        Properties props = new Properties();
        props.putAll(System.getProperties());
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.port", SES_PORT);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.starttls.required", "true");

        Session session = Session.getInstance(props);
        MimeMessage msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(from));
        msg.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
        msg.setSubject(subject, "UTF-8");
        msg.setContent(body, "text/plain; charset=UTF-8");

        Transport transport = session.getTransport();
        try {
            transport.connect(SES_HOST, SES_PORT, SES_USER, SES_PASSWORD);
            transport.sendMessage(msg, msg.getAllRecipients());
        } finally {
            transport.close();
        }
    }
}
