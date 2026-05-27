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
        if (shelterRepository.existsByEmailIgnoreCase(request.email())) {
            throw new IllegalArgumentException("Shelter email already exists: " + request.email());
        }

        Shelter shelter = new Shelter();
        String id = requireId(request.id(), "Shelter id is required");
        if (shelterRepository.existsById(id)) {
            throw new IllegalArgumentException("Shelter id already exists: " + id);
        }
        shelter.setId(id);
        applyData(shelter, request);
        return toResponse(shelterRepository.save(shelter));
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
