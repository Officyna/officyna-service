package br.com.officyna.infrastructure.persistence.mongodb.repository;

import br.com.officyna.infrastructure.persistence.mongodb.model.SupplyDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositório MongoDB para SupplyDocument.
 * Interface técnica que estende MongoRepository.
 * Será implementada pelo Spring Data MongoDB.
 */
@Repository
public interface SupplyMongoRepository extends MongoRepository<SupplyDocument, String> {

    boolean existsByName(String name);

    List<SupplyDocument> findByActiveTrue();

    List<SupplyDocument> findByActiveTrueAndType(String type);
}

