package br.com.officyna.infrastructure.persistence.mongodb.gateway;

import br.com.officyna.administrative.customer.domain.entity.Customer;
import br.com.officyna.administrative.customer.domain.repository.CustomerRepository;
import br.com.officyna.infrastructure.persistence.mapper.CustomerEntityDocumentMapper;
import br.com.officyna.infrastructure.persistence.mongodb.repository.CustomerMongoRepository;
import lombok.RequiredArgsConstructor;
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
public class CustomerRepositoryGateway implements CustomerRepository {

    private final CustomerMongoRepository mongoRepository;
    private final CustomerEntityDocumentMapper mapper;

    @Override
    public Customer save(Customer entity) {
        var document = mapper.toDocument(entity);
        var saved = mongoRepository.save(document);
        return mapper.toEntity(saved);
    }

    @Override
    public Optional<Customer> findById(String id) {
        return mongoRepository.findById(id).map(mapper::toEntity);
    }

    @Override
    public List<Customer> findAll() {
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
    public Optional<Customer> findByDocument(String document) {
        return mongoRepository.findByDocument(document).map(mapper::toEntity);
    }

    @Override
    public boolean existsByDocument(String document) {
        return mongoRepository.existsByDocument(document);
    }

    @Override
    public List<Customer> findByActiveTrue() {
        return mongoRepository.findByActiveTrue()
                .stream()
                .map(mapper::toEntity)
                .toList();
    }
}

