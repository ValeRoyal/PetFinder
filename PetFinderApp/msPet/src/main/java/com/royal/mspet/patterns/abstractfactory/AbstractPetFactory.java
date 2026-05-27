package com.royal.mspet.patterns.abstractfactory;

import com.royal.mspet.model.PetProfile;

public interface AbstractPetFactory {

    PetProfile createProfile(String id, String name, String breed, int age, String sex, String size);

    String createVaccinationCardId(String petProfileId);
}
