package com.royal.msnotification.patterns.strategy;

import com.royal.msnotification.model.Notification;

public class NotificationDeliveryContext {

    private NotificationDeliveryStrategy strategy;

    public void setStrategy(NotificationDeliveryStrategy strategy) {
        this.strategy = strategy;
    }

    public boolean deliver(Notification notification) {
        return strategy.deliver(notification);
    }
}
