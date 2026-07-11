package br.com.officyna.administrative.supply.domain.presenter;

import br.com.officyna.administrative.supply.api.resources.SupplyResponse;
import br.com.officyna.administrative.supply.domain.entity.Supply;
import br.com.officyna.administrative.supply.domain.entity.SupplyType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class SupplyPresenterTest {

    private final SupplyPresenter presenter = new SupplyPresenter();

    private Supply buildEntity(BigDecimal purchasePrice, BigDecimal salePrice, int stock, int min, int reserved) {
        return Supply.builder()
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

    @Test
    @DisplayName("toResponse deve mapear todos os campos corretamente")
    void toResponse_DeveMappearTodosOsCampos() {
        Supply entity = buildEntity(new BigDecimal("100.00"), new BigDecimal("130.00"), 50, 10, 5);

        SupplyResponse response = presenter.toResponse(entity);

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
        Supply entity = buildEntity(new BigDecimal("100.00"), new BigDecimal("130.00"), 50, 10, 8);

        SupplyResponse response = presenter.toResponse(entity);

        assertEquals(42, response.availableQuantity());
    }

    @Test
    @DisplayName("toResponse deve retornar availableQuantity zero quando reserved supera stock")
    void toResponse_DeveRetornarZeroQuandoReservedSuperaStock() {
        Supply entity = buildEntity(new BigDecimal("100.00"), new BigDecimal("130.00"), 5, 10, 10);

        SupplyResponse response = presenter.toResponse(entity);

        assertEquals(0, response.availableQuantity());
    }

    @Test
    @DisplayName("toResponse deve sinalizar belowMinimumStock quando stock abaixo do mínimo")
    void toResponse_DeveDetectarEstoqueAbaixoDoMinimo() {
        Supply entity = buildEntity(new BigDecimal("100.00"), new BigDecimal("130.00"), 3, 10, 0);

        SupplyResponse response = presenter.toResponse(entity);

        assertTrue(response.belowMinimumStock());
    }

    @Test
    @DisplayName("toResponse não deve sinalizar belowMinimumStock quando stock suficiente")
    void toResponse_NaoDeveDetectarEstoqueAbaixoDoMinimo_QuandoSuficiente() {
        Supply entity = buildEntity(new BigDecimal("100.00"), new BigDecimal("130.00"), 15, 10, 0);

        SupplyResponse response = presenter.toResponse(entity);

        assertFalse(response.belowMinimumStock());
    }

    @Test
    @DisplayName("toResponse deve calcular markup corretamente a partir de purchasePrice e salePrice")
    void toResponse_DeveCalcularMarkupPercentage() {
        Supply entity = buildEntity(new BigDecimal("100.00"), new BigDecimal("130.00"), 50, 10, 0);

        SupplyResponse response = presenter.toResponse(entity);

        assertEquals(new BigDecimal("30.00"), response.markupPercentage());
    }

    @Test
    @DisplayName("toResponse deve retornar markup zero quando purchasePrice é zero")
    void toResponse_DeveRetornarMarkupZero_QuandoPurchasePriceEhZero() {
        Supply entity = buildEntity(BigDecimal.ZERO, new BigDecimal("50.00"), 10, 2, 0);

        SupplyResponse response = presenter.toResponse(entity);

        assertEquals(BigDecimal.ZERO, response.markupPercentage());
    }
}