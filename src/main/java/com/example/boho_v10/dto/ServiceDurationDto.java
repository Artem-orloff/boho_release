package com.example.boho_v10.dto;

public record ServiceDurationDto(
        Long id,
        Integer durationMin,
        Integer priceCents,
        Integer sortOrder
) {}
