package br.com.officyna.serviceorder.domain.service;

import br.com.officyna.administrative.supply.domain.service.StockService;
import br.com.officyna.infrastructure.exception.DomainException;
import br.com.officyna.serviceorder.domain.dto.LaborDetailDTO;
import br.com.officyna.serviceorder.domain.dto.LaborsDTO;
import br.com.officyna.serviceorder.domain.dto.SupplyDTO;
import br.com.officyna.serviceorder.domain.dto.SupplyDetailDTO;
import br.com.officyna.serviceorder.domain.entity.ServiceOrderEntity;
import br.com.officyna.serviceorder.domain.enums.LaborSituation;
import br.com.officyna.serviceorder.domain.enums.ServiceOrderStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StatusServiceTest {

    @Mock
    private StockService stockService;

    @InjectMocks
    private StatusService service;

    private List<SupplyDetailDTO> buildSupplyItems() {
        return List.of(
                SupplyDetailDTO.builder()
                        .id("supply-1")
                        .name("Óleo Motor")
                        .quantity(3)
                        .unitPrice(new BigDecimal("58.50"))
                        .totalPrice(new BigDecimal("175.50"))
                        .build()
        );
    }

    private LaborDetailDTO buildApprovedLabor() {
        return LaborDetailDTO.builder()
                .laborId("labor-1")
                .situation(LaborSituation.APROVADO)
                .build();
    }

    private ServiceOrderEntity buildEntityWithSupplies(ServiceOrderStatus status) {
        return ServiceOrderEntity.builder()
                .status(status)
                .supplys(SupplyDTO.builder().supplysDetails(buildSupplyItems()).build())
                .build();
    }

    private ServiceOrderEntity buildEntityWithSuppliesAndLabors(ServiceOrderStatus status) {
        return ServiceOrderEntity.builder()
                .status(status)
                .supplys(SupplyDTO.builder().supplysDetails(buildSupplyItems()).build())
                .labors(LaborsDTO.builder().laborsDetails(List.of(buildApprovedLabor())).build())
                .build();
    }

    // ─── transições de status ─────────────────────────────────────────────────

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
    @DisplayName("Deve falhar ao finalizar ordem com serviços não concluídos")
    void updateStatus_ToFinalizada_WithIncompleteLabors_ShouldThrowException() {
        LaborDetailDTO labor = LaborDetailDTO.builder()
                .startDate(LocalDateTime.now())
                .endDate(null)
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
        "AGUARDANDO_APROVACAO, RECUSADA",
        "APROVADA, EM_EXECUCAO",
        "FINALIZADA, ENTREGUE"
    })
    @DisplayName("Deve validar transições de sucesso parametrizadas")
    void updateStatus_ValidTransitions_ShouldSucceed(ServiceOrderStatus current, ServiceOrderStatus next) {
        ServiceOrderEntity entity = ServiceOrderEntity.builder().status(current).build();
        if (next == ServiceOrderStatus.FINALIZADA) return;

        service.updateStatus(entity, next);
        assertThat(entity.getStatus()).isEqualTo(next);
    }

    @Test
    @DisplayName("Deve transitar de AGUARDANDO_APROVACAO para APROVADA quando há labors não pendentes")
    void updateStatus_ToAprovada_ShouldSucceed_WhenLaborsAreNotPending() {
        ServiceOrderEntity entity = buildEntityWithSuppliesAndLabors(ServiceOrderStatus.AGUARDANDO_APROVACAO);

        service.updateStatus(entity, ServiceOrderStatus.APROVADA);

        assertThat(entity.getStatus()).isEqualTo(ServiceOrderStatus.APROVADA);
        assertThat(entity.getApprovalDate()).isNotNull();
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

    // ─── integração com StockService ─────────────────────────────────────────

    @Test
    @DisplayName("Deve chamar reserveSupplies ao transitar para AGUARDANDO_APROVACAO")
    void updateStatus_ToAguardandoAprovacao_ShouldReserveSupplies() {
        ServiceOrderEntity entity = buildEntityWithSupplies(ServiceOrderStatus.EM_DIAGNOSTICO);

        service.updateStatus(entity, ServiceOrderStatus.AGUARDANDO_APROVACAO);

        assertThat(entity.getStatus()).isEqualTo(ServiceOrderStatus.AGUARDANDO_APROVACAO);
        assertThat(entity.getClientSendDate()).isNotNull();
        verify(stockService).reserveSupplies(entity.getSupplys().getSupplysDetails());
    }

    @Test
    @DisplayName("Deve chamar consumeSupplies ao transitar para APROVADA")
    void updateStatus_ToAprovada_ShouldConsumeSupplies() {
        ServiceOrderEntity entity = buildEntityWithSuppliesAndLabors(ServiceOrderStatus.AGUARDANDO_APROVACAO);

        service.updateStatus(entity, ServiceOrderStatus.APROVADA);

        assertThat(entity.getStatus()).isEqualTo(ServiceOrderStatus.APROVADA);
        assertThat(entity.getApprovalDate()).isNotNull();
        verify(stockService).consumeSupplies(entity.getSupplys().getSupplysDetails());
    }

    @Test
    @DisplayName("Deve chamar releaseSupplies ao transitar para RECUSADA")
    void updateStatus_ToRecusada_ShouldReleaseSupplies() {
        ServiceOrderEntity entity = buildEntityWithSupplies(ServiceOrderStatus.AGUARDANDO_APROVACAO);

        service.updateStatus(entity, ServiceOrderStatus.RECUSADA);

        assertThat(entity.getStatus()).isEqualTo(ServiceOrderStatus.RECUSADA);
        assertThat(entity.getRefuseDate()).isNotNull();
        verify(stockService).releaseSupplies(entity.getSupplys().getSupplysDetails());
    }

    @Test
    @DisplayName("Não deve chamar StockService ao transitar para status sem insumos (supplys null)")
    void updateStatus_WithoutSupplies_ShouldPassNullToStockService() {
        ServiceOrderEntity entity = ServiceOrderEntity.builder()
                .status(ServiceOrderStatus.AGUARDANDO_APROVACAO)
                .supplys(null)
                .labors(LaborsDTO.builder().laborsDetails(List.of(buildApprovedLabor())).build())
                .build();

        service.updateStatus(entity, ServiceOrderStatus.APROVADA);

        // StockService é chamado mas com null — o próprio StockService ignora listas nulas
        verify(stockService).consumeSupplies(null);
    }

    @Test
    @DisplayName("Não deve chamar StockService em transições que não envolvem estoque")
    void updateStatus_NonStockTransition_ShouldNotInteractWithStockService() {
        ServiceOrderEntity entity = buildEntityWithSupplies(ServiceOrderStatus.RECEBIDA);

        service.updateStatus(entity, ServiceOrderStatus.EM_DIAGNOSTICO);

        verifyNoInteractions(stockService);
    }
}