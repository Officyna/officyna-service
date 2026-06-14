package br.com.officyna.infrastructure.persistence.repository;

import java.util.List;
import java.util.Optional;

/**
 * Interface genérica para repositórios - pura, sem dependência do Spring Data.
 * Define contrato básico para operações CRUD no domínio.
 */
public interface IRepository<T, ID> {
    T save(T entity);
    Optional<T> findById(ID id);
    List<T> findAll();
    void deleteById(ID id);
    boolean existsById(ID id);
}

