package com.royal.msadopter.model;

import com.royal.msadopter.model.enums.SwipeDirection;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Entity
@Table(name = "swipe_action")
@Data
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SwipeAction {

    @Id
    @Column(name = "id", nullable = false, unique = true, length = 50)
    String id;

    @Column(name = "adopter_id", nullable = false, length = 50)
    String adopterId;

    @Column(name = "pet_profile_id", nullable = false, length = 50)
    String petProfileId;

    @Enumerated(EnumType.STRING)
    @Column(name = "direction", nullable = false, length = 20)
    SwipeDirection direction;

    @Column(name = "created_at", nullable = false)
    LocalDateTime createdAt;

    @Column(name = "undone", nullable = false)
    boolean undone;
}
