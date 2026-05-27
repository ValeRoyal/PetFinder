package com.royal.msnotification.model;

import com.royal.msnotification.model.enums.NotificationChannel;
import com.royal.msnotification.model.enums.NotificationType;

public record NotificationEvent(
        String id,
        String sourceService,
        String sourceEventId,
        String recipientId,
        String recipientEmail,
        NotificationType type,
        NotificationChannel channel,
        String subject,
        String content
) {
}
