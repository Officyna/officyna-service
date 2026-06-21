package br.com.officyna.infrastructure.persistence.mongodb.gateway;

import br.com.officyna.administrative.customer.domain.entity.Customer;
import br.com.officyna.administrative.customer.domain.entity.CustomerType;
import br.com.officyna.infrastructure.persistence.mapper.CustomerEntityDocumentMapper;
import br.com.officyna.infrastructure.persistence.mongodb.model.CustomerDocument;
import br.com.officyna.infrastructure.persistence.mongodb.repository.CustomerMongoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CustomerRepositoryGatewayTest {

    @Mock
    private CustomerMongoRepository mongoRepository;

    @Mock
    private CustomerEntityDocumentMapper mapper;

    @InjectMocks
    private CustomerRepositoryGateway gateway;

    private Customer customer;
    private CustomerDocument customerDocument;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        customer = Customer.builder()
                .id("1")
                .name("Test Customer")
                .document("12345678900")
                .type(CustomerType.INDIVIDUAL)
                .email("test@example.com")
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        customerDocument = CustomerDocument.builder()
                .id("1")
                .name("Test Customer")
                .document("12345678900")
                .type(CustomerType.INDIVIDUAL.name())
                .email("test@example.com")
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("Should save a customer successfully")
    void save_success() {
        when(mapper.toDocument(any(Customer.class))).thenReturn(customerDocument);
        when(mongoRepository.save(any(CustomerDocument.class))).thenReturn(customerDocument);
        when(mapper.toEntity(any(CustomerDocument.class))).thenReturn(customer);

        Customer savedCustomer = gateway.save(customer);

        assertNotNull(savedCustomer);
        assertEquals(customer.getId(), savedCustomer.getId());
        verify(mapper, times(1)).toDocument(customer);
        verify(mongoRepository, times(1)).save(customerDocument);
        verify(mapper, times(1)).toEntity(customerDocument);
    }

    @Test
    @DisplayName("Should find a customer by ID when it exists")
    void findById_found() {
        when(mongoRepository.findById("1")).thenReturn(Optional.of(customerDocument));
        when(mapper.toEntity(any(CustomerDocument.class))).thenReturn(customer);

        Optional<Customer> foundCustomer = gateway.findById("1");

        assertTrue(foundCustomer.isPresent());
        assertEquals(customer.getId(), foundCustomer.get().getId());
        verify(mongoRepository, times(1)).findById("1");
        verify(mapper, times(1)).toEntity(customerDocument);
    }

    @Test
    @DisplayName("Should return empty when customer not found by ID")
    void findById_notFound() {
        when(mongoRepository.findById("2")).thenReturn(Optional.empty());

        Optional<Customer> foundCustomer = gateway.findById("2");

        assertFalse(foundCustomer.isPresent());
        verify(mongoRepository, times(1)).findById("2");
        verify(mapper, never()).toEntity(any(CustomerDocument.class));
    }

    @Test
    @DisplayName("Should return all customers")
    void findAll_returnsAllCustomers() {
        List<CustomerDocument> documents = Collections.singletonList(customerDocument);
        List<Customer> customers = Collections.singletonList(customer);

        when(mongoRepository.findAll()).thenReturn(documents);
        when(mapper.toEntity(any(CustomerDocument.class))).thenReturn(customer);

        List<Customer> result = gateway.findAll();

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(customer.getId(), result.get(0).getId());
        verify(mongoRepository, times(1)).findAll();
        verify(mapper, times(1)).toEntity(customerDocument);
    }

    @Test
    @DisplayName("Should return empty list when no customers are found")
    void findAll_returnsEmptyList() {
        when(mongoRepository.findAll()).thenReturn(Collections.emptyList());

        List<Customer> result = gateway.findAll();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(mongoRepository, times(1)).findAll();
        verify(mapper, never()).toEntity(any(CustomerDocument.class));
    }

    @Test
    @DisplayName("Should delete a customer by ID")
    void deleteById_success() {
        doNothing().when(mongoRepository).deleteById("1");

        gateway.deleteById("1");

        verify(mongoRepository, times(1)).deleteById("1");
    }

    @Test
    @DisplayName("Should return true if customer exists by ID")
    void existsById_true() {
        when(mongoRepository.existsById("1")).thenReturn(true);

        assertTrue(gateway.existsById("1"));
        verify(mongoRepository, times(1)).existsById("1");
    }

    @Test
    @DisplayName("Should return false if customer does not exist by ID")
    void existsById_false() {
        when(mongoRepository.existsById("2")).thenReturn(false);

        assertFalse(gateway.existsById("2"));
        verify(mongoRepository, times(1)).existsById("2");
    }

    @Test
    @DisplayName("Should find a customer by document when it exists")
    void findByDocument_found() {
        when(mongoRepository.findByDocument("12345678900")).thenReturn(Optional.of(customerDocument));
        when(mapper.toEntity(any(CustomerDocument.class))).thenReturn(customer);

        Optional<Customer> foundCustomer = gateway.findByDocument("12345678900");

        assertTrue(foundCustomer.isPresent());
        assertEquals(customer.getDocument(), foundCustomer.get().getDocument());
        verify(mongoRepository, times(1)).findByDocument("12345678900");
        verify(mapper, times(1)).toEntity(customerDocument);
    }

    @Test
    @DisplayName("Should return empty when customer not found by document")
    void findByDocument_notFound() {
        when(mongoRepository.findByDocument("00000000000")).thenReturn(Optional.empty());

        Optional<Customer> foundCustomer = gateway.findByDocument("00000000000");

        assertFalse(foundCustomer.isPresent());
        verify(mongoRepository, times(1)).findByDocument("00000000000");
        verify(mapper, never()).toEntity(any(CustomerDocument.class));
    }

    @Test
    @DisplayName("Should return true if customer exists by document")
    void existsByDocument_true() {
        when(mongoRepository.existsByDocument("12345678900")).thenReturn(true);

        assertTrue(gateway.existsByDocument("12345678900"));
        verify(mongoRepository, times(1)).existsByDocument("12345678900");
    }

    @Test
    @DisplayName("Should return false if customer does not exist by document")
    void existsByDocument_false() {
        when(mongoRepository.existsByDocument("00000000000")).thenReturn(false);

        assertFalse(gateway.existsByDocument("00000000000"));
        verify(mongoRepository, times(1)).existsByDocument("00000000000");
    }

    @Test
    @DisplayName("Should return active customers")
    void findByActiveTrue_returnsActiveCustomers() {
        List<CustomerDocument> documents = Collections.singletonList(customerDocument);
        List<Customer> customers = Collections.singletonList(customer);

        when(mongoRepository.findByActiveTrue()).thenReturn(documents);
        when(mapper.toEntity(any(CustomerDocument.class))).thenReturn(customer);

        List<Customer> result = gateway.findByActiveTrue();

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(customer.getId(), result.get(0).getId());
        verify(mongoRepository, times(1)).findByActiveTrue();
        verify(mapper, times(1)).toEntity(customerDocument);
    }

    @Test
    @DisplayName("Should return empty list when no active customers are found")
    void findByActiveTrue_returnsEmptyList() {
        when(mongoRepository.findByActiveTrue()).thenReturn(Collections.emptyList());

        List<Customer> result = gateway.findByActiveTrue();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(mongoRepository, times(1)).findByActiveTrue();
        verify(mapper, never()).toEntity(any(CustomerDocument.class));
    }
}