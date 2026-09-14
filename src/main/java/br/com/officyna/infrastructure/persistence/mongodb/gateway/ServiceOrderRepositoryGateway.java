package br.com.officyna.infrastructure.persistence.mongodb.gateway;

import br.com.officyna.infrastructure.persistence.mapper.ServiceOrderEntityDocumentMapper;
import br.com.officyna.infrastructure.persistence.mongodb.model.ServiceOrderDocument;
import br.com.officyna.infrastructure.persistence.mongodb.repository.ServiceOrderMongoRepository;
import br.com.officyna.serviceorder.domain.entity.ServiceOrder;
import br.com.officyna.serviceorder.domain.repository.ServiceOrderRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Gateway de persistência para ServiceOrderEntity.
 * Implementa a interface de repositório puro do domínio.
 * Usa MongoRepository para acessar o MongoDB e realiza conversão de dados.
 */
@Component
public class ServiceOrderRepositoryGateway
        extends AbstractMongoRepositoryGateway<ServiceOrder, ServiceOrderDocument, ServiceOrderMongoRepository>
        implements ServiceOrderRepository {

    public ServiceOrderRepositoryGateway(ServiceOrderMongoRepository mongoRepository,
                                          ServiceOrderEntityDocumentMapper mapper) {
        super(mongoRepository, mapper::toDocument, mapper::toEntity);
    }

    @Override
    public List<ServiceOrder> findByLaborIdWithCompletedExecutions(String laborId) {
        return execute("findByLaborIdWithCompletedExecutions", () -> mongoRepository
                .findByLaborIdWithCompletedExecutions(laborId)
                .stream()
                .map(toEntity)
                .toList());
    }

    @Override
    public Optional<ServiceOrder> findByServiceOrderNumber(Long serviceOrderNumber) {
        return execute("findByServiceOrderNumber", () -> mongoRepository
                .findByServiceOrderNumber(serviceOrderNumber)
                .map(toEntity));
    }

    @Override
    public List<ServiceOrder> findByCustomerId(String id) {
        return execute("findByCustomerId", () -> mongoRepository.findByCustomerId(id)
                .stream()
                .map(toEntity)
                .toList());
    }
}
