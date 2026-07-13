package br.com.officyna.seed;

import br.com.officyna.infrastructure.persistence.mongodb.model.SupplyDocument;
import br.com.officyna.infrastructure.persistence.mongodb.repository.SupplyMongoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SupplySeederTest {

    private SupplyMongoRepository repository;
    private SupplySeeder seeder;

    @BeforeEach
    void setUp() {
        repository = mock(SupplyMongoRepository.class);
        seeder = new SupplySeeder(repository);
    }

    @Test
    void shouldNotSeedWhenDatabaseAlreadyHasData() {

        when(repository.count()).thenReturn(1L);

        seeder.seed();

        verify(repository, never()).save(any());
    }

    @Test
    void shouldSeedAllSupplies() {

        when(repository.count())
                .thenReturn(0L)
                .thenReturn(5L);

        seeder.seed();

        ArgumentCaptor<SupplyDocument> captor =
                ArgumentCaptor.forClass(SupplyDocument.class);

        verify(repository, times(5)).save(captor.capture());

        var supplies = captor.getAllValues();

        assertEquals("Óleo 5W30", supplies.get(0).getName());
        assertEquals(new BigDecimal("35.00"), supplies.get(0).getPurchasePrice());

        assertEquals("Aditivo para Radiador", supplies.get(4).getName());
        assertEquals(30, supplies.get(4).getStockQuantity());

        verify(repository, times(2)).count();
    }

}