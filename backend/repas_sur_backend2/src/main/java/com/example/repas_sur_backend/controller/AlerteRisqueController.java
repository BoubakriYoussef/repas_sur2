package com.example.repas_sur_backend.controller;

import com.example.repas_sur_backend.model.AlerteRisque;
import com.example.repas_sur_backend.service.AlerteRisqueService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/alertes")
public class AlerteRisqueController {

    private final AlerteRisqueService alerteRisqueService;

    public AlerteRisqueController(AlerteRisqueService alerteRisqueService) {
        this.alerteRisqueService = alerteRisqueService;
    }

    @GetMapping
    public List<AlerteRisque> getAll() {
        return alerteRisqueService.findAll();
    }

    @GetMapping("/{id}")
    public AlerteRisque getById(@PathVariable Long id) {
        return alerteRisqueService.getById(id);
    }

    @PostMapping
    public ResponseEntity<AlerteRisque> create(@RequestBody AlerteRisque alerteRisque) {
        AlerteRisque created = alerteRisqueService.save(alerteRisque);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public AlerteRisque update(@PathVariable Long id, @RequestBody AlerteRisque alerteRisque) {
        return alerteRisqueService.update(id, alerteRisque);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        alerteRisqueService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/generer/{serviceId}")
    public List<AlerteRisque> genererAlertes(@PathVariable Long serviceId) {
        return alerteRisqueService.genererAlertesPourService(serviceId);
    }
}
