package com.royal.mspet.service;

import com.royal.mspet.dto.request.PetProfileRequestDTO;
import com.royal.mspet.dto.response.PetProfileResponseDTO;
import com.royal.mspet.model.PetProfile;
import com.royal.mspet.model.enums.PetStatus;
import com.royal.mspet.patterns.facade.PetProfileFacade;
import com.royal.mspet.repository.PetProfileRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class PetProfileService {

    private final PetProfileRepository repository;
    private final PetProfileFacade facade;

    public PetProfileService(PetProfileRepository repository) {
        this.repository = repository;
        this.facade = new PetProfileFacade();
    }

    @Transactional
    public PetProfileResponseDTO create(PetProfileRequestDTO request) {
        String id = requireId(request.id(), "Pet profile id is required");
        if (repository.existsById(id)) {
            throw new IllegalArgumentException("Pet profile id already exists: " + id);
        }

        PetProfile profile = facade.createPetProfile(
                request.species(),
                id,
                request.name(),
                request.breed(),
                request.age(),
                request.sex(),
                request.size()
        );

        applyOptionalProfileData(profile, request);
        return toResponse(repository.save(profile), true);
    }

    @Transactional(readOnly = true)
    public List<PetProfileResponseDTO> findAll() {
        return repository.findAll().stream()
                .map(profile -> toResponse(profile, false))
                .toList();
    }

    @Transactional(readOnly = true)
    public PetProfileResponseDTO findById(String id) {
        return toResponse(findEntityById(id), true);
    }

    @Transactional
    public PetProfileResponseDTO update(String id, PetProfileRequestDTO request) {
        PetProfile profile = findEntityById(id);
        profile.setName(request.name());
        profile.setSpecies(request.species());
        profile.setBreed(request.breed());
        profile.setAge(request.age());
        profile.setSex(request.sex());
        profile.setSize(request.size());
        applyOptionalProfileData(profile, request);
        return toResponse(repository.save(profile), true);
    }

    @Transactional
    public void delete(String id) {
        PetProfile profile = findEntityById(id);
        repository.delete(profile);
    }

    @Transactional(readOnly = true)
    public List<PetProfileResponseDTO> findAvailable(String species) {
        List<PetProfile> profiles = species == null || species.isBlank()
                ? repository.findByStatus(PetStatus.AVAILABLE)
                : repository.findByStatusAndSpeciesIgnoreCase(PetStatus.AVAILABLE, species);

        return profiles.stream()
                .map(profile -> toResponse(profile, false))
                .toList();
    }

    @Transactional
    public PetProfileResponseDTO updateStatus(String id, PetStatus status) {
        PetProfile profile = findEntityById(id);
        profile.setStatus(status);
        return toResponse(repository.save(profile), true);
    }

    @Transactional
    public PetProfileResponseDTO addPhoto(String id, String photoUrl) {
        PetProfile profile = findEntityById(id);
        profile.addPhoto(photoUrl);
        return toResponse(repository.save(profile), true);
    }

    @Transactional
    public PetProfileResponseDTO addMedicalEventReference(String id, String medicalEventId) {
        PetProfile profile = findEntityById(id);
        profile.addMedicalEventId(medicalEventId);
        return toResponse(repository.save(profile), true);
    }

    @Transactional(readOnly = true)
    public String getBasicDisplayInfo(String id) {
        return facade.getBasicDisplayInfo(findEntityById(id));
    }

    @Transactional(readOnly = true)
    public String getEnhancedDisplayInfo(String id) {
        return facade.getEnhancedDisplayInfo(findEntityById(id));
    }

    private PetProfile findEntityById(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Pet profile not found: " + id));
    }

    private String requireId(String id, String message) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException(message);
        }
        return id;
    }

    private void applyOptionalProfileData(PetProfile profile, PetProfileRequestDTO request) {
        if (request.energyLevel() != null && !request.energyLevel().isBlank()) {
            profile.setEnergyLevel(request.energyLevel());
        }
        profile.setKidsCompatible(request.kidsCompatible());
        profile.setOtherPetsCompatible(request.otherPetsCompatible());
        if (request.photos() != null) {
            profile.getPhotos().clear();
            profile.getPhotos().addAll(request.photos());
        }
        if (request.bio() != null) {
            profile.setBio(request.bio());
        }
    }

    private PetProfileResponseDTO toResponse(PetProfile profile, boolean enhanced) {
        String displayInfo = enhanced
                ? facade.getEnhancedDisplayInfo(profile)
                : facade.getBasicDisplayInfo(profile);

        return new PetProfileResponseDTO(
                profile.getId(),
                profile.getName(),
                profile.getSpecies(),
                profile.getBreed(),
                profile.getAge(),
                profile.getSex(),
                profile.getSize(),
                profile.getEnergyLevel(),
                profile.isKidsCompatible(),
                profile.isOtherPetsCompatible(),
                List.copyOf(profile.getPhotos()),
                profile.getBio(),
                profile.getVaccinationCardId(),
                List.copyOf(profile.getMedicalEventIds()),
                profile.getStatus(),
                displayInfo
        );
    }
}
