package br.com.officyna.administrative.supply.domain.repository;

import br.com.officyna.administrative.supply.domain.entity.Supply;
import br.com.officyna.administrative.supply.domain.entity.SupplyType;

import java.util.List;
import java.util.Optional;

/**
 * Interface de repositório pura para SupplyEntity.
 * Sem qualquer dependência de Spring Data ou MongoDB.
 */
public interface SupplyRepository {
    boolean existsByName(String name);
    List<Supply> findByActiveTrue();
    List<Supply> findByActiveTrueAndType(SupplyType type);
    Supply save(Supply entity);
    Optional<Supply> findById(String id);
    List<Supply> findAll();
    void deleteById(String id);
    boolean existsById(String id);
}

