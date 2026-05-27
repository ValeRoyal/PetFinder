package com.royal.msmatch.model;

import com.royal.msmatch.model.enums.MatchStatus;
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
@Table(name = "pet_match")
@Data
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Match {

    @Id
    @Column(name = "id", nullable = false, unique = true, length = 50)
    String id;

    @Column(name = "adopter_id", nullable = false, length = 50)
    String adopterId;

    @Column(name = "pet_profile_id", nullable = false, length = 50)
    String petProfileId;

    @Column(name = "shelter_id", length = 50)
    String shelterId;

    @Column(name = "match_score", nullable = false)
    double matchScore;

    @Column(name = "message", length = 500)
    String message;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    MatchStatus status = MatchStatus.PENDING;

    @Column(name = "created_at", nullable = false)
    LocalDateTime createdAt;
}
