package br.com.officyna.serviceorder.domain.controller;

import br.com.officyna.serviceorder.api.resources.ModifySituationRequest;
import br.com.officyna.serviceorder.api.resources.ServiceOrderResponse;
import br.com.officyna.serviceorder.domain.enums.ServiceOrderStatus;
import br.com.officyna.serviceorder.domain.presenter.ServiceOrderPresenter;
import br.com.officyna.serviceorder.domain.service.CustomerServiceOrderService;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class CustomerServiceOrderControllerAdapter {

    private final CustomerServiceOrderService service;
    private final ServiceOrderPresenter presenter;

    public CustomerServiceOrderControllerAdapter(CustomerServiceOrderService service, ServiceOrderPresenter presenter) {
        this.service = service;
        this.presenter = presenter;
    }

    public List<ServiceOrderResponse> findByCustomerDocument(String document, ServiceOrderStatus status) {
        log.info("Searching service orders by customer document");

        List<ServiceOrderResponse> result = service.findByCustomerDocument(document, status)
                .stream()
                .map(presenter::toResponse)
                .toList();

        log.info("Service orders found by customer document");

        return result;
    }

    public ServiceOrderResponse updateLaborSituation(String id, List<ModifySituationRequest> request) {
        log.info("Updating labor situation for service order. serviceOrderId={}", id);

        ServiceOrderResponse response = presenter.toResponse(
                service.updateLaborSituation(id, request)
        );

        log.info("Labor situation updated successfully. serviceOrderId={}", id);

        return response;
    }


}
