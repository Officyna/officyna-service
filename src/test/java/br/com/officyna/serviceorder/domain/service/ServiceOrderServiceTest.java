package br.com.officyna.serviceorder.domain.service;

import br.com.officyna.administrative.supply.domain.service.StockService;
import br.com.officyna.infrastructure.exception.DomainException;
import br.com.officyna.serviceorder.domain.entity.ServiceOrderEntity;
import br.com.officyna.serviceorder.domain.enums.ServiceOrderStatus;
import br.com.officyna.serviceorder.domain.mapper.ServiceOrderMapper;
import br.com.officyna.serviceorder.repository.ServiceOrderRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServiceOrderServiceTest {

    @Mock
    private ServiceOrderRepository repository;

    @Mock
    private ServiceOrderMapper mapper;

    @Mock
    private BudgetService budgetService;

    @Spy
    private StatusService statusService = new StatusService(mock(StockService.class));

    @InjectMocks
    private ServiceOrderService service;

    @Test
    @DisplayName("Deve atualizar status com sucesso quando a precedência for respeitada")
    void updateStatus_ShouldSuccess_WhenTransitionIsValid() {
        // Arrange
        String id = "123";
        ServiceOrderEntity entity = new ServiceOrderEntity();
        entity.setStatus(ServiceOrderStatus.RECEBIDA);

        when(repository.findById(id)).thenReturn(Optional.of(entity));
        when(repository.save(any())).thenReturn(entity);

        // Act
        service.updateStatus(id, ServiceOrderStatus.EM_DIAGNOSTICO);

        // Assert
        assertThat(entity.getStatus()).isEqualTo(ServiceOrderStatus.EM_DIAGNOSTICO);
        verify(repository).save(entity);
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar pular um status na precedência")
    void updateStatus_ShouldThrowException_WhenTransitionIsInvalid() {
        // Arrange
        String id = "123";
        ServiceOrderEntity entity = new ServiceOrderEntity();
        entity.setStatus(ServiceOrderStatus.RECEBIDA);

        when(repository.findById(id)).thenReturn(Optional.of(entity));

        // Act & Assert
        assertThatThrownBy(() -> service.updateStatus(id, ServiceOrderStatus.APROVADA))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("Apenas ordens AGUARDANDO APROVAÇÃO podem ser aprovadas.");
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar retornar para o status RECEBIDA")
    void updateStatus_ShouldThrowException_WhenReturningToReceived() {
        // Arrange
        String id = "123";
        ServiceOrderEntity entity = new ServiceOrderEntity();
        entity.setStatus(ServiceOrderStatus.EM_DIAGNOSTICO);

        when(repository.findById(id)).thenReturn(Optional.of(entity));

        // Act & Assert
        assertThatThrownBy(() -> service.updateStatus(id, ServiceOrderStatus.RECEBIDA))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("A Ordem de Serviço já foi recebida e não pode retornar a este status.");
    }

    @Test
    @DisplayName("Deve lançar exceção se o novo status for igual ao atual")
    void updateStatus_ShouldThrowException_WhenStatusIsSame() {
        // Arrange
        String id = "123";
        ServiceOrderEntity entity = new ServiceOrderEntity();
        entity.setStatus(ServiceOrderStatus.EM_DIAGNOSTICO);

        when(repository.findById(id)).thenReturn(Optional.of(entity));

        // Act & Assert
        assertThatThrownBy(() -> service.updateStatus(id, ServiceOrderStatus.EM_DIAGNOSTICO))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("A Ordem de Serviço já foi processada com status " + ServiceOrderStatus.EM_DIAGNOSTICO.getStatusName() + ".");
    }

    @Test
    @DisplayName("Deve validar transição para ENTREGUE apenas após FINALIZADA")
    void updateStatus_ShouldValidateDelivery_OnlyAfterFinalized() {
        String id = "123";
        ServiceOrderEntity entity = new ServiceOrderEntity();
        entity.setStatus(ServiceOrderStatus.EM_EXECUCAO);

        when(repository.findById(id)).thenReturn(Optional.of(entity));

        assertThatThrownBy(() -> service.updateStatus(id, ServiceOrderStatus.ENTREGUE))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("Apenas ordes FINALIZADAS podem ser consideradas entregues");
    }
}