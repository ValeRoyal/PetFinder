package com.royal.msvet.controller;

import com.royal.msvet.dto.request.VeterinarianRequestDTO;
import com.royal.msvet.dto.response.VeterinarianResponseDTO;
import com.royal.msvet.service.VeterinarianService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/veterinarians")
@RequiredArgsConstructor
public class VeterinarianRestController {

    private final VeterinarianService veterinarianService;

    // RF-05.1 — registrar vet
    @PostMapping
    public ResponseEntity<VeterinarianResponseDTO> register(
            @RequestBody VeterinarianRequestDTO dto) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(veterinarianService.register(dto));
    }

    @PostMapping("/login")
    public ResponseEntity<VeterinarianResponseDTO> login(
            @RequestParam String identifier,
            @RequestParam String password) {
        return ResponseEntity.ok(veterinarianService.login(identifier, password));
    }

    // RF-05.1 — todos los vets de un refugio
    @GetMapping("/shelter/{shelterId}")
    public ResponseEntity<List<VeterinarianResponseDTO>> getByShelter(
            @PathVariable String shelterId) {
        return ResponseEntity.ok(veterinarianService.getByShelter(shelterId));
    }

    // Buscar vet por ID
    @GetMapping("/{id}")
    public ResponseEntity<VeterinarianResponseDTO> getById(
            @PathVariable String id) {
        return ResponseEntity.ok(veterinarianService.getById(id));
    }

    // Actualizar datos del vet
    @PutMapping("/{id}")
    public ResponseEntity<VeterinarianResponseDTO> update(
            @PathVariable String id,
            @RequestBody VeterinarianRequestDTO dto) {
        return ResponseEntity.ok(veterinarianService.update(id, dto));
    }
}