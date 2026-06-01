package com.royal.mspet.controller;

import com.royal.mspet.dto.request.PetProfileRequestDTO;
import com.royal.mspet.dto.request.TextValueRequestDTO;
import com.royal.mspet.dto.response.PetProfileResponseDTO;
import com.royal.mspet.model.enums.PetStatus;
import com.royal.mspet.service.PetProfileService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/pets")
public class PetProfileRestController {

    private final PetProfileService service;

    public PetProfileRestController(PetProfileService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<PetProfileResponseDTO> create(@RequestBody PetProfileRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    @GetMapping
    public ResponseEntity<List<PetProfileResponseDTO>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PetProfileResponseDTO> findById(@PathVariable String id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PetProfileResponseDTO> update(
            @PathVariable String id,
            @RequestBody PetProfileRequestDTO request
    ) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/available")
    public ResponseEntity<List<PetProfileResponseDTO>> findAvailable(
            @RequestParam(required = false) String species
    ) {
        return ResponseEntity.ok(service.findAvailable(species));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<PetProfileResponseDTO> updateStatus(
            @PathVariable String id,
            @RequestParam PetStatus status
    ) {
        return ResponseEntity.ok(service.updateStatus(id, status));
    }

    @PostMapping("/{id}/photos")
    public ResponseEntity<PetProfileResponseDTO> addPhoto(
            @PathVariable String id,
            @RequestBody TextValueRequestDTO request
    ) {
        return ResponseEntity.ok(service.addPhoto(id, request.value()));
    }

    @PostMapping("/{id}/medical-events")
    public ResponseEntity<PetProfileResponseDTO> addMedicalEventReference(
            @PathVariable String id,
            @RequestBody TextValueRequestDTO request
    ) {
        return ResponseEntity.ok(service.addMedicalEventReference(id, request.value()));
    }

    @GetMapping("/{id}/view/basic")
    public ResponseEntity<String> getBasicDisplayInfo(@PathVariable String id) {
        return ResponseEntity.ok(service.getBasicDisplayInfo(id));
    }

    @GetMapping("/{id}/view/enhanced")
    public ResponseEntity<String> getEnhancedDisplayInfo(@PathVariable String id) {
        return ResponseEntity.ok(service.getEnhancedDisplayInfo(id));
    }
}
