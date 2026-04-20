package br.com.officyna.serviceorder.api.controller;

import br.com.officyna.serviceorder.api.ServiceOrderApi;
import br.com.officyna.serviceorder.api.resources.*;
import br.com.officyna.serviceorder.domain.enums.ServiceOrderStatus;
import br.com.officyna.serviceorder.domain.service.ServiceOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ServiceOrderController implements ServiceOrderApi {

    private final ServiceOrderService service;

    @Override
    public ResponseEntity<List<ServiceOrderResponse>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @Override
    public ResponseEntity<ServiceOrderResponse> findById(String id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @Override
    public ResponseEntity<ServiceOrderResponse> findByServiceOrderNumber(Long serviceOrderNumber) {
        return ResponseEntity.ok(service.findByServiceOrderNumber(serviceOrderNumber));
    }

    @Override
    public ResponseEntity<ServiceOrderResponse> createServiceOrder(@RequestBody NewServiceOrderRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createServiceOrder(request));
    }

    @Override
    public ResponseEntity<ServiceOrderResponse> updateServiceOrder(String id, ExistServiceOrderRequest request) {
        return ResponseEntity.ok(service.updateServiceOrder(id, request));
    }

    @Override
    public ResponseEntity<ServiceOrderResponse> addLaborInServiceOrder(String id, List<LaborsRequest> laborsIdList) {
        return ResponseEntity.ok(service.addLaborsInServiceOrder(id, laborsIdList));
    }

    @Override
    public ResponseEntity<ServiceOrderResponse> removeLaborFromServiceOrder(String id, String laborId) {
        return ResponseEntity.ok(service.removeLaborFromServiceOrder(id, laborId));
    }

    @Override
    public ResponseEntity<ServiceOrderResponse> addSupplyInServiceOrder(String id, List<SupplysRequest> supplyIdList) {
        return ResponseEntity.ok(service.addSupplyFromServiceOrder(id, supplyIdList));
    }

    @Override
    public ResponseEntity<ServiceOrderResponse> removeSupplyFromServiceOrder(String id, String supplyId) {
        return ResponseEntity.ok(service.removeSupplyFromServiceOrder(id, supplyId));
    }

    @Override
    public ResponseEntity<Void> deleteServiceOrder(String id) {
        service.deleteServiceOrder(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<ServiceOrderResponse> startLabor(String id, String laborId) {
        return ResponseEntity.ok(service.startLabor(id, laborId));
    }

    @Override
    public ResponseEntity<ServiceOrderResponse> finishLabor(String id, String laborId) {
        return ResponseEntity.ok(service.finishLabor(id, laborId));
    }

    @Override
    public ResponseEntity<ServiceOrderResponse> updateStatus(String id, ServiceOrderStatus status) {
        return ResponseEntity.ok(service.updateStatus(id, status));
    }
}
