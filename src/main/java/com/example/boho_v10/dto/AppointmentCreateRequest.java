package com.example.boho_v10.dto;

public class AppointmentCreateRequest {
    private Long serviceId;
    private Long serviceDurationId;   // id строки из service_durations
    private String startLocal;        // "YYYY-MM-DDTHH:mm" (локальная зона салона)
    private String customerName;
    private String customerPhone;
    private String comment;

    public AppointmentCreateRequest() {}

    public Long getServiceId() { return serviceId; }
    public void setServiceId(Long serviceId) { this.serviceId = serviceId; }

    public Long getServiceDurationId() { return serviceDurationId; }
    public void setServiceDurationId(Long serviceDurationId) { this.serviceDurationId = serviceDurationId; }

    public String getStartLocal() { return startLocal; }
    public void setStartLocal(String startLocal) { this.startLocal = startLocal; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getCustomerPhone() { return customerPhone; }
    public void setCustomerPhone(String customerPhone) { this.customerPhone = customerPhone; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
}
