package com.royal.msadopter.service;

import com.royal.msadopter.dto.request.AdopterProfileRequestDTO;
import com.royal.msadopter.dto.request.PreferencesRequestDTO;
import com.royal.msadopter.dto.response.AdopterProfileResponseDTO;
import com.royal.msadopter.dto.response.PreferencesResponseDTO;
import com.royal.msadopter.dto.response.SwipeActionResponseDTO;
import com.royal.msadopter.model.AdopterProfile;
import com.royal.msadopter.model.Preferences;
import com.royal.msadopter.model.SwipeAction;
import com.royal.msadopter.model.enums.SwipeDirection;
import com.royal.msadopter.patterns.command.SwipeClient;
import com.royal.msadopter.repository.AdopterProfileRepository;
import com.royal.msadopter.repository.SwipeActionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

@Service
public class AdopterProfileService {

    private final AdopterProfileRepository adopterRepository;
    private final SwipeActionRepository swipeRepository;
    private final SwipeClient swipeClient;

    public AdopterProfileService(
            AdopterProfileRepository adopterRepository,
            SwipeActionRepository swipeRepository
    ) {
        this.adopterRepository = adopterRepository;
        this.swipeRepository = swipeRepository;
        this.swipeClient = new SwipeClient();
    }

    @Transactional
    public AdopterProfileResponseDTO create(AdopterProfileRequestDTO request) {
        validateEmailAvailability(request.email(), null);

        AdopterProfile adopter = new AdopterProfile();
        adopter.setId(generateNextId());
        applyProfileData(adopter, request);

        return toResponse(adopterRepository.save(adopter));
    }

    @Transactional(readOnly = true)
    public List<AdopterProfileResponseDTO> findAll() {
        return adopterRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public AdopterProfileResponseDTO findById(String id) {
        return toResponse(findEntityById(id));
    }

    @Transactional(readOnly = true)
    public AdopterProfileResponseDTO findByEmail(String email) {
        return adopterRepository.findByEmailIgnoreCase(email)
                .map(this::toResponse)
                .orElseThrow(() -> new IllegalArgumentException("Adopter not found by email: " + email));
    }

    @Transactional(readOnly = true)
    public AdopterProfileResponseDTO login(String identifier, String password) {
        AdopterProfile adopter = identifier.contains("@")
                ? adopterRepository.findByEmailIgnoreCase(identifier)
                    .orElseThrow(() -> new IllegalArgumentException("Adopter not found by email: " + identifier))
                : findEntityById(identifier);

        if (password == null || password.isBlank() || !matchesPassword(adopter.getPassword(), password)) {
            throw new IllegalArgumentException("Invalid password");
        }

        return toResponse(adopter);
    }

    @Transactional
    public AdopterProfileResponseDTO update(String id, AdopterProfileRequestDTO request) {
        AdopterProfile adopter = findEntityById(id);
        validateEmailAvailability(request.email(), adopter.getId());
        applyProfileData(adopter, request);
        return toResponse(adopterRepository.save(adopter));
    }

    @Transactional
    public AdopterProfileResponseDTO updatePreferences(String id, PreferencesRequestDTO request) {
        AdopterProfile adopter = findEntityById(id);
        adopter.updatePreferences(toPreferences(request));
        return toResponse(adopterRepository.save(adopter));
    }

    @Transactional
    public SwipeActionResponseDTO swipeRight(String adopterId, String swipeId, String petProfileId) {
        AdopterProfile adopter = findEntityById(adopterId);
        String id = requireId(swipeId, "Swipe id is required");
        if (swipeRepository.existsById(id)) {
            throw new IllegalArgumentException("Swipe id already exists: " + id);
        }
        SwipeAction action = swipeClient.swipeRight(adopter, id, petProfileId);
        adopterRepository.save(adopter);
        return toSwipeResponse(action);
    }

    @Transactional
    public SwipeActionResponseDTO swipeLeft(String adopterId, String swipeId, String petProfileId) {
        AdopterProfile adopter = findEntityById(adopterId);
        String id = requireId(swipeId, "Swipe id is required");
        if (swipeRepository.existsById(id)) {
            throw new IllegalArgumentException("Swipe id already exists: " + id);
        }
        SwipeAction action = swipeClient.swipeLeft(adopter, id, petProfileId);
        adopterRepository.save(adopter);
        return toSwipeResponse(action);
    }

    @Transactional
    public SwipeActionResponseDTO undoLastSwipe(String adopterId) {
        SwipeAction action = swipeRepository.findTopByAdopterIdAndUndoneFalseOrderByCreatedAtDesc(adopterId)
                .orElseThrow(() -> new IllegalArgumentException("No swipe available to undo for adopter: " + adopterId));
        action.setUndone(true);
        return toSwipeResponse(swipeRepository.save(action));
    }

    @Transactional(readOnly = true)
    public List<SwipeActionResponseDTO> findSwipeHistory(String adopterId) {
        return swipeRepository.findByAdopterIdOrderByCreatedAtDesc(adopterId).stream()
                .map(this::toSwipeResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<SwipeActionResponseDTO> findLikedPets(String adopterId) {
        return swipeRepository.findByAdopterIdAndDirectionAndUndoneFalseOrderByCreatedAtDesc(
                        adopterId,
                        SwipeDirection.RIGHT
                )
                .stream()
                .map(this::toSwipeResponse)
                .toList();
    }

    private AdopterProfile findEntityById(String id) {
        return adopterRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Adopter not found: " + id));
    }

    private void applyProfileData(AdopterProfile adopter, AdopterProfileRequestDTO request) {
        adopter.setName(request.name());
        adopter.setEmail(request.email());
        if (adopter.getId() == null) {
            adopter.setPassword(requirePassword(request.password()));
        } else if (request.password() != null && !request.password().isBlank()) {
            adopter.setPassword(request.password());
        }
        adopter.setPhone(request.phone());
        adopter.setLocation(request.location());
        adopter.setHousing(request.housing());
        adopter.setHasKids(request.hasKids());

        adopter.getCurrentPets().clear();
        if (request.currentPets() != null) {
            adopter.getCurrentPets().addAll(request.currentPets());
        }

        if (request.preferences() != null) {
            adopter.setPreferences(toPreferences(request.preferences()));
        }
    }

    private Preferences toPreferences(PreferencesRequestDTO request) {
        Preferences preferences = new Preferences();
        preferences.setMinAge(request.minAge());
        preferences.setMaxAge(request.maxAge());
        preferences.setEnergyMatch(request.energyMatch());
        preferences.setKidsFriendly(request.kidsFriendly());
        preferences.setOtherPetsFriendly(request.otherPetsFriendly());

        if (request.preferredSpecies() != null) {
            preferences.getPreferredSpecies().addAll(request.preferredSpecies());
        }
        if (request.preferredSizes() != null) {
            preferences.getPreferredSizes().addAll(request.preferredSizes());
        }

        return preferences;
    }

    private String requireId(String id, String message) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException(message);
        }
        return id;
    }

    private String requirePassword(String password) {
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("Password is required");
        }
        return password;
    }

