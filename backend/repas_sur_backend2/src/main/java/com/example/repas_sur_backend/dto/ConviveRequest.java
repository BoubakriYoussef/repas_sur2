package com.example.repas_sur_backend.dto;

import com.example.repas_sur_backend.model.enums.TypeConvive;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Set;

public record ConviveRequest(
    @NotBlank String nom,
    @NotBlank String prenom,
    @NotNull TypeConvive typeConvive,
    @NotNull Long siteId,
    Set<Long> allergeneIds,
    Set<Long> regimeIds
) {
}
