package com.royal.msmatch.patterns.iterator;

import com.royal.msmatch.dto.request.PetCandidateDTO;

import java.util.List;

public class AvailablePetCandidateCollection implements PetCandidateIterableCollection {

    private final List<PetCandidateDTO> candidates;

    public AvailablePetCandidateCollection(List<PetCandidateDTO> candidates) {
        this.candidates = candidates == null ? List.of() : List.copyOf(candidates);
    }

    @Override
    public PetCandidateIterator createIterator() {
        return new AvailablePetCandidateIterator(candidates);
    }
}
