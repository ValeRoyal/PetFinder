package com.royal.msvet.service;

import com.royal.msvet.dto.request.VeterinarianRequestDTO;
import com.royal.msvet.dto.response.VeterinarianResponseDTO;
import com.royal.msvet.model.Veterinarian;
import com.royal.msvet.repository.VeterinarianRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class VeterinarianService {

    private final VeterinarianRepository veterinarianRepository;

    // RF-05.1 — registrar vet en un refugio
    public VeterinarianResponseDTO register(VeterinarianRequestDTO dto) {
        validateEmailAvailability(dto.getEmail(), null);
        Veterinarian vet = new Veterinarian();
        vet.setId(generateNextId());
        vet.setName(dto.getName());
        vet.setSpecialty(dto.getSpecialty());
        vet.setPhoneNumber(dto.getPhoneNumber());
        vet.setEmail(dto.getEmail());
        vet.setPassword(requirePassword(dto.getPassword()));
        vet.setShelterId(dto.getShelterId());
        veterinarianRepository.save(vet);
        return toResponseDTO(vet);
    }

    public VeterinarianResponseDTO login(String identifier, String password) {
        Veterinarian vet = identifier.contains("@")
                ? veterinarianRepository.findByEmailIgnoreCase(identifier)
                    .orElseThrow(() -> new RuntimeException("Veterinario no encontrado"))
                : veterinarianRepository.findById(identifier)
                    .orElseThrow(() -> new RuntimeException("Veterinario no encontrado"));

        if (password == null || password.isBlank() || !matchesPassword(vet.getPassword(), password)) {
            throw new IllegalArgumentException("Invalid password");
        }

        return toResponseDTO(vet);
    }

    // RF-05.1 — todos los vets de un refugio
    public List<VeterinarianResponseDTO> getByShelter(String shelterId) {
        return veterinarianRepository.findByShelterId(shelterId)
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public VeterinarianResponseDTO getById(String id) {
        Veterinarian vet = veterinarianRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Veterinario no encontrado"));
        return toResponseDTO(vet);
    }

    // RF-05.1 — actualizar datos del vet
    public VeterinarianResponseDTO update(String id, VeterinarianRequestDTO dto) {
        Veterinarian vet = veterinarianRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Veterinario no encontrado"));
        validateEmailAvailability(dto.getEmail(), vet.getId());
        vet.setName(dto.getName());
        vet.setSpecialty(dto.getSpecialty());
        vet.setPhoneNumber(dto.getPhoneNumber());
        vet.setEmail(dto.getEmail());
        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            vet.setPassword(dto.getPassword());
        }
        veterinarianRepository.save(vet);
        return toResponseDTO(vet);
    }

    private VeterinarianResponseDTO toResponseDTO(Veterinarian vet) {
        VeterinarianResponseDTO dto = new VeterinarianResponseDTO();
        dto.setId(vet.getId());
        dto.setName(vet.getName());
        dto.setSpecialty(vet.getSpecialty());
        dto.setPhoneNumber(vet.getPhoneNumber());
        dto.setEmail(vet.getEmail());
        dto.setShelterId(vet.getShelterId());
        return dto;
    }

    private String requireId(String id, String message) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException(message);
        }
        return id;
    }

    private String requirePassword(String password) {
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("Password is required");
        }
        return password;
    }

    private void validateEmailAvailability(String email, String currentVeterinarianId) {
        veterinarianRepository.findByEmailIgnoreCase(email)
                .filter(existing -> currentVeterinarianId == null || !existing.getId().equals(currentVeterinarianId))
                .ifPresent(existing -> {
                    throw new IllegalArgumentException("Veterinarian email already exists: " + email);
                });
    }

    private boolean matchesPassword(String storedPassword, String providedPassword) {
        return storedPassword != null && storedPassword.equals(providedPassword);
    }

    private String generateNextId() {
        int max = veterinarianRepository.findAll().stream()
                .map(Veterinarian::getId)
                .filter(id -> id != null && id.toUpperCase(Locale.ROOT).startsWith("VET-"))
                .map(id -> id.substring("VET-".length()))
                .map(value -> {
                    try {
                        return Integer.parseInt(value);
                    } catch (NumberFormatException ex) {
                        return 0;
                    }
                })
                .max(Integer::compareTo)
                .orElse(0);

        return String.format("VET-%03d", max + 1);
    }
}
