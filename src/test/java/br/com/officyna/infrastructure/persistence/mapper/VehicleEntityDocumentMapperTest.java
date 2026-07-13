package br.com.officyna.infrastructure.persistence.mapper;

import br.com.officyna.administrative.vehicle.domain.entity.Vehicle;
import br.com.officyna.infrastructure.persistence.mongodb.model.VehicleDocument;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class VehicleEntityDocumentMapperTest {

    private VehicleEntityDocumentMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new VehicleEntityDocumentMapper();
    }

    // --- toDocument tests ---

    @Test
    @DisplayName("Should return null when toDocument receives a null Vehicle entity")
    void toDocument_nullEntity_returnsNull() {
        assertNull(mapper.toDocument(null));
    }

    @Test
    @DisplayName("Should correctly map a Vehicle entity to VehicleDocument")
    void toDocument_validEntity_returnsDocument() {
        Vehicle entity = Vehicle.builder()
                .id("vehicle-123")
                .customerId("customer-456")
                .customerName("Customer Name")
                .plate("ABC1234")
                .brand("Brand A")
                .model("Model B")
                .year(2020)
                .color("Red")
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        VehicleDocument document = mapper.toDocument(entity);

        assertNotNull(document);
        assertEquals(entity.getId(), document.getId());
        assertEquals(entity.getCustomerId(), document.getCustomerId());
        assertEquals(entity.getCustomerName(), document.getCustomerName());
        assertEquals(entity.getPlate(), document.getPlate());
        assertEquals(entity.getBrand(), document.getBrand());
        assertEquals(entity.getModel(), document.getModel());
        assertEquals(entity.getYear(), document.getYear());
        assertEquals(entity.getColor(), document.getColor());
        assertEquals(entity.isActive(), document.isActive());
        assertEquals(entity.getCreatedAt(), document.getCreatedAt());
        assertEquals(entity.getUpdatedAt(), document.getUpdatedAt());
    }

    // --- toEntity tests ---

    @Test
    @DisplayName("Should return null when toEntity receives a null VehicleDocument")
    void toEntity_nullDocument_returnsNull() {
        assertNull(mapper.toEntity(null));
    }

    @Test
    @DisplayName("Should correctly map a VehicleDocument to Vehicle entity")
    void toEntity_validDocument_returnsEntity() {
        VehicleDocument document = VehicleDocument.builder()
                .id("vehicle-123")
                .customerId("customer-456")
                .customerName("Customer Name")
                .plate("ABC1234")
                .brand("Brand A")
                .model("Model B")
                .year(2020)
                .color("Red")
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Vehicle entity = mapper.toEntity(document);

        assertNotNull(entity);
        assertEquals(document.getId(), entity.getId());
        assertEquals(document.getCustomerId(), entity.getCustomerId());
        assertEquals(document.getCustomerName(), entity.getCustomerName());
        assertEquals(document.getPlate(), entity.getPlate());
        assertEquals(document.getBrand(), entity.getBrand());
        assertEquals(document.getModel(), entity.getModel());
        assertEquals(document.getYear(), entity.getYear());
        assertEquals(document.getColor(), entity.getColor());
        assertEquals(document.isActive(), entity.isActive());
        assertEquals(document.getCreatedAt(), entity.getCreatedAt());
        assertEquals(document.getUpdatedAt(), entity.getUpdatedAt());
    }
}