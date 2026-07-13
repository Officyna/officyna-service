package br.com.officyna.administrative.customer.domain.repository;

import br.com.officyna.administrative.customer.domain.entity.Customer;

import java.util.List;
import java.util.Optional;

/**
 * Interface de repositório pura para CustomerEntity.
 * Sem qualquer dependência de Spring Data ou MongoDB.
 */
public interface CustomerRepository{
    Optional<Customer> findByDocument(String document);
    boolean existsByDocument(String document);
    List<Customer> findByActiveTrue();
    Customer save(Customer entity);
    Optional<Customer> findById(String id);
    List<Customer> findAll();
    void deleteById(String id);
    boolean existsById(String id);
}

