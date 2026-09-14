package br.com.officyna.administrative.supply.domain.controller;

import br.com.officyna.administrative.supply.api.resources.SupplyRequest;
import br.com.officyna.administrative.supply.api.resources.SupplyResponse;
import br.com.officyna.administrative.supply.domain.entity.SupplyType;
import br.com.officyna.administrative.supply.domain.mapper.SupplyMapper;
import br.com.officyna.administrative.supply.domain.presenter.SupplyPresenter;
import br.com.officyna.administrative.supply.domain.service.SupplyService;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class SupplyControllerAdapter {

    private final SupplyService service;
    private final SupplyMapper mapper;
    private final SupplyPresenter presenter;

    public SupplyControllerAdapter(
            SupplyService service,
            SupplyMapper mapper,
            SupplyPresenter presenter) {
        this.service = service;
        this.mapper = mapper;
        this.presenter = presenter;
    }

    public List<SupplyResponse> findAll() {
        log.info("Searching all active supplies");

        List<SupplyResponse> response = service.findAll()
                .stream()
                .map(presenter::toResponse)
                .toList();

        log.info("Active supplies found: {}", response.size());

        return response;
    }

    public List<SupplyResponse> findByType(SupplyType type) {
        log.info("Searching active supplies by type");

        List<SupplyResponse> response = service.findByType(type)
                .stream()
                .map(presenter::toResponse)
                .toList();

        log.info("Active supplies found by type: {}", response.size());

        return response;
    }

    public SupplyResponse findById(String id) {
        log.info("Searching supply by id");

        SupplyResponse response = presenter.toResponse(
                service.findById(id)
        );

        log.info("Supply found by id");

        return response;
    }

    public SupplyResponse create(SupplyRequest request) {
        log.info("Creating supply");

        SupplyResponse response = presenter.toResponse(
                service.create(mapper.toEntity(request))
        );

        log.info("Supply created successfully");

        return response;
    }

    public SupplyResponse update(String id, SupplyRequest request) {
        log.info("Updating supply");

        SupplyResponse response = presenter.toResponse(
                service.update(id, mapper.toEntity(request))
        );

        log.info("Supply updated successfully");

        return response;
    }

    public void delete(String id) {
        log.info("Deleting supply");

        service.delete(id);

        log.info("Supply deleted successfully");
    }
}