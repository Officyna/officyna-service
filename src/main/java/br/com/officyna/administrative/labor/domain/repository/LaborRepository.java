package br.com.officyna.administrative.labor.domain.repository;

import br.com.officyna.administrative.labor.domain.entity.Labor;
import br.com.officyna.infrastructure.persistence.repository.IRepository;

import java.util.List;
import java.util.Optional;

/**
 * Interface de repositório pura para LaborEntity.
 * Sem qualquer dependência de Spring Data ou MongoDB.
 */
public interface LaborRepository extends IRepository<Labor, String> {
    Optional<Labor> findByName(String name);
    boolean existsByName(String name);
    List<Labor> findByActiveTrue();
}

