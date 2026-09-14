package br.com.officyna.infrastructure.persistence.mongodb.gateway;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AbstractMongoRepositoryGatewayTest {

    private interface FakeMongoRepository extends MongoRepository<String, String> {
    }

    private static class FakeGateway extends AbstractMongoRepositoryGateway<Integer, String, FakeMongoRepository> {
        FakeGateway(FakeMongoRepository mongoRepository) {
            super(mongoRepository, Object::toString, Integer::valueOf);
        }
    }

    private FakeMongoRepository mongoRepository;
    private FakeGateway gateway;

    @BeforeEach
    void setUp() {
        mongoRepository = mock(FakeMongoRepository.class);
        gateway = new FakeGateway(mongoRepository);
    }

    @Test
    @DisplayName("save deve converter para documento, persistir e converter de volta para entidade")
    void save_shouldPersistAndReturnEntity() {
        when(mongoRepository.save("42")).thenReturn("42");

        Integer result = gateway.save(42);

        assertEquals(42, result);
        verify(mongoRepository).save("42");
    }

    @Test
    @DisplayName("findById deve retornar a entidade convertida quando existir")
    void findById_shouldReturnEntity_whenFound() {
        when(mongoRepository.findById("7")).thenReturn(Optional.of("7"));

        Optional<Integer> result = gateway.findById("7");

        assertTrue(result.isPresent());
        assertEquals(7, result.get());
    }

    @Test
    @DisplayName("findById deve retornar vazio quando não existir")
    void findById_shouldReturnEmpty_whenNotFound() {
        when(mongoRepository.findById("999")).thenReturn(Optional.empty());

        assertTrue(gateway.findById("999").isEmpty());
    }

    @Test
    @DisplayName("findAll deve converter todos os documentos retornados")
    void findAll_shouldReturnAllConvertedEntities() {
        when(mongoRepository.findAll()).thenReturn(List.of("1", "2", "3"));

        List<Integer> result = gateway.findAll();

        assertEquals(List.of(1, 2, 3), result);
    }

    @Test
    @DisplayName("findAll deve retornar lista vazia quando não houver documentos")
    void findAll_shouldReturnEmptyList_whenNoDocuments() {
        when(mongoRepository.findAll()).thenReturn(Collections.emptyList());

        assertTrue(gateway.findAll().isEmpty());
    }

    @Test
    @DisplayName("deleteById deve delegar ao MongoRepository")
    void deleteById_shouldDelegateToMongoRepository() {
        gateway.deleteById("5");

        verify(mongoRepository).deleteById("5");
    }

    @Test
    @DisplayName("existsById deve delegar e retornar o resultado do MongoRepository")
    void existsById_shouldDelegateAndReturnResult() {
        when(mongoRepository.existsById("5")).thenReturn(true);
        when(mongoRepository.existsById("6")).thenReturn(false);

        assertTrue(gateway.existsById("5"));
        assertFalse(gateway.existsById("6"));
    }

    @Test
    @DisplayName("execute deve logar e repropagar a exceção original quando a operação falhar")
    void execute_shouldLogAndRethrow_whenActionFails() {
        RuntimeException boom = new RuntimeException("boom");
        when(mongoRepository.findById("err")).thenThrow(boom);

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> gateway.findById("err"));
        assertSame(boom, thrown);
    }
}
