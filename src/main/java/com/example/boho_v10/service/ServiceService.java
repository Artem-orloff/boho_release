package com.example.boho_v10.service;

import com.example.boho_v10.dto.ServiceDto;
import com.example.boho_v10.dto.ServiceDurationDto;
import com.example.boho_v10.entity.ServiceEntity;
import com.example.boho_v10.entity.ServiceDurationEntity;
import com.example.boho_v10.repository.ServiceRepository;
import com.example.boho_v10.repository.ServiceDurationRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ServiceService {

    private final ServiceRepository serviceRepo;
    private final ServiceDurationRepository durationRepo;

    public ServiceService(ServiceRepository serviceRepo, ServiceDurationRepository durationRepo) {
        this.serviceRepo = serviceRepo;
        this.durationRepo = durationRepo;
    }

    public List<ServiceDto> getActiveServices() {
        return serviceRepo.findAllByActiveTrueOrderByNameAsc()
                .stream()
                .map(ServiceService::toDto)
                .toList();
    }

    public ServiceDto getService(Long id) {
        ServiceEntity e = serviceRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Service not found: " + id));
        return toDto(e);
    }

    private static ServiceDto toDto(ServiceEntity e) {
        return new ServiceDto(
                e.getId(),
                e.getName(),
                null,
                e.getActive()
        );
    }

    public List<ServiceDurationDto> getDurations(long serviceId) {
        if (!serviceRepo.existsById(serviceId)) {
            throw new IllegalArgumentException("Service not found: " + serviceId);
        }
        return durationRepo
                .findByService_IdOrderBySortOrderAscDurationMinAsc(serviceId)
                .stream()
                .map(d -> new ServiceDurationDto(
                        d.getId(),
                        d.getDurationMin(),
                        d.getPriceCents(),
                        d.getSortOrder()
                ))
                .toList();
    }


    private static ServiceDurationDto toDto(ServiceDurationEntity d) {
        return new ServiceDurationDto(
                d.getId(),
                d.getDurationMin(),
                d.getPriceCents(),
                d.getSortOrder()
        );
    }
}



