package com.royal.msvet.service;

import com.royal.msvet.dto.request.VaccineRequestDTO;
import com.royal.msvet.dto.response.VaccineResponseDTO;
import com.royal.msvet.model.Vaccine;
import com.royal.msvet.model.VaccinationCard;
import com.royal.msvet.model.Veterinarian;
import com.royal.msvet.repository.VaccineRepository;
import com.royal.msvet.repository.VaccinationCardRepository;
import com.royal.msvet.repository.VeterinarianRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VaccineService {

    private final VaccineRepository vaccineRepository;
    private final VaccinationCardRepository vaccinationCardRepository;
    private final VeterinarianRepository veterinarianRepository;

    // US-17 — addVaccine()
    public VaccineResponseDTO addVaccine(VaccineRequestDTO dto) {
        String id = requireId(dto.getId(), "Vaccine id is required");
        if (vaccineRepository.existsById(id)) {
            throw new IllegalArgumentException("Vaccine id already exists: " + id);
        }
        VaccinationCard card = vaccinationCardRepository.findById(dto.getVaccinationCardId())
                .orElseThrow(() -> new RuntimeException("Carnet no encontrado"));

        Veterinarian vet = veterinarianRepository.findById(dto.getAdministeredById())
                .orElseThrow(() -> new RuntimeException("Veterinario no encontrado"));

        Vaccine vaccine = new Vaccine();
        vaccine.setId(id);
        vaccine.setName(dto.getName());
        vaccine.setAppliedDate(dto.getAppliedDate());
        vaccine.setNextDueDate(dto.getNextDueDate());
        vaccine.setAdministeredBy(vet);
        vaccine.setVaccinationCard(card);
        vaccineRepository.save(vaccine);

        return VaccineResponseDTO.from(vaccine);
    }

    public VaccineResponseDTO updateVaccine(String id, VaccineRequestDTO dto) {
        Vaccine vaccine = vaccineRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vacuna no encontrada"));
        VaccinationCard card = vaccinationCardRepository.findById(dto.getVaccinationCardId())
                .orElseThrow(() -> new RuntimeException("Carnet no encontrado"));
        Veterinarian vet = veterinarianRepository.findById(dto.getAdministeredById())
                .orElseThrow(() -> new RuntimeException("Veterinario no encontrado"));

        vaccine.setName(dto.getName());
        vaccine.setAppliedDate(dto.getAppliedDate());
        vaccine.setNextDueDate(dto.getNextDueDate());
        vaccine.setAdministeredBy(vet);
        vaccine.setVaccinationCard(card);
        return VaccineResponseDTO.from(vaccineRepository.save(vaccine));
    }

    public void deleteVaccine(String id) {
        Vaccine vaccine = vaccineRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vacuna no encontrada"));
        vaccineRepository.delete(vaccine);
    }

    // RF-01.2 — próximas fechas de vacunación
    // alerta vacunas que vencen en los próximos 30 días
    public List<VaccineResponseDTO> getUpcoming(String petProfileId) {
        LocalDate today = LocalDate.now();
        LocalDate inThirtyDays = today.plusDays(30);
        return vaccineRepository.findByNextDueDateBetween(today, inThirtyDays)
                .stream()
                .filter(v -> v.getVaccinationCard()
                        .getPetProfileId()
                        .equals(petProfileId))
                .map(VaccineResponseDTO::from)
                .toList();
    }

    public List<VaccineResponseDTO> getByPet(String petProfileId) {
        return vaccineRepository.findByVaccinationCard_PetProfileId(petProfileId)
                .stream()
                .map(VaccineResponseDTO::from)
                .toList();
    }

    private String requireId(String id, String message) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException(message);
        }
        return id;
    }
}
