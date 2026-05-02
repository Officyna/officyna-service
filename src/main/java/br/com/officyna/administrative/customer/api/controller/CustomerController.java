package br.com.officyna.administrative.customer.api.controller;


import br.com.officyna.administrative.customer.api.CustomerApi;
import br.com.officyna.administrative.customer.api.resources.CustomerRequest;
import br.com.officyna.administrative.customer.api.resources.CustomerResponse;
import br.com.officyna.administrative.customer.domain.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CustomerController implements CustomerApi {

    private final CustomerService customerService;

    @Override
    public ResponseEntity<List<CustomerResponse>> findAll() {
        return ResponseEntity.ok(customerService.findAll());
    }

    @Override
    public ResponseEntity<CustomerResponse> findById(String id) {
        return ResponseEntity.ok(customerService.findById(id));
    }

    @Override
    public ResponseEntity<CustomerResponse> findByDocument(String document) {
        return ResponseEntity.ok(customerService.findByDocument(document));
    }

    @Override
    public ResponseEntity<CustomerResponse> create(CustomerRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(customerService.create(request));
    }

    @Override
    public ResponseEntity<CustomerResponse> update(String id, CustomerRequest request) {
        return ResponseEntity.ok(customerService.update(id, request));
    }

    @Override
    public ResponseEntity<Void> delete(String id) {
        customerService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
