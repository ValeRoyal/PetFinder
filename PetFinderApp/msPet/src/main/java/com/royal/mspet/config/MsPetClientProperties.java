package com.royal.mspet.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "petfinder.clients")
public record MsPetClientProperties(Service vet) {

    public record Service(
            String baseUrl,
            Integer connectTimeoutMillis,
            Integer responseTimeoutMillis
    ) {
    }
}
