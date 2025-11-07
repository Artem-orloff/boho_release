package com.example.boho_v10.controller;

import com.example.boho_v10.dto.ServiceDto;
import com.example.boho_v10.dto.ServiceDurationDto;
import com.example.boho_v10.service.ServiceService; // ← ВАЖНО: правильный пакет!
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ServiceController {

    private final ServiceService serviceService;

    public ServiceController(ServiceService serviceService) {
        this.serviceService = serviceService;
    }

    // пинг для диагностики
    @GetMapping(value="/ping", produces="application/json")
    public Map<String,Object> ping() { return Map.of("ok", true, "ts", Instant.now().toString()); }

    @GetMapping(value="/services", produces="application/json")
    public List<ServiceDto> services() {
        return serviceService.getActiveServices();
    }

    @GetMapping(value="/services/{id}/durations", produces="application/json")
    public List<ServiceDurationDto> durations(@PathVariable Long id) {
        return serviceService.getDurations(id);
    }

    @GetMapping(value="/services-durations", produces="application/json")
    public List<ServiceDurationDto> durationsAlias(@RequestParam("serviceId") Long id) {
        return serviceService.getDurations(id);
    }
}



