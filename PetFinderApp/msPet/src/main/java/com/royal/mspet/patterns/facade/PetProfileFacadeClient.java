package com.royal.mspet.patterns.facade;

import com.royal.mspet.model.PetProfile;

public class PetProfileFacadeClient {

    private final PetProfileFacade facade;

    public PetProfileFacadeClient(PetProfileFacade facade) {
        this.facade = facade;
    }

    public String createAndShowEnhancedProfile(
            String species,
            String id,
            String name,
            String breed,
            int age,
            String sex,
            String size
    ) {
        PetProfile profile = facade.createPetProfile(species, id, name, breed, age, sex, size);
        return facade.getEnhancedDisplayInfo(profile);
    }
}
