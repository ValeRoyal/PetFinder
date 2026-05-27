package com.royal.msmatch.patterns.strategy;

import com.royal.msmatch.dto.request.AdopterMatchDTO;
import com.royal.msmatch.dto.request.PetCandidateDTO;

public class StrictCompatibilityStrategy implements CompatibilityStrategy {

    private final WeightedCompatibilityStrategy weighted = new WeightedCompatibilityStrategy();

    @Override
    public double calculate(AdopterMatchDTO adopter, PetCandidateDTO pet) {
        double weightedScore = weighted.calculate(adopter, pet);
        if (weightedScore < 70.0) {
            return weightedScore;
        }
        if (adopter.hasKids() && !pet.kidsCompatible()) {
            return 0.0;
        }
        return weightedScore;
    }

    @Override
    public String getName() {
        return "STRICT";
    }
}
