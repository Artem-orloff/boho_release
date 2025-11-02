package com.example.boho_v10.service;

import com.example.boho_v10.dto.AppointmentAdminDto;
import com.example.boho_v10.entity.AppointmentEntity;
import com.example.boho_v10.repository.AppointmentRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class AppointmentAdminService {

    private final AppointmentRepository repository;

    public AppointmentAdminService(AppointmentRepository repository) {
        this.repository = repository;
    }

    // === метод, который просит контроллер ===
    public List<AppointmentAdminDto> findAllByOrderByStartTimeDesc() {
        return repository.findAllByOrderByStartTimeDesc()
                .stream().map(this::toDto).toList();
    }

    // алиасы на всякий случай (если где-то остались старые вызовы)
    public List<AppointmentAdminDto> findAllByOrderByStartAtDesc() {
        return findAllByOrderByStartTimeDesc();
    }
    public List<AppointmentAdminDto> listAll() {
        return findAllByOrderByStartTimeDesc();
    }

    public List<AppointmentAdminDto> getForDateUTC(LocalDate dateUtc) {
        LocalDateTime start = dateUtc.atStartOfDay();
        LocalDateTime end   = start.plusDays(1);
        return repository.findByDateRange(start, end)
                .stream().map(this::toDto).toList();
    }

    public void deleteById(Long id) { repository.deleteById(id); }

    private AppointmentAdminDto toDto(AppointmentEntity a) {
        return new AppointmentAdminDto(
                a.getId(),
                a.getServiceId(),
                a.getServiceDurationId(),
                a.getCustomerName(),
                a.getCustomerPhone(),
                a.getStartTime(),
                a.getEndTime(),
                a.getStatus()
        );
    }
}



