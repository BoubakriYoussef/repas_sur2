package com.example.repas_sur_backend.dto;

import com.example.repas_sur_backend.model.enums.TypeConvive;
import java.util.Set;

public record ConviveDto(
    Long id,
    String nom,
    String prenom,
    TypeConvive typeConvive,
    SiteRestaurationDto site,
    Set<IdCodeDto> allergenes,
    Set<RegimeDto> regimes
) {
}
