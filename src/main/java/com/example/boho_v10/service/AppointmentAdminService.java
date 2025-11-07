package com.example.boho_v10.service;

import com.example.boho_v10.dto.AppointmentAdminDto;
import com.example.boho_v10.entity.AppointmentEntity;
import com.example.boho_v10.repository.AppointmentRepository;
import com.example.boho_v10.repository.ServiceRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class AppointmentAdminService {

    private final AppointmentRepository repository;
    private final ServiceRepository serviceRepository;

    public AppointmentAdminService(AppointmentRepository repository,
                                   ServiceRepository serviceRepository) {
        this.repository = repository;
        this.serviceRepository = serviceRepository;
    }

    public List<AppointmentAdminDto> findAllByOrderByStartTimeDesc() {
        return repository.findAdminList();
    }

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

    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    private AppointmentAdminDto toDto(AppointmentEntity a) {
        String serviceName = serviceRepository.findNameById(a.getServiceId());
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