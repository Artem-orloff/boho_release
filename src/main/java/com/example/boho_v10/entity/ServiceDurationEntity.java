package com.example.boho_v10.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "service_durations")
public class ServiceDurationEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_id", nullable = false)
    private ServiceEntity service;                  // <-- имя поля: service

    @Column(name = "duration_min", nullable = false)
    private Integer durationMin;

    @Column(name = "price_cents", nullable = false)
    private Integer priceCents;

    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder = 100;

    @Column(name = "is_active", nullable = false)
    private Boolean active = true;

    @Column(name = "created_at", insertable = false, updatable = false)
    private java.sql.Timestamp createdAt;

    // getters/setters
    public Long getId() { return id; }

    public ServiceEntity getService() { return service; }
    public void setService(ServiceEntity service) { this.service = service; } // <-- Была опечатка

    // удобный «шорткат», чтобы не переписывать везде:
    public Long getServiceId() { return service != null ? service.getId() : null; }

    public Integer getDurationMin() { return durationMin; }
    public void setDurationMin(Integer durationMin) { this.durationMin = durationMin; }

    public Integer getPriceCents() { return priceCents; }
    public void setPriceCents(Integer priceCents) { this.priceCents = priceCents; }

    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }

    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }

    public java.sql.Timestamp getCreatedAt() { return createdAt; }
}






