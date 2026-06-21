package br.com.officyna.infrastructure.persistence.mapper;

import br.com.officyna.monitoring.domain.entity.LaborMonitoring;
import br.com.officyna.infrastructure.persistence.mongodb.model.LaborMonitoringDocument;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class LaborMonitoringEntityDocumentMapperTest {

    private LaborMonitoringEntityDocumentMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new LaborMonitoringEntityDocumentMapper();
    }

    // --- toDocument tests ---

    @Test
    @DisplayName("Should return null when toDocument receives a null LaborMonitoring entity")
    void toDocument_nullEntity_returnsNull() {
        assertNull(mapper.toDocument(null));
    }

    @Test
    @DisplayName("Should correctly map a LaborMonitoring entity to LaborMonitoringDocument")
    void toDocument_validEntity_returnsDocument() {
        LaborMonitoring entity = LaborMonitoring.builder()
                .id("lm-123")
                .laborId("labor-456")
                .laborName("Service A")
                .laborDescription("Description for Service A")
                .averageExecutionTimeInDays(5.0)
                .totalExecutions(100)
                .updatedAt(LocalDateTime.now())
                .build();

        LaborMonitoringDocument document = mapper.toDocument(entity);

        assertNotNull(document);
        assertEquals(entity.getId(), document.getId());
        assertEquals(entity.getLaborId(), document.getLaborId());
        assertEquals(entity.getLaborName(), document.getLaborName());
        assertEquals(entity.getLaborDescription(), document.getLaborDescription());
        assertEquals(entity.getAverageExecutionTimeInDays(), document.getAverageExecutionTimeInDays());
        assertEquals(entity.getTotalExecutions(), document.getTotalExecutions());
        assertEquals(entity.getUpdatedAt(), document.getUpdatedAt());
    }

    // --- toEntity tests ---

    @Test
    @DisplayName("Should return null when toEntity receives a null LaborMonitoringDocument")
    void toEntity_nullDocument_returnsNull() {
        assertNull(mapper.toEntity(null));
    }

    @Test
    @DisplayName("Should correctly map a LaborMonitoringDocument to LaborMonitoring entity")
    void toEntity_validDocument_returnsEntity() {
        LaborMonitoringDocument document = LaborMonitoringDocument.builder()
                .id("lm-123")
                .laborId("labor-456")
                .laborName("Service A")
                .laborDescription("Description for Service A")
                .averageExecutionTimeInDays(5.0)
                .totalExecutions(100)
                .updatedAt(LocalDateTime.now())
                .build();

        LaborMonitoring entity = mapper.toEntity(document);

        assertNotNull(entity);
        assertEquals(document.getId(), entity.getId());
        assertEquals(document.getLaborId(), entity.getLaborId());
        assertEquals(document.getLaborName(), entity.getLaborName());
        assertEquals(document.getLaborDescription(), entity.getLaborDescription());
        assertEquals(document.getAverageExecutionTimeInDays(), entity.getAverageExecutionTimeInDays());
        assertEquals(document.getTotalExecutions(), entity.getTotalExecutions());
        assertEquals(document.getUpdatedAt(), entity.getUpdatedAt());
    }
}