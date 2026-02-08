package com.example.repas_sur_backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Set;

public record PlatRequest(
    @NotBlank String nom,
    String categorie,
    String description,
    @NotNull Boolean contientPorc,
    @NotNull Boolean estVegetarien,
    Set<Long> allergeneIds
) {
}
