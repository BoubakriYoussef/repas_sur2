package com.example.repas_sur_backend.dto;

import com.example.repas_sur_backend.model.enums.TypeSite;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SiteRestaurationRequest(
    @NotBlank String nom,
    @NotNull TypeSite type,
    String adresse
) {
}
