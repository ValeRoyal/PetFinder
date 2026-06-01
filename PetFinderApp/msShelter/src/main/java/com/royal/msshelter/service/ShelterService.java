package com.royal.msshelter.service;

import com.royal.msshelter.dto.request.ShelterMessageRequestDTO;
import com.royal.msshelter.dto.request.ShelterRequestDTO;
import com.royal.msshelter.dto.response.ShelterMessageResponseDTO;
import com.royal.msshelter.dto.response.ShelterReportResponseDTO;
import com.royal.msshelter.dto.response.ShelterResponseDTO;
import com.royal.msshelter.model.Shelter;
import com.royal.msshelter.model.ShelterMessage;
import com.royal.msshelter.model.ShelterReport;
import com.royal.msshelter.patterns.facade.ShelterManagementFacade;
import com.royal.msshelter.repository.ShelterMessageRepository;
import com.royal.msshelter.repository.ShelterRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

@Service
public class ShelterService {

    private final ShelterRepository shelterRepository;
    private final ShelterMessageRepository messageRepository;
    private final ShelterManagementFacade facade;

    public ShelterService(ShelterRepository shelterRepository, ShelterMessageRepository messageRepository) {
        this.shelterRepository = shelterRepository;
        this.messageRepository = messageRepository;
        this.facade = new ShelterManagementFacade();
    }

    @Transactional
    public ShelterResponseDTO create(ShelterRequestDTO request) {
        validateEmailAvailability(request.email(), null);

        Shelter shelter = new Shelter();
        shelter.setId(generateNextId());
        applyData(shelter, request);
        return toResponse(shelterRepository.save(shelter));
    }

    @Transactional(readOnly = true)
    public ShelterResponseDTO login(String identifier, String password) {
        Shelter shelter = identifier.contains("@")
                ? shelterRepository.findByEmailIgnoreCase(identifier)
                    .orElseThrow(() -> new IllegalArgumentException("Shelter not found by email: " + identifier))
                : findEntityById(identifier);

        if (password == null || password.isBlank() || !matchesPassword(shelter.getPassword(), password)) {
            throw new IllegalArgumentException("Invalid password");
        }

        return toResponse(shelter);
    }

    @Transactional(readOnly = true)
    public List<ShelterResponseDTO> findAll() {
        return shelterRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ShelterResponseDTO findById(String id) {
        return toResponse(findEntityById(id));
    }

    @Transactional
    public ShelterResponseDTO update(String id, ShelterRequestDTO request) {
        Shelter shelter = findEntityById(id);
        validateEmailAvailability(request.email(), shelter.getId());
        applyData(shelter, request);
        return toResponse(shelterRepository.save(shelter));
    }

    @Transactional
    public ShelterResponseDTO registerPet(String shelterId, String petProfileId) {
        Shelter shelter = findEntityById(shelterId);
        facade.registerPet(shelter, petProfileId);
        return toResponse(shelterRepository.save(shelter));
    }

    @Transactional
    public ShelterResponseDTO removePet(String shelterId, String petProfileId) {
        Shelter shelter = findEntityById(shelterId);
        shelter.removePet(petProfileId);
        return toResponse(shelterRepository.save(shelter));
    }

    @Transactional
    public ShelterResponseDTO assignVeterinarian(String shelterId, String veterinarianId) {
        Shelter shelter = findEntityById(shelterId);
        facade.assignVeterinarian(shelter, veterinarianId);
        return toResponse(shelterRepository.save(shelter));
    }

    @Transactional
    public ShelterMessageResponseDTO sendMessage(String shelterId, ShelterMessageRequestDTO request) {
        Shelter shelter = findEntityById(shelterId);
        String messageId = requireId(request.id(), "Shelter message id is required");
        if (messageRepository.existsById(messageId)) {
            throw new IllegalArgumentException("Shelter message id already exists: " + messageId);
        }
        ShelterMessage message = facade.sendMessageToAdopter(
                shelter,
                messageId,
                request.recipient(),
                request.subject(),
                request.content()
        );
        return toMessageResponse(messageRepository.save(message));
    }

    @Transactional(readOnly = true)
    public List<ShelterMessageResponseDTO> findMessages(String shelterId) {
        return messageRepository.findByShelterIdOrderBySentAtDesc(shelterId).stream()
                .map(this::toMessageResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ShelterReportResponseDTO generateReport(String shelterId) {
        Shelter shelter = findEntityById(shelterId);
        ShelterReport report = facade.generateReport(shelter, messageRepository.countByShelterId(shelterId));
        return new ShelterReportResponseDTO(
                report.getShelterId(),
                report.getTotalPets(),
                report.getTotalVeterinarians(),
                report.getTotalMessagesSent()
        );
    }

    private Shelter findEntityById(String id) {
        return shelterRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Shelter not found: " + id));
    }

    private void applyData(Shelter shelter, ShelterRequestDTO request) {
        shelter.setName(request.name());
        shelter.setLocation(request.location());
        shelter.setEmail(request.email());
        if (shelter.getId() == null) {
            shelter.setPassword(requirePassword(request.password()));
        } else if (request.password() != null && !request.password().isBlank()) {
            shelter.setPassword(request.password());
        }
        shelter.setPhone(request.phone());

        shelter.getPhotos().clear();
        if (request.photos() != null) {
            shelter.getPhotos().addAll(request.photos());
        }

        shelter.getVideos().clear();
        if (request.videos() != null) {
            shelter.getVideos().addAll(request.videos());
        }
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

    private void validateEmailAvailability(String email, String currentShelterId) {
        shelterRepository.findByEmailIgnoreCase(email)
                .filter(existing -> currentShelterId == null || !existing.getId().equals(currentShelterId))
                .ifPresent(existing -> {
                    throw new IllegalArgumentException("Shelter email already exists: " + email);
                });
    }

    private boolean matchesPassword(String storedPassword, String providedPassword) {
        return storedPassword != null && storedPassword.equals(providedPassword);
    }

    private String generateNextId() {
        int max = shelterRepository.findAll().stream()
                .map(Shelter::getId)
                .filter(id -> id != null && id.toUpperCase(Locale.ROOT).startsWith("SHELTER-"))
                .map(id -> id.substring("SHELTER-".length()))
                .map(value -> {
                    try {
                        return Integer.parseInt(value);
                    } catch (NumberFormatException ex) {
                        return 0;
                    }
                })
                .max(Integer::compareTo)
                .orElse(0);

        return String.format("SHELTER-%03d", max + 1);
    }

    private ShelterResponseDTO toResponse(Shelter shelter) {
        return new ShelterResponseDTO(
                shelter.getId(),
                shelter.getName(),
                shelter.getLocation(),
                shelter.getEmail(),
                shelter.getPhone(),
                List.copyOf(shelter.getPhotos()),
                List.copyOf(shelter.getVideos()),
                List.copyOf(shelter.getPetProfileIds()),
                List.copyOf(shelter.getVeterinarianIds())
        );
    }

    private ShelterMessageResponseDTO toMessageResponse(ShelterMessage message) {
        return new ShelterMessageResponseDTO(
                message.getId(),
                message.getShelterId(),
                message.getRecipient(),
                message.getSubject(),
                message.getContent(),
                message.getSentAt()
        );
    }
}
