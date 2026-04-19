package br.com.officyna.administrative.labor.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class LaborEntityTest {

    @Test
    @DisplayName("Deve validar a criação da entidade via Builder e Getters")
    void builderAndGetters_ShouldWorkCorrectly() {
        String id = "id-123";
        String name = "Troca de Óleo";
        String description = "Serviço básico de manutenção";
        BigDecimal price = new BigDecimal("150.00");
        Integer executionTime = 1;

        LaborEntity entity = LaborEntity.builder()
                .id(id)
                .name(name)
                .description(description)
                .price(price)
                .executionTimeInDays(executionTime)
                .active(true)
                .build();

        assertAll(
                () -> assertEquals(id, entity.getId()),
                () -> assertEquals(name, entity.getName()),
                () -> assertEquals(description, entity.getDescription()),
                () -> assertEquals(price, entity.getPrice()),
                () -> assertEquals(executionTime, entity.getExecutionTimeInDays()),
                () -> assertTrue(entity.getActive())
        );
    }
}