package br.com.officyna.administrative.user.domain.controller;

import br.com.officyna.administrative.user.api.resources.UserRequest;
import br.com.officyna.administrative.user.api.resources.UserResponse;
import br.com.officyna.administrative.user.domain.mapper.UserMapper;
import br.com.officyna.administrative.user.domain.presenter.UserPresenter;
import br.com.officyna.administrative.user.domain.service.UserService;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class UserControllerAdapter {

    private final UserService service;
    private final UserMapper mapper;
    private final UserPresenter presenter;

    public UserControllerAdapter(UserService service, UserMapper mapper, UserPresenter presenter) {
        this.service = service;
        this.mapper = mapper;
        this.presenter = presenter;
    }

    public List<UserResponse> findAll() {
        log.info("Searching all active users");

        List<UserResponse> users = service.findAll()
                .stream()
                .map(presenter::toResponse)
                .toList();

        log.info("Active users found: {}", users.size());

        return users;
    }

    public UserResponse findById(String id) {
        log.info("Searching user by id: {}", id);

        UserResponse user = presenter.toResponse(service.findById(id));

        log.info("User found by id: {}", id);

        return user;
    }

    public UserResponse findByEmail(String email) {
        log.info("Searching user by email: {}", email);

        UserResponse user = presenter.toResponse(service.findByEmail(email));

        log.info("User found by email: {}", email);

        return user;
    }

    public UserResponse create(UserRequest request) {
        log.info("Creating user");

        UserResponse user = presenter.toResponse(
                service.create(mapper.toEntity(request))
        );

        log.info("User created successfully");

        return user;
    }

    public UserResponse update(String id, UserRequest request) {
        log.info("Updating user with id: {}", id);

        UserResponse user = presenter.toResponse(
                service.update(id, mapper.toEntity(request))
        );

        log.info("User updated successfully with id: {}", id);

        return user;
    }

    public void delete(String id) {
        log.info("Deleting user with id: {}", id);

        service.delete(id);

        log.info("User deleted successfully with id: {}", id);
    }
}