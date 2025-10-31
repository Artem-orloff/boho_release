package com.example.boho_v10.controller;

import com.example.boho_v10.dto.AppointmentAdminDto;
import com.example.boho_v10.service.AppointmentAdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/appointments")
public class AdminAppointmentController {

    private final AppointmentAdminService service;

    public AdminAppointmentController(AppointmentAdminService service) {
        this.service = service;
    }

    @GetMapping
    public List<AppointmentAdminDto> list() {
        return service.listAll();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // Небольшая обработка 404 без глобального advice (на время)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> notFound(IllegalArgumentException ex) {
        return ResponseEntity.status(404).body(ex.getMessage());
    }
}
