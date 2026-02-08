package com.example.repas_sur_backend.service;

import com.example.repas_sur_backend.dto.AllergeneDto;
import com.example.repas_sur_backend.dto.AllergeneRequest;
import com.example.repas_sur_backend.exception.NotFoundException;
import com.example.repas_sur_backend.model.Allergene;
import com.example.repas_sur_backend.repository.AllergeneRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AllergeneService {

    private final AllergeneRepository allergeneRepository;

    public AllergeneService(AllergeneRepository allergeneRepository) {
        this.allergeneRepository = allergeneRepository;
    }

    @Transactional(readOnly = true)
    public List<AllergeneDto> findAll() {
        return allergeneRepository.findAll().stream().map(this::toDto).toList();
    }

    @Transactional(readOnly = true)
    public AllergeneDto getById(Long id) {
        return toDto(findEntity(id));
    }

    public AllergeneDto save(AllergeneRequest request) {
        Allergene allergene = new Allergene();
        apply(allergene, request);
        return toDto(allergeneRepository.save(allergene));
    }

    public AllergeneDto update(Long id, AllergeneRequest request) {
        Allergene allergene = findEntity(id);
        apply(allergene, request);
        return toDto(allergeneRepository.save(allergene));
    }

    public void delete(Long id) {
        allergeneRepository.deleteById(id);
    }

    private Allergene findEntity(Long id) {
        return allergeneRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Allergene not found: " + id));
    }

    private void apply(Allergene allergene, AllergeneRequest request) {
        allergene.setCode(request.code());
        allergene.setLibelle(request.libelle());
        allergene.setDescription(request.description());
    }

    private AllergeneDto toDto(Allergene allergene) {
        return new AllergeneDto(
            allergene.getId(),
            allergene.getCode(),
            allergene.getLibelle(),
            allergene.getDescription()
        );
    }
}
