package com.royal.msnotification.patterns.observer;

import com.royal.msnotification.model.Notification;
import com.royal.msnotification.model.NotificationEvent;

public interface NotificationSubscriber {

    boolean supports(NotificationEvent event);

    Notification update(NotificationEvent event);
}
