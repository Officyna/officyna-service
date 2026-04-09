package br.com.officyna.administrative.customer.repository;

import br.com.officyna.administrative.customer.domain.CustomerEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends MongoRepository<CustomerEntity, String> {

    Optional<CustomerEntity> findByDocument(String document);

    boolean existsByDocument(String document);

    List<CustomerEntity> findByActiveTrue();
}