package br.com.officyna.infrastructure.persistence.mongodb.gateway;

import br.com.officyna.administrative.vehicle.domain.entity.Vehicle;
import br.com.officyna.administrative.vehicle.domain.repository.VehicleRepository;
import br.com.officyna.infrastructure.persistence.mapper.VehicleEntityDocumentMapper;
import br.com.officyna.infrastructure.persistence.mongodb.repository.VehicleMongoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class VehicleRepositoryGateway implements VehicleRepository {

    private final VehicleMongoRepository mongoRepository;
    private final VehicleEntityDocumentMapper mapper;

    @Override
    public Vehicle save(Vehicle entity) {
        try {
            var document = mapper.toDocument(entity);
            var saved = mongoRepository.save(document);

            return mapper.toEntity(saved);

        } catch (Exception e) {
            log.error(
                    "MongoDB operation failed operation=save repository=VehicleRepositoryGateway",
                    e
            );
            throw e;
        }
    }

    @Override
    public Optional<Vehicle> findById(String id) {
        try {
            return mongoRepository.findById(id)
                    .map(mapper::toEntity);

        } catch (Exception e) {
            log.error(
                    "MongoDB operation failed operation=findById repository=VehicleRepositoryGateway",
                    e
            );
            throw e;
        }
    }

    @Override
    public List<Vehicle> findAll() {
        try {
            return mongoRepository.findAll()
                    .stream()
                    .map(mapper::toEntity)
                    .toList();

        } catch (Exception e) {
            log.error(
                    "MongoDB operation failed operation=findAll repository=VehicleRepositoryGateway",
                    e
            );
            throw e;
        }
    }

    @Override
    public void deleteById(String id) {
        try {
            mongoRepository.deleteById(id);

        } catch (Exception e) {
            log.error(
                    "MongoDB operation failed operation=deleteById repository=VehicleRepositoryGateway",
                    e
            );
            throw e;
        }
    }

    @Override
    public boolean existsById(String id) {
        try {
            return mongoRepository.existsById(id);

        } catch (Exception e) {
            log.error(
                    "MongoDB operation failed operation=existsById repository=VehicleRepositoryGateway",
                    e
            );
            throw e;
        }
    }

    @Override
    public Optional<Vehicle> findByPlate(String plate) {
        try {
            return mongoRepository.findByPlate(plate)
                    .map(mapper::toEntity);

        } catch (Exception e) {
            log.error(
                    "MongoDB operation failed operation=findByPlate repository=VehicleRepositoryGateway",
                    e
            );
            throw e;
        }
    }

    @Override
    public boolean existsByPlate(String plate) {
        try {
            return mongoRepository.existsByPlate(plate);

        } catch (Exception e) {
            log.error(
                    "MongoDB operation failed operation=existsByPlate repository=VehicleRepositoryGateway",
                    e
            );
            throw e;
        }
    }

    @Override
    public List<Vehicle> findByCustomerId(String customerId) {
        try {
            return mongoRepository.findByCustomerId(customerId)
                    .stream()
                    .map(mapper::toEntity)
                    .toList();

        } catch (Exception e) {
            log.error(
                    "MongoDB operation failed operation=findByCustomerId repository=VehicleRepositoryGateway",
                    e
            );
            throw e;
        }
    }

    @Override
    public List<Vehicle> findByActiveTrue() {
        try {
            return mongoRepository.findByActiveTrue()
                    .stream()
                    .map(mapper::toEntity)
                    .toList();

        } catch (Exception e) {
            log.error(
                    "MongoDB operation failed operation=findByActiveTrue repository=VehicleRepositoryGateway",
                    e
            );
            throw e;
        }
    }
}