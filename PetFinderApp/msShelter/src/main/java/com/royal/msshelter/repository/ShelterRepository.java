package com.royal.msshelter.repository;

import com.royal.msshelter.model.Shelter;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ShelterRepository extends JpaRepository<Shelter, String> {

    Optional<Shelter> findByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCase(String email);
}
