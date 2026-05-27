package com.royal.msshelter.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ShelterReport {

    String shelterId;
    int totalPets;
    int totalVeterinarians;
    int totalMessagesSent;
}
