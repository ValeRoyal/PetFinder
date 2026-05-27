package com.royal.msnotification.config;

import com.royal.msnotification.patterns.strategy.EmailNotificationStrategy;
import com.royal.msnotification.patterns.strategy.InAppNotificationStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;

@Configuration
public class NotificationStrategyConfig {

    @Bean
    public EmailNotificationStrategy emailNotificationStrategy(
            JavaMailSender mailSender,
            MailProperties mailProperties
    ) {
        return new EmailNotificationStrategy(mailSender, mailProperties);
    }

    @Bean
    public InAppNotificationStrategy inAppNotificationStrategy() {
        return new InAppNotificationStrategy();
    }
}
