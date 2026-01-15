package com.example.repas_sur_backend.service;

import com.example.repas_sur_backend.model.Plat;
import com.example.repas_sur_backend.repository.PlatRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PlatService {

    private final PlatRepository platRepository;

    public PlatService(PlatRepository platRepository) {
        this.platRepository = platRepository;
    }

    @Transactional(readOnly = true)
    public List<Plat> findAll() {
        return platRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Plat getById(Long id) {
        return platRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Plat not found: " + id));
    }

    public Plat save(Plat plat) {
        return platRepository.save(plat);
    }

    public Plat update(Long id, Plat plat) {
        getById(id);
        plat.setId(id);
        return platRepository.save(plat);
    }

    public void delete(Long id) {
        platRepository.deleteById(id);
    }
}
