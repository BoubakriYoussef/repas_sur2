package com.example.repas_sur_backend.dto;

import com.example.repas_sur_backend.model.enums.RoleUtilisateur;

public record UtilisateurDto(
    Long id,
    String username,
    String email,
    String telephone,
    String poste,
    RoleUtilisateur role,
    boolean actif,
    SiteRestaurationDto site
) {
}
