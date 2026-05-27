package com.royal.msnotification.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "petfinder.mail")
public record MailProperties(String from) {
}
