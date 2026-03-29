package br.com.officyna.administrative.vehicle.repository;

import br.com.officyna.administrative.vehicle.domain.VehicleEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VehicleRepository extends MongoRepository<VehicleEntity, String> {

    Optional<VehicleEntity> findByPlate(String plate);

    boolean existsByPlate(String plate);

    List<VehicleEntity> findByCustomerId(String customerId);

    List<VehicleEntity> findByActiveTrue();
}