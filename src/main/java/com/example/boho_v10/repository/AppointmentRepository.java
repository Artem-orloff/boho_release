package com.example.boho_v10.repository;

import com.example.boho_v10.dto.AppointmentAdminDto;
import com.example.boho_v10.entity.AppointmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<AppointmentEntity, Long> {

    @Query("""
        select new com.example.boho_v10.dto.AppointmentAdminDto(
            a.id,
            a.serviceId,
            s.name,
            a.serviceDurationId,
            a.durationMin,
            a.startTime,
            a.endTime,
            a.status,
            a.customerName,
            a.customerPhone,
            a.comment,
            a.createdAt
        )
        from AppointmentEntity a
        left join a.service s
        order by a.createdAt desc
        """)
    List<AppointmentAdminDto> findAdminList();

    @Query("""
         select case when count(a)>0 then true else false end
           from AppointmentEntity a
          where a.status = 'booked'
            and a.startTime < :endTime
            and :startTime < a.endTime
         """)
    boolean existsByStatusBookedAndOverlap(LocalDateTime startTime, LocalDateTime endTime);

        @Query("""
      select a from AppointmentEntity a
       where a.status = 'booked'
         and a.startTime >= :dayStart
         and a.startTime <  :dayEnd
       order by a.startTime asc
    """)
    List<AppointmentEntity> findDayBusy(@Param("dayStart") LocalDateTime dayStart,
                                        @Param("dayEnd")   LocalDateTime dayEnd);

    List<AppointmentEntity> findAllByOrderByStartTimeDesc();

    @Query("""
         select a from AppointmentEntity a
          where a.startTime >= :start and a.startTime <  :end
          order by a.startTime asc
         """)
    List<AppointmentEntity> findByDateRange(LocalDateTime start, LocalDateTime end);
}