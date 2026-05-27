package com.royal.mspet.repository;

import com.royal.mspet.model.PetProfile;
import com.royal.mspet.model.enums.PetStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PetProfileRepository extends JpaRepository<PetProfile, String> {

    List<PetProfile> findByStatus(PetStatus status);

    List<PetProfile> findBySpeciesIgnoreCase(String species);

    List<PetProfile> findByStatusAndSpeciesIgnoreCase(PetStatus status, String species);
}
