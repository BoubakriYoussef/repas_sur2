package com.example.repas_sur_backend.repository;

import com.example.repas_sur_backend.model.Menu;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MenuRepository extends JpaRepository<Menu, Long> {
}
