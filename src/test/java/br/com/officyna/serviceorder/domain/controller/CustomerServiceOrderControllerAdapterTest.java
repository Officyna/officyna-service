package br.com.officyna.serviceorder.domain.controller;

import br.com.officyna.serviceorder.api.resources.ModifySituationRequest;
import br.com.officyna.serviceorder.api.resources.ServiceOrderResponse;
import br.com.officyna.serviceorder.domain.entity.ServiceOrder;
import br.com.officyna.serviceorder.domain.enums.ServiceOrderStatus;
import br.com.officyna.serviceorder.domain.presenter.ServiceOrderPresenter;
import br.com.officyna.serviceorder.domain.service.CustomerServiceOrderService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceOrderControllerAdapterTest {

    @Mock
    private CustomerServiceOrderService service;

    @Mock
    private ServiceOrderPresenter presenter;

    @InjectMocks
    private CustomerServiceOrderControllerAdapter adapter;

    @Test
    @DisplayName("findByCustomerDocument deve apresentar cada ordem retornada pelo use case")
    void findByCustomerDocument_ShouldPresentEach() {
        ServiceOrder entity = mock(ServiceOrder.class);
        ServiceOrderResponse response = mock(ServiceOrderResponse.class);

        when(service.findByCustomerDocument("doc", null)).thenReturn(List.of(entity));
        when(presenter.toResponse(entity)).thenReturn(response);

        List<ServiceOrderResponse> result = adapter.findByCustomerDocument("doc", null);

        assertEquals(1, result.size());
        assertSame(response, result.get(0));
    }

    @Test
    @DisplayName("updateLaborSituation deve chamar o use case e apresentar o resultado")
    void updateLaborSituation_ShouldInvokeUseCaseAndPresent() {
        List<ModifySituationRequest> request = List.of();
        ServiceOrder updated = mock(ServiceOrder.class);
        ServiceOrderResponse response = mock(ServiceOrderResponse.class);

        when(service.updateLaborSituation("id", request)).thenReturn(updated);
        when(presenter.toResponse(updated)).thenReturn(response);

        assertSame(response, adapter.updateLaborSituation("id", request));
    }
}