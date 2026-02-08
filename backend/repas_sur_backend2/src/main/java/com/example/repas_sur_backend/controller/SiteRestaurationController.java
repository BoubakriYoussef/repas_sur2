package com.example.repas_sur_backend.controller;

import com.example.repas_sur_backend.dto.SiteRestaurationDto;
import com.example.repas_sur_backend.dto.SiteRestaurationRequest;
import com.example.repas_sur_backend.service.SiteRestaurationService;
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
@RequestMapping("/api/sites")
public class SiteRestaurationController {

    private final SiteRestaurationService siteRestaurationService;

    public SiteRestaurationController(SiteRestaurationService siteRestaurationService) {
        this.siteRestaurationService = siteRestaurationService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','RESPONSABLE','CUISINE')")
    public List<SiteRestaurationDto> getAll() {
        return siteRestaurationService.findAll();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','RESPONSABLE','CUISINE')")
    public SiteRestaurationDto getById(@PathVariable Long id) {
        return siteRestaurationService.getById(id);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','RESPONSABLE')")
    public ResponseEntity<SiteRestaurationDto> create(@Valid @RequestBody SiteRestaurationRequest request) {
        SiteRestaurationDto created = siteRestaurationService.save(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','RESPONSABLE')")
    public SiteRestaurationDto update(@PathVariable Long id, @Valid @RequestBody SiteRestaurationRequest request) {
        return siteRestaurationService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','RESPONSABLE')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        siteRestaurationService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
