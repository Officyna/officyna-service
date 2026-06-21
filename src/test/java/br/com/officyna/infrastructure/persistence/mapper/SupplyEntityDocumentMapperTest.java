package br.com.officyna.infrastructure.persistence.mapper;

import br.com.officyna.administrative.supply.domain.entity.Supply;
import br.com.officyna.administrative.supply.domain.entity.SupplyType;
import br.com.officyna.infrastructure.persistence.mongodb.model.SupplyDocument;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class SupplyEntityDocumentMapperTest {

    private SupplyEntityDocumentMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new SupplyEntityDocumentMapper();
    }

    // --- toDocument tests ---

    @Test
    @DisplayName("Should return null when toDocument receives a null Supply entity")
    void toDocument_nullEntity_returnsNull() {
        assertNull(mapper.toDocument(null));
    }

    @Test
    @DisplayName("Should correctly map a Supply entity to SupplyDocument")
    void toDocument_validEntity_returnsDocument() {
        Supply entity = Supply.builder()
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

        SupplyDocument document = mapper.toDocument(entity);

        assertNotNull(document);
        assertEquals(entity.getId(), document.getId());
        assertEquals(entity.getName(), document.getName());
        assertEquals(entity.getDescription(), document.getDescription());
        assertEquals(entity.getType().name(), document.getType());
        assertEquals(entity.getPurchasePrice(), document.getPurchasePrice());
        assertEquals(entity.getSalePrice(), document.getSalePrice());
        assertEquals(entity.getStockQuantity(), document.getStockQuantity());
        assertEquals(entity.getMinimumQuantity(), document.getMinimumQuantity());
        assertEquals(entity.getReservedQuantity(), document.getReservedQuantity());
        assertEquals(entity.getActive(), document.getActive());
        assertEquals(entity.getCreatedAt(), document.getCreatedAt());
        assertEquals(entity.getUpdatedAt(), document.getUpdatedAt());
    }

    @Test
    @DisplayName("Should correctly map a Supply entity with null type to SupplyDocument")
    void toDocument_entityWithNullType_returnsDocumentWithNullType() {
        Supply entity = Supply.builder()
                .id("supply-123")
                .name("Engine Oil")
                .description("Synthetic engine oil 5W-30")
                .type(null)
                .purchasePrice(new BigDecimal("30.00"))
                .salePrice(new BigDecimal("50.00"))
                .stockQuantity(100)
                .minimumQuantity(10)
                .reservedQuantity(5)
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        SupplyDocument document = mapper.toDocument(entity);

        assertNotNull(document);
        assertNull(document.getType());
    }

    // --- toEntity tests ---

    @Test
    @DisplayName("Should return null when toEntity receives a null SupplyDocument")
    void toEntity_nullDocument_returnsNull() {
        assertNull(mapper.toEntity(null));
    }

    @Test
    @DisplayName("Should correctly map a SupplyDocument to Supply entity")
    void toEntity_validDocument_returnsEntity() {
        SupplyDocument document = SupplyDocument.builder()
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

        Supply entity = mapper.toEntity(document);

        assertNotNull(entity);
        assertEquals(document.getId(), entity.getId());
        assertEquals(document.getName(), entity.getName());
        assertEquals(document.getDescription(), entity.getDescription());
        assertEquals(SupplyType.valueOf(document.getType()), entity.getType());
        assertEquals(document.getPurchasePrice(), entity.getPurchasePrice());
        assertEquals(document.getSalePrice(), entity.getSalePrice());
        assertEquals(document.getStockQuantity(), entity.getStockQuantity());
        assertEquals(document.getMinimumQuantity(), entity.getMinimumQuantity());
        assertEquals(document.getReservedQuantity(), entity.getReservedQuantity());
        assertEquals(document.getActive(), entity.getActive());
        assertEquals(document.getCreatedAt(), entity.getCreatedAt());
        assertEquals(document.getUpdatedAt(), entity.getUpdatedAt());
    }

    @Test
    @DisplayName("Should correctly map a SupplyDocument with null type string to Supply entity")
    void toEntity_documentWithNullTypeString_returnsEntityWithNullType() {
        SupplyDocument document = SupplyDocument.builder()
                .id("supply-123")
                .name("Engine Oil")
                .description("Synthetic engine oil 5W-30")
                .type(null)
                .purchasePrice(new BigDecimal("30.00"))
                .salePrice(new BigDecimal("50.00"))
                .stockQuantity(100)
                .minimumQuantity(10)
                .reservedQuantity(5)
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Supply entity = mapper.toEntity(document);

        assertNotNull(entity);
        assertNull(entity.getType());
    }
}