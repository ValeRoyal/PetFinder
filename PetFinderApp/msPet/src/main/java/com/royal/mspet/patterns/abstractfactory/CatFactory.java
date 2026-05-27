package com.royal.mspet.patterns.abstractfactory;

import com.royal.mspet.model.PetProfile;
import com.royal.mspet.patterns.builder.ConcretePetBuilder;
import com.royal.mspet.patterns.builder.PetProfileDirector;

import java.util.List;

public class CatFactory implements AbstractPetFactory {

    @Override
    public PetProfile createProfile(String id, String name, String breed, int age, String sex, String size) {
        return new PetProfileDirector().buildAdoptableProfile(
                new ConcretePetBuilder(),
                id,
                name,
                "CAT",
                breed,
                age,
                sex,
                size,
                "MEDIUM",
                true,
                true,
                List.of(),
                "Cat profile created by CatFactory.",
                createVaccinationCardId(id)
        );
    }

    @Override
    public String createVaccinationCardId(String petProfileId) {
        return "VC-CAT-" + petProfileId;
    }
}
