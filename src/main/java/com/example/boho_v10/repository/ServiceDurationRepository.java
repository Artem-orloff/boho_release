package com.example.boho_v10.repository;

import com.example.boho_v10.entity.ServiceDurationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ServiceDurationRepository extends JpaRepository<ServiceDurationEntity, Long> {

    // Проверка принадлежности duration -> service, только активные
    @Query(value = """
            select * 
            from service_durations sd
            where sd.id = :sdId
              and sd.service_id = :serviceId
              and sd.is_active = true
            """, nativeQuery = true)
    Optional<ServiceDurationEntity> findActiveByIdAndServiceId(
            @Param("sdId") Long sdId,
            @Param("serviceId") Long serviceId
    );

    // Список длительностей услуги для фронта (активные, отсортированные)
    @Query(value = """
            select *
            from service_durations sd
            where sd.service_id = :serviceId
              and sd.is_active = true
            order by sd.sort_order asc, sd.duration_min asc
            """, nativeQuery = true)
    List<ServiceDurationEntity> findAllActiveByServiceIdOrdered(
            @Param("serviceId") Long serviceId
    );
}
