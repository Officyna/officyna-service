package br.com.officyna.infrastructure.persistence.mongodb.gateway;

import br.com.officyna.administrative.labor.domain.entity.Labor;
import br.com.officyna.infrastructure.persistence.mapper.LaborEntityDocumentMapper;
import br.com.officyna.infrastructure.persistence.mongodb.model.LaborDocument;
import br.com.officyna.infrastructure.persistence.mongodb.repository.LaborMongoRepository;
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

class LaborRepositoryGatewayTest {

    @Mock
    private LaborMongoRepository mongoRepository;

    @Mock
    private LaborEntityDocumentMapper mapper;

    @InjectMocks
    private LaborRepositoryGateway gateway;

    private Labor labor;
    private LaborDocument laborDocument;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        labor = Labor.builder()
                .id("labor-123")
                .name("Installation Service")
                .description("Installation of new equipment")
                .price(new BigDecimal("150.00"))
                .executionTimeInDays(2)
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        laborDocument = LaborDocument.builder()
                .id("labor-123")
                .name("Installation Service")
                .description("Installation of new equipment")
                .price(new BigDecimal("150.00"))
                .executionTimeInDays(2)
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("Should save a labor successfully")
    void save_success() {
        when(mapper.toDocument(any(Labor.class))).thenReturn(laborDocument);
        when(mongoRepository.save(any(LaborDocument.class))).thenReturn(laborDocument);
        when(mapper.toEntity(any(LaborDocument.class))).thenReturn(labor);

        Labor savedLabor = gateway.save(labor);

        assertNotNull(savedLabor);
        assertEquals(labor.getId(), savedLabor.getId());
        verify(mapper, times(1)).toDocument(labor);
        verify(mongoRepository, times(1)).save(laborDocument);
        verify(mapper, times(1)).toEntity(laborDocument);
    }

    @Test
    @DisplayName("Should find a labor by ID when it exists")
    void findById_found() {
        when(mongoRepository.findById("labor-123")).thenReturn(Optional.of(laborDocument));
        when(mapper.toEntity(any(LaborDocument.class))).thenReturn(labor);

        Optional<Labor> foundLabor = gateway.findById("labor-123");

        assertTrue(foundLabor.isPresent());
        assertEquals(labor.getId(), foundLabor.get().getId());
        verify(mongoRepository, times(1)).findById("labor-123");
        verify(mapper, times(1)).toEntity(laborDocument);
    }

    @Test
    @DisplayName("Should return empty when labor not found by ID")
    void findById_notFound() {
        when(mongoRepository.findById("non-existent-id")).thenReturn(Optional.empty());

        Optional<Labor> foundLabor = gateway.findById("non-existent-id");

        assertFalse(foundLabor.isPresent());
        verify(mongoRepository, times(1)).findById("non-existent-id");
        verify(mapper, never()).toEntity(any(LaborDocument.class));
    }

    @Test
    @DisplayName("Should return all labors")
    void findAll_returnsAllLabors() {
        List<LaborDocument> documents = Collections.singletonList(laborDocument);
        List<Labor> labors = Collections.singletonList(labor);

        when(mongoRepository.findAll()).thenReturn(documents);
        when(mapper.toEntity(any(LaborDocument.class))).thenReturn(labor);

        List<Labor> result = gateway.findAll();

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(labor.getId(), result.get(0).getId());
        verify(mongoRepository, times(1)).findAll();
        verify(mapper, times(1)).toEntity(laborDocument);
    }

    @Test
    @DisplayName("Should return empty list when no labors are found")
    void findAll_returnsEmptyList() {
        when(mongoRepository.findAll()).thenReturn(Collections.emptyList());

        List<Labor> result = gateway.findAll();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(mongoRepository, times(1)).findAll();
        verify(mapper, never()).toEntity(any(LaborDocument.class));
    }

    @Test
    @DisplayName("Should delete a labor by ID")
    void deleteById_success() {
        doNothing().when(mongoRepository).deleteById("labor-123");

        gateway.deleteById("labor-123");

        verify(mongoRepository, times(1)).deleteById("labor-123");
    }

    @Test
    @DisplayName("Should return true if labor exists by ID")
    void existsById_true() {
        when(mongoRepository.existsById("labor-123")).thenReturn(true);

        assertTrue(gateway.existsById("labor-123"));
        verify(mongoRepository, times(1)).existsById("labor-123");
    }

    @Test
    @DisplayName("Should return false if labor does not exist by ID")
    void existsById_false() {
        when(mongoRepository.existsById("non-existent-id")).thenReturn(false);

        assertFalse(gateway.existsById("non-existent-id"));
        verify(mongoRepository, times(1)).existsById("non-existent-id");
    }

    @Test
    @DisplayName("Should find a labor by name when it exists")
    void findByName_found() {
        when(mongoRepository.findByName("Installation Service")).thenReturn(Optional.of(laborDocument));
        when(mapper.toEntity(any(LaborDocument.class))).thenReturn(labor);

        Optional<Labor> foundLabor = gateway.findByName("Installation Service");

        assertTrue(foundLabor.isPresent());
        assertEquals(labor.getName(), foundLabor.get().getName());
        verify(mongoRepository, times(1)).findByName("Installation Service");
        verify(mapper, times(1)).toEntity(laborDocument);
    }

    @Test
    @DisplayName("Should return empty when labor not found by name")
    void findByName_notFound() {
        when(mongoRepository.findByName("Non-existent Service")).thenReturn(Optional.empty());

        Optional<Labor> foundLabor = gateway.findByName("Non-existent Service");

        assertFalse(foundLabor.isPresent());
        verify(mongoRepository, times(1)).findByName("Non-existent Service");
        verify(mapper, never()).toEntity(any(LaborDocument.class));
    }

    @Test
    @DisplayName("Should return true if labor exists by name")
    void existsByName_true() {
        when(mongoRepository.existsByName("Installation Service")).thenReturn(true);

        assertTrue(gateway.existsByName("Installation Service"));
        verify(mongoRepository, times(1)).existsByName("Installation Service");
    }

    @Test
    @DisplayName("Should return false if labor does not exist by name")
    void existsByName_false() {
        when(mongoRepository.existsByName("Non-existent Service")).thenReturn(false);

        assertFalse(gateway.existsByName("Non-existent Service"));
        verify(mongoRepository, times(1)).existsByName("Non-existent Service");
    }

    @Test
    @DisplayName("Should return active labors")
    void findByActiveTrue_returnsActiveLabors() {
        List<LaborDocument> documents = Collections.singletonList(laborDocument);
        List<Labor> labors = Collections.singletonList(labor);

        when(mongoRepository.findByActiveTrue()).thenReturn(documents);
        when(mapper.toEntity(any(LaborDocument.class))).thenReturn(labor);

        List<Labor> result = gateway.findByActiveTrue();

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(labor.getId(), result.get(0).getId());
        verify(mongoRepository, times(1)).findByActiveTrue();
        verify(mapper, times(1)).toEntity(laborDocument);
    }

    @Test
    @DisplayName("Should return empty list when no active labors are found")
    void findByActiveTrue_returnsEmptyList() {
        when(mongoRepository.findByActiveTrue()).thenReturn(Collections.emptyList());

        List<Labor> result = gateway.findByActiveTrue();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(mongoRepository, times(1)).findByActiveTrue();
        verify(mapper, never()).toEntity(any(LaborDocument.class));
    }
}