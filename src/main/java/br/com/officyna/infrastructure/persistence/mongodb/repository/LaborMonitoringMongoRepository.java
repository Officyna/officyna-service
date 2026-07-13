package br.com.officyna.infrastructure.persistence.mongodb.repository;

import br.com.officyna.infrastructure.persistence.mongodb.model.LaborMonitoringDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositório MongoDB para LaborMonitoringDocument.
 * Interface técnica que estende MongoRepository.
 * Será implementada pelo Spring Data MongoDB.
 */
@Repository
public interface LaborMonitoringMongoRepository extends MongoRepository<LaborMonitoringDocument, String> {

    Optional<LaborMonitoringDocument> findByLaborId(String laborId);
}

