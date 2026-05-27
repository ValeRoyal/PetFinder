package com.royal.msnotification.patterns.strategy;

import com.royal.msnotification.config.MailProperties;
import com.royal.msnotification.model.Notification;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

public class EmailNotificationStrategy implements NotificationDeliveryStrategy {

    private final JavaMailSender mailSender;
    private final MailProperties mailProperties;

    public EmailNotificationStrategy(JavaMailSender mailSender, MailProperties mailProperties) {
        this.mailSender = mailSender;
        this.mailProperties = mailProperties;
    }

    @Override
    public boolean deliver(Notification notification) {
        if (notification.getRecipientEmail() == null || notification.getRecipientEmail().isBlank()) {
            return false;
        }

        try {
            var message = mailSender.createMimeMessage();
            var helper = new MimeMessageHelper(message, "UTF-8");
            helper.setFrom(mailProperties.from());
            helper.setTo(notification.getRecipientEmail());
            helper.setSubject(notification.getSubject());
            helper.setText(notification.getContent(), false);
            mailSender.send(message);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    @Override
    public String getChannelName() {
        return "EMAIL";
    }
}
