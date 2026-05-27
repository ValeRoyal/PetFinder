package com.royal.mspet.patterns.builder;

import com.royal.mspet.model.PetProfile;
import com.royal.mspet.model.enums.PetStatus;

import java.util.List;

public interface PetProfileBuilder {

    PetProfileBuilder setBasicInfo(String id, String name, String species);

    PetProfileBuilder setPhysicalTraits(String breed, int age, String sex, String size);

    PetProfileBuilder setCompatibility(String energyLevel, boolean kidsCompatible, boolean otherPetsCompatible);

    PetProfileBuilder addPhotos(List<String> photos);

    PetProfileBuilder setBio(String bio);

    PetProfileBuilder setVaccinationCardId(String vaccinationCardId);

    PetProfileBuilder setStatus(PetStatus status);

    PetProfile build();
}
