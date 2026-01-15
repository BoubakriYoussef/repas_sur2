package com.example.repas_sur_backend.controller;

import com.example.repas_sur_backend.model.SiteRestauration;
import com.example.repas_sur_backend.service.SiteRestaurationService;
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
@RequestMapping("/api/sites")
public class SiteRestaurationController {

    private final SiteRestaurationService siteRestaurationService;

    public SiteRestaurationController(SiteRestaurationService siteRestaurationService) {
        this.siteRestaurationService = siteRestaurationService;
    }

    @GetMapping
    public List<SiteRestauration> getAll() {
        return siteRestaurationService.findAll();
    }

    @GetMapping("/{id}")
    public SiteRestauration getById(@PathVariable Long id) {
        return siteRestaurationService.getById(id);
    }

    @PostMapping
    public ResponseEntity<SiteRestauration> create(@RequestBody SiteRestauration siteRestauration) {
        SiteRestauration created = siteRestaurationService.save(siteRestauration);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public SiteRestauration update(@PathVariable Long id, @RequestBody SiteRestauration siteRestauration) {
        return siteRestaurationService.update(id, siteRestauration);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        siteRestaurationService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
