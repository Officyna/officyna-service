package br.com.officyna.serviceorder.domain.service;

import br.com.officyna.infrastructure.exception.DomainException;
import br.com.officyna.serviceorder.domain.dto.LaborDetailDTO;
import br.com.officyna.serviceorder.domain.dto.LaborsDTO;
import br.com.officyna.serviceorder.domain.entity.ServiceOrderEntity;
import br.com.officyna.serviceorder.domain.enums.ServiceOrderStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class StatusServiceTest {

    private StatusService service;

    @BeforeEach
    void setUp() {
        service = new StatusService();
    }

    @Test
    @DisplayName("Deve permitir transição de RECEBIDA para EM_DIAGNOSTICO e setar a data")
    void updateStatus_FromRecebidaToDiagnostico_ShouldSucceed() {
        ServiceOrderEntity entity = ServiceOrderEntity.builder().status(ServiceOrderStatus.RECEBIDA).build();
        
        service.updateStatus(entity, ServiceOrderStatus.EM_DIAGNOSTICO);

        assertThat(entity.getStatus()).isEqualTo(ServiceOrderStatus.EM_DIAGNOSTICO);
        assertThat(entity.getDiagnosisStartDate()).isNotNull();
    }

    @Test
    @DisplayName("Deve falhar ao tentar transição inválida (ex: RECEBIDA para APROVADA)")
    void updateStatus_InvalidTransition_ShouldThrowException() {
        ServiceOrderEntity entity = ServiceOrderEntity.builder().status(ServiceOrderStatus.RECEBIDA).build();

        assertThatThrownBy(() -> service.updateStatus(entity, ServiceOrderStatus.APROVADA))
                .isInstanceOf(DomainException.class)
                .hasMessage("Apenas ordens AGUARDANDO APROVAÇÃO podem ser aprovadas.");
    }

    @Test
    @DisplayName("Deve permitir finalizar ordem quando todos os serviços estiverem concluídos")
    void updateStatus_ToFinalizada_WithCompletedLabors_ShouldSucceed() {
        LaborDetailDTO labor = LaborDetailDTO.builder()
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now())
                .build();
        
        ServiceOrderEntity entity = ServiceOrderEntity.builder()
                .status(ServiceOrderStatus.EM_EXECUCAO)
                .labors(LaborsDTO.builder().laborsDetails(List.of(labor)).build())
                .build();

        service.updateStatus(entity, ServiceOrderStatus.FINALIZADA);

        assertThat(entity.getStatus()).isEqualTo(ServiceOrderStatus.FINALIZADA);
        assertThat(entity.getFinalizationDate()).isNotNull();
    }

    @Test
    @DisplayName("Deve falhar ao finalizar ordem com serviços não iniciados ou não finalizados")
    void updateStatus_ToFinalizada_WithIncompleteLabors_ShouldThrowException() {
        LaborDetailDTO labor = LaborDetailDTO.builder()
                .startDate(LocalDateTime.now())
                .endDate(null) // Não finalizado
                .build();

        ServiceOrderEntity entity = ServiceOrderEntity.builder()
                .status(ServiceOrderStatus.EM_EXECUCAO)
                .labors(LaborsDTO.builder().laborsDetails(List.of(labor)).build())
                .build();

        assertThatThrownBy(() -> service.updateStatus(entity, ServiceOrderStatus.FINALIZADA))
                .isInstanceOf(DomainException.class)
                .hasMessage("Não é possível finalizar ordem com serviços em aberto");
    }

    @ParameterizedTest
    @CsvSource({
        "RECEBIDA, EM_DIAGNOSTICO",
        "EM_DIAGNOSTICO, AGUARDANDO_APROVACAO",
        "AGUARDANDO_APROVACAO, APROVADA",
        "AGUARDANDO_APROVACAO, RECUSADA",
        "APROVADA, EM_EXECUCAO",
        "FINALIZADA, ENTREGUE"
    })
    @DisplayName("Deve validar transições de sucesso parametrizadas")
    void updateStatus_ValidTransitions_ShouldSucceed(ServiceOrderStatus current, ServiceOrderStatus next) {
        ServiceOrderEntity entity = ServiceOrderEntity.builder().status(current).build();
        
        // Mocking labors for FINALIZADA transition if needed is not directly possible with CsvSource easily
        // but for these generic transitions it works
        if (next == ServiceOrderStatus.FINALIZADA) return; 

        service.updateStatus(entity, next);
        assertThat(entity.getStatus()).isEqualTo(next);
    }

    @Test
    @DisplayName("validateStatusForStartExecution deve falhar se não houver serviços")
    void validateStatusForStartExecution_NoLabors_ShouldThrowException() {
        ServiceOrderEntity entity = ServiceOrderEntity.builder()
                .status(ServiceOrderStatus.APROVADA)
                .labors(null)
                .build();

        assertThatThrownBy(() -> service.validateStatusForStartExecution(entity))
                .isInstanceOf(DomainException.class)
                .hasMessage("A ordem de serviço não possui serviços cadastrados.");
    }

    @Test
    @DisplayName("validateStatusForStartExecution deve falhar se status for inválido")
    void validateStatusForStartExecution_InvalidStatus_ShouldThrowException() {
        ServiceOrderEntity entity = ServiceOrderEntity.builder()
                .status(ServiceOrderStatus.RECEBIDA)
                .build();

        assertThatThrownBy(() -> service.validateStatusForStartExecution(entity))
                .isInstanceOf(DomainException.class)
                .hasMessageStartingWith("Um serviço só pode ser iniciado ou finalizado");
    }
}
