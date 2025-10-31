package com.example.boho_v10.controller;

import com.example.boho_v10.service.AppointmentService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class AvailabilityController {

    private final AppointmentService appointmentService;

    public AvailabilityController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @GetMapping("/api/slots/busy")
    public List<Map<String, OffsetDateTime>> getBusy(@RequestParam Long serviceId,
                                                     @RequestParam String date) {
        LocalDate day = LocalDate.parse(date);

        List<Map<String, OffsetDateTime>> result = appointmentService.busyForDay(serviceId, day).stream()
                .map(a -> Map.of(
                        "start", a.getStartAt(),
                        "end",   a.getEndAt()
                ))
                .collect(Collectors.toList());

        // Разворачиваем список вручную, так как .reversed() нет в Java 17 для Stream
        java.util.Collections.reverse(result);
        return result;
    }
}