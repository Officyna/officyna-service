package br.com.officyna.administrative.labor.api.controller;

import br.com.officyna.administrative.labor.api.LaborApi;
import br.com.officyna.administrative.labor.api.resources.LaborRequest;
import br.com.officyna.administrative.labor.api.resources.LaborResponse;
import br.com.officyna.administrative.labor.domain.controller.LaborControllerAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class LaborController implements LaborApi {

    private final LaborControllerAdapter laborControllerAdapter;

    @Override
    public ResponseEntity<List<LaborResponse>> findAll() {
        return ResponseEntity.ok(laborControllerAdapter.findAll());
    }

    @Override
    public ResponseEntity<LaborResponse> findById(String id) {
        return ResponseEntity.ok(laborControllerAdapter.findById(id));
    }

    @Override
    public ResponseEntity<LaborResponse> create(LaborRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(laborControllerAdapter.create(request));
    }

    @Override
    public ResponseEntity<LaborResponse> update(String id, LaborRequest request) {
        return ResponseEntity.ok(laborControllerAdapter.update(id, request));
    }

    @Override
    public ResponseEntity<Void> delete(String id) {
        laborControllerAdapter.delete(id);
        return ResponseEntity.noContent().build();
    }
}