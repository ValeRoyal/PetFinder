package com.royal.msvet.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name="veterinarian")
public class Veterinarian {
    @Id
    @Column(name="ID", nullable=false, unique=true, length=50)
    String id;

    @Column(name = "name",  nullable = false, unique = false, length = 50)
    String name;

    @Column(name="specialty", nullable = false, unique = false, length = 100)
    String specialty;

    @Column(name = "phoneNumber",  nullable = false, unique = false, length = 50)
    String phoneNumber;

    @Column(name = "email", nullable = false, unique = true, length = 100)
    String email;                           // para notificaciones

    @Column(name = "password", nullable = false, length = 120)
    String password;

    // Referencia al refugio
    @Column(name = "shelter_id", nullable = false, length = 50)
    String shelterId;                       // vet pertenece a un refugio

    @OneToMany(mappedBy = "veterinarian",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    List<Appointment> schedule = new ArrayList<>();

}
