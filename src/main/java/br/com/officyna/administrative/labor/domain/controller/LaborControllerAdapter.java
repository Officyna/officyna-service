package br.com.officyna.administrative.labor.domain.controller;

import br.com.officyna.administrative.labor.api.resources.LaborRequest;
import br.com.officyna.administrative.labor.api.resources.LaborResponse;
import br.com.officyna.administrative.labor.domain.mapper.LaborMapper;
import br.com.officyna.administrative.labor.domain.presenter.LaborPresenter;
import br.com.officyna.administrative.labor.domain.service.LaborService;

import java.util.List;

public class LaborControllerAdapter {

    private final LaborService service;
    private final LaborMapper mapper;
    private final LaborPresenter presenter;

    public LaborControllerAdapter(LaborService service, LaborMapper mapper, LaborPresenter presenter) {
        this.service = service;
        this.mapper = mapper;
        this.presenter = presenter;
    }

    public List<LaborResponse> findAll() {
        return service.findAll()
                .stream()
                .map(presenter::toResponse)
                .toList();
    }

    public LaborResponse findById(String id) {
        return presenter.toResponse(service.findById(id));
    }

    public LaborResponse create(LaborRequest request) {
        return presenter.toResponse(service.create(mapper.toEntity(request)));
    }

    public LaborResponse update(String id, LaborRequest request) {
        return presenter.toResponse(service.update(id, mapper.toEntity(request)));
    }

    public void delete(String id) {
        service.delete(id);
    }
}