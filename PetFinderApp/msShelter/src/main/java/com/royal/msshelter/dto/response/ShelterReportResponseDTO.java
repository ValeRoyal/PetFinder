package com.royal.msshelter.dto.response;

public record ShelterReportResponseDTO(
        String shelterId,
        int totalPets,
        int totalVeterinarians,
        int totalMessagesSent
) {
}
