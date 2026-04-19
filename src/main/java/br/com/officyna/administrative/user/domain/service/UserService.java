package br.com.officyna.administrative.user.domain.service;

import br.com.officyna.administrative.user.api.resources.UserRequest;
import br.com.officyna.administrative.user.api.resources.UserResponse;
import br.com.officyna.administrative.user.domain.UserEntity;
import br.com.officyna.administrative.user.domain.mapper.UserMapper;
import br.com.officyna.administrative.user.repository.UserRepository;
import br.com.officyna.infrastructure.exception.DomainException;
import br.com.officyna.infrastructure.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public List<UserResponse> findAll() {
        return userRepository.findByActiveTrue()
                .stream()
                .map(userMapper::toResponse)
                .toList();
    }

    public UserResponse findById(String id) {
        return userMapper.toResponse(findEntityById(id));
    }

    public UserResponse findByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(userMapper::toResponse)
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado com este email: " + email));
    }

    public UserResponse create(UserRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new DomainException("Já existe um usuário com este email: " + request.email());
        }
        UserEntity entity = userMapper.toEntity(request);
        return userMapper.toResponse(userRepository.save(entity));
    }

    public UserResponse update(String id, UserRequest request) {
        UserEntity entity = findEntityById(id);

        boolean emailChanged = !entity.getEmail().equals(request.email());
        if (emailChanged && userRepository.existsByEmail(request.email())) {
            throw new DomainException("Já existe um usário com este email: " + request.email());
        }

        userMapper.updateEntity(entity, request);
        return userMapper.toResponse(userRepository.save(entity));
    }

    public void delete(String id) {
        UserEntity entity = findEntityById(id);
        entity.setActive(false);
        userRepository.save(entity);
    }

    public UserEntity findEntityById(String id) {
        return userRepository.findById(id)
                .orElseThrow(() -> NotFoundException.of("User", id));
    }
}
