package com.example.repas_sur_backend.controller;

import com.example.repas_sur_backend.dto.RegimeDto;
import com.example.repas_sur_backend.dto.RegimeRequest;
import com.example.repas_sur_backend.service.RegimeService;
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
@RequestMapping("/api/regimes")
public class RegimeController {

    private final RegimeService regimeService;

    public RegimeController(RegimeService regimeService) {
        this.regimeService = regimeService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','RESPONSABLE','CUISINE')")
    public List<RegimeDto> getAll() {
        return regimeService.findAll();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','RESPONSABLE','CUISINE')")
    public RegimeDto getById(@PathVariable Long id) {
        return regimeService.getById(id);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','RESPONSABLE')")
    public ResponseEntity<RegimeDto> create(@Valid @RequestBody RegimeRequest request) {
        RegimeDto created = regimeService.save(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','RESPONSABLE')")
    public RegimeDto update(@PathVariable Long id, @Valid @RequestBody RegimeRequest request) {
        return regimeService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','RESPONSABLE')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        regimeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
