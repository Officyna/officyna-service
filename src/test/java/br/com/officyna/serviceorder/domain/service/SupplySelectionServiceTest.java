package br.com.officyna.serviceorder.domain.service;

import br.com.officyna.administrative.supply.api.resources.SupplyResponse;
import br.com.officyna.administrative.supply.domain.SupplyType;
import br.com.officyna.administrative.supply.domain.service.SupplyService;
import br.com.officyna.infrastructure.exception.DomainException;
import br.com.officyna.serviceorder.api.resources.SupplysRequest;
import br.com.officyna.serviceorder.domain.dto.SupplyDTO;
import br.com.officyna.serviceorder.domain.dto.SupplyDetailDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
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
}
