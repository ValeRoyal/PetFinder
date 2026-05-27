package com.royal.msmatch.dto.request;

public record SwipeMatchRequestDTO(
        String id,
        String adopterId,
        String petProfileId,
        String shelterId,
        double score,
        boolean shelterApproves
) {
}
