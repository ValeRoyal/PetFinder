package com.royal.msmatch.dto.response;

import com.royal.msmatch.model.enums.MatchStatus;

import java.time.LocalDateTime;

public record MatchResponseDTO(
        String id,
        String adopterId,
        String petProfileId,
        String shelterId,
        double matchScore,
        String message,
        MatchStatus status,
        LocalDateTime createdAt
) {
}
