package com.royal.msnotification.dto.request;

import com.royal.msnotification.model.enums.NotificationChannel;
import com.royal.msnotification.model.enums.NotificationType;

public record NotificationEventRequestDTO(
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
