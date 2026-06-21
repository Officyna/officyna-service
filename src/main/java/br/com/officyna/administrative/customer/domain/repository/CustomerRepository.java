package br.com.officyna.administrative.customer.domain.repository;

import br.com.officyna.administrative.customer.domain.entity.Customer;
import br.com.officyna.infrastructure.persistence.repository.IRepository;

import java.util.List;
import java.util.Optional;

/**
 * Interface de repositório pura para CustomerEntity.
 * Sem qualquer dependência de Spring Data ou MongoDB.
 */
public interface CustomerRepository extends IRepository<Customer, String> {
    Optional<Customer> findByDocument(String document);
    boolean existsByDocument(String document);
    List<Customer> findByActiveTrue();
}

