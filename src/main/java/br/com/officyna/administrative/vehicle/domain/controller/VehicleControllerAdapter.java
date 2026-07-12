package br.com.officyna.administrative.vehicle.domain.controller;

import br.com.officyna.administrative.vehicle.api.resources.VehicleRequest;
import br.com.officyna.administrative.vehicle.api.resources.VehicleResponse;
import br.com.officyna.administrative.vehicle.domain.mapper.VehicleMapper;
import br.com.officyna.administrative.vehicle.domain.presenter.VehiclePresenter;
import br.com.officyna.administrative.vehicle.domain.service.VehicleService;

import java.util.List;

public class VehicleControllerAdapter {

    private final VehicleService service;
    private final VehicleMapper mapper;
    private final VehiclePresenter presenter;

    public VehicleControllerAdapter(VehicleService service, VehicleMapper mapper, VehiclePresenter presenter) {
        this.service = service;
        this.mapper = mapper;
        this.presenter = presenter;
    }

    public List<VehicleResponse> findAll() {
        return service.findAll()
                .stream()
                .map(presenter::toResponse)
                .toList();
    }

    public VehicleResponse findById(String id) {
        return presenter.toResponse(service.findById(id));
    }

    public List<VehicleResponse> findByCustomer(String customerId) {
        return service.findByCustomer(customerId)
                .stream()
                .map(presenter::toResponse)
                .toList();
    }

    public VehicleResponse create(VehicleRequest request) {
        return presenter.toResponse(service.create(mapper.toEntity(request)));
    }

    public VehicleResponse update(String id, VehicleRequest request) {
        return presenter.toResponse(service.update(id, mapper.toEntity(request)));
    }

    public void delete(String id) {
        service.delete(id);
    }
}