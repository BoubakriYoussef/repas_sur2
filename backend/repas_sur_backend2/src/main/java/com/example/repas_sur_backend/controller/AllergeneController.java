package com.example.repas_sur_backend.controller;

import com.example.repas_sur_backend.dto.AllergeneDto;
import com.example.repas_sur_backend.dto.AllergeneRequest;
import com.example.repas_sur_backend.service.AllergeneService;
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
@RequestMapping("/api/allergenes")
public class AllergeneController {

    private final AllergeneService allergeneService;

    public AllergeneController(AllergeneService allergeneService) {
        this.allergeneService = allergeneService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','RESPONSABLE','CUISINE')")
    public List<AllergeneDto> getAll() {
        return allergeneService.findAll();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','RESPONSABLE','CUISINE')")
    public AllergeneDto getById(@PathVariable Long id) {
        return allergeneService.getById(id);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','RESPONSABLE')")
    public ResponseEntity<AllergeneDto> create(@Valid @RequestBody AllergeneRequest request) {
        AllergeneDto created = allergeneService.save(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','RESPONSABLE')")
    public AllergeneDto update(@PathVariable Long id, @Valid @RequestBody AllergeneRequest request) {
        return allergeneService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','RESPONSABLE')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        allergeneService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
