package br.com.officyna.infrastructure.persistence.mongodb.gateway;

import br.com.officyna.administrative.vehicle.domain.entity.Vehicle;
import br.com.officyna.infrastructure.persistence.mapper.VehicleEntityDocumentMapper;
import br.com.officyna.infrastructure.persistence.mongodb.model.VehicleDocument;
import br.com.officyna.infrastructure.persistence.mongodb.repository.VehicleMongoRepository;
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

class VehicleRepositoryGatewayTest {

    @Mock
    private VehicleMongoRepository mongoRepository;

    @Mock
    private VehicleEntityDocumentMapper mapper;

    @InjectMocks
    private VehicleRepositoryGateway gateway;

    private Vehicle vehicle;
    private VehicleDocument vehicleDocument;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        vehicle = Vehicle.builder()
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

        vehicleDocument = VehicleDocument.builder()
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
    }

    @Test
    @DisplayName("Should save a vehicle successfully")
    void save_success() {
        when(mapper.toDocument(any(Vehicle.class))).thenReturn(vehicleDocument);
        when(mongoRepository.save(any(VehicleDocument.class))).thenReturn(vehicleDocument);
        when(mapper.toEntity(any(VehicleDocument.class))).thenReturn(vehicle);

        Vehicle savedVehicle = gateway.save(vehicle);

        assertNotNull(savedVehicle);
        assertEquals(vehicle.getId(), savedVehicle.getId());
        verify(mapper, times(1)).toDocument(vehicle);
        verify(mongoRepository, times(1)).save(vehicleDocument);
        verify(mapper, times(1)).toEntity(vehicleDocument);
    }

    @Test
    @DisplayName("Should find a vehicle by ID when it exists")
    void findById_found() {
        when(mongoRepository.findById("vehicle-123")).thenReturn(Optional.of(vehicleDocument));
        when(mapper.toEntity(any(VehicleDocument.class))).thenReturn(vehicle);

        Optional<Vehicle> foundVehicle = gateway.findById("vehicle-123");

        assertTrue(foundVehicle.isPresent());
        assertEquals(vehicle.getId(), foundVehicle.get().getId());
        verify(mongoRepository, times(1)).findById("vehicle-123");
        verify(mapper, times(1)).toEntity(vehicleDocument);
    }

    @Test
    @DisplayName("Should return empty when vehicle not found by ID")
    void findById_notFound() {
        when(mongoRepository.findById("non-existent-id")).thenReturn(Optional.empty());

        Optional<Vehicle> foundVehicle = gateway.findById("non-existent-id");

        assertFalse(foundVehicle.isPresent());
        verify(mongoRepository, times(1)).findById("non-existent-id");
        verify(mapper, never()).toEntity(any(VehicleDocument.class));
    }

    @Test
    @DisplayName("Should return all vehicles")
    void findAll_returnsAllVehicles() {
        List<VehicleDocument> documents = Collections.singletonList(vehicleDocument);
        List<Vehicle> vehicles = Collections.singletonList(vehicle);

        when(mongoRepository.findAll()).thenReturn(documents);
        when(mapper.toEntity(any(VehicleDocument.class))).thenReturn(vehicle);

        List<Vehicle> result = gateway.findAll();

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(vehicle.getId(), result.get(0).getId());
        verify(mongoRepository, times(1)).findAll();
        verify(mapper, times(1)).toEntity(vehicleDocument);
    }

    @Test
    @DisplayName("Should return empty list when no vehicles are found")
    void findAll_returnsEmptyList() {
        when(mongoRepository.findAll()).thenReturn(Collections.emptyList());

        List<Vehicle> result = gateway.findAll();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(mongoRepository, times(1)).findAll();
        verify(mapper, never()).toEntity(any(VehicleDocument.class));
    }

    @Test
    @DisplayName("Should delete a vehicle by ID")
    void deleteById_success() {
        doNothing().when(mongoRepository).deleteById("vehicle-123");

        gateway.deleteById("vehicle-123");

        verify(mongoRepository, times(1)).deleteById("vehicle-123");
    }

    @Test
    @DisplayName("Should return true if vehicle exists by ID")
    void existsById_true() {
        when(mongoRepository.existsById("vehicle-123")).thenReturn(true);

        assertTrue(gateway.existsById("vehicle-123"));
        verify(mongoRepository, times(1)).existsById("vehicle-123");
    }

    @Test
    @DisplayName("Should return false if vehicle does not exist by ID")
    void existsById_false() {
        when(mongoRepository.existsById("non-existent-id")).thenReturn(false);

        assertFalse(gateway.existsById("non-existent-id"));
        verify(mongoRepository, times(1)).existsById("non-existent-id");
    }

    @Test
    @DisplayName("Should find a vehicle by plate when it exists")
    void findByPlate_found() {
        when(mongoRepository.findByPlate("ABC1234")).thenReturn(Optional.of(vehicleDocument));
        when(mapper.toEntity(any(VehicleDocument.class))).thenReturn(vehicle);

        Optional<Vehicle> foundVehicle = gateway.findByPlate("ABC1234");

        assertTrue(foundVehicle.isPresent());
        assertEquals(vehicle.getPlate(), foundVehicle.get().getPlate());
        verify(mongoRepository, times(1)).findByPlate("ABC1234");
        verify(mapper, times(1)).toEntity(vehicleDocument);
    }

    @Test
    @DisplayName("Should return empty when vehicle not found by plate")
    void findByPlate_notFound() {
        when(mongoRepository.findByPlate("XYZ7890")).thenReturn(Optional.empty());

        Optional<Vehicle> foundVehicle = gateway.findByPlate("XYZ7890");

        assertFalse(foundVehicle.isPresent());
        verify(mongoRepository, times(1)).findByPlate("XYZ7890");
        verify(mapper, never()).toEntity(any(VehicleDocument.class));
    }

    @Test
    @DisplayName("Should return true if vehicle exists by plate")
    void existsByPlate_true() {
        when(mongoRepository.existsByPlate("ABC1234")).thenReturn(true);

        assertTrue(gateway.existsByPlate("ABC1234"));
        verify(mongoRepository, times(1)).existsByPlate("ABC1234");
    }

    @Test
    @DisplayName("Should return false if vehicle does not exist by plate")
    void existsByPlate_false() {
        when(mongoRepository.existsByPlate("XYZ7890")).thenReturn(false);

        assertFalse(gateway.existsByPlate("XYZ7890"));
        verify(mongoRepository, times(1)).existsByPlate("XYZ7890");
    }

    @Test
    @DisplayName("Should return vehicles by customer ID")
    void findByCustomerId_returnsVehicles() {
        List<VehicleDocument> documents = Collections.singletonList(vehicleDocument);
        List<Vehicle> vehicles = Collections.singletonList(vehicle);

        when(mongoRepository.findByCustomerId("customer-456")).thenReturn(documents);
        when(mapper.toEntity(any(VehicleDocument.class))).thenReturn(vehicle);

        List<Vehicle> result = gateway.findByCustomerId("customer-456");

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(vehicle.getCustomerId(), result.get(0).getCustomerId());
        verify(mongoRepository, times(1)).findByCustomerId("customer-456");
        verify(mapper, times(1)).toEntity(vehicleDocument);
    }

    @Test
    @DisplayName("Should return empty list when no vehicles found by customer ID")
    void findByCustomerId_returnsEmptyList() {
        when(mongoRepository.findByCustomerId("non-existent-customer-id")).thenReturn(Collections.emptyList());

        List<Vehicle> result = gateway.findByCustomerId("non-existent-customer-id");

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(mongoRepository, times(1)).findByCustomerId("non-existent-customer-id");
        verify(mapper, never()).toEntity(any(VehicleDocument.class));
    }

    @Test
    @DisplayName("Should return active vehicles")
    void findByActiveTrue_returnsActiveVehicles() {
        List<VehicleDocument> documents = Collections.singletonList(vehicleDocument);
        List<Vehicle> vehicles = Collections.singletonList(vehicle);

        when(mongoRepository.findByActiveTrue()).thenReturn(documents);
        when(mapper.toEntity(any(VehicleDocument.class))).thenReturn(vehicle);

        List<Vehicle> result = gateway.findByActiveTrue();

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(vehicle.getId(), result.get(0).getId());
        verify(mongoRepository, times(1)).findByActiveTrue();
        verify(mapper, times(1)).toEntity(vehicleDocument);
    }

    @Test
    @DisplayName("Should return empty list when no active vehicles are found")
    void findByActiveTrue_returnsEmptyList() {
        when(mongoRepository.findByActiveTrue()).thenReturn(Collections.emptyList());

        List<Vehicle> result = gateway.findByActiveTrue();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(mongoRepository, times(1)).findByActiveTrue();
        verify(mapper, never()).toEntity(any(VehicleDocument.class));
    }
}