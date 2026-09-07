package com.example.repas_sur_backend.repository;

import com.example.repas_sur_backend.model.AlerteRisque;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlerteRisqueRepository extends JpaRepository<AlerteRisque, Long> {
    boolean existsByServiceIdAndConviveId(Long serviceId, Long conviveId);
}
