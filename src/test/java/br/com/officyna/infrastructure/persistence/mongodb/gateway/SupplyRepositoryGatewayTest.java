package br.com.officyna.infrastructure.persistence.mongodb.gateway;

import br.com.officyna.administrative.supply.domain.entity.Supply;
import br.com.officyna.administrative.supply.domain.entity.SupplyType;
import br.com.officyna.infrastructure.persistence.mapper.SupplyEntityDocumentMapper;
import br.com.officyna.infrastructure.persistence.mongodb.model.SupplyDocument;
import br.com.officyna.infrastructure.persistence.mongodb.repository.SupplyMongoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class SupplyRepositoryGatewayTest {

    @Mock
    private SupplyMongoRepository mongoRepository;

    @Mock
    private SupplyEntityDocumentMapper mapper;

    @InjectMocks
    private SupplyRepositoryGateway gateway;

    private Supply supply;
    private SupplyDocument supplyDocument;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        supply = Supply.builder()
                .id("supply-123")
                .name("Engine Oil")
                .description("Synthetic engine oil 5W-30")
                .type(SupplyType.SUPPLY)
                .purchasePrice(new BigDecimal("30.00"))
                .salePrice(new BigDecimal("50.00"))
                .stockQuantity(100)
                .minimumQuantity(10)
                .reservedQuantity(5)
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        supplyDocument = SupplyDocument.builder()
                .id("supply-123")
                .name("Engine Oil")
                .description("Synthetic engine oil 5W-30")
                .type(SupplyType.PART.name())
                .purchasePrice(new BigDecimal("30.00"))
                .salePrice(new BigDecimal("50.00"))
                .stockQuantity(100)
                .minimumQuantity(10)
                .reservedQuantity(5)
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("Should save a Supply successfully")
    void save_success() {
        when(mapper.toDocument(any(Supply.class))).thenReturn(supplyDocument);
        when(mongoRepository.save(any(SupplyDocument.class))).thenReturn(supplyDocument);
        when(mapper.toEntity(any(SupplyDocument.class))).thenReturn(supply);

        Supply savedSupply = gateway.save(supply);

        assertNotNull(savedSupply);
        assertEquals(supply.getId(), savedSupply.getId());
        verify(mapper, times(1)).toDocument(supply);
        verify(mongoRepository, times(1)).save(supplyDocument);
        verify(mapper, times(1)).toEntity(supplyDocument);
    }

    @Test
    @DisplayName("Should find a Supply by ID when it exists")
    void findById_found() {
        when(mongoRepository.findById("supply-123")).thenReturn(Optional.of(supplyDocument));
        when(mapper.toEntity(any(SupplyDocument.class))).thenReturn(supply);

        Optional<Supply> foundSupply = gateway.findById("supply-123");

        assertTrue(foundSupply.isPresent());
        assertEquals(supply.getId(), foundSupply.get().getId());
        verify(mongoRepository, times(1)).findById("supply-123");
        verify(mapper, times(1)).toEntity(supplyDocument);
    }

    @Test
    @DisplayName("Should return empty when Supply not found by ID")
    void findById_notFound() {
        when(mongoRepository.findById("non-existent-id")).thenReturn(Optional.empty());

        Optional<Supply> foundSupply = gateway.findById("non-existent-id");

        assertFalse(foundSupply.isPresent());
        verify(mongoRepository, times(1)).findById("non-existent-id");
        verify(mapper, never()).toEntity(any(SupplyDocument.class));
    }

    @Test
    @DisplayName("Should return all Supplies")
    void findAll_returnsAllSupplies() {
        List<SupplyDocument> documents = Collections.singletonList(supplyDocument);
        List<Supply> supplies = Collections.singletonList(supply);

        when(mongoRepository.findAll()).thenReturn(documents);
        when(mapper.toEntity(any(SupplyDocument.class))).thenReturn(supply);

        List<Supply> result = gateway.findAll();

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(supply.getId(), result.get(0).getId());
        verify(mongoRepository, times(1)).findAll();
        verify(mapper, times(1)).toEntity(supplyDocument);
    }

    @Test
    @DisplayName("Should return empty list when no Supplies are found")
    void findAll_returnsEmptyList() {
        when(mongoRepository.findAll()).thenReturn(Collections.emptyList());

        List<Supply> result = gateway.findAll();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(mongoRepository, times(1)).findAll();
        verify(mapper, never()).toEntity(any(SupplyDocument.class));
    }

    @Test
    @DisplayName("Should delete a Supply by ID")
    void deleteById_success() {
        doNothing().when(mongoRepository).deleteById("supply-123");

        gateway.deleteById("supply-123");

        verify(mongoRepository, times(1)).deleteById("supply-123");
    }

    @Test
    @DisplayName("Should return true if Supply exists by ID")
    void existsById_true() {
        when(mongoRepository.existsById("supply-123")).thenReturn(true);

        assertTrue(gateway.existsById("supply-123"));
        verify(mongoRepository, times(1)).existsById("supply-123");
    }

    @Test
    @DisplayName("Should return false if Supply does not exist by ID")
    void existsById_false() {
        when(mongoRepository.existsById("non-existent-id")).thenReturn(false);

        assertFalse(gateway.existsById("non-existent-id"));
        verify(mongoRepository, times(1)).existsById("non-existent-id");
    }

    @Test
    @DisplayName("Should return true if Supply exists by name")
    void existsByName_true() {
        when(mongoRepository.existsByName("Engine Oil")).thenReturn(true);

        assertTrue(gateway.existsByName("Engine Oil"));
        verify(mongoRepository, times(1)).existsByName("Engine Oil");
    }

    @Test
    @DisplayName("Should return false if Supply does not exist by name")
    void existsByName_false() {
        when(mongoRepository.existsByName("Non-existent Supply")).thenReturn(false);

        assertFalse(gateway.existsByName("Non-existent Supply"));
        verify(mongoRepository, times(1)).existsByName("Non-existent Supply");
    }

    @Test
    @DisplayName("Should return active Supplies")
    void findByActiveTrue_returnsActiveSupplies() {
        List<SupplyDocument> documents = Collections.singletonList(supplyDocument);
        List<Supply> supplies = Collections.singletonList(supply);

        when(mongoRepository.findByActiveTrue()).thenReturn(documents);
        when(mapper.toEntity(any(SupplyDocument.class))).thenReturn(supply);

        List<Supply> result = gateway.findByActiveTrue();

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(supply.getId(), result.get(0).getId());
        verify(mongoRepository, times(1)).findByActiveTrue();
        verify(mapper, times(1)).toEntity(supplyDocument);
    }

    @Test
    @DisplayName("Should return empty list when no active Supplies are found")
    void findByActiveTrue_returnsEmptyList() {
        when(mongoRepository.findByActiveTrue()).thenReturn(Collections.emptyList());

        List<Supply> result = gateway.findByActiveTrue();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(mongoRepository, times(1)).findByActiveTrue();
        verify(mapper, never()).toEntity(any(SupplyDocument.class));
    }

    @Test
    @DisplayName("Should return active Supplies by type")
    void findByActiveTrueAndType_returnsActiveSuppliesByType() {
        List<SupplyDocument> documents = Collections.singletonList(supplyDocument);
        List<Supply> supplies = Collections.singletonList(supply);

        when(mongoRepository.findByActiveTrueAndType(SupplyType.PART.name())).thenReturn(documents);
        when(mapper.toEntity(any(SupplyDocument.class))).thenReturn(supply);

        List<Supply> result = gateway.findByActiveTrueAndType(SupplyType.PART);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(supply.getId(), result.get(0).getId());
        verify(mongoRepository, times(1)).findByActiveTrueAndType(SupplyType.PART.name());
        verify(mapper, times(1)).toEntity(supplyDocument);
    }

    @Test
    @DisplayName("Should return empty list when no active Supplies found by type")
    void findByActiveTrueAndType_returnsEmptyList() {
        when(mongoRepository.findByActiveTrueAndType(SupplyType.SERVICE.name())).thenReturn(Collections.emptyList());

        List<Supply> result = gateway.findByActiveTrueAndType(SupplyType.SERVICE);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(mongoRepository, times(1)).findByActiveTrueAndType(SupplyType.SERVICE.name());
        verify(mapper, never()).toEntity(any(SupplyDocument.class));
    }
}