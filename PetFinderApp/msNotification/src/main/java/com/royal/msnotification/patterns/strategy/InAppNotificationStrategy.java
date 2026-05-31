package com.royal.msnotification.patterns.strategy;

import com.royal.msnotification.model.Notification;
import com.royal.msnotification.patterns.templatemethod.NotificationDeliveryTemplate;

public class InAppNotificationStrategy extends NotificationDeliveryTemplate {

    @Override
    protected boolean canDeliver(Notification notification) {
        return notification.getRecipientId() != null && !notification.getRecipientId().isBlank();
    }

    @Override
    protected boolean doDeliver(Notification notification) {
        return true;
    }

    @Override
    public String getChannelName() {
        return "IN_APP";
    }
}
