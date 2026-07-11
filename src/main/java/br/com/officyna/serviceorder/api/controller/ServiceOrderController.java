package br.com.officyna.serviceorder.api.controller;

import br.com.officyna.serviceorder.api.ServiceOrderApi;
import br.com.officyna.serviceorder.api.resources.*;
import br.com.officyna.serviceorder.domain.controller.ServiceOrderControllerAdapter;
import br.com.officyna.serviceorder.domain.enums.ServiceOrderStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ServiceOrderController implements ServiceOrderApi {

    private final ServiceOrderControllerAdapter serviceOrderControllerAdapter;

    @Override
    public ResponseEntity<List<ServiceOrderResponse>> findAll() {
        return ResponseEntity.ok(serviceOrderControllerAdapter.findAll());
    }

    @Override
    public ResponseEntity<ServiceOrderResponse> findById(String id) {
        return ResponseEntity.ok(serviceOrderControllerAdapter.findById(id));
    }

    @Override
    public ResponseEntity<ServiceOrderResponse> findByServiceOrderNumber(Long serviceOrderNumber) {
        return ResponseEntity.ok(serviceOrderControllerAdapter.findByServiceOrderNumber(serviceOrderNumber));
    }

    @Override
    public ResponseEntity<ServiceOrderResponse> createServiceOrder(@RequestBody NewServiceOrderRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(serviceOrderControllerAdapter.createServiceOrder(request));
    }

    @Override
    public ResponseEntity<ServiceOrderResponse> updateServiceOrder(String id, ExistServiceOrderRequest request) {
        return ResponseEntity.ok(serviceOrderControllerAdapter.updateServiceOrder(id, request));
    }

    @Override
    public ResponseEntity<ServiceOrderResponse> addLaborInServiceOrder(String id, List<LaborsRequest> laborsIdList) {
        return ResponseEntity.ok(serviceOrderControllerAdapter.addLaborInServiceOrder(id, laborsIdList));
    }

    @Override
    public ResponseEntity<ServiceOrderResponse> removeLaborFromServiceOrder(String id, String laborId) {
        return ResponseEntity.ok(serviceOrderControllerAdapter.removeLaborFromServiceOrder(id, laborId));
    }

    @Override
    public ResponseEntity<ServiceOrderResponse> addSupplyInServiceOrder(String id, List<SupplysRequest> supplyIdList) {
        return ResponseEntity.ok(serviceOrderControllerAdapter.addSupplyInServiceOrder(id, supplyIdList));
    }

    @Override
    public ResponseEntity<ServiceOrderResponse> removeSupplyFromServiceOrder(String id, String supplyId) {
        return ResponseEntity.ok(serviceOrderControllerAdapter.removeSupplyFromServiceOrder(id, supplyId));
    }

    @Override
    public ResponseEntity<Void> deleteServiceOrder(String id) {
        serviceOrderControllerAdapter.deleteServiceOrder(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<ServiceOrderResponse> startLabor(String id, String laborId) {
        return ResponseEntity.ok(serviceOrderControllerAdapter.startLabor(id, laborId));
    }

    @Override
    public ResponseEntity<ServiceOrderResponse> finishLabor(String id, String laborId) {
        return ResponseEntity.ok(serviceOrderControllerAdapter.finishLabor(id, laborId));
    }

    @Override
    public ResponseEntity<ServiceOrderResponse> updateStatus(String id, ServiceOrderStatus status) {
        return ResponseEntity.ok(serviceOrderControllerAdapter.updateStatus(id, status));
    }

    @Override
    public ResponseEntity<SendToCustomerResponse> sendToCustomer(String id) {
        return ResponseEntity.ok(serviceOrderControllerAdapter.sendToCustomer(id));
    }
}