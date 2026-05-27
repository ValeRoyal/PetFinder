package com.royal.msshelter.dto.request;

import java.util.List;

public record ShelterRequestDTO(
        String id,
        String name,
        String location,
        String email,
        String phone,
        List<String> photos,
        List<String> videos
) {
}
