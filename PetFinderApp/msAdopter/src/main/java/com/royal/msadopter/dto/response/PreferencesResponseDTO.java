package com.royal.msadopter.dto.response;

import java.util.List;

public record PreferencesResponseDTO(
        List<String> preferredSpecies,
        int minAge,
        int maxAge,
        List<String> preferredSizes,
        String energyMatch,
        boolean kidsFriendly,
        boolean otherPetsFriendly
) {
}
