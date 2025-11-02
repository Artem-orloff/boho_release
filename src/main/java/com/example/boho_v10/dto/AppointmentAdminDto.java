package com.example.boho_v10.dto;

import java.time.OffsetDateTime;
import java.time.LocalDateTime;

public record AppointmentAdminDto(
        Long id,
        Long serviceId,
        Long serviceDurationId,
        String customerName,
        String customerPhone,
        LocalDateTime startTime,
        LocalDateTime endTime,
        String status
) {}