package br.com.officyna.infrastructure.persistence.mongodb.gateway;

import br.com.officyna.administrative.user.domain.entity.User;
import br.com.officyna.administrative.user.domain.repository.UserRepository;
import br.com.officyna.infrastructure.persistence.mapper.UserEntityDocumentMapper;
import br.com.officyna.infrastructure.persistence.mongodb.model.UserDocument;
import br.com.officyna.infrastructure.persistence.mongodb.repository.UserMongoRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Gateway de persistência para UserEntity.
 * Implementa a interface de repositório puro do domínio.
 * Usa MongoRepository para acessar o MongoDB e realiza conversão de dados.
 */
@Component
public class UserRepositoryGateway
        extends AbstractMongoRepositoryGateway<User, UserDocument, UserMongoRepository>
        implements UserRepository {

    public UserRepositoryGateway(UserMongoRepository mongoRepository, UserEntityDocumentMapper mapper) {
        super(mongoRepository, mapper::toDocument, mapper::toEntity);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return execute("findByEmail", () -> mongoRepository.findByEmail(email).map(toEntity));
    }

    @Override
    public boolean existsByEmail(String email) {
        return execute("existsByEmail", () -> mongoRepository.existsByEmail(email));
    }

    @Override
    public List<User> findByActiveTrue() {
        return execute("findByActiveTrue", () -> mongoRepository.findByActiveTrue()
                .stream()
                .map(toEntity)
                .toList());
    }
}
