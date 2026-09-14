package br.com.officyna.serviceorder.domain.controller;

import br.com.officyna.serviceorder.api.resources.ExistServiceOrderRequest;
import br.com.officyna.serviceorder.api.resources.LaborsRequest;
import br.com.officyna.serviceorder.api.resources.NewServiceOrderRequest;
import br.com.officyna.serviceorder.api.resources.SendToCustomerResponse;
import br.com.officyna.serviceorder.api.resources.ServiceOrderResponse;
import br.com.officyna.serviceorder.api.resources.SupplysRequest;
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
    @DisplayName("findById deve apresentar a ordem retornada pelo use case")
    void findById_ShouldPresentResult() {
        ServiceOrder entity = mock(ServiceOrder.class);
        ServiceOrderResponse response = mock(ServiceOrderResponse.class);

        when(service.findById("id")).thenReturn(entity);
        when(presenter.toResponse(entity)).thenReturn(response);

        assertSame(response, adapter.findById("id"));
    }

    @Test
    @DisplayName("findByServiceOrderNumber deve apresentar a ordem retornada pelo use case")
    void findByServiceOrderNumber_ShouldPresentResult() {
        ServiceOrder entity = mock(ServiceOrder.class);
        ServiceOrderResponse response = mock(ServiceOrderResponse.class);

        when(service.findByServiceOrderNumber(123L)).thenReturn(entity);
        when(presenter.toResponse(entity)).thenReturn(response);

        assertSame(response, adapter.findByServiceOrderNumber(123L));
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
    @DisplayName("updateServiceOrder deve chamar o use case e apresentar o resultado")
    void updateServiceOrder_ShouldInvokeUseCaseAndPresent() {
        ExistServiceOrderRequest request = ExistServiceOrderRequest.builder().mechanicId("m").build();
        ServiceOrder updated = mock(ServiceOrder.class);
        ServiceOrderResponse response = mock(ServiceOrderResponse.class);

        when(service.updateServiceOrder("id", request)).thenReturn(updated);
        when(presenter.toResponse(updated)).thenReturn(response);

        assertSame(response, adapter.updateServiceOrder("id", request));
    }

    @Test
    @DisplayName("addLaborInServiceOrder deve chamar o use case e apresentar o resultado")
    void addLaborInServiceOrder_ShouldInvokeUseCaseAndPresent() {
        List<LaborsRequest> laborsRequest = List.of(new LaborsRequest("labor-1"));
        ServiceOrder updated = mock(ServiceOrder.class);
        ServiceOrderResponse response = mock(ServiceOrderResponse.class);

        when(service.addLaborsInServiceOrder("id", laborsRequest)).thenReturn(updated);
        when(presenter.toResponse(updated)).thenReturn(response);

        assertSame(response, adapter.addLaborInServiceOrder("id", laborsRequest));
    }

    @Test
    @DisplayName("removeLaborFromServiceOrder deve chamar o use case e apresentar o resultado")
    void removeLaborFromServiceOrder_ShouldInvokeUseCaseAndPresent() {
        ServiceOrder updated = mock(ServiceOrder.class);
        ServiceOrderResponse response = mock(ServiceOrderResponse.class);

        when(service.removeLaborFromServiceOrder("id", "labor-1")).thenReturn(updated);
        when(presenter.toResponse(updated)).thenReturn(response);

        assertSame(response, adapter.removeLaborFromServiceOrder("id", "labor-1"));
    }

    @Test
    @DisplayName("addSupplyInServiceOrder deve chamar o use case e apresentar o resultado")
    void addSupplyInServiceOrder_ShouldInvokeUseCaseAndPresent() {
        List<SupplysRequest> supplysRequest = List.of(new SupplysRequest("supply-1", 2));
        ServiceOrder updated = mock(ServiceOrder.class);
        ServiceOrderResponse response = mock(ServiceOrderResponse.class);

        when(service.addSupplyFromServiceOrder("id", supplysRequest)).thenReturn(updated);
        when(presenter.toResponse(updated)).thenReturn(response);

        assertSame(response, adapter.addSupplyInServiceOrder("id", supplysRequest));
    }

    @Test
    @DisplayName("removeSupplyFromServiceOrder deve chamar o use case e apresentar o resultado")
    void removeSupplyFromServiceOrder_ShouldInvokeUseCaseAndPresent() {
        ServiceOrder updated = mock(ServiceOrder.class);
        ServiceOrderResponse response = mock(ServiceOrderResponse.class);

        when(service.removeSupplyFromServiceOrder("id", "supply-1")).thenReturn(updated);
        when(presenter.toResponse(updated)).thenReturn(response);

        assertSame(response, adapter.removeSupplyFromServiceOrder("id", "supply-1"));
    }

    @Test
    @DisplayName("startLabor deve chamar o use case e apresentar o resultado")
    void startLabor_ShouldInvokeUseCaseAndPresent() {
        ServiceOrder updated = mock(ServiceOrder.class);
        ServiceOrderResponse response = mock(ServiceOrderResponse.class);

        when(service.startLabor("id", "labor-1")).thenReturn(updated);
        when(presenter.toResponse(updated)).thenReturn(response);

        assertSame(response, adapter.startLabor("id", "labor-1"));
    }

    @Test
    @DisplayName("finishLabor deve chamar o use case e apresentar o resultado")
    void finishLabor_ShouldInvokeUseCaseAndPresent() {
        ServiceOrder updated = mock(ServiceOrder.class);
        ServiceOrderResponse response = mock(ServiceOrderResponse.class);

        when(service.finishLabor("id", "labor-1")).thenReturn(updated);
        when(presenter.toResponse(updated)).thenReturn(response);

        assertSame(response, adapter.finishLabor("id", "labor-1"));
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