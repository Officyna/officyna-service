package br.com.officyna.serviceorder.domain.service;

import br.com.officyna.administrative.supply.api.resources.SupplyResponse;
import br.com.officyna.administrative.supply.domain.SupplyType;
import br.com.officyna.administrative.supply.domain.service.SupplyService;
import br.com.officyna.infrastructure.exception.DomainException;
import br.com.officyna.infrastructure.exception.NotFoundException;
import br.com.officyna.serviceorder.api.resources.SupplysRequest;
import br.com.officyna.serviceorder.domain.dto.SupplyDTO;
import br.com.officyna.serviceorder.domain.dto.SupplyDetailDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SupplySelectionServiceTest {

    @Mock
    private SupplyService supplyService;

    @Spy
    private BudgetService budgetService;

    @InjectMocks
    private SupplySelectionService service;

    @Test
    @DisplayName("Deve adicionar novos suprimentos e calcular o valor total acumulado")
    void addSupplys_ShouldAddNewSupplysAndCalculateTotal() {
        // Arrange
        SupplysRequest req = new SupplysRequest("supply-1", 2);
        SupplyResponse response = new SupplyResponse(
                "supply-1",
                "Óleo 5W30",
                "Óleo sintético",
                SupplyType.SUPPLY,
                BigDecimal.valueOf(50.00),
                BigDecimal.valueOf(80.00),
                BigDecimal.valueOf(30.00),
                100,
                50,
                100,
                50,
                true,
                true,
                LocalDateTime.now(),
                LocalDateTime.now());
        
        when(supplyService.findById("supply-1")).thenReturn(response);

        // Act
        SupplyDTO result = service.addSupplys(List.of(req), new ArrayList<>());

        // Assert
        assertThat(result.getSupplysDetails()).hasSize(1);
        assertThat(result.getTotalSupplyAmount()).isEqualByComparingTo("160.00");
        assertThat(result.getSupplysDetails().get(0).getTotalPrice()).isEqualByComparingTo("160.00");
    }

    @Test
    @DisplayName("Deve remover um suprimento e recalcular o total")
    void removeSupply_ShouldRemoveAndRecalculate() {
        // Arrange
        SupplyDetailDTO detail = new SupplyDetailDTO("supply-1", "Óleo", "Desc", 2, BigDecimal.valueOf(80.00), BigDecimal.valueOf(160.00));
        List<SupplyDetailDTO> details = new ArrayList<>();
        details.add(detail);
        
        SupplyDTO supplys = new SupplyDTO();
        supplys.setSupplysDetails(details);
        supplys.setTotalSupplyAmount(BigDecimal.valueOf(160.00));

        // Act
        service.removeSupply(supplys, "supply-1");

        // Assert
        assertThat(supplys.getSupplysDetails()).isEmpty();
        assertThat(supplys.getTotalSupplyAmount()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar remover suprimento de uma lista vazia")
    void removeSupply_ShouldThrowException_WhenListIsEmpty() {
        SupplyDTO supplys = new SupplyDTO();
        supplys.setSupplysDetails(new ArrayList<>());

        assertThatThrownBy(() -> service.removeSupply(supplys, "any-id"))
                .isInstanceOf(DomainException.class)
                .hasMessage("A Ordem de Serviço não possui suprimentos cadastrados.");
    }

    @Test
    @DisplayName("Deve adicionar múltiplos suprimentos mantendo o cálculo correto")
    void addSupplys_ShouldAddMultipleSupplysAndCalculateCorrectly() {
        SupplysRequest req1 = new SupplysRequest("supply-1", 2);
        SupplysRequest req2 = new SupplysRequest("supply-2", 3);

        when(supplyService.findById("supply-1")).thenReturn(new SupplyResponse(
                "supply-1", "Óleo", "Desc", SupplyType.SUPPLY,
                BigDecimal.valueOf(50.00), BigDecimal.valueOf(80.00),
                BigDecimal.valueOf(30.00), 100, 50, 100, 50, true, true,
                LocalDateTime.now(), LocalDateTime.now()
        ));
        when(supplyService.findById("supply-2")).thenReturn(new SupplyResponse(
                "supply-2", "Filtro", "Desc", SupplyType.SUPPLY,
                BigDecimal.valueOf(20.00), BigDecimal.valueOf(50.00),
                BigDecimal.valueOf(30.00), 100, 50, 100, 50, true, true,
                LocalDateTime.now(), LocalDateTime.now()
        ));

        SupplyDTO result = service.addSupplys(List.of(req1, req2), new ArrayList<>());

        assertThat(result.getSupplysDetails()).hasSize(2);
        // 2 * 80.00 + 3 * 50.00 = 160.00 + 150.00 = 310.00
        assertThat(result.getTotalSupplyAmount()).isEqualByComparingTo("310.00");
    }

    @Test
    @DisplayName("Deve manter os suprimentos existentes ao adicionar novos")
    void addSupplys_ShouldMaintainExistingSupplys() {
        SupplyDetailDTO existing = new SupplyDetailDTO("supply-1", "Óleo", "Desc", 1, BigDecimal.valueOf(80.00), BigDecimal.valueOf(80.00));
        SupplysRequest req = new SupplysRequest("supply-2", 2);

        when(supplyService.findById("supply-2")).thenReturn(new SupplyResponse(
                "supply-2", "Filtro", "Desc", SupplyType.SUPPLY,
                BigDecimal.valueOf(20.00), BigDecimal.valueOf(50.00),
                BigDecimal.valueOf(30.00), 100, 50, 100, 50, true, true,
                LocalDateTime.now(), LocalDateTime.now()
        ));

        SupplyDTO result = service.addSupplys(List.of(req), new ArrayList<>(List.of(existing)));

        assertThat(result.getSupplysDetails()).hasSize(2);
        // 80.00 + 2 * 50.00 = 80.00 + 100.00 = 180.00
        assertThat(result.getTotalSupplyAmount()).isEqualByComparingTo("180.00");
    }

    @Test
    @DisplayName("Deve lançar exceção ao remover suprimento com ID nulo")
    void removeSupply_ShouldThrowException_WhenSupplyIdIsNull() {
        SupplyDetailDTO detail = new SupplyDetailDTO("supply-1", "Óleo", "Desc", 1, BigDecimal.valueOf(80.00), BigDecimal.valueOf(80.00));
        List<SupplyDetailDTO> details = new ArrayList<>(List.of(detail));

        SupplyDTO supplys = new SupplyDTO();
        supplys.setSupplysDetails(details);

        assertThatThrownBy(() -> service.removeSupply(supplys, null))
                .isInstanceOf(DomainException.class)
                .hasMessage("A Ordem de Serviço não possui suprimentos cadastrados.");
    }

    @Test
    @DisplayName("Deve remover corretamente um suprimento específico dentre múltiplos")
    void removeSupply_ShouldRemoveSpecificSupplyFromMultiple() {
        SupplyDetailDTO supply1 = new SupplyDetailDTO("supply-1", "Óleo", "Desc", 1, BigDecimal.valueOf(80.00), BigDecimal.valueOf(80.00));
        SupplyDetailDTO supply2 = new SupplyDetailDTO("supply-2", "Filtro", "Desc", 1, BigDecimal.valueOf(50.00), BigDecimal.valueOf(50.00));
        List<SupplyDetailDTO> details = new ArrayList<>(List.of(supply1, supply2));

        SupplyDTO supplys = new SupplyDTO();
        supplys.setSupplysDetails(details);
        supplys.setTotalSupplyAmount(BigDecimal.valueOf(130.00));

        service.removeSupply(supplys, "supply-1");

        assertThat(supplys.getSupplysDetails()).hasSize(1);
        assertThat(supplys.getSupplysDetails().get(0).getId()).isEqualTo("supply-2");
        assertThat(supplys.getTotalSupplyAmount()).isEqualByComparingTo("50.00");
    }

    @Test
    @DisplayName("Deve lançar NotFoundException quando suprimento não for encontrado")
    void addSupplys_ShouldThrowNotFoundException_WhenSupplyNotFound() {
        SupplysRequest req = new SupplysRequest("supply-not-found", 1);

        when(supplyService.findById("supply-not-found")).thenThrow(new NotFoundException("Suprimento não encontrado"));

        assertThatThrownBy(() -> service.addSupplys(List.of(req), new ArrayList<>()))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando nenhum suprimento é adicionado")
    void addSupplys_WithNoRequests_ShouldReturnExistingOnly() {
        SupplyDetailDTO existing = new SupplyDetailDTO("supply-1", "Óleo", "Desc", 1, BigDecimal.valueOf(80.00), BigDecimal.valueOf(80.00));

        SupplyDTO result = service.addSupplys(null, new ArrayList<>(List.of(existing)));

        assertThat(result.getSupplysDetails()).hasSize(1);
        assertThat(result.getTotalSupplyAmount()).isEqualByComparingTo("80.00");
    }
}
