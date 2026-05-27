package com.royal.msnotification.patterns.iterator;

import com.royal.msnotification.model.Notification;

import java.util.List;

public class PendingNotificationCollection implements NotificationIterableCollection {

    private final List<Notification> notifications;

    public PendingNotificationCollection(List<Notification> notifications) {
        this.notifications = notifications == null ? List.of() : List.copyOf(notifications);
    }

    @Override
    public NotificationIterator createIterator() {
        return new PendingNotificationIterator(notifications);
    }
}
