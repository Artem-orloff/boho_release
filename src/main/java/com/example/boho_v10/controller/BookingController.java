package com.example.boho_v10.controller;

import com.example.boho_v10.dto.AppointmentCreateRequest;
import com.example.boho_v10.dto.AppointmentResponse;
import com.example.boho_v10.service.AppointmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class BookingController {

    private final AppointmentService appointmentService;
    public BookingController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @PostMapping(value="/appointments", consumes="application/json", produces="application/json")
    public ResponseEntity<AppointmentResponse> create(@RequestBody AppointmentCreateRequest req) {
        return ResponseEntity.ok(appointmentService.create(req));
    }
}



