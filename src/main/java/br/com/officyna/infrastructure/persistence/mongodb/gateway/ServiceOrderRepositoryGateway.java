package br.com.officyna.infrastructure.persistence.mongodb.gateway;

import br.com.officyna.infrastructure.persistence.mapper.ServiceOrderEntityDocumentMapper;
import br.com.officyna.infrastructure.persistence.mongodb.repository.ServiceOrderMongoRepository;
import br.com.officyna.serviceorder.domain.entity.ServiceOrder;
import br.com.officyna.serviceorder.domain.repository.ServiceOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Gateway de persistência para ServiceOrderEntity.
 * Implementa a interface de repositório puro do domínio.
 * Usa MongoRepository para acessar o MongoDB e realiza conversão de dados.
 */
@Component
@RequiredArgsConstructor
public class ServiceOrderRepositoryGateway implements ServiceOrderRepository {

    private final ServiceOrderMongoRepository mongoRepository;
    private final ServiceOrderEntityDocumentMapper mapper;

    @Override
    public ServiceOrder save(ServiceOrder entity) {
        var document = mapper.toDocument(entity);
        var saved = mongoRepository.save(document);
        return mapper.toEntity(saved);
    }

    @Override
    public Optional<ServiceOrder> findById(String id) {
        return mongoRepository.findById(id).map(mapper::toEntity);
    }

    @Override
    public List<ServiceOrder> findAll() {
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
    public List<ServiceOrder> findByLaborIdWithCompletedExecutions(String laborId) {
        return mongoRepository.findByLaborIdWithCompletedExecutions(laborId)
                .stream()
                .map(mapper::toEntity)
                .toList();
    }

    @Override
    public Optional<ServiceOrder> findByServiceOrderNumber(Long serviceOrderNumber) {
        return mongoRepository.findByServiceOrderNumber(serviceOrderNumber).map(mapper::toEntity);
    }

    @Override
    public List<ServiceOrder> findByCustomerId(String id) {
        return mongoRepository.findByCustomerId(id)
                .stream()
                .map(mapper::toEntity)
                .toList();
    }
}

