package com.royal.msmatch.dto.response;

public record CompatibilityScoreResponseDTO(
        String petProfileId,
        String shelterId,
        double score,
        String detail
) {
}
