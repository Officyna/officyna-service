package br.com.officyna.infrastructure.persistence.mongodb.gateway;

import br.com.officyna.infrastructure.persistence.mapper.LaborMonitoringEntityDocumentMapper;
import br.com.officyna.infrastructure.persistence.mongodb.repository.LaborMonitoringMongoRepository;
import br.com.officyna.monitoring.domain.entity.LaborMonitoring;
import br.com.officyna.monitoring.domain.repository.LaborMonitoringRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Gateway de persistência para LaborMonitoringEntity.
 * Implementa a interface de repositório puro do domínio.
 * Usa MongoRepository para acessar o MongoDB e realiza conversão de dados.
 */
@Component
@RequiredArgsConstructor
public class LaborMonitoringRepositoryGateway implements LaborMonitoringRepository {

    private final LaborMonitoringMongoRepository mongoRepository;
    private final LaborMonitoringEntityDocumentMapper mapper;

    @Override
    public LaborMonitoring save(LaborMonitoring entity) {
        var document = mapper.toDocument(entity);
        var saved = mongoRepository.save(document);
        return mapper.toEntity(saved);
    }

    @Override
    public Optional<LaborMonitoring> findById(String id) {
        return mongoRepository.findById(id).map(mapper::toEntity);
    }

    @Override
    public List<LaborMonitoring> findAll() {
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
    public Optional<LaborMonitoring> findByLaborId(String laborId) {
        return mongoRepository.findByLaborId(laborId).map(mapper::toEntity);
    }
}

