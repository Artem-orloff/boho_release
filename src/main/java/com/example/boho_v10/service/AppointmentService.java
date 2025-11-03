package com.example.boho_v10.service;

import com.example.boho_v10.dto.AppointmentCreateRequest;
import com.example.boho_v10.dto.AppointmentResponse;
import com.example.boho_v10.entity.AppointmentEntity;
import com.example.boho_v10.entity.ServiceDurationEntity;
import com.example.boho_v10.repository.AppointmentRepository;
import com.example.boho_v10.repository.ServiceDurationRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AppointmentService {

    private final AppointmentRepository repo;
    private final ServiceDurationRepository durationRepo;

    public AppointmentService(AppointmentRepository repo,
                              ServiceDurationRepository durationRepo) {
        this.repo = repo;
        this.durationRepo = durationRepo;
    }

    public AppointmentResponse create(AppointmentCreateRequest req) {
        if (req == null || req.startLocal() == null || req.startLocal().isBlank()) {
            throw new IllegalArgumentException("startLocal is required");
        }
        if (req.serviceId() == null || req.serviceDurationId() == null) {
            throw new IllegalArgumentException("serviceId and serviceDurationId are required");
        }

        LocalDateTime start = LocalDateTime.parse(req.startLocal());

        ServiceDurationEntity dur = durationRepo.findById(req.serviceDurationId())
                .orElseThrow(() -> new IllegalArgumentException("ServiceDuration not found: " + req.serviceDurationId()));

        if (!dur.getService().getId().equals(req.serviceId())) {
            throw new IllegalArgumentException("Duration doesn't belong to the given service");
        }

        int minutes = dur.getDurationMin();
        LocalDateTime end = start.plusMinutes(minutes);

        AppointmentEntity a = new AppointmentEntity();
        a.setServiceId(req.serviceId());
        a.setServiceDurationId(req.serviceDurationId());
        a.setStartTime(start);
        a.setEndTime(end);
        a.setDurationMin(minutes);             // НЕ ЗАБЫТЬ: в БД NOT NULL
        a.setStatus("booked");
        a.setCustomerName(req.customerName());
        a.setCustomerPhone(req.customerPhone());
        a.setComment(req.comment());

        repo.save(a);

        return new AppointmentResponse(
                a.getId(),
                a.getServiceId(),
                a.getServiceDurationId(),
                a.getStartTime(),
                a.getEndTime(),
                a.getStatus(),
                a.getCustomerName(),
                a.getCustomerPhone(),
                a.getComment()
        );
    }
}



