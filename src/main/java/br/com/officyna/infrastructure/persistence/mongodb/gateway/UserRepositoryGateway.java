package br.com.officyna.infrastructure.persistence.mongodb.gateway;

import br.com.officyna.administrative.user.domain.entity.User;
import br.com.officyna.administrative.user.domain.repository.UserRepository;
import br.com.officyna.infrastructure.persistence.mapper.UserEntityDocumentMapper;
import br.com.officyna.infrastructure.persistence.mongodb.repository.UserMongoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class UserRepositoryGateway implements UserRepository {

    private final UserMongoRepository mongoRepository;
    private final UserEntityDocumentMapper mapper;

    @Override
    public User save(User entity) {
        try {
            var document = mapper.toDocument(entity);
            var saved = mongoRepository.save(document);

            return mapper.toEntity(saved);

        } catch (Exception e) {
            log.error(
                    "MongoDB operation failed operation=save repository=UserRepositoryGateway",
                    e
            );
            throw e;
        }
    }

    @Override
    public Optional<User> findById(String id) {
        try {
            return mongoRepository.findById(id)
                    .map(mapper::toEntity);

        } catch (Exception e) {
            log.error(
                    "MongoDB operation failed operation=findById repository=UserRepositoryGateway",
                    e
            );
            throw e;
        }
    }

    @Override
    public List<User> findAll() {
        try {
            return mongoRepository.findAll()
                    .stream()
                    .map(mapper::toEntity)
                    .toList();

        } catch (Exception e) {
            log.error(
                    "MongoDB operation failed operation=findAll repository=UserRepositoryGateway",
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
                    "MongoDB operation failed operation=deleteById repository=UserRepositoryGateway",
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
                    "MongoDB operation failed operation=existsById repository=UserRepositoryGateway",
                    e
            );
            throw e;
        }
    }

    @Override
    public Optional<User> findByEmail(String email) {
        try {
            return mongoRepository.findByEmail(email)
                    .map(mapper::toEntity);

        } catch (Exception e) {
            log.error(
                    "MongoDB operation failed operation=findByEmail repository=UserRepositoryGateway",
                    e
            );
            throw e;
        }
    }

    @Override
    public boolean existsByEmail(String email) {
        try {
            return mongoRepository.existsByEmail(email);

        } catch (Exception e) {
            log.error(
                    "MongoDB operation failed operation=existsByEmail repository=UserRepositoryGateway",
                    e
            );
            throw e;
        }
    }

    @Override
    public List<User> findByActiveTrue() {
        try {
            return mongoRepository.findByActiveTrue()
                    .stream()
                    .map(mapper::toEntity)
                    .toList();

        } catch (Exception e) {
            log.error(
                    "MongoDB operation failed operation=findByActiveTrue repository=UserRepositoryGateway",
                    e
            );
            throw e;
        }
    }
}