package com.royal.msnotification.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "petfinder.clients")
public record MsNotificationClientProperties(
        Service match,
        Service adopter,
        Service shelter,
        Service vet
) {
    public record Service(
            String baseUrl,
            Integer connectTimeoutMillis,
            Integer responseTimeoutMillis
    ) {
    }
}
