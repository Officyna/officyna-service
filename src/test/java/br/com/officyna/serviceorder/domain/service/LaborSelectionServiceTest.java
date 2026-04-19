package br.com.officyna.serviceorder.domain.service;

import br.com.officyna.administrative.labor.api.resources.LaborResponse;
import br.com.officyna.administrative.labor.domain.service.LaborService;
import br.com.officyna.serviceorder.api.resources.IdListRequest;
import br.com.officyna.serviceorder.domain.dto.LaborDetailDTO;
import br.com.officyna.serviceorder.domain.dto.LaborsDTO;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LaborSelectionServiceTest {

    @Mock
    private LaborService laborService;

    @InjectMocks
    private LaborSelectionService service;

    @Test
    @DisplayName("Deve adicionar novos serviços e calcular o valor total acumulado")
    void addLabors_ShouldAddNewLaborsAndCalculateTotal() {
        // Arrange
        IdListRequest req = new IdListRequest();
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
        LaborDetailDTO existing = new LaborDetailDTO("old-1", "Alinhamento", "Desc", BigDecimal.valueOf(100.00), null, null);
        IdListRequest req = new IdListRequest();
        req.setId("new-1");

        when(laborService.findById("new-1")).thenReturn(new LaborResponse("new-1", "Balanceamento", "Desc", BigDecimal.valueOf(80.00), 1, LocalDateTime.now(), LocalDateTime.now(), true));

        LaborsDTO result = service.addLabors(List.of(req), new ArrayList<>(List.of(existing)));

        assertThat(result.getLaborsDetails()).hasSize(2);
        assertThat(result.getTotalLaborsAmount()).isEqualByComparingTo("180.00");
    }

    @Test
    @DisplayName("Deve calcular o valor total como zero quando a lista de serviços for nula")
    void calculateTotalLaborsAmount_ShouldReturnZero_WhenLaborsDetailsIsNull() {
        // Arrange
        LaborsDTO labors = new LaborsDTO();
        labors.setLaborsDetails(null);

        // Act
        service.calculateTotalLaborsAmount(labors);

        // Assert
        assertThat(labors.getTotalLaborsAmount()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("Deve calcular o valor total como zero quando a lista de serviços estiver vazia")
    void calculateTotalLaborsAmount_ShouldReturnZero_WhenLaborsDetailsIsEmpty() {
        // Arrange
        LaborsDTO labors = new LaborsDTO();
        labors.setLaborsDetails(List.of());

        // Act
        service.calculateTotalLaborsAmount(labors);

        // Assert
        assertThat(labors.getTotalLaborsAmount()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("Deve calcular o valor total corretamente com múltiplos serviços")
    void calculateTotalLaborsAmount_ShouldCalculateCorrectTotal() {
        // Arrange
        LaborDetailDTO labor1 = LaborDetailDTO.builder().laborPrice(new BigDecimal("100.50")).build();
        LaborDetailDTO labor2 = LaborDetailDTO.builder().laborPrice(new BigDecimal("200.25")).build();
        LaborDetailDTO labor3 = LaborDetailDTO.builder().laborPrice(new BigDecimal("50.00")).build();

        LaborsDTO labors = new LaborsDTO();
        labors.setLaborsDetails(List.of(labor1, labor2, labor3));

        // Act
        service.calculateTotalLaborsAmount(labors);

        // Assert
        assertThat(labors.getTotalLaborsAmount()).isEqualByComparingTo(new BigDecimal("350.75"));
    }
}
