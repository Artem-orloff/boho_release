package com.example.boho_v10.repository;

import com.example.boho_v10.entity.AppointmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<AppointmentEntity, Long> {

    @Query("""
        select (count(a) > 0) from AppointmentEntity a
        where a.serviceId = :serviceId
          and a.startAt < :newEnd
          and :newStart < a.endAt
    """)
    boolean existsOverlap(@Param("serviceId") Long serviceId,
                          @Param("newStart") OffsetDateTime newStart,
                          @Param("newEnd")   OffsetDateTime newEnd);

    @Query("""
        select a from AppointmentEntity a
        where a.startAt >= :dayStart and a.startAt < :dayEnd and a.serviceId = :serviceId
        order by a.startAt
    """)
    List<AppointmentEntity> findBusyForDay(@Param("serviceId") Long serviceId,
                                           @Param("dayStart") OffsetDateTime dayStart,
                                           @Param("dayEnd")   OffsetDateTime dayEnd);

    // для AppointmentAdminService (ошибка "findAllByOrderByStartDesc")
    List<AppointmentEntity> findAllByOrderByStartAtDesc();


    List<AppointmentEntity> findAllByStartAtGreaterThanEqualAndStartAtLessThanOrderByStartAtAsc(
            java.time.OffsetDateTime dayStart,
            java.time.OffsetDateTime dayEnd
    );
}
