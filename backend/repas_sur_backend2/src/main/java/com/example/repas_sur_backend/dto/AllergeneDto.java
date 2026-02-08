package com.example.repas_sur_backend.dto;

public record AllergeneDto(
    Long id,
    String code,
    String libelle,
    String description
) {
}
