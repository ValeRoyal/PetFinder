package com.royal.msmatch.patterns.strategy;

import com.royal.msmatch.dto.request.AdopterMatchDTO;
import com.royal.msmatch.dto.request.AdopterPreferencesDTO;
import com.royal.msmatch.dto.request.PetCandidateDTO;

public class WeightedCompatibilityStrategy implements CompatibilityStrategy {

    @Override
    public double calculate(AdopterMatchDTO adopter, PetCandidateDTO pet) {
        AdopterPreferencesDTO preferences = adopter.preferences();
        double score = 0.0;

        if (containsIgnoreCase(preferences.preferredSpecies(), pet.species())) {
            score += 25.0;
        }
        if (pet.age() >= preferences.minAge() && pet.age() <= preferences.maxAge()) {
            score += 20.0;
        }
        if (containsIgnoreCase(preferences.preferredSizes(), pet.size())) {
            score += 15.0;
        }
        if (equalsIgnoreCase(preferences.energyMatch(), pet.energyLevel())) {
            score += 15.0;
        }
        if (!adopter.hasKids() || pet.kidsCompatible() == preferences.kidsFriendly()) {
            score += 15.0;
        }
        if (pet.otherPetsCompatible() == preferences.otherPetsFriendly()) {
            score += 10.0;
        }

        return Math.min(score, 100.0);
    }

    @Override
    public String getName() {
        return "WEIGHTED";
    }

    private boolean containsIgnoreCase(Iterable<String> values, String candidate) {
        if (values == null || candidate == null) {
            return false;
        }
        for (String value : values) {
            if (equalsIgnoreCase(value, candidate)) {
                return true;
            }
        }
        return false;
    }

    private boolean equalsIgnoreCase(String left, String right) {
        return left != null && right != null && left.equalsIgnoreCase(right);
    }
}
