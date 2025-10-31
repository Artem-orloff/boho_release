package com.example.boho_v10.controller;

import com.example.boho_v10.dto.AppointmentCreateRequest;
import com.example.boho_v10.entity.AppointmentEntity;
import com.example.boho_v10.service.AppointmentService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

    private final AppointmentService service;

    public AppointmentController(AppointmentService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<AppointmentEntity> create(@Valid @RequestBody AppointmentCreateRequest req) {
        AppointmentEntity created = service.create(req);
        return ResponseEntity.ok(created);
    }

    // Список записей на конкретный день (ожидаем дату в UTC, формат YYYY-MM-DD)
    @GetMapping
    public ResponseEntity<List<AppointmentEntity>> byDate(
            @RequestParam("date")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateUtc) {
        return ResponseEntity.ok(service.getForDateUTC(dateUtc));
    }
}
