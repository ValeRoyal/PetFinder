package com.royal.msadopter.model;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.List;

@Embeddable
@Data
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Preferences {

    @ElementCollection
    @CollectionTable(
            name = "adopter_preferred_species",
            joinColumns = @JoinColumn(name = "adopter_id")
    )
    @Column(name = "species", nullable = false, length = 50)
    List<String> preferredSpecies = new ArrayList<>();

    @Column(name = "min_age")
    int minAge;

    @Column(name = "max_age")
    int maxAge;

    @ElementCollection
    @CollectionTable(
            name = "adopter_preferred_sizes",
            joinColumns = @JoinColumn(name = "adopter_id")
    )
    @Column(name = "size", nullable = false, length = 20)
    List<String> preferredSizes = new ArrayList<>();

    @Column(name = "energy_match", length = 30)
    String energyMatch;

    @Column(name = "kids_friendly")
    boolean kidsFriendly;

    @Column(name = "other_pets_friendly")
    boolean otherPetsFriendly;
}
