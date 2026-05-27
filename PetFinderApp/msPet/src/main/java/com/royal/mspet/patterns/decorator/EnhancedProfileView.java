package com.royal.mspet.patterns.decorator;

import java.util.List;

public class EnhancedProfileView extends ProfileViewDecorator {

    private final String featuredBadge;
    private final List<String> medicalEventIds;

    public EnhancedProfileView(ProfileView wrapped, String featuredBadge, List<String> medicalEventIds) {
        super(wrapped);
        this.featuredBadge = featuredBadge;
        this.medicalEventIds = medicalEventIds == null ? List.of() : List.copyOf(medicalEventIds);
    }

    @Override
    public String getDisplayInfo() {
        return wrapped.getDisplayInfo()
                + " Badge: " + getFeaturedBadge()
                + ". Medical events registered: " + medicalEventIds.size() + ".";
    }

    public String getFeaturedBadge() {
        return featuredBadge;
    }

    public List<String> getMedicalEventIds() {
        return medicalEventIds;
    }
}
