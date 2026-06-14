package br.com.officyna.infrastructure.persistence.mongodb.repository;

import br.com.officyna.infrastructure.persistence.mongodb.model.VehicleDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositório MongoDB para VehicleDocument.
 * Interface técnica que estende MongoRepository.
 * Será implementada pelo Spring Data MongoDB.
 */
@Repository
public interface VehicleMongoRepository extends MongoRepository<VehicleDocument, String> {

    Optional<VehicleDocument> findByPlate(String plate);

    boolean existsByPlate(String plate);

    List<VehicleDocument> findByCustomerId(String customerId);

    List<VehicleDocument> findByActiveTrue();
}

