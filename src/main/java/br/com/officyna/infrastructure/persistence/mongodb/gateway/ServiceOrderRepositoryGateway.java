package br.com.officyna.infrastructure.persistence.mongodb.gateway;

import br.com.officyna.infrastructure.persistence.mapper.ServiceOrderEntityDocumentMapper;
import br.com.officyna.infrastructure.persistence.mongodb.repository.ServiceOrderMongoRepository;
import br.com.officyna.serviceorder.domain.entity.ServiceOrderEntity;
import br.com.officyna.serviceorder.domain.repository.IServiceOrderRepository;
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
public class ServiceOrderRepositoryGateway implements IServiceOrderRepository {

    private final ServiceOrderMongoRepository mongoRepository;
    private final ServiceOrderEntityDocumentMapper mapper;

    @Override
    public ServiceOrderEntity save(ServiceOrderEntity entity) {
        var document = mapper.toDocument(entity);
        var saved = mongoRepository.save(document);
        return mapper.toEntity(saved);
    }

    @Override
    public Optional<ServiceOrderEntity> findById(String id) {
        return mongoRepository.findById(id).map(mapper::toEntity);
    }

    @Override
    public List<ServiceOrderEntity> findAll() {
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
    public List<ServiceOrderEntity> findByLaborIdWithCompletedExecutions(String laborId) {
        return mongoRepository.findByLaborIdWithCompletedExecutions(laborId)
                .stream()
                .map(mapper::toEntity)
                .toList();
    }

    @Override
    public Optional<ServiceOrderEntity> findByServiceOrderNumber(Long serviceOrderNumber) {
        return mongoRepository.findByServiceOrderNumber(serviceOrderNumber).map(mapper::toEntity);
    }

    @Override
    public List<ServiceOrderEntity> findByCustomerId(String id) {
        return mongoRepository.findByCustomerId(id)
                .stream()
                .map(mapper::toEntity)
                .toList();
    }
}

