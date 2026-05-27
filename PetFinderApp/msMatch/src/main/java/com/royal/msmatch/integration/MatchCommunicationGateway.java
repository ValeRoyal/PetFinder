package com.royal.msmatch.integration;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class MatchCommunicationGateway {

    private final WebClient petWebClient;
    private final WebClient adopterWebClient;
    private final WebClient notificationWebClient;
    private final WebClient shelterWebClient;

    public MatchCommunicationGateway(
            @Qualifier("petWebClient") WebClient petWebClient,
            @Qualifier("adopterWebClient") WebClient adopterWebClient,
            @Qualifier("notificationWebClient") WebClient notificationWebClient,
            @Qualifier("shelterWebClient") WebClient shelterWebClient
    ) {
        this.petWebClient = petWebClient;
        this.adopterWebClient = adopterWebClient;
        this.notificationWebClient = notificationWebClient;
        this.shelterWebClient = shelterWebClient;
    }

    public WebClient petClient() {
        return petWebClient;
    }

    public WebClient adopterClient() {
        return adopterWebClient;
    }

    public WebClient notificationClient() {
        return notificationWebClient;
    }

    public WebClient shelterClient() {
        return shelterWebClient;
    }
}
