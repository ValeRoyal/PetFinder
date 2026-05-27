package com.royal.mspet.dto.response;

import com.royal.mspet.model.enums.PetStatus;
import java.util.List;

public record PetProfileResponseDTO(
        String id,
        String name,
        String species,
        String breed,
        int age,
        String sex,
        String size,
        String energyLevel,
        boolean kidsCompatible,
        boolean otherPetsCompatible,
        List<String> photos,
        String bio,
        String vaccinationCardId,
        List<String> medicalEventIds,
        PetStatus status,
        String displayInfo
) {
}
