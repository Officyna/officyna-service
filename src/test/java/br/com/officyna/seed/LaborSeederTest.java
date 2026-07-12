package br.com.officyna.seed;

import br.com.officyna.infrastructure.persistence.mongodb.model.LaborDocument;
import br.com.officyna.infrastructure.persistence.mongodb.repository.LaborMongoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LaborSeederTest {

    private LaborMongoRepository repository;
    private LaborSeeder seeder;

    @BeforeEach
    void setUp() {
        repository = mock(LaborMongoRepository.class);
        seeder = new LaborSeeder(repository);
    }

    @Test
    void shouldNotSeedWhenDatabaseAlreadyHasData() {

        when(repository.count()).thenReturn(1L);

        seeder.seed();

        verify(repository, never()).save(any());
    }

    @Test
    void shouldSeedAllLabors() {

        when(repository.count())
                .thenReturn(0L)
                .thenReturn(6L);

        seeder.seed();

        ArgumentCaptor<LaborDocument> captor =
                ArgumentCaptor.forClass(LaborDocument.class);

        verify(repository, times(6)).save(captor.capture());

        var labors = captor.getAllValues();

        assertEquals("Troca de óleo", labors.get(0).getName());
        assertEquals(new BigDecimal("120.00"), labors.get(0).getPrice());

        assertEquals("Revisão Completa", labors.get(5).getName());
        assertEquals(2, labors.get(5).getExecutionTimeInDays());

        verify(repository, times(2)).count();
    }

}