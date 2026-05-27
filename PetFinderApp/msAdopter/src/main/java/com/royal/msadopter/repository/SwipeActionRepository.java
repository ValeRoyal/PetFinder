package com.royal.msadopter.repository;

import com.royal.msadopter.model.SwipeAction;
import com.royal.msadopter.model.enums.SwipeDirection;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SwipeActionRepository extends JpaRepository<SwipeAction, String> {

    List<SwipeAction> findByAdopterIdOrderByCreatedAtDesc(String adopterId);

    List<SwipeAction> findByAdopterIdAndDirectionAndUndoneFalseOrderByCreatedAtDesc(
            String adopterId,
            SwipeDirection direction
    );

    Optional<SwipeAction> findTopByAdopterIdAndUndoneFalseOrderByCreatedAtDesc(String adopterId);
}
