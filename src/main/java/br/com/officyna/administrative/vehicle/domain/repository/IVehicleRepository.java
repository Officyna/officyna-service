package br.com.officyna.administrative.vehicle.domain.repository;

import br.com.officyna.administrative.vehicle.domain.VehicleEntity;
import br.com.officyna.infrastructure.persistence.repository.IRepository;

import java.util.List;
import java.util.Optional;

/**
 * Interface de repositório pura para VehicleEntity.
 * Sem qualquer dependência de Spring Data ou MongoDB.
 */
public interface IVehicleRepository extends IRepository<VehicleEntity, String> {
    Optional<VehicleEntity> findByPlate(String plate);
    boolean existsByPlate(String plate);
    List<VehicleEntity> findByCustomerId(String customerId);
    List<VehicleEntity> findByActiveTrue();
}

