package br.com.officyna.infrastructure.persistence.mongodb.gateway;

import br.com.officyna.administrative.vehicle.domain.entity.Vehicle;
import br.com.officyna.administrative.vehicle.domain.repository.VehicleRepository;
import br.com.officyna.infrastructure.persistence.mapper.VehicleEntityDocumentMapper;
import br.com.officyna.infrastructure.persistence.mongodb.model.VehicleDocument;
import br.com.officyna.infrastructure.persistence.mongodb.repository.VehicleMongoRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Gateway de persistência para VehicleEntity.
 * Implementa a interface de repositório puro do domínio.
 * Usa MongoRepository para acessar o MongoDB e realiza conversão de dados.
 */
@Component
public class VehicleRepositoryGateway
        extends AbstractMongoRepositoryGateway<Vehicle, VehicleDocument, VehicleMongoRepository>
        implements VehicleRepository {

    public VehicleRepositoryGateway(VehicleMongoRepository mongoRepository, VehicleEntityDocumentMapper mapper) {
        super(mongoRepository, mapper::toDocument, mapper::toEntity);
    }

    @Override
    public Optional<Vehicle> findByPlate(String plate) {
        return execute("findByPlate", () -> mongoRepository.findByPlate(plate).map(toEntity));
    }

    @Override
    public boolean existsByPlate(String plate) {
        return execute("existsByPlate", () -> mongoRepository.existsByPlate(plate));
    }

    @Override
    public List<Vehicle> findByCustomerId(String customerId) {
        return execute("findByCustomerId", () -> mongoRepository.findByCustomerId(customerId)
                .stream()
                .map(toEntity)
                .toList());
    }

    @Override
    public List<Vehicle> findByActiveTrue() {
        return execute("findByActiveTrue", () -> mongoRepository.findByActiveTrue()
                .stream()
                .map(toEntity)
                .toList());
    }
}
