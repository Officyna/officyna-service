package br.com.officyna.administrative.user.domain.repository;

import br.com.officyna.administrative.user.domain.entity.User;
import br.com.officyna.infrastructure.persistence.repository.IRepository;

import java.util.List;
import java.util.Optional;

/**
 * Interface de repositório pura para UserEntity.
 * Sem qualquer dependência de Spring Data ou MongoDB.
 */
public interface UserRepository extends IRepository<User, String> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    List<User> findByActiveTrue();
}

