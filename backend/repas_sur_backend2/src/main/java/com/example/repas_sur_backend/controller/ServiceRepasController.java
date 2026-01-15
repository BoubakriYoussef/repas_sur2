package com.example.repas_sur_backend.controller;

import com.example.repas_sur_backend.model.ServiceRepas;
import com.example.repas_sur_backend.service.ServiceRepasService;
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
@RequestMapping("/api/services-repas")
public class ServiceRepasController {

    private final ServiceRepasService serviceRepasService;

    public ServiceRepasController(ServiceRepasService serviceRepasService) {
        this.serviceRepasService = serviceRepasService;
    }

    @GetMapping
    public List<ServiceRepas> getAll() {
        return serviceRepasService.findAll();
    }

    @GetMapping("/{id}")
    public ServiceRepas getById(@PathVariable Long id) {
        return serviceRepasService.getById(id);
    }

    @PostMapping
    public ResponseEntity<ServiceRepas> create(@RequestBody ServiceRepas serviceRepas) {
        ServiceRepas created = serviceRepasService.save(serviceRepas);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ServiceRepas update(@PathVariable Long id, @RequestBody ServiceRepas serviceRepas) {
        return serviceRepasService.update(id, serviceRepas);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        serviceRepasService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
