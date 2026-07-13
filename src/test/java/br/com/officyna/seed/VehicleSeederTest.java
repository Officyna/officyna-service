package br.com.officyna.seed;

import br.com.officyna.infrastructure.persistence.mongodb.model.CustomerDocument;
import br.com.officyna.infrastructure.persistence.mongodb.model.VehicleDocument;
import br.com.officyna.infrastructure.persistence.mongodb.repository.CustomerMongoRepository;
import br.com.officyna.infrastructure.persistence.mongodb.repository.VehicleMongoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class VehicleSeederTest {

    private VehicleMongoRepository repository;
    private CustomerMongoRepository customerRepository;
    private VehicleSeeder seeder;

    @BeforeEach
    void setUp() {

        repository = mock(VehicleMongoRepository.class);
        customerRepository = mock(CustomerMongoRepository.class);

        seeder = new VehicleSeeder(repository, customerRepository);
    }

    @Test
    void shouldNotSeedWhenVehicleAlreadyExists() {

        when(repository.existsByPlate("ABC1D23"))
                .thenReturn(true);

        seeder.seed();

        verify(repository, never()).save(any());
    }

    @Test
    void shouldSeedVehicles() {

        CustomerDocument customer = CustomerDocument.builder()
                .id("1")
                .name("João da Silva")
                .document("12345678901")
                .build();

        when(repository.existsByPlate("ABC1D23"))
                .thenReturn(false);

        when(customerRepository.findByDocument("12345678901"))
                .thenReturn(Optional.of(customer));

        when(repository.count()).thenReturn(2L);

        seeder.seed();

        ArgumentCaptor<VehicleDocument> captor =
                ArgumentCaptor.forClass(VehicleDocument.class);

        verify(repository, times(2)).save(captor.capture());

        var vehicles = captor.getAllValues();

        assertEquals("ABC1D23", vehicles.get(0).getPlate());
        assertEquals("Toyota", vehicles.get(0).getBrand());

        assertEquals("DEF4G56", vehicles.get(1).getPlate());
        assertEquals("Honda", vehicles.get(1).getBrand());

        verify(repository).count();
    }

    @Test
    void shouldThrowExceptionWhenCustomerDoesNotExist() {

        when(repository.existsByPlate("ABC1D23"))
                .thenReturn(false);

        when(customerRepository.findByDocument("12345678901"))
                .thenReturn(Optional.empty());

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> seeder.seed());

        assertEquals("Cliente seed não encontrado.", exception.getMessage());

        verify(repository, never()).save(any());
    }

}