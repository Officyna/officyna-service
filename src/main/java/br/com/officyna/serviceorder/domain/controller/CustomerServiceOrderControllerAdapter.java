package br.com.officyna.serviceorder.domain.controller;

import br.com.officyna.serviceorder.api.resources.ModifySituationRequest;
import br.com.officyna.serviceorder.api.resources.ServiceOrderResponse;
import br.com.officyna.serviceorder.domain.enums.ServiceOrderStatus;
import br.com.officyna.serviceorder.domain.presenter.ServiceOrderPresenter;
import br.com.officyna.serviceorder.domain.service.CustomerServiceOrderService;

import java.util.List;

public class CustomerServiceOrderControllerAdapter {

    private final CustomerServiceOrderService service;
    private final ServiceOrderPresenter presenter;

    public CustomerServiceOrderControllerAdapter(CustomerServiceOrderService service, ServiceOrderPresenter presenter) {
        this.service = service;
        this.presenter = presenter;
    }

    public List<ServiceOrderResponse> findByCustomerDocument(String document, ServiceOrderStatus status) {
        return service.findByCustomerDocument(document, status)
                .stream()
                .map(presenter::toResponse)
                .toList();
    }

    public ServiceOrderResponse updateLaborSituation(String id, List<ModifySituationRequest> request) {
        return presenter.toResponse(service.updateLaborSituation(id, request));
    }
}