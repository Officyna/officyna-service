package br.com.officyna.infrastructure.persistence.mongodb.gateway;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class AbstractMongoRepositoryGateway<E, D, R extends MongoRepository<D, String>> {

    private final Logger log = LoggerFactory.getLogger(getClass());

    protected final R mongoRepository;
    protected final Function<E, D> toDocument;
    protected final Function<D, E> toEntity;

    protected AbstractMongoRepositoryGateway(R mongoRepository, Function<E, D> toDocument, Function<D, E> toEntity) {
        this.mongoRepository = mongoRepository;
        this.toDocument = toDocument;
        this.toEntity = toEntity;
    }

    public E save(E entity) {
        return execute("save", () -> {
            D document = toDocument.apply(entity);
            D saved = mongoRepository.save(document);
            return toEntity.apply(saved);
        });
    }

    public Optional<E> findById(String id) {
        return execute("findById", () -> mongoRepository.findById(id).map(toEntity));
    }

    public List<E> findAll() {
        return execute("findAll", () -> mongoRepository.findAll().stream().map(toEntity).toList());
    }

    public void deleteById(String id) {
        executeVoid("deleteById", () -> mongoRepository.deleteById(id));
    }

    public boolean existsById(String id) {
        return execute("existsById", () -> mongoRepository.existsById(id));
    }

    protected <T> T execute(String operation, Supplier<T> action) {
        try {
            return action.get();
        } catch (Exception e) {
            log.error("MongoDB operation failed operation={} repository={}", operation, getClass().getSimpleName(), e);
            throw e;
        }
    }

    protected void executeVoid(String operation, Runnable action) {
        execute(operation, () -> {
            action.run();
            return null;
        });
    }
}
