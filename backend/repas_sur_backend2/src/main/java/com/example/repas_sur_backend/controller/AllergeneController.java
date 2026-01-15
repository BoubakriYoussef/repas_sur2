package com.example.repas_sur_backend.controller;

import com.example.repas_sur_backend.model.Allergene;
import com.example.repas_sur_backend.service.AllergeneService;
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
@RequestMapping("/api/allergenes")
public class AllergeneController {

    private final AllergeneService allergeneService;

    public AllergeneController(AllergeneService allergeneService) {
        this.allergeneService = allergeneService;
    }

    @GetMapping
    public List<Allergene> getAll() {
        return allergeneService.findAll();
    }

    @GetMapping("/{id}")
    public Allergene getById(@PathVariable Long id) {
        return allergeneService.getById(id);
    }

    @PostMapping
    public ResponseEntity<Allergene> create(@RequestBody Allergene allergene) {
        Allergene created = allergeneService.save(allergene);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public Allergene update(@PathVariable Long id, @RequestBody Allergene allergene) {
        return allergeneService.update(id, allergene);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        allergeneService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
