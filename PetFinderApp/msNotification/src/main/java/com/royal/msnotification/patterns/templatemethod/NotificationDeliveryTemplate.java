package com.royal.msnotification.patterns.templatemethod;

import com.royal.msnotification.model.Notification;
import com.royal.msnotification.patterns.strategy.NotificationDeliveryStrategy;

public abstract class NotificationDeliveryTemplate implements NotificationDeliveryStrategy {

    @Override
    public final boolean deliver(Notification notification) {
        if (!canDeliver(notification)) {
            return false;
        }
        return doDeliver(notification);
    }

    protected abstract boolean canDeliver(Notification notification);

    protected abstract boolean doDeliver(Notification notification);
}
