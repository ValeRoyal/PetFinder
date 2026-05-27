package com.royal.msnotification.patterns.iterator;

import com.royal.msnotification.model.Notification;
import com.royal.msnotification.model.enums.NotificationStatus;

import java.util.List;

public class PendingNotificationIterator implements NotificationIterator {

    private final List<Notification> notifications;
    private int position;

    public PendingNotificationIterator(List<Notification> notifications) {
        this.notifications = notifications == null ? List.of() : notifications;
    }

    @Override
    public boolean hasNext() {
        while (position < notifications.size()) {
            if (notifications.get(position).getStatus() == NotificationStatus.PENDING) {
                return true;
            }
            position++;
        }
        return false;
    }

    @Override
    public Notification next() {
        if (!hasNext()) {
            throw new IllegalStateException("No more pending notifications");
        }
        return notifications.get(position++);
    }
}
