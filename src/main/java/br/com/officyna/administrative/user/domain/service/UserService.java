package br.com.officyna.administrative.user.domain.service;

import br.com.officyna.administrative.user.domain.entity.User;
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
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<User> findAll() {
        return repository.findByActiveTrue();
    }

    public User findById(String id) {
        return findEntityById(id);
    }

    public User findByEmail(String email) {
        return repository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado com este email: " + email));
    }

    public User create(User user) {
        validateAdminOrManager();
        Optional<User> userExist = repository.findByEmail(user.getEmail());
        if (userExist.isPresent() && Boolean.TRUE.equals(userExist.get().getActive())) {
            throw new DomainException("Já existe um usuário com este email: " + user.getEmail());
        }
        user.setId(userExist.map(User::getId).orElse(null));
        user.setEmail(normalizeEmail(user));
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return repository.save(user);
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

    public User update(String id, User changes) {
        User entity = findEntityById(id);

        boolean emailChanged = !entity.getEmail().equals(changes.getEmail());
        if (emailChanged && repository.existsByEmail(changes.getEmail())) {
            throw new DomainException("Já existe um usário com este email: " + changes.getEmail());
        }

        entity.setName(changes.getName());
        entity.setEmail(changes.getEmail());
        entity.setUserRole(changes.getUserRole());
        return repository.save(entity);
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