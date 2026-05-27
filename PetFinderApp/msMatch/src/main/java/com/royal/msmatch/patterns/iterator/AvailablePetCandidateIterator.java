package com.royal.msmatch.patterns.iterator;

import com.royal.msmatch.dto.request.PetCandidateDTO;

import java.util.List;

public class AvailablePetCandidateIterator implements PetCandidateIterator {

    private final List<PetCandidateDTO> candidates;
    private int position;

    public AvailablePetCandidateIterator(List<PetCandidateDTO> candidates) {
        this.candidates = candidates == null ? List.of() : candidates;
    }

    @Override
    public boolean hasNext() {
        while (position < candidates.size()) {
            String status = candidates.get(position).status();
            if (status == null || status.equalsIgnoreCase("AVAILABLE")) {
                return true;
            }
            position++;
        }
        return false;
    }

    @Override
    public PetCandidateDTO next() {
        if (!hasNext()) {
            throw new IllegalStateException("No more available pet candidates");
        }
        return candidates.get(position++);
    }
}
