package br.com.officyna.administrative.customer.domain.repository;

import br.com.officyna.administrative.customer.domain.CustomerEntity;
import br.com.officyna.infrastructure.persistence.repository.IRepository;

import java.util.List;
import java.util.Optional;

/**
 * Interface de repositório pura para CustomerEntity.
 * Sem qualquer dependência de Spring Data ou MongoDB.
 */
public interface ICustomerRepository extends IRepository<CustomerEntity, String> {
    Optional<CustomerEntity> findByDocument(String document);
    boolean existsByDocument(String document);
    List<CustomerEntity> findByActiveTrue();
}

