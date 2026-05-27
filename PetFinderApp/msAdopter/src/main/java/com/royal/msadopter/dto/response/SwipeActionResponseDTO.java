package com.royal.msadopter.dto.response;

import com.royal.msadopter.model.enums.SwipeDirection;

import java.time.LocalDateTime;

public record SwipeActionResponseDTO(
        String id,
        String adopterId,
        String petProfileId,
        SwipeDirection direction,
        LocalDateTime createdAt,
        boolean undone
) {
}
