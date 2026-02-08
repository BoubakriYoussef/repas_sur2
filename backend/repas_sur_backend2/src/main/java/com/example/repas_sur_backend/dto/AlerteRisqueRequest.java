package com.example.repas_sur_backend.dto;

import com.example.repas_sur_backend.model.enums.EtatAlerte;
import com.example.repas_sur_backend.model.enums.NiveauAlerte;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Set;

public record AlerteRisqueRequest(
    @NotNull EtatAlerte etat,
    @NotNull NiveauAlerte niveau,
    String message,
    LocalDateTime dateCreation,
    @NotNull Long conviveId,
    @NotNull Long serviceId,
    Set<Long> allergeneIds
) {
}
