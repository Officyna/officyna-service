package br.com.officyna.administrative.labor.domain.controller;

import br.com.officyna.administrative.labor.api.resources.LaborRequest;
import br.com.officyna.administrative.labor.api.resources.LaborResponse;
import br.com.officyna.administrative.labor.domain.mapper.LaborMapper;
import br.com.officyna.administrative.labor.domain.presenter.LaborPresenter;
import br.com.officyna.administrative.labor.domain.service.LaborService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class LaborControllerAdapter {

    private static final Logger log = LoggerFactory.getLogger(LaborControllerAdapter.class);

    private final LaborService service;
    private final LaborMapper mapper;
    private final LaborPresenter presenter;

    public LaborControllerAdapter(
            LaborService service,
            LaborMapper mapper,
            LaborPresenter presenter) {
        this.service = service;
        this.mapper = mapper;
        this.presenter = presenter;
    }

    public List<LaborResponse> findAll() {
        log.info("Searching all active labors");

        List<LaborResponse> response = service.findAll()
                .stream()
                .map(presenter::toResponse)
                .toList();

        log.info("Active labors found: {}", response.size());

        return response;
    }

    public LaborResponse findById(String id) {
        log.info("Searching labor by id");

        LaborResponse response = presenter.toResponse(service.findById(id));

        log.info("Labor found by id");

        return response;
    }

    public LaborResponse create(LaborRequest request) {
        log.info("Creating labor");

        LaborResponse response = presenter.toResponse(
                service.create(mapper.toEntity(request))
        );

        log.info("Labor created successfully");

        return response;
    }

    public LaborResponse update(String id, LaborRequest request) {
        log.info("Updating labor");

        LaborResponse response = presenter.toResponse(
                service.update(id, mapper.toEntity(request))
        );

        log.info("Labor updated successfully");

        return response;
    }

    public void delete(String id) {
        log.info("Deleting labor");

        service.delete(id);

        log.info("Labor deleted successfully");
    }
}