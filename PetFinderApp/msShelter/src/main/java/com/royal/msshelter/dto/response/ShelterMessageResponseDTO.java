package com.royal.msshelter.dto.response;

import java.time.LocalDateTime;

public record ShelterMessageResponseDTO(
        String id,
        String shelterId,
        String recipient,
        String subject,
        String content,
        LocalDateTime sentAt
) {
}
