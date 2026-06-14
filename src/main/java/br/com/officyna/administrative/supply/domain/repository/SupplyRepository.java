package br.com.officyna.administrative.supply.domain.repository;

import br.com.officyna.administrative.supply.domain.entity.Supply;
import br.com.officyna.administrative.supply.domain.entity.SupplyType;
import br.com.officyna.infrastructure.persistence.repository.IRepository;

import java.util.List;

/**
 * Interface de repositório pura para SupplyEntity.
 * Sem qualquer dependência de Spring Data ou MongoDB.
 */
public interface SupplyRepository extends IRepository<Supply, String> {
    boolean existsByName(String name);
    List<Supply> findByActiveTrue();
    List<Supply> findByActiveTrueAndType(SupplyType type);
}

