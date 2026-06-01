package com.royal.msvet.controller;

import com.royal.msvet.dto.request.MedicalEventRequestDTO;
import com.royal.msvet.dto.response.MedicalEventResponseDTO;
import com.royal.msvet.patterns.proxy.MedicalEventClient;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/medical-events")
@RequiredArgsConstructor
public class MedicalEventRestController {

    // ← usa el Client, no el Service directamente
    private final MedicalEventClient medicalEventClient;

    // RF-01.3 — registrar evento médico
    // el rol viene en el header para que el Proxy valide
    @PostMapping
    public ResponseEntity<MedicalEventResponseDTO> register(
            @RequestBody MedicalEventRequestDTO dto) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(medicalEventClient.register(dto));
    }

    // US-16 — todos los eventos de una mascota
    @GetMapping("/pet/{petProfileId}")
    public ResponseEntity<List<MedicalEventResponseDTO>> getByPet(
            @PathVariable String petProfileId) {
        return ResponseEntity.ok(medicalEventClient.getByPet(petProfileId));
    }

    @PutMapping("/{eventId}")
    public ResponseEntity<MedicalEventResponseDTO> update(
            @PathVariable String eventId,
            @RequestBody MedicalEventRequestDTO dto) {
        return ResponseEntity.ok(medicalEventClient.update(eventId, dto));
    }

    @DeleteMapping("/{eventId}")
    public ResponseEntity<Void> delete(@PathVariable String eventId) {
        medicalEventClient.delete(eventId);
        return ResponseEntity.noContent().build();
    }

    // RF-04.4 — el vet añade sus notas
    // X-Requester-Role en header: "VET" o "ADOPTER"
    @PatchMapping("/{eventId}/vet-notes")
    public ResponseEntity<MedicalEventResponseDTO> updateVetNotes(
            @PathVariable String eventId,
            @RequestParam String vetNotes,
            @RequestHeader("X-Requester-Role") String requesterRole) {
        return ResponseEntity.ok(
                medicalEventClient.addVetNotes(eventId, vetNotes, requesterRole)
        );
    }
}
