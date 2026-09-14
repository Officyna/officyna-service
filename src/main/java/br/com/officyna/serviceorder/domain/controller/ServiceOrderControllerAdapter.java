package br.com.officyna.serviceorder.domain.controller;

import br.com.officyna.serviceorder.api.resources.*;
import br.com.officyna.serviceorder.domain.enums.ServiceOrderStatus;
import br.com.officyna.serviceorder.domain.presenter.ServiceOrderPresenter;
import br.com.officyna.serviceorder.domain.service.ServiceOrderService;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class ServiceOrderControllerAdapter {

    private final ServiceOrderService service;
    private final ServiceOrderPresenter presenter;

    public ServiceOrderControllerAdapter(ServiceOrderService service, ServiceOrderPresenter presenter) {
        this.service = service;
        this.presenter = presenter;
    }

    public List<ServiceOrderResponse> findAll() {
        log.info("Searching all service orders");

        List<ServiceOrderResponse> result = service.findAll()
                .stream()
                .map(presenter::toResponse)
                .toList();

        log.info("Service orders found. count={}", result.size());

        return result;
    }

    public ServiceOrderResponse findById(String id) {
        log.info("Searching service order by id. serviceOrderId={}", id);

        ServiceOrderResponse response = presenter.toResponse(service.findById(id));

        log.info("Service order found by id. serviceOrderId={}", id);

        return response;
    }

    public ServiceOrderResponse findByServiceOrderNumber(Long serviceOrderNumber) {
        log.info("Searching service order by number. serviceOrderNumber={}", serviceOrderNumber);

        ServiceOrderResponse response = presenter.toResponse(
                service.findByServiceOrderNumber(serviceOrderNumber)
        );

        log.info("Service order found by number. serviceOrderNumber={}", serviceOrderNumber);

        return response;
    }

    public ServiceOrderResponse createServiceOrder(NewServiceOrderRequest request) {
        log.info("Creating service order");

        ServiceOrderResponse response = presenter.toResponse(
                service.createServiceOrder(request)
        );

        log.info("Service order created successfully");

        return response;
    }

    public ServiceOrderResponse updateServiceOrder(String id, ExistServiceOrderRequest request) {
        log.info("Updating service order. serviceOrderId={}", id);

        ServiceOrderResponse response = presenter.toResponse(
                service.updateServiceOrder(id, request)
        );

        log.info("Service order updated successfully. serviceOrderId={}", id);

        return response;
    }

    public ServiceOrderResponse addLaborInServiceOrder(String id, List<LaborsRequest> laborsIdList) {
        log.info("Adding labors to service order. serviceOrderId={}, laborCount={}",
                id, laborsIdList.size());

        ServiceOrderResponse response = presenter.toResponse(
                service.addLaborsInServiceOrder(id, laborsIdList)
        );

        log.info("Labors added to service order. serviceOrderId={}", id);

        return response;
    }

    public ServiceOrderResponse removeLaborFromServiceOrder(String id, String laborId) {
        log.info("Removing labor from service order. serviceOrderId={}, laborId={}",
                id, laborId);

        ServiceOrderResponse response = presenter.toResponse(
                service.removeLaborFromServiceOrder(id, laborId)
        );

        log.info("Labor removed from service order. serviceOrderId={}, laborId={}",
                id, laborId);

        return response;
    }

    public ServiceOrderResponse addSupplyInServiceOrder(String id, List<SupplysRequest> supplyIdList) {
        log.info("Adding supplies to service order. serviceOrderId={}, supplyCount={}",
                id, supplyIdList.size());

        ServiceOrderResponse response = presenter.toResponse(
                service.addSupplyFromServiceOrder(id, supplyIdList)
        );

        log.info("Supplies added to service order. serviceOrderId={}", id);

        return response;
    }

    public ServiceOrderResponse removeSupplyFromServiceOrder(String id, String supplyId) {
        log.info("Removing supply from service order. serviceOrderId={}, supplyId={}",
                id, supplyId);

        ServiceOrderResponse response = presenter.toResponse(
                service.removeSupplyFromServiceOrder(id, supplyId)
        );

        log.info("Supply removed from service order. serviceOrderId={}, supplyId={}",
                id, supplyId);

        return response;
    }

    public void deleteServiceOrder(String id) {
        log.info("Deleting service order. serviceOrderId={}", id);

        service.deleteServiceOrder(id);

        log.info("Service order deleted successfully. serviceOrderId={}", id);
    }

    public ServiceOrderResponse startLabor(String id, String laborId) {
        log.info("Starting labor execution. serviceOrderId={}, laborId={}",
                id, laborId);

        ServiceOrderResponse response = presenter.toResponse(
                service.startLabor(id, laborId)
        );

        log.info("Labor execution started. serviceOrderId={}, laborId={}",
                id, laborId);

        return response;
    }

    public ServiceOrderResponse finishLabor(String id, String laborId) {
        log.info("Finishing labor execution. serviceOrderId={}, laborId={}",
                id, laborId);

        ServiceOrderResponse response = presenter.toResponse(
                service.finishLabor(id, laborId)
        );

        log.info("Labor execution finished. serviceOrderId={}, laborId={}",
                id, laborId);

        return response;
    }

    public ServiceOrderResponse updateStatus(String id, ServiceOrderStatus status) {
        log.info("Updating service order status. serviceOrderId={}, status={}",
                id, status);

        ServiceOrderResponse response = presenter.toResponse(
                service.updateStatus(id, status)
        );

        log.info("Service order status updated. serviceOrderId={}, status={}",
                id, status);

        return response;
    }

    public SendToCustomerResponse sendToCustomer(String id) {
        log.info("Sending service order to customer. serviceOrderId={}", id);

        service.sendToCustomer(id);

        log.info("Service order sent to customer successfully. serviceOrderId={}", id);

        return new SendToCustomerResponse("Ordem de serviço enviada para o cliente");
    }

}
