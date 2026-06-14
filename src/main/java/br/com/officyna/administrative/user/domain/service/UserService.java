package br.com.officyna.administrative.user.domain.service;

import br.com.officyna.administrative.user.api.resources.UserRequest;
import br.com.officyna.administrative.user.api.resources.UserResponse;
import br.com.officyna.administrative.user.domain.entity.User;
import br.com.officyna.administrative.user.domain.mapper.UserMapper;
import br.com.officyna.administrative.user.domain.repository.UserRepository;
import br.com.officyna.infrastructure.exception.DomainException;
import br.com.officyna.infrastructure.exception.NotFoundException;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

public class UserService {

    private final UserRepository repository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository repository, UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    public List<UserResponse> findAll() {
        return repository.findByActiveTrue()
                .stream()
                .map(userMapper::toResponse)
                .toList();
    }

    public UserResponse findById(String id) {
        return userMapper.toResponse(findEntityById(id));
    }

    public UserResponse findByEmail(String email) {
        return repository.findByEmail(email)
                .map(userMapper::toResponse)
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado com este email: " + email));
    }

    public UserResponse create(UserRequest request) {
        validateAdminOrManager();
        Optional<User> userExist = repository.findByEmail(request.email());
        if (userExist.isPresent() && Boolean.TRUE.equals(userExist.get().getActive())) {
            throw new DomainException("Já existe um usuário com este email: " + request.email());
        }
        User entity = userMapper.toEntity(request);
        entity.setId(userExist.map(User::getId).orElse(null));
        entity.setEmail(normalizeEmail(entity));
        entity.setPassword(passwordEncoder.encode(request.password()));
        return userMapper.toResponse(repository.save(entity));
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

    private static @NonNull String normalizeEmail(User entity) {
        return entity.getEmail().toLowerCase(Locale.ROOT).trim();
    }

    public UserResponse update(String id, UserRequest request) {
        User entity = findEntityById(id);

        boolean emailChanged = !entity.getEmail().equals(request.email());
        if (emailChanged && repository.existsByEmail(request.email())) {
            throw new DomainException("Já existe um usário com este email: " + request.email());
        }

        userMapper.updateEntity(entity, request);
        return userMapper.toResponse(repository.save(entity));
    }

    public void delete(String id) {
        User entity = findEntityById(id);
        entity.setActive(false);
        repository.save(entity);
    }

    public User findEntityById(String id) {
        return repository.findById(id)
                .orElseThrow(() -> NotFoundException.of("User", id));
    }
}
