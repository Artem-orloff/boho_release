package com.example.boho_v10.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "service_durations")
public class ServiceDurationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_id", nullable = false)
    private ServiceEntity service;

    @Column(name = "duration_min", nullable = false)
    private Integer durationMin;

    @Column(name = "price_cents", nullable = false)
    private Integer priceCents;

    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder = 100;

//    public ServiceDurationEntity() {}
//
//    public ServiceDurationEntity(ServiceEntity service, Integer durationMin, Integer priceCents, Integer sortOrder) {
//        this.service = service;
//        this.durationMin = durationMin;
//        this.priceCents = priceCents;
//        this.sortOrder = sortOrder;
//    }

    // геттеры/сеттеры
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public ServiceEntity getService() { return service; }
    public void setService(ServiceEntity service) { this.service = service; }

    public Integer getDurationMin() { return durationMin; }
    public void setDurationMin(Integer durationMin) { this.durationMin = durationMin; }

    public Integer getPriceCents() { return priceCents; }
    public void setPriceCents(Integer priceCents) { this.priceCents = priceCents; }

    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }
}



