package com.example.repas_sur_backend.service;

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
    public List<Regime> findAll() {
        return regimeRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Regime getById(Long id) {
        return regimeRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Regime not found: " + id));
    }

    public Regime save(Regime regime) {
        return regimeRepository.save(regime);
    }

    public Regime update(Long id, Regime regime) {
        getById(id);
        regime.setId(id);
        return regimeRepository.save(regime);
    }

    public void delete(Long id) {
        regimeRepository.deleteById(id);
    }
}
