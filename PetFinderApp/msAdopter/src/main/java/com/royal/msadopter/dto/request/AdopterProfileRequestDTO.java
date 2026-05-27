package com.royal.msadopter.dto.request;

import java.util.List;

public record AdopterProfileRequestDTO(
        String id,
        String name,
        String email,
        String phone,
        String location,
        String housing,
        boolean hasKids,
        List<String> currentPets,
        PreferencesRequestDTO preferences
) {
}
