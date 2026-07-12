package br.com.officyna.serviceorder.domain.controller;

import br.com.officyna.serviceorder.api.resources.NewServiceOrderRequest;
import br.com.officyna.serviceorder.api.resources.SendToCustomerResponse;
import br.com.officyna.serviceorder.api.resources.ServiceOrderResponse;
import br.com.officyna.serviceorder.domain.entity.ServiceOrder;
import br.com.officyna.serviceorder.domain.enums.ServiceOrderStatus;
import br.com.officyna.serviceorder.domain.presenter.ServiceOrderPresenter;
import br.com.officyna.serviceorder.domain.service.ServiceOrderService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServiceOrderControllerAdapterTest {

    @Mock
    private ServiceOrderService service;

    @Mock
    private ServiceOrderPresenter presenter;

    @InjectMocks
    private ServiceOrderControllerAdapter adapter;

    @Test
    @DisplayName("findAll deve apresentar cada ordem retornada pelo use case")
    void findAll_ShouldPresentEach() {
        ServiceOrder entity = mock(ServiceOrder.class);
        ServiceOrderResponse response = mock(ServiceOrderResponse.class);

        when(service.findAll()).thenReturn(List.of(entity));
        when(presenter.toResponse(entity)).thenReturn(response);

        List<ServiceOrderResponse> result = adapter.findAll();

        assertEquals(1, result.size());
        assertSame(response, result.get(0));
    }

    @Test
    @DisplayName("createServiceOrder deve chamar o use case e apresentar o resultado")
    void createServiceOrder_ShouldInvokeUseCaseAndPresent() {
        NewServiceOrderRequest request = NewServiceOrderRequest.builder().customerId("c").vehicleId("v").build();
        ServiceOrder created = mock(ServiceOrder.class);
        ServiceOrderResponse response = mock(ServiceOrderResponse.class);

        when(service.createServiceOrder(request)).thenReturn(created);
        when(presenter.toResponse(created)).thenReturn(response);

        assertSame(response, adapter.createServiceOrder(request));
    }

    @Test
    @DisplayName("updateStatus deve chamar o use case e apresentar o resultado")
    void updateStatus_ShouldInvokeUseCaseAndPresent() {
        ServiceOrder updated = mock(ServiceOrder.class);
        ServiceOrderResponse response = mock(ServiceOrderResponse.class);

        when(service.updateStatus("id", ServiceOrderStatus.APROVADA)).thenReturn(updated);
        when(presenter.toResponse(updated)).thenReturn(response);

        assertSame(response, adapter.updateStatus("id", ServiceOrderStatus.APROVADA));
    }

    @Test
    @DisplayName("deleteServiceOrder deve delegar ao use case")
    void deleteServiceOrder_ShouldDelegate() {
        adapter.deleteServiceOrder("id");
        verify(service).deleteServiceOrder("id");
    }

    @Test
    @DisplayName("sendToCustomer deve delegar ao use case e devolver a mensagem de confirmação")
    void sendToCustomer_ShouldDelegateAndReturnMessage() {
        SendToCustomerResponse result = adapter.sendToCustomer("id");

        verify(service).sendToCustomer("id");
        assertThat(result.message()).contains("enviada para o cliente");
    }
}