package br.com.officyna.monitoring.domain.controller;

import br.com.officyna.monitoring.api.resources.ForceRecalcResponse;
import br.com.officyna.monitoring.api.resources.LaborMonitoringResponse;
import br.com.officyna.monitoring.domain.presenter.LaborMonitoringPresenter;
import br.com.officyna.monitoring.domain.service.LaborMonitoringService;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class MonitoringControllerAdapter {

    private final LaborMonitoringService service;
    private final LaborMonitoringPresenter presenter;

    public MonitoringControllerAdapter(
            LaborMonitoringService service,
            LaborMonitoringPresenter presenter) {
        this.service = service;
        this.presenter = presenter;
    }

    public List<LaborMonitoringResponse> findAll() {
        log.info("Searching all labor monitoring records");

        List<LaborMonitoringResponse> monitoring = service.findAll()
                .stream()
                .map(presenter::toResponse)
                .toList();

        log.info("Labor monitoring records found: {}", monitoring.size());

        return monitoring;
    }

    public ForceRecalcResponse forceRecalc() {
        log.info("Starting force recalculation of labor monitoring");

        int processed = service.forceRecalc();

        log.info(
                "Force recalculation of labor monitoring completed. Records processed: {}",
                processed
        );

        return new ForceRecalcResponse(processed);
    }
}