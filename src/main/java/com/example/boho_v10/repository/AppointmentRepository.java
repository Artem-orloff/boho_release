package com.example.boho_v10.repository;

import com.example.boho_v10.entity.AppointmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<AppointmentEntity, Long> {

    // Уже использует AppointmentService (проверка пересечений)
    @Query("""
         select case when count(a)>0 then true else false end
           from AppointmentEntity a
          where a.status = 'booked'
            and a.startTime < :endTime
            and :startTime < a.endTime
         """)
    boolean existsByStatusBookedAndOverlap(LocalDateTime startTime, LocalDateTime endTime);

    // Для AvailabilityController: занятость за день по услуге
    @Query("""
         select a from AppointmentEntity a
          where a.serviceId = :serviceId
            and a.status = 'booked'
            and a.startTime >= :dayStart
            and a.startTime <  :dayEnd
          order by a.startTime asc
         """)
    List<AppointmentEntity> findDayByService(Long serviceId, LocalDateTime dayStart, LocalDateTime dayEnd);

    // Для админ-списка: все брони по убыванию времени старта
    // (можно без @Query — это derived query по имени)
    List<AppointmentEntity> findAllByOrderByStartTimeDesc();

    // Для админ-списка: брони в диапазоне дат/времени
    @Query("""
         select a from AppointmentEntity a
          where a.startTime >= :start and a.startTime <  :end
          order by a.startTime asc
         """)
    List<AppointmentEntity> findByDateRange(LocalDateTime start, LocalDateTime end);
}



