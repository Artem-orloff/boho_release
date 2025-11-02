package com.example.boho_v10.repository;

import com.example.boho_v10.entity.ServiceDurationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ServiceDurationRepository extends JpaRepository<ServiceDurationEntity, Long> {
    List<ServiceDurationEntity> findByService_IdOrderBySortOrderAscDurationMinAsc(Long serviceId);
    Optional<ServiceDurationEntity> findByIdAndService_Id(Long id, Long serviceId);
    Optional<ServiceDurationEntity> findByIdAndService_IdAndService_ActiveTrue(Long id, Long serviceId);
}



