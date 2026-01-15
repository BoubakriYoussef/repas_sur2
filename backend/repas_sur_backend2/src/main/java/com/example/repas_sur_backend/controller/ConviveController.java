package com.example.repas_sur_backend.controller;

import com.example.repas_sur_backend.model.Convive;
import com.example.repas_sur_backend.service.ConviveService;
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
@RequestMapping("/api/convives")
public class ConviveController {

    private final ConviveService conviveService;

    public ConviveController(ConviveService conviveService) {
        this.conviveService = conviveService;
    }

    @GetMapping
    public List<Convive> getAll() {
        return conviveService.findAll();
    }

    @GetMapping("/{id}")
    public Convive getById(@PathVariable Long id) {
        return conviveService.getById(id);
    }

    @PostMapping
    public ResponseEntity<Convive> create(@RequestBody Convive convive) {
        Convive created = conviveService.save(convive);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public Convive update(@PathVariable Long id, @RequestBody Convive convive) {
        return conviveService.update(id, convive);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        conviveService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
