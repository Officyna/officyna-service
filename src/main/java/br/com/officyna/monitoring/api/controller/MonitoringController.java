package br.com.officyna.monitoring.api.controller;

import br.com.officyna.monitoring.api.MonitoringApi;
import br.com.officyna.monitoring.api.resources.ForceRecalcResponse;
import br.com.officyna.monitoring.api.resources.LaborMonitoringResponse;
import br.com.officyna.monitoring.domain.service.LaborMonitoringService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class MonitoringController implements MonitoringApi {

    private final LaborMonitoringService laborMonitoringService;

    @Override
    public ResponseEntity<List<LaborMonitoringResponse>> findAll() {
        return ResponseEntity.ok(laborMonitoringService.findAll());
    }

    @Override
    public ResponseEntity<Void> testUpdate(String laborId, LocalDateTime startDate, LocalDateTime endDate) {
        laborMonitoringService.updateExecutionTimeInDays(laborId, startDate, endDate);
        return ResponseEntity.accepted().build();
    }

    @Override
    public ResponseEntity<ForceRecalcResponse> forceRecalc() {
        return ResponseEntity.ok(laborMonitoringService.forceRecalc());
    }
}