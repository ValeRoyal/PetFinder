package com.royal.msshelter.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "petfinder.clients")
public record MsShelterClientProperties(
        Service pet,
        Service adopter,
        Service notification,
        Service vet
) {
    public record Service(
            String baseUrl,
            Integer connectTimeoutMillis,
            Integer responseTimeoutMillis
    ) {
    }
}
