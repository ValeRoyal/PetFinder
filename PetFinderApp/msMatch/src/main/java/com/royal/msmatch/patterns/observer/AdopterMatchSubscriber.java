package com.royal.msmatch.patterns.observer;

import com.royal.msmatch.model.Match;

public class AdopterMatchSubscriber implements MatchSubscriber {

    @Override
    public void update(Match match) {
        match.setMessage("Adopter notified about mutual match.");
    }
}
