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

    private final AppointmentRepository apptRepo;
    private final ServiceDurationRepository durationRepo;

    public AppointmentService(AppointmentRepository apptRepo,
                              ServiceDurationRepository durationRepo) {
        this.apptRepo = apptRepo;
        this.durationRepo = durationRepo;
    }

    public AppointmentResponse create(AppointmentCreateRequest req) {
        if (req == null) throw new IllegalArgumentException("empty request");

        ServiceDurationEntity d = durationRepo
                .findByIdAndService_IdAndService_ActiveTrue(req.serviceDurationId(), req.serviceId())
                .orElseThrow(() -> new IllegalArgumentException("Неверная услуга/длительность"));

        LocalDateTime start = parseStart(req.startLocal());
        LocalDateTime end   = start.plusMinutes(d.getDurationMin());

        if (apptRepo.existsByStatusBookedAndOverlap(start, end)) {
            throw new IllegalStateException("Слот занят");
        }

        AppointmentEntity a = new AppointmentEntity();
        a.setServiceId(req.serviceId());
        a.setServiceDurationId(req.serviceDurationId());
        a.setCustomerName(trim(req.customerName()));
        a.setCustomerPhone(trim(req.customerPhone()));
        a.setStartTime(start);
        a.setEndTime(end);
        a.setStatus("booked");
        a.setComment(trim(req.comment()));

        Long id = apptRepo.save(a).getId();
        return new AppointmentResponse(id, a.getStatus());
    }

    private static LocalDateTime parseStart(String iso) {
        if (iso == null || iso.isBlank()) throw new IllegalArgumentException("startLocal is required");
        try { return LocalDateTime.parse(iso); }
        catch (Exception e) { throw new IllegalArgumentException("Неверный формат startLocal: " + iso); }
    }
    private static String trim(String s){ return s==null? null : s.trim(); }
}



