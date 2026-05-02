package br.com.officyna.serviceorder.api.controller;

import br.com.officyna.serviceorder.api.CustomerServiceOrderApi;
import br.com.officyna.serviceorder.api.resources.ModifySituationRequest;
import br.com.officyna.serviceorder.api.resources.ServiceOrderResponse;
import br.com.officyna.serviceorder.domain.enums.ServiceOrderStatus;
import br.com.officyna.serviceorder.domain.service.CustomerServiceOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CustomerServiceOrderController implements CustomerServiceOrderApi {

    private final CustomerServiceOrderService service;

    @Override
    public ResponseEntity<List<ServiceOrderResponse>> findByCustomerDocument(String document, ServiceOrderStatus status) {
        return ResponseEntity.ok(service.findByCustomerDocument(document, status));
    }

    @Override
    public ResponseEntity<ServiceOrderResponse> aprovalLabors(String id, List<ModifySituationRequest> request) {
       return ResponseEntity.ok(service.updateLaborSituation(id, request));
    }
}
