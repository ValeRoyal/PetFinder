package com.royal.msmatch.dto.request;

import java.util.List;

public record MatchCalculationRequestDTO(
        AdopterMatchDTO adopter,
        List<PetCandidateDTO> candidates
) {
}
