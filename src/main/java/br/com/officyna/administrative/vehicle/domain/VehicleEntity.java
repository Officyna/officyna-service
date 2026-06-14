package br.com.officyna.administrative.vehicle.domain;

import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehicleEntity {

    private String id;

    private String customerId;
    private String customerName;

    private String plate;

    private String brand;
    private String model;
    private Integer year;
    private String color;

    private boolean active;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}