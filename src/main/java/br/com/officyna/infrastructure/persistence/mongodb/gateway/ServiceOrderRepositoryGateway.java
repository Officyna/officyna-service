package br.com.officyna.infrastructure.persistence.mongodb.gateway;

import br.com.officyna.infrastructure.persistence.mapper.ServiceOrderEntityDocumentMapper;
import br.com.officyna.infrastructure.persistence.mongodb.repository.ServiceOrderMongoRepository;
import br.com.officyna.serviceorder.domain.entity.ServiceOrder;
import br.com.officyna.serviceorder.domain.repository.ServiceOrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class ServiceOrderRepositoryGateway implements ServiceOrderRepository {

    private final ServiceOrderMongoRepository mongoRepository;
    private final ServiceOrderEntityDocumentMapper mapper;

    @Override
    public ServiceOrder save(ServiceOrder entity) {
        try {
            var document = mapper.toDocument(entity);
            var saved = mongoRepository.save(document);

            return mapper.toEntity(saved);

        } catch (Exception e) {
            log.error(
                    "MongoDB operation failed operation=save repository=ServiceOrderRepositoryGateway",
                    e
            );
            throw e;
        }
    }

    @Override
    public Optional<ServiceOrder> findById(String id) {
        try {
            return mongoRepository.findById(id)
                    .map(mapper::toEntity);

        } catch (Exception e) {
            log.error(
                    "MongoDB operation failed operation=findById repository=ServiceOrderRepositoryGateway",
                    e
            );
            throw e;
        }
    }

    @Override
    public List<ServiceOrder> findAll() {
        try {
            return mongoRepository.findAll()
                    .stream()
                    .map(mapper::toEntity)
                    .toList();

        } catch (Exception e) {
            log.error(
                    "MongoDB operation failed operation=findAll repository=ServiceOrderRepositoryGateway",
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
                    "MongoDB operation failed operation=deleteById repository=ServiceOrderRepositoryGateway",
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
                    "MongoDB operation failed operation=existsById repository=ServiceOrderRepositoryGateway",
                    e
            );
            throw e;
        }
    }

    @Override
    public List<ServiceOrder> findByLaborIdWithCompletedExecutions(String laborId) {
        try {
            return mongoRepository.findByLaborIdWithCompletedExecutions(laborId)
                    .stream()
                    .map(mapper::toEntity)
                    .toList();

        } catch (Exception e) {
            log.error(
                    "MongoDB operation failed operation=findByLaborIdWithCompletedExecutions repository=ServiceOrderRepositoryGateway",
                    e
            );
            throw e;
        }
    }

    @Override
    public Optional<ServiceOrder> findByServiceOrderNumber(Long serviceOrderNumber) {
        try {
            return mongoRepository.findByServiceOrderNumber(serviceOrderNumber)
                    .map(mapper::toEntity);

        } catch (Exception e) {
            log.error(
                    "MongoDB operation failed operation=findByServiceOrderNumber repository=ServiceOrderRepositoryGateway",
                    e
            );
            throw e;
        }
    }

    @Override
    public List<ServiceOrder> findByCustomerId(String id) {
        try {
            return mongoRepository.findByCustomerId(id)
                    .stream()
                    .map(mapper::toEntity)
                    .toList();

        } catch (Exception e) {
            log.error(
                    "MongoDB operation failed operation=findByCustomerId repository=ServiceOrderRepositoryGateway",
                    e
            );
            throw e;
        }
    }
}