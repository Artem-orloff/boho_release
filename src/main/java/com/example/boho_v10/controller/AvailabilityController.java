package com.example.boho_v10.controller;

import com.example.boho_v10.dto.BusySlotDto;
import com.example.boho_v10.repository.AppointmentRepository;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/slots")
public class AvailabilityController {

    private final AppointmentRepository repo;

    public AvailabilityController(AppointmentRepository repo) { this.repo = repo; }

    @GetMapping("/busy")
    public List<BusySlotDto> busy(@RequestParam Long serviceId,
                                  @RequestParam java.time.LocalDate date) {
        java.time.LocalDateTime from = date.atStartOfDay();
        java.time.LocalDateTime to   = from.plusDays(1);
        return repo.findDayByService(serviceId, from, to).stream()
                .map(a -> new BusySlotDto(a.getStartTime(), a.getEndTime()))
                .toList();
    }
}








