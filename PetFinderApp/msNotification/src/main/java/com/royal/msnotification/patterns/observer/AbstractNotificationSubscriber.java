package com.royal.msnotification.patterns.observer;

import com.royal.msnotification.model.Notification;
import com.royal.msnotification.model.NotificationEvent;
import com.royal.msnotification.model.enums.NotificationStatus;

import java.time.LocalDateTime;

public abstract class AbstractNotificationSubscriber implements NotificationSubscriber {

    protected Notification createNotification(NotificationEvent event, String fallbackSubject, String fallbackContent) {
        Notification notification = new Notification();
        notification.setId(event.id());
        notification.setRecipientId(event.recipientId());
        notification.setRecipientEmail(event.recipientEmail());
        notification.setType(event.type());
        notification.setChannel(event.channel());
        notification.setStatus(NotificationStatus.PENDING);
        notification.setSubject(resolve(event.subject(), fallbackSubject));
        notification.setContent(resolve(event.content(), fallbackContent));
        notification.setSourceService(event.sourceService());
        notification.setSourceEventId(event.sourceEventId());
        notification.setCreatedAt(LocalDateTime.now());
        return notification;
    }

    private String resolve(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value;
    }
}
