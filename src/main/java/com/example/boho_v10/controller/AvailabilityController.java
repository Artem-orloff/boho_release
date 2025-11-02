package com.example.boho_v10.controller;

import com.example.boho_v10.dto.BusySlotDto;
import com.example.boho_v10.repository.AppointmentRepository;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api")
public class AvailabilityController {

    private final AppointmentRepository repo;
    public AvailabilityController(AppointmentRepository repo) { this.repo = repo; }

    @GetMapping(value = "/slots/busy", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<BusySlotDto> busyForDay(@RequestParam Long serviceId,
                                        @RequestParam LocalDate date) {
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end   = start.plusDays(1);
        return repo.findDayByService(serviceId, start, end)
                .stream()
                .map(a -> new BusySlotDto(a.getStartTime(), a.getEndTime()))
                .toList();
    }
}



