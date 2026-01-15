package com.example.repas_sur_backend.repository;

import com.example.repas_sur_backend.model.Plat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlatRepository extends JpaRepository<Plat, Long> {
}
