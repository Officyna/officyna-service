package br.com.officyna.infrastructure.persistence.mongodb.gateway;

import br.com.officyna.administrative.customer.domain.entity.Customer;
import br.com.officyna.administrative.customer.domain.repository.CustomerRepository;
import br.com.officyna.infrastructure.persistence.mapper.CustomerEntityDocumentMapper;
import br.com.officyna.infrastructure.persistence.mongodb.repository.CustomerMongoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Gateway de persistência para CustomerEntity.
 * Implementa a interface de repositório puro do domínio.
 * Usa MongoRepository para acessar o MongoDB e realiza conversão de dados.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class CustomerRepositoryGateway implements CustomerRepository {

    private final CustomerMongoRepository mongoRepository;
    private final CustomerEntityDocumentMapper mapper;

    @Override
    public Customer save(Customer entity) {
        try {
            var document = mapper.toDocument(entity);
            var saved = mongoRepository.save(document);

            return mapper.toEntity(saved);

        } catch (Exception e) {
            log.error(
                    "MongoDB operation failed operation=save repository=CustomerRepositoryGateway",
                    e
            );
            throw e;
        }
    }

    @Override
    public Optional<Customer> findById(String id) {
        try {
            return mongoRepository.findById(id)
                    .map(mapper::toEntity);

        } catch (Exception e) {
            log.error(
                    "MongoDB operation failed operation=findById repository=CustomerRepositoryGateway",
                    e
            );
            throw e;
        }
    }

    @Override
    public List<Customer> findAll() {
        try {
            return mongoRepository.findAll()
                    .stream()
                    .map(mapper::toEntity)
                    .toList();

        } catch (Exception e) {
            log.error(
                    "MongoDB operation failed operation=findAll repository=CustomerRepositoryGateway",
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
                    "MongoDB operation failed operation=deleteById repository=CustomerRepositoryGateway",
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
                    "MongoDB operation failed operation=existsById repository=CustomerRepositoryGateway",
                    e
            );
            throw e;
        }
    }

    @Override
    public Optional<Customer> findByDocument(String document) {
        try {
            return mongoRepository.findByDocument(document)
                    .map(mapper::toEntity);

        } catch (Exception e) {
            log.error(
                    "MongoDB operation failed operation=findByDocument repository=CustomerRepositoryGateway",
                    e
            );
            throw e;
        }
    }

    @Override
    public Optional<Customer> findByEmail(String email) {
        try {
            return mongoRepository.findByEmail(email)
                    .map(mapper::toEntity);

        } catch (Exception e) {
            log.error(
                    "MongoDB operation failed operation=findByEmail repository=CustomerRepositoryGateway",
                    e
            );
            throw e;
        }
    }

    @Override
    public boolean existsByDocument(String document) {
        try {
            return mongoRepository.existsByDocument(document);

        } catch (Exception e) {
            log.error(
                    "MongoDB operation failed operation=existsByDocument repository=CustomerRepositoryGateway",
                    e
            );
            throw e;
        }
    }

    @Override
    public List<Customer> findByActiveTrue() {
        try {
            return mongoRepository.findByActiveTrue()
                    .stream()
                    .map(mapper::toEntity)
                    .toList();

        } catch (Exception e) {
            log.error(
                    "MongoDB operation failed operation=findByActiveTrue repository=CustomerRepositoryGateway",
                    e
            );
            throw e;
        }
    }
}