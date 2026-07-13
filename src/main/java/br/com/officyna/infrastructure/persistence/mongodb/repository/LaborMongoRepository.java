package br.com.officyna.infrastructure.persistence.mongodb.repository;

import br.com.officyna.infrastructure.persistence.mongodb.model.LaborDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositório MongoDB para LaborDocument.
 * Interface técnica que estende MongoRepository.
 * Será implementada pelo Spring Data MongoDB.
 */
@Repository
public interface LaborMongoRepository extends MongoRepository<LaborDocument, String> {

    Optional<LaborDocument> findByName(String name);

    boolean existsByName(String name);

    List<LaborDocument> findByActiveTrue();
}

