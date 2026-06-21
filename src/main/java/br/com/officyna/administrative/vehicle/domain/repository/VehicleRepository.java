package br.com.officyna.administrative.vehicle.domain.repository;

import br.com.officyna.administrative.vehicle.domain.entity.Vehicle;
import br.com.officyna.infrastructure.persistence.repository.IRepository;

import java.util.List;
import java.util.Optional;

/**
 * Interface de repositório pura para VehicleEntity.
 * Sem qualquer dependência de Spring Data ou MongoDB.
 */
public interface VehicleRepository extends IRepository<Vehicle, String> {
    Optional<Vehicle> findByPlate(String plate);
    boolean existsByPlate(String plate);
    List<Vehicle> findByCustomerId(String customerId);
    List<Vehicle> findByActiveTrue();
}

