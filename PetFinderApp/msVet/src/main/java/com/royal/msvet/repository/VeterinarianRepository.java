package com.royal.msvet.repository;

import com.royal.msvet.model.Veterinarian;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface VeterinarianRepository extends JpaRepository<Veterinarian, String> {
    List<Veterinarian> findByShelterId(String shelterId);
    java.util.Optional<Veterinarian> findByEmailIgnoreCase(String email);
    boolean existsByEmailIgnoreCase(String email);
}
