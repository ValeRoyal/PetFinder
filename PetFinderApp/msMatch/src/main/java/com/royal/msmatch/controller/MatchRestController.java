package com.royal.msmatch.controller;

import com.royal.msmatch.dto.request.MatchCalculationRequestDTO;
import com.royal.msmatch.dto.request.SwipeMatchRequestDTO;
import com.royal.msmatch.dto.response.CompatibilityScoreResponseDTO;
import com.royal.msmatch.dto.response.MatchResponseDTO;
import com.royal.msmatch.model.enums.MatchStatus;
import com.royal.msmatch.service.MatchService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/matches")
public class MatchRestController {

    private final MatchService service;

    public MatchRestController(MatchService service) {
        this.service = service;
    }

    @PostMapping("/recommendations")
    public ResponseEntity<List<CompatibilityScoreResponseDTO>> calculateRecommendations(
            @RequestBody MatchCalculationRequestDTO request,
            @RequestParam(defaultValue = "WEIGHTED") String strategy
    ) {
        return ResponseEntity.ok(service.calculateRecommendations(request, strategy));
    }

    @PostMapping("/swipes")
    public ResponseEntity<MatchResponseDTO> processSwipeMatch(@RequestBody SwipeMatchRequestDTO request) {
        return ResponseEntity.ok(service.processSwipeMatch(request));
    }

    @GetMapping("/adopters/{adopterId}")
    public ResponseEntity<List<MatchResponseDTO>> findByAdopter(@PathVariable String adopterId) {
        return ResponseEntity.ok(service.findByAdopter(adopterId));
    }

    @GetMapping("/mutual")
    public ResponseEntity<List<MatchResponseDTO>> findMutualMatches() {
        return ResponseEntity.ok(service.findMutualMatches());
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<MatchResponseDTO> updateStatus(
            @PathVariable String id,
            @RequestParam MatchStatus status
    ) {
        return ResponseEntity.ok(service.updateStatus(id, status));
    }

    @GetMapping("/communication")
    public ResponseEntity<String> communicationSummary() {
        return ResponseEntity.ok(service.communicationSummary());
    }
}
