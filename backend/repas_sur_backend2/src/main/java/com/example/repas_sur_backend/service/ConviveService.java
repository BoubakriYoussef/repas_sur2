package com.example.repas_sur_backend.service;

import com.example.repas_sur_backend.model.Convive;
import com.example.repas_sur_backend.repository.ConviveRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ConviveService {

    private final ConviveRepository conviveRepository;

    public ConviveService(ConviveRepository conviveRepository) {
        this.conviveRepository = conviveRepository;
    }

    @Transactional(readOnly = true)
    public List<Convive> findAll() {
        return conviveRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Convive getById(Long id) {
        return conviveRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Convive not found: " + id));
    }

    public Convive save(Convive convive) {
        return conviveRepository.save(convive);
    }

    public Convive update(Long id, Convive convive) {
        getById(id);
        convive.setId(id);
        return conviveRepository.save(convive);
    }

    public void delete(Long id) {
        conviveRepository.deleteById(id);
    }
}
