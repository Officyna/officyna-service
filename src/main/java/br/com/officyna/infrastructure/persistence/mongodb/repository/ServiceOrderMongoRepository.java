package br.com.officyna.infrastructure.persistence.mongodb.repository;

import br.com.officyna.infrastructure.persistence.mongodb.model.ServiceOrderDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositório MongoDB para ServiceOrderDocument.
 * Interface técnica que estende MongoRepository.
 * Será implementada pelo Spring Data MongoDB.
 */
@Repository
public interface ServiceOrderMongoRepository extends MongoRepository<ServiceOrderDocument, String> {

    @Query("{ 'labors.laborsDetails': { $elemMatch: { 'laborId': ?0, 'startDate': { $ne: null }, 'endDate': { $ne: null } } } }")
    List<ServiceOrderDocument> findByLaborIdWithCompletedExecutions(String laborId);

    Optional<ServiceOrderDocument> findByServiceOrderNumber(Long serviceOrderNumber);

    List<ServiceOrderDocument> findByCustomerId(String id);
}

