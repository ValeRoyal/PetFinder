package com.royal.msadopter.dto.response;

import java.util.List;

public record AdopterProfileResponseDTO(
        String id,
        String name,
        String email,
        String phone,
        String location,
        String housing,
        boolean hasKids,
        List<String> currentPets,
        PreferencesResponseDTO preferences,
        List<SwipeActionResponseDTO> swipeHistory
) {
}
