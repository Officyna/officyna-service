package br.com.officyna.infrastructure.persistence.mongodb.gateway;

import br.com.officyna.administrative.supply.domain.entity.Supply;
import br.com.officyna.administrative.supply.domain.entity.SupplyType;
import br.com.officyna.administrative.supply.domain.repository.SupplyRepository;
import br.com.officyna.infrastructure.persistence.mapper.SupplyEntityDocumentMapper;
import br.com.officyna.infrastructure.persistence.mongodb.repository.SupplyMongoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class SupplyRepositoryGateway implements SupplyRepository {

    private final SupplyMongoRepository mongoRepository;
    private final SupplyEntityDocumentMapper mapper;

    @Override
    public Supply save(Supply entity) {
        try {
            var document = mapper.toDocument(entity);
            var saved = mongoRepository.save(document);

            return mapper.toEntity(saved);

        } catch (Exception e) {
            log.error(
                    "MongoDB operation failed operation=save repository=SupplyRepositoryGateway",
                    e
            );
            throw e;
        }
    }

    @Override
    public Optional<Supply> findById(String id) {
        try {
            return mongoRepository.findById(id)
                    .map(mapper::toEntity);

        } catch (Exception e) {
            log.error(
                    "MongoDB operation failed operation=findById repository=SupplyRepositoryGateway",
                    e
            );
            throw e;
        }
    }

    @Override
    public List<Supply> findAll() {
        try {
            return mongoRepository.findAll()
                    .stream()
                    .map(mapper::toEntity)
                    .toList();

        } catch (Exception e) {
            log.error(
                    "MongoDB operation failed operation=findAll repository=SupplyRepositoryGateway",
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
                    "MongoDB operation failed operation=deleteById repository=SupplyRepositoryGateway",
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
                    "MongoDB operation failed operation=existsById repository=SupplyRepositoryGateway",
                    e
            );
            throw e;
        }
    }

    @Override
    public boolean existsByName(String name) {
        try {
            return mongoRepository.existsByName(name);

        } catch (Exception e) {
            log.error(
                    "MongoDB operation failed operation=existsByName repository=SupplyRepositoryGateway",
                    e
            );
            throw e;
        }
    }

    @Override
    public List<Supply> findByActiveTrue() {
        try {
            return mongoRepository.findByActiveTrue()
                    .stream()
                    .map(mapper::toEntity)
                    .toList();

        } catch (Exception e) {
            log.error(
                    "MongoDB operation failed operation=findByActiveTrue repository=SupplyRepositoryGateway",
                    e
            );
            throw e;
        }
    }

    @Override
    public List<Supply> findByActiveTrueAndType(SupplyType type) {
        try {
            return mongoRepository.findByActiveTrueAndType(type.name())
                    .stream()
                    .map(mapper::toEntity)
                    .toList();

        } catch (Exception e) {
            log.error(
                    "MongoDB operation failed operation=findByActiveTrueAndType repository=SupplyRepositoryGateway",
                    e
            );
            throw e;
        }
    }
}