package br.com.officyna.infrastructure.persistence.mongodb.gateway;

import br.com.officyna.administrative.labor.domain.entity.Labor;
import br.com.officyna.administrative.labor.domain.repository.LaborRepository;
import br.com.officyna.infrastructure.persistence.mapper.LaborEntityDocumentMapper;
import br.com.officyna.infrastructure.persistence.mongodb.model.LaborDocument;
import br.com.officyna.infrastructure.persistence.mongodb.repository.LaborMongoRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Gateway de persistência para LaborEntity.
 * Implementa a interface de repositório puro do domínio.
 * Usa MongoRepository para acessar o MongoDB e realiza conversão de dados.
 */
@Component
public class LaborRepositoryGateway
        extends AbstractMongoRepositoryGateway<Labor, LaborDocument, LaborMongoRepository>
        implements LaborRepository {

    public LaborRepositoryGateway(LaborMongoRepository mongoRepository, LaborEntityDocumentMapper mapper) {
        super(mongoRepository, mapper::toDocument, mapper::toEntity);
    }

    @Override
    public Optional<Labor> findByName(String name) {
        return execute("findByName", () -> mongoRepository.findByName(name).map(toEntity));
    }

    @Override
    public boolean existsByName(String name) {
        return execute("existsByName", () -> mongoRepository.existsByName(name));
    }

    @Override
    public List<Labor> findByActiveTrue() {
        return execute("findByActiveTrue", () -> mongoRepository.findByActiveTrue()
                .stream()
                .map(toEntity)
                .toList());
    }
}
