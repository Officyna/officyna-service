package br.com.officyna.infrastructure.persistence.mongodb.gateway;

import br.com.officyna.administrative.supply.domain.SupplyEntity;
import br.com.officyna.administrative.supply.domain.SupplyType;
import br.com.officyna.administrative.supply.domain.repository.ISupplyRepository;
import br.com.officyna.infrastructure.persistence.mapper.SupplyEntityDocumentMapper;
import br.com.officyna.infrastructure.persistence.mongodb.repository.SupplyMongoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Gateway de persistência para SupplyEntity.
 * Implementa a interface de repositório puro do domínio.
 * Usa MongoRepository para acessar o MongoDB e realiza conversão de dados.
 */
@Component
@RequiredArgsConstructor
public class SupplyRepositoryGateway implements ISupplyRepository {

    private final SupplyMongoRepository mongoRepository;
    private final SupplyEntityDocumentMapper mapper;

    @Override
    public SupplyEntity save(SupplyEntity entity) {
        var document = mapper.toDocument(entity);
        var saved = mongoRepository.save(document);
        return mapper.toEntity(saved);
    }

    @Override
    public Optional<SupplyEntity> findById(String id) {
        return mongoRepository.findById(id).map(mapper::toEntity);
    }

    @Override
    public List<SupplyEntity> findAll() {
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
    public boolean existsByName(String name) {
        return mongoRepository.existsByName(name);
    }

    @Override
    public List<SupplyEntity> findByActiveTrue() {
        return mongoRepository.findByActiveTrue()
                .stream()
                .map(mapper::toEntity)
                .toList();
    }

    @Override
    public List<SupplyEntity> findByActiveTrueAndType(SupplyType type) {
        return mongoRepository.findByActiveTrueAndType(type.name())
                .stream()
                .map(mapper::toEntity)
                .toList();
    }
}

