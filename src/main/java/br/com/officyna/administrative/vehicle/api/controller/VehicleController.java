package br.com.officyna.administrative.vehicle.api.controller;

import br.com.officyna.administrative.vehicle.api.VehicleApi;
import br.com.officyna.administrative.vehicle.api.resources.VehicleRequest;
import br.com.officyna.administrative.vehicle.api.resources.VehicleResponse;
import br.com.officyna.administrative.vehicle.domain.service.VehicleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class VehicleController implements VehicleApi {

    private final VehicleService vehicleService;

    @Override
    public ResponseEntity<List<VehicleResponse>> findAll() {
        return ResponseEntity.ok(vehicleService.findAll());
    }

    @Override
    public ResponseEntity<VehicleResponse> findById(String id) {
        return ResponseEntity.ok(vehicleService.findById(id));
    }

    @Override
    public ResponseEntity<List<VehicleResponse>> findByCustomer(String customerId) {
        return ResponseEntity.ok(vehicleService.findByCustomer(customerId));
    }

    @Override
    public ResponseEntity<VehicleResponse> create(VehicleRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(vehicleService.create(request));
    }

    @Override
    public ResponseEntity<VehicleResponse> update(String id, VehicleRequest request) {
        return ResponseEntity.ok(vehicleService.update(id, request));
    }

    @Override
    public ResponseEntity<Void> delete(String id) {
        vehicleService.delete(id);
        return ResponseEntity.noContent().build();
    }
}