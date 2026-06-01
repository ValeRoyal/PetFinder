package com.royal.msvet.controller;

import com.royal.msvet.dto.request.VaccineRequestDTO;
import com.royal.msvet.dto.response.VaccineResponseDTO;
import com.royal.msvet.service.VaccineService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/vaccines")
@RequiredArgsConstructor
public class VaccineRestController {

    private final VaccineService vaccineService;

    // US-17 — addVaccine()
    @PostMapping
    public ResponseEntity<VaccineResponseDTO> addVaccine(
            @RequestBody VaccineRequestDTO dto) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(vaccineService.addVaccine(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<VaccineResponseDTO> updateVaccine(
            @PathVariable String id,
            @RequestBody VaccineRequestDTO dto) {
        return ResponseEntity.ok(vaccineService.updateVaccine(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVaccine(@PathVariable String id) {
        vaccineService.deleteVaccine(id);
        return ResponseEntity.noContent().build();
    }

    // Todas las vacunas de una mascota
    @GetMapping("/pet/{petProfileId}")
    public ResponseEntity<List<VaccineResponseDTO>> getByPet(
            @PathVariable String petProfileId) {
        return ResponseEntity.ok(vaccineService.getByPet(petProfileId));
    }

    // RF-01.2 — próximas vacunas en los siguientes 30 días
    @GetMapping("/pet/{petProfileId}/upcoming")
    public ResponseEntity<List<VaccineResponseDTO>> getUpcoming(
            @PathVariable String petProfileId) {
        return ResponseEntity.ok(vaccineService.getUpcoming(petProfileId));
    }
}
