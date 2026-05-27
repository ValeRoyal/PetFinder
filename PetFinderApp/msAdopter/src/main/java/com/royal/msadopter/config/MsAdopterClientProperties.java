package com.royal.msadopter.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "petfinder.clients")
public record MsAdopterClientProperties(
        Service pet,
        Service match,
        Service notification
) {
    public record Service(
            String baseUrl,
            Integer connectTimeoutMillis,
            Integer responseTimeoutMillis
    ) {
    }
}
