package com.example.boho_v10.service;

import com.example.boho_v10.dto.AppointmentAdminDto;
import com.example.boho_v10.entity.AppointmentEntity;
import com.example.boho_v10.repository.AppointmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AppointmentAdminService {

    private final AppointmentRepository repo;

    public AppointmentAdminService(AppointmentRepository repo) {
        this.repo = repo;
    }

    @Transactional(readOnly = true)
    public List<AppointmentAdminDto> listAll() {
        return repo.findAllByOrderByStartAtDesc().stream()
                .map(this::toDto)
                .toList();
    }


    @Transactional
    public void deleteById(Long id) {
        if (!repo.existsById(id)) throw new IllegalArgumentException("Appointment not found: " + id);
        repo.deleteById(id);
    }

    private AppointmentAdminDto toDto(AppointmentEntity a) {
        return new AppointmentAdminDto(
                a.getId(),
                a.getService().getId(),
                a.getService().getName(),
                a.getStartAt(),
                a.getEndAt(),
                a.getCustomerName(),
                a.getCustomerPhone(),
                a.getComment(),
                a.getDurationMin()   // ← добавили
        );
    }
}