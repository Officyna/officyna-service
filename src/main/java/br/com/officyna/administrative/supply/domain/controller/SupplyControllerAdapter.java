package br.com.officyna.administrative.supply.domain.controller;

import br.com.officyna.administrative.supply.api.resources.SupplyRequest;
import br.com.officyna.administrative.supply.api.resources.SupplyResponse;
import br.com.officyna.administrative.supply.domain.entity.SupplyType;
import br.com.officyna.administrative.supply.domain.mapper.SupplyMapper;
import br.com.officyna.administrative.supply.domain.presenter.SupplyPresenter;
import br.com.officyna.administrative.supply.domain.service.SupplyService;

import java.util.List;

public class SupplyControllerAdapter {

    private final SupplyService service;
    private final SupplyMapper mapper;
    private final SupplyPresenter presenter;

    public SupplyControllerAdapter(SupplyService service, SupplyMapper mapper, SupplyPresenter presenter) {
        this.service = service;
        this.mapper = mapper;
        this.presenter = presenter;
    }

    public List<SupplyResponse> findAll() {
        return service.findAll()
                .stream()
                .map(presenter::toResponse)
                .toList();
    }

    public List<SupplyResponse> findByType(SupplyType type) {
        return service.findByType(type)
                .stream()
                .map(presenter::toResponse)
                .toList();
    }

    public SupplyResponse findById(String id) {
        return presenter.toResponse(service.findById(id));
    }

    public SupplyResponse create(SupplyRequest request) {
        return presenter.toResponse(service.create(mapper.toEntity(request)));
    }

    public SupplyResponse update(String id, SupplyRequest request) {
        return presenter.toResponse(service.update(id, mapper.toEntity(request)));
    }

    public void delete(String id) {
        service.delete(id);
    }
}