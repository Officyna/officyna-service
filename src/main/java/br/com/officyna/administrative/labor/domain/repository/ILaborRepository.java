package br.com.officyna.administrative.labor.domain.repository;

import br.com.officyna.administrative.labor.domain.LaborEntity;
import br.com.officyna.infrastructure.persistence.repository.IRepository;

import java.util.List;
import java.util.Optional;

/**
 * Interface de repositório pura para LaborEntity.
 * Sem qualquer dependência de Spring Data ou MongoDB.
 */
public interface ILaborRepository extends IRepository<LaborEntity, String> {
    Optional<LaborEntity> findByName(String name);
    boolean existsByName(String name);
    List<LaborEntity> findByActiveTrue();
}

