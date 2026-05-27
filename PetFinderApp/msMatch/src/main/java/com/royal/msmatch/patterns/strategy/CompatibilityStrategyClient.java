package com.royal.msmatch.patterns.strategy;

import com.royal.msmatch.dto.request.AdopterMatchDTO;
import com.royal.msmatch.dto.request.PetCandidateDTO;
import com.royal.msmatch.patterns.singleton.MatchingEngine;

public class CompatibilityStrategyClient {

    private final MatchingEngine context;

    public CompatibilityStrategyClient(MatchingEngine context) {
        this.context = context;
    }

    public double calculateWithStrategy(
            CompatibilityStrategy strategy,
            AdopterMatchDTO adopter,
            PetCandidateDTO pet
    ) {
        context.setStrategy(strategy);
        return context.getCompatibilityScore(adopter, pet);
    }
}
