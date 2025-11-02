// AppointmentController.java (админский, если он про списки)
package com.example.boho_v10.controller;

import com.example.boho_v10.dto.AppointmentAdminDto;
import com.example.boho_v10.service.AppointmentAdminService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/admin/appointments")
public class AppointmentController {

    private final AppointmentAdminService service;

    public AppointmentController(AppointmentAdminService service) {
        this.service = service;
    }

    @GetMapping("/all")
    public ResponseEntity<List<AppointmentAdminDto>> allDesc() {
        return ResponseEntity.ok(service.findAllByOrderByStartTimeDesc());
    }

    @GetMapping("/by-date")
    public ResponseEntity<List<AppointmentAdminDto>> byDate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateUtc) {
        return ResponseEntity.ok(service.getForDateUTC(dateUtc));
    }
}



