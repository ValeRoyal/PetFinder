package com.royal.msnotification.controller;

import com.royal.msnotification.dto.request.NotificationEventRequestDTO;
import com.royal.msnotification.dto.request.NotificationRequestDTO;
import com.royal.msnotification.dto.response.NotificationResponseDTO;
import com.royal.msnotification.service.NotificationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationRestController {

    private final NotificationService service;

    public NotificationRestController(NotificationService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<NotificationResponseDTO> createDirect(@RequestBody NotificationRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createDirect(request));
    }

    @PostMapping("/events")
    public ResponseEntity<List<NotificationResponseDTO>> publishEvent(@RequestBody NotificationEventRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.publishEvent(request));
    }

    @PostMapping("/process-pending")
    public ResponseEntity<List<NotificationResponseDTO>> processPending() {
        return ResponseEntity.ok(service.processPending());
    }

    @GetMapping
    public ResponseEntity<List<NotificationResponseDTO>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/recipients/{recipientId}")
    public ResponseEntity<List<NotificationResponseDTO>> findByRecipient(@PathVariable String recipientId) {
        return ResponseEntity.ok(service.findByRecipient(recipientId));
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<NotificationResponseDTO> markAsRead(@PathVariable String id) {
        return ResponseEntity.ok(service.markAsRead(id));
    }

    @GetMapping("/communication")
    public ResponseEntity<String> communicationSummary() {
        return ResponseEntity.ok(service.communicationSummary());
    }
}
