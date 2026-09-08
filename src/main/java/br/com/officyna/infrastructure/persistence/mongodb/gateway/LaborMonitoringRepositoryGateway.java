package br.com.officyna.infrastructure.persistence.mongodb.gateway;

import br.com.officyna.infrastructure.persistence.mapper.LaborMonitoringEntityDocumentMapper;
import br.com.officyna.infrastructure.persistence.mongodb.repository.LaborMonitoringMongoRepository;
import br.com.officyna.monitoring.domain.entity.LaborMonitoring;
import br.com.officyna.monitoring.domain.repository.LaborMonitoringRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class LaborMonitoringRepositoryGateway implements LaborMonitoringRepository {

    private final LaborMonitoringMongoRepository mongoRepository;
    private final LaborMonitoringEntityDocumentMapper mapper;

    @Override
    public LaborMonitoring save(LaborMonitoring entity) {
        try {
            var document = mapper.toDocument(entity);
            var saved = mongoRepository.save(document);

            return mapper.toEntity(saved);

        } catch (Exception e) {
            log.error(
                    "MongoDB operation failed operation=save repository=LaborMonitoringRepositoryGateway",
                    e
            );
            throw e;
        }
    }

    @Override
    public Optional<LaborMonitoring> findById(String id) {
        try {
            return mongoRepository.findById(id)
                    .map(mapper::toEntity);

        } catch (Exception e) {
            log.error(
                    "MongoDB operation failed operation=findById repository=LaborMonitoringRepositoryGateway",
                    e
            );
            throw e;
        }
    }

    @Override
    public List<LaborMonitoring> findAll() {
        try {
            return mongoRepository.findAll()
                    .stream()
                    .map(mapper::toEntity)
                    .toList();

        } catch (Exception e) {
            log.error(
                    "MongoDB operation failed operation=findAll repository=LaborMonitoringRepositoryGateway",
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
                    "MongoDB operation failed operation=deleteById repository=LaborMonitoringRepositoryGateway",
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
                    "MongoDB operation failed operation=existsById repository=LaborMonitoringRepositoryGateway",
                    e
            );
            throw e;
        }
    }

    @Override
    public Optional<LaborMonitoring> findByLaborId(String laborId) {
        try {
            return mongoRepository.findByLaborId(laborId)
                    .map(mapper::toEntity);

        } catch (Exception e) {
            log.error(
                    "MongoDB operation failed operation=findByLaborId repository=LaborMonitoringRepositoryGateway",
                    e
            );
            throw e;
        }
    }
}