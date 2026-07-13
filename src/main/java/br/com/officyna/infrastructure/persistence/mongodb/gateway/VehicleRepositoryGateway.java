package br.com.officyna.infrastructure.persistence.mongodb.gateway;

import br.com.officyna.administrative.vehicle.domain.entity.Vehicle;
import br.com.officyna.administrative.vehicle.domain.repository.VehicleRepository;
import br.com.officyna.infrastructure.persistence.mapper.VehicleEntityDocumentMapper;
import br.com.officyna.infrastructure.persistence.mongodb.repository.VehicleMongoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Gateway de persistência para VehicleEntity.
 * Implementa a interface de repositório puro do domínio.
 * Usa MongoRepository para acessar o MongoDB e realiza conversão de dados.
 */
@Component
@RequiredArgsConstructor
public class VehicleRepositoryGateway implements VehicleRepository {

    private final VehicleMongoRepository mongoRepository;
    private final VehicleEntityDocumentMapper mapper;

    @Override
    public Vehicle save(Vehicle entity) {
        var document = mapper.toDocument(entity);
        var saved = mongoRepository.save(document);
        return mapper.toEntity(saved);
    }

    @Override
    public Optional<Vehicle> findById(String id) {
        return mongoRepository.findById(id).map(mapper::toEntity);
    }

    @Override
    public List<Vehicle> findAll() {
        return mongoRepository.findAll()
                .stream()
                .map(mapper::toEntity)
                .toList();
    }

    @Override
    public void deleteById(String id) {
        mongoRepository.deleteById(id);
    }

    @Override
    public boolean existsById(String id) {
        return mongoRepository.existsById(id);
    }

    @Override
    public Optional<Vehicle> findByPlate(String plate) {
        return mongoRepository.findByPlate(plate).map(mapper::toEntity);
    }

    @Override
    public boolean existsByPlate(String plate) {
        return mongoRepository.existsByPlate(plate);
    }

    @Override
    public List<Vehicle> findByCustomerId(String customerId) {
        return mongoRepository.findByCustomerId(customerId)
                .stream()
                .map(mapper::toEntity)
                .toList();
    }

    @Override
    public List<Vehicle> findByActiveTrue() {
        return mongoRepository.findByActiveTrue()
                .stream()
                .map(mapper::toEntity)
                .toList();
    }
}

