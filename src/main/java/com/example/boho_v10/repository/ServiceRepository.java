package com.example.boho_v10.repository;

import com.example.boho_v10.entity.ServiceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ServiceRepository extends JpaRepository<ServiceEntity, Long> {
    List<ServiceEntity> findAllByActiveTrueOrderByNameAsc();

    Optional<ServiceEntity> findByIdAndActiveTrue(Long id);

    @Query("select s.name from ServiceEntity s where s.id = :id")
    String findNameById(@Param("id") Long id);
}
