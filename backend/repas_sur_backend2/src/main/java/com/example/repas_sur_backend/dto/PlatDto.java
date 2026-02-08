package com.example.repas_sur_backend.dto;

import java.util.Set;

public record PlatDto(
    Long id,
    String nom,
    String categorie,
    String description,
    boolean contientPorc,
    boolean estVegetarien,
    Set<IdCodeDto> allergenes
) {
}
