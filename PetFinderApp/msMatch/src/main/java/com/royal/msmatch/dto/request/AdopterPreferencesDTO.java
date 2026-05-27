package com.royal.msmatch.dto.request;

import java.util.List;

public record AdopterPreferencesDTO(
        List<String> preferredSpecies,
        int minAge,
        int maxAge,
        List<String> preferredSizes,
        String energyMatch,
        boolean kidsFriendly,
        boolean otherPetsFriendly
) {
}
