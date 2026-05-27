package com.royal.mspet.patterns.facade;

import com.royal.mspet.model.PetProfile;
import com.royal.mspet.patterns.abstractfactory.PetProfileCreationClient;
import com.royal.mspet.patterns.decorator.BasicProfileView;
import com.royal.mspet.patterns.decorator.EnhancedProfileView;
import com.royal.mspet.patterns.decorator.ProfileView;

public class PetProfileFacade {

    private final PetProfileCreationClient creationClient;

    public PetProfileFacade() {
        this(new PetProfileCreationClient());
    }

    public PetProfileFacade(PetProfileCreationClient creationClient) {
        this.creationClient = creationClient;
    }

    public PetProfile createPetProfile(
            String species,
            String id,
            String name,
            String breed,
            int age,
            String sex,
            String size
    ) {
        return creationClient.createPetProfile(species, id, name, breed, age, sex, size);
    }

    public ProfileView createBasicView(PetProfile profile) {
        return new BasicProfileView(profile);
    }

    public ProfileView createEnhancedView(PetProfile profile) {
        ProfileView basicView = createBasicView(profile);
        return new EnhancedProfileView(
                basicView,
                resolveFeaturedBadge(profile),
                profile.getMedicalEventIds()
        );
    }

    public String getBasicDisplayInfo(PetProfile profile) {
        return createBasicView(profile).getDisplayInfo();
    }

    public String getEnhancedDisplayInfo(PetProfile profile) {
        return createEnhancedView(profile).getDisplayInfo();
    }

    private String resolveFeaturedBadge(PetProfile profile) {
        if (profile.isKidsCompatible() && profile.isOtherPetsCompatible()) {
            return "Family friendly";
        }
        if (profile.isKidsCompatible()) {
            return "Good with kids";
        }
        if (profile.isOtherPetsCompatible()) {
            return "Good with pets";
        }
        return "Needs special matching";
    }
}
