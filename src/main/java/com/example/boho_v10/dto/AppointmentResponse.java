package com.example.boho_v10.dto;

import java.time.LocalDateTime;

public record AppointmentResponse(
        Long id,
        Long serviceId,
        Long serviceDurationId,
        LocalDateTime startTime,
        LocalDateTime endTime,
        String status,
        String customerName,
        String customerPhone,
        String comment
) {}



