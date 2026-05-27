package com.royal.msnotification.patterns.strategy;

import com.royal.msnotification.model.Notification;

public class InAppNotificationStrategy implements NotificationDeliveryStrategy {

    @Override
    public boolean deliver(Notification notification) {
        return notification.getRecipientId() != null && !notification.getRecipientId().isBlank();
    }

    @Override
    public String getChannelName() {
        return "IN_APP";
    }
}
