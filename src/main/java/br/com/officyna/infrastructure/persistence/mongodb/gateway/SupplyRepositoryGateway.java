package br.com.officyna.infrastructure.persistence.mongodb.gateway;

import br.com.officyna.administrative.supply.domain.entity.Supply;
import br.com.officyna.administrative.supply.domain.entity.SupplyType;
import br.com.officyna.administrative.supply.domain.repository.SupplyRepository;
import br.com.officyna.infrastructure.persistence.mapper.SupplyEntityDocumentMapper;
import br.com.officyna.infrastructure.persistence.mongodb.model.SupplyDocument;
import br.com.officyna.infrastructure.persistence.mongodb.repository.SupplyMongoRepository;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Gateway de persistência para SupplyEntity.
 * Implementa a interface de repositório puro do domínio.
 * Usa MongoRepository para acessar o MongoDB e realiza conversão de dados.
 */
@Component
public class SupplyRepositoryGateway
        extends AbstractMongoRepositoryGateway<Supply, SupplyDocument, SupplyMongoRepository>
        implements SupplyRepository {

    public SupplyRepositoryGateway(SupplyMongoRepository mongoRepository, SupplyEntityDocumentMapper mapper) {
        super(mongoRepository, mapper::toDocument, mapper::toEntity);
    }

    @Override
    public boolean existsByName(String name) {
        return execute("existsByName", () -> mongoRepository.existsByName(name));
    }

    @Override
    public List<Supply> findByActiveTrue() {
        return execute("findByActiveTrue", () -> mongoRepository.findByActiveTrue()
                .stream()
                .map(toEntity)
                .toList());
    }

    @Override
    public List<Supply> findByActiveTrueAndType(SupplyType type) {
        return execute("findByActiveTrueAndType", () -> mongoRepository.findByActiveTrueAndType(type.name())
                .stream()
                .map(toEntity)
                .toList());
    }
}
