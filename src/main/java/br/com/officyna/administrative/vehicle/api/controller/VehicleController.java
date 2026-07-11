package br.com.officyna.administrative.vehicle.api.controller;

import br.com.officyna.administrative.vehicle.api.VehicleApi;
import br.com.officyna.administrative.vehicle.api.resources.VehicleRequest;
import br.com.officyna.administrative.vehicle.api.resources.VehicleResponse;
import br.com.officyna.administrative.vehicle.domain.controller.VehicleControllerAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class VehicleController implements VehicleApi {

    private final VehicleControllerAdapter vehicleControllerAdapter;

    @Override
    public ResponseEntity<List<VehicleResponse>> findAll() {
        return ResponseEntity.ok(vehicleControllerAdapter.findAll());
    }

    @Override
    public ResponseEntity<VehicleResponse> findById(String id) {
        return ResponseEntity.ok(vehicleControllerAdapter.findById(id));
    }

    @Override
    public ResponseEntity<List<VehicleResponse>> findByCustomer(String customerId) {
        return ResponseEntity.ok(vehicleControllerAdapter.findByCustomer(customerId));
    }

    @Override
    public ResponseEntity<VehicleResponse> create(VehicleRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(vehicleControllerAdapter.create(request));
    }

    @Override
    public ResponseEntity<VehicleResponse> update(String id, VehicleRequest request) {
        return ResponseEntity.ok(vehicleControllerAdapter.update(id, request));
    }

    @Override
    public ResponseEntity<Void> delete(String id) {
        vehicleControllerAdapter.delete(id);
        return ResponseEntity.noContent().build();
    }
}