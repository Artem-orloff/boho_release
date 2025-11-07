package com.example.boho_v10.controller;

import com.example.boho_v10.dto.BusySlotDto;
import com.example.boho_v10.repository.AppointmentRepository;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/slots")
public class AvailabilityController {

    private final AppointmentRepository repo;

    public AvailabilityController(AppointmentRepository repo) {
        this.repo = repo;
    }

    @GetMapping("/busy")
    public List<BusySlotDto> getBusySlots(
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        LocalDateTime dayStart = date.atStartOfDay();
        LocalDateTime dayEnd   = dayStart.plusDays(1);

        return repo.findDayBusy(dayStart, dayEnd).stream()
                .map(a -> new BusySlotDto(a.getStartTime(), a.getEndTime()))
                .toList();
    }
}
