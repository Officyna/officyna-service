package br.com.officyna.administrative.supply.api.controller;

import br.com.officyna.administrative.supply.api.SupplyApi;
import br.com.officyna.administrative.supply.api.resources.SupplyRequest;
import br.com.officyna.administrative.supply.api.resources.SupplyResponse;
import br.com.officyna.administrative.supply.domain.controller.SupplyControllerAdapter;
import br.com.officyna.administrative.supply.domain.entity.SupplyType;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class SupplyController implements SupplyApi {

    private final SupplyControllerAdapter supplyControllerAdapter;

    @Override
    public ResponseEntity<List<SupplyResponse>> findAll() {
        return ResponseEntity.ok(supplyControllerAdapter.findAll());
    }

    @Override
    public ResponseEntity<List<SupplyResponse>> findByType(SupplyType type) {
        return ResponseEntity.ok(supplyControllerAdapter.findByType(type));
    }

    @Override
    public ResponseEntity<SupplyResponse> findById(String id) {
        return ResponseEntity.ok(supplyControllerAdapter.findById(id));
    }

    @Override
    public ResponseEntity<SupplyResponse> create(SupplyRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(supplyControllerAdapter.create(request));
    }

    @Override
    public ResponseEntity<SupplyResponse> update(String id, SupplyRequest request) {
        return ResponseEntity.ok(supplyControllerAdapter.update(id, request));
    }

    @Override
    public ResponseEntity<Void> delete(String id) {
        supplyControllerAdapter.delete(id);
        return ResponseEntity.noContent().build();
    }
}