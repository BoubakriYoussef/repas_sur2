package com.example.repas_sur_backend.controller;

import com.example.repas_sur_backend.model.ActionCorrective;
import com.example.repas_sur_backend.service.ActionCorrectiveService;
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
@RequestMapping("/api/actions-correctives")
public class ActionCorrectiveController {

    private final ActionCorrectiveService actionCorrectiveService;

    public ActionCorrectiveController(ActionCorrectiveService actionCorrectiveService) {
        this.actionCorrectiveService = actionCorrectiveService;
    }

    @GetMapping
    public List<ActionCorrective> getAll() {
        return actionCorrectiveService.findAll();
    }

    @GetMapping("/{id}")
    public ActionCorrective getById(@PathVariable Long id) {
        return actionCorrectiveService.getById(id);
    }

    @PostMapping
    public ResponseEntity<ActionCorrective> create(@RequestBody ActionCorrective actionCorrective) {
        ActionCorrective created = actionCorrectiveService.save(actionCorrective);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ActionCorrective update(@PathVariable Long id, @RequestBody ActionCorrective actionCorrective) {
        return actionCorrectiveService.update(id, actionCorrective);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        actionCorrectiveService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
