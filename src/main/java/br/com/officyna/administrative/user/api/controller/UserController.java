package br.com.officyna.administrative.user.api.controller;

import br.com.officyna.administrative.user.api.UserApi;
import br.com.officyna.administrative.user.api.resources.UserRequest;
import br.com.officyna.administrative.user.api.resources.UserResponse;
import br.com.officyna.administrative.user.domain.controller.UserControllerAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class UserController implements UserApi {

    private final UserControllerAdapter userControllerAdapter;

    @Override
    public ResponseEntity<List<UserResponse>> findAll() {
        return ResponseEntity.ok(userControllerAdapter.findAll());
    }

    @Override
    public ResponseEntity<UserResponse> findById(String id) {
        return ResponseEntity.ok(userControllerAdapter.findById(id));
    }

    @Override
    public ResponseEntity<UserResponse> findByEmail(String email) {
        return ResponseEntity.ok(userControllerAdapter.findByEmail(email));
    }

    @Override
    public ResponseEntity<UserResponse> create(UserRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userControllerAdapter.create(request));
    }

    @Override
    public ResponseEntity<UserResponse> update(String id, UserRequest request) {
        return ResponseEntity.ok(userControllerAdapter.update(id, request));
    }

    @Override
    public ResponseEntity<Void> delete(String id) {
        userControllerAdapter.delete(id);
        return ResponseEntity.noContent().build();
    }
}