package com.example.boho_v10.dto;

public record AppointmentCreateRequest
    (Long serviceId,
    Long serviceDurationId,   // id строки из service_durations
    String startLocal,   // "YYYY-MM-DDTHH:mm" (локальная зона салона)
    String customerName,
    String customerPhone,
    String comment
)
    {}
