package br.com.officyna.administrative.vehicle.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class VehicleEntityTest {

    @Test
    @DisplayName("Deve validar a criação da entidade via Builder e Getters")
    void builderAndGetters_ShouldWorkCorrectly() {
        String id = "id-123";
        String customerId = "customer-456";
        String customerName = "João Silva";
        String plate = "ABC-1234";
        String brand = "Toyota";
        String model = "Corolla";
        Integer year = 2020;
        String color = "Prata";
        LocalDateTime now = LocalDateTime.now();

        VehicleEntity entity = VehicleEntity.builder()
                .id(id)
                .customerId(customerId)
                .customerName(customerName)
                .plate(plate)
                .brand(brand)
                .model(model)
                .year(year)
                .color(color)
                .active(true)
                .createdAt(now)
                .updatedAt(now)
                .build();

        assertAll(
                () -> assertEquals(id, entity.getId()),
                () -> assertEquals(customerId, entity.getCustomerId()),
                () -> assertEquals(customerName, entity.getCustomerName()),
                () -> assertEquals(plate, entity.getPlate()),
                () -> assertEquals(brand, entity.getBrand()),
                () -> assertEquals(model, entity.getModel()),
                () -> assertEquals(year, entity.getYear()),
                () -> assertEquals(color, entity.getColor()),
                () -> assertTrue(entity.isActive()),
                () -> assertEquals(now, entity.getCreatedAt()),
                () -> assertEquals(now, entity.getUpdatedAt())
        );
    }
}