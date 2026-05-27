package com.royal.msmatch.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "petfinder.clients")
public record MsMatchClientProperties(
        Service pet,
        Service adopter,
        Service notification,
        Service shelter
) {
    public record Service(
            String baseUrl,
            Integer connectTimeoutMillis,
            Integer responseTimeoutMillis
    ) {
    }
}
