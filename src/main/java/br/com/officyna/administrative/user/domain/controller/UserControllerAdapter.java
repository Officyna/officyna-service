package br.com.officyna.administrative.user.domain.controller;

import br.com.officyna.administrative.user.api.resources.UserRequest;
import br.com.officyna.administrative.user.api.resources.UserResponse;
import br.com.officyna.administrative.user.domain.mapper.UserMapper;
import br.com.officyna.administrative.user.domain.presenter.UserPresenter;
import br.com.officyna.administrative.user.domain.service.UserService;

import java.util.List;

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
        return service.findAll()
                .stream()
                .map(presenter::toResponse)
                .toList();
    }

    public UserResponse findById(String id) {
        return presenter.toResponse(service.findById(id));
    }

    public UserResponse findByEmail(String email) {
        return presenter.toResponse(service.findByEmail(email));
    }

    public UserResponse create(UserRequest request) {
        return presenter.toResponse(service.create(mapper.toEntity(request)));
    }

    public UserResponse update(String id, UserRequest request) {
        return presenter.toResponse(service.update(id, mapper.toEntity(request)));
    }

    public void delete(String id) {
        service.delete(id);
    }
}