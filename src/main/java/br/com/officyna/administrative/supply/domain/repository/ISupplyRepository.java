package br.com.officyna.administrative.supply.domain.repository;

import br.com.officyna.administrative.supply.domain.SupplyEntity;
import br.com.officyna.administrative.supply.domain.SupplyType;
import br.com.officyna.infrastructure.persistence.repository.IRepository;

import java.util.List;

/**
 * Interface de repositório pura para SupplyEntity.
 * Sem qualquer dependência de Spring Data ou MongoDB.
 */
public interface ISupplyRepository extends IRepository<SupplyEntity, String> {
    boolean existsByName(String name);
    List<SupplyEntity> findByActiveTrue();
    List<SupplyEntity> findByActiveTrueAndType(SupplyType type);
}

