package br.com.officyna.monitoring.repository;

import br.com.officyna.monitoring.domain.entity.LaborMonitoringEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LaborMonitoringRepository extends MongoRepository<LaborMonitoringEntity, String> {

    Optional<LaborMonitoringEntity> findByLaborId(String laborId);
}