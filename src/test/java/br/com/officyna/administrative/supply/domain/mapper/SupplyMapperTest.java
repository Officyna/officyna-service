package br.com.officyna.administrative.supply.domain.mapper;

import br.com.officyna.administrative.supply.api.resources.SupplyRequest;
import br.com.officyna.administrative.supply.domain.entity.Supply;
import br.com.officyna.administrative.supply.domain.entity.SupplyType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class SupplyMapperTest {

    private final SupplyMapper mapper = new SupplyMapper();

    private SupplyRequest buildRequest(BigDecimal purchasePrice, BigDecimal markup, int stock, int min, int reserved) {
        return new SupplyRequest(
                "Óleo Motor 5W30",
                "Óleo sintético",
                SupplyType.SUPPLY,
                purchasePrice,
                markup,
                stock,
                min,
                reserved
        );
    }

    @Test
    @DisplayName("toEntity deve mapear todos os campos corretamente")
    void toEntity_DeveMappearTodosOsCampos() {
        SupplyRequest request = buildRequest(new BigDecimal("100.00"), new BigDecimal("30.00"), 50, 10, 0);

        Supply entity = mapper.toEntity(request);

        assertEquals("Óleo Motor 5W30", entity.getName());
        assertEquals("Óleo sintético", entity.getDescription());
        assertEquals(SupplyType.SUPPLY, entity.getType());
        assertEquals(new BigDecimal("100.00"), entity.getPurchasePrice());
        assertEquals(50, entity.getStockQuantity());
        assertEquals(10, entity.getMinimumQuantity());
        assertEquals(0, entity.getReservedQuantity());
    }

    @Test
    @DisplayName("toEntity deve definir active como true")
    void toEntity_DeveDefinirActiveTrue() {
        SupplyRequest request = buildRequest(new BigDecimal("50.00"), new BigDecimal("20.00"), 10, 2, 0);

        Supply entity = mapper.toEntity(request);

        assertTrue(entity.getActive());
    }

    @Test
    @DisplayName("toEntity deve calcular salePrice com markup de 30%")
    void toEntity_DeveCalcularSalePriceComMarkup30() {
        SupplyRequest request = buildRequest(new BigDecimal("100.00"), new BigDecimal("30.00"), 10, 2, 0);

        Supply entity = mapper.toEntity(request);

        assertEquals(new BigDecimal("130.00"), entity.getSalePrice());
    }

    @Test
    @DisplayName("toEntity deve calcular salePrice com markup de 0%")
    void toEntity_DeveCalcularSalePriceComMarkupZero() {
        SupplyRequest request = buildRequest(new BigDecimal("100.00"), new BigDecimal("0.00"), 10, 2, 0);

        Supply entity = mapper.toEntity(request);

        assertEquals(new BigDecimal("100.00"), entity.getSalePrice());
    }

    @Test
    @DisplayName("toEntity deve calcular salePrice com markup de 50%")
    void toEntity_DeveCalcularSalePriceComMarkup50() {
        SupplyRequest request = buildRequest(new BigDecimal("45.90"), new BigDecimal("50.00"), 30, 5, 0);

        Supply entity = mapper.toEntity(request);

        assertEquals(new BigDecimal("68.85"), entity.getSalePrice());
    }
}