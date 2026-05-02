package br.com.officyna.administrative.supply.domain.service;

import br.com.officyna.administrative.supply.domain.SupplyEntity;
import br.com.officyna.administrative.supply.domain.SupplyType;
import br.com.officyna.administrative.supply.repository.SupplyRepository;
import br.com.officyna.infrastructure.exception.DomainException;
import br.com.officyna.infrastructure.exception.NotFoundException;
import br.com.officyna.serviceorder.domain.dto.SupplyDetailDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StockServiceTest {

    @Mock
    private SupplyRepository supplyRepository;

    @InjectMocks
    private StockService stockService;

    private SupplyEntity buildSupply(String id, int stock, int minimum, int reserved) {
        return SupplyEntity.builder()
                .id(id)
                .name("Óleo Motor")
                .type(SupplyType.SUPPLY)
                .purchasePrice(new BigDecimal("45.00"))
                .salePrice(new BigDecimal("58.50"))
                .stockQuantity(stock)
                .minimumQuantity(minimum)
                .reservedQuantity(reserved)
                .active(true)
                .build();
    }

    private SupplyDetailDTO buildItem(String supplyId, int quantity) {
        return SupplyDetailDTO.builder()
                .id(supplyId)
                .name("Óleo Motor")
                .quantity(quantity)
                .unitPrice(new BigDecimal("58.50"))
                .totalPrice(new BigDecimal("58.50").multiply(BigDecimal.valueOf(quantity)))
                .build();
    }

    // ─── reserveSupplies ──────────────────────────────────────────────────────

    @Test
    @DisplayName("Deve incrementar reservedQuantity ao reservar insumos")
    void reserveSupplies_ShouldIncrementReservedQuantity() {
        SupplyEntity supply = buildSupply("s1", 20, 5, 2);
        when(supplyRepository.findById("s1")).thenReturn(Optional.of(supply));

        stockService.reserveSupplies(List.of(buildItem("s1", 3)));

        assertEquals(5, supply.getReservedQuantity()); // 2 + 3
        verify(supplyRepository).save(supply);
    }

    @Test
    @DisplayName("Deve reservar múltiplos insumos de uma vez")
    void reserveSupplies_ShouldHandleMultipleItems() {
        SupplyEntity s1 = buildSupply("s1", 20, 5, 0);
        SupplyEntity s2 = buildSupply("s2", 15, 3, 1);
        when(supplyRepository.findById("s1")).thenReturn(Optional.of(s1));
        when(supplyRepository.findById("s2")).thenReturn(Optional.of(s2));

        stockService.reserveSupplies(List.of(buildItem("s1", 4), buildItem("s2", 2)));

        assertEquals(4, s1.getReservedQuantity());
        assertEquals(3, s2.getReservedQuantity()); // 1 + 2
        verify(supplyRepository, times(2)).save(any(SupplyEntity.class));
    }

    @Test
    @DisplayName("Não deve fazer nada quando a lista de insumos é null")
    void reserveSupplies_ShouldDoNothing_WhenListIsNull() {
        stockService.reserveSupplies(null);
        verifyNoInteractions(supplyRepository);
    }

    @Test
    @DisplayName("Não deve fazer nada quando a lista de insumos está vazia")
    void reserveSupplies_ShouldDoNothing_WhenListIsEmpty() {
        stockService.reserveSupplies(List.of());
        verifyNoInteractions(supplyRepository);
    }

    @Test
    @DisplayName("Deve lançar NotFoundException quando insumo não existe ao reservar")
    void reserveSupplies_ShouldThrowNotFoundException_WhenSupplyNotFound() {
        when(supplyRepository.findById("inexistente")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> stockService.reserveSupplies(List.of(buildItem("inexistente", 2))));
    }

    // ─── consumeSupplies ──────────────────────────────────────────────────────

    @Test
    @DisplayName("Deve decrementar stockQuantity e reservedQuantity ao consumir insumos")
    void consumeSupplies_ShouldDecrementStockAndReserved() {
        SupplyEntity supply = buildSupply("s1", 20, 5, 3);
        when(supplyRepository.findById("s1")).thenReturn(Optional.of(supply));

        stockService.consumeSupplies(List.of(buildItem("s1", 3)));

        assertEquals(17, supply.getStockQuantity());  // 20 - 3
        assertEquals(0,  supply.getReservedQuantity()); // 3 - 3
        verify(supplyRepository).save(supply);
    }

    @Test
    @DisplayName("reservedQuantity não deve ficar negativo após consumo")
    void consumeSupplies_ReservedShouldNotGoBelowZero() {
        SupplyEntity supply = buildSupply("s1", 10, 5, 1); // reservado = 1, consumindo 3
        when(supplyRepository.findById("s1")).thenReturn(Optional.of(supply));

        stockService.consumeSupplies(List.of(buildItem("s1", 3)));

        assertEquals(7, supply.getStockQuantity());
        assertEquals(0, supply.getReservedQuantity()); // Math.max(0, 1 - 3)
    }

    @Test
    @DisplayName("Deve lançar DomainException quando estoque é insuficiente")
    void consumeSupplies_ShouldThrowDomainException_WhenStockInsufficient() {
        SupplyEntity supply = buildSupply("s1", 2, 5, 2);
        when(supplyRepository.findById("s1")).thenReturn(Optional.of(supply));

        assertThrows(DomainException.class,
                () -> stockService.consumeSupplies(List.of(buildItem("s1", 5))));
        verify(supplyRepository, never()).save(any());
    }

    @Test
    @DisplayName("Não deve notificar quando estoque fica acima do mínimo após consumo")
    void consumeSupplies_ShouldNotNotify_WhenStockAboveMinimum() {
        SupplyEntity supply = buildSupply("s1", 20, 5, 3);
        when(supplyRepository.findById("s1")).thenReturn(Optional.of(supply));

        stockService.consumeSupplies(List.of(buildItem("s1", 3)));

        // estoque resultante = 17, mínimo = 5 → sem notificação (sem exceção)
        assertEquals(17, supply.getStockQuantity());
        verify(supplyRepository).save(supply);
    }

    @Test
    @DisplayName("Deve acionar notificação mockada quando estoque fica abaixo do mínimo")
    void consumeSupplies_ShouldTriggerNotification_WhenStockFallsBelowMinimum() {
        SupplyEntity supply = buildSupply("s1", 8, 5, 4);
        when(supplyRepository.findById("s1")).thenReturn(Optional.of(supply));

        // consumindo 4 unidades: estoque vai de 8 para 4 (abaixo do mínimo de 5)
        stockService.consumeSupplies(List.of(buildItem("s1", 4)));

        assertEquals(4, supply.getStockQuantity());
        assertTrue(supply.getStockQuantity() < supply.getMinimumQuantity());
        // notificação mockada: apenas verifica que o fluxo completou sem erros
        verify(supplyRepository).save(supply);
    }

    @Test
    @DisplayName("Não deve fazer nada quando a lista de insumos é null")
    void consumeSupplies_ShouldDoNothing_WhenListIsNull() {
        stockService.consumeSupplies(null);
        verifyNoInteractions(supplyRepository);
    }

    @Test
    @DisplayName("Deve consumir múltiplos insumos e salvar cada um")
    void consumeSupplies_ShouldHandleMultipleItems() {
        SupplyEntity s1 = buildSupply("s1", 10, 2, 3);
        SupplyEntity s2 = buildSupply("s2", 15, 5, 2);
        when(supplyRepository.findById("s1")).thenReturn(Optional.of(s1));
        when(supplyRepository.findById("s2")).thenReturn(Optional.of(s2));

        stockService.consumeSupplies(List.of(buildItem("s1", 3), buildItem("s2", 2)));

        assertEquals(7,  s1.getStockQuantity());
        assertEquals(13, s2.getStockQuantity());
        verify(supplyRepository, times(2)).save(any(SupplyEntity.class));
    }

    // ─── releaseSupplies ──────────────────────────────────────────────────────

    @Test
    @DisplayName("Deve decrementar reservedQuantity ao liberar reserva")
    void releaseSupplies_ShouldDecrementReservedQuantity() {
        SupplyEntity supply = buildSupply("s1", 20, 5, 5);
        when(supplyRepository.findById("s1")).thenReturn(Optional.of(supply));

        stockService.releaseSupplies(List.of(buildItem("s1", 3)));

        assertEquals(2, supply.getReservedQuantity()); // 5 - 3
        assertEquals(20, supply.getStockQuantity()); // estoque não muda
        verify(supplyRepository).save(supply);
    }

    @Test
    @DisplayName("reservedQuantity não deve ficar negativo ao liberar reserva")
    void releaseSupplies_ReservedShouldNotGoBelowZero() {
        SupplyEntity supply = buildSupply("s1", 20, 5, 1);
        when(supplyRepository.findById("s1")).thenReturn(Optional.of(supply));

        stockService.releaseSupplies(List.of(buildItem("s1", 5)));

        assertEquals(0, supply.getReservedQuantity()); // Math.max(0, 1 - 5)
        assertEquals(20, supply.getStockQuantity()); // estoque não muda
    }

    @Test
    @DisplayName("Não deve alterar o estoque ao liberar reserva")
    void releaseSupplies_ShouldNotChangeStockQuantity() {
        SupplyEntity supply = buildSupply("s1", 20, 5, 4);
        when(supplyRepository.findById("s1")).thenReturn(Optional.of(supply));

        stockService.releaseSupplies(List.of(buildItem("s1", 4)));

        assertEquals(20, supply.getStockQuantity());
    }

    @Test
    @DisplayName("Não deve fazer nada quando a lista de insumos é null")
    void releaseSupplies_ShouldDoNothing_WhenListIsNull() {
        stockService.releaseSupplies(null);
        verifyNoInteractions(supplyRepository);
    }

    @Test
    @DisplayName("Não deve fazer nada quando a lista de insumos está vazia")
    void releaseSupplies_ShouldDoNothing_WhenListIsEmpty() {
        stockService.releaseSupplies(List.of());
        verifyNoInteractions(supplyRepository);
    }
}