package com.example.boho_v10.dto;

import java.time.LocalDateTime;

public class AppointmentAdminDto {
    private final Long id;
    private final Long serviceId;
    private final String serviceName;
    private final Long serviceDurationId;
    private final Integer durationMin;
    private final LocalDateTime startTime;
    private final LocalDateTime endTime;
    private final String status;
    private final String customerName;
    private final String customerPhone;
    private final String comment;
    private final LocalDateTime createdAt;

    public AppointmentAdminDto(Long id,
                               Long serviceId,
                               String serviceName,
                               Long serviceDurationId,
                               Integer durationMin,
                               LocalDateTime startTime,
                               LocalDateTime endTime,
                               String status,
                               String customerName,
                               String customerPhone,
                               String comment,
                               LocalDateTime createdAt) {
        this.id = id;
        this.serviceId = serviceId;
        this.serviceName = serviceName;
        this.serviceDurationId = serviceDurationId;
        this.durationMin = durationMin;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
        this.customerName = customerName;
        this.customerPhone = customerPhone;
        this.comment = comment;
        this.createdAt = createdAt;
    }

    public AppointmentAdminDto(Long id,
                               Long serviceId,
                               Long serviceDurationId,
                               String customerName,
                               String customerPhone,
                               LocalDateTime startTime,
                               LocalDateTime endTime,
                               String status) {
        this(id, serviceId, null, serviceDurationId, null, startTime, endTime, status,
                customerName, customerPhone, null, null);
    }

    public Long getId() { return id; }
    public Long getServiceId() { return serviceId; }
    public String getServiceName() { return serviceName; }
    public Long getServiceDurationId() { return serviceDurationId; }
    public Integer getDurationMin() { return durationMin; }
    public LocalDateTime getStartTime() { return startTime; }
    public LocalDateTime getEndTime() { return endTime; }
    public String getStatus() { return status; }
    public String getCustomerName() { return customerName; }
    public String getCustomerPhone() { return customerPhone; }
    public String getComment() { return comment; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}