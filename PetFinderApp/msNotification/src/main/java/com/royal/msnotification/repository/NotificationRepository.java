package com.royal.msnotification.repository;

import com.royal.msnotification.model.Notification;
import com.royal.msnotification.model.enums.NotificationStatus;
import com.royal.msnotification.model.enums.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, String> {

    List<Notification> findByRecipientIdOrderByCreatedAtDesc(String recipientId);

    List<Notification> findByStatusOrderByCreatedAtAsc(NotificationStatus status);

    List<Notification> findByType(NotificationType type);
}
