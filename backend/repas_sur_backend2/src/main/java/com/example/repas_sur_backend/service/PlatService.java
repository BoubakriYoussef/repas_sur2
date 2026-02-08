package com.example.repas_sur_backend.service;

import com.example.repas_sur_backend.dto.IdCodeDto;
import com.example.repas_sur_backend.dto.PlatDto;
import com.example.repas_sur_backend.dto.PlatRequest;
import com.example.repas_sur_backend.exception.NotFoundException;
import com.example.repas_sur_backend.model.Allergene;
import com.example.repas_sur_backend.model.Plat;
import com.example.repas_sur_backend.repository.AllergeneRepository;
import com.example.repas_sur_backend.repository.PlatRepository;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PlatService {

    private final PlatRepository platRepository;
    private final AllergeneRepository allergeneRepository;

    public PlatService(PlatRepository platRepository, AllergeneRepository allergeneRepository) {
        this.platRepository = platRepository;
        this.allergeneRepository = allergeneRepository;
    }

    @Transactional(readOnly = true)
    public List<PlatDto> findAll() {
        return platRepository.findAll().stream().map(this::toDto).toList();
    }

    @Transactional(readOnly = true)
    public PlatDto getById(Long id) {
        return toDto(findEntity(id));
    }

    public PlatDto save(PlatRequest request) {
        Plat plat = new Plat();
        apply(plat, request);
        return toDto(platRepository.save(plat));
    }

    public PlatDto update(Long id, PlatRequest request) {
        Plat plat = findEntity(id);
        apply(plat, request);
        return toDto(platRepository.save(plat));
    }

    public void delete(Long id) {
        platRepository.deleteById(id);
    }

    private Plat findEntity(Long id) {
        return platRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Plat not found: " + id));
    }

    private void apply(Plat plat, PlatRequest request) {
        plat.setNom(request.nom());
        plat.setCategorie(request.categorie());
        plat.setDescription(request.description());
        plat.setContientPorc(Boolean.TRUE.equals(request.contientPorc()));
        plat.setEstVegetarien(Boolean.TRUE.equals(request.estVegetarien()));
        plat.setAllergenes(fetchAllergenes(request.allergeneIds()));
    }

    private Set<Allergene> fetchAllergenes(Set<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return new HashSet<>();
        }
        return new HashSet<>(allergeneRepository.findAllById(ids));
    }

    private PlatDto toDto(Plat plat) {
        Set<IdCodeDto> allergenes = plat.getAllergenes().stream()
            .map(allergene -> new IdCodeDto(allergene.getId(), allergene.getCode(), allergene.getLibelle()))
            .collect(java.util.stream.Collectors.toSet());

        return new PlatDto(
            plat.getId(),
            plat.getNom(),
            plat.getCategorie(),
            plat.getDescription(),
            plat.isContientPorc(),
            plat.isEstVegetarien(),
            allergenes
        );
    }
}
