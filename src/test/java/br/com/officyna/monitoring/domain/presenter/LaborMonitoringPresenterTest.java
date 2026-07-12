package br.com.officyna.monitoring.domain.presenter;

import br.com.officyna.monitoring.api.resources.LaborMonitoringResponse;
import br.com.officyna.monitoring.domain.entity.LaborMonitoring;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LaborMonitoringPresenterTest {

    private final LaborMonitoringPresenter presenter = new LaborMonitoringPresenter();

    private LaborMonitoring buildEntity(String laborId, double average, int total) {
        return LaborMonitoring.builder()
                .id("mon-" + laborId)
                .laborId(laborId)
                .laborName("Labor " + laborId)
                .laborDescription("Desc " + laborId)
                .averageExecutionTimeInDays(average)
                .totalExecutions(total)
                .build();
    }

    @Test
    @DisplayName("toResponse deve mapear todos os campos")
    void toResponse_DeveMappearTodosOsCampos() {
        LaborMonitoringResponse response = presenter.toResponse(buildEntity("lab1", 3.0, 2));

        assertEquals("lab1", response.laborId());
        assertEquals("Labor lab1", response.laborName());
        assertEquals(3.0, response.averageExecutionTimeInDays());
        assertEquals(2, response.totalExecutions());
    }

    @Test
    @DisplayName("Deve formatar 0.25 dia útil (2 horas) como 02:00:00")
    void toResponse_DeveFormatarQuartoDeJornada() {
        LaborMonitoringResponse response = presenter.toResponse(buildEntity("lab1", 0.25, 1));

        assertEquals("02:00:00", response.averageExecutionTimeFormatted());
    }

    @Test
    @DisplayName("Deve formatar 1.0 dia útil (8 horas exatas) como 1 dia 00:00:00")
    void toResponse_DeveFormatarUmDiaUtil() {
        LaborMonitoringResponse response = presenter.toResponse(buildEntity("lab1", 1.0, 1));

        assertEquals("1 dia 00:00:00", response.averageExecutionTimeFormatted());
    }

    @Test
    @DisplayName("Deve formatar 1.5 dias úteis (12 horas) como 1 dia 04:00:00")
    void toResponse_DeveFormatarDiaEMeio() {
        LaborMonitoringResponse response = presenter.toResponse(buildEntity("lab1", 1.5, 2));

        assertEquals("1 dia 04:00:00", response.averageExecutionTimeFormatted());
    }
}