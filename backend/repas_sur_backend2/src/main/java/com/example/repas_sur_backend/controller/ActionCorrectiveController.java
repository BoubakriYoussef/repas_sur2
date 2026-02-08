package com.example.repas_sur_backend.controller;

import com.example.repas_sur_backend.dto.ActionCorrectiveDto;
import com.example.repas_sur_backend.dto.ActionCorrectiveRequest;
import com.example.repas_sur_backend.service.ActionCorrectiveService;
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
@RequestMapping("/api/actions-correctives")
public class ActionCorrectiveController {

    private final ActionCorrectiveService actionCorrectiveService;

    public ActionCorrectiveController(ActionCorrectiveService actionCorrectiveService) {
        this.actionCorrectiveService = actionCorrectiveService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','RESPONSABLE','CUISINE')")
    public List<ActionCorrectiveDto> getAll() {
        return actionCorrectiveService.findAll();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','RESPONSABLE','CUISINE')")
    public ActionCorrectiveDto getById(@PathVariable Long id) {
        return actionCorrectiveService.getById(id);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','RESPONSABLE','CUISINE')")
    public ResponseEntity<ActionCorrectiveDto> create(@Valid @RequestBody ActionCorrectiveRequest request) {
        ActionCorrectiveDto created = actionCorrectiveService.save(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','RESPONSABLE')")
    public ActionCorrectiveDto update(@PathVariable Long id, @Valid @RequestBody ActionCorrectiveRequest request) {
        return actionCorrectiveService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','RESPONSABLE')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        actionCorrectiveService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
