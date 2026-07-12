package br.com.officyna.serviceorder.domain.controller;

import br.com.officyna.serviceorder.api.resources.*;
import br.com.officyna.serviceorder.domain.enums.ServiceOrderStatus;
import br.com.officyna.serviceorder.domain.presenter.ServiceOrderPresenter;
import br.com.officyna.serviceorder.domain.service.ServiceOrderService;

import java.util.List;

public class ServiceOrderControllerAdapter {

    private final ServiceOrderService service;
    private final ServiceOrderPresenter presenter;

    public ServiceOrderControllerAdapter(ServiceOrderService service, ServiceOrderPresenter presenter) {
        this.service = service;
        this.presenter = presenter;
    }

    public List<ServiceOrderResponse> findAll() {
        return service.findAll()
                .stream()
                .map(presenter::toResponse)
                .toList();
    }

    public ServiceOrderResponse findById(String id) {
        return presenter.toResponse(service.findById(id));
    }

    public ServiceOrderResponse findByServiceOrderNumber(Long serviceOrderNumber) {
        return presenter.toResponse(service.findByServiceOrderNumber(serviceOrderNumber));
    }

    public ServiceOrderResponse createServiceOrder(NewServiceOrderRequest request) {
        return presenter.toResponse(service.createServiceOrder(request));
    }

    public ServiceOrderResponse updateServiceOrder(String id, ExistServiceOrderRequest request) {
        return presenter.toResponse(service.updateServiceOrder(id, request));
    }

    public ServiceOrderResponse addLaborInServiceOrder(String id, List<LaborsRequest> laborsIdList) {
        return presenter.toResponse(service.addLaborsInServiceOrder(id, laborsIdList));
    }

    public ServiceOrderResponse removeLaborFromServiceOrder(String id, String laborId) {
        return presenter.toResponse(service.removeLaborFromServiceOrder(id, laborId));
    }

    public ServiceOrderResponse addSupplyInServiceOrder(String id, List<SupplysRequest> supplyIdList) {
        return presenter.toResponse(service.addSupplyFromServiceOrder(id, supplyIdList));
    }

    public ServiceOrderResponse removeSupplyFromServiceOrder(String id, String supplyId) {
        return presenter.toResponse(service.removeSupplyFromServiceOrder(id, supplyId));
    }

    public void deleteServiceOrder(String id) {
        service.deleteServiceOrder(id);
    }

    public ServiceOrderResponse startLabor(String id, String laborId) {
        return presenter.toResponse(service.startLabor(id, laborId));
    }

    public ServiceOrderResponse finishLabor(String id, String laborId) {
        return presenter.toResponse(service.finishLabor(id, laborId));
    }

    public ServiceOrderResponse updateStatus(String id, ServiceOrderStatus status) {
        return presenter.toResponse(service.updateStatus(id, status));
    }

    public SendToCustomerResponse sendToCustomer(String id) {
        service.sendToCustomer(id);
        return new SendToCustomerResponse("Ordem de serviço enviada para o cliente");
    }
}