package br.com.officyna.serviceorder.domain.mapper;

import br.com.officyna.serviceorder.api.resources.ServiceOrderResponse;
import br.com.officyna.serviceorder.domain.entity.ServiceOrderEntity;
import br.com.officyna.serviceorder.domain.enums.ServiceOrderStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class ServiceOrderMapperTest {

    private final ServiceOrderMapper mapper = new ServiceOrderMapper();

    @Test
    @DisplayName("Deve retornar a data de diagnóstico quando o status for EM_DIAGNOSTICO")
    void toResponse_ShouldReturnDiagnosisDate_WhenStatusIsDiagnosis() {
        // Arrange
        LocalDateTime now = LocalDateTime.of(2023, 10, 27, 10, 0);
        ServiceOrderEntity entity = ServiceOrderEntity.builder()
                .id("1")
                .serviceOrderNumber(100L)
                .status(ServiceOrderStatus.EM_DIAGNOSTICO)
                .DiagnosisStartDate(now)
                .totalBudgetAmount(BigDecimal.valueOf(150.50))
                .createdAt(now)
                .build();

        // Act
        ServiceOrderResponse response = mapper.toResponse(entity);

        // Assert
        assertThat(response.statusDate()).isEqualTo("27/10/2023 10:00");
    }

    @Test
    @DisplayName("Deve formatar o valor monetário corretamente")
    void toResponse_ShouldFormatMoney() {
        ServiceOrderEntity entity = new ServiceOrderEntity();
        entity.setServiceOrderNumber(1L);
        entity.setStatus(ServiceOrderStatus.RECEBIDA);
        entity.setTotalBudgetAmount(new BigDecimal("1234.5"));

        ServiceOrderResponse response = mapper.toResponse(entity);

        assertThat(response.totalBudgetAmount()).isEqualTo("R$ 1234,50");
    }
}