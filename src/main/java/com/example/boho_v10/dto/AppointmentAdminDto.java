package com.example.boho_v10.dto;

import java.time.OffsetDateTime;

public record AppointmentAdminDto(
        Long id,
        Long serviceId,
        String serviceName,
        OffsetDateTime startAt,   // UTC
        OffsetDateTime endAt,     // UTC
        String customerName,
        String customerPhone,
        String comment,
        Integer durationMin
) {}