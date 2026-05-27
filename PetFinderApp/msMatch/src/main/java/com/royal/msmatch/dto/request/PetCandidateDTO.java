package com.royal.msmatch.dto.request;

public record PetCandidateDTO(
        String id,
        String shelterId,
        String species,
        String breed,
        int age,
        String size,
        String energyLevel,
        boolean kidsCompatible,
        boolean otherPetsCompatible,
        String status
) {
}
