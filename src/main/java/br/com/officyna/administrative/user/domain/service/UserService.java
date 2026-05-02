package br.com.officyna.administrative.user.domain.service;

import br.com.officyna.administrative.user.api.resources.UserRequest;
import br.com.officyna.administrative.user.api.resources.UserResponse;
import br.com.officyna.administrative.user.domain.UserEntity;
import br.com.officyna.administrative.user.domain.mapper.UserMapper;
import br.com.officyna.administrative.user.repository.UserRepository;
import br.com.officyna.infrastructure.exception.DomainException;
import br.com.officyna.infrastructure.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

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
        validateAdminOrManager();
        Optional<UserEntity> userExist = userRepository.findByEmail(request.email());
        if (userExist.isPresent() && Boolean.TRUE.equals(userExist.get().getActive())) {
            throw new DomainException("Já existe um usuário com este email: " + request.email());
        }
        UserEntity entity = userMapper.toEntity(request);
        entity.setId(userExist.map(UserEntity::getId).orElse(null));
        entity.setEmail(normalizeEmail(entity));
        entity.setPassword(passwordEncoder.encode(request.password()));
        return userMapper.toResponse(userRepository.save(entity));
    }

    private void validateAdminOrManager() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assert auth != null;
        boolean hasPermission = auth.getAuthorities().stream()
                .anyMatch(a -> Objects.equals(a.getAuthority(), "ROLE_ADMIN") || Objects.equals(a.getAuthority(), "ROLE_MANAGER"));
        if (!hasPermission) {
            throw new DomainException("Apenas ADMIN ou MANAGER podem criar usuários internos.");
        }
    }

    private static @NonNull String normalizeEmail(UserEntity entity) {
        return entity.getEmail().toLowerCase(Locale.ROOT).trim();
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
