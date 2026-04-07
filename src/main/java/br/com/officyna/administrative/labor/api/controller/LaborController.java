package br.com.officyna.administrative.labor.api.controller;

import br.com.officyna.administrative.labor.api.LaborApi;
import br.com.officyna.administrative.labor.api.resources.LaborRequest;
import br.com.officyna.administrative.labor.api.resources.LaborResponse;
import br.com.officyna.administrative.labor.domain.service.LaborService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class LaborController implements LaborApi {

    private final LaborService laborService;

    @Override
    public ResponseEntity<List<LaborResponse>> findAll() {
        return ResponseEntity.ok(laborService.findAll());
    }

    @Override
    public ResponseEntity<LaborResponse> findById(String id) {
        return ResponseEntity.ok(laborService.findById(id));
    }

    @Override
    public ResponseEntity<LaborResponse> create(LaborRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(laborService.create(request));
    }

    @Override
    public ResponseEntity<LaborResponse> update(String id, LaborRequest request) {
        return ResponseEntity.ok(laborService.update(id, request));
    }

    @Override
    public ResponseEntity<Void> delete(String id) {
        laborService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
