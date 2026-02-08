package com.example.repas_sur_backend.controller;

import com.example.repas_sur_backend.dto.ConviveDto;
import com.example.repas_sur_backend.dto.ConviveRequest;
import com.example.repas_sur_backend.service.ConviveService;
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
@RequestMapping("/api/convives")
public class ConviveController {

    private final ConviveService conviveService;

    public ConviveController(ConviveService conviveService) {
        this.conviveService = conviveService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','RESPONSABLE','CUISINE')")
    public List<ConviveDto> getAll() {
        return conviveService.findAll();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','RESPONSABLE','CUISINE')")
    public ConviveDto getById(@PathVariable Long id) {
        return conviveService.getById(id);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','RESPONSABLE')")
    public ResponseEntity<ConviveDto> create(@Valid @RequestBody ConviveRequest request) {
        ConviveDto created = conviveService.save(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','RESPONSABLE')")
    public ConviveDto update(@PathVariable Long id, @Valid @RequestBody ConviveRequest request) {
        return conviveService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','RESPONSABLE')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        conviveService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
