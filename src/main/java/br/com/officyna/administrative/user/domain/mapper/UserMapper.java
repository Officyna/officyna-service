package br.com.officyna.administrative.user.domain.mapper;

import br.com.officyna.administrative.user.api.resources.UserRequest;
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
}