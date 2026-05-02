package br.com.officyna.monitoring.api.controller;

import br.com.officyna.monitoring.api.resources.ForceRecalcResponse;
import br.com.officyna.monitoring.api.resources.LaborMonitoringResponse;
import br.com.officyna.monitoring.domain.service.LaborMonitoringService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class MonitoringControllerTest {

    private MockMvc mockMvc;

    @Mock
    private LaborMonitoringService laborMonitoringService;

    @InjectMocks
    private MonitoringController monitoringController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(monitoringController).build();
    }

    // ─────────────── GET /api/monitoring/labors ───────────────

    @Test
    @DisplayName("Deve retornar lista de monitoramentos com status 200")
    void findAll_ShouldReturnOk() throws Exception {
        LaborMonitoringResponse response = buildResponse("lab-1", "Troca de óleo", 3.5, "3 dias 04:00:00", 10);
        when(laborMonitoringService.findAll()).thenReturn(List.of(response));

        mockMvc.perform(get("/api/monitoring/labors"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(laborMonitoringService, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando não há monitoramentos")
    void findAll_ShouldReturnEmptyList_WhenNoMonitorings() throws Exception {
        when(laborMonitoringService.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/api/monitoring/labors"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("[]"));

        verify(laborMonitoringService, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve retornar múltiplos monitoramentos")
    void findAll_ShouldReturnMultipleMonitorings() throws Exception {
        List<LaborMonitoringResponse> responses = List.of(
                buildResponse("lab-1", "Troca de óleo", 1.0, "1 dia 00:00:00", 5),
                buildResponse("lab-2", "Alinhamento", 0.5, "04:00:00", 3)
        );
        when(laborMonitoringService.findAll()).thenReturn(responses);

        mockMvc.perform(get("/api/monitoring/labors"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(laborMonitoringService, times(1)).findAll();
    }

    // ─────────────── PUT /api/monitoring/force-recalc ───────────────

    @Test
    @DisplayName("Deve recalcular médias e retornar quantidade de serviços processados")
    void forceRecalc_ShouldReturnOk() throws Exception {
        ForceRecalcResponse response = new ForceRecalcResponse(7);
        when(laborMonitoringService.forceRecalc()).thenReturn(response);

        mockMvc.perform(put("/api/monitoring/force-recalc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(laborMonitoringService, times(1)).forceRecalc();
    }

    @Test
    @DisplayName("Deve retornar zero quando não há serviços para recalcular")
    void forceRecalc_ShouldReturnZero_WhenNoLabors() throws Exception {
        ForceRecalcResponse response = new ForceRecalcResponse(0);
        when(laborMonitoringService.forceRecalc()).thenReturn(response);

        mockMvc.perform(put("/api/monitoring/force-recalc"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"laborsProcessed\":0}"));

        verify(laborMonitoringService, times(1)).forceRecalc();
    }

    // ─────────────── helper ───────────────

    private LaborMonitoringResponse buildResponse(String laborId, String name, double avg, String formatted, int total) {
        return new LaborMonitoringResponse(laborId, name, "Descrição", avg, formatted, total, LocalDateTime.now());
    }
}