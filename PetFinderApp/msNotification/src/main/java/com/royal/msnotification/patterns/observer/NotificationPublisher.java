package com.royal.msnotification.patterns.observer;

import com.royal.msnotification.model.Notification;
import com.royal.msnotification.model.NotificationEvent;

import java.util.ArrayList;
import java.util.List;

public class NotificationPublisher {

    private final List<NotificationSubscriber> subscribers = new ArrayList<>();

    public void subscribe(NotificationSubscriber subscriber) {
        subscribers.add(subscriber);
    }

    public void unsubscribe(NotificationSubscriber subscriber) {
        subscribers.remove(subscriber);
    }

    public List<Notification> publish(NotificationEvent event) {
        return subscribers.stream()
                .filter(subscriber -> subscriber.supports(event))
                .map(subscriber -> subscriber.update(event))
                .toList();
    }
}
