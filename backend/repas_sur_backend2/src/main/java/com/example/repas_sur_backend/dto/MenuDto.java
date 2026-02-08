package com.example.repas_sur_backend.dto;

import java.util.Set;

public record MenuDto(
    Long id,
    String nom,
    String description,
    Set<IdNomDto> plats
) {
}
