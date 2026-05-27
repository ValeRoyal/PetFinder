package com.royal.msnotification.patterns.observer;

import com.royal.msnotification.model.Notification;
import com.royal.msnotification.model.NotificationEvent;
import com.royal.msnotification.model.enums.NotificationType;

public class VetNotificationSubscriber extends AbstractNotificationSubscriber {

    @Override
    public boolean supports(NotificationEvent event) {
        return event.type() == NotificationType.VET_ASSIGNED;
    }

    @Override
    public Notification update(NotificationEvent event) {
        return createNotification(event, "Veterinarian assigned", "A veterinarian was assigned for follow-up.");
    }
}
