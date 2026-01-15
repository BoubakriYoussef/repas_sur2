package com.example.repas_sur_backend.service;

import com.example.repas_sur_backend.model.ActionCorrective;
import com.example.repas_sur_backend.repository.ActionCorrectiveRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ActionCorrectiveService {

    private final ActionCorrectiveRepository actionCorrectiveRepository;

    public ActionCorrectiveService(ActionCorrectiveRepository actionCorrectiveRepository) {
        this.actionCorrectiveRepository = actionCorrectiveRepository;
    }

    @Transactional(readOnly = true)
    public List<ActionCorrective> findAll() {
        return actionCorrectiveRepository.findAll();
    }

    @Transactional(readOnly = true)
    public ActionCorrective getById(Long id) {
        return actionCorrectiveRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("ActionCorrective not found: " + id));
    }

    public ActionCorrective save(ActionCorrective actionCorrective) {
        return actionCorrectiveRepository.save(actionCorrective);
    }

    public ActionCorrective update(Long id, ActionCorrective actionCorrective) {
        getById(id);
        actionCorrective.setId(id);
        return actionCorrectiveRepository.save(actionCorrective);
    }

    public void delete(Long id) {
        actionCorrectiveRepository.deleteById(id);
    }
}
