package br.com.officyna.infrastructure.persistence.mongodb.gateway;

import br.com.officyna.administrative.customer.domain.entity.Customer;
import br.com.officyna.administrative.customer.domain.repository.CustomerRepository;
import br.com.officyna.infrastructure.persistence.mapper.CustomerEntityDocumentMapper;
import br.com.officyna.infrastructure.persistence.mongodb.model.CustomerDocument;
import br.com.officyna.infrastructure.persistence.mongodb.repository.CustomerMongoRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Gateway de persistência para CustomerEntity.
 * Implementa a interface de repositório puro do domínio.
 * Usa MongoRepository para acessar o MongoDB e realiza conversão de dados.
 */
@Component
public class CustomerRepositoryGateway
        extends AbstractMongoRepositoryGateway<Customer, CustomerDocument, CustomerMongoRepository>
        implements CustomerRepository {

    public CustomerRepositoryGateway(CustomerMongoRepository mongoRepository, CustomerEntityDocumentMapper mapper) {
        super(mongoRepository, mapper::toDocument, mapper::toEntity);
    }

    @Override
    public Optional<Customer> findByDocument(String document) {
        return execute("findByDocument", () -> mongoRepository.findByDocument(document).map(toEntity));
    }

    @Override
    public Optional<Customer> findByEmail(String email) {
        return execute("findByEmail", () -> mongoRepository.findByEmail(email).map(toEntity));
    }

    @Override
    public boolean existsByDocument(String document) {
        return execute("existsByDocument", () -> mongoRepository.existsByDocument(document));
    }

    @Override
    public List<Customer> findByActiveTrue() {
        return execute("findByActiveTrue", () -> mongoRepository.findByActiveTrue()
                .stream()
                .map(toEntity)
                .toList());
    }
}
