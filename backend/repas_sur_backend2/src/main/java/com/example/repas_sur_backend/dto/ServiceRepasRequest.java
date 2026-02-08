package com.example.repas_sur_backend.dto;

import com.example.repas_sur_backend.model.enums.StatutService;
import com.example.repas_sur_backend.model.enums.TypeRepas;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record ServiceRepasRequest(
    @NotNull LocalDateTime dateService,
    @NotNull TypeRepas typeRepas,
    @NotNull StatutService statut,
    @NotNull Long siteId,
    Long menuId
) {
}
