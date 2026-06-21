package br.com.officyna.infrastructure.persistence.mongodb.gateway;

import br.com.officyna.administrative.labor.domain.entity.Labor;
import br.com.officyna.administrative.labor.domain.repository.LaborRepository;
import br.com.officyna.infrastructure.persistence.mapper.LaborEntityDocumentMapper;
import br.com.officyna.infrastructure.persistence.mongodb.repository.LaborMongoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Gateway de persistência para LaborEntity.
 * Implementa a interface de repositório puro do domínio.
 * Usa MongoRepository para acessar o MongoDB e realiza conversão de dados.
 */
@Component
@RequiredArgsConstructor
public class LaborRepositoryGateway implements LaborRepository {

    private final LaborMongoRepository mongoRepository;
    private final LaborEntityDocumentMapper mapper;

    @Override
    public Labor save(Labor entity) {
        var document = mapper.toDocument(entity);
        var saved = mongoRepository.save(document);
        return mapper.toEntity(saved);
    }

    @Override
    public Optional<Labor> findById(String id) {
        return mongoRepository.findById(id).map(mapper::toEntity);
    }

    @Override
    public List<Labor> findAll() {
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
    public Optional<Labor> findByName(String name) {
        return mongoRepository.findByName(name).map(mapper::toEntity);
    }

    @Override
    public boolean existsByName(String name) {
        return mongoRepository.existsByName(name);
    }

    @Override
    public List<Labor> findByActiveTrue() {
        return mongoRepository.findByActiveTrue()
                .stream()
                .map(mapper::toEntity)
                .toList();
    }
}

