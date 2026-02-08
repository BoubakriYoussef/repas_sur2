package com.example.repas_sur_backend.dto;

import com.example.repas_sur_backend.model.enums.RegimeType;

public record RegimeDto(
    Long id,
    String code,
    String libelle,
    RegimeType type,
    String description
) {
}
