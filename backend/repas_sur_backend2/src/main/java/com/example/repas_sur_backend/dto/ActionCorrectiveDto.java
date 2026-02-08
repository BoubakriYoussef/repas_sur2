package com.example.repas_sur_backend.dto;

import java.time.LocalDateTime;

public record ActionCorrectiveDto(
    Long id,
    LocalDateTime date,
    String typeAction,
    String description,
    IdNomDto alerte,
    IdNomDto convive,
    UtilisateurDto utilisateur
) {
}
