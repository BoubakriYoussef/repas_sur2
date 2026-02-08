package com.example.repas_sur_backend.dto;

import com.example.repas_sur_backend.model.enums.TypeSite;

public record SiteRestaurationDto(
    Long id,
    String nom,
    TypeSite type,
    String adresse
) {
}
