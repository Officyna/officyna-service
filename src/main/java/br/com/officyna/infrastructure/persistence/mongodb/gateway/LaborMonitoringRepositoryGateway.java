package br.com.officyna.infrastructure.persistence.mongodb.gateway;

import br.com.officyna.infrastructure.persistence.mapper.LaborMonitoringEntityDocumentMapper;
import br.com.officyna.infrastructure.persistence.mongodb.model.LaborMonitoringDocument;
import br.com.officyna.infrastructure.persistence.mongodb.repository.LaborMonitoringMongoRepository;
import br.com.officyna.monitoring.domain.entity.LaborMonitoring;
import br.com.officyna.monitoring.domain.repository.LaborMonitoringRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Gateway de persistência para LaborMonitoringEntity.
 * Implementa a interface de repositório puro do domínio.
 * Usa MongoRepository para acessar o MongoDB e realiza conversão de dados.
 */
@Component
public class LaborMonitoringRepositoryGateway
        extends AbstractMongoRepositoryGateway<LaborMonitoring, LaborMonitoringDocument, LaborMonitoringMongoRepository>
        implements LaborMonitoringRepository {

    public LaborMonitoringRepositoryGateway(LaborMonitoringMongoRepository mongoRepository,
                                             LaborMonitoringEntityDocumentMapper mapper) {
        super(mongoRepository, mapper::toDocument, mapper::toEntity);
    }

    @Override
    public Optional<LaborMonitoring> findByLaborId(String laborId) {
        return execute("findByLaborId", () -> mongoRepository.findByLaborId(laborId).map(toEntity));
    }
}
