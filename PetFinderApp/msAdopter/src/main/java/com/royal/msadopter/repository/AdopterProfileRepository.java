package com.royal.msadopter.repository;

import com.royal.msadopter.model.AdopterProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdopterProfileRepository extends JpaRepository<AdopterProfile, String> {

    Optional<AdopterProfile> findByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCase(String email);
}
