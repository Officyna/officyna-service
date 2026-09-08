package br.com.officyna.infrastructure.persistence.mongodb.gateway;

import br.com.officyna.administrative.labor.domain.entity.Labor;
import br.com.officyna.administrative.labor.domain.repository.LaborRepository;
import br.com.officyna.infrastructure.persistence.mapper.LaborEntityDocumentMapper;
import br.com.officyna.infrastructure.persistence.mongodb.repository.LaborMongoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class LaborRepositoryGateway implements LaborRepository {

    private final LaborMongoRepository mongoRepository;
    private final LaborEntityDocumentMapper mapper;

    @Override
    public Labor save(Labor entity) {
        try {
            var document = mapper.toDocument(entity);
            var saved = mongoRepository.save(document);

            return mapper.toEntity(saved);

        } catch (Exception e) {
            log.error(
                    "MongoDB operation failed operation=save repository=LaborRepositoryGateway",
                    e
            );
            throw e;
        }
    }

    @Override
    public Optional<Labor> findById(String id) {
        try {
            return mongoRepository.findById(id)
                    .map(mapper::toEntity);

        } catch (Exception e) {
            log.error(
                    "MongoDB operation failed operation=findById repository=LaborRepositoryGateway",
                    e
            );
            throw e;
        }
    }

    @Override
    public List<Labor> findAll() {
        try {
            return mongoRepository.findAll()
                    .stream()
                    .map(mapper::toEntity)
                    .toList();

        } catch (Exception e) {
            log.error(
                    "MongoDB operation failed operation=findAll repository=LaborRepositoryGateway",
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
                    "MongoDB operation failed operation=deleteById repository=LaborRepositoryGateway",
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
                    "MongoDB operation failed operation=existsById repository=LaborRepositoryGateway",
                    e
            );
            throw e;
        }
    }

    @Override
    public Optional<Labor> findByName(String name) {
        try {
            return mongoRepository.findByName(name)
                    .map(mapper::toEntity);

        } catch (Exception e) {
            log.error(
                    "MongoDB operation failed operation=findByName repository=LaborRepositoryGateway",
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
                    "MongoDB operation failed operation=existsByName repository=LaborRepositoryGateway",
                    e
            );
            throw e;
        }
    }

    @Override
    public List<Labor> findByActiveTrue() {
        try {
            return mongoRepository.findByActiveTrue()
                    .stream()
                    .map(mapper::toEntity)
                    .toList();

        } catch (Exception e) {
            log.error(
                    "MongoDB operation failed operation=findByActiveTrue repository=LaborRepositoryGateway",
                    e
            );
            throw e;
        }
    }
}