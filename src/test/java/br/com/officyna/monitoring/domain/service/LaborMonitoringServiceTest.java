package br.com.officyna.monitoring.domain.service;

import br.com.officyna.administrative.labor.domain.LaborEntity;
import br.com.officyna.administrative.labor.repository.LaborRepository;
import br.com.officyna.monitoring.api.resources.ForceRecalcResponse;
import br.com.officyna.monitoring.api.resources.LaborMonitoringResponse;
import br.com.officyna.monitoring.domain.entity.LaborMonitoringEntity;
import br.com.officyna.monitoring.repository.LaborMonitoringRepository;
import br.com.officyna.serviceorder.domain.dto.LaborDetailDTO;
import br.com.officyna.serviceorder.domain.dto.LaborsDTO;
import br.com.officyna.serviceorder.domain.entity.ServiceOrderEntity;
import br.com.officyna.serviceorder.repository.ServiceOrderRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LaborMonitoringServiceTest {

    @Mock
    private LaborMonitoringRepository monitoringRepository;

    @Mock
    private LaborRepository laborRepository;

    @Mock
    private ServiceOrderRepository serviceOrderRepository;

    @InjectMocks
    private LaborMonitoringService service;

    private LaborMonitoringEntity buildMonitoringEntity(String laborId, double average, int total) {
        return LaborMonitoringEntity.builder()
                .id("mon-" + laborId)
                .laborId(laborId)
                .laborName("Labor " + laborId)
                .laborDescription("Desc " + laborId)
                .averageExecutionTimeInDays(average)
                .totalExecutions(total)
                .build();
    }

    private LaborEntity buildLaborEntity(String id, String name) {
        return LaborEntity.builder()
                .id(id)
                .name(name)
                .description("Desc " + name)
                .price(new BigDecimal("100.00"))
                .executionTimeInDays(3)
                .active(true)
                .build();
    }

    private LaborDetailDTO buildLaborDetail(String laborId, LocalDateTime start, LocalDateTime end) {
        return LaborDetailDTO.builder()
                .laborId(laborId)
                .name("Labor " + laborId)
                .description("Desc " + laborId)
                .laborPrice(new BigDecimal("100.00"))
                .startDate(start)
                .endDate(end)
                .build();
    }

    // ─────────────── findAll ───────────────

    @Test
    @DisplayName("Deve retornar lista de monitoramentos quando existirem registros")
    void findAll_DeveRetornarListaDeMonitoramentos() {
        LaborMonitoringEntity entity1 = buildMonitoringEntity("lab1", 3.0, 2);
        LaborMonitoringEntity entity2 = buildMonitoringEntity("lab2", 5.5, 4);

        when(monitoringRepository.findAll()).thenReturn(List.of(entity1, entity2));

        List<LaborMonitoringResponse> result = service.findAll();

        assertEquals(2, result.size());
        assertEquals("lab1", result.get(0).laborId());
        assertEquals(3.0, result.get(0).averageExecutionTimeInDays());
        assertEquals(2, result.get(0).totalExecutions());
        assertEquals("lab2", result.get(1).laborId());
        verify(monitoringRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando não houver registros de monitoramento")
    void findAll_DeveRetornarListaVaziaQuandoSemRegistros() {
        when(monitoringRepository.findAll()).thenReturn(List.of());

        List<LaborMonitoringResponse> result = service.findAll();

        assertTrue(result.isEmpty());
        verify(monitoringRepository, times(1)).findAll();
    }

    // ─────────────── updateExecutionTimeInDays ───────────────

    @Test
    @DisplayName("Deve criar novo registro de monitoramento quando labor ainda não tem histórico")
    void updateExecutionTimeInDays_DeveCriarNovoRegistro_QuandoLaborSemHistorico() {
        String laborId = "lab1";
        LocalDateTime start = LocalDateTime.of(2024, 1, 1, 8, 0);
        LocalDateTime end = LocalDateTime.of(2024, 1, 4, 8, 0); // 3 dias

        when(monitoringRepository.findByLaborId(laborId)).thenReturn(Optional.empty());
        when(laborRepository.findById(laborId)).thenReturn(Optional.of(buildLaborEntity(laborId, "Troca de óleo")));
        when(monitoringRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        service.updateExecutionTimeInDays(laborId, start, end);

        ArgumentCaptor<LaborMonitoringEntity> captor = ArgumentCaptor.forClass(LaborMonitoringEntity.class);
        verify(monitoringRepository, times(1)).save(captor.capture());

        LaborMonitoringEntity saved = captor.getValue();
        assertEquals(laborId, saved.getLaborId());
        assertEquals(3.0, saved.getAverageExecutionTimeInDays());
        assertEquals(1, saved.getTotalExecutions());
    }

    @Test
    @DisplayName("Deve recalcular média incremental quando labor já possui histórico")
    void updateExecutionTimeInDays_DeveRecalcularMedia_QuandoLaborComHistorico() {
        String laborId = "lab1";
        LocalDateTime start = LocalDateTime.of(2024, 1, 1, 8, 0);
        LocalDateTime end = LocalDateTime.of(2024, 1, 6, 8, 0); // 5 dias

        LaborMonitoringEntity existing = buildMonitoringEntity(laborId, 3.0, 2); // média atual = 3, total = 2
        // nova média = (3 * 2 + 5) / 3 = 11/3 ≈ 3.666...

        when(monitoringRepository.findByLaborId(laborId)).thenReturn(Optional.of(existing));
        when(monitoringRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        service.updateExecutionTimeInDays(laborId, start, end);

        ArgumentCaptor<LaborMonitoringEntity> captor = ArgumentCaptor.forClass(LaborMonitoringEntity.class);
        verify(monitoringRepository, times(1)).save(captor.capture());

        LaborMonitoringEntity saved = captor.getValue();
        assertEquals(3, saved.getTotalExecutions());
        assertEquals(11.0 / 3.0, saved.getAverageExecutionTimeInDays(), 0.001);
    }

    @Test
    @DisplayName("Deve ignorar atualização quando endDate for anterior ao startDate")
    void updateExecutionTimeInDays_DeveIgnorar_QuandoDuracaoNegativa() {
        String laborId = "lab1";
        LocalDateTime start = LocalDateTime.of(2024, 1, 5, 8, 0);
        LocalDateTime end = LocalDateTime.of(2024, 1, 1, 8, 0); // end antes de start

        service.updateExecutionTimeInDays(laborId, start, end);

        verify(monitoringRepository, never()).findByLaborId(any());
        verify(monitoringRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve ignorar atualização quando labor não existe no banco")
    void updateExecutionTimeInDays_DeveIgnorar_QuandoLaborNaoExiste() {
        String laborId = "laborInexistente";
        LocalDateTime start = LocalDateTime.of(2024, 1, 1, 8, 0);
        LocalDateTime end = LocalDateTime.of(2024, 1, 3, 8, 0);

        when(monitoringRepository.findByLaborId(laborId)).thenReturn(Optional.empty());
        when(laborRepository.findById(laborId)).thenReturn(Optional.empty());

        service.updateExecutionTimeInDays(laborId, start, end);

        verify(monitoringRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve aceitar execução no mesmo dia com duração zero")
    void updateExecutionTimeInDays_DeveAceitarDuracaoZero() {
        String laborId = "lab1";
        LocalDateTime start = LocalDateTime.of(2024, 1, 1, 8, 0);
        LocalDateTime end = LocalDateTime.of(2024, 1, 1, 18, 0); // mesmo dia

        when(monitoringRepository.findByLaborId(laborId)).thenReturn(Optional.empty());
        when(laborRepository.findById(laborId)).thenReturn(Optional.of(buildLaborEntity(laborId, "Revisão rápida")));
        when(monitoringRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        service.updateExecutionTimeInDays(laborId, start, end);

        ArgumentCaptor<LaborMonitoringEntity> captor = ArgumentCaptor.forClass(LaborMonitoringEntity.class);
        verify(monitoringRepository, times(1)).save(captor.capture());
        assertEquals(0.0, captor.getValue().getAverageExecutionTimeInDays());
    }

    // ─────────────── forceRecalc ───────────────

    @Test
    @DisplayName("Deve processar todos os labors ativos e calcular média a partir das service orders")
    void forceRecalc_DeveProcessarTodosOsLabors() {
        LocalDateTime base = LocalDateTime.of(2024, 1, 1, 8, 0);

        LaborEntity labor1 = buildLaborEntity("lab1", "Troca de óleo");
        LaborEntity labor2 = buildLaborEntity("lab2", "Alinhamento");

        LaborDetailDTO lab1exec1 = buildLaborDetail("lab1", base, base.plusDays(2));
        LaborDetailDTO lab1exec2 = buildLaborDetail("lab1", base, base.plusDays(4));
        LaborDetailDTO lab2exec1 = buildLaborDetail("lab2", base, base.plusDays(3));

        ServiceOrderEntity so1 = ServiceOrderEntity.builder()
                .labors(LaborsDTO.builder().laborsDetails(List.of(lab1exec1)).build())
                .build();
        ServiceOrderEntity so2 = ServiceOrderEntity.builder()
                .labors(LaborsDTO.builder().laborsDetails(List.of(lab1exec2)).build())
                .build();
        ServiceOrderEntity so3 = ServiceOrderEntity.builder()
                .labors(LaborsDTO.builder().laborsDetails(List.of(lab2exec1)).build())
                .build();

        when(laborRepository.findByActiveTrue()).thenReturn(List.of(labor1, labor2));
        when(serviceOrderRepository.findByLaborIdWithCompletedExecutions("lab1")).thenReturn(List.of(so1, so2));
        when(serviceOrderRepository.findByLaborIdWithCompletedExecutions("lab2")).thenReturn(List.of(so3));
        when(monitoringRepository.findByLaborId(any())).thenReturn(Optional.empty());
        when(monitoringRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        ForceRecalcResponse response = service.forceRecalc();

        assertEquals(2, response.laborsProcessed());
        verify(monitoringRepository, times(2)).save(any(LaborMonitoringEntity.class));
    }

    @Test
    @DisplayName("Não deve salvar monitoramento quando labor não possui execuções completas")
    void forceRecalc_NaoDeveSalvar_QuandoSemExecucoes() {
        LaborEntity labor = buildLaborEntity("lab1", "Troca de óleo");

        when(laborRepository.findByActiveTrue()).thenReturn(List.of(labor));
        when(serviceOrderRepository.findByLaborIdWithCompletedExecutions("lab1")).thenReturn(List.of());

        ForceRecalcResponse response = service.forceRecalc();

        assertEquals(0, response.laborsProcessed());
        verify(monitoringRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve retornar zero quando não houver labors ativos")
    void forceRecalc_DeveRetornarZeroQuandoSemLabors() {
        when(laborRepository.findByActiveTrue()).thenReturn(List.of());

        ForceRecalcResponse response = service.forceRecalc();

        assertEquals(0, response.laborsProcessed());
        verify(monitoringRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve atualizar registro de monitoramento existente no forceRecalc")
    void forceRecalc_DeveAtualizarRegistroExistente() {
        LocalDateTime base = LocalDateTime.of(2024, 1, 1, 8, 0);
        LaborEntity labor = buildLaborEntity("lab1", "Troca de óleo");
        LaborDetailDTO detail = buildLaborDetail("lab1", base, base.plusDays(5));

        ServiceOrderEntity so = ServiceOrderEntity.builder()
                .labors(LaborsDTO.builder().laborsDetails(List.of(detail)).build())
                .build();

        LaborMonitoringEntity existing = buildMonitoringEntity("lab1", 2.0, 3);

        when(laborRepository.findByActiveTrue()).thenReturn(List.of(labor));
        when(serviceOrderRepository.findByLaborIdWithCompletedExecutions("lab1")).thenReturn(List.of(so));
        when(monitoringRepository.findByLaborId("lab1")).thenReturn(Optional.of(existing));
        when(monitoringRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        service.forceRecalc();

        ArgumentCaptor<LaborMonitoringEntity> captor = ArgumentCaptor.forClass(LaborMonitoringEntity.class);
        verify(monitoringRepository, times(1)).save(captor.capture());

        assertEquals(5.0, captor.getValue().getAverageExecutionTimeInDays());
        assertEquals(1, captor.getValue().getTotalExecutions());
        assertEquals("Troca de óleo", captor.getValue().getLaborName());
    }
}