package com.royal.msnotification.patterns.observer;

import com.royal.msnotification.model.Notification;
import com.royal.msnotification.model.NotificationEvent;
import com.royal.msnotification.model.enums.NotificationType;

public class MatchNotificationSubscriber extends AbstractNotificationSubscriber {

    @Override
    public boolean supports(NotificationEvent event) {
        return event.type() == NotificationType.MATCH_CREATED;
    }

    @Override
    public Notification update(NotificationEvent event) {
        return createNotification(event, "New match in PetFinder", "You have a new mutual match.");
    }
}
