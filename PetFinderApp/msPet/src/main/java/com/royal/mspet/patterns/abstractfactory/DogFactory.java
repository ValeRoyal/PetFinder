package com.royal.mspet.patterns.abstractfactory;

import com.royal.mspet.model.PetProfile;
import com.royal.mspet.patterns.builder.ConcretePetBuilder;
import com.royal.mspet.patterns.builder.PetProfileDirector;

import java.util.List;

public class DogFactory implements AbstractPetFactory {

    @Override
    public PetProfile createProfile(String id, String name, String breed, int age, String sex, String size) {
        return new PetProfileDirector().buildAdoptableProfile(
                new ConcretePetBuilder(),
                id,
                name,
                "DOG",
                breed,
                age,
                sex,
                size,
                "HIGH",
                true,
                true,
                List.of(),
                "Dog profile created by DogFactory.",
                createVaccinationCardId(id)
        );
    }

    @Override
    public String createVaccinationCardId(String petProfileId) {
        return "VC-DOG-" + petProfileId;
    }
}
