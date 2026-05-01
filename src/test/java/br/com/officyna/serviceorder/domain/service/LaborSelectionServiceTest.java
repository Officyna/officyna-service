package br.com.officyna.serviceorder.domain.service;

import br.com.officyna.administrative.labor.api.resources.LaborResponse;
import br.com.officyna.administrative.labor.domain.service.LaborService;
import br.com.officyna.infrastructure.exception.NotFoundException;
import br.com.officyna.serviceorder.api.resources.LaborsRequest;
import br.com.officyna.serviceorder.domain.dto.LaborDetailDTO;
import br.com.officyna.serviceorder.domain.dto.LaborsDTO;
import br.com.officyna.serviceorder.domain.enums.LaborSituation;
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
class LaborSelectionServiceTest {

    @Mock
    private LaborService laborService;

    @Spy
    private BudgetService budgetService;

    @InjectMocks
    private LaborSelectionService service;

    @Test
    @DisplayName("Deve adicionar novos serviços e calcular o valor total acumulado")
    void addLabors_ShouldAddNewLaborsAndCalculateTotal() {
        // Arrange
        LaborsRequest req = new LaborsRequest();
        req.setId("labor-1");

        LaborResponse response = new LaborResponse("labor-1", "Troca de Óleo e Filtro", "Substituição de óleo sintético 5W30 e filtro de óleo original.", BigDecimal.valueOf(150.00), 1, LocalDateTime.now(), LocalDateTime.now(), true);
        when(laborService.findById("labor-1")).thenReturn(response);

        // Act
        LaborsDTO result = service.addLabors(List.of(req), new ArrayList<>());

        // Assert
        assertThat(result.getLaborsDetails()).hasSize(1);
        assertThat(result.getTotalLaborsAmount()).isEqualByComparingTo("150.00");
    }

    @Test
    @DisplayName("Deve manter a lista de serviços existentes ao adicionar novos")
    void addLabors_ShouldMaintainExistingLabors() {
        LaborDetailDTO existing = new LaborDetailDTO("old-1", "Alinhamento", "Desc", BigDecimal.valueOf(100.00), null, null, LaborSituation.PENDENTE, LocalDateTime.now());
        LaborsRequest req = new LaborsRequest();
        req.setId("new-1");

        when(laborService.findById("new-1")).thenReturn(new LaborResponse("new-1", "Balanceamento", "Desc", BigDecimal.valueOf(80.00), 1, LocalDateTime.now(), LocalDateTime.now(), true));

        LaborsDTO result = service.addLabors(List.of(req), new ArrayList<>(List.of(existing)));

        assertThat(result.getLaborsDetails()).hasSize(2);
        assertThat(result.getTotalLaborsAmount()).isEqualByComparingTo("180.00");
    }

    @Test
    @DisplayName("Deve adicionar múltiplos serviços com cálculo correto")
    void addLabors_ShouldAddMultipleLaborsAndCalculateTotal() {
        LaborsRequest req1 = new LaborsRequest();
        req1.setId("labor-1");
        LaborsRequest req2 = new LaborsRequest();
        req2.setId("labor-2");

        when(laborService.findById("labor-1")).thenReturn(new LaborResponse("labor-1", "Serviço 1", "Desc", BigDecimal.valueOf(100.00), 1, LocalDateTime.now(), LocalDateTime.now(), true));
        when(laborService.findById("labor-2")).thenReturn(new LaborResponse("labor-2", "Serviço 2", "Desc", BigDecimal.valueOf(200.00), 1, LocalDateTime.now(), LocalDateTime.now(), true));

        LaborsDTO result = service.addLabors(List.of(req1, req2), new ArrayList<>());

        assertThat(result.getLaborsDetails()).hasSize(2);
        assertThat(result.getTotalLaborsAmount()).isEqualByComparingTo("300.00");
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando nenhum labor é passado")
    void addLabors_WithNoLaborsRequest_ShouldReturnExistingOnly() {
        LaborDetailDTO existing = new LaborDetailDTO("old-1", "Serviço", "Desc", BigDecimal.valueOf(50.00), null, null, LaborSituation.PENDENTE, LocalDateTime.now());

        LaborsDTO result = service.addLabors(null, new ArrayList<>(List.of(existing)));

        assertThat(result.getLaborsDetails()).hasSize(1);
        assertThat(result.getTotalLaborsAmount()).isEqualByComparingTo("50.00");
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando nenhum labor existente e nenhuma requisição")
    void addLabors_WithNoLaborsRequestAndNoExisting_ShouldReturnEmpty() {
        LaborsDTO result = service.addLabors(null, null);

        assertThat(result.getLaborsDetails()).isEmpty();
        assertThat(result.getTotalLaborsAmount()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("Deve manter status PENDENTE para novos serviços")
    void addLabors_ShouldSetStatusPendingForNewLabors() {
        LaborsRequest req = new LaborsRequest();
        req.setId("labor-1");

        when(laborService.findById("labor-1")).thenReturn(new LaborResponse("labor-1", "Serviço", "Desc", BigDecimal.valueOf(100.00), 1, LocalDateTime.now(), LocalDateTime.now(), true));

        LaborsDTO result = service.addLabors(List.of(req), new ArrayList<>());

        assertThat(result.getLaborsDetails()).hasSize(1);
        assertThat(result.getLaborsDetails().get(0).getSituation()).isEqualTo(LaborSituation.PENDENTE);
    }

    @Test
    @DisplayName("Deve lançar exceção quando labor não for encontrado")
    void addLabors_ShouldThrowNotFoundException_WhenLaborNotFound() {
        LaborsRequest req = new LaborsRequest();
        req.setId("labor-not-found");

        when(laborService.findById("labor-not-found")).thenThrow(new NotFoundException("Labor não encontrado"));

        assertThatThrownBy(() -> service.addLabors(List.of(req), new ArrayList<>()))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Labor não encontrado");
    }

    @Test
    @DisplayName("Deve somar corretamente múltiplos serviços ignorando rejeitados quando calcular total")
    void addLabors_ShouldCalculateTotalIgnoringRejected() {
        LaborDetailDTO rejected = new LaborDetailDTO("r-1", "Rejeitado", "Desc", BigDecimal.valueOf(50.00), null, null, LaborSituation.REJEITADO, LocalDateTime.now());
        LaborsRequest req = new LaborsRequest();
        req.setId("new-1");

        when(laborService.findById("new-1")).thenReturn(new LaborResponse("new-1", "Aprovado", "Desc", BigDecimal.valueOf(100.00), 1, LocalDateTime.now(), LocalDateTime.now(), true));

        LaborsDTO result = service.addLabors(List.of(req), new ArrayList<>(List.of(rejected)));

        assertThat(result.getLaborsDetails()).hasSize(2);
        // Total deve ser 100 pois rejeitado é ignorado no cálculo
        assertThat(result.getTotalLaborsAmount()).isEqualByComparingTo("100.00");
    }
}
