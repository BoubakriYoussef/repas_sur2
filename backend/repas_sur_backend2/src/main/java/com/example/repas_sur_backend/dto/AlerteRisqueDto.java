package com.example.repas_sur_backend.dto;

import com.example.repas_sur_backend.model.enums.EtatAlerte;
import com.example.repas_sur_backend.model.enums.NiveauAlerte;
import java.time.LocalDateTime;
import java.util.Set;

public record AlerteRisqueDto(
    Long id,
    EtatAlerte etat,
    NiveauAlerte niveau,
    String message,
    LocalDateTime dateCreation,
    IdNomDto convive,
    ServiceRepasDto service,
    Set<IdCodeDto> allergenes
) {
}
