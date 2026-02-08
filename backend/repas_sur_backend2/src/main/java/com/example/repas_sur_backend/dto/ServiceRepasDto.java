package com.example.repas_sur_backend.dto;

import com.example.repas_sur_backend.model.enums.StatutService;
import com.example.repas_sur_backend.model.enums.TypeRepas;
import java.time.LocalDateTime;

public record ServiceRepasDto(
    Long id,
    LocalDateTime dateService,
    TypeRepas typeRepas,
    StatutService statut,
    SiteRestaurationDto site,
    IdNomDto menu
) {
}
