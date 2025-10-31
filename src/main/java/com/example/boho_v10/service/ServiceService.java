package com.example.boho_v10.service;

import com.example.boho_v10.dto.ServiceDto;
import com.example.boho_v10.entity.ServiceEntity;
import com.example.boho_v10.repository.ServiceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ServiceService {

    private final ServiceRepository repo;

    public ServiceService(ServiceRepository repo) {
        this.repo = repo;
    }

    @Transactional(readOnly = true)
    public List<ServiceDto> getActiveServices() {
        return repo.findAllByActiveTrueOrderByNameAsc().stream()
                .map(this::toDto)
                .toList();
    }

    private ServiceDto toDto(ServiceEntity e) {
        return new ServiceDto(e.getId(), e.getName(), e.getDurationMin());
    }
}
