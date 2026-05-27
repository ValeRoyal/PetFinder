package com.royal.mspet.patterns.builder;

import com.royal.mspet.model.PetProfile;
import com.royal.mspet.model.enums.PetStatus;

import java.util.List;

public class PetProfileDirector {

    public PetProfile buildAdoptableProfile(
            PetProfileBuilder builder,
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
            String vaccinationCardId
    ) {
        return builder
                .setBasicInfo(id, name, species)
                .setPhysicalTraits(breed, age, sex, size)
                .setCompatibility(energyLevel, kidsCompatible, otherPetsCompatible)
                .addPhotos(photos)
                .setBio(bio)
                .setVaccinationCardId(vaccinationCardId)
                .setStatus(PetStatus.AVAILABLE)
                .build();
    }

    public PetProfile buildUnavailableProfile(
            PetProfileBuilder builder,
            String id,
            String name,
            String species,
            String breed,
            int age,
            String sex,
            String size,
            String vaccinationCardId
    ) {
        return builder
                .setBasicInfo(id, name, species)
                .setPhysicalTraits(breed, age, sex, size)
                .setCompatibility("UNKNOWN", false, false)
                .setVaccinationCardId(vaccinationCardId)
                .setStatus(PetStatus.UNAVAILABLE)
                .build();
    }
}
