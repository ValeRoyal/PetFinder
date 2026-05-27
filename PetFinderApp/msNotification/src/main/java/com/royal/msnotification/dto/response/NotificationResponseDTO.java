package com.royal.msnotification.dto.response;

import com.royal.msnotification.model.enums.NotificationChannel;
import com.royal.msnotification.model.enums.NotificationStatus;
import com.royal.msnotification.model.enums.NotificationType;

import java.time.LocalDateTime;

public record NotificationResponseDTO(
        String id,
        String recipientId,
        String recipientEmail,
        NotificationType type,
        NotificationChannel channel,
        NotificationStatus status,
        String subject,
        String content,
        String sourceService,
        String sourceEventId,
        LocalDateTime createdAt,
        LocalDateTime sentAt,
        LocalDateTime readAt
) {
}
