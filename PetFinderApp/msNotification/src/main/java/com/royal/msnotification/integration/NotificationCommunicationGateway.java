package com.royal.msnotification.integration;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class NotificationCommunicationGateway {

    private final WebClient matchWebClient;
    private final WebClient adopterWebClient;
    private final WebClient shelterWebClient;
    private final WebClient vetWebClient;

    public NotificationCommunicationGateway(
            @Qualifier("matchWebClient") WebClient matchWebClient,
            @Qualifier("adopterWebClient") WebClient adopterWebClient,
            @Qualifier("shelterWebClient") WebClient shelterWebClient,
            @Qualifier("vetWebClient") WebClient vetWebClient
    ) {
        this.matchWebClient = matchWebClient;
        this.adopterWebClient = adopterWebClient;
        this.shelterWebClient = shelterWebClient;
        this.vetWebClient = vetWebClient;
    }

    public WebClient matchClient() {
        return matchWebClient;
    }

    public WebClient adopterClient() {
        return adopterWebClient;
    }

    public WebClient shelterClient() {
        return shelterWebClient;
    }

    public WebClient vetClient() {
        return vetWebClient;
    }
}
