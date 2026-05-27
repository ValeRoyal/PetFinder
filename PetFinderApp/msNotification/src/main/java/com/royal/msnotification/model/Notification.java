package com.royal.msnotification.model;

import com.royal.msnotification.model.enums.NotificationChannel;
import com.royal.msnotification.model.enums.NotificationStatus;
import com.royal.msnotification.model.enums.NotificationType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Entity
@Table(name = "notification")
@Data
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Notification {

    @Id
    @Column(name = "id", nullable = false, unique = true, length = 50)
    String id;

    @Column(name = "recipient_id", nullable = false, length = 50)
    String recipientId;

    @Column(name = "recipient_email", length = 120)
    String recipientEmail;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 40)
    NotificationType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "channel", nullable = false, length = 20)
    NotificationChannel channel;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    NotificationStatus status = NotificationStatus.PENDING;

    @Column(name = "subject", nullable = false, length = 150)
    String subject;

    @Column(name = "content", nullable = false, length = 1000)
    String content;

    @Column(name = "source_service", nullable = false, length = 50)
    String sourceService;

    @Column(name = "source_event_id", length = 50)
    String sourceEventId;

    @Column(name = "created_at", nullable = false)
    LocalDateTime createdAt;

    @Column(name = "sent_at")
    LocalDateTime sentAt;

    @Column(name = "read_at")
    LocalDateTime readAt;
}
