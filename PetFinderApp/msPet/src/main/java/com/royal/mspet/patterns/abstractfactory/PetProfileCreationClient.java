package com.royal.mspet.patterns.abstractfactory;

import com.royal.mspet.model.PetProfile;

public class PetProfileCreationClient {

    public PetProfile createPetProfile(String species, String id, String name, String breed, int age, String sex, String size) {
        AbstractPetFactory factory = selectFactory(species);
        return factory.createProfile(id, name, breed, age, sex, size);
    }

    private AbstractPetFactory selectFactory(String species) {
        return switch (species.toUpperCase()) {
            case "CAT" -> new CatFactory();
            case "DOG" -> new DogFactory();
            default -> throw new IllegalArgumentException("Unsupported pet species: " + species);
        };
    }
}
