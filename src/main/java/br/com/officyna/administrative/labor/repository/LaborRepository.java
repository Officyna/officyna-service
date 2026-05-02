package br.com.officyna.administrative.labor.repository;

import br.com.officyna.administrative.labor.domain.LaborEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LaborRepository extends MongoRepository<LaborEntity, String> {

    Optional<LaborEntity> findByName(String name);

    boolean existsByName(String name);

    List<LaborEntity> findByActiveTrue();
}
