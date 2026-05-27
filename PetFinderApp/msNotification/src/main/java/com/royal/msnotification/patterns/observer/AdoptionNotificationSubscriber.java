package com.royal.msnotification.patterns.observer;

import com.royal.msnotification.model.Notification;
import com.royal.msnotification.model.NotificationEvent;
import com.royal.msnotification.model.enums.NotificationType;

import java.util.Set;

public class AdoptionNotificationSubscriber extends AbstractNotificationSubscriber {

    private static final Set<NotificationType> TYPES = Set.of(
            NotificationType.TRIAL_STARTED,
            NotificationType.ADOPTION_APPROVED,
            NotificationType.ADOPTION_REJECTED
    );

    @Override
    public boolean supports(NotificationEvent event) {
        return TYPES.contains(event.type());
    }

    @Override
    public Notification update(NotificationEvent event) {
        return createNotification(event, "Adoption process update", "There is an update in your adoption process.");
    }
}
