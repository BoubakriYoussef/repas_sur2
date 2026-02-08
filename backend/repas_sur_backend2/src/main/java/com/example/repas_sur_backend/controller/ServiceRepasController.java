package com.example.repas_sur_backend.controller;

import com.example.repas_sur_backend.dto.ServiceRepasDto;
import com.example.repas_sur_backend.dto.ServiceRepasRequest;
import com.example.repas_sur_backend.service.ServiceRepasService;
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
@RequestMapping("/api/services-repas")
public class ServiceRepasController {

    private final ServiceRepasService serviceRepasService;

    public ServiceRepasController(ServiceRepasService serviceRepasService) {
        this.serviceRepasService = serviceRepasService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','RESPONSABLE','CUISINE')")
    public List<ServiceRepasDto> getAll() {
        return serviceRepasService.findAll();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','RESPONSABLE','CUISINE')")
    public ServiceRepasDto getById(@PathVariable Long id) {
        return serviceRepasService.getById(id);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','RESPONSABLE')")
    public ResponseEntity<ServiceRepasDto> create(@Valid @RequestBody ServiceRepasRequest request) {
        ServiceRepasDto created = serviceRepasService.save(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','RESPONSABLE')")
    public ServiceRepasDto update(@PathVariable Long id, @Valid @RequestBody ServiceRepasRequest request) {
        return serviceRepasService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','RESPONSABLE')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        serviceRepasService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
