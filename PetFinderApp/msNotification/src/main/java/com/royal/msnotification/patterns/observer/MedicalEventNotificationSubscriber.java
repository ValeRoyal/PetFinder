package com.royal.msnotification.patterns.observer;

import com.royal.msnotification.model.Notification;
import com.royal.msnotification.model.NotificationEvent;
import com.royal.msnotification.model.enums.NotificationType;

public class MedicalEventNotificationSubscriber extends AbstractNotificationSubscriber {

    @Override
    public boolean supports(NotificationEvent event) {
        return event.type() == NotificationType.MEDICAL_EVENT;
    }

    @Override
    public Notification update(NotificationEvent event) {
        return createNotification(event, "Medical event update", "A medical event was registered.");
    }
}
