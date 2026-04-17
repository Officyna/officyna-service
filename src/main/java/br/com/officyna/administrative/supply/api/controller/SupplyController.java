package br.com.officyna.administrative.supply.api.controller;

import br.com.officyna.administrative.supply.api.SupplyApi;
import br.com.officyna.administrative.supply.api.resources.SupplyRequest;
import br.com.officyna.administrative.supply.api.resources.SupplyResponse;
import br.com.officyna.administrative.supply.domain.SupplyType;
import br.com.officyna.administrative.supply.domain.service.SupplyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class SupplyController implements SupplyApi {

    private final SupplyService supplyService;

    @Override
    public ResponseEntity<List<SupplyResponse>> findAll() {
        return ResponseEntity.ok(supplyService.findAll());
    }

    @Override
    public ResponseEntity<List<SupplyResponse>> findByType(SupplyType type) {
        return ResponseEntity.ok(supplyService.findByType(type));
    }

    @Override
    public ResponseEntity<SupplyResponse> findById(String id) {
        return ResponseEntity.ok(supplyService.findById(id));
    }

    @Override
    public ResponseEntity<SupplyResponse> create(SupplyRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(supplyService.create(request));
    }

    @Override
    public ResponseEntity<SupplyResponse> update(String id, SupplyRequest request) {
        return ResponseEntity.ok(supplyService.update(id, request));
    }

    @Override
    public ResponseEntity<Void> delete(String id) {
        supplyService.delete(id);
        return ResponseEntity.noContent().build();
    }
}