package com.example.boho_v10.dto;

public class ServiceDto {
    private Long id;
    private String name;
    private Integer durationMin;

    public ServiceDto() {}

    public ServiceDto(Long id, String name, Integer durationMin) {
        this.id = id;
        this.name = name;
        this.durationMin = durationMin;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public Integer getDurationMin() { return durationMin; }

    public void setId(Long id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setDurationMin(Integer durationMin) { this.durationMin = durationMin; }
}
