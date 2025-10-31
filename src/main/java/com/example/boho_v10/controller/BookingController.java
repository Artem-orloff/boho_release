package com.example.boho_v10.controller;

import com.example.boho_v10.dto.AppointmentCreateRequest;
import com.example.boho_v10.entity.AppointmentEntity;
import com.example.boho_v10.service.AppointmentService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/booking4")
public class BookingController {

    private final AppointmentService appointmentService;

    public BookingController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @PostMapping("/api/appointments")
    public ResponseEntity<?> create(@RequestBody AppointmentCreateRequest req) {
        try {
            var saved = appointmentService.create(req);
            return ResponseEntity.status(201).body(new java.util.LinkedHashMap<>() {{
                put("id", saved.getId());
                put("start", saved.getStartAt());
                put("end", saved.getEndAt());
                put("durationMin", saved.getDurationMin());
            }});
        } catch (DataIntegrityViolationException ex) {
            return ResponseEntity.status(409).body(new java.util.LinkedHashMap<>() {{ put("error","TIME_SLOT_TAKEN"); }});
        } catch (IllegalArgumentException bad) {
            return ResponseEntity.badRequest().body(new java.util.LinkedHashMap<>() {{ put("error", bad.getMessage()); }});
        }
    }

}
