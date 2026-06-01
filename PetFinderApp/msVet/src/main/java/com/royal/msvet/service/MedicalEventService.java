package com.royal.msvet.service;

import com.royal.msvet.dto.request.MedicalEventRequestDTO;
import com.royal.msvet.dto.response.MedicalEventResponseDTO;
import com.royal.msvet.model.MedicalEvent;
import com.royal.msvet.patterns.proxy.MedicalEventServiceInterface;
import com.royal.msvet.repository.MedicalEventRepository;
import com.royal.msvet.repository.VaccinationCardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class MedicalEventService implements MedicalEventServiceInterface {

    private final MedicalEventRepository medicalEventRepository;
    private final VaccinationCardRepository vaccinationCardRepository;

    @Override
    public MedicalEventResponseDTO registerEvent(MedicalEventRequestDTO dto) {
        String id = requireId(dto.getId(), "Medical event id is required");
        if (medicalEventRepository.existsById(id)) {
            throw new IllegalArgumentException("Medical event id already exists: " + id);
        }
        MedicalEvent event = new MedicalEvent();
        event.setId(id);
        event.setPetProfileId(dto.getPetProfileId());
        event.setDate(dto.getDate());
        event.setEventType(dto.getEventType());
        event.setTitle(dto.getTitle());
        event.setDescription(dto.getDescription());
        event.setVetNotes(dto.getVetNotes());
        event.setRegisteredById(dto.getRegisteredById());
        event.setRegisteredByRole(dto.getRegisteredByRole());
        event.setVaccinationCard(
                vaccinationCardRepository.findById(dto.getVaccinationCardId())
                        .orElseThrow(() -> new RuntimeException("Carnet no encontrado"))
        );
        medicalEventRepository.save(event);
        return MedicalEventResponseDTO.from(event);
    }

    private String requireId(String id, String message) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException(message);
        }
        return id;
    }

    @Override
    public List<MedicalEventResponseDTO> getEventsByPet(String petProfileId) {
        return medicalEventRepository.findByPetProfileId(petProfileId)
                .stream()
                .map(MedicalEventResponseDTO::from)
                .toList();
    }

    @Override
    public MedicalEventResponseDTO updateVetNotes(String eventId, String vetNotes, String requestRole) {
        MedicalEvent event = medicalEventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Evento no encontrado"));
        event.setVetNotes(vetNotes);
        medicalEventRepository.save(event);
        return MedicalEventResponseDTO.from(event);
    }

    @Override
    public MedicalEventResponseDTO updateEvent(String eventId, MedicalEventRequestDTO dto) {
        MedicalEvent event = medicalEventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Evento no encontrado"));
        event.setPetProfileId(dto.getPetProfileId());
        event.setDate(dto.getDate());
        event.setEventType(dto.getEventType());
        event.setTitle(dto.getTitle());
        event.setDescription(dto.getDescription());
        event.setVetNotes(dto.getVetNotes());
        event.setRegisteredById(dto.getRegisteredById());
        event.setRegisteredByRole(dto.getRegisteredByRole());
        event.setVaccinationCard(
                vaccinationCardRepository.findById(dto.getVaccinationCardId())
                        .orElseThrow(() -> new RuntimeException("Carnet no encontrado"))
        );
        return MedicalEventResponseDTO.from(medicalEventRepository.save(event));
    }

    @Override
    public void deleteEvent(String eventId) {
        MedicalEvent event = medicalEventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Evento no encontrado"));
        medicalEventRepository.delete(event);
    }
}
