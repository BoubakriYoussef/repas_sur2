package com.example.repas_sur_backend.repository;

import com.example.repas_sur_backend.model.Allergene;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AllergeneRepository extends JpaRepository<Allergene, Long> {
}
