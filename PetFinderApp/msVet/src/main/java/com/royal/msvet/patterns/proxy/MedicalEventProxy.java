package com.royal.msvet.patterns.proxy;

import com.royal.msvet.dto.request.MedicalEventRequestDTO;
import com.royal.msvet.dto.response.MedicalEventResponseDTO;
import com.royal.msvet.model.enums.MedicalEventAuthor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class MedicalEventProxy implements MedicalEventServiceInterface {

    private final MedicalEventServiceInterface realService;

    @Override
    public MedicalEventResponseDTO registerEvent(MedicalEventRequestDTO dto) {
        if (dto.getRegisteredByRole() == MedicalEventAuthor.ADOPTER
                && dto.getVetNotes() != null) {
            throw new SecurityException(
                    "Un adoptante no puede registrar notas veterinarias"
            );
        }
        return realService.registerEvent(dto);
    }

    @Override
    public List<MedicalEventResponseDTO> getEventsByPet(String petProfileId) {
        return realService.getEventsByPet(petProfileId);
    }

    @Override
    public MedicalEventResponseDTO updateVetNotes(String eventId, String vetNotes, String requesterRole) {
        if (!requesterRole.equals("VET")) {
            throw new SecurityException(
                    "Solo un veterinario puede añadir notas veterinarias"
            );
        }
        return realService.updateVetNotes(eventId, vetNotes, requesterRole);
    }

    @Override
    public MedicalEventResponseDTO updateEvent(String eventId, MedicalEventRequestDTO dto) {
        if (dto.getRegisteredByRole() == MedicalEventAuthor.ADOPTER
                && dto.getVetNotes() != null) {
            throw new SecurityException(
                    "Un adoptante no puede registrar notas veterinarias"
            );
        }
        return realService.updateEvent(eventId, dto);
    }

    @Override
    public void deleteEvent(String eventId) {
        realService.deleteEvent(eventId);
    }
}
