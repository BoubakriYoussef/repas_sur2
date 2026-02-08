package com.example.repas_sur_backend.controller;

import com.example.repas_sur_backend.dto.AlerteEtatUpdateRequest;
import com.example.repas_sur_backend.dto.AlerteRisqueDto;
import com.example.repas_sur_backend.dto.AlerteRisqueRequest;
import com.example.repas_sur_backend.service.AlerteRisqueService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
    @PreAuthorize("hasAnyRole('ADMIN','RESPONSABLE','CUISINE')")
    public List<AlerteRisqueDto> getAll() {
        return alerteRisqueService.findAll();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','RESPONSABLE','CUISINE')")
    public AlerteRisqueDto getById(@PathVariable Long id) {
        return alerteRisqueService.getById(id);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','RESPONSABLE')")
    public ResponseEntity<AlerteRisqueDto> create(@Valid @RequestBody AlerteRisqueRequest request) {
        AlerteRisqueDto created = alerteRisqueService.save(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','RESPONSABLE')")
    public AlerteRisqueDto update(@PathVariable Long id, @Valid @RequestBody AlerteRisqueRequest request) {
        return alerteRisqueService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','RESPONSABLE')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        alerteRisqueService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/generer/{serviceId}")
    @PreAuthorize("hasAnyRole('ADMIN','RESPONSABLE')")
    public List<AlerteRisqueDto> genererAlertes(@PathVariable Long serviceId) {
        return alerteRisqueService.genererAlertesPourService(serviceId);
    }

    @PostMapping("/{id}/etat")
    @PreAuthorize("hasAnyRole('ADMIN','RESPONSABLE','CUISINE')")
    public AlerteRisqueDto updateEtat(@PathVariable Long id, @Valid @RequestBody AlerteEtatUpdateRequest request) {
        return alerteRisqueService.updateEtat(id, request);
    }
}
