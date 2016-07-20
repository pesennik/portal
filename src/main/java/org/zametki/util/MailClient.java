package org.zametki.util;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class MailClient {
    private static final Logger log = LoggerFactory.getLogger(MailClient.class);

    public static void sendMail(@NotNull String toEmail, @NotNull String subject, @NotNull String body) throws IOException{
        log.info("EMAIL to: " + toEmail + "" +
                        "\nsubj: " + subject
                        + "\n-----"
                        + body
                        + "\n-----"
        );
    }
}
