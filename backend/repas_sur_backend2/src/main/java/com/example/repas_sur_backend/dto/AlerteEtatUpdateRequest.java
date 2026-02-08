package com.example.repas_sur_backend.dto;

import com.example.repas_sur_backend.model.enums.EtatAlerte;
import jakarta.validation.constraints.NotNull;

public record AlerteEtatUpdateRequest(
    @NotNull EtatAlerte etat
) {
}
