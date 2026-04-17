package br.com.officyna.administrative.supply.repository;

import br.com.officyna.administrative.supply.domain.SupplyEntity;
import br.com.officyna.administrative.supply.domain.SupplyType;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SupplyRepository extends MongoRepository<SupplyEntity, String> {

    boolean existsByName(String name);

    List<SupplyEntity> findByActiveTrue();

    List<SupplyEntity> findByActiveTrueAndType(SupplyType type);
}