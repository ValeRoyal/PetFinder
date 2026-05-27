package com.royal.msshelter.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Entity
@Table(name = "shelter_message")
@Data
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ShelterMessage {

    @Id
    @Column(name = "id", nullable = false, unique = true, length = 50)
    String id;

    @Column(name = "shelter_id", nullable = false, length = 50)
    String shelterId;

    @Column(name = "recipient", nullable = false, length = 120)
    String recipient;

    @Column(name = "subject", nullable = false, length = 150)
    String subject;

    @Column(name = "content", nullable = false, length = 1000)
    String content;

    @Column(name = "sent_at", nullable = false)
    LocalDateTime sentAt;
}
