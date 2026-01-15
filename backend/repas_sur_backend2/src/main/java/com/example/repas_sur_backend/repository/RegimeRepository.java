package com.example.repas_sur_backend.repository;

import com.example.repas_sur_backend.model.Regime;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RegimeRepository extends JpaRepository<Regime, Long> {
}
