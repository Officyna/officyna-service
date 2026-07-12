package br.com.officyna.seed;

import br.com.officyna.infrastructure.persistence.mongodb.model.CustomerDocument;
import br.com.officyna.infrastructure.persistence.mongodb.repository.CustomerMongoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomerSeederTest {

    private CustomerMongoRepository repository;
    private CustomerSeeder customerSeeder;

    @BeforeEach
    void setUp() {
        repository = mock(CustomerMongoRepository.class);
        customerSeeder = new CustomerSeeder(repository);
    }

    @Test
    void shouldNotSeedWhenCustomerAlreadyExists() {

        when(repository.existsByDocument("12345678901"))
                .thenReturn(true);

        customerSeeder.seed();

        verify(repository).existsByDocument("12345678901");
        verify(repository, never()).save(any());
        verify(repository, never()).count();
    }

    @Test
    void shouldSeedCustomersWhenDatabaseIsEmpty() {

        when(repository.existsByDocument("12345678901"))
                .thenReturn(false);

        when(repository.count()).thenReturn(2L);

        customerSeeder.seed();

        ArgumentCaptor<CustomerDocument> captor =
                ArgumentCaptor.forClass(CustomerDocument.class);

        verify(repository, times(2)).save(captor.capture());

        var customers = captor.getAllValues();

        CustomerDocument first = customers.get(0);
        CustomerDocument second = customers.get(1);

        assertEquals("João da Silva", first.getName());
        assertEquals("12345678901", first.getDocument());
        assertEquals("INDIVIDUAL", first.getType());
        assertTrue(first.getActive());

        assertNotNull(first.getAddress());
        assertEquals("Rua das Flores", first.getAddress().getStreet());
        assertEquals("São Paulo", first.getAddress().getCity());

        assertEquals("Empresa XPTO", second.getName());
        assertEquals("12345678000199", second.getDocument());
        assertEquals("COMPANY", second.getType());
        assertTrue(second.getActive());

        assertNotNull(second.getAddress());
        assertEquals("Av. Paulista", second.getAddress().getStreet());

        verify(repository).count();
    }

}