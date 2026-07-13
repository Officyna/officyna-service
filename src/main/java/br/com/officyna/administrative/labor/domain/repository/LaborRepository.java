package br.com.officyna.administrative.labor.domain.repository;

import br.com.officyna.administrative.labor.domain.entity.Labor;

import java.util.List;
import java.util.Optional;

/**
 * Interface de repositório pura para LaborEntity.
 * Sem qualquer dependência de Spring Data ou MongoDB.
 */
public interface LaborRepository{
    Optional<Labor> findByName(String name);
    boolean existsByName(String name);
    List<Labor> findByActiveTrue();
    Labor save(Labor entity);
    Optional<Labor> findById(String id);
    List<Labor> findAll();
    void deleteById(String id);
    boolean existsById(String id);
}

