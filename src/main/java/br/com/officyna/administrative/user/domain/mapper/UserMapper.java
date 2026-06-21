package br.com.officyna.administrative.user.domain.mapper;

import br.com.officyna.administrative.user.api.resources.UserRequest;
import br.com.officyna.administrative.user.api.resources.UserResponse;
import br.com.officyna.administrative.user.domain.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User toEntity(UserRequest request) {
        return User.builder()
                .name(request.name())
                .email(request.email())
                .password(request.password())
                .userRole(request.userRole())
                .active(true)
                .build();
    }

    public UserResponse toResponse(User entity) {
        return UserResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .email(entity.getEmail())
                .userRole(entity.getUserRole())
                .active(entity.getActive())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public void updateEntity(User entity, UserRequest request) {
        entity.setName(request.name());
        entity.setEmail(request.email());
        entity.setUserRole(request.userRole());
    }
}
