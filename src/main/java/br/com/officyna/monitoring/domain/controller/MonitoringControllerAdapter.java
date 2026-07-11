package br.com.officyna.monitoring.domain.controller;

import br.com.officyna.monitoring.api.resources.ForceRecalcResponse;
import br.com.officyna.monitoring.api.resources.LaborMonitoringResponse;
import br.com.officyna.monitoring.domain.presenter.LaborMonitoringPresenter;
import br.com.officyna.monitoring.domain.service.LaborMonitoringService;

import java.util.List;

public class MonitoringControllerAdapter {

    private final LaborMonitoringService service;
    private final LaborMonitoringPresenter presenter;

    public MonitoringControllerAdapter(LaborMonitoringService service, LaborMonitoringPresenter presenter) {
        this.service = service;
        this.presenter = presenter;
    }

    public List<LaborMonitoringResponse> findAll() {
        return service.findAll()
                .stream()
                .map(presenter::toResponse)
                .toList();
    }

    public ForceRecalcResponse forceRecalc() {
        return new ForceRecalcResponse(service.forceRecalc());
    }
}