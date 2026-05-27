package com.royal.msmatch.patterns.strategy;

import com.royal.msmatch.dto.request.AdopterMatchDTO;
import com.royal.msmatch.dto.request.PetCandidateDTO;

public interface CompatibilityStrategy {

    double calculate(AdopterMatchDTO adopter, PetCandidateDTO pet);

    String getName();
}
