package com.example.repas_sur_backend.dto;

import jakarta.validation.constraints.NotBlank;

public record AllergeneRequest(
    @NotBlank String code,
    @NotBlank String libelle,
    String description
) {
}
