package com.royal.msmatch.patterns.observer;

import com.royal.msmatch.model.Match;

public interface MatchSubscriber {

    void update(Match match);
}
