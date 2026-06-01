package com.royal.mspet.model;

import com.royal.mspet.model.enums.PetStatus;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pet_profile")
@Data
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PetProfile {

    @Id
    @Column(name = "id", nullable = false, unique = true, length = 50)
    String id;

    @Column(name = "name", nullable = false, length = 50)
    String name;

    @Column(name = "species", nullable = false, length = 50)
    String species;

    @Column(name = "breed", nullable = false, length = 50)
    String breed;

    @Column(name = "age", nullable = false)
    int age;

    @Column(name = "sex", nullable = false, length = 20)
    String sex;

    @Column(name = "size", nullable = false, length = 20)
    String size;

    @Column(name = "energy_level", nullable = false, length = 30)
    String energyLevel;

    @Column(name = "kids_compatible", nullable = false)
    boolean kidsCompatible;

    @Column(name = "other_pets_compatible", nullable = false)
    boolean otherPetsCompatible;

    @ElementCollection
    @CollectionTable(
            name = "pet_profile_photos",
            joinColumns = @JoinColumn(name = "pet_profile_id")
    )
    @Column(name = "photo_url", nullable = false, columnDefinition = "TEXT")
    List<String> photos = new ArrayList<>();

    @Column(name = "bio", length = 1500)
    String bio;

    @Column(name = "vaccination_card_id", length = 50)
    String vaccinationCardId;

    @ElementCollection
    @CollectionTable(
            name = "pet_profile_medical_events",
            joinColumns = @JoinColumn(name = "pet_profile_id")
    )
    @Column(name = "medical_event_id", nullable = false, length = 50)
    List<String> medicalEventIds = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    PetStatus status = PetStatus.AVAILABLE;

    public void addPhoto(String photoUrl) {
        photos.add(photoUrl);
    }

    public void addMedicalEventId(String medicalEventId) {
        medicalEventIds.add(medicalEventId);
    }
}
