package com.royal.msmatch.service;

import com.royal.msmatch.config.MatchingProperties;
import com.royal.msmatch.dto.request.MatchCalculationRequestDTO;
import com.royal.msmatch.dto.request.PetCandidateDTO;
import com.royal.msmatch.dto.request.SwipeMatchRequestDTO;
import com.royal.msmatch.dto.response.CompatibilityScoreResponseDTO;
import com.royal.msmatch.dto.response.MatchResponseDTO;
import com.royal.msmatch.integration.MatchCommunicationGateway;
import com.royal.msmatch.model.Match;
import com.royal.msmatch.model.enums.MatchStatus;
import com.royal.msmatch.patterns.iterator.AvailablePetCandidateCollection;
import com.royal.msmatch.patterns.iterator.PetCandidateIterator;
import com.royal.msmatch.patterns.observer.AdopterMatchSubscriber;
import com.royal.msmatch.patterns.observer.MatchPublisher;
import com.royal.msmatch.patterns.observer.ShelterMatchSubscriber;
import com.royal.msmatch.patterns.singleton.MatchingEngineClient;
import com.royal.msmatch.patterns.singleton.MatchingEngine;
import com.royal.msmatch.patterns.strategy.StrictCompatibilityStrategy;
import com.royal.msmatch.patterns.strategy.WeightedCompatibilityStrategy;
import com.royal.msmatch.repository.MatchRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class MatchService {

    private final MatchRepository repository;
    private final MatchingProperties properties;
    private final MatchCommunicationGateway communicationGateway;
    private final MatchPublisher matchPublisher;

    public MatchService(
            MatchRepository repository,
            MatchingProperties properties,
            MatchCommunicationGateway communicationGateway
    ) {
        this.repository = repository;
        this.properties = properties;
        this.communicationGateway = communicationGateway;
        this.matchPublisher = new MatchPublisher();
        this.matchPublisher.subscribe(new AdopterMatchSubscriber());
        this.matchPublisher.subscribe(new ShelterMatchSubscriber());
    }

    @Transactional(readOnly = true)
    public List<CompatibilityScoreResponseDTO> calculateRecommendations(MatchCalculationRequestDTO request, String strategyName) {
        MatchingEngine engine = configureEngine(strategyName);
        AvailablePetCandidateCollection collection = new AvailablePetCandidateCollection(request.candidates());
        PetCandidateIterator iterator = collection.createIterator();
        List<CompatibilityScoreResponseDTO> scores = new ArrayList<>();

        while (iterator.hasNext()) {
            PetCandidateDTO pet = iterator.next();
            double score = round(engine.getCompatibilityScore(request.adopter(), pet));
            if (score >= properties.minimumScore()) {
                scores.add(new CompatibilityScoreResponseDTO(
                        pet.id(),
                        pet.shelterId(),
                        score,
                        engine.getStrategyName() + " strategy"
                ));
            }
        }

        return scores.stream()
                .sorted(Comparator.comparingDouble(CompatibilityScoreResponseDTO::score).reversed())
                .toList();
    }

    @Transactional
    public MatchResponseDTO processSwipeMatch(SwipeMatchRequestDTO request) {
        Match match = repository.findByAdopterIdAndPetProfileId(request.adopterId(), request.petProfileId())
                .orElseGet(() -> createPendingMatch(request));

        match.setMatchScore(round(request.score()));
        match.setShelterId(request.shelterId());

        if (request.score() >= properties.minimumScore() && request.shelterApproves()) {
            match.setStatus(MatchStatus.MUTUAL);
            matchPublisher.publish(match);
        } else {
            match.setStatus(MatchStatus.PENDING);
            match.setMessage("Waiting for mutual approval or minimum score.");
        }

        return toResponse(repository.save(match));
    }

    @Transactional(readOnly = true)
    public List<MatchResponseDTO> findByAdopter(String adopterId) {
        return repository.findByAdopterIdOrderByMatchScoreDesc(adopterId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<MatchResponseDTO> findMutualMatches() {
        return repository.findByStatus(MatchStatus.MUTUAL).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public MatchResponseDTO updateStatus(String matchId, MatchStatus status) {
        Match match = repository.findById(matchId)
                .orElseThrow(() -> new IllegalArgumentException("Match not found: " + matchId));
        match.setStatus(status);
        return toResponse(repository.save(match));
    }

    public String communicationSummary() {
        // These clients are intentionally exposed through a gateway so this service remains the communicator
        // between match logic and the microservices it depends on.
        return "MatchCommunicationGateway ready: "
                + communicationGateway.petClient()
                + ", "
                + communicationGateway.adopterClient()
                + ", "
                + communicationGateway.shelterClient()
                + ", "
                + communicationGateway.notificationClient();
    }

    private MatchingEngine configureEngine(String strategyName) {
        MatchingEngine engine = new MatchingEngineClient().getSharedMatchingEngine();
        if ("STRICT".equalsIgnoreCase(strategyName)) {
            engine.setStrategy(new StrictCompatibilityStrategy());
        } else {
            engine.setStrategy(new WeightedCompatibilityStrategy());
        }
        return engine;
    }

    private Match createPendingMatch(SwipeMatchRequestDTO request) {
        String id = requireId(request.id(), "Match id is required");
        if (repository.existsById(id)) {
            throw new IllegalArgumentException("Match id already exists: " + id);
        }
        Match match = new Match();
        match.setId(id);
        match.setAdopterId(request.adopterId());
        match.setPetProfileId(request.petProfileId());
        match.setShelterId(request.shelterId());
        match.setCreatedAt(LocalDateTime.now());
        match.setStatus(MatchStatus.PENDING);
        return match;
    }

    private String requireId(String id, String message) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException(message);
        }
        return id;
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    private MatchResponseDTO toResponse(Match match) {
        return new MatchResponseDTO(
                match.getId(),
                match.getAdopterId(),
                match.getPetProfileId(),
                match.getShelterId(),
                match.getMatchScore(),
                match.getMessage(),
                match.getStatus(),
                match.getCreatedAt()
        );
    }
}
