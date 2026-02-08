package com.example.repas_sur_backend.service;

import com.example.repas_sur_backend.dto.RegimeDto;
import com.example.repas_sur_backend.dto.RegimeRequest;
import com.example.repas_sur_backend.exception.NotFoundException;
import com.example.repas_sur_backend.model.Regime;
import com.example.repas_sur_backend.repository.RegimeRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class RegimeService {

    private final RegimeRepository regimeRepository;

    public RegimeService(RegimeRepository regimeRepository) {
        this.regimeRepository = regimeRepository;
    }

    @Transactional(readOnly = true)
    public List<RegimeDto> findAll() {
        return regimeRepository.findAll().stream().map(this::toDto).toList();
    }

    @Transactional(readOnly = true)
    public RegimeDto getById(Long id) {
        return toDto(findEntity(id));
    }

    public RegimeDto save(RegimeRequest request) {
        Regime regime = new Regime();
        apply(regime, request);
        return toDto(regimeRepository.save(regime));
    }

    public RegimeDto update(Long id, RegimeRequest request) {
        Regime regime = findEntity(id);
        apply(regime, request);
        return toDto(regimeRepository.save(regime));
    }

    public void delete(Long id) {
        regimeRepository.deleteById(id);
    }

    private Regime findEntity(Long id) {
        return regimeRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Regime not found: " + id));
    }

    private void apply(Regime regime, RegimeRequest request) {
        regime.setCode(request.code());
        regime.setLibelle(request.libelle());
        regime.setType(request.type());
        regime.setDescription(request.description());
    }

    private RegimeDto toDto(Regime regime) {
        return new RegimeDto(
            regime.getId(),
            regime.getCode(),
            regime.getLibelle(),
            regime.getType(),
            regime.getDescription()
        );
    }
}
