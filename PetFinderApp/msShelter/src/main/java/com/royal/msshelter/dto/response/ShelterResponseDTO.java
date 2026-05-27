package com.royal.msshelter.dto.response;

import java.util.List;

public record ShelterResponseDTO(
        String id,
        String name,
        String location,
        String email,
        String phone,
        List<String> photos,
        List<String> videos,
        List<String> petProfileIds,
        List<String> veterinarianIds
) {
}
