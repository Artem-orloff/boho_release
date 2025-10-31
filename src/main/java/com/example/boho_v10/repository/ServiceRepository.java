package com.example.boho_v10.repository;

import com.example.boho_v10.entity.ServiceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ServiceRepository extends JpaRepository<ServiceEntity, Long> {
    List<ServiceEntity> findAllByActiveTrueOrderByNameAsc();
}
