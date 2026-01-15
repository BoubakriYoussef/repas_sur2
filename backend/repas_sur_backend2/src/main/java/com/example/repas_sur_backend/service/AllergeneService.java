package com.example.repas_sur_backend.service;

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
    public List<Allergene> findAll() {
        return allergeneRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Allergene getById(Long id) {
        return allergeneRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Allergene not found: " + id));
    }

    public Allergene save(Allergene allergene) {
        return allergeneRepository.save(allergene);
    }

    public Allergene update(Long id, Allergene allergene) {
        getById(id);
        allergene.setId(id);
        return allergeneRepository.save(allergene);
    }

    public void delete(Long id) {
        allergeneRepository.deleteById(id);
    }
}
