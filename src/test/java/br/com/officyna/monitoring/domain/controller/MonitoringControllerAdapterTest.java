package br.com.officyna.monitoring.domain.controller;

import br.com.officyna.monitoring.api.resources.ForceRecalcResponse;
import br.com.officyna.monitoring.api.resources.LaborMonitoringResponse;
import br.com.officyna.monitoring.domain.entity.LaborMonitoring;
import br.com.officyna.monitoring.domain.presenter.LaborMonitoringPresenter;
import br.com.officyna.monitoring.domain.service.LaborMonitoringService;
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
class MonitoringControllerAdapterTest {

    @Mock
    private LaborMonitoringService service;

    @Mock
    private LaborMonitoringPresenter presenter;

    @InjectMocks
    private MonitoringControllerAdapter adapter;

    @Test
    @DisplayName("findAll deve apresentar cada monitoramento retornado pelo use case")
    void findAll_ShouldPresentEach() {
        LaborMonitoring entity = mock(LaborMonitoring.class);
        LaborMonitoringResponse response = mock(LaborMonitoringResponse.class);

        when(service.findAll()).thenReturn(List.of(entity));
        when(presenter.toResponse(entity)).thenReturn(response);

        List<LaborMonitoringResponse> result = adapter.findAll();

        assertEquals(1, result.size());
        assertSame(response, result.get(0));
    }

    @Test
    @DisplayName("forceRecalc deve envolver a quantidade processada em ForceRecalcResponse")
    void forceRecalc_ShouldWrapProcessedCount() {
        when(service.forceRecalc()).thenReturn(7);

        ForceRecalcResponse response = adapter.forceRecalc();

        assertEquals(7, response.laborsProcessed());
        verify(service).forceRecalc();
    }
}