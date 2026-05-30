package com.royal.msnotification.patterns.observer;

import com.royal.msnotification.model.Notification;
import com.royal.msnotification.model.NotificationEvent;
import com.royal.msnotification.model.enums.NotificationType;

public class GeneralNotificationSubscriber extends AbstractNotificationSubscriber {

    @Override
    public boolean supports(NotificationEvent event) {
        return event.type() == NotificationType.GENERAL;
    }

    @Override
    public Notification update(NotificationEvent event) {
        return createNotification(event, "General update", "There is an update in your PetFinder account.");
    }
}
