package com.royal.msnotification.patterns.observer;

import com.royal.msnotification.model.Notification;
import com.royal.msnotification.model.NotificationEvent;
import com.royal.msnotification.model.enums.NotificationType;

public class LoginNotificationSubscriber extends AbstractNotificationSubscriber {

    @Override
    public boolean supports(NotificationEvent event) {
        return event.type() == NotificationType.LOGIN;
    }

    @Override
    public Notification update(NotificationEvent event) {
        return createNotification(event, "Login detected", "A login was registered in your PetFinder account.");
    }
}
