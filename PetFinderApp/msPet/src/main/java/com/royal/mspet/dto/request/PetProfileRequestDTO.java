package com.royal.mspet.dto.request;

import java.util.List;

public record PetProfileRequestDTO(
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
        String bio
) {
}
