package com.example.repas_sur_backend.repository;

import com.example.repas_sur_backend.model.ActionCorrective;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActionCorrectiveRepository extends JpaRepository<ActionCorrective, Long> {
    boolean existsByAlerteId(Long alerteId);

    boolean existsByAlerteIdAndIdNot(Long alerteId, Long id);
}
