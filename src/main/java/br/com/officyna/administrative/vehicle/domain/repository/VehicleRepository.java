package br.com.officyna.administrative.vehicle.domain.repository;

import br.com.officyna.administrative.vehicle.domain.entity.Vehicle;

import java.util.List;
import java.util.Optional;

/**
 * Interface de repositório pura para VehicleEntity.
 * Sem qualquer dependência de Spring Data ou MongoDB.
 */
public interface VehicleRepository{
    Optional<Vehicle> findByPlate(String plate);
    boolean existsByPlate(String plate);
    List<Vehicle> findByCustomerId(String customerId);
    List<Vehicle> findByActiveTrue();
    Vehicle save(Vehicle entity);
    Optional<Vehicle> findById(String id);
    List<Vehicle> findAll();
    void deleteById(String id);
    boolean existsById(String id);
}