    private void validateEmailAvailability(String email, String currentAdopterId) {
        adopterRepository.findByEmailIgnoreCase(email)
                .filter(existing -> currentAdopterId == null || !existing.getId().equals(currentAdopterId))
                .ifPresent(existing -> {
                    throw new IllegalArgumentException("Adopter email already exists: " + email);
                });
    }

    private boolean matchesPassword(String storedPassword, String providedPassword) {
        return storedPassword != null && storedPassword.equals(providedPassword);
    }

    private String generateNextId() {
        int max = adopterRepository.findAll().stream()
                .map(AdopterProfile::getId)
                .filter(id -> id != null && id.toUpperCase(Locale.ROOT).startsWith("ADOPTER-"))
                .map(id -> id.substring("ADOPTER-".length()))
                .map(value -> {
                    try {
                        return Integer.parseInt(value);
                    } catch (NumberFormatException ex) {
                        return 0;
                    }
                })
                .max(Integer::compareTo)
                .orElse(0);

        return String.format("ADOPTER-%03d", max + 1);
    }

    private AdopterProfileResponseDTO toResponse(AdopterProfile adopter) {
        return new AdopterProfileResponseDTO(
                adopter.getId(),
                adopter.getName(),
                adopter.getEmail(),
                adopter.getPhone(),
                adopter.getLocation(),
                adopter.getHousing(),
                adopter.isHasKids(),
                List.copyOf(adopter.getCurrentPets()),
                toPreferencesResponse(adopter.getPreferences()),
                adopter.getSwipeHistory().stream()
                        .map(this::toSwipeResponse)
                        .toList()
        );
    }

    private PreferencesResponseDTO toPreferencesResponse(Preferences preferences) {
        return new PreferencesResponseDTO(
                List.copyOf(preferences.getPreferredSpecies()),
                preferences.getMinAge(),
                preferences.getMaxAge(),
                List.copyOf(preferences.getPreferredSizes()),
                preferences.getEnergyMatch(),
                preferences.isKidsFriendly(),
                preferences.isOtherPetsFriendly()
        );
    }

    private SwipeActionResponseDTO toSwipeResponse(SwipeAction action) {
        return new SwipeActionResponseDTO(
                action.getId(),
                action.getAdopterId(),
                action.getPetProfileId(),
                action.getDirection(),
                action.getCreatedAt(),
                action.isUndone()
        );
    }
}
