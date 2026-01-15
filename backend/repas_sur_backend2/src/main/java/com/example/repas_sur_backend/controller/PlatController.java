package com.example.repas_sur_backend.controller;

import com.example.repas_sur_backend.model.Plat;
import com.example.repas_sur_backend.service.PlatService;
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
@RequestMapping("/api/plats")
public class PlatController {

    private final PlatService platService;

    public PlatController(PlatService platService) {
        this.platService = platService;
    }

    @GetMapping
    public List<Plat> getAll() {
        return platService.findAll();
    }

    @GetMapping("/{id}")
    public Plat getById(@PathVariable Long id) {
        return platService.getById(id);
    }

    @PostMapping
    public ResponseEntity<Plat> create(@RequestBody Plat plat) {
        Plat created = platService.save(plat);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public Plat update(@PathVariable Long id, @RequestBody Plat plat) {
        return platService.update(id, plat);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        platService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
