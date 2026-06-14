package br.com.officyna.infrastructure.persistence.mongodb.repository;

import br.com.officyna.infrastructure.persistence.mongodb.model.CustomerDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositório MongoDB para CustomerDocument.
 * Interface técnica que estende MongoRepository.
 * Será implementada pelo Spring Data MongoDB.
 */
@Repository
public interface CustomerMongoRepository extends MongoRepository<CustomerDocument, String> {

    Optional<CustomerDocument> findByDocument(String document);

    boolean existsByDocument(String document);

    List<CustomerDocument> findByActiveTrue();
}

