package br.com.officyna.administrative.customer.domain.controller;

import br.com.officyna.administrative.customer.api.resources.CustomerRequest;
import br.com.officyna.administrative.customer.api.resources.CustomerResponse;
import br.com.officyna.administrative.customer.domain.mapper.CustomerMapper;
import br.com.officyna.administrative.customer.domain.presenter.CustomerPresenter;
import br.com.officyna.administrative.customer.domain.service.CustomerService;

import java.util.List;

public class CustomerControllerAdapter {

    private final CustomerService service;
    private final CustomerMapper mapper;
    private final CustomerPresenter presenter;

    public CustomerControllerAdapter(CustomerService service, CustomerMapper mapper, CustomerPresenter presenter) {
        this.service = service;
        this.mapper = mapper;
        this.presenter = presenter;
    }

    public List<CustomerResponse> findAll() {
        return service.findAll()
                .stream()
                .map(presenter::toResponse)
                .toList();
    }

    public CustomerResponse findById(String id) {
        return presenter.toResponse(service.findById(id));
    }

    public CustomerResponse findByDocument(String document) {
        return presenter.toResponse(service.findByDocument(document));
    }

    public CustomerResponse create(CustomerRequest request) {
        return presenter.toResponse(service.create(mapper.toEntity(request)));
    }

    public CustomerResponse update(String id, CustomerRequest request) {
        return presenter.toResponse(service.update(id, mapper.toEntity(request)));
    }

    public void delete(String id) {
        service.delete(id);
    }
}