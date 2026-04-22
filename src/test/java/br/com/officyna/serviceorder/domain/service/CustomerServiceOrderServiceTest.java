package br.com.officyna.serviceorder.domain.service;

import br.com.officyna.administrative.customer.api.resources.CustomerResponse;
import br.com.officyna.serviceorder.api.resources.ServiceOrderResponse;
import br.com.officyna.serviceorder.domain.entity.ServiceOrderEntity;
import br.com.officyna.serviceorder.domain.enums.ServiceOrderStatus;
import br.com.officyna.serviceorder.domain.mapper.ServiceOrderMapper;
import br.com.officyna.serviceorder.repository.ServiceOrderRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
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
        verify(mapper, times(2)).toResponse(any(ServiceOrderEntity.class));
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
        verify(serviceOrderRepository).findByCustomerId(customerId);
        verify(mapper, never()).toResponse(any());
    }
}
