package br.com.officyna.administrative.supply.domain.mapper;

import br.com.officyna.administrative.supply.api.resources.SupplyRequest;
import br.com.officyna.administrative.supply.api.resources.SupplyResponse;
import br.com.officyna.administrative.supply.domain.SupplyEntity;
import br.com.officyna.administrative.supply.domain.SupplyType;
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

    private SupplyEntity buildEntity(BigDecimal purchasePrice, BigDecimal salePrice, int stock, int min, int reserved) {
        return SupplyEntity.builder()
                .id("sup-1")
                .name("Óleo Motor 5W30")
                .description("Óleo sintético")
                .type(SupplyType.SUPPLY)
                .purchasePrice(purchasePrice)
                .salePrice(salePrice)
                .stockQuantity(stock)
                .minimumQuantity(min)
                .reservedQuantity(reserved)
                .active(true)
                .build();
    }

    // ─────────────── toEntity ───────────────

    @Test
    @DisplayName("toEntity deve mapear todos os campos corretamente")
    void toEntity_DeveMappearTodosOsCampos() {
        SupplyRequest request = buildRequest(new BigDecimal("100.00"), new BigDecimal("30.00"), 50, 10, 0);

        SupplyEntity entity = mapper.toEntity(request);

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

        SupplyEntity entity = mapper.toEntity(request);

        assertTrue(entity.getActive());
    }

    @Test
    @DisplayName("toEntity deve calcular salePrice com markup de 30%")
    void toEntity_DeveCalcularSalePriceComMarkup30() {
        // 100 * (1 + 30/100) = 130.00
        SupplyRequest request = buildRequest(new BigDecimal("100.00"), new BigDecimal("30.00"), 10, 2, 0);

        SupplyEntity entity = mapper.toEntity(request);

        assertEquals(new BigDecimal("130.00"), entity.getSalePrice());
    }

    @Test
    @DisplayName("toEntity deve calcular salePrice com markup de 0%")
    void toEntity_DeveCalcularSalePriceComMarkupZero() {
        // 100 * (1 + 0) = 100.00
        SupplyRequest request = buildRequest(new BigDecimal("100.00"), new BigDecimal("0.00"), 10, 2, 0);

        SupplyEntity entity = mapper.toEntity(request);

        assertEquals(new BigDecimal("100.00"), entity.getSalePrice());
    }

    @Test
    @DisplayName("toEntity deve calcular salePrice com markup de 50%")
    void toEntity_DeveCalcularSalePriceComMarkup50() {
        // 45.90 * 1.5 = 68.85
        SupplyRequest request = buildRequest(new BigDecimal("45.90"), new BigDecimal("50.00"), 30, 5, 0);

        SupplyEntity entity = mapper.toEntity(request);

        assertEquals(new BigDecimal("68.85"), entity.getSalePrice());
    }

    // ─────────────── toResponse ───────────────

    @Test
    @DisplayName("toResponse deve mapear todos os campos corretamente")
    void toResponse_DeveMappearTodosOsCampos() {
        SupplyEntity entity = buildEntity(new BigDecimal("100.00"), new BigDecimal("130.00"), 50, 10, 5);

        SupplyResponse response = mapper.toResponse(entity);

        assertEquals("sup-1", response.id());
        assertEquals("Óleo Motor 5W30", response.name());
        assertEquals(SupplyType.SUPPLY, response.type());
        assertEquals(new BigDecimal("100.00"), response.purchasePrice());
        assertEquals(new BigDecimal("130.00"), response.salePrice());
        assertEquals(50, response.stockQuantity());
        assertEquals(10, response.minimumQuantity());
        assertEquals(5, response.reservedQuantity());
        assertTrue(response.active());
    }

    @Test
    @DisplayName("toResponse deve calcular availableQuantity como stock menos reserved")
    void toResponse_DeveCalcularAvailableQuantity() {
        SupplyEntity entity = buildEntity(new BigDecimal("100.00"), new BigDecimal("130.00"), 50, 10, 8);

        SupplyResponse response = mapper.toResponse(entity);

        assertEquals(42, response.availableQuantity());
    }

    @Test
    @DisplayName("toResponse deve retornar availableQuantity zero quando reserved supera stock")
    void toResponse_DeveRetornarZeroQuandoReservedSuperaStock() {
        SupplyEntity entity = buildEntity(new BigDecimal("100.00"), new BigDecimal("130.00"), 5, 10, 10);

        SupplyResponse response = mapper.toResponse(entity);

        assertEquals(0, response.availableQuantity());
    }

    @Test
    @DisplayName("toResponse deve sinalizar belowMinimumStock quando stock abaixo do mínimo")
    void toResponse_DeveDetectarEstoqueAbaixoDoMinimo() {
        SupplyEntity entity = buildEntity(new BigDecimal("100.00"), new BigDecimal("130.00"), 3, 10, 0);

        SupplyResponse response = mapper.toResponse(entity);

        assertTrue(response.belowMinimumStock());
    }

    @Test
    @DisplayName("toResponse não deve sinalizar belowMinimumStock quando stock suficiente")
    void toResponse_NaoDeveDetectarEstoqueAbaixoDoMinimo_QuandoSuficiente() {
        SupplyEntity entity = buildEntity(new BigDecimal("100.00"), new BigDecimal("130.00"), 15, 10, 0);

        SupplyResponse response = mapper.toResponse(entity);

        assertFalse(response.belowMinimumStock());
    }

    @Test
    @DisplayName("toResponse deve calcular markup corretamente a partir de purchasePrice e salePrice")
    void toResponse_DeveCalcularMarkupPercentage() {
        // (130 - 100) / 100 * 100 = 30%
        SupplyEntity entity = buildEntity(new BigDecimal("100.00"), new BigDecimal("130.00"), 50, 10, 0);

        SupplyResponse response = mapper.toResponse(entity);

        assertEquals(new BigDecimal("30.00"), response.markupPercentage());
    }

    @Test
    @DisplayName("toResponse deve retornar markup zero quando purchasePrice é zero")
    void toResponse_DeveRetornarMarkupZero_QuandoPurchasePriceEhZero() {
        SupplyEntity entity = buildEntity(BigDecimal.ZERO, new BigDecimal("50.00"), 10, 2, 0);

        SupplyResponse response = mapper.toResponse(entity);

        assertEquals(BigDecimal.ZERO, response.markupPercentage());
    }

    // ─────────────── updateEntity ───────────────

    @Test
    @DisplayName("updateEntity deve atualizar todos os campos do entity")
    void updateEntity_DeveAtualizarTodosOsCampos() {
        SupplyEntity entity = buildEntity(new BigDecimal("100.00"), new BigDecimal("130.00"), 50, 10, 2);
        SupplyRequest request = buildRequest(new BigDecimal("200.00"), new BigDecimal("25.00"), 80, 20, 3);

        mapper.updateEntity(entity, request);

        assertEquals("Óleo Motor 5W30", entity.getName());
        assertEquals(new BigDecimal("200.00"), entity.getPurchasePrice());
        assertEquals(80, entity.getStockQuantity());
        assertEquals(20, entity.getMinimumQuantity());
        assertEquals(3, entity.getReservedQuantity());
    }

    @Test
    @DisplayName("updateEntity deve recalcular salePrice com o novo markup")
    void updateEntity_DeveRecalcularSalePrice() {
        // novo: 200 * (1 + 25/100) = 250.00
        SupplyEntity entity = buildEntity(new BigDecimal("100.00"), new BigDecimal("130.00"), 50, 10, 0);
        SupplyRequest request = buildRequest(new BigDecimal("200.00"), new BigDecimal("25.00"), 50, 10, 0);

        mapper.updateEntity(entity, request);

        assertEquals(new BigDecimal("250.00"), entity.getSalePrice());
    }
}