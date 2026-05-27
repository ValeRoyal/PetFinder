package com.royal.msmatch.patterns.iterator;

import com.royal.msmatch.dto.request.PetCandidateDTO;

public interface PetCandidateIterator {

    boolean hasNext();

    PetCandidateDTO next();
}
