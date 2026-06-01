package com.royal.msshelter.model;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
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
@Table(name = "shelter")
@Data
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Shelter {

    @Id
    @Column(name = "id", nullable = false, unique = true, length = 50)
    String id;

    @Column(name = "name", nullable = false, length = 100)
    String name;

    @Column(name = "location", nullable = false, length = 150)
    String location;

    @Column(name = "email", nullable = false, unique = true, length = 120)
    String email;

    @Column(name = "password", nullable = false, length = 120)
    String password;

    @Column(name = "phone", nullable = false, length = 30)
    String phone;

    @ElementCollection
    @CollectionTable(
            name = "shelter_photos",
            joinColumns = @JoinColumn(name = "shelter_id")
    )
    @Column(name = "photo_url", nullable = false, length = 255)
    List<String> photos = new ArrayList<>();

    @ElementCollection
    @CollectionTable(
            name = "shelter_videos",
            joinColumns = @JoinColumn(name = "shelter_id")
    )
    @Column(name = "video_url", nullable = false, length = 255)
    List<String> videos = new ArrayList<>();

    @ElementCollection
    @CollectionTable(
            name = "shelter_pet_profiles",
            joinColumns = @JoinColumn(name = "shelter_id")
    )
    @Column(name = "pet_profile_id", nullable = false, length = 50)
    List<String> petProfileIds = new ArrayList<>();

    @ElementCollection
    @CollectionTable(
            name = "shelter_veterinarians",
            joinColumns = @JoinColumn(name = "shelter_id")
    )
    @Column(name = "veterinarian_id", nullable = false, length = 50)
    List<String> veterinarianIds = new ArrayList<>();

    public void registerPet(String petProfileId) {
        if (!petProfileIds.contains(petProfileId)) {
            petProfileIds.add(petProfileId);
        }
    }

    public void removePet(String petProfileId) {
        petProfileIds.remove(petProfileId);
    }

    public void registerVeterinarian(String veterinarianId) {
        veterinarianIds.add(veterinarianId);
    }

    public boolean hasVeterinarian(String veterinarianId) {
        return veterinarianIds.contains(veterinarianId);
    }
}
