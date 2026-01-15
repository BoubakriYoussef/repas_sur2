package com.example.repas_sur_backend.repository;

import com.example.repas_sur_backend.model.ServiceRepas;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceRepasRepository extends JpaRepository<ServiceRepas, Long> {
}
