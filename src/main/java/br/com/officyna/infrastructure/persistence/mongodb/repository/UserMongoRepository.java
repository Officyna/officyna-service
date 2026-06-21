package br.com.officyna.infrastructure.persistence.mongodb.repository;

import br.com.officyna.infrastructure.persistence.mongodb.model.UserDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositório MongoDB para UserDocument.
 * Interface técnica que estende MongoRepository.
 * Será implementada pelo Spring Data MongoDB.
 */
@Repository
public interface UserMongoRepository extends MongoRepository<UserDocument, String> {

    Optional<UserDocument> findByEmail(String email);

    boolean existsByEmail(String email);

    List<UserDocument> findByActiveTrue();
}

