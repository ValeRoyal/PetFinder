package com.royal.mspet.patterns.builder;

import com.royal.mspet.model.PetProfile;
import com.royal.mspet.model.enums.PetStatus;

import java.util.List;

public class ConcretePetBuilder implements PetProfileBuilder {

    private final PetProfile profile = new PetProfile();

    @Override
    public PetProfileBuilder setBasicInfo(String id, String name, String species) {
        profile.setId(id);
        profile.setName(name);
        profile.setSpecies(species);
        return this;
    }

    @Override
    public PetProfileBuilder setPhysicalTraits(String breed, int age, String sex, String size) {
        profile.setBreed(breed);
        profile.setAge(age);
        profile.setSex(sex);
        profile.setSize(size);
        return this;
    }

    @Override
    public PetProfileBuilder setCompatibility(String energyLevel, boolean kidsCompatible, boolean otherPetsCompatible) {
        profile.setEnergyLevel(energyLevel);
        profile.setKidsCompatible(kidsCompatible);
        profile.setOtherPetsCompatible(otherPetsCompatible);
        return this;
    }

    @Override
    public PetProfileBuilder addPhotos(List<String> photos) {
        if (photos != null) {
            profile.getPhotos().addAll(photos);
        }
        return this;
    }

    @Override
    public PetProfileBuilder setBio(String bio) {
        profile.setBio(bio);
        return this;
    }

    @Override
    public PetProfileBuilder setVaccinationCardId(String vaccinationCardId) {
        profile.setVaccinationCardId(vaccinationCardId);
        return this;
    }

    @Override
    public PetProfileBuilder setStatus(PetStatus status) {
        profile.setStatus(status);
        return this;
    }

    @Override
    public PetProfile build() {
        if (profile.getStatus() == null) {
            profile.setStatus(PetStatus.AVAILABLE);
        }
        return profile;
    }
}
