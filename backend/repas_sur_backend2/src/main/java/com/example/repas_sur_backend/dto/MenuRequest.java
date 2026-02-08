package com.example.repas_sur_backend.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.Set;

public record MenuRequest(
    @NotBlank String nom,
    String description,
    Set<Long> platIds
) {
}
