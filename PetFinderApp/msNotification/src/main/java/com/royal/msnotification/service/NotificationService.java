package com.royal.msnotification.service;

import com.royal.msnotification.dto.request.NotificationEventRequestDTO;
import com.royal.msnotification.dto.request.NotificationRequestDTO;
import com.royal.msnotification.dto.response.NotificationResponseDTO;
import com.royal.msnotification.integration.NotificationCommunicationGateway;
import com.royal.msnotification.model.Notification;
import com.royal.msnotification.model.NotificationEvent;
import com.royal.msnotification.model.enums.NotificationChannel;
import com.royal.msnotification.model.enums.NotificationStatus;
import com.royal.msnotification.patterns.iterator.NotificationIterator;
import com.royal.msnotification.patterns.iterator.PendingNotificationCollection;
import com.royal.msnotification.patterns.observer.AdoptionNotificationSubscriber;
import com.royal.msnotification.patterns.observer.LoginNotificationSubscriber;
import com.royal.msnotification.patterns.observer.MatchNotificationSubscriber;
import com.royal.msnotification.patterns.observer.MedicalEventNotificationSubscriber;
import com.royal.msnotification.patterns.observer.NotificationPublisher;
import com.royal.msnotification.patterns.observer.VetNotificationSubscriber;
import com.royal.msnotification.patterns.strategy.EmailNotificationStrategy;
import com.royal.msnotification.patterns.strategy.InAppNotificationStrategy;
import com.royal.msnotification.patterns.strategy.NotificationDeliveryClient;
import com.royal.msnotification.repository.NotificationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationService {

    private final NotificationRepository repository;
    private final NotificationCommunicationGateway communicationGateway;
    private final NotificationPublisher publisher;
    private final NotificationDeliveryClient deliveryClient;

    public NotificationService(
            NotificationRepository repository,
            NotificationCommunicationGateway communicationGateway,
            EmailNotificationStrategy emailNotificationStrategy,
            InAppNotificationStrategy inAppNotificationStrategy
    ) {
        this.repository = repository;
        this.communicationGateway = communicationGateway;
        this.publisher = new NotificationPublisher();
        this.deliveryClient = new NotificationDeliveryClient(emailNotificationStrategy, inAppNotificationStrategy);
        subscribeDefaultHandlers();
    }

    @Transactional
    public NotificationResponseDTO createDirect(NotificationRequestDTO request) {
        String id = requireId(request.id(), "Notification id is required");
        if (repository.existsById(id)) {
            throw new IllegalArgumentException("Notification id already exists: " + id);
        }
        Notification notification = new Notification();
        notification.setId(id);
        notification.setRecipientId(request.recipientId());
        notification.setRecipientEmail(request.recipientEmail());
        notification.setType(request.type());
        notification.setChannel(resolveChannel(request.channel()));
        notification.setStatus(NotificationStatus.PENDING);
        notification.setSubject(request.subject());
        notification.setContent(request.content());
        notification.setSourceService("msNotification");
        notification.setCreatedAt(LocalDateTime.now());
        return toResponse(repository.save(notification));
    }

    @Transactional
    public List<NotificationResponseDTO> publishEvent(NotificationEventRequestDTO request) {
        String id = requireId(request.id(), "Notification id is required");
        if (repository.existsById(id)) {
            throw new IllegalArgumentException("Notification id already exists: " + id);
        }
        NotificationEvent event = new NotificationEvent(
                id,
                request.sourceService(),
                request.sourceEventId(),
                request.recipientId(),
                request.recipientEmail(),
                request.type(),
                resolveChannel(request.channel()),
                request.subject(),
                request.content()
        );

        return publisher.publish(event).stream()
                .map(repository::save)
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public List<NotificationResponseDTO> processPending() {
        List<Notification> pending = repository.findByStatusOrderByCreatedAtAsc(NotificationStatus.PENDING);
        PendingNotificationCollection collection = new PendingNotificationCollection(pending);
        NotificationIterator iterator = collection.createIterator();

        while (iterator.hasNext()) {
            Notification notification = iterator.next();
            boolean delivered = deliveryClient.deliver(notification);
            notification.setStatus(delivered ? NotificationStatus.SENT : NotificationStatus.FAILED);
            if (delivered) {
                notification.setSentAt(LocalDateTime.now());
            }
            repository.save(notification);
        }

        return pending.stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<NotificationResponseDTO> findAll() {
        return repository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<NotificationResponseDTO> findByRecipient(String recipientId) {
        return repository.findByRecipientIdOrderByCreatedAtDesc(recipientId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public NotificationResponseDTO markAsRead(String id) {
        Notification notification = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Notification not found: " + id));
        notification.setStatus(NotificationStatus.READ);
        notification.setReadAt(LocalDateTime.now());
        return toResponse(repository.save(notification));
    }

    public String communicationSummary() {
        return "NotificationCommunicationGateway ready: "
                + communicationGateway.matchClient()
                + ", "
                + communicationGateway.adopterClient()
                + ", "
                + communicationGateway.shelterClient()
                + ", "
                + communicationGateway.vetClient();
    }

    private void subscribeDefaultHandlers() {
        publisher.subscribe(new MatchNotificationSubscriber());
        publisher.subscribe(new LoginNotificationSubscriber());
        publisher.subscribe(new VetNotificationSubscriber());
        publisher.subscribe(new MedicalEventNotificationSubscriber());
        publisher.subscribe(new AdoptionNotificationSubscriber());
    }

    private NotificationChannel resolveChannel(NotificationChannel channel) {
        return channel == null ? NotificationChannel.IN_APP : channel;
    }

    private String requireId(String id, String message) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException(message);
        }
        return id;
    }

    private NotificationResponseDTO toResponse(Notification notification) {
        return new NotificationResponseDTO(
                notification.getId(),
                notification.getRecipientId(),
                notification.getRecipientEmail(),
                notification.getType(),
                notification.getChannel(),
                notification.getStatus(),
                notification.getSubject(),
                notification.getContent(),
                notification.getSourceService(),
                notification.getSourceEventId(),
                notification.getCreatedAt(),
                notification.getSentAt(),
                notification.getReadAt()
        );
    }
}
