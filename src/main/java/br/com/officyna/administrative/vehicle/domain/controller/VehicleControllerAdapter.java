package br.com.officyna.administrative.vehicle.domain.controller;

import br.com.officyna.administrative.vehicle.api.resources.VehicleRequest;
import br.com.officyna.administrative.vehicle.api.resources.VehicleResponse;
import br.com.officyna.administrative.vehicle.domain.mapper.VehicleMapper;
import br.com.officyna.administrative.vehicle.domain.presenter.VehiclePresenter;
import br.com.officyna.administrative.vehicle.domain.service.VehicleService;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class VehicleControllerAdapter {

    private final VehicleService service;
    private final VehicleMapper mapper;
    private final VehiclePresenter presenter;

    public VehicleControllerAdapter(
            VehicleService service,
            VehicleMapper mapper,
            VehiclePresenter presenter) {
        this.service = service;
        this.mapper = mapper;
        this.presenter = presenter;
    }

    public List<VehicleResponse> findAll() {
        log.info("Searching all vehicles");

        List<VehicleResponse> vehicles = service.findAll()
                .stream()
                .map(presenter::toResponse)
                .toList();

        log.info("Vehicles found: {}", vehicles.size());

        return vehicles;
    }

    public VehicleResponse findById(String id) {
        log.info("Searching vehicle by id: {}", id);

        VehicleResponse vehicle = presenter.toResponse(
                service.findById(id)
        );

        log.info("Vehicle found by id: {}", id);

        return vehicle;
    }

    public List<VehicleResponse> findByCustomer(String customerId) {
        log.info("Searching vehicles by customer id: {}", customerId);

        List<VehicleResponse> vehicles = service.findByCustomer(customerId)
                .stream()
                .map(presenter::toResponse)
                .toList();

        log.info(
                "Vehicles found for customer id {}: {}",
                customerId,
                vehicles.size()
        );

        return vehicles;
    }

    public VehicleResponse create(VehicleRequest request) {
        log.info("Creating vehicle");

        VehicleResponse vehicle = presenter.toResponse(
                service.create(mapper.toEntity(request))
        );

        log.info("Vehicle created successfully");

        return vehicle;
    }

    public VehicleResponse update(String id, VehicleRequest request) {
        log.info("Updating vehicle with id: {}", id);

        VehicleResponse vehicle = presenter.toResponse(
                service.update(id, mapper.toEntity(request))
        );

        log.info("Vehicle updated successfully with id: {}", id);

        return vehicle;
    }

    public void delete(String id) {
        log.info("Deleting vehicle with id: {}", id);

        service.delete(id);

        log.info("Vehicle deleted successfully with id: {}", id);
    }
}