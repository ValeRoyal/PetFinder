package com.royal.msadopter.controller;

import com.royal.msadopter.dto.request.AdopterProfileRequestDTO;
import com.royal.msadopter.dto.request.PreferencesRequestDTO;
import com.royal.msadopter.dto.request.SwipeRequestDTO;
import com.royal.msadopter.dto.response.AdopterProfileResponseDTO;
import com.royal.msadopter.dto.response.SwipeActionResponseDTO;
import com.royal.msadopter.service.AdopterProfileService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/adopters")
public class AdopterProfileRestController {

    private final AdopterProfileService service;

    public AdopterProfileRestController(AdopterProfileService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<AdopterProfileResponseDTO> create(@RequestBody AdopterProfileRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    @GetMapping
    public ResponseEntity<List<AdopterProfileResponseDTO>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AdopterProfileResponseDTO> findById(@PathVariable String id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @GetMapping("/by-email")
    public ResponseEntity<AdopterProfileResponseDTO> findByEmail(@RequestParam String email) {
        return ResponseEntity.ok(service.findByEmail(email));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AdopterProfileResponseDTO> update(
            @PathVariable String id,
            @RequestBody AdopterProfileRequestDTO request
    ) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @PatchMapping("/{id}/preferences")
    public ResponseEntity<AdopterProfileResponseDTO> updatePreferences(
            @PathVariable String id,
            @RequestBody PreferencesRequestDTO request
    ) {
        return ResponseEntity.ok(service.updatePreferences(id, request));
    }

    @PostMapping("/{id}/swipes/right")
    public ResponseEntity<SwipeActionResponseDTO> swipeRight(
            @PathVariable String id,
            @RequestBody SwipeRequestDTO request
    ) {
        return ResponseEntity.ok(service.swipeRight(id, request.id(), request.petProfileId()));
    }

    @PostMapping("/{id}/swipes/left")
    public ResponseEntity<SwipeActionResponseDTO> swipeLeft(
            @PathVariable String id,
            @RequestBody SwipeRequestDTO request
    ) {
        return ResponseEntity.ok(service.swipeLeft(id, request.id(), request.petProfileId()));
    }

    @PatchMapping("/{id}/swipes/undo-last")
    public ResponseEntity<SwipeActionResponseDTO> undoLastSwipe(@PathVariable String id) {
        return ResponseEntity.ok(service.undoLastSwipe(id));
    }

    @GetMapping("/{id}/swipes")
    public ResponseEntity<List<SwipeActionResponseDTO>> findSwipeHistory(@PathVariable String id) {
        return ResponseEntity.ok(service.findSwipeHistory(id));
    }

    @GetMapping("/{id}/likes")
    public ResponseEntity<List<SwipeActionResponseDTO>> findLikedPets(@PathVariable String id) {
        return ResponseEntity.ok(service.findLikedPets(id));
    }
}
