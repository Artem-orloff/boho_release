package com.example.boho_v10.controller;

import com.example.boho_v10.dto.ServiceDurationDto;
import com.example.boho_v10.entity.ServiceDurationEntity;
import com.example.boho_v10.repository.ServiceDurationRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/services") // базовый префикс контроллера
public class ServiceController {

    private final ServiceDurationRepository durationRepo;

    public ServiceController(ServiceDurationRepository durationRepo) {
        this.durationRepo = durationRepo;
    }

    // GET /api/services/{id}/durations
    @GetMapping("/{id}/durations")
    public List<ServiceDurationDto> getDurations(@PathVariable Long id) {
        List<ServiceDurationEntity> list = durationRepo.findAllActiveByServiceIdOrdered(id);
        return list.stream()
                .map(sd -> new ServiceDurationDto(
                        sd.getId(),
                        sd.getDurationMin(),
                        sd.getPriceCents(),
                        sd.getSortOrder()
                ))
                .toList();
    }
}
