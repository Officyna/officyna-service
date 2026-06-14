package br.com.officyna.administrative.user.domain.repository;

import br.com.officyna.administrative.user.domain.UserEntity;
import br.com.officyna.infrastructure.persistence.repository.IRepository;

import java.util.List;
import java.util.Optional;

/**
 * Interface de repositório pura para UserEntity.
 * Sem qualquer dependência de Spring Data ou MongoDB.
 */
public interface IUserRepository extends IRepository<UserEntity, String> {
    Optional<UserEntity> findByEmail(String email);
    boolean existsByEmail(String email);
    List<UserEntity> findByActiveTrue();
}

