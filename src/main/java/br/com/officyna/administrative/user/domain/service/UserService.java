package br.com.officyna.administrative.user.domain.service;

import br.com.officyna.administrative.user.domain.entity.User;
import br.com.officyna.administrative.user.domain.repository.UserRepository;
import br.com.officyna.administrative.user.domain.exception.UserBusinessException;
import br.com.officyna.administrative.user.domain.exception.UserNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

@Slf4j
public class UserService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<User> findAll() {
        log.info("Finding all active users");

        List<User> users = repository.findByActiveTrue();

        log.info("Active users found: {}", users.size());

        return users;
    }

    public User findById(String id) {
        log.info("Finding user by id: {}", id);

        User user = findEntityById(id);

        log.info("User found by id: {}", id);

        return user;
    }

    public User findByEmail(String email) {
        log.info("Finding user by email: {}", email);

        User user = repository.findByEmail(email)
                .orElseThrow(() ->
                        new UserNotFoundException(
                                "Usuário não encontrado com este email: " + email
                        )
                );

        log.info("User found by email: {}", email);

        return user;
    }

    public User create(User user) {
        log.info("Creating user with email: {}", user.getEmail());

        validateAdminOrManager();

        Optional<User> userExist = repository.findByEmail(user.getEmail());

        if (userExist.isPresent() && Boolean.TRUE.equals(userExist.get().getActive())) {
            log.warn("User creation failed. Email already registered: {}", user.getEmail());

            throw new UserBusinessException(
                    "Já existe um usuário com este email: " + user.getEmail()
            );
        }

        user.setId(userExist.map(User::getId).orElse(null));
        user.setEmail(normalizeEmail(user));
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        User savedUser = repository.save(user);

        log.info("User created successfully with id: {}", savedUser.getId());

        return savedUser;
    }

    private void validateAdminOrManager() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        assert auth != null;

        boolean hasPermission = auth.getAuthorities().stream()
                .anyMatch(a ->
                        Objects.equals(a.getAuthority(), "ROLE_ADMIN")
                                || Objects.equals(a.getAuthority(), "ROLE_MANAGER")
                );

        if (!hasPermission) {
            log.warn("User creation denied. User does not have ADMIN or MANAGER permission");

            throw new UserBusinessException(
                    "Apenas ADMIN ou MANAGER podem criar usuários internos."
            );
        }

        log.debug("User creation permission validated");
    }

    private static @NonNull String normalizeEmail(User entity) {
        return entity.getEmail()
                .toLowerCase(Locale.ROOT)
                .trim();
    }

    public User update(String id, User changes) {
        log.info("Updating user with id: {}", id);

        User entity = findEntityById(id);

        boolean emailChanged = !entity.getEmail().equals(changes.getEmail());

        if (emailChanged && repository.existsByEmail(changes.getEmail())) {
            log.warn(
                    "User update failed. Email already registered: {}",
                    changes.getEmail()
            );

            throw new UserBusinessException(
                    "Já existe um usário com este email: " + changes.getEmail()
            );
        }

        entity.setName(changes.getName());
        entity.setEmail(changes.getEmail());
        entity.setUserRole(changes.getUserRole());

        User updatedUser = repository.save(entity);

        log.info("User updated successfully with id: {}", id);

        return updatedUser;
    }

    public void delete(String id) {
        log.info("Deleting user with id: {}", id);

        User entity = findEntityById(id);

        entity.setActive(false);
        repository.save(entity);

        log.info("User deactivated successfully with id: {}", id);
    }

    public User findEntityById(String id) {
        log.debug("Searching user entity by id: {}", id);

        return repository.findById(id)
                .orElseThrow(() -> {
                    log.warn("User not found with id: {}", id);
                    return UserNotFoundException.of(id);
                });
    }
}