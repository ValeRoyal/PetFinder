package com.royal.msmatch.patterns.singleton;

import com.royal.msmatch.dto.request.AdopterMatchDTO;
import com.royal.msmatch.dto.request.PetCandidateDTO;
import com.royal.msmatch.patterns.strategy.CompatibilityStrategy;
import com.royal.msmatch.patterns.strategy.WeightedCompatibilityStrategy;

public class MatchingEngine {

    private static MatchingEngine instance;
    private CompatibilityStrategy strategy;

    private MatchingEngine() {
        this.strategy = new WeightedCompatibilityStrategy();
    }

    public static synchronized MatchingEngine getInstance() {
        if (instance == null) {
            instance = new MatchingEngine();
        }
        return instance;
    }

    public void setStrategy(CompatibilityStrategy strategy) {
        this.strategy = strategy;
    }

    public double getCompatibilityScore(AdopterMatchDTO adopter, PetCandidateDTO pet) {
        return strategy.calculate(adopter, pet);
    }

    public String getStrategyName() {
        return strategy.getName();
    }
}
