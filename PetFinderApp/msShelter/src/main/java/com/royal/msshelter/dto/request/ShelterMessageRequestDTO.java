package com.royal.msshelter.dto.request;

public record ShelterMessageRequestDTO(
        String id,
        String recipient,
        String subject,
        String content
) {
}
