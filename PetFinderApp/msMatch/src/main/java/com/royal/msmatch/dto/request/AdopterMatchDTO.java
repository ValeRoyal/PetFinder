package com.royal.msmatch.dto.request;

public record AdopterMatchDTO(
        String id,
        boolean hasKids,
        AdopterPreferencesDTO preferences
) {
}
