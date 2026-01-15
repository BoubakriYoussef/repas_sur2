package com.example.repas_sur_backend.controller;

import com.example.repas_sur_backend.model.Regime;
import com.example.repas_sur_backend.service.RegimeService;
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
@RequestMapping("/api/regimes")
public class RegimeController {

    private final RegimeService regimeService;

    public RegimeController(RegimeService regimeService) {
        this.regimeService = regimeService;
    }

    @GetMapping
    public List<Regime> getAll() {
        return regimeService.findAll();
    }

    @GetMapping("/{id}")
    public Regime getById(@PathVariable Long id) {
        return regimeService.getById(id);
    }

    @PostMapping
    public ResponseEntity<Regime> create(@RequestBody Regime regime) {
        Regime created = regimeService.save(regime);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public Regime update(@PathVariable Long id, @RequestBody Regime regime) {
        return regimeService.update(id, regime);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        regimeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
