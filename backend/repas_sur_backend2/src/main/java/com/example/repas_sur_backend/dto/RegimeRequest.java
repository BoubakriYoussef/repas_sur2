package com.example.repas_sur_backend.dto;

import com.example.repas_sur_backend.model.enums.RegimeType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RegimeRequest(
    @NotBlank String code,
    @NotBlank String libelle,
    @NotNull RegimeType type,
    String description
) {
}
