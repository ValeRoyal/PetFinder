package com.royal.msnotification.patterns.strategy;

import com.royal.msnotification.model.Notification;

public interface NotificationDeliveryStrategy {

    boolean deliver(Notification notification);

    String getChannelName();
}
