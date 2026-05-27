package com.royal.msmatch.patterns.observer;

import com.royal.msmatch.model.Match;

public class ShelterMatchSubscriber implements MatchSubscriber {

    @Override
    public void update(Match match) {
        String currentMessage = match.getMessage() == null ? "" : match.getMessage() + " ";
        match.setMessage(currentMessage + "Shelter notified about mutual match.");
    }
}
