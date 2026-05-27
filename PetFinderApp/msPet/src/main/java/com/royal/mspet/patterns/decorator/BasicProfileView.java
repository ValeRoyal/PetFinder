package com.royal.mspet.patterns.decorator;

import com.royal.mspet.model.PetProfile;
import java.util.List;

public class BasicProfileView implements ProfileView {

    private final PetProfile profile;

    public BasicProfileView(PetProfile profile) {
        this.profile = profile;
    }

    @Override
    public String getDisplayInfo() {
        return String.format(
                "%s is a %s %s, %d years old. Energy: %s. Status: %s.",
                profile.getName(),
                profile.getSpecies(),
                profile.getBreed(),
                profile.getAge(),
                profile.getEnergyLevel(),
                profile.getStatus()
        );
    }

    @Override
    public List<String> getPhotos() {
        return List.copyOf(profile.getPhotos());
    }
}
