package br.com.officyna.administrative.supply.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class SupplyEntityTest {

    @Test
    @DisplayName("Deve validar a criação da entidade via Builder e Getters")
    void builderAndGetters_ShouldWorkCorrectly() {
        String id = "id-123";
        String name = "Óleo Motor 5W30";
        String description = "Óleo sintético para motor a gasolina";
        BigDecimal purchasePrice = new BigDecimal("45.90");
        BigDecimal salePrice = new BigDecimal("59.67");
        LocalDateTime now = LocalDateTime.now();

        SupplyEntity entity = SupplyEntity.builder()
                .id(id)
                .name(name)
                .description(description)
                .type(SupplyType.SUPPLY)
                .purchasePrice(purchasePrice)
                .salePrice(salePrice)
                .stockQuantity(50)
                .minimumQuantity(10)
                .reservedQuantity(3)
                .active(true)
                .createdAt(now)
                .updatedAt(now)
                .build();

        assertAll(
                () -> assertEquals(id, entity.getId()),
                () -> assertEquals(name, entity.getName()),
                () -> assertEquals(description, entity.getDescription()),
                () -> assertEquals(SupplyType.SUPPLY, entity.getType()),
                () -> assertEquals(purchasePrice, entity.getPurchasePrice()),
                () -> assertEquals(salePrice, entity.getSalePrice()),
                () -> assertEquals(50, entity.getStockQuantity()),
                () -> assertEquals(10, entity.getMinimumQuantity()),
                () -> assertEquals(3, entity.getReservedQuantity()),
                () -> assertTrue(entity.getActive()),
                () -> assertEquals(now, entity.getCreatedAt()),
                () -> assertEquals(now, entity.getUpdatedAt())
        );
    }
}