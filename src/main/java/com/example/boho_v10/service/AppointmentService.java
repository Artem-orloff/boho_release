package com.example.boho_v10.service;

import com.example.boho_v10.dto.AppointmentCreateRequest;
import com.example.boho_v10.entity.AppointmentEntity;
import com.example.boho_v10.entity.ServiceDurationEntity;
import com.example.boho_v10.repository.AppointmentRepository;
import com.example.boho_v10.repository.ServiceDurationRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.*;
import java.util.List;
import java.util.Objects;

import static org.springframework.http.HttpStatus.*;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final ServiceDurationRepository serviceDurationRepository;
    private final JdbcTemplate jdbcTemplate;

    private final ZoneId salonZone = ZoneId.of("Europe/Berlin");

    public AppointmentService(AppointmentRepository appointmentRepository,
                              ServiceDurationRepository serviceDurationRepository,
                              JdbcTemplate jdbcTemplate) {
        this.appointmentRepository = appointmentRepository;
        this.serviceDurationRepository = serviceDurationRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<AppointmentEntity> busyForDay(Long serviceId, LocalDate day) {
        OffsetDateTime dayStart = day.atStartOfDay(salonZone).toOffsetDateTime();
        OffsetDateTime dayEnd   = day.plusDays(1).atStartOfDay(salonZone).toOffsetDateTime();
        return appointmentRepository.findBusyForDay(serviceId, dayStart, dayEnd);
    }

    // ============================================================
    //                     СОЗДАНИЕ ЗАПИСИ
    // ============================================================

    @Transactional
    public AppointmentEntity create(AppointmentCreateRequest req) {

        validate(req); // ← предварительная валидация входных данных

        // 1) Находим длительность услуги
        ServiceDurationEntity sd = serviceDurationRepository
                .findActiveByIdAndServiceId(req.getServiceDurationId(), req.getServiceId())
                .orElseThrow(() -> new ResponseStatusException(BAD_REQUEST, "Invalid serviceDurationId for this service"));
        int durationMin = sd.getDurationMin();

        // 2) Конвертируем локальное время клиента в OffsetDateTime зоны салона
        LocalDateTime startLocal = LocalDateTime.parse(req.getStartLocal());
        ZonedDateTime zStart = startLocal.atZone(salonZone);
        OffsetDateTime startAt = zStart.toOffsetDateTime();
        OffsetDateTime endAt   = startAt.plusMinutes(durationMin);

        // 3) Advisory lock (защита от гонок бронирования)
        //    Используем 2 ключа: service_id и дата (epoch days)
// 3) Advisory-lock на (serviceId, day) через один bigint-ключ
        long serviceKey = req.getServiceId();                      // <= 2^63-1 ок
        long dayKey     = zStart.toLocalDate().toEpochDay();       // дни от 1970-01-01

// Собираем детерминированный 64-битный ключ: [serviceId(32)] << 32 | [epochDay(32)]
        long lockKey = ((serviceKey & 0xFFFFFFFFL) << 32) | (dayKey & 0xFFFFFFFFL);

// ВАЖНО: эта функция ничего не возвращает — просто execute()
//        jdbcTemplate.execute(
//                "SELECT pg_advisory_xact_lock(?)",
//                (PreparedStatementCallback<Void>) ps -> {
//                    ps.setLong(1, lockKey);
//                    ps.execute();
//                    return null;
//                }
//        );


        // 4) Проверка пересечений
        boolean conflict = appointmentRepository.existsOverlap(req.getServiceId(), startAt, endAt);
        if (conflict) {
            throw new ResponseStatusException(CONFLICT, "TIME_SLOT_TAKEN");
        }

        // 5) Создание и сохранение записи
        AppointmentEntity a = new AppointmentEntity();
        a.setServiceId(req.getServiceId());
        a.setStartAt(startAt);
        a.setEndAt(endAt);
        a.setDurationMin(durationMin);
        a.setCustomerName(req.getCustomerName().trim());
        a.setCustomerPhone(req.getCustomerPhone().trim());
        a.setComment(req.getComment());

        return appointmentRepository.save(a);
    }

    // ============================================================
    //                     ДОПОЛНИТЕЛЬНЫЕ МЕТОДЫ
    // ============================================================

    public List<AppointmentEntity> getForDateUTC(LocalDate dateUtc) {
        var start = dateUtc.atStartOfDay(ZoneOffset.UTC).toOffsetDateTime();
        var end   = dateUtc.plusDays(1).atStartOfDay(ZoneOffset.UTC).toOffsetDateTime();
        return appointmentRepository
                .findAllByStartAtGreaterThanEqualAndStartAtLessThanOrderByStartAtAsc(start, end);
    }

    // ============================================================
    //                     ВАЛИДАЦИЯ ВХОДНЫХ ДАННЫХ
    // ============================================================

    private void validate(AppointmentCreateRequest req) {
        if (req.getServiceId() == null || req.getServiceId() <= 0)
            throw new ResponseStatusException(BAD_REQUEST, "Invalid serviceId");

        if (req.getServiceDurationId() == null || req.getServiceDurationId() <= 0)
            throw new ResponseStatusException(BAD_REQUEST, "Invalid serviceDurationId");

        if (req.getCustomerName() == null || req.getCustomerName().trim().length() < 2)
            throw new ResponseStatusException(BAD_REQUEST, "Invalid customerName");

        if (req.getCustomerPhone() == null || !req.getCustomerPhone()
                .matches("^\\+7\\s?\\(9\\d{2}\\)\\s?\\d{3}-\\d{2}-\\d{2}$"))
            throw new ResponseStatusException(BAD_REQUEST, "Invalid customerPhone");

        if (req.getStartLocal() == null || req.getStartLocal().isBlank()) {
            throw new ResponseStatusException(BAD_REQUEST, "Missing startLocal");
        }

        try {
            LocalDateTime.parse(req.getStartLocal());
        } catch (Exception e) {
            throw new ResponseStatusException(BAD_REQUEST, "Invalid startLocal format");
        }
    }
}
