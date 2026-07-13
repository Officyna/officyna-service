package br.com.officyna.infrastructure.persistence.mongodb.gateway;

import br.com.officyna.infrastructure.persistence.mapper.LaborMonitoringEntityDocumentMapper;
import br.com.officyna.infrastructure.persistence.mongodb.model.LaborMonitoringDocument;
import br.com.officyna.infrastructure.persistence.mongodb.repository.LaborMonitoringMongoRepository;
import br.com.officyna.monitoring.domain.entity.LaborMonitoring;
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

class LaborMonitoringRepositoryGatewayTest {

    @Mock
    private LaborMonitoringMongoRepository mongoRepository;

    @Mock
    private LaborMonitoringEntityDocumentMapper mapper;

    @InjectMocks
    private LaborMonitoringRepositoryGateway gateway;

    private LaborMonitoring laborMonitoring;
    private LaborMonitoringDocument laborMonitoringDocument;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        laborMonitoring = LaborMonitoring.builder()
                .id("lm-123")
                .laborId("labor-456")
                .laborName("Service A")
                .laborDescription("Description for Service A")
                .averageExecutionTimeInDays(5.0)
                .totalExecutions(100)
                .updatedAt(LocalDateTime.now())
                .build();

        laborMonitoringDocument = LaborMonitoringDocument.builder()
                .id("lm-123")
                .laborId("labor-456")
                .laborName("Service A")
                .laborDescription("Description for Service A")
                .averageExecutionTimeInDays(5.0)
                .totalExecutions(100)
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("Should save a LaborMonitoring successfully")
    void save_success() {
        when(mapper.toDocument(any(LaborMonitoring.class))).thenReturn(laborMonitoringDocument);
        when(mongoRepository.save(any(LaborMonitoringDocument.class))).thenReturn(laborMonitoringDocument);
        when(mapper.toEntity(any(LaborMonitoringDocument.class))).thenReturn(laborMonitoring);

        LaborMonitoring savedLaborMonitoring = gateway.save(laborMonitoring);

        assertNotNull(savedLaborMonitoring);
        assertEquals(laborMonitoring.getId(), savedLaborMonitoring.getId());
        verify(mapper, times(1)).toDocument(laborMonitoring);
        verify(mongoRepository, times(1)).save(laborMonitoringDocument);
        verify(mapper, times(1)).toEntity(laborMonitoringDocument);
    }

    @Test
    @DisplayName("Should find a LaborMonitoring by ID when it exists")
    void findById_found() {
        when(mongoRepository.findById("lm-123")).thenReturn(Optional.of(laborMonitoringDocument));
        when(mapper.toEntity(any(LaborMonitoringDocument.class))).thenReturn(laborMonitoring);

        Optional<LaborMonitoring> foundLaborMonitoring = gateway.findById("lm-123");

        assertTrue(foundLaborMonitoring.isPresent());
        assertEquals(laborMonitoring.getId(), foundLaborMonitoring.get().getId());
        verify(mongoRepository, times(1)).findById("lm-123");
        verify(mapper, times(1)).toEntity(laborMonitoringDocument);
    }

    @Test
    @DisplayName("Should return empty when LaborMonitoring not found by ID")
    void findById_notFound() {
        when(mongoRepository.findById("non-existent-id")).thenReturn(Optional.empty());

        Optional<LaborMonitoring> foundLaborMonitoring = gateway.findById("non-existent-id");

        assertFalse(foundLaborMonitoring.isPresent());
        verify(mongoRepository, times(1)).findById("non-existent-id");
        verify(mapper, never()).toEntity(any(LaborMonitoringDocument.class));
    }

    @Test
    @DisplayName("Should return all LaborMonitorings")
    void findAll_returnsAllLaborMonitorings() {
        List<LaborMonitoringDocument> documents = Collections.singletonList(laborMonitoringDocument);
        List<LaborMonitoring> laborMonitorings = Collections.singletonList(laborMonitoring);

        when(mongoRepository.findAll()).thenReturn(documents);
        when(mapper.toEntity(any(LaborMonitoringDocument.class))).thenReturn(laborMonitoring);

        List<LaborMonitoring> result = gateway.findAll();

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(laborMonitoring.getId(), result.get(0).getId());
        verify(mongoRepository, times(1)).findAll();
        verify(mapper, times(1)).toEntity(laborMonitoringDocument);
    }

    @Test
    @DisplayName("Should return empty list when no LaborMonitorings are found")
    void findAll_returnsEmptyList() {
        when(mongoRepository.findAll()).thenReturn(Collections.emptyList());

        List<LaborMonitoring> result = gateway.findAll();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(mongoRepository, times(1)).findAll();
        verify(mapper, never()).toEntity(any(LaborMonitoringDocument.class));
    }

    @Test
    @DisplayName("Should delete a LaborMonitoring by ID")
    void deleteById_success() {
        doNothing().when(mongoRepository).deleteById("lm-123");

        gateway.deleteById("lm-123");

        verify(mongoRepository, times(1)).deleteById("lm-123");
    }

    @Test
    @DisplayName("Should return true if LaborMonitoring exists by ID")
    void existsById_true() {
        when(mongoRepository.existsById("lm-123")).thenReturn(true);

        assertTrue(gateway.existsById("lm-123"));
        verify(mongoRepository, times(1)).existsById("lm-123");
    }

    @Test
    @DisplayName("Should return false if LaborMonitoring does not exist by ID")
    void existsById_false() {
        when(mongoRepository.existsById("non-existent-id")).thenReturn(false);

        assertFalse(gateway.existsById("non-existent-id"));
        verify(mongoRepository, times(1)).existsById("non-existent-id");
    }

    @Test
    @DisplayName("Should find a LaborMonitoring by laborId when it exists")
    void findByLaborId_found() {
        when(mongoRepository.findByLaborId("labor-456")).thenReturn(Optional.of(laborMonitoringDocument));
        when(mapper.toEntity(any(LaborMonitoringDocument.class))).thenReturn(laborMonitoring);

        Optional<LaborMonitoring> foundLaborMonitoring = gateway.findByLaborId("labor-456");

        assertTrue(foundLaborMonitoring.isPresent());
        assertEquals(laborMonitoring.getLaborId(), foundLaborMonitoring.get().getLaborId());
        verify(mongoRepository, times(1)).findByLaborId("labor-456");
        verify(mapper, times(1)).toEntity(laborMonitoringDocument);
    }

    @Test
    @DisplayName("Should return empty when LaborMonitoring not found by laborId")
    void findByLaborId_notFound() {
        when(mongoRepository.findByLaborId("non-existent-labor-id")).thenReturn(Optional.empty());

        Optional<LaborMonitoring> foundLaborMonitoring = gateway.findByLaborId("non-existent-labor-id");

        assertFalse(foundLaborMonitoring.isPresent());
        verify(mongoRepository, times(1)).findByLaborId("non-existent-labor-id");
        verify(mapper, never()).toEntity(any(LaborMonitoringDocument.class));
    }
}