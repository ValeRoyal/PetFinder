package com.royal.msmatch.patterns.observer;

import com.royal.msmatch.model.Match;

import java.util.ArrayList;
import java.util.List;

public class MatchPublisher {

    private final List<MatchSubscriber> subscribers = new ArrayList<>();

    public void subscribe(MatchSubscriber subscriber) {
        subscribers.add(subscriber);
    }

    public void unsubscribe(MatchSubscriber subscriber) {
        subscribers.remove(subscriber);
    }

    public void publish(Match match) {
        subscribers.forEach(subscriber -> subscriber.update(match));
    }
}
