package com.royal.msshelter.patterns.facade;

import com.royal.msshelter.model.Shelter;
import com.royal.msshelter.model.ShelterMessage;
import com.royal.msshelter.model.ShelterReport;
import com.royal.msshelter.patterns.adapter.EmailAdapter;
import com.royal.msshelter.patterns.adapter.MessagingAdapter;

import java.time.LocalDateTime;

public class ShelterManagementFacade {

    private final MessagingAdapter messagingAdapter;

    public ShelterManagementFacade() {
        this(new EmailAdapter());
    }

    public ShelterManagementFacade(MessagingAdapter messagingAdapter) {
        this.messagingAdapter = messagingAdapter;
    }

    public Shelter registerPet(Shelter shelter, String petProfileId) {
        shelter.registerPet(petProfileId);
        return shelter;
    }

    public Shelter assignVeterinarian(Shelter shelter, String veterinarianId) {
        shelter.registerVeterinarian(veterinarianId);
        return shelter;
    }

    public ShelterMessage sendMessageToAdopter(
            Shelter shelter,
            String messageId,
            String adopterEmail,
            String subject,
            String content
    ) {
        messagingAdapter.sendMessage(adopterEmail, subject, content);

        ShelterMessage message = new ShelterMessage();
        message.setId(messageId);
        message.setShelterId(shelter.getId());
        message.setRecipient(adopterEmail);
        message.setSubject(subject);
        message.setContent(content);
        message.setSentAt(LocalDateTime.now());
        return message;
    }

    public ShelterReport generateReport(Shelter shelter, int totalMessagesSent) {
        return new ShelterReport(
                shelter.getId(),
                shelter.getPetProfileIds().size(),
                shelter.getVeterinarianIds().size(),
                totalMessagesSent
        );
    }
}
