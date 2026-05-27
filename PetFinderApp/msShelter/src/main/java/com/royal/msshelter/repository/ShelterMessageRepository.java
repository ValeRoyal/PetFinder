package com.royal.msshelter.repository;

import com.royal.msshelter.model.ShelterMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ShelterMessageRepository extends JpaRepository<ShelterMessage, String> {

    List<ShelterMessage> findByShelterIdOrderBySentAtDesc(String shelterId);

    int countByShelterId(String shelterId);
}
