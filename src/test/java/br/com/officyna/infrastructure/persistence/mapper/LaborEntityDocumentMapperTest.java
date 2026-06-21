package br.com.officyna.infrastructure.persistence.mapper;

import br.com.officyna.administrative.labor.domain.entity.Labor;
import br.com.officyna.infrastructure.persistence.mongodb.model.LaborDocument;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class LaborEntityDocumentMapperTest {

    private LaborEntityDocumentMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new LaborEntityDocumentMapper();
    }

    // --- toDocument tests ---

    @Test
    @DisplayName("Should return null when toDocument receives a null Labor entity")
    void toDocument_nullEntity_returnsNull() {
        assertNull(mapper.toDocument(null));
    }

    @Test
    @DisplayName("Should correctly map a Labor entity to LaborDocument")
    void toDocument_validEntity_returnsDocument() {
        Labor entity = Labor.builder()
                .id("labor-123")
                .name("Installation Service")
                .description("Installation of new equipment")
                .price(new BigDecimal("150.00"))
                .executionTimeInDays(2)
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        LaborDocument document = mapper.toDocument(entity);

        assertNotNull(document);
        assertEquals(entity.getId(), document.getId());
        assertEquals(entity.getName(), document.getName());
        assertEquals(entity.getDescription(), document.getDescription());
        assertEquals(entity.getPrice(), document.getPrice());
        assertEquals(entity.getExecutionTimeInDays(), document.getExecutionTimeInDays());
        assertEquals(entity.getActive(), document.getActive());
        assertEquals(entity.getCreatedAt(), document.getCreatedAt());
        assertEquals(entity.getUpdatedAt(), document.getUpdatedAt());
    }

    // --- toEntity tests ---

    @Test
    @DisplayName("Should return null when toEntity receives a null LaborDocument")
    void toEntity_nullDocument_returnsNull() {
        assertNull(mapper.toEntity(null));
    }

    @Test
    @DisplayName("Should correctly map a LaborDocument to Labor entity")
    void toEntity_validDocument_returnsEntity() {
        LaborDocument document = LaborDocument.builder()
                .id("labor-123")
                .name("Installation Service")
                .description("Installation of new equipment")
                .price(new BigDecimal("150.00"))
                .executionTimeInDays(2)
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Labor entity = mapper.toEntity(document);

        assertNotNull(entity);
        assertEquals(document.getId(), entity.getId());
        assertEquals(document.getName(), entity.getName());
        assertEquals(document.getDescription(), entity.getDescription());
        assertEquals(document.getPrice(), entity.getPrice());
        assertEquals(document.getExecutionTimeInDays(), entity.getExecutionTimeInDays());
        assertEquals(document.getActive(), entity.getActive());
        assertEquals(document.getCreatedAt(), entity.getCreatedAt());
        assertEquals(document.getUpdatedAt(), entity.getUpdatedAt());
    }
}