package com.royal.msshelter.controller;

import com.royal.msshelter.dto.request.ShelterMessageRequestDTO;
import com.royal.msshelter.dto.request.ShelterRequestDTO;
import com.royal.msshelter.dto.request.TextValueRequestDTO;
import com.royal.msshelter.dto.response.ShelterMessageResponseDTO;
import com.royal.msshelter.dto.response.ShelterReportResponseDTO;
import com.royal.msshelter.dto.response.ShelterResponseDTO;
import com.royal.msshelter.service.ShelterService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/shelters")
public class ShelterRestController {

    private final ShelterService service;

    public ShelterRestController(ShelterService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ShelterResponseDTO> create(@RequestBody ShelterRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    @PostMapping("/login")
    public ResponseEntity<ShelterResponseDTO> login(@RequestParam String identifier, @RequestParam String password) {
        return ResponseEntity.ok(service.login(identifier, password));
    }

    @GetMapping
    public ResponseEntity<List<ShelterResponseDTO>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ShelterResponseDTO> findById(@PathVariable String id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ShelterResponseDTO> update(
            @PathVariable String id,
            @RequestBody ShelterRequestDTO request
    ) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @PostMapping("/{id}/pets")
    public ResponseEntity<ShelterResponseDTO> registerPet(
            @PathVariable String id,
            @RequestBody TextValueRequestDTO request
    ) {
        return ResponseEntity.ok(service.registerPet(id, request.value()));
    }

    @DeleteMapping("/{id}/pets/{petProfileId}")
    public ResponseEntity<ShelterResponseDTO> removePet(
            @PathVariable String id,
            @PathVariable String petProfileId
    ) {
        return ResponseEntity.ok(service.removePet(id, petProfileId));
    }

    @PostMapping("/{id}/veterinarians")
    public ResponseEntity<ShelterResponseDTO> assignVeterinarian(
            @PathVariable String id,
            @RequestBody TextValueRequestDTO request
    ) {
        return ResponseEntity.ok(service.assignVeterinarian(id, request.value()));
    }

    @PostMapping("/{id}/messages")
    public ResponseEntity<ShelterMessageResponseDTO> sendMessage(
            @PathVariable String id,
            @RequestBody ShelterMessageRequestDTO request
    ) {
        return ResponseEntity.ok(service.sendMessage(id, request));
    }

    @GetMapping("/{id}/messages")
    public ResponseEntity<List<ShelterMessageResponseDTO>> findMessages(@PathVariable String id) {
        return ResponseEntity.ok(service.findMessages(id));
    }

    @GetMapping("/{id}/report")
    public ResponseEntity<ShelterReportResponseDTO> generateReport(@PathVariable String id) {
        return ResponseEntity.ok(service.generateReport(id));
    }
}
