package com.example.repas_sur_backend.dto;

import com.example.repas_sur_backend.model.enums.RoleUtilisateur;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UtilisateurRequest(
    @NotBlank String username,
    String password,
    String email,
    String telephone,
    String poste,
    @NotNull RoleUtilisateur role,
    boolean actif,
    Long siteId
) {
}
