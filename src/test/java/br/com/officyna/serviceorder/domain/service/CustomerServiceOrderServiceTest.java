package br.com.officyna.serviceorder.domain.service;

import br.com.officyna.administrative.customer.api.resources.CustomerResponse;
import br.com.officyna.infrastructure.exception.DomainException;
import br.com.officyna.infrastructure.exception.NotFoundException;
import br.com.officyna.serviceorder.api.resources.ModifySituationRequest;
import br.com.officyna.serviceorder.api.resources.ServiceOrderResponse;
import br.com.officyna.serviceorder.domain.dto.LaborDetailDTO;
import br.com.officyna.serviceorder.domain.dto.LaborsDTO;
import br.com.officyna.serviceorder.domain.entity.ServiceOrderEntity;
import br.com.officyna.serviceorder.domain.enums.LaborSituation;
import br.com.officyna.serviceorder.domain.enums.ServiceOrderStatus;
import br.com.officyna.serviceorder.domain.mapper.ServiceOrderMapper;
import br.com.officyna.serviceorder.repository.ServiceOrderRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceOrderServiceTest {

    @Mock
    private ServiceOrderRepository serviceOrderRepository;

    @Mock
    private CustomerAndMecnichalService customerService;

    @Mock
    private ServiceOrderMapper mapper;

    @Mock
    private ServiceOrderService serviceOrderService;

    @Mock
    private StatusService statusService;

    @InjectMocks
    private CustomerServiceOrderService service;

    @Test
    @DisplayName("Deve retornar todas as ordens do cliente quando o status for nulo")
    void findByCustomerDocument_WhenStatusIsNull_ShouldReturnAllOrders() {
        // Arrange
        String document = "12345678900";
        String customerId = "cust-1";
        CustomerResponse customerResponse = mock(CustomerResponse.class);
        when(customerResponse.id()).thenReturn(customerId);
        when(customerService.getCustomerByDocument(document)).thenReturn(customerResponse);

        ServiceOrderEntity entity1 = ServiceOrderEntity.builder().status(ServiceOrderStatus.RECEBIDA).build();
        ServiceOrderEntity entity2 = ServiceOrderEntity.builder().status(ServiceOrderStatus.EM_EXECUCAO).build();
        when(serviceOrderRepository.findByCustomerId(customerId)).thenReturn(List.of(entity1, entity2));

        ServiceOrderResponse response = mock(ServiceOrderResponse.class);
        when(mapper.toResponse(any(ServiceOrderEntity.class))).thenReturn(response);

        // Act
        List<ServiceOrderResponse> result = service.findByCustomerDocument(document, null);

        // Assert
        assertThat(result).hasSize(2);
        verify(customerService).getCustomerByDocument(document);
        verify(serviceOrderRepository).findByCustomerId(customerId);
    }

    @Test
    @DisplayName("Deve retornar apenas ordens com status específico")
    void findByCustomerDocument_WhenStatusIsProvided_ShouldFilterOrders() {
        // Arrange
        String document = "12345678900";
        String customerId = "cust-1";
        CustomerResponse customerResponse = mock(CustomerResponse.class);
        when(customerResponse.id()).thenReturn(customerId);
        when(customerService.getCustomerByDocument(document)).thenReturn(customerResponse);

        ServiceOrderEntity entity1 = ServiceOrderEntity.builder().status(ServiceOrderStatus.RECEBIDA).build();
        ServiceOrderEntity entity2 = ServiceOrderEntity.builder().status(ServiceOrderStatus.EM_EXECUCAO).build();
        when(serviceOrderRepository.findByCustomerId(customerId)).thenReturn(List.of(entity1, entity2));

        ServiceOrderResponse response = mock(ServiceOrderResponse.class);
        when(mapper.toResponse(any(ServiceOrderEntity.class))).thenReturn(response);

        // Act
        List<ServiceOrderResponse> result = service.findByCustomerDocument(document, ServiceOrderStatus.EM_EXECUCAO);

        // Assert
        assertThat(result).hasSize(1);
        verify(mapper, times(1)).toResponse(entity2);
        verify(mapper, never()).toResponse(entity1);
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando o cliente não possui ordens")
    void findByCustomerDocument_WhenNoOrdersFound_ShouldReturnEmptyList() {
        // Arrange
        String document = "12345678900";
        String customerId = "cust-1";
        CustomerResponse customerResponse = mock(CustomerResponse.class);
        when(customerResponse.id()).thenReturn(customerId);
        when(customerService.getCustomerByDocument(document)).thenReturn(customerResponse);

        when(serviceOrderRepository.findByCustomerId(customerId)).thenReturn(List.of());

        // Act
        List<ServiceOrderResponse> result = service.findByCustomerDocument(document, null);

        // Assert
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Deve atualizar a situação dos serviços com sucesso")
    void updateLaborSituation_ShouldSucceed() {
        // Arrange
        String orderId = "order-1";
        LaborDetailDTO labor1 = LaborDetailDTO.builder().laborId("l1").situation(LaborSituation.PENDENTE).build();
        LaborDetailDTO labor2 = LaborDetailDTO.builder().laborId("l2").situation(LaborSituation.PENDENTE).build();
        
        ServiceOrderEntity entity = ServiceOrderEntity.builder()
                .id(orderId)
                .status(ServiceOrderStatus.AGUARDANDO_APROVACAO)
                .labors(LaborsDTO.builder().laborsDetails(new ArrayList<>(List.of(labor1, labor2))).build())
                .build();

        List<ModifySituationRequest> request = List.of(
                new ModifySituationRequest("l1", LaborSituation.APROVADO),
                new ModifySituationRequest("l2", LaborSituation.REJEITADO)
        );

        when(serviceOrderRepository.findById(orderId)).thenReturn(Optional.of(entity));
        when(serviceOrderService.save(any())).thenReturn(entity);
        when(mapper.toResponse(any())).thenReturn(mock(ServiceOrderResponse.class));

        // Act
        service.updateLaborSituation(orderId, request);

        // Assert
        assertThat(labor1.getSituation()).isEqualTo(LaborSituation.APROVADO);
        assertThat(labor1.getSituationDate()).isNotNull();
        assertThat(labor2.getSituation()).isEqualTo(LaborSituation.REJEITADO);
        assertThat(labor2.getSituationDate()).isNotNull();
        
        verify(statusService).updateStatus(entity, ServiceOrderStatus.APROVADA);
        verify(serviceOrderService).save(entity);
    }

    @Test
    @DisplayName("Deve lançar exceção se a O.S não estiver em AGUARDANDO APROVACAO")
    void updateLaborSituation_InvalidStatus_ShouldThrowException() {
        // Arrange
        String orderId = "order-1";
        ServiceOrderEntity entity = ServiceOrderEntity.builder()
                .status(ServiceOrderStatus.RECEBIDA)
                .build();

        when(serviceOrderRepository.findById(orderId)).thenReturn(Optional.of(entity));

        // Act & Assert
        assertThatThrownBy(() -> service.updateLaborSituation(orderId, List.of()))
                .isInstanceOf(DomainException.class)
                .hasMessage("Só é possivel atualizar a situação de um serviço para O.S AGUARDANDO APROVAÇÃO");
        
        verify(statusService, never()).updateStatus(any(), any());
    }

    @Test
    @DisplayName("Deve lançar exceção se a O.S não for encontrada")
    void updateLaborSituation_OrderNotFound_ShouldThrowException() {
        String orderId = "not-found";
        when(serviceOrderRepository.findById(orderId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.updateLaborSituation(orderId, List.of()))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    @DisplayName("Deve filtrar múltiplos status e retornar apenas os solicitados")
    void findByCustomerDocument_WithMultipleStatusFilter_ShouldReturnFiltered() {
        String document = "12345678900";
        String customerId = "cust-1";
        CustomerResponse customerResponse = mock(CustomerResponse.class);
        when(customerResponse.id()).thenReturn(customerId);
        when(customerService.getCustomerByDocument(document)).thenReturn(customerResponse);

        ServiceOrderEntity entity1 = ServiceOrderEntity.builder().status(ServiceOrderStatus.RECEBIDA).build();
        ServiceOrderEntity entity2 = ServiceOrderEntity.builder().status(ServiceOrderStatus.EM_EXECUCAO).build();
        ServiceOrderEntity entity3 = ServiceOrderEntity.builder().status(ServiceOrderStatus.FINALIZADA).build();
        when(serviceOrderRepository.findByCustomerId(customerId)).thenReturn(List.of(entity1, entity2, entity3));

        ServiceOrderResponse response = mock(ServiceOrderResponse.class);
        when(mapper.toResponse(any())).thenReturn(response);

        List<ServiceOrderResponse> result = service.findByCustomerDocument(document, ServiceOrderStatus.FINALIZADA);

        assertThat(result).hasSize(1);
        verify(mapper, times(1)).toResponse(entity3);
    }

    @Test
    @DisplayName("Deve atualizar múltiplos serviços com situações diferentes")
    void updateLaborSituation_WithMultipleDifferentSituations_ShouldUpdateAll() {
        String orderId = "order-1";
        LaborDetailDTO labor1 = LaborDetailDTO.builder().laborId("l1").situation(LaborSituation.PENDENTE).build();
        LaborDetailDTO labor2 = LaborDetailDTO.builder().laborId("l2").situation(LaborSituation.PENDENTE).build();
        LaborDetailDTO labor3 = LaborDetailDTO.builder().laborId("l3").situation(LaborSituation.PENDENTE).build();

        ServiceOrderEntity entity = ServiceOrderEntity.builder()
                .id(orderId)
                .status(ServiceOrderStatus.AGUARDANDO_APROVACAO)
                .labors(LaborsDTO.builder().laborsDetails(new ArrayList<>(List.of(labor1, labor2, labor3))).build())
                .build();

        List<ModifySituationRequest> request = List.of(
                new ModifySituationRequest("l1", LaborSituation.APROVADO),
                new ModifySituationRequest("l2", LaborSituation.APROVADO),
                new ModifySituationRequest("l3", LaborSituation.REJEITADO)
        );

        when(serviceOrderRepository.findById(orderId)).thenReturn(Optional.of(entity));
        when(serviceOrderService.save(any())).thenReturn(entity);
        when(mapper.toResponse(any())).thenReturn(mock(ServiceOrderResponse.class));

        service.updateLaborSituation(orderId, request);

        assertThat(labor1.getSituation()).isEqualTo(LaborSituation.APROVADO);
        assertThat(labor2.getSituation()).isEqualTo(LaborSituation.APROVADO);
        assertThat(labor3.getSituation()).isEqualTo(LaborSituation.REJEITADO);

        verify(statusService).updateStatus(entity, ServiceOrderStatus.APROVADA);
        verify(serviceOrderService).save(entity);
    }

    @Test
    @DisplayName("Deve retornar lista com um único pedido quando filtrado por status específico")
    void findByCustomerDocument_WithSpecificStatus_ShouldReturnOne() {
        String document = "99999999999";
        String customerId = "cust-2";
        CustomerResponse customerResponse = mock(CustomerResponse.class);
        when(customerResponse.id()).thenReturn(customerId);
        when(customerService.getCustomerByDocument(document)).thenReturn(customerResponse);

        ServiceOrderEntity entity = ServiceOrderEntity.builder().status(ServiceOrderStatus.ENTREGUE).build();
        when(serviceOrderRepository.findByCustomerId(customerId)).thenReturn(List.of(entity));

        ServiceOrderResponse response = mock(ServiceOrderResponse.class);
        when(mapper.toResponse(entity)).thenReturn(response);

        List<ServiceOrderResponse> result = service.findByCustomerDocument(document, ServiceOrderStatus.ENTREGUE);

        assertThat(result).hasSize(1);
        verify(mapper).toResponse(entity);
    }

    @Test
    @DisplayName("Deve lançar exceção quando tentar atualizar labor inexistente")
    void updateLaborSituation_WithNonExistentLaborId_ShouldNotUpdate() {
        String orderId = "order-1";
        LaborDetailDTO labor1 = LaborDetailDTO.builder().laborId("l1").situation(LaborSituation.PENDENTE).build();

        ServiceOrderEntity entity = ServiceOrderEntity.builder()
                .id(orderId)
                .status(ServiceOrderStatus.AGUARDANDO_APROVACAO)
                .labors(LaborsDTO.builder().laborsDetails(new ArrayList<>(List.of(labor1))).build())
                .build();

        List<ModifySituationRequest> request = List.of(
                new ModifySituationRequest("l999", LaborSituation.APROVADO)
        );

        when(serviceOrderRepository.findById(orderId)).thenReturn(Optional.of(entity));
        when(serviceOrderService.save(any())).thenReturn(entity);
        when(mapper.toResponse(any())).thenReturn(mock(ServiceOrderResponse.class));

        service.updateLaborSituation(orderId, request);

        // l1 não foi alterado
        assertThat(labor1.getSituation()).isEqualTo(LaborSituation.PENDENTE);
        verify(serviceOrderService).save(entity);
    }

    @Test
    @DisplayName("Deve registrar a data da situação ao atualizar labor")
    void updateLaborSituation_ShouldSetSituationDate() {
        String orderId = "order-1";
        LaborDetailDTO labor = LaborDetailDTO.builder().laborId("l1").situation(LaborSituation.PENDENTE).build();

        ServiceOrderEntity entity = ServiceOrderEntity.builder()
                .id(orderId)
                .status(ServiceOrderStatus.AGUARDANDO_APROVACAO)
                .labors(LaborsDTO.builder().laborsDetails(new ArrayList<>(List.of(labor))).build())
                .build();

        List<ModifySituationRequest> request = List.of(
                new ModifySituationRequest("l1", LaborSituation.APROVADO)
        );

        when(serviceOrderRepository.findById(orderId)).thenReturn(Optional.of(entity));
        when(serviceOrderService.save(any())).thenReturn(entity);
        when(mapper.toResponse(any())).thenReturn(mock(ServiceOrderResponse.class));

        service.updateLaborSituation(orderId, request);

        assertThat(labor.getSituationDate()).isNotNull();
    }
}
