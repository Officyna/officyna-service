package br.com.officyna.administrative.user.domain.repository;

import br.com.officyna.administrative.user.domain.entity.User;

import java.util.List;
import java.util.Optional;

/**
 * Interface de repositório pura para UserEntity.
 * Sem qualquer dependência de Spring Data ou MongoDB.
 */
public interface UserRepository {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    List<User> findByActiveTrue();
    User save(User entity);
    Optional<User> findById(String id);
    List<User> findAll();
    void deleteById(String id);
    boolean existsById(String id);
}

