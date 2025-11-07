package com.example.boho_v10.dto;

public record AppointmentCreateRequest
    (Long serviceId,
     Long serviceDurationId,
     String startLocal,
     String customerName,
     String customerPhone,
     String comment
)
    {}
