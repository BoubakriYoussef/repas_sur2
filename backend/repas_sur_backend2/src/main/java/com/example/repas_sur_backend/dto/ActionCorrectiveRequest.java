package com.example.repas_sur_backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record ActionCorrectiveRequest(
    @NotNull LocalDateTime date,
    @NotBlank String typeAction,
    String description,
    @NotNull Long alerteId,
    @NotNull Long utilisateurId
) {
}
