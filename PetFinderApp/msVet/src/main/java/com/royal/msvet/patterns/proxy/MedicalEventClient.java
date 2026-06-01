package com.royal.msvet.patterns.proxy;

import com.royal.msvet.dto.request.MedicalEventRequestDTO;
import com.royal.msvet.dto.response.MedicalEventResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
@RequiredArgsConstructor
public class MedicalEventClient {

    private final MedicalEventProxy proxy;  // ← solo habla con el proxy

    public MedicalEventResponseDTO register(MedicalEventRequestDTO dto) {
        return proxy.registerEvent(dto);
    }

    public List<MedicalEventResponseDTO> getByPet(String petProfileId) {
        return proxy.getEventsByPet(petProfileId);
    }

    public MedicalEventResponseDTO addVetNotes(String eventId,
                                               String vetNotes,
                                               String requesterRole) {
        return proxy.updateVetNotes(eventId, vetNotes, requesterRole);
    }

    public MedicalEventResponseDTO update(String eventId, MedicalEventRequestDTO dto) {
        return proxy.updateEvent(eventId, dto);
    }

    public void delete(String eventId) {
        proxy.deleteEvent(eventId);
    }
}
