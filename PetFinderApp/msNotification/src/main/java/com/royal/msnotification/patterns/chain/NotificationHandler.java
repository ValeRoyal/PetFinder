package com.royal.msnotification.patterns.chain;

import com.royal.msnotification.model.Notification;
import com.royal.msnotification.model.NotificationEvent;

public interface NotificationHandler {

    NotificationHandler setNext(NotificationHandler handler);

    Notification handle(NotificationEvent event);
}
