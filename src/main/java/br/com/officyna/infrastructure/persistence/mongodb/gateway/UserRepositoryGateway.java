package br.com.officyna.infrastructure.persistence.mongodb.gateway;

import br.com.officyna.administrative.user.domain.UserEntity;
import br.com.officyna.administrative.user.domain.repository.IUserRepository;
import br.com.officyna.infrastructure.persistence.mapper.UserEntityDocumentMapper;
import br.com.officyna.infrastructure.persistence.mongodb.repository.UserMongoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Gateway de persistência para UserEntity.
 * Implementa a interface de repositório puro do domínio.
 * Usa MongoRepository para acessar o MongoDB e realiza conversão de dados.
 */
@Component
@RequiredArgsConstructor
public class UserRepositoryGateway implements IUserRepository {

    private final UserMongoRepository mongoRepository;
    private final UserEntityDocumentMapper mapper;

    @Override
    public UserEntity save(UserEntity entity) {
        var document = mapper.toDocument(entity);
        var saved = mongoRepository.save(document);
        return mapper.toEntity(saved);
    }

    @Override
    public Optional<UserEntity> findById(String id) {
        return mongoRepository.findById(id).map(mapper::toEntity);
    }

    @Override
    public List<UserEntity> findAll() {
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
    public Optional<UserEntity> findByEmail(String email) {
        return mongoRepository.findByEmail(email).map(mapper::toEntity);
    }

    @Override
    public boolean existsByEmail(String email) {
        return mongoRepository.existsByEmail(email);
    }

    @Override
    public List<UserEntity> findByActiveTrue() {
        return mongoRepository.findByActiveTrue()
                .stream()
                .map(mapper::toEntity)
                .toList();
    }
}

