package com.royal.msmatch.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "petfinder.match")
public record MatchingProperties(double minimumScore) {
}
