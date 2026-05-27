package com.royal.msmatch.repository;

import com.royal.msmatch.model.Match;
import com.royal.msmatch.model.enums.MatchStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MatchRepository extends JpaRepository<Match, String> {

    List<Match> findByAdopterIdOrderByMatchScoreDesc(String adopterId);

    List<Match> findByPetProfileId(String petProfileId);

    List<Match> findByStatus(MatchStatus status);

    Optional<Match> findByAdopterIdAndPetProfileId(String adopterId, String petProfileId);
}
