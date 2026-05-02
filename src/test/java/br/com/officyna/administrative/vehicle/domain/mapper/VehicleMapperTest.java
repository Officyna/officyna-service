package br.com.officyna.administrative.vehicle.domain.mapper;

import br.com.officyna.administrative.customer.domain.CustomerEntity;
import br.com.officyna.administrative.vehicle.api.resources.VehicleRequest;
import br.com.officyna.administrative.vehicle.api.resources.VehicleResponse;
import br.com.officyna.administrative.vehicle.domain.VehicleEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class VehicleMapperTest {

    private VehicleMapper mapper;
    private CustomerEntity customer;
    private VehicleRequest request;

    @BeforeEach
    void setUp() {
        mapper = new VehicleMapper();

        customer = CustomerEntity.builder()
                .id("cust-123")
                .name("João Silva")
                .build();

        request = new VehicleRequest(
                "cust-123",
                "abc-1234",
                "Toyota",
                "Corolla",
                2020,
                "Silver"
        );
    }

    @Nested
    class ToEntity {

        @Test
        void shouldMapRequestToEntity() {
            // Act
            VehicleEntity entity = mapper.toEntity(request, customer);

            // Assert
            assertNotNull(entity);
            assertEquals(customer.getId(), entity.getCustomerId());
            assertEquals(customer.getName(), entity.getCustomerName());
            assertEquals("ABC-1234", entity.getPlate());
            assertEquals(request.brand(), entity.getBrand());
            assertEquals(request.model(), entity.getModel());
            assertEquals(request.year(), entity.getYear());
            assertEquals(request.color(), entity.getColor());
            assertTrue(entity.isActive());
        }

        @Test
        void shouldConvertPlateToUpperCase() {
            // Arrange
            VehicleRequest requestWithLowerPlate = new VehicleRequest(
                    "cust-123",
                    "xyz-5678",
                    "Honda",
                    "Civic",
                    2021,
                    "Black"
            );

            // Act
            VehicleEntity entity = mapper.toEntity(requestWithLowerPlate, customer);

            // Assert
            assertEquals("XYZ-5678", entity.getPlate());
        }

        @Test
        void shouldSetActiveToTrue() {
            // Act
            VehicleEntity entity = mapper.toEntity(request, customer);

            // Assert
            assertTrue(entity.isActive());
        }

        @Test
        void shouldNotSetIdOrTimestamps() {
            // Act
            VehicleEntity entity = mapper.toEntity(request, customer);

            // Assert
            assertNull(entity.getId());
            assertNull(entity.getCreatedAt());
            assertNull(entity.getUpdatedAt());
        }

        @Test
        void shouldHandleNullColor() {
            // Arrange
            VehicleRequest requestWithNullColor = new VehicleRequest(
                    "cust-123",
                    "abc-1234",
                    "Ford",
                    "Focus",
                    2019,
                    null
            );

            // Act
            VehicleEntity entity = mapper.toEntity(requestWithNullColor, customer);

            // Assert
            assertNull(entity.getColor());
        }

        @Test
        void shouldHandleMercosulPlate() {
            // Arrange
            VehicleRequest requestWithMercosulPlate = new VehicleRequest(
                    "cust-123",
                    "ABC1D23",
                    "Chevrolet",
                    "Onix",
                    2023,
                    "White"
            );

            // Act
            VehicleEntity entity = mapper.toEntity(requestWithMercosulPlate, customer);

            // Assert
            assertEquals("ABC1D23", entity.getPlate());
        }
    }

    @Nested
    class ToResponse {

        private VehicleEntity entity;

        @BeforeEach
        void setUp() {
            entity = VehicleEntity.builder()
                    .id("vehicle-123")
                    .customerId("cust-123")
                    .customerName("João Silva")
                    .plate("ABC-1234")
                    .brand("Toyota")
                    .model("Corolla")
                    .year(2020)
                    .color("Silver")
                    .active(true)
                    .createdAt(LocalDateTime.of(2026, 5, 1, 10, 30))
                    .updatedAt(LocalDateTime.of(2026, 5, 2, 15, 45))
                    .build();
        }

        @Test
        void shouldMapEntityToResponse() {
            // Act
            VehicleResponse response = mapper.toResponse(entity);

            // Assert
            assertNotNull(response);
            assertEquals(entity.getId(), response.id());
            assertEquals(entity.getCustomerId(), response.customerId());
            assertEquals(entity.getCustomerName(), response.customerName());
            assertEquals(entity.getPlate(), response.plate());
            assertEquals(entity.getBrand(), response.brand());
            assertEquals(entity.getModel(), response.model());
            assertEquals(entity.getYear(), response.year());
            assertEquals(entity.getColor(), response.color());
            assertEquals(entity.isActive(), response.active());
            assertEquals(entity.getCreatedAt(), response.createdAt());
        }

        @Test
        void shouldPreserveAllFields() {
            // Act
            VehicleResponse response = mapper.toResponse(entity);

            // Assert
            assertEquals("vehicle-123", response.id());
            assertEquals("cust-123", response.customerId());
            assertEquals("João Silva", response.customerName());
            assertEquals("ABC-1234", response.plate());
            assertEquals("Toyota", response.brand());
            assertEquals("Corolla", response.model());
            assertEquals(2020, response.year());
            assertEquals("Silver", response.color());
            assertTrue(response.active());
        }

        @Test
        void shouldHandleInactiveVehicle() {
            // Arrange
            entity.setActive(false);

            // Act
            VehicleResponse response = mapper.toResponse(entity);

            // Assert
            assertFalse(response.active());
        }

        @Test
        void shouldHandleNullColor() {
            // Arrange
            entity.setColor(null);

            // Act
            VehicleResponse response = mapper.toResponse(entity);

            // Assert
            assertNull(response.color());
        }

        @Test
        void shouldPreserveCreatedAtTimestamp() {
            // Act
            VehicleResponse response = mapper.toResponse(entity);

            // Assert
            assertEquals(LocalDateTime.of(2026, 5, 1, 10, 30), response.createdAt());
        }
    }

    @Nested
    class UpdateEntity {

        private VehicleEntity entity;

        @BeforeEach
        void setUp() {
            entity = VehicleEntity.builder()
                    .id("vehicle-123")
                    .customerId("old-cust-123")
                    .customerName("Old Customer")
                    .plate("OLD-1234")
                    .brand("Honda")
                    .model("Civic")
                    .year(2015)
                    .color("Black")
                    .active(true)
                    .createdAt(LocalDateTime.of(2026, 1, 1, 10, 0))
                    .updatedAt(LocalDateTime.of(2026, 1, 1, 10, 0))
                    .build();
        }

        @Test
        void shouldUpdateAllFields() {
            // Arrange
            VehicleRequest newRequest = new VehicleRequest(
                    "cust-456",
                    "xyz-5678",
                    "Toyota",
                    "Corolla",
                    2020,
                    "Silver"
            );
            CustomerEntity newCustomer = CustomerEntity.builder()
                    .id("cust-456")
                    .name("Maria Santos")
                    .build();

            // Act
            mapper.updateEntity(entity, newRequest, newCustomer);

            // Assert
            assertEquals("cust-456", entity.getCustomerId());
            assertEquals("Maria Santos", entity.getCustomerName());
            assertEquals("XYZ-5678", entity.getPlate());
            assertEquals("Toyota", entity.getBrand());
            assertEquals("Corolla", entity.getModel());
            assertEquals(2020, entity.getYear());
            assertEquals("Silver", entity.getColor());
        }

        @Test
        void shouldConvertPlateToUpperCaseDuringUpdate() {
            // Arrange
            VehicleRequest updateRequest = new VehicleRequest(
                    "cust-456",
                    "new-1111",
                    "Ford",
                    "Focus",
                    2022,
                    "Red"
            );

            // Act
            mapper.updateEntity(entity, updateRequest, customer);

            // Assert
            assertEquals("NEW-1111", entity.getPlate());
        }

        @Test
        void shouldPreserveIdAndTimestamps() {
            // Arrange
            String originalId = entity.getId();
            LocalDateTime originalCreatedAt = entity.getCreatedAt();

            VehicleRequest updateRequest = new VehicleRequest(
                    "cust-789",
                    "different-plate",
                    "Chevrolet",
                    "Onix",
                    2023,
                    "White"
            );

            // Act
            mapper.updateEntity(entity, updateRequest, customer);

            // Assert
            assertEquals(originalId, entity.getId());
            assertEquals(originalCreatedAt, entity.getCreatedAt());
            // updatedAt should be managed by MongoDB @LastModifiedDate
        }

        @Test
        void shouldUpdateOnlyFieldsFromRequest() {
            // Arrange
            boolean originalActive = entity.isActive();
            VehicleRequest updateRequest = new VehicleRequest(
                    "new-cust",
                    "new-plate",
                    "New Brand",
                    "New Model",
                    2024,
                    "Green"
            );

            // Act
            mapper.updateEntity(entity, updateRequest, customer);

            // Assert
            // Note: active should NOT be modified by updateEntity
            assertEquals(originalActive, entity.isActive());
        }

        @Test
        void shouldHandleNullColorDuringUpdate() {
            // Arrange
            VehicleRequest updateRequest = new VehicleRequest(
                    "cust-999",
                    "no-color-111",
                    "Fiat",
                    "Uno",
                    2021,
                    null
            );

            // Act
            mapper.updateEntity(entity, updateRequest, customer);

            // Assert
            assertNull(entity.getColor());
        }

        @Test
        void shouldUpdateWithMercosulPlate() {
            // Arrange
            VehicleRequest mercosulRequest = new VehicleRequest(
                    "cust-111",
                    "ABC1D99",
                    "Volkswagen",
                    "T-Cross",
                    2023,
                    "Blue"
            );

            // Act
            mapper.updateEntity(entity, mercosulRequest, customer);

            // Assert
            assertEquals("ABC1D99", entity.getPlate());
        }
    }
}

