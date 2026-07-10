package br.com.officyna.administrative.customer.api.controller;


import br.com.officyna.administrative.customer.api.CustomerApi;
import br.com.officyna.administrative.customer.api.resources.CustomerRequest;
import br.com.officyna.administrative.customer.api.resources.CustomerResponse;
import br.com.officyna.administrative.customer.domain.controller.CustomerControllerAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CustomerController implements CustomerApi {

    private final CustomerControllerAdapter customerControllerAdapter;

    @Override
    public ResponseEntity<List<CustomerResponse>> findAll() {
        return ResponseEntity.ok(customerControllerAdapter.findAll());
    }

    @Override
    public ResponseEntity<CustomerResponse> findById(String id) {
        return ResponseEntity.ok(customerControllerAdapter.findById(id));
    }

    @Override
    public ResponseEntity<CustomerResponse> findByDocument(String document) {
        return ResponseEntity.ok(customerControllerAdapter.findByDocument(document));
    }

    @Override
    public ResponseEntity<CustomerResponse> create(CustomerRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(customerControllerAdapter.create(request));
    }

    @Override
    public ResponseEntity<CustomerResponse> update(String id, CustomerRequest request) {
        return ResponseEntity.ok(customerControllerAdapter.update(id, request));
    }

    @Override
    public ResponseEntity<Void> delete(String id) {
        customerControllerAdapter.delete(id);
        return ResponseEntity.noContent().build();
    }
}