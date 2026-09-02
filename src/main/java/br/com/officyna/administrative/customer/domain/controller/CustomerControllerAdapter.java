package br.com.officyna.administrative.customer.domain.controller;

import br.com.officyna.administrative.customer.api.resources.CustomerRequest;
import br.com.officyna.administrative.customer.api.resources.CustomerResponse;
import br.com.officyna.administrative.customer.domain.mapper.CustomerMapper;
import br.com.officyna.administrative.customer.domain.presenter.CustomerPresenter;
import br.com.officyna.administrative.customer.domain.service.CustomerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class CustomerControllerAdapter {

    private final CustomerService service;
    private final CustomerMapper mapper;
    private final CustomerPresenter presenter;

    private static final Logger logger =
            LoggerFactory.getLogger(CustomerControllerAdapter.class);

    public CustomerControllerAdapter(
            CustomerService service,
            CustomerMapper mapper,
            CustomerPresenter presenter) {
        this.service = service;
        this.mapper = mapper;
        this.presenter = presenter;
    }

    public List<CustomerResponse> findAll() {
        logger.info("Searching all active customers");

        List<CustomerResponse> response = service.findAll()
                .stream()
                .map(presenter::toResponse)
                .toList();

        logger.info("Active customers found: {}", response.size());

        return response;
    }

    public CustomerResponse findById(String id) {
        logger.info("Searching customer by id: {}", id);

        CustomerResponse response =
                presenter.toResponse(service.findById(id));

        logger.info("Customer found by id: {}", id);

        return response;
    }

    public CustomerResponse findByDocument(String document) {
        logger.info("Searching customer by document");

        CustomerResponse response =
                presenter.toResponse(service.findByDocument(document));

        logger.info("Customer found by document");

        return response;
    }

    public CustomerResponse create(CustomerRequest request) {
        logger.info("Creating customer");

        CustomerResponse response =
                presenter.toResponse(
                        service.create(mapper.toEntity(request))
                );

        logger.info("Customer created successfully");

        return response;
    }

    public CustomerResponse update(String id, CustomerRequest request) {
        logger.info("Updating customer with id: {}", id);

        CustomerResponse response =
                presenter.toResponse(
                        service.update(id, mapper.toEntity(request))
                );

        logger.info("Customer updated successfully with id: {}", id);

        return response;
    }

    public void delete(String id) {
        logger.info("Deactivating customer with id: {}", id);

        service.delete(id);

        logger.info("Customer deactivated successfully with id: {}", id);
    }
}