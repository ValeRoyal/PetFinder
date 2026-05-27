package com.royal.msshelter.patterns.facade;

import com.royal.msshelter.model.Shelter;
import com.royal.msshelter.model.ShelterMessage;

public class ShelterFacadeClient {

    private final ShelterManagementFacade facade;

    public ShelterFacadeClient(ShelterManagementFacade facade) {
        this.facade = facade;
    }

    public ShelterMessage notifyAdopterAboutPet(Shelter shelter, String messageId, String adopterEmail, String petName) {
        return facade.sendMessageToAdopter(
                shelter,
                messageId,
                adopterEmail,
                "PetFinder adoption update",
                "The shelter has an update about " + petName + "."
        );
    }
}
