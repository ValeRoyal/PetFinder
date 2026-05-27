package com.royal.msadopter.dto.request;

import java.util.List;

public record PreferencesRequestDTO(
        List<String> preferredSpecies,
        int minAge,
        int maxAge,
        List<String> preferredSizes,
        String energyMatch,
        boolean kidsFriendly,
        boolean otherPetsFriendly
) {
}
