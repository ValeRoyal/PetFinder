package com.royal.msvet.patterns.proxy;

import com.royal.msvet.dto.request.MedicalEventRequestDTO;
import com.royal.msvet.dto.response.MedicalEventResponseDTO;
import java.util.List;

public interface MedicalEventServiceInterface {
    MedicalEventResponseDTO registerEvent(MedicalEventRequestDTO dto);
    List<MedicalEventResponseDTO> getEventsByPet(String petProfileId);
    MedicalEventResponseDTO updateVetNotes(String eventId, String vetNotes, String requesterRole); // ← agrega este parámetro
    MedicalEventResponseDTO updateEvent(String eventId, MedicalEventRequestDTO dto);
    void deleteEvent(String eventId);
}
