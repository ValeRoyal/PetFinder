package com.royal.msadopter.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "adopter_profile")
@Data
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AdopterProfile {

    @Id
    @Column(name = "id", nullable = false, unique = true, length = 50)
    String id;

    @Column(name = "name", nullable = false, length = 80)
    String name;

    @Column(name = "email", nullable = false, unique = true, length = 120)
    String email;

    @Column(name = "phone", nullable = false, length = 30)
    String phone;

    @Column(name = "location", nullable = false, length = 120)
    String location;

    @Column(name = "housing", nullable = false, length = 60)
    String housing;

    @Column(name = "has_kids", nullable = false)
    boolean hasKids;

    @ElementCollection
    @CollectionTable(
            name = "adopter_current_pets",
            joinColumns = @JoinColumn(name = "adopter_id")
    )
    @Column(name = "pet_description", nullable = false, length = 120)
    List<String> currentPets = new ArrayList<>();

    @Embedded
    Preferences preferences = new Preferences();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "adopter_id")
    List<SwipeAction> swipeHistory = new ArrayList<>();

    public void updatePreferences(Preferences preferences) {
        this.preferences = preferences;
    }

    public void registerSwipe(SwipeAction swipeAction) {
        swipeHistory.add(swipeAction);
    }
}
